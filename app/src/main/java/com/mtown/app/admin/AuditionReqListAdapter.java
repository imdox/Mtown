package com.mtown.app.admin;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Gallery;
import android.widget.LinearLayout;
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
import com.mtown.app.R;
import com.mtown.app.auth.AuthActivity;
import com.mtown.app.dao.AuditionDAO;
import com.mtown.app.dao.AuditionReqDAO;
import com.mtown.app.home.MainActivity;
import com.mtown.app.support.AppController;
import com.mtown.app.support.GalleryImageAdapter;
import com.mtown.app.support.GalleryURLImageAdapter;
import com.mtown.app.user.AcceptAuditionActivity;
import com.mtown.app.user.AddEditUserActivity;
import com.mtown.app.user.ProfileActivity;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class AuditionReqListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private List<Object> dataList = new ArrayList();
    private Context context;

    // Constructor
    public AuditionReqListAdapter(Context context){
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
        if (dataList.get(position) instanceof AuditionReqDAO) {
            return AppController.TYPE_MODEL;
        }
        return -1;
    }

    // Invoked by layout manager to replace the contents of the views
    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, final int position) {
        int viewType=holder.getItemViewType();
        switch (viewType){
            case AppController.TYPE_MODEL:
                AuditionReqDAO auditionReqDAO=(AuditionReqDAO) dataList.get(position);
                ((DetailsViewHolder)holder).showAuditionReqListDetails(auditionReqDAO, position);
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
                layout= R.layout.audition_req_item;
                View auditionView = LayoutInflater.from(parent.getContext()).inflate(layout, parent, false);
                viewHolder=new DetailsViewHolder(auditionView);
                break;
            default:
                viewHolder=null;
                break;
        }
        return viewHolder;
    }


    // ViewHolder of object type details
    public class DetailsViewHolder extends RecyclerView.ViewHolder {

        private TextView txtRequestAuditionTitle,txtReqRole,txtCreatedBy,
                txtUserAction,txtUserComment,txtModelImages,txtcmtH;
        private Button btnAcceptReq,btnRejectReq,btnDetailsReq;
        private LinearLayout layUserAction;

        private Gallery gallery;

        public DetailsViewHolder(View itemView) {
            super(itemView);
            // Initiate view
            txtRequestAuditionTitle =(TextView)itemView.findViewById(R.id.txtRequestAuditionTitle);
            txtReqRole=(TextView)itemView.findViewById(R.id.txtReqRole);
            txtCreatedBy=(TextView)itemView.findViewById(R.id.txtCreatedBy);
            btnAcceptReq=(Button) itemView.findViewById(R.id.btnAcceptReq);
            btnRejectReq=(Button) itemView.findViewById(R.id.btnRejectReq);
            btnDetailsReq=(Button) itemView.findViewById(R.id.btnDetailsReq);
            layUserAction=(LinearLayout) itemView.findViewById(R.id.layUserAction);
            txtUserAction=(TextView)itemView.findViewById(R.id.txtUserAction);
            txtUserComment=(TextView)itemView.findViewById(R.id.txtUserComment);
            txtModelImages=(TextView)itemView.findViewById(R.id.txtModelImages);
            txtcmtH=(TextView)itemView.findViewById(R.id.txtcmtH);
            gallery = (Gallery)itemView.findViewById(R.id.gallery);

            txtRequestAuditionTitle.setTypeface(AppController.getDefaultBoldFont(context));
            txtReqRole.setTypeface(AppController.getDefaultFont(context));
            txtCreatedBy.setTypeface(AppController.getDefaultFont(context));
            btnAcceptReq.setTypeface(AppController.getDefaultBoldFont(context));
            btnRejectReq.setTypeface(AppController.getDefaultBoldFont(context));
            btnDetailsReq.setTypeface(AppController.getDefaultBoldFont(context));
            txtUserAction.setTypeface(AppController.getDefaultFont(context));
            txtUserComment.setTypeface(AppController.getDefaultFont(context));
            txtModelImages.setTypeface(AppController.getDefaultBoldFont(context));
            txtcmtH.setTypeface(AppController.getDefaultBoldFont(context));
        }

        public void showAuditionReqListDetails(final AuditionReqDAO auditionReqDAO, final int position){
            if(AppController.getSharedPref(context).getString(context.getString(R.string.tagGroupType), "").equals("admin")){
                txtRequestAuditionTitle.setText(auditionReqDAO.getCreated_by_name()+ " sent invitation for "+ auditionReqDAO.getAudition_title()+" audition.");
            } else if((AppController.getSharedPref(context).getString(context.getString(R.string.tagGroupType), "").equals("producer"))){
                txtRequestAuditionTitle.setText("You have sent invitation for "+ auditionReqDAO.getAudition_title()+" audition.");
            }else if((AppController.getSharedPref(context).getString(context.getString(R.string.tagGroupType), "").equals("model"))){
                txtRequestAuditionTitle.setText("You have invited for "+ auditionReqDAO.getAudition_title()+" audition.");
            }
            txtReqRole.setText("Role : "+auditionReqDAO.getRole_type());
            txtCreatedBy.setText("Created By : "+auditionReqDAO.getCreated_by_name());
            if(AppController.getSharedPref(context).getString(context.getString(R.string.tagGroupType), "").equals("model")){
                layUserAction.setVisibility(View.GONE);
                btnAcceptReq.setVisibility(View.VISIBLE);
                btnRejectReq.setVisibility(View.VISIBLE);
                if(auditionReqDAO.getMt_conformation().equals("1") || auditionReqDAO.getMt_conformation().equals("2")){
                   // btnAcceptReq.setEnabled(false);
                   // btnRejectReq.setEnabled(false);
                    btnAcceptReq.setVisibility(View.GONE);
                    btnRejectReq.setVisibility(View.GONE);
                }
            }else {
                layUserAction.setVisibility(View.VISIBLE);
                btnAcceptReq.setVisibility(View.GONE);
                btnRejectReq.setVisibility(View.GONE);
            }

            if(auditionReqDAO.getMt_conformation().equals("0")){
                gallery.setVisibility(View.GONE);
                txtModelImages.setVisibility(View.GONE);
                txtUserAction.setText("Model action : Pending(Viewed by Model)");
            } else  if(auditionReqDAO.getMt_conformation().equals("1")){
                gallery.setVisibility(View.VISIBLE);
                txtModelImages.setVisibility(View.VISIBLE);
                txtUserAction.setText("Model action : Invitation accepted by Model");
                txtUserComment.setText(auditionReqDAO.getMt_comment());
                if(auditionReqDAO.getAudition_images().length()>4){
                    gallery.setSpacing(5);
                    final GalleryURLImageAdapter galleryImageAdapter= new GalleryURLImageAdapter(context,auditionReqDAO.getAudition_images().split(","));
                    gallery.setAdapter(galleryImageAdapter);

                }
            } else  if(auditionReqDAO.getMt_conformation().equals("2")){
                txtUserAction.setText("Model action : Invitation rejected by Model");
                txtUserComment.setText(auditionReqDAO.getMt_comment());
                gallery.setVisibility(View.GONE);
                txtModelImages.setVisibility(View.GONE);
            }


            btnAcceptReq.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(context,AcceptAuditionActivity.class);
                    intent.putExtra(context.getString(R.string.tagIndex), position);
                    context.startActivity(intent);
                }
            });

            btnRejectReq.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    try{
                        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);
                        alertDialogBuilder.setMessage("Are you sure, You want to reject?");
                        alertDialogBuilder.setPositiveButton("yes",
                                new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface arg0, int arg1) {
                                        arg0.dismiss();
                                        final Dialog dialog = new Dialog(context,R.style.full_screen_dialog);
                                        dialog.setContentView(R.layout.dialog_req_reject);
                                        dialog.setTitle("");
                                        dialog.getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT);

                                        Button btnRejectReq;
                                        final EditText editUserComment;

                                        btnRejectReq =(Button) dialog.findViewById(R.id.btnRejectReq);
                                        editUserComment =(EditText)dialog.findViewById(R.id.editUserComment);


                                        btnRejectReq.setTypeface(AppController.getDefaultBoldFont(context));
                                        editUserComment.setTypeface(AppController.getDefaultFont(context));
                                        btnRejectReq.setOnClickListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View view) {
                                                if(editUserComment.getText().toString().trim().length()>10){
                                                    changeReqStatus(auditionReqDAO.getModel_id(),auditionReqDAO.getAudition_id(),"2",editUserComment.getText().toString().trim(),position);
                                                    dialog.dismiss();
                                                }else {
                                                    Toast.makeText(context, "Please specify specific reason.",Toast.LENGTH_LONG).show();
                                                }
                                            }
                                        });

                                        dialog.show();
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
                    }catch (Exception e){
                    }
                }
            });

            btnDetailsReq.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                  Intent intent = new Intent(context,AuditionDetailsActivity.class);
                    intent.putExtra(context.getString(R.string.tagUserId), auditionReqDAO.getAudition_id());
                    context.startActivity(intent);
                }
            });
        }
    }

    private void changeReqStatus(final String strModelId,final String strAuditionId,final String strConfirmationId,final String strComment,final int position){
        try {
            final ProgressDialog pd = new ProgressDialog(context);
            pd.setMessage("Wait, updating request status.");
            pd.show();
            StringRequest strReq = new StringRequest(Request.Method.POST, context.getString(R.string.defaultURL), new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    try {
                        pd.dismiss();
                        JSONObject jsonObject = new JSONObject(response);
                        if(jsonObject.getString(context.getString(R.string.tagStatus)).equals(context.getString(R.string.tagStatusValue))){
                            RequestListActivity.auditionReqDAOS.get(position).setMt_conformation(strConfirmationId);
                            RequestListActivity.auditionReqDAOS.get(position).setMt_comment(strComment);
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
                    //tag=&=9&=2&=2
                    Map<String, String> params = new HashMap<String, String>();
                    params.put(context.getString(R.string.tag), "reject_audition_detail");
                    params.put("model_id", strModelId);
                    params.put("audition_id", strAuditionId);
                    params.put("confirmation", strConfirmationId);
                    params.put("comment", strComment);
                    return params;
                }
            };
            strReq.setRetryPolicy(new DefaultRetryPolicy(10000, 2, DefaultRetryPolicy.DEFAULT_TIMEOUT_MS));
            AppController.getInstance().addToRequestQueue(strReq);
        } catch (Exception e) {
        }

    }
}

