package com.mtown.app.admin;

import android.content.Intent;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
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
import com.mtown.app.dao.AuditionDAO;
import com.mtown.app.home.MainActivity;
import com.mtown.app.support.AppController;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class AuditionDetailsActivity extends AppCompatActivity {

    private TextView txtRequestAudition,txtCreatedBy,txtMobileNo,txtTotalModel,txModelRole,
            txtDescription,txtDescriptionValue,txtNote,txtNoteValue;

    private ProgressBar progressBar;
    private String audId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_audition_details);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setHomeButtonEnabled(true);
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
        //get Intent
        Intent intentData = getIntent();
        audId = intentData.getStringExtra(getString(R.string.tagUserId));

        progressBar = (ProgressBar)findViewById(R.id.progressBar);
         txtRequestAudition =(TextView)findViewById(R.id.txtRequestAudition);
        txtCreatedBy =(TextView)findViewById(R.id.txtCreatedBy);
        txtMobileNo =(TextView)findViewById(R.id.txtMobileNo);
        txtTotalModel =(TextView)findViewById(R.id.txtTotalModel);
        txModelRole =(TextView)findViewById(R.id.txModelRole);
        txtDescription =(TextView)findViewById(R.id.txtDescription);
        txtDescriptionValue =(TextView)findViewById(R.id.txtDescriptionValue);
        txtNote =(TextView)findViewById(R.id.txtNote);
        txtNoteValue =(TextView)findViewById(R.id.txtNoteValue);

        if(AppController.getSharedPref(AuditionDetailsActivity.this).getString(getString(R.string.tagGroupType), "").equals("model")){
            txtMobileNo.setVisibility(View.GONE);
            txtTotalModel.setVisibility(View.GONE);
        }


        txtRequestAudition.setTypeface(AppController.getDefaultBoldFont(AuditionDetailsActivity.this));
        txtCreatedBy.setTypeface(AppController.getDefaultFont(AuditionDetailsActivity.this));
        txtMobileNo.setTypeface(AppController.getDefaultFont(AuditionDetailsActivity.this));
        txModelRole.setTypeface(AppController.getDefaultFont(AuditionDetailsActivity.this));
        txtTotalModel.setTypeface(AppController.getDefaultFont(AuditionDetailsActivity.this));
        txtDescription.setTypeface(AppController.getDefaultBoldFont(AuditionDetailsActivity.this));
        txtDescriptionValue.setTypeface(AppController.getDefaultFont(AuditionDetailsActivity.this));
        txtNote.setTypeface(AppController.getDefaultBoldFont(AuditionDetailsActivity.this));
        txtNoteValue.setTypeface(AppController.getDefaultFont(AuditionDetailsActivity.this));


        if (AppController.isConnectingToInternet(this)) {
            getAuditionDetails();
        } else {
            Toast.makeText(AuditionDetailsActivity.this, "Please connect to internet", Toast.LENGTH_SHORT).show();
        }
    }

    public void getAuditionDetails() {
        try {
            progressBar.setVisibility(View.VISIBLE);
            StringRequest strReq = new StringRequest(Request.Method.POST, getString(R.string.defaultURL), new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    try {
                        progressBar.setVisibility(View.GONE);
                        JSONObject jsonObject = new JSONObject(response);
                        if(jsonObject.getString(getString(R.string.tagStatus)).equals(getString(R.string.tagStatusValue))){
                            JSONObject jsonObjectData = jsonObject.getJSONObject(getString(R.string.tagResult));

                                    /*Bind data to Data Model */
                                AuditionDAO     auditionDAO = new AuditionDAO(jsonObjectData.getString("audition_title"),
                                            jsonObjectData.getString("created_by_id"),jsonObjectData.getString("created_by_name"),
                                            jsonObjectData.getString("description"),jsonObjectData.getString("id"),
                                            jsonObjectData.getString("note"),jsonObjectData.getString("role_type"),
                                        jsonObjectData.getString("total_model"),jsonObjectData.getString("created_by_mobile"));
                                txtCreatedBy.setText("Created By : "+auditionDAO.getCreated_by_name());
                                txtMobileNo.setText("Mobile No. : "+auditionDAO.getMobile());
                                txtTotalModel.setText("Total Models : "+auditionDAO.getTotal_model());
                                txModelRole.setText("Role : "+auditionDAO.getRole_type());
                                txtRequestAudition.setText("Audition title : "+auditionDAO.getAudition_title());
                                txtNoteValue.setText(auditionDAO.getNote());
                                txtDescriptionValue.setText(auditionDAO.getDescription());

                        } else {
                            Toast.makeText(AuditionDetailsActivity.this, "Oops! Something went wrong.",Toast.LENGTH_LONG).show();
                        }
                    } catch (Exception e) {
                        Toast.makeText(AuditionDetailsActivity.this, "Oops! Something went wrong.",Toast.LENGTH_LONG).show();
                        e.printStackTrace();
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Toast.makeText(AuditionDetailsActivity.this, "Oops! Something went wrong.",Toast.LENGTH_LONG).show();
                    progressBar.setVisibility(View.GONE);
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
                    params.put(getString(R.string.tag), "audition_details");
                    params.put("id", audId);
                    return params;
                }
            };
            strReq.setRetryPolicy(new DefaultRetryPolicy(10000, 2, DefaultRetryPolicy.DEFAULT_TIMEOUT_MS));
            AppController.getInstance().addToRequestQueue(strReq);
        } catch (Exception e) {
        }
    }

}
