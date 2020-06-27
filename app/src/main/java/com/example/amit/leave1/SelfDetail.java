package com.example.amit.leave1;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TableRow;
import android.widget.TextView;


public class SelfDetail extends AppCompatActivity {
    TextView sts, rjlabel, rjr, f, t, n, a, r, lt, on, pp, fa, amt, ah, aVP, rsnl;
    private String type, from, to, nd, rsn, alt, st, rjrsn, ond, prpol, fasst, famt, ahod, avp;
    TableRow fr, tr, or, wr, far, amtr, ahr, avpr;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_self_detail);
        sts = (TextView) findViewById(R.id.status);
        rjlabel = (TextView) findViewById(R.id.rrj);
        rjr = (TextView) findViewById(R.id.rr);
        f = (TextView) findViewById(R.id.lfrom);
        t = (TextView) findViewById(R.id.lto);
        n = (TextView) findViewById(R.id.ndl);
        a = (TextView) findViewById(R.id.altfac);
        r = (TextView) findViewById(R.id.lrsn);
        lt = (TextView) findViewById(R.id.tl);
        on = (TextView) findViewById(R.id.on);
        pp = (TextView) findViewById(R.id.when);
        fa = (TextView) findViewById(R.id.fasst);
        amt = (TextView) findViewById(R.id.amt);
        ah = (TextView) findViewById(R.id.aprv_hod);
        aVP = (TextView) findViewById(R.id.aprv_vp);
        fr = (TableRow) findViewById(R.id.from_row);
        tr = (TableRow) findViewById(R.id.to_row);
        or = (TableRow) findViewById(R.id.on_row);
        wr = (TableRow) findViewById(R.id.when_row);
        far = (TableRow) findViewById(R.id.fa_row);
        amtr = (TableRow) findViewById(R.id.amt_row);
        ahr = (TableRow) findViewById(R.id.aprvHOD_row);
        avpr = (TableRow) findViewById(R.id.aprvVP_row);
        rsnl = (TextView) findViewById(R.id.rsnl);
        r.setVisibility(View.GONE);
        rjr.setVisibility(View.GONE);
        getSupportActionBar().setTitle("Leave Details");
        Intent i = getIntent();
        type = i.getStringExtra("Type");
        from = i.getStringExtra("From");
        to = i.getStringExtra("To");
        nd = i.getStringExtra("NoDays");
        rsn = i.getStringExtra("Reason");
        alt = i.getStringExtra("Alternate");
        st = i.getStringExtra("Status");
        rjrsn = i.getStringExtra("RjReason");
        ond = i.getStringExtra("OnD");
        prpol = i.getStringExtra("PP");
        fasst = i.getStringExtra("FAssist");
        famt = i.getStringExtra("FAmount");
        ahod = i.getStringExtra("A_HOD");
        avp = i.getStringExtra("A_VP");
        lt.setText(type);
        if (ond.equals("0000-00-00")) {
            f.setText(from);
            t.setText(to);
            or.setVisibility(View.GONE);
            wr.setVisibility(View.GONE);
        } else {
            on.setText(ond);
            fr.setVisibility(View.GONE);
            tr.setVisibility(View.GONE);
            if (prpol.equals("1"))
                pp.setText("Pre Lunch");
            else if (prpol.equals("2"))
                pp.setText("Post Lunch");
            else pp.setText("-----");
        }
        n.setText(nd);
        if (type.equals("OOD")) {
            if (fasst.equals("1")) {
                fa.setText("Yes");
                amt.setText(famt);
            } else {
                fa.setText("No");
                amtr.setVisibility(View.GONE);
            }
        } else {
            far.setVisibility(View.GONE);
            amtr.setVisibility(View.GONE);
        }
        r.setText(rsn);
        a.setText(alt);
        if (ahod.equals("0000-00-00"))
            ahr.setVisibility(View.GONE);
        else ah.setText(ahod);
        if (avp.equals("0000-00-00"))
            avpr.setVisibility(View.GONE);
        else aVP.setText(avp);
        if (st.equals("0")) {
            sts.setText("Pending");
            rjlabel.setVisibility(View.GONE);
            rjr.setVisibility(View.GONE);
        } else if (st.equals("2")) {
            sts.setText("Rejected");
            if (rjrsn.equals(""))
                rjr.setText("-----");
            else
                rjr.setText(rjrsn);

        } else {
            sts.setText("Accepted");
            rjlabel.setVisibility(View.GONE);
            rjr.setVisibility(View.GONE);
        }
        rsnl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (r.getVisibility() == View.VISIBLE) {
                    r.setVisibility(View.GONE);
                    rsnl.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.sortdown, 0);
                } else {
                    r.setVisibility(View.VISIBLE);
                    rsnl.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.sortup, 0);
                }
            }
        });
        rjlabel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (rjr.getVisibility() == View.VISIBLE) {
                    rjr.setVisibility(View.GONE);
                    rjlabel.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.sortdown, 0);
                } else {
                    rjr.setVisibility(View.VISIBLE);
                    rjlabel.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.sortup, 0);
                }
            }
        });
    }

}
