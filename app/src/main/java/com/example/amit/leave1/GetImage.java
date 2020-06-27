package com.example.amit.leave1;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.BitmapDrawable;
import android.os.AsyncTask;
import android.util.Log;
import android.util.SparseArray;
import android.util.SparseBooleanArray;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;

import java.io.IOException;
import java.lang.ref.WeakReference;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by Amit on 30-10-2015.
 */
public class GetImage extends AsyncTask<String, Void, Bitmap> {
    private final WeakReference<ImageView> ivref;
    private final WeakReference<ProgressBar> pbref;
    private ProgressBar pbar;
    private SparseArray<Bitmap> imgl;
    private int pos;
    private Context context;

    public GetImage(Context context, ImageView img, ProgressBar pb, SparseArray<Bitmap> imgl, int pos) {
        ivref = new WeakReference<ImageView>(img);
        pbref = new WeakReference<ProgressBar>(pb);
        this.imgl = imgl;
        this.pos = pos;
        this.context = context;
        pbar = pbref.get();
    }

    public GetImage(ImageView img, ProgressBar pb) {
        ivref = new WeakReference<ImageView>(img);
        pbref = new WeakReference<ProgressBar>(pb);
        pos = -1;
        pbar = pbref.get();
    }

    @Override
    protected void onPreExecute() {
        pbar.setVisibility(View.VISIBLE);
    }

    @Override
    protected void onPostExecute(Bitmap b) {
        RequestHandler rh = new RequestHandler(context);
        if (isCancelled())
            b = null;
        if (ivref != null) {
            ImageView img = ivref.get();
            if (b != null) {
                b = rh.getCircleBitmap(b);
                img.setImageBitmap(b);
                if (pos != -1)
                    imgl.put(pos, b);
            } else {
                img.setImageResource(R.drawable.profile27);
            }
        }
        pbar.setVisibility(View.GONE);

    }

    @Override
    protected Bitmap doInBackground(String... params) {
        String id = params[0];
        Bitmap image = null;
        try {
            URL url = new URL("http://www.lmsinbmsce.890m.com/imgdownload.php?Faculty_Id=" + id);
            image = BitmapFactory.decodeStream(url.openConnection().getInputStream());
        } catch (MalformedURLException e) {
            Log.e("TAG", "MalformedURLException");
        } catch (IOException e) {
            Log.e("TAG", "IOException");
        }
        return image;
    }

}
