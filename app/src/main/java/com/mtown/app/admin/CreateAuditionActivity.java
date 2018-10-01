package com.mtown.app.admin;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
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
import com.mtown.app.support.AppController;
import com.mtown.app.support.Validation;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class CreateAuditionActivity extends AppCompatActivity {

    private EditText txtAuditionTitle,txtAuditionRole,edtAuditionDescription,edtAuditionNote;
    private Button btnCreateAudition;
    private TextView txtSelectModel;

    private ProgressBar progressBar;


   private String created_by,created_name,created_mobile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_audition);
        AppController.modelIds = "";
        AppController.modelCode = "";
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setHomeButtonEnabled(true);
            actionBar.setDisplayHomeAsUpEnabled(true);
        } progressBar = (ProgressBar)findViewById(R.id.progressBar);
        btnCreateAudition  = findViewById(R.id.btnCreateAudition);

        txtAuditionTitle = findViewById(R.id.txtAuditionTitle);
        txtAuditionRole = findViewById(R.id.txtAuditionRole);
        txtSelectModel = findViewById(R.id.txtSelectModel);
        edtAuditionDescription = findViewById(R.id.edtAuditionDescription);
        edtAuditionNote = findViewById(R.id.edtAuditionNote);

        txtAuditionTitle.setTypeface(AppController.getDefaultFont(CreateAuditionActivity.this));
        txtAuditionRole.setTypeface(AppController.getDefaultFont(CreateAuditionActivity.this));
        txtSelectModel.setTypeface(AppController.getDefaultFont(CreateAuditionActivity.this));
        edtAuditionDescription.setTypeface(AppController.getDefaultFont(CreateAuditionActivity.this));
        edtAuditionNote.setTypeface(AppController.getDefaultFont(CreateAuditionActivity.this));
        btnCreateAudition.setTypeface(AppController.getDefaultBoldFont(CreateAuditionActivity.this));

        btnCreateAudition.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                boolean isValidData = false;

                if (AppController.isConnectingToInternet(CreateAuditionActivity.this)) {
                    if (!Validation.hasText(txtAuditionTitle)) {
                    } else if (!Validation.hasText(txtAuditionRole)) {
                    } else if (txtSelectModel.getText().toString().trim().length()<5) {
                        txtSelectModel.setError("Please select models.");
                    } else if (!Validation.hasText(edtAuditionDescription)) {
                    } else if (!Validation.hasText(edtAuditionNote)) {
                    } else {
                        createAudition();
                    }
                } else{
                    Toast.makeText(CreateAuditionActivity.this, "Please connect to internet", Toast.LENGTH_SHORT).show();
                }
            }
        });
        txtSelectModel.setText(AppController.modelCode);

        txtSelectModel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
             startActivity(new Intent(CreateAuditionActivity.this, ListActivity.class));
            }
        });
        getUserDetails();
    }

    @Override
    protected void onResume() {
        super.onResume();
        txtSelectModel.setText(AppController.modelCode);
    }

    private void getUserDetails(){
        try {
            StringRequest strReq = new StringRequest(Request.Method.POST, getString(R.string.defaultURL), new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    try {
                        JSONObject jsonObject = new JSONObject(response);
                        if(jsonObject.getString(getString(R.string.tagStatus)).equals(getString(R.string.tagStatusValue))){
                            JSONObject jsonObjectData = jsonObject.getJSONObject(getString(R.string.tagResult));
                            created_by=jsonObjectData.getString("id");
                            created_name=jsonObjectData.getString("firstname")+" "+jsonObjectData.getString("lastname");
                            created_mobile=jsonObjectData.getString("username");
                        }
                    } catch (Exception e) {
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
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
                    params.put(getString(R.string.tag), "user_details");
                    params.put("id", AppController.getSharedPref(CreateAuditionActivity.this).getString(getString(R.string.tagUserId),"0"));
                    return params;
                }
            };
            strReq.setRetryPolicy(new DefaultRetryPolicy(10000, 2, DefaultRetryPolicy.DEFAULT_TIMEOUT_MS));
            AppController.getInstance().addToRequestQueue(strReq);
        } catch (Exception e) {
        }
    }

    private void createAudition(){
        try {
            progressBar.setVisibility(View.VISIBLE);
            StringRequest strReq = new StringRequest(Request.Method.POST, getString(R.string.defaultURL), new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    try {
                        progressBar.setVisibility(View.GONE);
                        JSONObject jsonObject = new JSONObject(response);
                        if(jsonObject.getString(getString(R.string.tagStatus)).equals(getString(R.string.tagStatusValue))){
                            Toast.makeText(CreateAuditionActivity.this, jsonObject.getString(getString(R.string.tagSuccessMsg)), Toast.LENGTH_SHORT).show();
                            finish();
                        } else {
                            Toast.makeText(CreateAuditionActivity.this, jsonObject.getString(getString(R.string.tagSuccessMsg)),Toast.LENGTH_LONG).show();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
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
                    params.put(getString(R.string.tag), "add_audition");
                    params.put("audition_title", txtAuditionTitle.getText().toString().trim());
                    params.put("description", edtAuditionDescription.getText().toString().trim());
                    params.put("role_type", txtAuditionRole.getText().toString().trim());
                    params.put("note", edtAuditionNote.getText().toString().trim());
                    params.put("created_by", created_by);
                    params.put("created_name", created_name);
                    params.put("created_mobile", created_mobile);
                    params.put("model_ids", AppController.modelIds);
                    params.put("total_model", String.valueOf(AppController.modelIds.split(",").length));
                    return params;
                }
            };
            strReq.setRetryPolicy(new DefaultRetryPolicy(10000, 2, DefaultRetryPolicy.DEFAULT_TIMEOUT_MS));
            AppController.getInstance().addToRequestQueue(strReq);
        } catch (Exception e) {
        }
    }
}
