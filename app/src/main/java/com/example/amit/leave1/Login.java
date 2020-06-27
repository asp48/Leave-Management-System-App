package com.example.amit.leave1;

import android.app.ActionBar;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.support.v7.internal.widget.AppCompatPopupWindow;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
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
import java.util.HashMap;

public class Login extends Activity {
    EditText pd;
    AutoCompleteTextView un;
    Button sn, sp;
    ArrayAdapter<String> adp;
    private String upd = "", uname;
    private ArrayList<String> u;
    private int n;
    SharedPreferences spf, spf2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        un = (AutoCompleteTextView) findViewById(R.id.uname);
        pd = (EditText) findViewById(R.id.upswd);
        sn = (Button) findViewById(R.id.snin);
        sp = (Button) findViewById(R.id.snup);
        spf = getSharedPreferences("Unames", MODE_PRIVATE);
        spf2 = getSharedPreferences("Profile", MODE_PRIVATE);
        setUname();
        un.setThreshold(0);
        sn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                uname = un.getText().toString().trim();
                upd = pd.getText().toString().trim();
                if (uname.equals("") || upd.equals("")) {
                    String msg = "This field cannot be left empty";
                    if (uname.equals(""))
                        un.setError(msg);
                    if (upd.equals(""))
                        pd.setError(msg);
                } else {
                    if (!u.contains(uname)) {
                        SharedPreferences.Editor e = spf.edit();
                        n++;
                        e.putString(String.valueOf(n), uname);
                        e.putInt("NEntry", n);
                        e.apply();
                    }
                    new signin().execute(uname);
                }
            }
        });
        sp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(Login.this, Registration.class);
                i.putExtra("RegorEdit", "0");
                startActivity(i);
                overridePendingTransition(R.anim.bottom_in, R.anim.top_out);
            }
        });

    }

    public void setUname() {
        u = new ArrayList<String>();
        n = spf.getInt("NEntry", 0);
        for (int i = 1; i <= n; i++) {
            u.add(spf.getString(String.valueOf(i), ""));
        }
        adp = new ArrayAdapter<String>(this, R.layout.userid, u);
        un.setAdapter(adp);
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        setUname();
    }

    private class signin extends AsyncTask<String, Void, String> {
        private ProgressDialog Dialog = new ProgressDialog(Login.this);

        @Override
        protected String doInBackground(String... unm) {
            RequestHandler rh = new RequestHandler(Login.this);
            HashMap<String, String> data = new HashMap<String, String>();
            data.put("Uname", unm[0]);
            return rh.sendPostRequest("login.php", data);
        }

        @Override
        protected void onPreExecute() {
            Dialog.setMessage("Signing in.....");
            Dialog.setCancelable(false);
            Dialog.show();
        }

        @Override
        protected void onPostExecute(String s) {
            Dialog.dismiss();
            Log.e("TAG",s);
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
                    authenticate(s);
            }
        }
    }

    private void authenticate(String s) {
        try {
            JSONObject jo = new JSONObject(s);
            JSONArray ja = jo.getJSONArray("Results");
            if (ja.length() < 1)
                Toast.makeText(getBaseContext(), "Invalid Username", Toast.LENGTH_LONG).show();
            else {
                JSONObject c = ja.getJSONObject(0);
                String spd = c.getString("password");
                String role = c.getString("role");
                String facid = c.getString("Faculty_Id");
                if (upd.equals(spd)) {
                    Toast.makeText(getBaseContext(), "Login Successful", Toast.LENGTH_LONG).show();
                    Intent i;
                    if (role.equals("P") || role.equals("NT")) {
                        i = new Intent(Login.this, MenuList.class);
                    } else {
                        i = new Intent(Login.this, AdminMenuList.class);
                    }
                    i.putExtra("View", "0");
                    Log.e("TAG", role);
                    SharedPreferences.Editor k = spf2.edit();
                    k.putString("Role", role);
                    k.putString("Faculty_Id", facid);
                    k.putString("Pswd", upd);
                    k.putString("Uname", uname);
                    k.putString("Login", "1");
                    k.apply();
                    startActivity(i);
                    overridePendingTransition(R.anim.bottom_in, R.anim.top_out);
                    finish();
                } else
                    Toast.makeText(getBaseContext(), "Invalid Password", Toast.LENGTH_LONG).show();

            }

        } catch (JSONException e) {
            Toast.makeText(getBaseContext(), "Connection Error:Something Went Wrong.Try again later", Toast.LENGTH_LONG).show();
        }

    }
}
