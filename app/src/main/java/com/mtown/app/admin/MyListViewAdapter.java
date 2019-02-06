package com.mtown.app.admin;


import android.content.Context;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.mtown.app.R;
import com.mtown.app.dao.ModelDAO;
import com.mtown.app.support.AppController;

import java.util.ArrayList;
import java.util.List;


public class MyListViewAdapter extends ArrayAdapter<ModelDAO> {
    private Context myContext;
    private LayoutInflater inflater;
    private ArrayList<ModelDAO> modelDAOS;
    private SparseBooleanArray mSelectedItemsIds;

    // Constructor for get Context and  list

    public MyListViewAdapter(Context context, int resourceId, ArrayList<ModelDAO> modelDAOS) {
        super(context, resourceId, modelDAOS);
        this.mSelectedItemsIds = new SparseBooleanArray();
        this.myContext = context;
        this.modelDAOS = modelDAOS;
        this.inflater = LayoutInflater.from(context);
    }


    // Container Class for item

    private class ViewHolder {
        TextView tvTitle,txtDesignation,txtExp;
        ImageView myImg;

    }

    public View getView(int position, View view, ViewGroup parent) {
        final ViewHolder holder;
        if (view == null) {
            holder = new ViewHolder();
            view = inflater.inflate(R.layout.list_item, null);
            holder.tvTitle = (TextView) view.findViewById(R.id.textview);
            holder.myImg = (ImageView) view.findViewById(R.id.myImage);
            holder.txtDesignation = (TextView) view.findViewById(R.id.txtDesignation);
            holder.txtExp = (TextView) view.findViewById(R.id.txtExp);
            holder.tvTitle.setTypeface(AppController.getDefaultFont(myContext));
            holder.txtDesignation.setTypeface(AppController.getDefaultFont(myContext));
            holder.txtExp.setTypeface(AppController.getDefaultFont(myContext));
                                    view.setTag(holder);
        } else {
            holder = (ViewHolder) view.getTag();
        }

        // Capture position and set to the  TextViews
        holder.tvTitle.setText("Name : "+modelDAOS.get(position).getFirstname()+" "+modelDAOS.get(position).getLastname());
        holder.txtDesignation.setText("Designation : "+modelDAOS.get(position).getDesignation());
        holder.txtExp.setText("Experience : "+modelDAOS.get(position).getExperience()+" years");
        // Capture position and set to the  ImageView
        Glide.with(myContext).load(modelDAOS.get(position).getProfile_image().toString().trim())
                .into(holder.myImg);
        return view;
    }

    @Override
    public void remove(ModelDAO object) {
        modelDAOS.remove(object);
        notifyDataSetChanged();
    }


    // get List after update or delete

    public List<ModelDAO> getMyList() {
     return modelDAOS;
    }


    public void toggleSelection(int position) {
        selectView(position, !mSelectedItemsIds.get(position));
    }


    // Remove selection after unchecked

    public void removeSelection() {
        mSelectedItemsIds = new SparseBooleanArray();
        notifyDataSetChanged();
    }


    // Item checked on selection

    public void selectView(int position, boolean value) {
        if (value)
            mSelectedItemsIds.put(position, value);
        else
            mSelectedItemsIds.delete(position);
        notifyDataSetChanged();
    }

    // Get number of selected item
    public int getSelectedCount() {
        return mSelectedItemsIds.size();
    }


    public SparseBooleanArray getSelectedIds() {
        return mSelectedItemsIds;
    }
}
