package com.example.amit.leave1;

import android.app.Activity;
import android.app.DownloadManager;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.AsyncTask;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.util.SparseArray;
import android.util.SparseBooleanArray;
import android.view.ActionMode;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.util.ArrayList;


public class aprvtab2 extends Activity {
    ListView lv;
    ArrayList<String> nm, id, desg, dept, from, to, nd, rsn, alt, type, rjrsn, on, pp, fa, amt, aon, stH, stVP, aH, aVP, st, lid;
    TextView em;
    SparseArray<Bitmap> imgl;
    DashboardAdapter dba;
    String sid, role;
    int lsize = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_aprvtab2);
        lv = (ListView) findViewById(R.id.ahlist);
        em = (TextView) findViewById(R.id.emptymessege);
        lv.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE_MODAL);
        imgl = new SparseArray<>();
        Intent i = getIntent();
        sid = i.getStringExtra("Super_Id");
        role = i.getStringExtra("Role");
        displaylist();
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent m = new Intent(aprvtab2.this, SelfDetail.class);
                m.putExtra("Type", type.get(i));
                m.putExtra("From", from.get(i));
                m.putExtra("To", to.get(i));
                m.putExtra("NoDays", nd.get(i));
                m.putExtra("Reason", rsn.get(i));
                m.putExtra("Alternate", alt.get(i));
                m.putExtra("Status", String.valueOf(st.get(i)));
                m.putExtra("RjReason", rjrsn.get(i));
                m.putExtra("OnD", on.get(i));
                m.putExtra("PP", pp.get(i));
                m.putExtra("FAssist", fa.get(i));
                m.putExtra("FAmount", amt.get(i));
                m.putExtra("A_HOD", aH.get(i));
                m.putExtra("A_VP", aVP.get(i));
                startActivity(m);
                overridePendingTransition(R.anim.bottom_in, R.anim.top_out);
            }
        });
        lv.setMultiChoiceModeListener(new AbsListView.MultiChoiceModeListener() {
            @Override
            public void onItemCheckedStateChanged(ActionMode mode, int position, long id, boolean checked) {
                final int count = lv.getCheckedItemCount();
                mode.setTitle(count + " Selected");
                Log.e("TAG", position + "");
                dba.toggleSelection(position);
            }

            @Override
            public boolean onCreateActionMode(ActionMode mode, Menu menu) {
                mode.getMenuInflater().inflate(R.menu.remove, menu);
                return true;
            }

            @Override
            public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
                return false;
            }

            @Override
            public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.delete:
                        SparseBooleanArray selected = dba.getSelectedIds();
                        LocalDB db = new LocalDB(aprvtab2.this);
                        ArrayList<String> rid = new ArrayList<String>();
                        int size = nm.size();
                        for (int i = size - 1; i >= 0; i--) {
                            if (selected.get(i)) {
                                Log.e("TAG", "Choosen " + i);
                                rid.add(lid.get(i));
                                remove(i);
                            }
                        }
                        String[] rlid = new String[rid.size()];
                        db.deleteItem(rid.toArray(rlid));
                        mode.finish();
                        orderimgl();
                        load();
                        return true;
                    default:
                        return false;
                }
            }

            @Override
            public void onDestroyActionMode(ActionMode mode) {
                dba.removeSelection();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        displaylist();
    }

    private void load() {
        em.setVisibility(View.GONE);
        lv.setVisibility(View.VISIBLE);
        SparseBooleanArray spb = new SparseBooleanArray();
        for (int i = 0; i < nm.size(); i++)
            spb.put(i, true);
        dba = new DashboardAdapter(aprvtab2.this, nm, id, desg, dept, st, spb, imgl);
        lv.setAdapter(dba);
    }

    private void remove(int clicked) {
        nm.remove(clicked);
        id.remove(clicked);
        desg.remove(clicked);
        dept.remove(clicked);
        type.remove(clicked);
        from.remove(clicked);
        to.remove(clicked);
        nd.remove(clicked);
        rsn.remove(clicked);
        alt.remove(clicked);
        st.remove(clicked);
        aon.remove(clicked);
        pp.remove(clicked);
        fa.remove(clicked);
        amt.remove(clicked);
        on.remove(clicked);
        stH.remove(clicked);
        stVP.remove(clicked);
        aH.remove(clicked);
        aVP.remove(clicked);
        lid.remove(clicked);
        imgl.remove(clicked);
    }

    private void orderimgl() {
        int x = 0, y;
        while (x < lsize) {
            if (imgl.get(x) == null) {
                y = x + 1;
                while (y < lsize) {
                    if (imgl.get(y) != null)
                        break;
                    y++;
                }
                if (y == nm.size())
                    break;
                else {
                    imgl.put(x, imgl.get(y));
                    dba.notifyDataSetChanged();
                    imgl.remove(y);
                    x = y;
                }
            }
            x++;
        }
    }

    public void displaylist() {
        LocalDB db = new LocalDB(aprvtab2.this);
        String query = "";
        if (role.equals("HOD"))
            query = "select * from Approve where Super_Id = '" + sid + "' and Status != 0 order by LeaveID DESC";
        else if (role.equals("VP"))
            query = "select * from Approve where Type != 'CL' and Status != 0 order by LeaveID DESC";
        Log.e("TAG", query);
        type = db.getAllData(query, db.type);
        Log.e("TAG", "dsa" + type.size());
        Log.e("TAG", "count rows " + db.numberOfRows());
        if (type.isEmpty()) {
            em.setVisibility(View.VISIBLE);
            em.setBackgroundColor(Color.WHITE);
            em.setText("No History Entries Found.");
        } else {
            nm = db.getAllData(query, db.name);
            lsize = nm.size();
            id = db.getAllData(query, db.fid);
            desg = db.getAllData(query, db.desg);
            dept = db.getAllData(query, db.dept);
            from = db.getAllData(query, db.fromD);
            to = db.getAllData(query, db.toD);
            nd = db.getAllData(query, db.nd);
            rsn = db.getAllData(query, db.rsn);
            alt = db.getAllData(query, db.alt);
            st = db.getAllData(query, db.st);
            rjrsn = db.getAllData(query, db.rjrsn);
            on = db.getAllData(query, db.onD);
            pp = db.getAllData(query, db.pp);
            aon = db.getAllData(query, db.aon);
            fa = db.getAllData(query, db.fasst);
            amt = db.getAllData(query, db.amt);
            stH = db.getAllData(query, db.ByHOD);
            stVP = db.getAllData(query, db.ByVP);
            aH = db.getAllData(query, db.a_HOD);
            aVP = db.getAllData(query, db.a_VP);
            lid = db.getAllData(query, db.lid);
            load();
        }
    }
}