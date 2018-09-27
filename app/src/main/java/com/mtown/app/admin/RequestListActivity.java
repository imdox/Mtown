package com.mtown.app.admin;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
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
import com.google.android.gms.ads.InterstitialAd;
import com.mtown.app.R;
import com.mtown.app.dao.AuditionDAO;
import com.mtown.app.dao.AuditionReqDAO;
import com.mtown.app.support.AppController;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RequestListActivity extends AppCompatActivity {

    public static ArrayList<AuditionReqDAO> auditionReqDAOS;
    private AuditionReqListAdapter auditionReqListAdapter;
    private List<Object> adapterList;

    private ProgressBar progressBar;
    private RecyclerView recyclerView;
    private InterstitialAd interstitialAd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_request_list);

        getSupportActionBar().setTitle("Audition Request ");
        progressBar = (ProgressBar)findViewById(R.id.progressBar);
        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        // Set Layout Manager
        RecyclerView.LayoutManager mLayoutManager = new GridLayoutManager(this, 1);
        recyclerView.setLayoutManager(mLayoutManager);
        // Limiting the size
        recyclerView.setHasFixedSize(true);

        if (AppController.isConnectingToInternet(this)) {
            getModelDetails();
        } else {
            Toast.makeText(RequestListActivity.this, "Please connect to internet", Toast.LENGTH_SHORT).show();
        }
    }

    private String userId,createdById = "0";

    public void getModelDetails() {
        try {
            userId = AppController.getSharedPref(RequestListActivity.this).getString(getString(R.string.tagUserId), "0");
            if(AppController.getSharedPref(RequestListActivity.this).getString(getString(R.string.tagGroupType), "").equals("admin")){
                userId = "0";
                createdById="0";
            } else if((AppController.getSharedPref(RequestListActivity.this).getString(getString(R.string.tagGroupType), "").equals("producer"))){
                createdById = userId;
                userId = "-1";
            }

            progressBar.setVisibility(View.VISIBLE);
            StringRequest strReq = new StringRequest(Request.Method.POST, getString(R.string.defaultURL), new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    try {
                        progressBar.setVisibility(View.GONE);
                        JSONObject jsonObject = new JSONObject(response);
                        if(jsonObject.getString(getString(R.string.tagStatus)).equals(getString(R.string.tagStatusValue))){
                            JSONArray jsonArray = jsonObject.getJSONArray(getString(R.string.tagResult));
                            if(jsonArray.length()>0){
                                auditionReqDAOS = new ArrayList<AuditionReqDAO>();
                                for(int i=0;i<jsonArray.length();i++){
                                    JSONObject jsonObjectData = jsonArray.getJSONObject(i);

                                    /*Bind data to Data Model */
                                    AuditionReqDAO auditionReqDAO = new AuditionReqDAO(jsonObjectData.getString("audition_id"),
                                            jsonObjectData.getString("id"),jsonObjectData.getString("audition_images"),
                                            jsonObjectData.getString("model_id"),jsonObjectData.getString("mt_comment"),
                                            jsonObjectData.getString("mt_conformation"),jsonObjectData.getString("notification"),
                                            jsonObjectData.getString("reason"),jsonObjectData.getString("result_status"),jsonObjectData.getString("audition_title"),
                                            jsonObjectData.getString("created_by_id"),jsonObjectData.getString("created_by_name"),
                                            jsonObjectData.getString("description"),
                                            jsonObjectData.getString("note"),jsonObjectData.getString("role_type"),jsonObjectData.getString("total_model"));
                                    auditionReqDAOS.add(auditionReqDAO);
                                }
                                setAdapter();
                            } else {
                                Toast.makeText(RequestListActivity.this, jsonObject.getString(getString(R.string.tagSuccessMsg)), Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            Toast.makeText(RequestListActivity.this, jsonObject.getString(getString(R.string.tagSuccessMsg)),Toast.LENGTH_LONG).show();
                        }
                    } catch (Exception e) {
                        Toast.makeText(RequestListActivity.this, "Oops! Something went wrong.",Toast.LENGTH_LONG).show();
                        e.printStackTrace();
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Toast.makeText(RequestListActivity.this, "Oops! Something went wrong.",Toast.LENGTH_LONG).show();
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
                    params.put(getString(R.string.tag), "audition_details_list");
                    params.put("model_id", userId);
                    params.put("created_by_id", createdById);
                    return params;
                }
            };
            strReq.setRetryPolicy(new DefaultRetryPolicy(10000, 2, DefaultRetryPolicy.DEFAULT_TIMEOUT_MS));
            AppController.getInstance().addToRequestQueue(strReq);
        } catch (Exception e) {
        }
    }

    private void setAdapter(){
        try {
            adapterList = new ArrayList<>();
            adapterList.addAll(auditionReqDAOS);

            // Initiating Adapter
            auditionReqListAdapter = new AuditionReqListAdapter(RequestListActivity.this);
            recyclerView.setAdapter(auditionReqListAdapter);
            auditionReqListAdapter.setAdapterData(adapterList);
            auditionReqListAdapter.notifyDataSetChanged();
        } catch (Exception e){
        }
    }
}

