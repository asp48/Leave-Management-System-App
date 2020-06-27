package com.example.amit.leave1;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.TextView;

import javax.security.auth.login.LoginException;


public class MainActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Runnable r = new Runnable() {
            @Override
            public void run() {
                Log.e("TAG", "Main Activity");
                SharedPreferences sp = getSharedPreferences("Profile", MODE_PRIVATE);
                int login = Integer.parseInt(sp.getString("Login", "0"));
                Intent i;
                Log.e("TAG", sp.getString("Role", ""));
                if ((sp.getString("Role", "").equals("P") || sp.getString("Role", "").equals("NT")) && login == 1)
                    i = new Intent(MainActivity.this, MenuList.class);
                else if ((sp.getString("Role", "").equals("HOD") || sp.getString("Role", "").equals("VP")) && login == 1)
                    i = new Intent(MainActivity.this, AdminMenuList.class);
                else i = new Intent(MainActivity.this, Login.class);
                startActivity(i);
                overridePendingTransition(R.anim.bottom_in, R.anim.top_out);
                finish();
            }
        };
        Handler handler = new Handler();
        handler.postDelayed(r, 2000);
    }
}
