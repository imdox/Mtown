package com.mtown.app.user;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkError;
import com.android.volley.NoConnectionError;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.ServerError;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.bumptech.glide.Glide;
import com.mtown.app.R;
import com.mtown.app.auth.AuthActivity;
import com.mtown.app.dao.ModelDAO;
import com.mtown.app.dao.SearchDAO;
import com.mtown.app.home.MainActivity;
import com.mtown.app.support.AppController;


import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;



public class ModelListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private List<Object> dataList = new ArrayList();
    private Context context;
    String strStatus = "0";

    // Constructor
    public ModelListAdapter(Context context){
        this.context=context;
    }

    public void setAdapterData(List<Object> adapterData){
        this.dataList = adapterData;
    }

    // We need to override this as we need to differentiate
    // which type viewHolder to be attached
    // This is being called from onBindViewHolder() method
    @Override
    public int getItemViewType(int position) {
        if (dataList.get(position) instanceof ModelDAO) {
            return AppController.TYPE_MODEL;
        }else if (dataList.get(position) instanceof SearchDAO) {
            return AppController.TYPE_MODEL_SEARCH;
        }
        return -1;
    }

    // Invoked by layout manager to replace the contents of the views
    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, final int position) {
        int viewType=holder.getItemViewType();
        switch (viewType){
            case AppController.TYPE_MODEL:
                ModelDAO modelDAO=(ModelDAO) dataList.get(position);
                ((DetailsViewHolder)holder).showModelDetails(modelDAO,position);
                break;
            case AppController.TYPE_MODEL_SEARCH:
                SearchDAO modelSearchDAO=(SearchDAO) dataList.get(position);
                ((SearchDetailsViewHolder)holder).SearchShowModelDetails(modelSearchDAO,position);
                break;
            default:
                break;
        }
    }

    @Override
    public int getItemCount(){return dataList.size();}

    // Invoked by layout manager to create new views
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        int layout = 0;
        RecyclerView.ViewHolder viewHolder;
        // Identify viewType returned by getItemViewType(...)
        // and return ViewHolder Accordingly
        switch (viewType){
            case AppController.TYPE_MODEL:
                layout= R.layout.model_item;
                View modelView = LayoutInflater.from(parent.getContext()).inflate(layout, parent, false);
                viewHolder=new DetailsViewHolder(modelView);
                break;
            case AppController.TYPE_MODEL_SEARCH:
                layout= R.layout.search_model_item;
                View modelSearchView = LayoutInflater.from(parent.getContext()).inflate(layout, parent, false);
                viewHolder = new SearchDetailsViewHolder(modelSearchView);
                break;

            default:
                viewHolder=null;
                break;
        }
        return viewHolder;
    }


    // ViewHolder of object type details
    public class DetailsViewHolder extends RecyclerView.ViewHolder {

        private TextView txtModelId;
        private ImageView imgModel,imgStatus;

        public DetailsViewHolder(View itemView) {
            super(itemView);
            // Initiate view
            txtModelId =(TextView)itemView.findViewById(R.id.txtModelId);
            imgModel = (ImageView) itemView.findViewById(R.id.imgModel);
            imgStatus = (ImageView) itemView.findViewById(R.id.imgStatus);

            txtModelId.setTypeface(AppController.getDefaultBoldFont(context));
        }

        public void showModelDetails(final ModelDAO modelDAO, final int position){
            // Attach values for each item
            if(modelDAO.getStatus().equals("1")){
                imgStatus.setImageResource(R.drawable.ic_check_circle_green_24dp);
            }else {
                imgStatus.setImageResource(R.drawable.ic_check_circle_black_24dp);
            }
            txtModelId.setText(modelDAO.getModel_code());
            Glide.with(context).load(modelDAO.getProfile_image().toString().trim())
                    .into(imgModel);
            imgModel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(context,ProfileActivity.class);
                    intent.putExtra(context.getString(R.string.tagIndex), position);
                    context.startActivity(intent);
                }
            });

            imgStatus.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(AppController.getSharedPref(context).getString(context.getString(R.string.tagGroupType), "").equals("admin")){
                        String strMsg = "";
                        if(modelDAO.getStatus().equals("1")){
                            strStatus = "0";
                            strMsg = "Are you sure, \nYou want to change status invalidate.";
                        }else {
                            strStatus = "1";
                            strMsg = "Are you sure, \nYou want to change status validate.";
                        }

                        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);
                        alertDialogBuilder.setMessage(strMsg);
                        alertDialogBuilder.setPositiveButton("yes",
                                new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface arg0, int arg1) {
                                        changeStatus(modelDAO.getId(),strStatus,position);
                                        arg0.dismiss();
                                    }
                                });

                        alertDialogBuilder.setNegativeButton("No",new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        });

                        AlertDialog alertDialog = alertDialogBuilder.create();
                        alertDialog.show();
                    }
                }
            });

        }

    }

    // ViewHolder of object type details
    public class SearchDetailsViewHolder extends RecyclerView.ViewHolder {

        private TextView txtModelId,txtModelName,txtModelDes,txtModelExp;
        private ImageView imgModel,imgStatus;

        public SearchDetailsViewHolder(View itemView) {
            super(itemView);
            // Initiate view
            txtModelId =(TextView)itemView.findViewById(R.id.txtModelId);
            imgModel = (ImageView) itemView.findViewById(R.id.imgModel);
            txtModelName =(TextView)itemView.findViewById(R.id.txtModelName);
            txtModelDes =(TextView)itemView.findViewById(R.id.txtModelDes);
            txtModelExp =(TextView)itemView.findViewById(R.id.txtModelExp);
            imgStatus = (ImageView) itemView.findViewById(R.id.imgStatus);

            txtModelId.setTypeface(AppController.getDefaultBoldFont(context));
            txtModelName.setTypeface(AppController.getDefaultFont(context));
            txtModelDes.setTypeface(AppController.getDefaultFont(context));
            txtModelExp.setTypeface(AppController.getDefaultFont(context));
        }

        public void SearchShowModelDetails(final SearchDAO searchDAO, final int position){
            // Attach values for each item
            if(searchDAO.getStatus().equals("1")){
                imgStatus.setImageResource(R.drawable.ic_check_circle_green_24dp);
            }else {
                imgStatus.setImageResource(R.drawable.ic_check_circle_black_24dp);
            }
            txtModelId.setText("Model Id : "+searchDAO.getModel_code());
            txtModelName.setText("Name : "+searchDAO.getFirstname()+ " "+searchDAO.getLastname());
            txtModelDes.setText("Designation : "+searchDAO.getDesignation());
            txtModelExp.setText("Experience : "+searchDAO.getExperience()+" years");
            Glide.with(context).load(searchDAO.getProfile_image().toString().trim())
                    .into(imgModel);
            imgModel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    ArrayList<ModelDAO> searchDetails = new ArrayList<ModelDAO>();
                    searchDetails.add(new ModelDAO(searchDAO.getId(),searchDAO.getModel_code(),searchDAO.getAbout_you(),searchDAO.getAge(),searchDAO.getDesignation(),
                            searchDAO.getExperience(),searchDAO.getEye_color(),searchDAO.getFirstname(),searchDAO.getGender(),searchDAO.getHeight(),
                            searchDAO.getKnown_languages(),searchDAO.getLastname(),searchDAO.getMobile(),searchDAO.getProfile_image(),searchDAO.getSkin_color(),searchDAO.getWeight(),
                            searchDAO.getModel_images(),searchDAO.getEmail(),searchDAO.getStatus()));
                    MainActivity.profileDetails = searchDetails;
                    Intent intent = new Intent(context,ProfileActivity.class);
                    intent.putExtra(context.getString(R.string.tagIndex), -1);
                    context.startActivity(intent);
                }
            });
        }

    }

    private void changeStatus(final String strId,final String strStatus,final int position){
        try {
            final ProgressDialog pd = new ProgressDialog(context);
            pd.setMessage("Wait, updating status.");
            pd.show();
            StringRequest strReq = new StringRequest(Request.Method.POST, context.getString(R.string.defaultURL), new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    try {
                        pd.dismiss();
                        JSONObject jsonObject = new JSONObject(response);
                        if(jsonObject.getString(context.getString(R.string.tagStatus)).equals(context.getString(R.string.tagStatusValue))){
                            if(MainActivity.modelDAOS.get(position).getStatus().equals("1")){
                                MainActivity.modelDAOS.get(position).setStatus("0");
                            }else {
                                MainActivity.modelDAOS.get(position).setStatus("1");
                            }
                            notifyDataSetChanged();
                            Toast.makeText(context, jsonObject.getString(context.getString(R.string.tagSuccessMsg)),Toast.LENGTH_LONG).show();
                        } else {
                            Toast.makeText(context, jsonObject.getString(context.getString(R.string.tagSuccessMsg)),Toast.LENGTH_LONG).show();
                        }
                    } catch (Exception e) {
                        Toast.makeText(context, "Oops! Something went wrong.",Toast.LENGTH_LONG).show();
                        e.printStackTrace();
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    pd.dismiss();
                    if (error instanceof TimeoutError || error instanceof NoConnectionError)
                        Log.d("TAG.......", "v1: " + error);
                    else if (error instanceof AuthFailureError)
                        Log.d("TAG..........", "v2: " + error);
                    else if (error instanceof ServerError)
                        Log.d("TAG.........", "v3: " + error);
                    else if (error instanceof NetworkError)
                        Log.d("TAG.......", "v4: " + error);
                    else if (error instanceof ParseError)
                        Log.d("TAG.......", "v5: " + error);
                }
            }) {

                @Override
                protected Map<String, String> getParams() {
                    Map<String, String> params = new HashMap<String, String>();
                    params.put(context.getString(R.string.tag), "update_model_status");
                    params.put("id", strId);
                    params.put("status", strStatus);
                    return params;
                }
            };
            strReq.setRetryPolicy(new DefaultRetryPolicy(10000, 2, DefaultRetryPolicy.DEFAULT_TIMEOUT_MS));
            AppController.getInstance().addToRequestQueue(strReq);
        } catch (Exception e) {
        }

    }

}

