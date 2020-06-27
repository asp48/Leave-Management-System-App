package com.example.amit.leave1;

import android.animation.PropertyValuesHolder;
import android.app.ActionBar;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Color;
import android.os.AsyncTask;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.util.Log;
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
import java.io.InputStreamReader;
import java.net.URI;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;


public class Notification extends AppCompatActivity {
    ArrayList<String> detl, type, from, to, nd, rsn, alt, st, rjrsn, lid, on, pp, fa, amt, aon, stH, stVP, aH, aVP;
    ListView lv;
    TextView tv;
    historyadapter ha;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification);
        SharedPreferences sp = getSharedPreferences("Profile", MODE_PRIVATE);
        SharedPreferences.Editor e = sp.edit();
        e.putString("Activity", "2");
        e.apply();
        detl = new ArrayList<String>();
        lv = (ListView) findViewById(R.id.ntfication);
        tv = (TextView) findViewById(R.id.emptymessege);
        tv.setVisibility(View.GONE);
        lv.setDivider(null);
        lv.setDividerHeight(0);
        Intent t = getIntent();
        final String v = t.getStringExtra("View");
        final String facid = t.getStringExtra("Faculty_Id");
        if (v.equals("0")) {
            getSupportActionBar().setTitle("Notifications");
            type = t.getStringArrayListExtra("Type");
            from = t.getStringArrayListExtra("From");
            to = t.getStringArrayListExtra("To");
            nd = t.getStringArrayListExtra("NoDays");
            rsn = t.getStringArrayListExtra("Reason");
            alt = t.getStringArrayListExtra("Alternate");
            st = t.getStringArrayListExtra("Status");
            rjrsn = t.getStringArrayListExtra("RjReason");
            lid = t.getStringArrayListExtra("LeaveId");
            on = t.getStringArrayListExtra("OnD");
            pp = t.getStringArrayListExtra("PP");
            stH = t.getStringArrayListExtra("ByHOD");
            stVP = t.getStringArrayListExtra("ByVP");
            aH = t.getStringArrayListExtra("A_HOD");
            aVP = t.getStringArrayListExtra("A_VP");
            aon = t.getStringArrayListExtra("A_On");
            fa = t.getStringArrayListExtra("FAssist");
            amt = t.getStringArrayListExtra("FAmount");
            loaddetails();
        } else {
            getSupportActionBar().setTitle("History");
            dispHistory(facid);
        }
        lv.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE_MODAL);
        lv.setMultiChoiceModeListener(new AbsListView.MultiChoiceModeListener() {
            @Override
            public void onItemCheckedStateChanged(ActionMode mode, int position, long id, boolean checked) {
                final int count = lv.getCheckedItemCount();
                mode.setTitle(count + " Selected");
                Log.e("TAG", position + "");
                ha.toggleSelection(position);
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
                        SparseBooleanArray selected = ha.getSelectedIds();
                        LocalDB db = new LocalDB(Notification.this);
                        ArrayList<String> rid = new ArrayList<String>();
                        int size = detl.size();
                        for (int i = size - 1; i >= 0; i--) {
                            if (selected.get(i)) {
                                rid.add(lid.get(i));
                                remove(i);
                            }
                        }
                        String[] rlid = new String[rid.size()];
                        db.deleteItem(rid.toArray(rlid));
                        mode.finish();
                        loadlist();
                        return true;
                    default:
                        return false;
                }
            }

            @Override
            public void onDestroyActionMode(ActionMode mode) {
                ha.removeSelection();
            }
        });
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int pos, long l) {
                Intent m = new Intent(Notification.this, SelfDetail.class);
                m.putExtra("Type", type.get(pos));
                m.putExtra("From", from.get(pos));
                m.putExtra("To", to.get(pos));
                m.putExtra("NoDays", nd.get(pos));
                m.putExtra("Reason", rsn.get(pos));
                m.putExtra("Alternate", alt.get(pos));
                m.putExtra("Status", st.get(pos));
                m.putExtra("RjReason", rjrsn.get(pos));
                m.putExtra("OnD", on.get(pos));
                m.putExtra("PP", pp.get(pos));
                m.putExtra("FAssist", fa.get(pos));
                m.putExtra("FAmount", amt.get(pos));
                m.putExtra("A_HOD", aH.get(pos));
                m.putExtra("A_VP", aVP.get(pos));
                startActivity(m);
                overridePendingTransition(R.anim.bottom_in, R.anim.top_out);
                if (v.equals("0")) {
                    detl.remove(pos);
                    st.remove(pos);
                    lid.remove(pos);
                }
                loadlist();
            }
        });

    }


    private void remove(int clicked) {
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
        detl.remove(clicked);
        lid.remove(clicked);
    }

    private void dispHistory(String fid) {
        LocalDB db = new LocalDB(Notification.this);
        String query = "select * from Approve where Faculty_Id= '" + fid + "' ORDER BY LeaveId DESC";
        type = db.getAllData(query, db.type);
        Log.e("TAG", "dsa" + type.size());
        Log.e("TAG", "count rows " + db.numberOfRows());
        if (type.isEmpty()) {
            tv.setVisibility(View.VISIBLE);
            tv.setBackgroundColor(Color.WHITE);
            tv.setText("No History Entries Found.");
        } else {
            from = db.getAllData(query, db.fromD);
            to = db.getAllData(query, db.toD);
            nd = db.getAllData(query, db.nd);
            rsn = db.getAllData(query, db.rsn);
            alt = db.getAllData(query, db.alt);
            st = db.getAllData(query, db.st);
            rjrsn = db.getAllData(query, db.rjrsn);
            lid = db.getAllData(query, db.lid);
            on = db.getAllData(query, db.onD);
            pp = db.getAllData(query, db.pp);
            aon = db.getAllData(query, db.aon);
            fa = db.getAllData(query, db.fasst);
            amt = db.getAllData(query, db.amt);
            stH = db.getAllData(query, db.ByHOD);
            stVP = db.getAllData(query, db.ByVP);
            aH = db.getAllData(query, db.a_HOD);
            aVP = db.getAllData(query, db.a_VP);
            loaddetails();
        }
    }

    private void loaddetails() {
        for (int k = 0; k < type.size(); k++) {
            String temp = "<b>Applied On : </b>" + aon.get(k) + "<br/> <b>LEAVE  : </b>" + type.get(k);
            if (on.get(k).equals("0000-00-00"))
                temp += "<br/> <b>FROM  : </b>" + from.get(k) + " <br/> <b>TO     : </b>" + to.get(k);
            else {
                temp += "<br/> <b>ON    : </b>" + on.get(k) + " <br/> <b> WHEN    : </b>";
                if (pp.get(k).equals("1")) temp += "Pre Lunch";
                else if (pp.get(k).equals("2")) temp += "Post Lunch";
            }
            if (st.get(k).equals("0") && stH.get(k).equals("1"))
                temp += "<br/> To be Approved by Principal/Vice Principal";
            detl.add(temp);
        }
        loadlist();
    }

    private void loadlist() {
        if (type.size() < 1) {
            tv.setVisibility(View.VISIBLE);
            tv.setBackgroundColor(Color.WHITE);
            tv.setText("You have Read all Notifications");
        } else {
            ha = new historyadapter(this, st, detl);
            lv.setAdapter(ha);
        }
    }

}
