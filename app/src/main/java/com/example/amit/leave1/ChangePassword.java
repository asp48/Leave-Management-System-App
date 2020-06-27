package com.example.amit.leave1;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.HashMap;

import javax.net.ssl.HttpsURLConnection;


public class ChangePassword extends ActionBarActivity {
    Button b;
    EditText cp, np, rp;
    SharedPreferences sp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_password);
        getSupportActionBar().setTitle("Change Password");
        sp = getSharedPreferences("Profile", MODE_PRIVATE);
        SharedPreferences.Editor e = sp.edit();
        e.putString("Activity", "2");
        e.apply();
        b = (Button) findViewById(R.id.psubmit);
        Intent i = getIntent();
        cp = (EditText) findViewById(R.id.currentpsd);
        np = (EditText) findViewById(R.id.newpsd);
        rp = (EditText) findViewById(R.id.rtpsd);
        final String cpsd = sp.getString("Pswd", "");
        b.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String cpswd = cp.getText().toString();
                String npswd = np.getText().toString();
                String rtpswd = rp.getText().toString();
                if (cpswd.equals("") || npswd.equals("") || rtpswd.equals(""))
                    Toast.makeText(getBaseContext(), "Fill All fields", Toast.LENGTH_LONG).show();
                else if (cpswd.equals(cpsd) && rtpswd.equals(npswd))
                    new changepsd().execute(sp.getString("Faculty_Id", ""), npswd);
                else
                    Toast.makeText(getBaseContext(), "Invalid Entries", Toast.LENGTH_LONG).show();
            }
        });
    }

    @Override
    public void onBackPressed() {
        AlertDialog.Builder ab = new AlertDialog.Builder(ChangePassword.this);
        ab.setTitle("Confirm");
        ab.setMessage("Are you sure you want to exit?");
        ab.setPositiveButton("YES", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
                finish();
            }
        });
        ab.setNegativeButton("NO", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.cancel();
            }
        });
        ab.show();
    }

    private class changepsd extends AsyncTask<String, Void, String> {
        private ProgressDialog dialog = new ProgressDialog(ChangePassword.this);
        private String pswd = "";

        @Override
        protected String doInBackground(String... d) {
            pswd = d[1];
            RequestHandler rh = new RequestHandler(ChangePassword.this);
            HashMap<String, String> data = new HashMap<String, String>();
            data.put("Fid", d[0]);
            data.put("Pswd", d[1]);
            return rh.sendPostRequest("chpsd.php", data);
        }

        @Override
        protected void onPreExecute() {
            dialog.setMessage("Changing Password...");
            dialog.setCancelable(false);
            dialog.show();
        }

        @Override
        protected void onPostExecute(String s) {
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
                case "500":
                    SharedPreferences.Editor e = sp.edit();
                    e.putString("Pswd", pswd);
                    e.apply();
                    Toast.makeText(getBaseContext(), "Password Changed Successfully", Toast.LENGTH_SHORT).show();
                    break;
                default:
                    Toast.makeText(getBaseContext(), "Connection Error:Something Went Wrong.Try again later", Toast.LENGTH_SHORT).show();
            }
            dialog.dismiss();
            finish();
        }
    }
}
