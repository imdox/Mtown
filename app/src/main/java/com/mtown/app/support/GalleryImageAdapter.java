package com.mtown.app.support;

import android.content.Context;
import android.net.Uri;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Gallery;
import android.widget.ImageView;

import com.mtown.app.R;

import java.util.ArrayList;

public class GalleryImageAdapter extends BaseAdapter {
    private Context mContext;
    private ArrayList<Uri> mArrayUri;

    public GalleryImageAdapter(Context context, ArrayList<Uri> mArrayUri) {
        this.mContext = context;
        this.mArrayUri = mArrayUri;
    }

    public int getCount() {
        return mArrayUri.size();
    }

    public Object getItem(int position) {
        return position;
    }

    public long getItemId(int position) {
        return position;
    }

    // Override this method according to your need
    public View getView(int index, View view, ViewGroup viewGroup) {
        // TODO Auto-generated method stub
        ImageView i = new ImageView(mContext);

        i.setImageURI(mArrayUri.get(index));;
        i.setLayoutParams(new Gallery.LayoutParams(250, 200));
        i.setScaleType(ImageView.ScaleType.CENTER_CROP);
        return i;
    }

}