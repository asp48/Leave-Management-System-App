package com.example.amit.leave1;

import android.app.TabActivity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TabHost;


public class Approval extends TabActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_approval);
        AdminMenuList.setActivity("2");
        TabHost tabHost = (TabHost) findViewById(android.R.id.tabhost);
        TabHost.TabSpec tab1 = tabHost.newTabSpec("History");
        TabHost.TabSpec tab2 = tabHost.newTabSpec("Approve");
        Intent i = getIntent();
        String sid = i.getStringExtra("Faculty_Id");
        String role = i.getStringExtra("Role");
        // Set the Tab name and Activity
        // that will be opened when particular Tab will be selected
        tab1.setIndicator("History");
        Intent j = new Intent(Approval.this, aprvtab2.class);
        j.putExtra("Super_Id", sid);
        j.putExtra("Role", role);
        tab1.setContent(j);
        tab2.setIndicator("Approve");
        Intent k = new Intent(Approval.this, aprvtab1.class);
        k.putExtra("Faculty_Id", sid);
        k.putExtra("Role", role);
        tab2.setContent(k);
        tabHost.addTab(tab1);
        tabHost.addTab(tab2);
    }
}
