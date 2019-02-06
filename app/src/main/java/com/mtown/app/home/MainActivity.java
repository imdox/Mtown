package com.mtown.app.home;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
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
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;
import com.mtown.app.R;
import com.mtown.app.admin.AuditionListActivity;
import com.mtown.app.admin.CreateAuditionActivity;
import com.mtown.app.admin.RequestListActivity;
import com.mtown.app.auth.AuthActivity;
import com.mtown.app.dao.ModelDAO;
import com.mtown.app.search.SearchActivity;
import com.mtown.app.support.AppController;
import com.mtown.app.user.AddEditUserActivity;
import com.mtown.app.user.ModelListAdapter;
import com.mtown.app.user.ProfileActivity;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.sentry.Sentry;

public class MainActivity extends AppCompatActivity {

    public static ArrayList<ModelDAO> modelDAOS;
    public static ArrayList<ModelDAO> profileDetails;

    private ModelListAdapter modelListAdapter;
    private List<Object> adapterList;
    private ProgressBar progressBar;
    private RecyclerView recyclerView;

    private InterstitialAd interstitialAd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //checkPermission(Manifest.permission.CALL_PHONE);

        Context ctx = this.getApplicationContext();
        Sentry.init("https://9ca5182254934a0f8c812de9ff24e3ec@sentry.io/1363819");

        AdRequest addRequest = new AdRequest.Builder()
                .addTestDevice("E07693B78D043837CF9399C247ABE73D").build();
        interstitialAd = new InterstitialAd(MainActivity.this);
        interstitialAd.setAdUnitId("ca-app-pub-3940256099942544/8691691433");
        interstitialAd.loadAd(addRequest);
        interstitialAd.setAdListener(new AdListener(){
            @Override
            public void onAdClosed(){
                super.onAdClosed();
                userProfile();
            }
        });


//        getSupportActionBar().setDisplayShowHomeEnabled(true);
//        getSupportActionBar().setLogo(R.mipmap.ic_launcher);
//        getSupportActionBar().setDisplayUseLogoEnabled(true);

        progressBar = (ProgressBar)findViewById(R.id.progressBar);
        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        // Set Layout Manager
        RecyclerView.LayoutManager mLayoutManager = new GridLayoutManager(this, 2);
        recyclerView.setLayoutManager(mLayoutManager);
        // Limiting the size
        recyclerView.setHasFixedSize(true);

