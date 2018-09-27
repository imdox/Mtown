package com.mtown.app.admin;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

import com.mtown.app.R;
import com.mtown.app.dao.AuditionDAO;
import com.mtown.app.dao.AuditionReqDAO;
import com.mtown.app.support.AppController;

import java.util.ArrayList;
import java.util.List;


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

        private TextView txtRequestAuditionTitle,txtReqRole,txtCreatedBy;
        private Button btnAcceptReq,btnRejectReq,btnDetailsReq;

        public DetailsViewHolder(View itemView) {
            super(itemView);
            // Initiate view
            txtRequestAuditionTitle =(TextView)itemView.findViewById(R.id.txtRequestAuditionTitle);
            txtReqRole=(TextView)itemView.findViewById(R.id.txtReqRole);
            txtCreatedBy=(TextView)itemView.findViewById(R.id.txtCreatedBy);


            btnAcceptReq=(Button) itemView.findViewById(R.id.btnAcceptReq);
            btnRejectReq=(Button) itemView.findViewById(R.id.btnRejectReq);
            btnDetailsReq=(Button) itemView.findViewById(R.id.btnDetailsReq);

            txtRequestAuditionTitle.setTypeface(AppController.getDefaultBoldFont(context));
            txtReqRole.setTypeface(AppController.getDefaultFont(context));
            txtCreatedBy.setTypeface(AppController.getDefaultFont(context));
            btnDetailsReq.setTypeface(AppController.getDefaultBoldFont(context));
        }

        public void showAuditionReqListDetails(final AuditionReqDAO auditionReqDAO, final int position){
            // Attach values for each item
            txtRequestAuditionTitle.setText("You have invitation for "+ auditionReqDAO.getAudition_title() + " audition.");
            txtReqRole.setText("Role : "+auditionReqDAO.getRole_type());
            txtCreatedBy.setText("Created By : "+auditionReqDAO.getCreated_by_name());


            btnAcceptReq.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                          }
            });

            btnRejectReq.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
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
}

