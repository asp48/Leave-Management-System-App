package com.example.amit.leave1;

import android.app.IntentService;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by Amit on 01-01-2016.
 */
public class NotificationReceiver extends IntentService {
    NotificationCompat.Builder ntfn;
    NotificationManager manager;
    TaskStackBuilder stackBuilder;
    PendingIntent pi;
    Intent x;
    SharedPreferences sp;
    volatile boolean running;
    ArrayList<String> lid, type, from, to, nd, rsn, alt, st, rjrsn, ond, pp, fa, amt, aon, stH, stVP, aH, aVP;

    public NotificationReceiver() {
        super("NotificationReceiver");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        sp = getSharedPreferences("Profile", MODE_PRIVATE);
        String fid = intent.getStringExtra("Faculty_Id");
        HashMap<String, String> data = new HashMap<String, String>();
        data.put("Fid", fid);
        manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        running = true;
        while (running) {
            RequestHandler rh = new RequestHandler(this);
            String msg = rh.sendPostRequest("getnotification.php", data);
            Log.e("TAG", msg);
            int n = loadnotification(msg);
            Log.e("TAG", String.valueOf(n));
            if (running && n > 0) {
                if (sp.getString("Role", "").equals("P") || sp.getString("Role", "").equals("NT"))
                    x = new Intent(NotificationReceiver.this, MenuList.class);
                else if (sp.getString("Role", "").equals("HOD") || sp.getString("Role", "").equals("VP"))
                    x = new Intent(NotificationReceiver.this, AdminMenuList.class);
                x.putExtra("View", "1");
                x.putStringArrayListExtra("Type", type);
                x.putStringArrayListExtra("From", from);
                x.putStringArrayListExtra("To", to);
                x.putStringArrayListExtra("NoDays", nd);
                x.putStringArrayListExtra("Reason", rsn);
                x.putStringArrayListExtra("Alternate", alt);
                x.putStringArrayListExtra("Status", st);
                x.putStringArrayListExtra("RjReason", rjrsn);
                x.putStringArrayListExtra("LeaveId", lid);
                x.putStringArrayListExtra("OnD", ond);
                x.putStringArrayListExtra("PP", pp);
                x.putStringArrayListExtra("FAssist", fa);
                x.putStringArrayListExtra("FAmount", amt);
                x.putStringArrayListExtra("ByHOD", stH);
                x.putStringArrayListExtra("ByVP", stVP);
                x.putStringArrayListExtra("A_HOD", aH);
                x.putStringArrayListExtra("A_VP", aVP);
                x.putStringArrayListExtra("A_On", aon);
                x.putExtra("Faculty_Id", fid);
                if (sp.getString("Activity", "2").equals("0") || sp.getString("Activity", "2").equals("1")) {
                    Log.e("TAG", "inside ntfn receive + activity check");
                    x.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(x);
                } else {
                    ntfn = new NotificationCompat.Builder(NotificationReceiver.this);
                    ntfn.setContentTitle("You have " + n + " New Notifications");
                    String content = "";
                    for (int j = 0; j < n; j++) {
                        content += "Your Leave ";
                        if (!(ond.get(j)).equals("0000-00-00")) {
                            content += "On " + ond.get(j);
                            if (st.get(j).equals("1"))
                                content += " got Accepted\n";
                            else
                                content += " got Rejected\n";
                        } else {
                            content += "From " + from.get(j) + " To " + to.get(j);
                            if (st.get(j).equals("1"))
                                content += " got Accepted\n";
                            else
                                content += " got Rejected\n";
                        }
                    }
                    Log.e("TAG", content);
                    ntfn.setContentText(content);
                    ntfn.setAutoCancel(true);
                    ntfn.setSmallIcon(R.mipmap.ic_launcher);
                    ntfn.setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION));
                    x.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                    pi = PendingIntent.getActivity(this, 0, x, PendingIntent.FLAG_CANCEL_CURRENT);
                    ntfn.setContentIntent(pi);
                    ntfn.setStyle(new NotificationCompat.BigTextStyle().bigText(content));
                    manager.notify((int) (System.currentTimeMillis()), ntfn.build());
                }
            }
            try {
                Thread.sleep(90000);
            } catch (InterruptedException e) {
            }
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        running = false;
        stopSelf();
        manager.cancelAll();
    }

    private int loadnotification(String jsnstr) {
        type = new ArrayList<String>();
        from = new ArrayList<String>();
        to = new ArrayList<String>();
        nd = new ArrayList<String>();
        rsn = new ArrayList<String>();
        alt = new ArrayList<String>();
        st = new ArrayList<String>();
        rjrsn = new ArrayList<String>();
        lid = new ArrayList<String>();
        ond = new ArrayList<String>();
        pp = new ArrayList<String>();
        fa = new ArrayList<String>();
        amt = new ArrayList<String>();
        aon = new ArrayList<String>();
        stH = new ArrayList<String>();
        stVP = new ArrayList<String>();
        aH = new ArrayList<String>();
        aVP = new ArrayList<String>();
        try {
            JSONObject jo = new JSONObject(jsnstr);
            JSONArray ja = jo.getJSONArray("Results");
            if (ja.length() > 0) {
                for (int i = 0; i < ja.length(); i++) {
                    JSONObject c = ja.getJSONObject(i);
                    type.add(c.getString("Type"));
                    from.add(c.getString("FromD"));
                    to.add(c.getString("ToD"));
                    nd.add(c.getString("Nd"));
                    rsn.add(c.getString("Rsn"));
                    alt.add(c.getString("Alt"));
                    st.add(c.getString("St"));
                    rjrsn.add(c.getString("Rjrsn"));
                    lid.add(c.getString("Lid"));
                    aon.add(c.getString("A_On"));
                    ond.add(c.getString("OnD"));
                    pp.add(c.getString("PP"));
                    fa.add(c.getString("FAsst"));
                    amt.add(c.getString("Famt"));
                    stH.add(c.getString("stHOD"));
                    stVP.add(c.getString("stVP"));
                    aH.add(c.getString("A_HOD"));
                    aVP.add(c.getString("A_VP"));
                    MoveToHistory(i);
                }
                Log.e("TAG", String.valueOf(ja.length()));
                return ja.length();
            } else
                return 0;
        } catch (JSONException e) {
            Log.e("TAG", "error in json");
            return 0;
        }
    }

    private void MoveToHistory(int index) {
        LocalDB db = new LocalDB(NotificationReceiver.this);
        if (db.checkid(lid.get(index))) {
            Log.e("TAG", rjrsn.get(index) + stH.get(index) + stVP.get(index) + aVP.get(index));
            db.update(db.ByHOD, stH.get(index), lid.get(index), 1);
            db.update(db.ByVP, stVP.get(index), lid.get(index), 1);
            db.update(db.a_HOD, aH.get(index), lid.get(index), 0);
            db.update(db.a_VP, aVP.get(index), lid.get(index), 0);
            db.update(db.rjrsn, rjrsn.get(index), lid.get(index), 0);
            Log.e("TAG", "updated history");
        } else {
            String[] v = new String[23];
            v[0] = sp.getString("Super_Id", "");
            v[1] = sp.getString("Name", "");
            v[2] = sp.getString("Faculty_Id", "");
            v[3] = sp.getString("Desg", "");
            v[4] = sp.getString("Dept", "");
            v[5] = lid.get(index);
            v[6] = aon.get(index);
            v[7] = type.get(index);
            v[8] = from.get(index);
            v[9] = to.get(index);
            v[10] = ond.get(index);
            v[11] = pp.get(index);
            v[12] = nd.get(index);
            v[13] = rsn.get(index);
            v[14] = alt.get(index);
            v[15] = fa.get(index);
            v[16] = amt.get(index);
            v[17] = st.get(index);
            v[18] = rjrsn.get(index);
            v[19] = stH.get(index);
            v[20] = stVP.get(index);
            v[21] = aH.get(index);
            v[22] = aVP.get(index);
            Log.e("TAG", "Inside move to history");
            if (db.insert(v))
                Log.e("TAG", "moved to history");
        }
    }
}
