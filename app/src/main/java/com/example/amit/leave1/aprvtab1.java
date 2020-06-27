package com.example.amit.leave1;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.SparseArray;
import android.util.SparseBooleanArray;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.ScrollView;
import android.widget.TableRow;
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
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;

import javax.net.ssl.HttpsURLConnection;


public class aprvtab1 extends Activity {
    ListView lv;
    TextView em, nme, fid, fdsg, fdept, ltype, lfrom, lto, lnd, lrsn, alt_fac, onD, prpol, fasst, famt, aprvHOD, ap_on;
    ArrayList<String> nm, id, desg, dept, from, to, nd, rsn, alt, type, lid, on, pp, fa, amt, ah, aon, st;
    ScrollView sv;
    RadioButton a, r;
    EditText rn;
    Button dn, cl;
    ImageView profile;
    private String sid = "", rjrsn = "", role;
    private int clicked, sf = 0;
    TableRow from_row, to_row, on_row, when_row, fa_row, amt_row, aprvHOD_row;
    SparseArray<Bitmap> imgl;
    SparseBooleanArray spb;
    RequestHandler rh;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_aprvtab1);
        lv = (ListView) findViewById(R.id.aprlist);
        sv = (ScrollView) findViewById(R.id.vdetail);
        a = (RadioButton) findViewById(R.id.ch1);
        r = (RadioButton) findViewById(R.id.ch2);
        dn = (Button) findViewById(R.id.dnbtn);
        cl = (Button) findViewById(R.id.cancelbtn);
        rn = (EditText) findViewById(R.id.rsn);
        em = (TextView) findViewById(R.id.emptymessege);
        nme = (TextView) findViewById(R.id.aprlname);
        fid = (TextView) findViewById(R.id.aprlid);
        fdsg = (TextView) findViewById(R.id.aprldesig);
        fdept = (TextView) findViewById(R.id.aprldept);
        ltype = (TextView) findViewById(R.id.aprltl);
        lfrom = (TextView) findViewById(R.id.aprlfrom);
        lto = (TextView) findViewById(R.id.aprlto);
        lnd = (TextView) findViewById(R.id.aprlnd);
        lrsn = (TextView) findViewById(R.id.aprlrsn);
        alt_fac = (TextView) findViewById(R.id.aprlalt);
        profile = (ImageView) findViewById(R.id.pf);
        onD = (TextView) findViewById(R.id.onD);
        prpol = (TextView) findViewById(R.id.pr_pol);
        fasst = (TextView) findViewById(R.id.fassist);
        famt = (TextView) findViewById(R.id.famt);
        ap_on = (TextView) findViewById(R.id.ap_on);
        aprvHOD = (TextView) findViewById(R.id.aprv_hod);
        from_row = (TableRow) findViewById(R.id.from_row);
        to_row = (TableRow) findViewById(R.id.to_row);
        on_row = (TableRow) findViewById(R.id.on_row);
        when_row = (TableRow) findViewById(R.id.when_row);
        fa_row = (TableRow) findViewById(R.id.fa_row);
        amt_row = (TableRow) findViewById(R.id.amt_row);
        aprvHOD_row = (TableRow) findViewById(R.id.aprvHOD_row);
        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);
        int h = dm.heightPixels;
        profile.requestLayout();
        profile.getLayoutParams().height = 3 * h / 8;
        em.setVisibility(View.GONE);
        nm = new ArrayList<>();
        id = new ArrayList<>();
        desg = new ArrayList<>();
        dept = new ArrayList<>();
        type = new ArrayList<>();
        from = new ArrayList<>();
        to = new ArrayList<>();
        nd = new ArrayList<>();
        rsn = new ArrayList<>();
        alt = new ArrayList<>();
        st = new ArrayList<>();
        lid = new ArrayList<>();
        aon = new ArrayList<>();
        on = new ArrayList<>();
        pp = new ArrayList<>();
        fa = new ArrayList<>();
        amt = new ArrayList<>();
        ah = new ArrayList<>();
        imgl = new SparseArray<>();
        rh = new RequestHandler(aprvtab1.this);
        sv.setVisibility(View.GONE);
        Intent i = getIntent();
        sid = i.getStringExtra("Faculty_Id");
        role = i.getStringExtra("Role");
        Log.e("TAG", sid + role);
        new aprvlist().execute(sid, role);
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                clicked = i;
                nme.setText(nm.get(i));
                fid.setText(id.get(i));
                fdsg.setText(desg.get(i));
                if (desg.get(i).equals("HOD") || role.equals("HOD"))
                    aprvHOD_row.setVisibility(View.GONE);
                else aprvHOD.setText(ah.get(i));
                fdept.setText(dept.get(i));
                ltype.setText(type.get(i));
                ap_on.setText(aon.get(i));
                if (type.get(i).equals("OOD")) {
                    if (fa.get(i).equals("1")) {
                        fasst.setText("Yes");
                        famt.setText(amt.get(i));
                    } else {
                        fasst.setText("No");
                        amt_row.setVisibility(View.GONE);
                    }
                } else {
                    fa_row.setVisibility(View.GONE);
                    amt_row.setVisibility(View.GONE);
                }
                lnd.setText(nd.get(i));
                lrsn.setText(rsn.get(i));
                alt_fac.setText(alt.get(i));
                if (on.get(i).equals("0000-00-00")) {
                    lfrom.setText(from.get(i));
                    lto.setText(to.get(i));
                    on_row.setVisibility(View.GONE);
                    when_row.setVisibility(View.GONE);
                } else {
                    from_row.setVisibility(View.GONE);
                    to_row.setVisibility(View.GONE);
                    onD.setText(on.get(i));
                    if (pp.get(i).equals("1"))
                        prpol.setText("Pre Lunch");
                    else if (pp.get(i).equals("2"))
                        prpol.setText("Post Lunch");
                }
                lv.setVisibility(View.GONE);
                sv.setVisibility(View.VISIBLE);
                rn.setVisibility(View.GONE);
                profile.setImageBitmap(imgl.get(i));
            }
        });
        a.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                a.setChecked(true);
                r.setChecked(false);
                rn.setVisibility(View.GONE);
            }
        });
        r.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                a.setChecked(false);
                r.setChecked(true);
                rn.setVisibility(View.VISIBLE);
            }
        });
        dn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (rn.getVisibility() == View.VISIBLE) {
                    rjrsn = rn.getText().toString();
                    sf = 2;
                } else
                    sf = 1;
                new updatestatus().execute(lid.get(clicked), rjrsn, String.valueOf(sf), role, type.get(clicked), id.get(clicked), nd.get(clicked));
                load();
                sv.setVisibility(View.GONE);
                lv.setVisibility(View.VISIBLE);
            }
        });
        cl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sv.setVisibility(View.GONE);
                lv.setVisibility(View.VISIBLE);
                Toast.makeText(getBaseContext(), "operation Canceled!!", Toast.LENGTH_LONG).show();
            }
        });
        cl.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_UP)
                    cl.setBackgroundColor(Color.WHITE);
                if (event.getAction() == MotionEvent.ACTION_DOWN)
                    cl.setBackgroundColor(Color.CYAN);
                return false;
            }
        });
        dn.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_UP)
                    dn.setBackgroundColor(Color.WHITE);
                if (event.getAction() == MotionEvent.ACTION_DOWN)
                    dn.setBackgroundColor(Color.CYAN);
                return false;
            }
        });
    }

    private void load() {
        DashboardAdapter dba = new DashboardAdapter(aprvtab1.this, nm, id, desg, dept, st, spb, imgl);
        lv.setAdapter(dba);
    }

    private class aprvlist extends AsyncTask<String, Void, String> {
        private ProgressDialog dialog = new ProgressDialog(aprvtab1.this);

        @Override
        protected String doInBackground(String... d) {
            HashMap<String, String> data = new HashMap<String, String>();
            data.put("Sid", d[0]);
            data.put("Role", d[1]);
            return rh.sendPostRequest("getaprvlist.php", data);
        }

        @Override
        protected void onPreExecute() {
            dialog.setMessage("Fetching Requests....");
            dialog.show();
            dialog.setCancelable(false);
        }

        @Override
        protected void onPostExecute(String s) {
            Log.e("TAG", s);
            switch (s) {
                case "300":
                    refresh("Connection Error", "Couldn't Connect. Check your Internet Settings.", "Try Again");
                    break;
                case "310":
                    refresh("Connection Timeout", "Taking longer than usual to connect. Try again later.", "Try Again");
                    break;
                case "400":
                    refresh("Connection Error", "Something Went Wrong. Try again later.", "Try Again");
                    break;
                case "410":
                    refresh("Connection Error", "Something Went Wrong. Try again later.", "Try Again");
                    break;
                default:
                    showList(s);
            }
            dialog.dismiss();
        }
    }

    private void showList(String s) {
        Log.e("TAG", s);
        try {
            JSONObject jo = new JSONObject(s);
            JSONArray ja = jo.getJSONArray("Results");
            if (ja.length() > 0) {
                for (int i = 0; i < ja.length(); i++) {
                    JSONObject c = ja.getJSONObject(i);
                    nm.add(c.getString("Name"));
                    desg.add(c.getString("Desg"));
                    dept.add(c.getString("Dept"));
                    id.add(c.getString("Fid"));
                    type.add(c.getString("Type"));
                    from.add(c.getString("FromD"));
                    to.add(c.getString("ToD"));
                    nd.add(c.getString("Nd"));
                    rsn.add(c.getString("Rsn"));
                    alt.add(c.getString("Alt"));
                    lid.add(c.getString("Lid"));
                    on.add(c.getString("OnD"));
                    pp.add(c.getString("PP"));
                    fa.add(c.getString("FAsst"));
                    amt.add(c.getString("Famt"));
                    aon.add(c.getString("A_On"));
                    ah.add(c.getString("A_HOD"));
                    st.add("3");
                }
                spb = new SparseBooleanArray();
                for (int k = 0; k < nm.size(); k++)
                    spb.put(k, true);
                load();
            } else {
                sv.setVisibility(View.GONE);
                lv.setVisibility(View.GONE);
                em.setVisibility(View.VISIBLE);
                em.setBackgroundColor(Color.WHITE);
                em.setText("You have Reviewed all the Requests.");
            }

        } catch (JSONException e) {
            refresh("Connection Error", "Something Went Wrong. Try again later.", "Try Again");
        }
    }

    private class updatestatus extends AsyncTask<String, Void, String> {
        private ProgressDialog updt = new ProgressDialog(aprvtab1.this);

        @Override
        protected String doInBackground(String... d) {
            HashMap<String, String> data = new HashMap<String, String>();
            Log.e("TAG", d[0] + d[1] + d[2] + d[3] + d[4] + d[5] + d[6]);
            data.put("Lid", d[0]);
            data.put("Rjrsn", d[1]);
            data.put("St", d[2]);
            data.put("Role", d[3]);
            data.put("Type", d[4]);
            data.put("Fid", d[5]);
            data.put("Nd", d[6]);
            return rh.sendPostRequest("updatestatus.php", data);
        }

        @Override
        protected void onPreExecute() {
            updt.setMessage("Updating Database...");
            updt.show();
            updt.setCancelable(false);
        }

        @Override
        protected void onPostExecute(String s) {
            Log.e("TAG", s);
            switch (s) {
                case "300":
                    Toast.makeText(getBaseContext(), "Couldn't Connect. Check your Internet Settings", Toast.LENGTH_SHORT).show();
                    break;
                case "310":
                    Toast.makeText(getBaseContext(), "Connection Timeout: Taking longer than usual. Try again later", Toast.LENGTH_SHORT).show();
                    break;
                case "400":
                    Toast.makeText(getBaseContext(), "Connection Error:Something Went Wrong.Try again later", Toast.LENGTH_SHORT).show();
                    break;
                case "410":
                    Toast.makeText(getBaseContext(), "Connection Error:Something Went Wrong.Try again later", Toast.LENGTH_SHORT).show();
                    break;
                default:
                    if (s.contains("500")) {
                        MoveToHistory(s);
                        Toast.makeText(getBaseContext(), "Updated Successfully", Toast.LENGTH_SHORT).show();
                        spb.put(clicked, false);
                        load();
                    } else
                        Toast.makeText(getBaseContext(), "Connection Error:Something Went Wrong.Try again later", Toast.LENGTH_SHORT).show();

            }
            updt.dismiss();
        }
    }

    private void MoveToHistory(String r) {
        LocalDB db = new LocalDB(aprvtab1.this);
        String[] t = r.trim().split("/");
        if (db.checkid(lid.get(clicked))) {
            if (role.equals("VP") || type.get(clicked).equals("CL") || sf == 2)
                db.update(db.st, String.valueOf(sf), lid.get(clicked), 1);
            if (sf == 2)
                db.update(db.rjrsn, rjrsn, lid.get(clicked), 0);
            switch (role) {
                case "HOD":
                    db.update(db.ByHOD, String.valueOf(sf), lid.get(clicked), 1);
                    db.update(db.a_HOD, t[1], lid.get(clicked), 0);
                    break;
                case "VP":
                    db.update(db.ByVP, String.valueOf(sf), lid.get(clicked), 1);
                    db.update(db.a_VP, t[1], lid.get(clicked), 0);
                    db.update(db.a_HOD, ah.get(clicked), lid.get(clicked), 0);
                    db.update(db.ByHOD, "1", lid.get(clicked), 1);
            }
        } else {
            String[] v = new String[23];
            v[0] = sid;
            v[1] = nm.get(clicked);
            v[2] = id.get(clicked);
            v[3] = desg.get(clicked);
            v[4] = dept.get(clicked);
            v[5] = lid.get(clicked);
            v[6] = aon.get(clicked);
            v[7] = type.get(clicked);
            v[8] = from.get(clicked);
            v[9] = to.get(clicked);
            v[10] = on.get(clicked);
            v[11] = pp.get(clicked);
            v[12] = nd.get(clicked);
            v[13] = rsn.get(clicked);
            v[14] = alt.get(clicked);
            v[15] = fa.get(clicked);
            v[16] = amt.get(clicked);
            if (role.equals("VP") || type.get(clicked).equals("CL") || sf == 2)
                v[17] = String.valueOf(sf);
            else v[17] = "0";
            v[18] = rjrsn;
            switch (role) {
                case "VP":
                    v[19] = "1";
                    v[20] = String.valueOf(sf);
                    v[21] = ah.get(clicked);
                    v[22] = t[1];
                    break;
                case "HOD":
                    v[19] = String.valueOf(sf);
                    v[20] = "0";
                    v[21] = t[1];
                    v[22] = "0000-00-00";
            }
            Log.e("TAG", "Inside move to history");
            if (db.insert(v))
                Log.e("TAG", "moved to history");
        }
    }

    @Override
    public void onBackPressed() {
        if (lv.getVisibility() == View.VISIBLE || em.getVisibility() == View.VISIBLE) {
            finish();
        } else {
            sv.setVisibility(View.GONE);
            lv.setVisibility(View.VISIBLE);
            load();
        }
    }

    public void refresh(final String title, final String msg, final String action) {
        final AlertDialog.Builder adb = new AlertDialog.Builder(aprvtab1.this);
        adb.setTitle(title);
        adb.setMessage(msg);
        adb.setPositiveButton(action, null);
        final AlertDialog ad = adb.create();
        ad.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;
        ad.show();
        ad.getButton(DialogInterface.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (rh.isConnected(aprvtab1.this)) {
                    ad.dismiss();
                    new aprvlist().execute(sid, role);
                } else {
                    ad.dismiss();
                    refresh(title, msg, action);
                }
            }
        });
    }
}
