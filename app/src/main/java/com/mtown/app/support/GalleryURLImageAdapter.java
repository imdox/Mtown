package com.mtown.app.support;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Gallery;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.mtown.app.R;
import com.mtown.app.admin.MyListViewAdapter;
import com.mtown.app.user.AddEditUserActivity;

import java.util.ArrayList;

public class GalleryURLImageAdapter extends BaseAdapter {
    private Context mContext;
    private String[] mArrayUrl;
    private LayoutInflater inflater;

    public GalleryURLImageAdapter(Context context, String[] mArrayUrl) {
        this.mContext = context;
        this.mArrayUrl = mArrayUrl;
        this.inflater = LayoutInflater.from(context);
    }

    public int getCount() {
        return mArrayUrl.length;
    }

    public Object getItem(int position) {
        return position;
    }

    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemViewType(int position) {
        return super.getItemViewType(position);
    }

    private class ViewHolder {
        ImageView myImg;
    }

    // Override this method according to your need
    public View getView(int index, View view, ViewGroup viewGroup) {
        // TODO Auto-generated method stub

        final ViewHolder holder;
        if (view == null) {
            holder = new ViewHolder();
            view = inflater.inflate(R.layout.image_view, null);
            holder.myImg = (ImageView) view.findViewById(R.id.myImage);
            view.setTag(holder);
        } else {
            holder = (ViewHolder) view.getTag();
        }

        Glide.with(mContext).load(mArrayUrl[index].toString().trim())
                .into(holder.myImg);
        return view;
    }

}