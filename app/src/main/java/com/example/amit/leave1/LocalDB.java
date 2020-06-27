package com.example.amit.leave1;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;

/**
 * Created by Amit on 09-01-2016.
 */
public class LocalDB extends SQLiteOpenHelper {
    public final static String DataBaseName = "History.db";
    public final static String TableName = "Approve";
    public final static String sid = "Super_Id";
    public final static String name = "Name";
    public final static String fid = "Faculty_Id";
    public final static String desg = "Designation";
    public final static String dept = "Department";
    public final static String lid = "LeaveID";
    public final static String aon = "Applied_On";
    public final static String type = "Type";
    public final static String fromD = "FromD";
    public final static String toD = "ToD";
    public final static String onD = "OnD";
    public final static String pp = "PR_POL";
    public final static String nd = "NoDays";
    public final static String rsn = "Reason";
    public final static String alt = "Alternate";
    public final static String fasst = "FAssist";
    public final static String amt = "FAmount";
    public final static String st = "Status";
    public final static String rjrsn = "RjReason";
    public final static String ByHOD = "ByHOD";
    public final static String ByVP = "ByVP";
    public final static String a_HOD = "Approve_HOD";
    public final static String a_VP = "Approve_VP";

    public LocalDB(Context context) {
        super(context, DataBaseName, null, 2);
    }


    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(
                "create table Approve(Super_Id text,Name text,Faculty_Id text,Designation text,Department text,LeaveID integer primary key," +
                        "Applied_On text NOT NULL DEFAULT '0000-00-00',Type text,FromD text NOT NULL DEFAULT '0000-00-00',ToD text NOT NULL DEFAULT '0000-00-00'," +
                        "OnD text NOT NULL DEFAULT '0000-00-00',PR_POL integer DEFAULT 0,NoDays text,Reason text,Alternate text,FAssist integer DEFAULT 0," +
                        "FAmount integer DEFAULT 0,Status integer DEFAULT 0,RjReason text,ByHOD integer DEFAULT 0,ByVP integer DEFAULT 0," +
                        "Approve_HOD text NOT NULL DEFAULT '0000-00-00',Approve_VP text NOT NULL DEFAULT '0000-00-00')"
        );
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        onCreate(db);
    }

    public boolean insert(String[] a) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(sid, a[0]);
        cv.put(name, a[1]);
        cv.put(fid, a[2]);
        cv.put(desg, a[3]);
        cv.put(dept, a[4]);
        cv.put(lid, Integer.parseInt(a[5]));
        cv.put(aon, a[6]);
        cv.put(type, a[7]);
        cv.put(fromD, a[8]);
        cv.put(toD, a[9]);
        cv.put(onD, a[10]);
        cv.put(pp, Integer.parseInt(a[11]));
        cv.put(nd, a[12]);
        cv.put(rsn, a[13]);
        cv.put(alt, a[14]);
        cv.put(fasst, Integer.parseInt(a[15]));
        cv.put(amt, Integer.parseInt(a[16]));
        cv.put(st, Integer.parseInt(a[17]));
        cv.put(rjrsn, a[18]);
        cv.put(ByHOD, Integer.parseInt(a[19]));
        cv.put(ByVP, Integer.parseInt(a[20]));
        cv.put(a_HOD, a[21]);
        cv.put(a_VP, a[22]);
        long i = db.insert(TableName, null, cv);
        if (i == -1)
            return false;
        else
            return true;
    }

    public Cursor getData(String id) {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery("select * from Approve where LeaveID=" + id + "", null);
    }

    public int numberOfRows() {
        SQLiteDatabase db = this.getReadableDatabase();
        return (int) DatabaseUtils.queryNumEntries(db, TableName);
    }

    public boolean update(String label, String value, String id, int x) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        if (x == 0)
            cv.put(label, value);
        else cv.put(label, Integer.parseInt(value));
        db.update(TableName, cv, "LeaveID = ? ", new String[]{id});
        return true;
    }

    public void deleteItem(String[] id) {
        SQLiteDatabase db = this.getWritableDatabase();
        for (String temp : id) {
            db.delete(TableName, "LeaveID = ? ", new String[]{temp});
        }
    }

    public ArrayList<String> getAllData(String query, String col_name) {
        ArrayList<String> array_list = new ArrayList<String>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res = db.rawQuery(query, null);
        res.moveToFirst();
        while (!res.isAfterLast()) {
            array_list.add(res.getString(res.getColumnIndex(col_name)));
            res.moveToNext();
        }
        res.close();
        return array_list;
    }

    public boolean checkid(String id) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res = db.rawQuery("select * from Approve where LeaveID=" + id + "", null);
        if (res.getCount() > 0)
            return true;
        else return false;
    }

    public boolean pending(String id) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res = db.rawQuery("select * from Approve where Status=0 and Faculty_Id='" + id + "'", null);
        if (res.getCount() > 0)
            return true;
        else return false;
    }
}
