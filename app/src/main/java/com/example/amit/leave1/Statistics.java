package com.example.amit.leave1;

import android.app.ActionBar;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Window;
import android.widget.TextView;


public class Statistics extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_statistics);
        this.setFinishOnTouchOutside(true);
        TextView rla = (TextView) findViewById(R.id.rlapplied);
        TextView rlr = (TextView) findViewById(R.id.rlremain);
        TextView cla = (TextView) findViewById(R.id.clapplied);
        TextView clr = (TextView) findViewById(R.id.clremain);
        TextView elr = (TextView) findViewById(R.id.elr);
        SharedPreferences sp = getSharedPreferences("Profile", MODE_PRIVATE);
        String cl = sp.getString("O_CL", "0");
        String rh = sp.getString("O_RH", "0");
        String el = sp.getString("O_EL", "0");
        clr.setText(cl);
        rlr.setText(rh);
        elr.setText(el);
        rla.setText(String.valueOf(2 - Double.parseDouble(rh)));
        cla.setText(String.valueOf(12 - Double.parseDouble(cl)));
    }

}
