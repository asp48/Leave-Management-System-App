package com.example.amit.leave1;

import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.os.AsyncTask;
import android.util.Log;
import android.util.SparseArray;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.SurfaceHolder;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.io.IOException;
import java.lang.ref.WeakReference;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by Amit on 18-06-2015.
 */
public class DashboardAdapter extends ArrayAdapter<String> {
    private final Context context;
    private final ArrayList<String> nm, id, desg, dept, st;
    private final SparseBooleanArray spb;
    private final RequestHandler rh;
    private SparseArray<Bitmap> imgl;
    private SparseBooleanArray selected;

    public DashboardAdapter(Activity context, ArrayList<String> nm, ArrayList<String> id, ArrayList<String> desg, ArrayList<String> dept, ArrayList<String> st, SparseBooleanArray spb) {
        super(context, R.layout.dsb, nm);
        this.context = context;
        this.nm = nm;
        this.id = id;
        this.desg = desg;
        this.dept = dept;
        this.st = st;
        this.spb = spb;
        rh = new RequestHandler(context);
        selected = new SparseBooleanArray();
    }

    public DashboardAdapter(Activity context, ArrayList<String> nm, ArrayList<String> id, ArrayList<String> desg, ArrayList<String> dept, ArrayList<String> st, SparseBooleanArray spb, SparseArray<Bitmap> imgl) {
        super(context, R.layout.dsb, nm);
        this.context = context;
        this.nm = nm;
        this.id = id;
        this.desg = desg;
        this.dept = dept;
        this.st = st;
        this.spb = spb;
        this.imgl = imgl;
        rh = new RequestHandler(context);
        selected = new SparseBooleanArray();
    }

    public View getView(int position, View rowview, ViewGroup parent) {
        ViewHolder holder;
        LayoutInflater inflator = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        if (rowview == null) {
            rowview = inflator.inflate(R.layout.dsb, null, true);
            holder = new ViewHolder();
            holder.nme = (TextView) rowview.findViewById(R.id.name);
            holder.eid = (TextView) rowview.findViewById(R.id.empid);
            holder.dg = (TextView) rowview.findViewById(R.id.desgnation);
            holder.dpt = (TextView) rowview.findViewById(R.id.dept);
            holder.img = (ImageView) rowview.findViewById(R.id.icon);
            holder.status = (ImageView) rowview.findViewById(R.id.aprvstatus);
            holder.pb = (ProgressBar) rowview.findViewById(R.id.pbar);
            rowview.setTag(holder);
        } else {
            holder = (ViewHolder) rowview.getTag();
        }
        if (!spb.get(position))
            return new View(context);
        holder.pb.setVisibility(View.GONE);
        holder.nme.setText(nm.get(position));
        holder.eid.setText(id.get(position));
        holder.dg.setText(desg.get(position));
        holder.dpt.setText(dept.get(position));
        if (holder.img != null) {
            if (rh.isConnected(context)) {
                if (imgl.get(position) == null) {
                    imgl.put(position, BitmapFactory.decodeResource(context.getResources(), R.drawable.profile27));
                    new GetImage(context, holder.img, holder.pb, imgl, position).execute(id.get(position));
                } else
                    holder.img.setImageBitmap(imgl.get(position));
            }
        }
        if (st.get(position).equals("1")) {
            holder.status.setImageResource(R.mipmap.accept);
        } else if (st.get(position).equals("2"))
            holder.status.setImageResource(R.mipmap.reject);
        return rowview;
    }

    static class ViewHolder {
        TextView nme;
        TextView eid;
        TextView dg;
        TextView dpt;
        ImageView img;
        ImageView status;
        ProgressBar pb;
    }

    public void toggleSelection(int position) {
        if (!selected.get(position))
            selected.put(position, true);
        else
            selected.delete(position);
        Log.e("TAG", position + " changed to " + selected.get(position));
        notifyDataSetChanged();
    }

    public void removeSelection() {
        selected = new SparseBooleanArray();
        notifyDataSetChanged();
    }

    public SparseBooleanArray getSelectedIds() {
        return selected;
    }
}
