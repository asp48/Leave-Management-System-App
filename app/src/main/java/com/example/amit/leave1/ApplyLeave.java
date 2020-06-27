package com.example.amit.leave1;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.NumberPicker;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;

import javax.net.ssl.HttpsURLConnection;


public class ApplyLeave extends ActionBarActivity implements View.OnClickListener, RadioGroup.OnCheckedChangeListener {
    private EditText fromDateEtxt;
    private EditText toDateEtxt, ed1, ed2, ed4, amt;
    private RadioGroup rg1, rg2;
    private DatePickerDialog dialog;
    private SimpleDateFormat dateFormatter;
    private EditText nodays;
    private EditText altfac, nr, on;
    private String facid, super_id, reasons, role, st1 = "", st2 = "", st3 = "", fasst = "0";
    private int typel, f = 0, dma = 1, choosen = 0, op1 = 0;
    private double diffInDays;
    private CheckBox fa;
    private TextView t1, t2, t3, t4, hdl, fdl, onl, fal;
    private ArrayList<String> tl, rn;
    private NumberPicker d;
    private Spinner reason;
    private double cl, rh, el;
    SharedPreferences sp;
    private String[] m, yr, d1, d2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_apply_leave);
        getSupportActionBar().setTitle("Apply Leave");
        sp = getSharedPreferences("Profile", MODE_PRIVATE);
        cl = Double.parseDouble(sp.getString("O_CL", "0"));
        rh = Double.parseDouble(sp.getString("O_RH", "0"));
        el = Double.parseDouble(sp.getString("O_EL", "0"));
        findViewsById();
        SharedPreferences.Editor e = sp.edit();
        e.putString("Activity", "2");
        e.apply();
        Spinner spinner = (Spinner) findViewById(R.id.leave_spinner);
        reason = (Spinner) findViewById(R.id.Reason_spinner);
        fromDateEtxt.setOnClickListener(this);
        toDateEtxt.setOnClickListener(this);
        on.setOnClickListener(this);
        nodays.setOnClickListener(this);
        facid = sp.getString("Faculty_Id", "");
        super_id = sp.getString("Super_Id", "");
        role = sp.getString("Role", "");
        Log.e("TAG", "inside al " + role);
        nr.setVisibility(View.GONE);
        fa.setVisibility(View.GONE);
        fal.setVisibility(View.GONE);
        amt.setVisibility(View.GONE);
        if (role.equals("P")) {
            rg1.setVisibility(View.GONE);
            actv(false);
            fdl.setVisibility(View.GONE);
        } else if (role.equals("NT")) {
            tl.remove(2);
        }
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, tl);
        spinner.setAdapter(adapter);
        loadReasons();
        dateFormatter = new SimpleDateFormat("dd-MM-yyyy", Locale.US);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                typel = i;
                if (tl.get(typel).equals("OOD")) {
                    fal.setVisibility(View.VISIBLE);
                    fa.setVisibility(View.VISIBLE);
                } else {
                    fal.setVisibility(View.GONE);
                    fa.setVisibility(View.GONE);
                    amt.setVisibility(View.GONE);
                }
                if (!(tl.get(typel).equals("CL") && role.equals("HOD"))) {
                    rg1.setVisibility(View.GONE);
                    actv(false);
                    fdl.setVisibility(View.GONE);
                } else {
                    rg1.setVisibility(View.VISIBLE);
                }
                if (tl.get(typel).equals("OOD")) {
                    rn.clear();
                    rn = new ArrayList<String>();
                    rn.add("Valuation");
                    rn.add("Squad");
                    rn.add("University Duties");
                    rn.add("Other");
                    loadReasons();
                } else if (tl.get(typel).equals("SPCL")) {
                    rn.clear();
                    rn = new ArrayList<String>();
                    rn.add("Seminar");
                    rn.add("Workshop");
                    rn.add("FDP");
                    rn.add("SDP");
                    rn.add("Paper Presentation");
                    rn.add("Other");
                    loadReasons();
                } else {
                    rn.clear();
                    rn = new ArrayList<String>();
                    rn.add("Function");
                    rn.add("Not Feeling Well");
                    rn.add("Other");
                    loadReasons();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        fa.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    amt.setVisibility(View.VISIBLE);
                    fasst = "1";
                } else {
                    amt.setVisibility(View.GONE);
                    fasst = "0";
                }
            }
        });
        reason.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if (rn.get(i).equals("Other")) {
                    nr.setVisibility(View.VISIBLE);
                } else {
                    nr.setVisibility(View.GONE);
                    reasons = rn.get(i);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
    }


    private void loadReasons() {
        ArrayAdapter<String> adap = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, rn);
        reason.setAdapter(adap);
    }

    private void findViewsById() {

        ed1 = fromDateEtxt = (EditText) findViewById(R.id.et1);
        fromDateEtxt.setInputType(InputType.TYPE_NULL);
        fromDateEtxt.requestFocus();
        nodays = (EditText) findViewById(R.id.et3);
        ed2 = toDateEtxt = (EditText) findViewById(R.id.et2);
        toDateEtxt.setInputType(InputType.TYPE_NULL);
        ed4 = altfac = (EditText) findViewById(R.id.et4);
        rg1 = (RadioGroup) findViewById(R.id.radioGroup1);
        rg2 = (RadioGroup) findViewById(R.id.radioGroup2);
        rg1.setOnCheckedChangeListener((RadioGroup.OnCheckedChangeListener) this);
        rg2.setOnCheckedChangeListener((RadioGroup.OnCheckedChangeListener) this);
        t1 = (TextView) findViewById(R.id.from);
        t2 = (TextView) findViewById(R.id.to);
        t3 = (TextView) findViewById(R.id.no);
        t4 = (TextView) findViewById(R.id.alt);
        hdl = (TextView) findViewById(R.id.hllabel);
        fdl = (TextView) findViewById(R.id.fllabel);
        nr = (EditText) findViewById(R.id.nreason);
        on = (EditText) findViewById(R.id.on);
        onl = (TextView) findViewById(R.id.onlabel);
        fal = (TextView) findViewById(R.id.falabel);
        fa = (CheckBox) findViewById(R.id.fa);
        amt = (EditText) findViewById(R.id.amt);
        tl = new ArrayList<String>();
        rn = new ArrayList<String>();
        if (cl > 0)
            tl.add("CL");
        if (rh > 0)
            tl.add("RH");
        if (el > 0)
            tl.add("EL");
        tl.add("OOD");
        tl.add("SPCL");
        rn.add("Function");
        rn.add("Not Feeling Well");
        rn.add("Other");
    }

    private void actv(final boolean active) {
        if (!active) {
            hdl.setVisibility(View.GONE);
            onl.setVisibility(View.GONE);
            on.setText("");
            on.setVisibility(View.GONE);
            rg2.setVisibility(View.GONE);
            fdl.setVisibility(View.VISIBLE);
            fromDateEtxt.setVisibility(View.VISIBLE);
            toDateEtxt.setVisibility(View.VISIBLE);
            t1.setVisibility(View.VISIBLE);
            t2.setVisibility(View.VISIBLE);
        } else {
            fdl.setVisibility(View.GONE);
            fromDateEtxt.setText("");
            toDateEtxt.setText("");
            fromDateEtxt.setVisibility(View.GONE);
            toDateEtxt.setVisibility(View.GONE);
            t1.setVisibility(View.GONE);
            t2.setVisibility(View.GONE);
            hdl.setVisibility(View.VISIBLE);
            onl.setVisibility(View.VISIBLE);
            on.setVisibility(View.VISIBLE);
            rg2.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onCheckedChanged(RadioGroup group, int checkedId) {
        switch (checkedId) {
            case R.id.hday:
                actv(true);
                diffInDays = 0.5;
                nodays.setText("0.5");
                break;

            case R.id.fday:
                actv(false);
                diffInDays = 0;
                nodays.setText("0");
                break;

            case R.id.pre:
                op1 = 1;
                diffInDays = 0.5;
                nodays.setText("0.5");
                break;

            case R.id.post:
                op1 = 2;
                diffInDays = 0.5;
                nodays.setText("0.5");
                break;
        }
    }


    private void setDateTimeField(final int v) {
        Calendar newCalendar = Calendar.getInstance();
        dialog = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {

            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                Calendar newDate = Calendar.getInstance();
                int h = newDate.get(Calendar.HOUR_OF_DAY);
                Log.e("TAG", h + "");
                newDate.set(year, monthOfYear, dayOfMonth);
                if (newDate.getTime().before(new Date()) || (newDate.getTime().equals(new Date()) && h >= 9)) {
                    Toast.makeText(getBaseContext(), "Invalid Date", Toast.LENGTH_SHORT).show();
                    return;
                }
                Date ftDate = newDate.getTime();
                if ((int) ((ftDate.getTime() - new Date().getTime()) / (1000 * 60 * 60 * 24)) > 15) {
                    Toast.makeText(getBaseContext(), "Cannot apply for leave more than 15 days before", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (v == 0) {
                    fromDateEtxt.setText(dateFormatter.format(newDate.getTime()));
                    if (!toDateEtxt.getText().toString().equals(""))
                        set();
                } else if (v == 1) {
                    toDateEtxt.setText(dateFormatter.format(newDate.getTime()));
                    if (!fromDateEtxt.getText().toString().equals(""))
                        set();
                } else {
                    on.setText(dateFormatter.format(newDate.getTime()));
                }
            }

        }, newCalendar.get(Calendar.YEAR), newCalendar.get(Calendar.MONTH), newCalendar.get(Calendar.DAY_OF_MONTH));
        dialog.show();
    }


    @Override
    public void onClick(View view) {
        Calendar c = Calendar.getInstance();
        int month = c.get(Calendar.MONTH) + 1;
        SharedPreferences.Editor e = sp.edit();
        if (tl.get(typel).equals("RH"))
            if (!sp.getString("Month", "").equals(String.valueOf(month))) {
                e.putString("Month", "");
                e.apply();
            }
        if (view == fromDateEtxt) {
            if (tl.get(typel).equals("RH")) {
                if (sp.getString("Dates_1", "").equals("") || sp.getString("Dates_2", "").equals(""))
                    new getRHDates().execute(month, 0);
                else openNumPicker(0);
            } else
                setDateTimeField(0);
        } else if (view == toDateEtxt) {
            if (tl.get(typel).equals("RH")) {
                if (sp.getString("Dates_1", "").equals("") || sp.getString("Dates_2", "").equals(""))
                    new getRHDates().execute(month, 1);
                else openNumPicker(1);
            } else
                setDateTimeField(1);
        } else if (view == on)
            setDateTimeField(2);
    }


    public void openNumPicker(final int x) {
        f = 0;
        AlertDialog.Builder adb = new AlertDialog.Builder(ApplyLeave.this);
        LayoutInflater li = LayoutInflater.from(ApplyLeave.this);
        View vw = li.inflate(R.layout.datepicker, null);
        d = (NumberPicker) vw.findViewById(R.id.dt);
        final Calendar c = Calendar.getInstance();
        final int cm = c.get(Calendar.MONTH) + 1;
        final int cy = c.get(Calendar.YEAR);
        m = new String[2];
        m[0] = String.valueOf(cm);
        m[1] = String.valueOf(1 + (cm) % 12);
        ArrayList<String> dt1;
        int d2s;
        dt1 = new ArrayList<>(Arrays.asList(sp.getString("Dates_1", "0").split("-")));
        d2 = sp.getString("Dates_2", "0").split("-");
        d2s = d2.length;
        if (dt1.get(0).equals("0"))
            dt1.remove(0);
        if (d2[0].equals("0"))
            d2s = 0;
        if (dt1.size() == 0 && d2s == 0) {
            Toast.makeText(getBaseContext(), "No RH Dates available for this and the next month", Toast.LENGTH_SHORT);
            return;
        }
        c.setTime(new Date());
        int today = c.get(Calendar.DAY_OF_MONTH);
        for (int i = 0; i < dt1.size(); i++) {
            if (today > Integer.parseInt(dt1.get(i)))
                dt1.remove(i);
            else break;
        }
        d1 = dt1.toArray(new String[dt1.size()]);
        Log.e("TAG", "" + d1.length + "  " + d2.length);
        if (cm == 12) {
            adb.setTitle("Choose Year");
            yr = new String[2];
            yr[0] = String.valueOf(cy);
            yr[1] = String.valueOf(cy + 1);
            d.setMinValue(0);
            d.setMaxValue(1);
            d.setDisplayedValues(yr);
            f = 1;
        } else {
            adb.setTitle("Choose Month");
            d.setMinValue(0);
            d.setMaxValue(1);
            d.setDisplayedValues(m);
        }
        d.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
                choosen = newVal;
            }
        });
        adb.setView(vw);
        adb.setPositiveButton("OK", null);
        final AlertDialog ad = adb.create();
        ad.show();
        ad.getButton(DialogInterface.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (f == 1) {
                    st3 = yr[choosen];
                    ad.setTitle("Choose Month");
                    d.setMinValue(0);
                    d.setMaxValue(1);
                    d.setDisplayedValues(m);
                    choosen = 0;
                } else if (f == 2) {
                    if (dma == 1) {
                        st1 = d1[choosen];
                    } else {
                        st1 = d2[choosen];
                        Log.e("TAG", "st1 " + st1);
                    }
                    String date = st1 + "-" + st2 + "-" + st3;
                    Log.e("TAG", date);
                    int h = c.get(Calendar.HOUR_OF_DAY);
                    if (st1.trim().equals(String.valueOf(h)) && h >= 9) {
                        Toast.makeText(getBaseContext(), "Invalid Date", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    Date ftdate;
                    try {
                        ftdate = dateFormatter.parse(date);
                    } catch (ParseException e) {
                        Toast.makeText(getBaseContext(), "Error while parsing!!!", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    if ((int) ((ftdate.getTime() - new Date().getTime()) / (1000 * 60 * 60 * 24)) > 15) {
                        Toast.makeText(getBaseContext(), "Cannot apply for leave more than 15 days before", Toast.LENGTH_SHORT).show();
                        ad.dismiss();
                        return;
                    }
                    if (x == 0) {
                        fromDateEtxt.setText(date);
                        if (!toDateEtxt.getText().toString().equals(""))
                            set();
                    } else if (x == 1) {
                        toDateEtxt.setText(date);
                        if (!fromDateEtxt.getText().toString().equals(""))
                            set();
                    }
                    ad.dismiss();
                } else {
                    st2 = m[choosen];
                    if (st3.equals(""))
                        st3 = String.valueOf(cy);
                    Log.e("TAG", choosen + "  " + d1.length + " " + d2.length);
                    if ((choosen == 0 && d1.length == 0) || (choosen == 1 && d2.length == 0)) {
                        Log.e("TAG", "inside rh dates not available");
                        Toast.makeText(getBaseContext(), "No RH Dates available for this Month", Toast.LENGTH_SHORT).show();
                        ad.dismiss();
                    } else {
                        ad.setTitle("Choose Day of Month");
                        d.setMinValue(0);
                        if (choosen == 0) {
                            Log.e("TAG", "display d1");
                            d.setMaxValue(d1.length - 1);
                            d.setDisplayedValues(d1);
                            dma = 1;
                        } else {
                            Log.e("TAG", "display d2");
                            d.setMaxValue(d2.length - 1);
                            d.setDisplayedValues(d2);
                            dma = 2;
                        }
                    }
                    f = 2;
                    choosen = 0;
                }
            }
        });
    }

    public void set() {
        Date d = null, d1 = null;
        try {
            d = dateFormatter.parse(fromDateEtxt.getText().toString());
        } catch (ParseException e) {
            Toast.makeText(getBaseContext(), "Error while Parsing", Toast.LENGTH_SHORT).show();
            return;
        }
        try {
            d1 = dateFormatter.parse(toDateEtxt.getText().toString());
        } catch (ParseException e) {
            Toast.makeText(getBaseContext(), "Error while Parsing", Toast.LENGTH_SHORT).show();
            return;
        }

        Calendar cal1 = Calendar.getInstance();
        cal1.setTime(d);
        Calendar cal2 = Calendar.getInstance();
        cal2.setTime(d1);
        diffInDays = 0;
        while (cal1.before(cal2) || cal1.equals(cal2)) {
            if (Calendar.SUNDAY != cal1.get(Calendar.DAY_OF_WEEK))
                diffInDays++;
            cal1.add(Calendar.DATE, 1);
        }
        Log.e("TAG", diffInDays + "");
        if (diffInDays == 0) {
            Toast.makeText(this, "Invalid dates", Toast.LENGTH_SHORT).show();
            fromDateEtxt.setText("");
            toDateEtxt.setText("");
            nodays.setText("");
        } else {
            nodays.setText(String.valueOf(diffInDays));
        }
    }

    public void cancel(View v) {
        Toast.makeText(getBaseContext(), "Request Cancelled", Toast.LENGTH_LONG).show();
        onBackPressed();
    }

    public void submit(View v) {
        if (diffInDays > 5) {
            Toast.makeText(getBaseContext(), "Cannot Apply leave for more than 5 days", Toast.LENGTH_SHORT).show();
            return;
        }
        String onD = "0000-00-00", famt = "0", fromD = "0000-00-00", toD = "0000-00-00";
        boolean flg = true;
        if (nr.getVisibility() == View.VISIBLE) {
            reasons = nr.getText().toString().trim();
            if (reasons.isEmpty()) flg = false;
        }
        if (on.getVisibility() == View.VISIBLE) {
            onD = on.getText().toString().trim();
            if (onD.isEmpty()) flg = false;
            if (rg2.getCheckedRadioButtonId() == -1) {
                flg = false;
                op1 = 0;
            }
        }
        if (amt.getVisibility() == View.VISIBLE) {
            famt = amt.getText().toString().trim();
            if (famt.isEmpty()) flg = false;
        }
        if (fromDateEtxt.getVisibility() == View.VISIBLE) {
            fromD = fromDateEtxt.getText().toString().trim();
            if (fromD.isEmpty()) flg = false;
        }
        if (toDateEtxt.getVisibility() == View.VISIBLE) {
            toD = toDateEtxt.getText().toString().trim();
            if (toD.isEmpty()) flg = false;
        }
        Log.e("TAG", fromD + " " + toD + " " + onD);
        if (!altfac.getText().toString().equals("") && flg) {
            diffInDays = Double.parseDouble(nodays.getText().toString().trim());
            if (tl.get(typel).equals("CL") && (cl - diffInDays) < 0) {
                Toast.makeText(this, "Not enough CL to apply this leave", Toast.LENGTH_SHORT).show();
            } else if (tl.get(typel).equals("RH") && (rh - diffInDays) < 0) {
                Toast.makeText(this, "Not enough RH to apply this leave", Toast.LENGTH_SHORT).show();
            } else if (tl.get(typel).equals("EL") && (el - diffInDays) < 0) {
                Toast.makeText(this, "Not enough EL to apply this leave", Toast.LENGTH_SHORT).show();
            } else {
                SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy", Locale.US);
                SimpleDateFormat sdf2 = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
                try {
                    Date d;
                    if (onD.equals("0000-00-00")) {
                        d = sdf.parse(fromD);
                        fromD = sdf2.format(d);
                        d = sdf.parse(toD);
                        toD = sdf2.format(d);
                    } else {
                        Log.e("TAG", onD);
                        d = sdf.parse(onD);
                        Log.e("TAG", d + "");
                        onD = sdf2.format(d);
                        Log.e("TAG", onD);
                    }
                } catch (Exception e) {
                    Toast.makeText(this, e.toString(), Toast.LENGTH_LONG).show();
                }
                String afac = altfac.getText().toString().trim();
                String[] data = {facid, super_id, tl.get(typel), fromD, toD, String.valueOf(diffInDays), reasons, afac, onD, String.valueOf(op1), fasst, famt};
                new apply().execute(data);
            }
        } else {
            Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show();
        }
    }

    private class apply extends AsyncTask<String, Void, String> {
        private ProgressDialog dialog = new ProgressDialog(ApplyLeave.this);
        RequestHandler rh = new RequestHandler(ApplyLeave.this);
        String values[];

        @Override
        protected String doInBackground(String... d) {
            values = new String[d.length];
            values = d;
            HashMap<String, String> data = new HashMap<String, String>();
            Log.e("TAG", d[0] + d[1] + d[2] + d[3] + d[4] + d[5] + d[6] + d[7] + d[8] + d[9] + d[10] + d[11]);
            data.put("Fid", d[0]);
            data.put("Sid", d[1]);
            data.put("Type", d[2]);
            data.put("From", d[3]);
            data.put("To", d[4]);
            data.put("Nd", d[5]);
            data.put("Rsn", d[6]);
            data.put("Alt", d[7]);
            data.put("OnD", d[8]);
            data.put("PR_POL", d[9]);
            data.put("FAst", d[10]);
            data.put("Famt", d[11]);
            return rh.sendPostRequest("apply.php", data);
        }

        @Override
        protected void onPreExecute() {
            dialog.setMessage("Sending Request......");
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
                default:
                    if (s.contains("500")) {
                        MoveToHistory(s, values);
                        Toast.makeText(getBaseContext(), "Successful Submission", Toast.LENGTH_SHORT).show();
                        onBackPressed();
                    } else
                        Toast.makeText(getBaseContext(), "Connection Error:Something Went Wrong.Try again later", Toast.LENGTH_SHORT).show();
            }
            dialog.dismiss();
        }
    }

    private void MoveToHistory(String r, String[] d) {
        LocalDB db = new LocalDB(ApplyLeave.this);
        String[] v = new String[23];
        String[] t = r.split("/");
        v[0] = d[1];
        v[1] = sp.getString("Name", "");
        v[2] = d[0];
        v[3] = sp.getString("Desg", "");
        v[4] = sp.getString("Dept", "");
        v[5] = t[1];
        v[6] = t[2];
        v[7] = d[2];
        v[8] = d[3];
        v[9] = d[4];
        v[10] = d[8];
        v[11] = d[9];
        v[12] = d[5];
        v[13] = d[6];
        v[14] = d[7];
        v[15] = d[10];
        v[16] = d[11];
        v[17] = "0";
        v[18] = "";
        v[19] = "0";
        v[20] = "0";
        v[21] = "0000-00-00";
        v[22] = "0000-00-00";
        Log.e("TAG", "Inside move to history");
        if (db.insert(v))
            Log.e("TAG", "moved to history");
    }

    private class getRHDates extends AsyncTask<Integer, Void, String> {
        private ProgressDialog pd;
        int x;
        int month;
        RequestHandler rh = new RequestHandler(ApplyLeave.this);

        @Override
        protected void onPreExecute() {
            pd = new ProgressDialog(ApplyLeave.this);
            pd.setCancelable(false);
            pd.setMessage("Fetching Dates...");
        }

        @Override
        protected String doInBackground(Integer... d) {
            month = d[0];
            x = d[1];
            HashMap<String, String> data = new HashMap<>();
            return rh.sendPostRequest("getRHDates.php", data);
        }

        @Override
        protected void onPostExecute(String s) {
            pd.dismiss();
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
                    setRHDates(s, x, month, rh);
            }
        }
    }

    public void setRHDates(String s, int x, int month, RequestHandler rh) {
        Log.e("TAG", s + " jhhd " + x);
        try {
            JSONObject jo = new JSONObject(s);
            JSONArray ja = jo.getJSONArray("Results");
            JSONObject jobj;
            if (ja.length() > 0) {
                SharedPreferences.Editor e = sp.edit();
                jobj = ja.getJSONObject(0);
                e.putString("Dates_1", jobj.getString("Dates"));
                jobj = ja.getJSONObject(1);
                e.putString("Dates_2", jobj.getString("Dates"));
                e.putString("Month", String.valueOf(month));
                e.apply();
                openNumPicker(x);
            } else
                Toast.makeText(getBaseContext(), "Connection Error:Something Went Wrong.Try again later", Toast.LENGTH_SHORT).show();
        } catch (JSONException e) {
            Toast.makeText(getBaseContext(), "Connection Error:Something Went Wrong.Try again later", Toast.LENGTH_SHORT).show();
        }
    }
}
