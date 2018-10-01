package com.mtown.app.admin;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.mtown.app.R;
import com.mtown.app.dao.AuditionDAO;
import com.mtown.app.dao.ModelDAO;
import com.mtown.app.support.AppController;
import com.mtown.app.user.ProfileActivity;

import java.util.ArrayList;
import java.util.List;


public class AuditionListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private List<Object> dataList = new ArrayList();
    private Context context;

    // Constructor
    public AuditionListAdapter(Context context){
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
        if (dataList.get(position) instanceof AuditionDAO) {
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
                AuditionDAO auditionDAO=(AuditionDAO) dataList.get(position);
                ((DetailsViewHolder)holder).showAuditionListDetails(auditionDAO,position);
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
                layout= R.layout.audition_item;
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

        private TextView txtRequestAudition,txtCreatedBy,txtMobileNo,txtTotalModel,txModelRole;
        private Button btnCloseReq,btnViewDetails;

        public DetailsViewHolder(View itemView) {
            super(itemView);
            // Initiate view
            txtRequestAudition =(TextView)itemView.findViewById(R.id.txtRequestAudition);
            txtCreatedBy =(TextView)itemView.findViewById(R.id.txtCreatedBy);
            txtMobileNo =(TextView)itemView.findViewById(R.id.txtMobileNo);
            txtTotalModel =(TextView)itemView.findViewById(R.id.txtTotalModel);
            txModelRole =(TextView)itemView.findViewById(R.id.txModelRole);
            btnCloseReq =(Button) itemView.findViewById(R.id.btnCloseReq);
            btnViewDetails =(Button) itemView.findViewById(R.id.btnViewDetails);
            txtRequestAudition.setTypeface(AppController.getDefaultBoldFont(context));
            txtCreatedBy.setTypeface(AppController.getDefaultFont(context));
            txtMobileNo.setTypeface(AppController.getDefaultFont(context));
            txModelRole.setTypeface(AppController.getDefaultFont(context));
            txtTotalModel.setTypeface(AppController.getDefaultFont(context));
            btnCloseReq.setTypeface(AppController.getDefaultBoldFont(context));
            btnViewDetails.setTypeface(AppController.getDefaultBoldFont(context));
        }

        public void showAuditionListDetails(final AuditionDAO auditionDAO, final int position){
            // Attach values for each item
            txtCreatedBy.setText("Created By : "+auditionDAO.getCreated_by_name());
            txtMobileNo.setText("Mobile No. : "+auditionDAO.getMobile());
            txtTotalModel.setText("Total Models : "+auditionDAO.getTotal_model());
            txModelRole.setText("Role : "+auditionDAO.getRole_type());
            txtRequestAudition.setText(auditionDAO.getAudition_title());

      /*      txtMobileNo.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    try{
                        String dial = "tel:"+auditionDAO.getMobile();
                        context.startActivity(new Intent(Intent.ACTION_CALL, Uri.parse(dial)));
                    }catch (Exception e){
                    }
                }
            });*/

            btnCloseReq.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                }
            });



            btnViewDetails.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    detailsDialog(auditionDAO);
                }
            });
        }
    }

    private void detailsDialog(AuditionDAO auditionDAO){
        try{
            final Dialog dialog = new Dialog(context,R.style.full_screen_dialog);
            dialog.setContentView(R.layout.dialog_details);
            dialog.setTitle("");
            dialog.getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT);
            TextView txtRequestAudition,txtCreatedBy,txtMobileNo,txtTotalModel,txModelRole,
                    txtDescription,txtDescriptionValue,txtNote,txtNoteValue;
            txtRequestAudition =(TextView)dialog.findViewById(R.id.txtRequestAudition);
            txtCreatedBy =(TextView)dialog.findViewById(R.id.txtCreatedBy);
            txtMobileNo =(TextView)dialog.findViewById(R.id.txtMobileNo);
            txtTotalModel =(TextView)dialog.findViewById(R.id.txtTotalModel);
            txModelRole =(TextView)dialog.findViewById(R.id.txModelRole);
            txtDescription =(TextView)dialog.findViewById(R.id.txtDescription);
            txtDescriptionValue =(TextView)dialog.findViewById(R.id.txtDescriptionValue);
            txtNote =(TextView)dialog.findViewById(R.id.txtNote);
            txtNoteValue =(TextView)dialog.findViewById(R.id.txtNoteValue);


            txtRequestAudition.setTypeface(AppController.getDefaultBoldFont(context));
            txtCreatedBy.setTypeface(AppController.getDefaultFont(context));
            txtMobileNo.setTypeface(AppController.getDefaultFont(context));
            txModelRole.setTypeface(AppController.getDefaultFont(context));
            txtTotalModel.setTypeface(AppController.getDefaultFont(context));
            txtDescription.setTypeface(AppController.getDefaultBoldFont(context));
            txtDescriptionValue.setTypeface(AppController.getDefaultFont(context));
            txtNote.setTypeface(AppController.getDefaultBoldFont(context));
            txtNoteValue.setTypeface(AppController.getDefaultFont(context));

            txtCreatedBy.setText("Created By : "+auditionDAO.getCreated_by_name());
            txtMobileNo.setText("Mobile No. : "+auditionDAO.getMobile());
            txtTotalModel.setText("Total Models : "+auditionDAO.getTotal_model());
            txModelRole.setText("Role : "+auditionDAO.getRole_type());
            txtRequestAudition.setText("Audition Title : "+auditionDAO.getAudition_title());
            txtNoteValue.setText(auditionDAO.getNote());
            txtDescriptionValue.setText(auditionDAO.getDescription());

            dialog.show();

        }catch (Exception e){
        }
    }
}

