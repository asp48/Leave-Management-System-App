package com.example.amit.leave1;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.provider.MediaStore;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.Spinner;
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
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;

import javax.net.ssl.HttpsURLConnection;


public class Registration extends Activity {
    private static final int PICK_IMAGE_REQUEST = 1;
    EditText nm, facid, mob, unme, pswd, cpswd, onum, adrss, mail, cl, rh, el, odesg;
    Spinner deptment, desig;
    ImageView dp;
    String v, fid, o_name, o_uname, o_fid, o_desg;
    RadioButton rb1, rb2;
    ArrayList<String> dptlist, desgns;
    Button reg, bk;
    int index, o_dept, dsgi;
    RequestHandler rhndle;
    Bitmap bmp;
    int dpchanged = 0, valid[];

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);
        nm = (EditText) findViewById(R.id.name);
        desig = (Spinner) findViewById(R.id.designation);
        odesg = (EditText) findViewById(R.id.odesg);
        deptment = (Spinner) findViewById(R.id.dept);
        facid = (EditText) findViewById(R.id.fid);
        mob = (EditText) findViewById(R.id.phone);
        unme = (EditText) findViewById(R.id.uname);
        pswd = (EditText) findViewById(R.id.password);
        cpswd = (EditText) findViewById(R.id.cpassword);
        reg = (Button) findViewById(R.id.btnRegister);
        bk = (Button) findViewById(R.id.bkLoginScr);
        onum = (EditText) findViewById(R.id.office);
        adrss = (EditText) findViewById(R.id.address);
        dp = (ImageView) findViewById(R.id.profile);
        rb1 = (RadioButton) findViewById(R.id.teaching);
        rb2 = (RadioButton) findViewById(R.id.nteaching);
        mail = (EditText) findViewById(R.id.email);
        cl = (EditText) findViewById(R.id.cl);
        rh = (EditText) findViewById(R.id.rh);
        el = (EditText) findViewById(R.id.el);
        rhndle = new RequestHandler(Registration.this);
        odesg.setVisibility(View.GONE);
        dptlist = new ArrayList<>();
        dptlist.add("Department");
        dptlist.add("CSE");
        dptlist.add("ISE");
        dptlist.add("ECE");
        dptlist.add("MEE");
        dptlist.add("CHE");
        dptlist.add("MCA");
        desgns = new ArrayList<>();
        desgns.add("Designation");
        desgns.add("Professor");
        desgns.add("Asst.Professor");
        desgns.add("Assoc.Professor");
        desgns.add("HOD");
        //desgns.add("Dean");
        desgns.add("Vice Principal");
        //desgns.add("Principal");
        desgns.add("Other");
        setDesgAdp();
        rb1.setChecked(true);
        ArrayAdapter<String> adp = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, dptlist);
        deptment.setAdapter(adp);
        Intent i = getIntent();
        v = i.getStringExtra("RegorEdit");
        String role = "";
        if (v.equals("1") || v.equals("2")) {
            dp.setVisibility(View.GONE);
            o_name = i.getStringExtra("Name");
            nm.setText(o_name);
            o_fid = i.getStringExtra("Faculty_Id");
            facid.setText(o_fid);
            o_desg = i.getStringExtra("Designation");
            o_dept = i.getIntExtra("Dept", 0);
            deptment.setSelection(o_dept);
            mob.setText(i.getStringExtra("Mobile"));
            o_uname = i.getStringExtra("Username");
            unme.setText(o_uname);
            adrss.setText(i.getStringExtra("Address"));
            mail.setText(i.getStringExtra("Email"));
            cl.setText(i.getStringExtra("CL"));
            rh.setText(i.getStringExtra("RH"));
            el.setText(i.getStringExtra("EL"));
            if (v.equals("1")) {
                SharedPreferences sp = getSharedPreferences("Profile", MODE_PRIVATE);
                role = sp.getString("Role", "");
                nm.setTextColor(getResources().getColor(R.color.input_register_hint));
                facid.setTextColor(getResources().getColor(R.color.input_register_hint));
                unme.setTextColor(getResources().getColor(R.color.input_register_hint));
                cl.setTextColor(getResources().getColor(R.color.input_register_hint));
                rh.setTextColor(getResources().getColor(R.color.input_register_hint));
                el.setTextColor(getResources().getColor(R.color.input_register_hint));
                nm.setEnabled(false);
                unme.setEnabled(false);
                facid.setEnabled(false);
                desig.setEnabled(false);
                deptment.setEnabled(false);
                cl.setEnabled(false);
                rh.setEnabled(false);
                el.setEnabled(false);
            } else {
                role = i.getStringExtra("Roles");
                mob.setTextColor(getResources().getColor(R.color.input_register_hint));
                mail.setTextColor(getResources().getColor(R.color.input_register_hint));
                adrss.setTextColor(getResources().getColor(R.color.input_register_hint));
                onum.setTextColor(getResources().getColor(R.color.input_register_hint));
                mob.setEnabled(false);
                onum.setEnabled(false);
                mail.setEnabled(false);
                adrss.setEnabled(false);
            }
            if (role.equals("NT")) {
                onum.setVisibility(View.GONE);
                desgns = new ArrayList<>();
                desgns.add("Designation");
                desgns.add("Clerk");
                desgns.add("Office Assistant");
                desgns.add("Lab Instructor");
                desgns.add("Other");
                setDesgAdp();
            } else
                onum.setText(i.getStringExtra("Office"));
            desig.setSelection(desgns.indexOf(o_desg));
            pswd.setVisibility(View.GONE);
            cpswd.setVisibility(View.GONE);
            rb1.setVisibility(View.GONE);
            rb2.setVisibility(View.GONE);
            reg.setText("Save Changes");
            bk.setText("Discard Changes");
        }
        desig.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int i, long l) {
                dsgi = i;
                if (i == 0 || v.equals("1"))
                    ((TextView) parent.getChildAt(0)).setTextColor(getResources().getColor(R.color.input_register_hint));
                else
                    ((TextView) parent.getChildAt(0)).setTextColor(Color.WHITE);
                ((TextView) parent.getChildAt(0)).setTextSize(18);
                ((TextView) parent.getChildAt(0)).setPadding(20, 20, 20, 20);
                ((TextView) parent.getChildAt(0)).setGravity(Gravity.CENTER_VERTICAL);
                if (desgns.get(i).equals("Other"))
                    odesg.setVisibility(View.VISIBLE);
                else
                    odesg.setVisibility(View.GONE);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });
        dp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);
            }
        });
        deptment.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int i, long l) {
                index = i;
                if (index == 0 || v.equals("1"))
                    ((TextView) parent.getChildAt(0)).setTextColor(getResources().getColor(R.color.input_register_hint));
                else
                    ((TextView) parent.getChildAt(0)).setTextColor(Color.WHITE);
                ((TextView) parent.getChildAt(0)).setTextSize(18);
                ((TextView) parent.getChildAt(0)).setPadding(20, 20, 20, 20);
                ((TextView) parent.getChildAt(0)).setGravity(Gravity.CENTER_VERTICAL);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
        rb2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                desgns = new ArrayList<>();
                desgns.add("Designation");
                desgns.add("Clerk");
                desgns.add("Office Assistant");
                desgns.add("Lab Instructor");
                desgns.add("Other");
                setDesgAdp();
                rb1.setChecked(false);
                onum.setVisibility(View.GONE);
            }
        });
        rb1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                desgns = new ArrayList<>();
                desgns.add("Designation");
                desgns.add("Professor");
                desgns.add("Asst.Professor");
                desgns.add("Assoc.Professor");
                desgns.add("HOD");
                /*desgns.add("Dean");
                desgns.add("Vice Principal");
                desgns.add("Principal");*/
                desgns.add("Other");
                setDesgAdp();
                rb2.setChecked(false);
                onum.setVisibility(View.VISIBLE);
            }
        });
        bk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
        reg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int flg = 0;
                String phone = mob.getText().toString().trim();
                String ono = onum.getText().toString().trim();
                String adr = adrss.getText().toString().trim();
                String eml = mail.getText().toString().trim();
                String name = nm.getText().toString().trim();
                String desg = desgns.get(dsgi);
                String un = unme.getText().toString().trim();
                fid = facid.getText().toString().trim();
                String ncl = cl.getText().toString().trim();
                String nrh = rh.getText().toString().trim();
                String nel = el.getText().toString().trim();
                if (dsgi == 0 || index == 0) flg = 1;
                if (odesg.getVisibility() == View.VISIBLE) {
                    desg = odesg.getText().toString().trim();
                    if (desg.isEmpty()) flg = 1;
                }
                if (phone.length() < 10) {
                    mob.setError("Mobile Number Should have minimum 10 digits");
                    return;
                }
                if (!eml.contains("@")) {
                    mail.setError("Invalid Email ID");
                    return;
                }
                if (onum.getVisibility() == View.VISIBLE) {
                    ono = onum.getText().toString().trim();
                    if (ono.isEmpty()) flg = 1;
                    if (onum.length() < 10) {
                        onum.setError("Office Number Should have minimum 10 digits");
                        return;
                    }
                }
                if (!name.isEmpty() && !eml.isEmpty() && !desg.isEmpty()
                        && !phone.isEmpty() && !un.isEmpty() && !fid.isEmpty() && flg != 1 && !ncl.isEmpty()
                        && !nrh.isEmpty() && !nel.isEmpty()) {
                    if (v.equals("0") || v.equals("2")) {
                        int c, r, e;
                        c = Integer.parseInt(ncl);
                        r = Integer.parseInt(nrh);
                        e = Integer.parseInt(nel);
                        int fg = 0;
                        if (c < 0 || c > 15) {
                            cl.setError("Invalid: CL should be between 0 and 15");
                            fg = 1;
                        }
                        if (r < 0 || r > 2) {
                            rh.setError("Invalid: RH should be between 0 and 2");
                            fg = 1;
                        }
                        if (e < 0) {
                            el.setError("Invalid: EL cannot be negative");
                            fg = 1;
                        }
                        String pwd = "";
                        if (v.equals("0")) {
                            valid = new int[4];
                            valid[0] = valid[1] = valid[2] = valid[3] = 1;
                            pwd = pswd.getText().toString().trim();
                            String cpwd = cpswd.getText().toString().trim();
                            if (!pwd.isEmpty() && !cpwd.isEmpty()) {
                                if (pwd.equals(cpwd)) {
                                    int a[];
                                    a = new int[4];
                                    Byte b;
                                    for (char x : pwd.toCharArray()) {
                                        if (Character.isUpperCase(x)) a[0]++;
                                        else if (Character.isLowerCase(x)) a[1]++;
                                        else if (Character.isDigit(x)) a[2]++;
                                        else a[3]++;
                                    }
                                    if (!(a[0] > 0 && a[1] > 0 && a[2] > 0 && a[3] > 0)) {
                                        fg = 1;
                                        pswd.setError("Password must contain atleast 1 uppercase letter,1 lowercase letter,1 digit and 1 special character");
                                    }
                                } else {
                                    cpswd.setError("Passwords must match");
                                    fg = 1;
                                }
                            } else {
                                Toast.makeText(getApplicationContext(),
                                        "Please enter all fields", Toast.LENGTH_LONG)
                                        .show();
                                fg = 1;
                            }
                        }
                        if (v.equals("2")) {
                            valid = new int[4];
                            if (!name.equals(o_name)) valid[0] = 1;
                            else valid[0] = 0;
                            if (!un.equals(o_uname)) valid[1] = 1;
                            else valid[1] = 0;
                            if (!fid.equals(o_fid)) valid[2] = 1;
                            else valid[2] = 0;
                            if (!desg.equals(o_desg) && o_dept != index) valid[3] = 1;
                            else valid[3] = 0;
                        }
                        if (fg == 0) {
                            String role;
                            if (desg.equals("Professor") || desg.equals("Asst.Professor") || desg.equals("Assoc.Professor"))
                                role = "P";
                            else if (desg.equals("HOD"))
                                role = "HOD";
                            else if (desg.equals("Vice Principal") || desg.equals("Principal"))
                                role = "A";
                            else role = "NT";
                            String[] data = {name, un, fid, desg, dptlist.get(index), phone, ono, eml, adr, ncl, nrh, nel, role, pwd};
                            new Register().execute(data);
                        }
                    } else if (v.equals("1")) {
                        String[] info = {name, un, fid, desg, dptlist.get(index), phone, ono, eml, adr, ncl, nrh, nel};
                        new Register().execute(info);
                    }
                } else {
                    Toast.makeText(getApplicationContext(),
                            "Please enter all fields", Toast.LENGTH_LONG)
                            .show();
                }
            }
        });
    }

    private class Register extends AsyncTask<String, Void, String> {
        private ProgressDialog pd = new ProgressDialog(Registration.this);
        String desg = "";

        @Override
        protected void onPreExecute() {
            pd.setMessage("Validating...");
            pd.setCancelable(false);
            pd.show();
        }

        @Override
        protected String doInBackground(String... data) {
            desg = data[3];
            HashMap<String, String> d = new HashMap<String, String>();
            String link = "";
            d.put("Fid", data[2]);
            if (v.equals("0") || v.equals("1")) {
                d.put("Mob", data[5]);
                d.put("Office", data[6]);
                d.put("Email", data[7]);
                d.put("Adr", data[8]);
            }
            if (v.equals("0") || v.equals("2")) {
                d.put("Name", data[0]);
                d.put("Uname", data[1]);
                d.put("Desg", data[3]);
                d.put("Dept", data[4]);
                d.put("CL", data[9]);
                d.put("RH", data[10]);
                d.put("EL", data[11]);
                d.put("Role", data[12]);
                d.put("CN", valid[0] + "");
                d.put("CF", valid[1] + "");
                d.put("CU", valid[2] + "");
                d.put("CD", valid[3] + "");
            }
            if (v.equals("0")) {
                d.put("Pswd", data[13]);
                d.put("I", "2");
                link = "register.php";
            }
            if (v.equals("1"))
                link = "EditProfile.php";
            if (v.equals("2")) {
                d.put("I", "1");
                link = "register.php";
            }
            return rhndle.sendPostRequest(link, d);
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
                    if (v.equals("1")) {
                        if (s.equals("500")) {
                            Toast.makeText(getBaseContext(), "Updated Successfully", Toast.LENGTH_SHORT).show();
                            SharedPreferences sp = getSharedPreferences("Profile", MODE_PRIVATE);
                            SharedPreferences.Editor e = sp.edit();
                            e.putString("Mob", mob.getText().toString().trim());
                            e.putString("Email", mail.getText().toString().trim());
                            e.putString("Office", onum.getText().toString().trim());
                            e.putString("Address", adrss.getText().toString().trim());
                            e.apply();
                            onBackPressed();
                        } else
                            Toast.makeText(getBaseContext(), "Encountered an Error", Toast.LENGTH_SHORT).show();
                    } else
                        decide(s, desg);
            }
            pd.dismiss();
        }
    }

    public void decide(String jsnstr, String desg) {
        Log.e("TAG", jsnstr);
        try {
            JSONObject jo = new JSONObject(jsnstr);
            JSONArray ja = jo.getJSONArray("Results");
            if (ja.length() > 0) {
                String temp;
                JSONObject r = ja.getJSONObject(0);
                boolean flag = false;
                if ((temp = r.getString("Name")).equals("1")) {
                    nm.setError("Name already exists");
                    flag = true;
                }
                if ((temp = r.getString("Fid")).equals("1")) {
                    facid.setError("Faculty Id already exists");
                    flag = true;
                }
                if ((temp = r.getString("Uname")).equals("1")) {
                    unme.setError("Username already exists");
                    flag = true;
                }
                if ((temp = r.getString("Desg")).equals("1")) {
                    Toast.makeText(getBaseContext(), "There should be a single person for this designation", Toast.LENGTH_SHORT).show();
                    flag = true;
                }
                if (!flag) {
                    if (v.equals("0")) {
                        Toast.makeText(getBaseContext(), "Registered Successfully", Toast.LENGTH_SHORT).show();
                        if (dpchanged == 1) {
                            reg.setVisibility(View.GONE);
                            rhndle.uploadImage(MenuList.UPLOAD_URL, bmp, fid);
                        }  onBackPressed();
                    } else if (v.equals("2")) {
                        Toast.makeText(getBaseContext(), "Updated Successfully", Toast.LENGTH_SHORT).show();
                        Intent i = new Intent();
                        i.putExtra("Name", nm.getText().toString());
                        i.putExtra("Uname", unme.getText().toString());
                        i.putExtra("Fid", facid.getText().toString());
                        i.putExtra("Desg", desg);
                        i.putExtra("Dept", dptlist.get(index));
                        i.putExtra("CL", cl.getText().toString());
                        i.putExtra("RH", rh.getText().toString());
                        i.putExtra("EL", el.getText().toString());
                        setResult(RESULT_OK, i);
                        finish();
                    }
                }
            } else {
                if (v.equals("0")) {
                    Toast.makeText(getBaseContext(), "Registered Successfully", Toast.LENGTH_SHORT).show();
                    if (dpchanged == 1)
                        rhndle.uploadImage(MenuList.UPLOAD_URL, bmp, fid);
                    onBackPressed();
                } else if (v.equals("2")) {
                    Toast.makeText(getBaseContext(), "Updated Successfully", Toast.LENGTH_SHORT).show();
                    Intent i = new Intent();
                    i.putExtra("Name", nm.getText().toString());
                    i.putExtra("Uname", unme.getText().toString());
                    i.putExtra("Fid", facid.getText().toString());
                    i.putExtra("Desg", desg);
                    i.putExtra("Dept", dptlist.get(index));
                    i.putExtra("CL", cl.getText().toString());
                    i.putExtra("RH", rh.getText().toString());
                    i.putExtra("EL", el.getText().toString());
                    setResult(RESULT_OK, i);
                    finish();
                }
            }
        } catch (JSONException e) {
            Toast.makeText(getBaseContext(), "Error Encountered in JSON object", Toast.LENGTH_SHORT).show();
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {

            Uri filePath = data.getData();
            try {
                Bitmap temp = MediaStore.Images.Media.getBitmap(getContentResolver(), filePath);
                dp.setImageBitmap(temp);
                bmp = Bitmap.createScaledBitmap(temp, 180, 180, false);
                dpchanged = 1;
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }

    private void setDesgAdp() {
        ArrayAdapter<String> adp = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, desgns);
        desig.setAdapter(adp);
    }
}
