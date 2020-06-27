package com.example.amit.leave1;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.media.MediaPlayer;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarActivity;
import android.util.Base64;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
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
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URI;
import java.util.ArrayList;
import java.util.HashMap;


public class MenuList extends ActionBarActivity {
    private static final int PICK_IMAGE_REQUEST = 1;
    public static final String UPLOAD_URL = "imgupload.php";
    Button ap, hy, cp;
    DrawerLayout dl;
    View dv;
    ImageView p;
    TextView nfc, tn, ti, td, tdp, tm, te, unme, office, adr;
    ArrayList<String> type, from, to, nd, rsn, alt, st, rjrsn, lid, on, pp, fa, amt, aon, stH, stVP, aH, aVP;
    private String cl, rl, el;
    private Uri filePath;
    private Bitmap bitmap;
    private String facid, role;
    private String super_id;
    private ProgressBar pb;
    private Intent ntfnservice = null;
    private SharedPreferences sp;
    RequestHandler rh;
    String v;
    int alflag = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu_list);
        ap = (Button) findViewById(R.id.apply);
        hy = (Button) findViewById(R.id.history);
        cp = (Button) findViewById(R.id.chp);
        dl = (DrawerLayout) findViewById(R.id.drawerLayout);
        dv = (View) findViewById(R.id.drawer);
        p = (ImageView) findViewById(R.id.pf);
        tn = (TextView) findViewById(R.id.pname);
        ti = (TextView) findViewById(R.id.pid);
        td = (TextView) findViewById(R.id.pdesig);
        tdp = (TextView) findViewById(R.id.pdept);
        tm = (TextView) findViewById(R.id.pmobile);
        te = (TextView) findViewById(R.id.pemail);
        pb = (ProgressBar) findViewById(R.id.progressBar);
        unme = (TextView) findViewById(R.id.tuname);
        office = (TextView) findViewById(R.id.toffice);
        adr = (TextView) findViewById(R.id.address);
        pb.setVisibility(View.GONE);
        type = new ArrayList<String>();
        from = new ArrayList<String>();
        to = new ArrayList<String>();
        nd = new ArrayList<String>();
        rsn = new ArrayList<String>();
        alt = new ArrayList<String>();
        st = new ArrayList<String>();
        rjrsn = new ArrayList<String>();
        lid = new ArrayList<String>();
        rh = new RequestHandler(this);
        getSupportActionBar().setTitle("");
        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);
        int temp = dm.widthPixels;
        DrawerLayout.LayoutParams params = (android.support.v4.widget.DrawerLayout.LayoutParams) dv.getLayoutParams();
        params.width = 7 * temp / 8;
        dv.setLayoutParams(params);
        p.requestLayout();
        temp = dm.heightPixels;
        p.getLayoutParams().height = 3 * temp / 8;
        sp = getSharedPreferences("Profile", MODE_PRIVATE);
        role = sp.getString("Role", "");
        Log.e("TAG", "inside amlist " + role);
        facid = sp.getString("Faculty_Id", "");
        ti.setText(facid);
        cp.setText("CHANGE\nPASSWORD");
        new profilefetch().execute(facid);
        new GetImage(p, pb).execute(facid);
        checkIfpending(facid);
        cp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(MenuList.this, ChangePassword.class);
                startActivity(i);
                overridePendingTransition(R.anim.bottom_in, R.anim.top_out);
            }
        });
        hy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(MenuList.this, Notification.class);
                i.putExtra("View", "1");
                i.putExtra("Faculty_Id", facid);
                startActivity(i);
                overridePendingTransition(R.anim.bottom_in, R.anim.top_out);
            }
        });
        ap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (alflag == 1) {
                    Toast.makeText(getBaseContext(), "Cannot apply while your previous request is pending", Toast.LENGTH_SHORT).show();
                    return;
                }
                Intent i = new Intent(MenuList.this, ApplyLeave.class);
                startActivity(i);
                overridePendingTransition(R.anim.bottom_in, R.anim.top_out);
            }
        });

    }

    private void checkIfpending(String fid) {
        LocalDB db = new LocalDB(MenuList.this);
        if (db.pending(fid))
            alflag = 1;
        else alflag = 0;
    }

    public void choose(View v) {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);
    }

    public void showAlertDialog(String msg, final int t) {
        AlertDialog.Builder ab = new AlertDialog.Builder(MenuList.this);
        ab.setTitle("Confirm");
        ab.setMessage(msg);
        ab.setPositiveButton("YES", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
                if (ntfnservice != null)
                    stopService(ntfnservice);
                if (t == 1) {
                    SharedPreferences.Editor e = sp.edit();
                    e.putString("Login", "0");
                    e.apply();
                    Intent i = new Intent(MenuList.this, Login.class);
                    startActivity(i);
                    overridePendingTransition(R.anim.bottom_in, R.anim.top_out);
                }
                finish();
            }
        });
        ab.setNegativeButton("NO", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int i) {
                dialog.cancel();
            }
        });
        ab.show();
    }

    public void onBackPressed() {
        showAlertDialog("Are you sure you want to exit?", 0);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_menu_list, menu);
        MenuItem item = menu.findItem(R.id.nfn);
        MenuItemCompat.setActionView(item, R.layout.notif_icon);
        View view = MenuItemCompat.getActionView(item);
        nfc = (TextView) view.findViewById(R.id.count);
        RelativeLayout nfi = (RelativeLayout) view.findViewById(R.id.notif_ic);
        nfc.setVisibility(View.GONE);
        nfi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (nfc.getVisibility() == View.VISIBLE) {
                    nfc.setVisibility(View.GONE);
                    Intent x = new Intent(MenuList.this, Notification.class);
                    x.putExtra("View", "0");
                    x.putStringArrayListExtra("Type", type);
                    x.putStringArrayListExtra("From", from);
                    x.putStringArrayListExtra("To", to);
                    x.putStringArrayListExtra("NoDays", nd);
                    x.putStringArrayListExtra("Reason", rsn);
                    x.putStringArrayListExtra("Alternate", alt);
                    x.putStringArrayListExtra("Status", st);
                    x.putStringArrayListExtra("RjReason", rjrsn);
                    x.putStringArrayListExtra("LeaveId", lid);
                    x.putStringArrayListExtra("OnD", on);
                    x.putStringArrayListExtra("PP", pp);
                    x.putStringArrayListExtra("FAssist", fa);
                    x.putStringArrayListExtra("FAmount", amt);
                    x.putStringArrayListExtra("ByHOD", stH);
                    x.putStringArrayListExtra("ByVP", stVP);
                    x.putStringArrayListExtra("A_HOD", aH);
                    x.putStringArrayListExtra("A_VP", aVP);
                    x.putStringArrayListExtra("A_On", aon);
                    x.putExtra("Faculty_Id", facid);
                    startActivity(x);
                    overridePendingTransition(R.anim.bottom_in, R.anim.top_out);
                } else {
                    Toast.makeText(getBaseContext(), "Notifications not found", Toast.LENGTH_LONG).show();
                }
            }
        });
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        SharedPreferences.Editor e = sp.edit();
        e.putString("Activity", "0");
        e.apply();
        checkIfpending(facid);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.profile) {
            dl.openDrawer(dv);
        } else if (id == R.id.stats) {
            Intent i = new Intent(MenuList.this, Statistics.class);
            startActivity(i);
            overridePendingTransition(R.anim.rotate_out, R.anim.rotate_in);
        } else if (id == R.id.logout)
            showAlertDialog("Are you sure you want to logout?", 1);
        else if (id == R.id.editp) {

            Intent i = new Intent(MenuList.this, Registration.class);
            i.putExtra("RegorEdit", "1");
            i.putExtra("Name", tn.getText().toString());
            i.putExtra("Faculty_Id", facid);
            i.putExtra("Designation", td.getText().toString());
            int temp = 0;
            switch (sp.getString("Dept", "")) {
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
            i.putExtra("Mobile", tm.getText().toString());
            i.putExtra("Office", sp.getString("Office", ""));
            i.putExtra("Address", sp.getString("Address", ""));
            i.putExtra("Username", sp.getString("Uname", ""));
            i.putExtra("Email", te.getText().toString());
            i.putExtra("CL", cl + "");
            i.putExtra("RH", rl + "");
            i.putExtra("EL", el + "");
            startActivity(i);
        } else {
            return super.onOptionsItemSelected(item);
        }

        return true;
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {

            filePath = data.getData();
            try {
                Bitmap bitmaptemp = MediaStore.Images.Media.getBitmap(getContentResolver(), filePath);
                bitmap = Bitmap.createScaledBitmap(bitmaptemp, 180, 180, false);
                p.setImageBitmap(rh.getCircleBitmap(bitmaptemp));
                rh.uploadImage(UPLOAD_URL, bitmap, facid);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private class profilefetch extends AsyncTask<String, Void, String> {
        private ProgressDialog dialog = new ProgressDialog(MenuList.this);

        @Override
        protected String doInBackground(String... id) {
            HashMap<String, String> data = new HashMap<String, String>();
            data.put("Fid", id[0]);
            return rh.sendPostRequest("profile.php", data);
        }

        @Override
        protected void onPreExecute() {
            dialog.setMessage("Fetching details....");
            dialog.setCancelable(false);
            dialog.show();
        }

        @Override
        protected void onPostExecute(String s) {
            dialog.cancel();
            routing(s);
        }
    }

    private void showDetails(String jsonstr) {
        Log.e("TAG", jsonstr);
        int fg = 0;
        try {
            JSONObject jo = new JSONObject(jsonstr);
            JSONArray ja = jo.getJSONArray("Results");
            if (ja.length() < 1) {
                refresh("Server Error", "Something Went Wrong. Try again later.", "Try Again");
            } else {
                JSONObject c = ja.getJSONObject(0);
                String name = c.getString("Name");
                String desg = c.getString("Desg");
                String dept = c.getString("Dept");
                String mobile = c.getString("Mob");
                String email = c.getString("Email");
                String onum = c.getString("Office");
                String adrs = c.getString("Adr");
                super_id = c.getString("Sid");
                cl = c.getString("CL");
                rl = c.getString("RL");
                el = c.getString("EL");
                SharedPreferences.Editor e = sp.edit();
                e.putString("Name", name);
                e.putString("Desg", desg);
                e.putString("Dept", dept);
                e.putString("Mob", mobile);
                e.putString("Email", email);
                e.putString("Office", onum);
                e.putString("Address", adrs);
                e.putString("Super_Id", super_id);
                e.putString("O_CL", cl);
                e.putString("O_RH", rl);
                e.putString("O_EL", el);
                e.putString("Activity", "0");
                e.apply();
                tn.setText(name);
                td.setText(desg);
                tdp.setText(dept);
                tm.setText(mobile);
                te.setText(email);
                adr.setText(adrs);
                office.setText(onum);
                unme.setText(sp.getString("Uname", ""));
                getSupportActionBar().setTitle(name);
            }
        } catch (JSONException e) {
            refresh("Connection Error", "Something Went Wrong. Try again later.", "Try Again");
            fg = 1;
        }
        if (fg == 0) {
            Log.e("TAG", "launching service");
            ntfnservice = new Intent(this, NotificationReceiver.class);
            ntfnservice.putExtra("Faculty_Id", facid);
            startService(ntfnservice);
        }
    }

    @Override
    protected void onNewIntent(Intent intent) {
        Log.e("TAG", "inside intent1");
        super.onNewIntent(intent);
        setIntent(intent);
        Intent t = getIntent();
        v = t.getStringExtra("View");
        Log.e("TAG", "inside new intent");
        if (v.equals("1")) {
            Log.e("TAG", "inside intent2");
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
            nfc.setVisibility(View.VISIBLE);
            nfc.setText(String.valueOf(type.size()));
            if (sp.getString("Activity", "").equals("0")) {
                Uri notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
                MediaPlayer mp = MediaPlayer.create(getApplicationContext(), notification);
                mp.start();
                mp.setLooping(false);
            }
        }
    }

    private void routing(String s) {
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
                showDetails(s);
        }
    }

    public void refresh(final String title, final String msg, final String action) {
        final AlertDialog.Builder adb = new AlertDialog.Builder(MenuList.this);
        adb.setTitle(title);
        adb.setMessage(msg);
        adb.setPositiveButton(action, null);
        adb.setCancelable(false);
        final AlertDialog ad = adb.create();
        ad.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;
        ad.show();
        ad.getButton(DialogInterface.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (rh.isConnected(MenuList.this)) {
                    ad.dismiss();
                    new profilefetch().execute(facid);
                    new GetImage(p, pb).execute(facid);
                } else {
                    ad.dismiss();
                    refresh(title, msg, action);
                }
            }
        });
    }
}
