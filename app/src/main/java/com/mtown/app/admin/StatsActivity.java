package com.mtown.app.admin;

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
import com.mtown.app.support.AppController;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class StatsActivity extends AppCompatActivity {

    private TextView txtTMV,txtHTM,txtHATM,txtATM,txtHIATM,txtIATM,txtTAV,txtHTA;
    private ProgressBar progressBar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stats);
        progressBar = (ProgressBar)findViewById(R.id.progressBar);

        txtTMV = findViewById(R.id.txtTMV);
        txtHTM = findViewById(R.id.txtHTM);
        txtHATM = findViewById(R.id.txtHATM);
        txtATM = findViewById(R.id.txtATM);
        txtHIATM = findViewById(R.id.txtHIATM);
        txtIATM = findViewById(R.id.txtIATM);
        txtTAV = findViewById(R.id.txtTAV);
        txtHTA = findViewById(R.id.txtHTA);
        txtTMV.setTypeface(AppController.getDefaultBoldFont(StatsActivity.this));
        txtHTM.setTypeface(AppController.getDefaultBoldFont(StatsActivity.this));
        txtHATM.setTypeface(AppController.getDefaultBoldFont(StatsActivity.this));
        txtATM.setTypeface(AppController.getDefaultBoldFont(StatsActivity.this));
        txtHIATM.setTypeface(AppController.getDefaultBoldFont(StatsActivity.this));
        txtIATM.setTypeface(AppController.getDefaultBoldFont(StatsActivity.this));
        txtTAV.setTypeface(AppController.getDefaultBoldFont(StatsActivity.this));
        txtHTA.setTypeface(AppController.getDefaultBoldFont(StatsActivity.this));
        getStats();
    }

    private void getStats() {
        try {
            progressBar.setVisibility(View.VISIBLE);
            StringRequest strReq = new StringRequest(Request.Method.POST, getString(R.string.defaultURL), new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    try {
                        progressBar.setVisibility(View.GONE);
                        JSONObject jsonObject = new JSONObject(response);
                        if(jsonObject.getString(getString(R.string.tagStatus)).equals(getString(R.string.tagStatusValue))){
                            JSONObject jsonObject2 = jsonObject.getJSONObject("stats");
                            txtTMV.setText(jsonObject2.getString("mtotalCount"));
                            txtHATM.setText(jsonObject2.getString("mactiveCount"));
                            txtHIATM.setText(jsonObject2.getString("munactiveCount"));
                            txtTAV.setText(jsonObject2.getString("auditionCount"));
                        } else {
                            Toast.makeText(StatsActivity.this, jsonObject.getString(getString(R.string.tagSuccessMsg)),Toast.LENGTH_LONG).show();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Toast.makeText(StatsActivity.this, "Oops! Something went wrong.",Toast.LENGTH_LONG).show();
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
                    params.put(getString(R.string.tag), "statistics");
                    return params;
                }
            };
            strReq.setRetryPolicy(new DefaultRetryPolicy(10000, 2, DefaultRetryPolicy.DEFAULT_TIMEOUT_MS));
            AppController.getInstance().addToRequestQueue(strReq);
        } catch (Exception e) {
        }
    }

}
