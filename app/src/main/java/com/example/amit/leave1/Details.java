package com.example.amit.leave1;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.AsyncTask;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.SparseArray;
import android.util.SparseBooleanArray;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

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
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;

import javax.net.ssl.HttpsURLConnection;


public class Details extends ActionBarActivity {
    ListView lv;
    ImageView profile;
    ArrayList<String> nm, unme, fid, desg, dept, mob, onum, mail, adrs, cl, rl, el, st, roles;
    ScrollView sv;
    TextView tv, n, un, fac_id, d, m, on, ml, adr;
    EditText clt, rlt, elt;
    int selected;
    MenuItem efp;
    String v, sid = "", role = "", dpt = "";
    ;
    Button ac, rj;
    boolean fg = false;
    SharedPreferences sp;
    RequestHandler rh;
    SparseArray<Bitmap> imgl;
    SparseBooleanArray spb;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);
        AdminMenuList.setActivity("2");
        lv = (ListView) findViewById(R.id.detail);
        sv = (ScrollView) findViewById(R.id.vprofile);
        tv = (TextView) findViewById(R.id.emptymessege);
        n = (TextView) findViewById(R.id.fname);
        fac_id = (TextView) findViewById(R.id.fid);
        d = (TextView) findViewById(R.id.fdesig);
        m = (TextView) findViewById(R.id.fmob);
        ml = (TextView) findViewById(R.id.fmail);
        clt = (EditText) findViewById(R.id.fcl);
        rlt = (EditText) findViewById(R.id.frl);
        profile = (ImageView) findViewById(R.id.pf);
        un = (TextView) findViewById(R.id.funame);
        adr = (TextView) findViewById(R.id.fadr);
        elt = (EditText) findViewById(R.id.fel);
        on = (TextView) findViewById(R.id.office);
        ac = (Button) findViewById(R.id.accept);
        rj = (Button) findViewById(R.id.rej);
        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);
        int h = dm.heightPixels;
        profile.requestLayout();
        profile.getLayoutParams().height = 3 * h / 8;
        rh = new RequestHandler(Details.this);
        sp = getSharedPreferences("Profile", MODE_PRIVATE);
        Intent k = getIntent();
        v = k.getStringExtra("View");
        sid = sp.getString("Faculty_Id", "");
        if (v.equals("1")) {
            getSupportActionBar().setTitle("New Profiles");
            role = sp.getString("Role", "");
            dpt = sp.getString("Dept", "");
        } else {
            getSupportActionBar().setTitle("Employees List");
            clt.setEnabled(false);
            rlt.setEnabled(false);
            elt.setEnabled(false);
        }
        nm = new ArrayList<>();
        fid = new ArrayList<>();
        desg = new ArrayList<>();
        dept = new ArrayList<>();
        mob = new ArrayList<>();
        mail = new ArrayList<>();
        cl = new ArrayList<>();
        rl = new ArrayList<>();
        st = new ArrayList<>();
        onum = new ArrayList<>();
        adrs = new ArrayList<>();
        el = new ArrayList<>();
        unme = new ArrayList<>();
        imgl = new SparseArray<>();
        roles = new ArrayList<>();
        sv.setVisibility(View.GONE);
        tv.setVisibility(View.GONE);
        new getFacultyList().execute(sid, role, dpt);
        ac.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fg = true;
                new ProfileApprove().execute(sid, fid.get(selected), clt.getText().toString().trim(), rlt.getText().toString().trim(),
                        elt.getText().toString().trim(), "0");
            }
        });
        rj.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fg = false;
                new ProfileApprove().execute(sid, fid.get(selected), clt.getText().toString().trim(), rlt.getText().toString().trim(),
                        elt.getText().toString().trim(), "1");
            }
        });
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                selected = i;
                dispDetails();
            }
        });

    }

    private void dispDetails() {
        lv.setVisibility(View.GONE);
        sv.setVisibility(View.VISIBLE);
        if (v.equals("0")) {
            ac.setVisibility(View.GONE);
            rj.setVisibility(View.GONE);
        }
        efp.setVisible(true);
        n.setText(nm.get(selected));
        fac_id.setText(fid.get(selected));
        d.setText(desg.get(selected));
        m.setText(mob.get(selected));
        ml.setText(mail.get(selected));
        clt.setText(cl.get(selected));
        rlt.setText(rl.get(selected));
        on.setText(onum.get(selected));
        un.setText(unme.get(selected));
        adr.setText(adrs.get(selected));
        elt.setText(el.get(selected));
        profile.setImageBitmap(imgl.get(selected));
    }

    private void load() {
        if (fid.isEmpty()) {
            sv.setVisibility(View.GONE);
            lv.setVisibility(View.GONE);
            tv.setVisibility(View.VISIBLE);
            tv.setBackgroundColor(Color.WHITE);
            tv.setText("No Entries Found");
        } else {
            sv.setVisibility(View.GONE);
            lv.setVisibility(View.VISIBLE);
            DashboardAdapter dba = new DashboardAdapter(Details.this, nm, fid, desg, dept, st, spb, imgl);
            lv.setAdapter(dba);
        }
    }

    @Override
    public void onBackPressed() {
        if (lv.getVisibility() == View.VISIBLE || tv.getVisibility() == View.VISIBLE) {
            finish();
        } else {
            sv.setVisibility(View.GONE);
            lv.setVisibility(View.VISIBLE);
            load();
            efp.setVisible(false);
        }
    }

    private class getFacultyList extends AsyncTask<String, Void, String> {
        private ProgressDialog dialog = new ProgressDialog(Details.this);

        @Override
        protected String doInBackground(String... d) {
            HashMap<String, String> data = new HashMap<String, String>();
            String url = "";
            Log.e("TAG", d[0] + d[1] + d[2]);
            if (v.equals("0")) {
                data.put("Super_Id", d[0]);
                url = "getfacultylist.php";
            } else {
                data.put("Role", d[1]);
                data.put("Dept", d[2]);
                url = "getProfilelist.php";
            }
            return rh.sendPostRequest(url, data);
        }

        @Override
        protected void onPreExecute() {
            dialog.setMessage("Fetching List...");
            dialog.show();
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

    @Override
    protected void onRestart() {
        super.onRestart();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    private void showList(String jsnstr) {
        Log.e("TAG", jsnstr);
        try {
            JSONObject jo = new JSONObject(jsnstr);
            JSONArray ja = jo.getJSONArray("Results");
            if (ja.length() > 0) {
                for (int i = 0; i < ja.length(); i++) {
                    JSONObject c = ja.getJSONObject(i);
                    nm.add(c.getString("NAME"));
                    fid.add(c.getString("Faculty_Id"));
                    desg.add(c.getString("DESIGNATION"));
                    dept.add(c.getString("DEPARTMENT"));
                    mob.add(c.getString("MOBILE"));
                    mail.add(c.getString("EMAIL"));
                    cl.add(c.getString("CL"));
                    rl.add(c.getString("RL"));
                    onum.add(c.getString("Office"));
                    el.add(c.getString("EL"));
                    adrs.add(c.getString("Adr"));
                    unme.add(c.getString("Uname"));
                    roles.add(c.getString("Role"));
                    st.add("3");
                }
            }
            spb = new SparseBooleanArray();
            for (int k = 0; k < nm.size(); k++)
                spb.put(k, true);
            load();
        } catch (JSONException e) {
            refresh("Connection Error", "Something Went Wrong. Try again later.", "Try Again");
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_details, menu);
        efp = menu.findItem(R.id.editfp);
        efp.setVisible(false);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.editfp) {
            Intent i = new Intent(Details.this, Registration.class);
            i.putExtra("RegorEdit", "2");
            i.putExtra("Name", nm.get(selected));
            i.putExtra("Faculty_Id", fid.get(selected));
            i.putExtra("Designation", desg.get(selected));
            int temp = 0;
            switch (dept.get(selected)) {
                case "CSE":
                    temp = 1;
                    break;
                case "ISE":
                    temp = 2;
                    break;
                case "ECE":
                    temp = 3;
                    break;
                case "MEE":
                    temp = 4;
                    break;
                case "CHE":
                    temp = 5;
                    break;
                case "MCA":
                    temp = 6;
                    break;
            }
            i.putExtra("Dept", temp);
            i.putExtra("Mobile", mob.get(selected));
            i.putExtra("Office", onum.get(selected));
            i.putExtra("Address", adrs.get(selected));
            i.putExtra("Username", unme.get(selected));
            i.putExtra("Roles", roles.get(selected));
            i.putExtra("Email", mail.get(selected));
            i.putExtra("CL", cl.get(selected));
            i.putExtra("RH", rl.get(selected));
            i.putExtra("EL", el.get(selected));
            startActivityForResult(i, 2);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private class ProfileApprove extends AsyncTask<String, Void, String> {
        private ProgressDialog pd;

        @Override
        protected void onPreExecute() {
            pd = new ProgressDialog(Details.this);
            pd.setMessage("Updating Database...");
            pd.show();
            pd.setCancelable(false);
        }

        @Override
        protected String doInBackground(String... d) {
            HashMap<String, String> data = new HashMap<String, String>();
            data.put("Sid", d[0]);
            data.put("Fid", d[1]);
            data.put("CL", d[2]);
            data.put("RH", d[3]);
            data.put("EL", d[4]);
            data.put("Status", d[5]);
            return rh.sendPostRequest("profaprv.php", data);
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
                case "500501":
                case "501":
                    Toast.makeText(getBaseContext(), "Updated Successfully", Toast.LENGTH_SHORT).show();
                    pd.dismiss();
                    sendMail();
                    spb.put(selected, false);
                    load();
                    break;
                default:
                    Toast.makeText(getBaseContext(), "Connection Error:Something Went Wrong.Try again later", Toast.LENGTH_SHORT).show();
                    pd.dismiss();
            }
        }
    }

    public void sendMail() {
        Intent m = new Intent(Intent.ACTION_SEND);
        m.setType("message/rfc822");
        m.putExtra(Intent.EXTRA_EMAIL, new String[]{mail.get(selected)});
        m.putExtra(Intent.EXTRA_SUBJECT, "LMS Profile Status");
        String t;
        if (fg) t = "accepted";
        else t = "rejected";
        m.putExtra(Intent.EXTRA_TEXT, "Your Profile for LMS application is " + t);
        try {
            startActivity(Intent.createChooser(m, "Send Mail..."));
        } catch (ActivityNotFoundException e) {
            Toast.makeText(getBaseContext(), "There are no email clients installed", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.e("TAG", " " + resultCode + "  " + requestCode);
        if (requestCode == 2 && resultCode == RESULT_OK) {
            Log.e("TAG", "inside onactivity result");
            nm.add(selected, data.getStringExtra("Name"));
            unme.add(selected, data.getStringExtra("Uname"));
            fid.add(selected, data.getStringExtra("Fid"));
            desg.add(selected, data.getStringExtra("Desg"));
            dept.add(selected, data.getStringExtra("Dept"));
            cl.add(selected, data.getStringExtra("CL"));
            rl.add(selected, data.getStringExtra("RH"));
            el.add(selected, data.getStringExtra("EL"));
            dispDetails();
        }
    }

    public void refresh(final String title, final String msg, final String action) {
        final AlertDialog.Builder adb = new AlertDialog.Builder(Details.this);
        adb.setTitle(title);
        adb.setMessage(msg);
        adb.setPositiveButton(action, null);
        final AlertDialog ad = adb.create();
        ad.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;
        ad.show();
        ad.getButton(DialogInterface.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (rh.isConnected(Details.this)) {
                    ad.dismiss();
                    new getFacultyList().execute(sid, role, dpt);
                } else {
                    ad.dismiss();
                    refresh(title, msg, action);
                }
            }
        });
    }
}