        if (AppController.isConnectingToInternet(this)) {
            getModelList();
        } else {
            Toast.makeText(MainActivity.this, "Please connect to internet", Toast.LENGTH_SHORT).show();
        }

    }

    public void getModelDetails() {
        try {
            StringRequest strReq = new StringRequest(Request.Method.POST, getString(R.string.defaultURL), new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    try {
                        JSONObject jsonObject = new JSONObject(response);
                        if(jsonObject.getString(getString(R.string.tagStatus)).equals(getString(R.string.tagStatusValue))){
                            JSONObject jsonObjectData = jsonObject.getJSONObject(getString(R.string.tagResult));
                            profileDetails = new ArrayList<ModelDAO>();

                            /*To get Image*/
                            String imgUrls[] = jsonObjectData.getString("model_images").split(",");;

                            /*Bind data to Data Model */
                            ModelDAO newsDAO = new ModelDAO(jsonObjectData.getString("id"),
                                    jsonObjectData.getString("model_code"),jsonObjectData.getString("about_you"),
                                    jsonObjectData.getString("age"),jsonObjectData.getString("designation"),
                                    jsonObjectData.getString("experience"),jsonObjectData.getString("eye_color"),
                                    jsonObjectData.getString("firstname"),jsonObjectData.getString("gender"),jsonObjectData.getString("height"),
                                    jsonObjectData.getString("known_languages"),jsonObjectData.getString("lastname"),jsonObjectData.getString("mobile"),
                                    jsonObjectData.getString("profile_image"),jsonObjectData.getString("skin_color"),
                                    jsonObjectData.getString("weight"),imgUrls,jsonObjectData.getString("email"),jsonObjectData.getString("status")
                            );
                            profileDetails.add(newsDAO);

                        } else {
                            Toast.makeText(MainActivity.this, "Oops! Something went wrong.",Toast.LENGTH_LONG).show();
                        }
                    } catch (Exception e) {
                        Toast.makeText(MainActivity.this, "Oops! Something went wrong.",Toast.LENGTH_LONG).show();
                        e.printStackTrace();
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Toast.makeText(MainActivity.this, "Oops! Something went wrong.",Toast.LENGTH_LONG).show();
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
                    params.put(getString(R.string.tag), "model_details");
                    params.put("id", AppController.getSharedPref(MainActivity.this).getString(getString(R.string.tagModelId),"0"));
                    return params;
                }
            };
            strReq.setRetryPolicy(new DefaultRetryPolicy(10000, 2, DefaultRetryPolicy.DEFAULT_TIMEOUT_MS));
            AppController.getInstance().addToRequestQueue(strReq);
        } catch (Exception e) {
        }
    }



    public void getModelList() {
        try {
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
                                modelDAOS = new ArrayList<ModelDAO>();
                                for(int i=0;i<jsonArray.length();i++){
                                    JSONObject jsonObjectData = jsonArray.getJSONObject(i);
                                    /*To get Image*/
                                    String imgUrls[] = jsonObjectData.getString("model_images").split(",");

                                    /*Bind data to Data Model */
                                    ModelDAO newsDAO = new ModelDAO(jsonObjectData.getString("id"),
                                            jsonObjectData.getString("model_code"),jsonObjectData.getString("about_you"),
                                            jsonObjectData.getString("age"),jsonObjectData.getString("designation"),
                                            jsonObjectData.getString("experience"),jsonObjectData.getString("eye_color"),
                                            jsonObjectData.getString("firstname"),jsonObjectData.getString("gender"),jsonObjectData.getString("height"),
                                            jsonObjectData.getString("known_languages"),jsonObjectData.getString("lastname"),jsonObjectData.getString("mobile"),
                                            jsonObjectData.getString("profile_image"),jsonObjectData.getString("skin_color"),
                                            jsonObjectData.getString("weight"),imgUrls,jsonObjectData.getString("email"),jsonObjectData.getString("status")
                                    );
                                    modelDAOS.add(newsDAO);
                                }
                                setNewsAdapter(1);
                            } else {
                                Toast.makeText(MainActivity.this, jsonObject.getString(getString(R.string.tagSuccessMsg)), Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            Toast.makeText(MainActivity.this, jsonObject.getString(getString(R.string.tagSuccessMsg)),Toast.LENGTH_LONG).show();
                        }
                    } catch (Exception e) {
                        Toast.makeText(MainActivity.this, "Oops! Something went wrong.",Toast.LENGTH_LONG).show();
                        e.printStackTrace();
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Toast.makeText(MainActivity.this, "Oops! Something went wrong.",Toast.LENGTH_LONG).show();
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
                    params.put(getString(R.string.tag), "modellist");
                    params.put("role_type", AppController.getSharedPref(MainActivity.this).getString(getString(R.string.tagGroupType), ""));

                    return params;
                }
            };
            strReq.setRetryPolicy(new DefaultRetryPolicy(10000, 2, DefaultRetryPolicy.DEFAULT_TIMEOUT_MS));
            AppController.getInstance().addToRequestQueue(strReq);
        } catch (Exception e) {
        }
    }

    private void setNewsAdapter(int listType){
        try {
            adapterList = new ArrayList<>();
            switch(listType){
                case AppController.TYPE_MODEL:
                    adapterList.addAll(modelDAOS);
                    break;

            }
            // Initiating Adapter
            modelListAdapter = new ModelListAdapter(MainActivity.this);
            recyclerView.setAdapter(modelListAdapter);
            modelListAdapter.setAdapterData(adapterList);
            modelListAdapter.notifyDataSetChanged();
        } catch (Exception e){
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
       if(AppController.getSharedPref(MainActivity.this).getString(getString(R.string.tagGroupType), "").equals("model")){
           getMenuInflater().inflate(R.menu.menu_main, menu);
       }else if(AppController.getSharedPref(MainActivity.this).getString(getString(R.string.tagGroupType), "").equals("admin")){
           getMenuInflater().inflate(R.menu.menu_admin, menu);
       } else {
           getMenuInflater().inflate(R.menu.menu_pd, menu);
       }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()) {
        case R.id.idLogOut:
             AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
            alertDialogBuilder.setMessage("Are you sure, You want to logout?");
                    alertDialogBuilder.setPositiveButton("yes",
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface arg0, int arg1) {
                                    AppController.getSharedPrefEditor(MainActivity.this).clear().commit();
                                    startActivity(new Intent(MainActivity.this,AuthActivity.class));
                                    finish();
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

            return(true);
        case R.id.profile:
//            if(interstitialAd.isLoaded()||interstitialAd.isLoading()){
//                interstitialAd.show();
//            }else{
                userProfile();
          //  }
            return(true);

        case R.id.idCreateAudition:
            startActivity(new Intent(MainActivity.this,CreateAuditionActivity.class));
            return(true);

        case R.id.idAuditionList:
            startActivity(new Intent(MainActivity.this,AuditionListActivity.class));
            return(true);

        case R.id.idAuditionReqList:
            //RequestListActivity
            startActivity(new Intent(MainActivity.this,RequestListActivity.class));
            return(true);

        case R.id.idSearch:
            //RequestListActivity
            startActivity(new Intent(MainActivity.this,SearchActivity.class));
            return(true);


        }
        return(super.onOptionsItemSelected(item));
    }

    private static final int MAKE_CALL_PERMISSION_REQUEST_CODE = 1;
    private boolean checkPermission(String permission) {
        return ContextCompat.checkSelfPermission(this, permission) == PackageManager.PERMISSION_GRANTED;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch(requestCode) {
            case MAKE_CALL_PERMISSION_REQUEST_CODE :
                if (grantResults.length > 0 && (grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                    Toast.makeText(this, "You can call the number by clicking on the button", Toast.LENGTH_SHORT).show();
                }
                return;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(AppController.getSharedPref(MainActivity.this).getString(getString(R.string.tagIsProfile),"0").equals("1")){
            getModelDetails();
        }
    }

    private void userProfile(){
        try{
            String isPCreated = AppController.getSharedPref(MainActivity.this).getString(getString(R.string.tagIsProfile),"0");
            if(isPCreated.equals("0")){
                Intent intent = new Intent(this,AddEditUserActivity.class);
                intent.putExtra(getString(R.string.tagIndex), 0);
                startActivity(intent);
            }else{
                Intent intent = new Intent(this, ProfileActivity.class);
                intent.putExtra(getString(R.string.tagIndex), -1);
                startActivity(intent);
            }
        }catch (Exception e){
        }
    }
}
