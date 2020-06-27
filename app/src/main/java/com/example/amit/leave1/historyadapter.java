package com.example.amit.leave1;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatTextView;
import android.text.Html;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by Amit on 25-09-2015.
 */
public class historyadapter extends ArrayAdapter<String> {
    private final Context context;
    private final ArrayList<String> detl, status;
    private SparseBooleanArray selected;

    public historyadapter(Activity context, ArrayList<String> status, ArrayList<String> detl) {
        super(context, R.layout.dsb, detl);
        this.context = context;
        this.status = status;
        this.detl = detl;
        selected = new SparseBooleanArray();
    }

    public View getView(int position, View v, ViewGroup parent) {
        LayoutInflater inflator = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View rowview = inflator.inflate(R.layout.ntfn, null, true);
        AppCompatTextView dl = (AppCompatTextView) rowview.findViewById(R.id.details);
        dl.setText(Html.fromHtml(detl.get(position)));
        if (status.get(position).equals("1"))
            dl.setCompoundDrawablesWithIntrinsicBounds(null, null, getContext().getResources().getDrawable(R.mipmap.accept), null);
        else if (status.get(position).equals("2"))
            dl.setCompoundDrawablesWithIntrinsicBounds(null, null, getContext().getResources().getDrawable(R.mipmap.reject), null);
        else
            dl.setCompoundDrawablesWithIntrinsicBounds(null, null, getContext().getResources().getDrawable(R.mipmap.pending), null);
        return rowview;
    }

    public void toggleSelection(int position) {
        if (!selected.get(position))
            selected.put(position, true);
        else
            selected.delete(position);
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
