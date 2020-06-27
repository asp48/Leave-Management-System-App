package com.example.amit.leave1;

import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.AsyncTask;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.util.SparseArray;
import android.util.SparseBooleanArray;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.ImageView;
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
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;

import javax.net.ssl.HttpsURLConnection;

public class Dashboard extends ActionBarActivity {
    ListView lv;
    ArrayList<String> nm, id, desg, dept, from, to, nd, rsn, alt, type, st, on, pp, fa, amt, ah, aon, avp;
    private DatePickerDialog dpd;
    TextView em;
    String dte, sid, role;
    SimpleDateFormat dateFormatter;
    MenuItem search;
    SharedPreferences sp;
    SparseArray<Bitmap> imgl;
    RequestHandler rh;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);
        AdminMenuList.setActivity("2");
        lv = (ListView) findViewById(R.id.dsblist);
        em = (TextView) findViewById(R.id.emptymessege);
        imgl = new SparseArray<>();
        dateFormatter = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
        dte = dateFormatter.format(new Date());
        rh = new RequestHandler(Dashboard.this);
        sp = getSharedPreferences("Profile", MODE_PRIVATE);
        sid = sp.getString("Faculty_Id", "");
        role = sp.getString("Role", "");
        new dashboardlist().execute(dte, sid, role);
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Log.e("TAG", i + "");
                Intent m = new Intent(Dashboard.this, SelfDetail.class);
                m.putExtra("Type", type.get(i));
                m.putExtra("From", from.get(i));
                m.putExtra("To", to.get(i));
                m.putExtra("NoDays", nd.get(i));
                m.putExtra("Reason", rsn.get(i));
                m.putExtra("Alternate", alt.get(i));
                m.putExtra("Status", "1");
                m.putExtra("RjReason", "");
                m.putExtra("OnD", on.get(i));
                m.putExtra("PP", pp.get(i));
                m.putExtra("FAssist", fa.get(i));
                m.putExtra("FAmount", amt.get(i));
                m.putExtra("A_HOD", ah.get(i));
                m.putExtra("A_VP", avp.get(i));
                startActivity(m);
                overridePendingTransition(R.anim.bottom_in, R.anim.top_out);
            }
        });
    }

    private void setDate() {
        Calendar newCalendar = Calendar.getInstance();
        dpd = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {

            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                Calendar newDate = Calendar.getInstance();
                newDate.set(year, monthOfYear, dayOfMonth);
                dte = dateFormatter.format(newDate.getTime());
                if (dte != "") {
                    String[] ed = {dte, sid, role};
                    new dashboardlist().execute(ed);
                }
                Log.e("TAG", dte);
            }

        }, newCalendar.get(Calendar.YEAR), newCalendar.get(Calendar.MONTH), newCalendar.get(Calendar.DAY_OF_MONTH));
        dpd.show();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_dashboard, menu);
        search = menu.findItem(R.id.search);
        if (role.equals("HOD"))
            search.setVisible(false);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.search) {
            showlist();
            return true;
        }
        if (id == R.id.bydate) {
            setDate();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void showlist() {
        final AlertDialog dialog;
        final AlertDialog.Builder adb = new AlertDialog.Builder(Dashboard.this);
        LayoutInflater li = LayoutInflater.from(Dashboard.this);
        View vw = li.inflate(R.layout.srch, null);
        ListView l = (ListView) vw.findViewById(R.id.srchlst);
        final ArrayList<String> choice = new ArrayList<>();
        adb.setTitle("Select Department");
        choice.add("CSE");
        choice.add("ISE");
        choice.add("ECE");
        choice.add("MEE");
        choice.add("CHE");
        choice.add("MCA");
        ArrayAdapter<String> adp = new ArrayAdapter<String>(this, R.layout.userid, choice);
        l.setAdapter(adp);
        adb.setView(vw);
        dialog = adb.create();
        dialog.show();
        l.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                dialog.cancel();
                load(choice.get(position));
            }
        });
    }

    private void load(String x) {
        int i = 0, c = 0;
        SparseBooleanArray sp = new SparseBooleanArray();
        while (i < nm.size()) {
            if (dept.get(i).trim().equals(x)) {
                sp.put(i, true);
                c++;
            } else sp.put(i, false);
            i++;
        }
        if (c > 0) {
            em.setVisibility(View.GONE);
            lv.setVisibility(View.VISIBLE);
            DashboardAdapter dba = new DashboardAdapter(Dashboard.this, nm, id, desg, dept, st, sp, imgl);
            lv.setAdapter(dba);
        } else {
            lv.setVisibility(View.GONE);
            em.setVisibility(View.VISIBLE);
            em.setBackgroundColor(Color.WHITE);
            em.setText("No  Entries Found");
        }
    }

    private class dashboardlist extends AsyncTask<String, Void, String> {
        private ProgressDialog dialog = new ProgressDialog(Dashboard.this);

        @Override
        protected String doInBackground(String... d) {
            HashMap<String, String> data = new HashMap<String, String>();
            data.put("Date", d[0]);
            data.put("Sid", d[1]);
            data.put("Role", d[2]);
            Log.e("TAG", sid + role);
            return rh.sendPostRequest("dashboardlist.php", data);
        }

        @Override
        protected void onPreExecute() {
            dialog.setMessage("Fetching Entries....");
            dialog.show();
            dialog.setCancelable(false);
        }

        @Override
        protected void onPostExecute(String s) {
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
        try {
            Log.e("TAG", "Inside aprv2:showlist " + s);
            JSONObject jo = new JSONObject(s);
            JSONArray ja = jo.getJSONArray("Results");
            if (ja.length() > 0) {
                nm = new ArrayList<String>();
                id = new ArrayList<String>();
                desg = new ArrayList<String>();
                dept = new ArrayList<String>();
                st = new ArrayList<String>();
                type = new ArrayList<String>();
                from = new ArrayList<String>();
                to = new ArrayList<String>();
                nd = new ArrayList<String>();
                rsn = new ArrayList<String>();
                alt = new ArrayList<String>();
                aon = new ArrayList<String>();
                on = new ArrayList<String>();
                pp = new ArrayList<String>();
                fa = new ArrayList<String>();
                amt = new ArrayList<String>();
                ah = new ArrayList<String>();
                avp = new ArrayList<String>();
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
                    on.add(c.getString("OnD"));
                    pp.add(c.getString("PP"));
                    fa.add(c.getString("FAsst"));
                    amt.add(c.getString("Famt"));
                    aon.add(c.getString("A_On"));
                    ah.add(c.getString("A_HOD"));
                    avp.add(c.getString("A_VP"));
                    st.add("3");
                }
                loadlist();
            } else {
                lv.setVisibility(View.GONE);
                em.setVisibility(View.VISIBLE);
                em.setBackgroundColor(Color.WHITE);
                em.setText("No  Entries Found");
            }

        } catch (JSONException e) {
            refresh("Connection Error", "Something Went Wrong. Try again later.", "Try Again");
        }
    }

    private void loadlist() {
        em.setVisibility(View.GONE);
        lv.setVisibility(View.VISIBLE);
        SparseBooleanArray spb = new SparseBooleanArray();
        for (int i = 0; i < nm.size(); i++)
            spb.put(i, true);
        DashboardAdapter dba = new DashboardAdapter(Dashboard.this, nm, id, desg, dept, st, spb, imgl);
        lv.setAdapter(dba);
    }

    public void refresh(final String title, final String msg, final String action) {
        final AlertDialog.Builder adb = new AlertDialog.Builder(Dashboard.this);
        adb.setTitle(title);
        adb.setMessage(msg);
        adb.setPositiveButton(action, null);
        final AlertDialog ad = adb.create();
        ad.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;
        ad.show();
        ad.getButton(DialogInterface.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (rh.isConnected(Dashboard.this)) {
                    ad.dismiss();
                    new dashboardlist().execute(dte, sid, role);
                } else {
                    ad.dismiss();
                    refresh(title, msg, action);
                }
            }
        });
    }
}
