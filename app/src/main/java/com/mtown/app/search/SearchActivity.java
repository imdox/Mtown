package com.mtown.app.search;

import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;

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
import com.mtown.app.dao.SearchDAO;
import com.mtown.app.support.AppController;
import com.mtown.app.user.ModelListAdapter;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SearchActivity extends AppCompatActivity {

    public ArrayList<SearchDAO> searchMDAOS;
    private ModelListAdapter modelListAdapter;
    private List<Object> adapterList;
    private ProgressBar progressBar;
    private RecyclerView recyclerView;

    private InterstitialAd interstitialAd;
    private EditText editText;
    //private ImageView imgSearch;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setHomeButtonEnabled(true);
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        AdRequest addRequest = new AdRequest.Builder()
                .addTestDevice("E07693B78D043837CF9399C247ABE73D").build();
        interstitialAd = new InterstitialAd(SearchActivity.this);
        interstitialAd.setAdUnitId("ca-app-pub-3940256099942544/8691691433");
        interstitialAd.loadAd(addRequest);
        interstitialAd.setAdListener(new AdListener(){
            @Override
            public void onAdClosed(){
                super.onAdClosed();
              //  userProfile();
            }
        });

        //imgSearch = (ImageView)findViewById(R.id.imgSearch);
        progressBar = (ProgressBar)findViewById(R.id.progressBar);
        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        // Set Layout Manager
        RecyclerView.LayoutManager mLayoutManager = new GridLayoutManager(this, 1);
        recyclerView.setLayoutManager(mLayoutManager);
        // Limiting the size
        recyclerView.setHasFixedSize(true);
        editText = findViewById(R.id.txtSearch);
        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                    try{
                        String str = s.toString();
                        if(str.length()>3){
                            searchModel(str);
                        }else if(str.length()<1){
                            if(!searchMDAOS.isEmpty()){
                                searchMDAOS.clear();
                            }
                          //  imgSearch.setVisibility(View.VISIBLE);
                            setSearchAdapter(1);
                        }
                    }catch (Exception e){
                     }
                // TODO Auto-generated method stub
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // TODO Auto-generated method stub
            }

            @Override
            public void afterTextChanged(Editable s) {
                // TODO Auto-generated method stub
            }
        });
    }

    public void searchModel(final String strSearch) {
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
                                searchMDAOS = new ArrayList<SearchDAO>();
                                for(int i=0;i<jsonArray.length();i++){
                                    JSONObject jsonObjectData = jsonArray.getJSONObject(i);
                                    /*To get Image*/
                                    String imgUrls[] = jsonObjectData.getString("model_images").split(",");;

                                    /*Bind data to Data Model */
                                    SearchDAO newsDAO = new SearchDAO(jsonObjectData.getString("id"),
                                            jsonObjectData.getString("model_code"),jsonObjectData.getString("about_you"),
                                            jsonObjectData.getString("age"),jsonObjectData.getString("designation"),
                                            jsonObjectData.getString("experience"),jsonObjectData.getString("eye_color"),
                                            jsonObjectData.getString("firstname"),jsonObjectData.getString("gender"),jsonObjectData.getString("height"),
                                            jsonObjectData.getString("known_languages"),jsonObjectData.getString("lastname"),jsonObjectData.getString("mobile"),
                                            jsonObjectData.getString("profile_image"),jsonObjectData.getString("skin_color"),
                                            jsonObjectData.getString("weight"),imgUrls,jsonObjectData.getString("email"),jsonObjectData.getString("status")
                                    );
                                    searchMDAOS.add(newsDAO);
                                }
                                setSearchAdapter(2);
                            } else {
                  //              Toast.makeText(SearchActivity.this, jsonObject.getString(getString(R.string.tagSuccessMsg)), Toast.LENGTH_SHORT).show();
                            }
                        } else {
                    //        Toast.makeText(SearchActivity.this, "Oops! Something went wrong.",Toast.LENGTH_LONG).show();
                        }
                    } catch (Exception e) {
                      //  Toast.makeText(SearchActivity.this, "Oops! Something went wrong.",Toast.LENGTH_LONG).show();
                        e.printStackTrace();
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                  //  Toast.makeText(SearchActivity.this, "Oops! Something went wrong.",Toast.LENGTH_LONG).show();
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
                    params.put(getString(R.string.tag), "search");
                    params.put("search", strSearch);
                    params.put("role_type", AppController.getSharedPref(SearchActivity.this).getString(getString(R.string.tagGroupType), ""));
                    return params;
                }
            };
            strReq.setRetryPolicy(new DefaultRetryPolicy(10000, 2, DefaultRetryPolicy.DEFAULT_TIMEOUT_MS));
            AppController.getInstance().addToRequestQueue(strReq);
        } catch (Exception e) {
        }
    }

    private void setSearchAdapter(int listType){
        try {
            //imgSearch.setVisibility(View.GONE);
            adapterList = new ArrayList<>();
            switch(listType){
                case AppController.TYPE_MODEL_SEARCH:
                    adapterList.addAll(searchMDAOS);
                    break;
            }
            // Initiating Adapter
            modelListAdapter = new ModelListAdapter(SearchActivity.this);
            recyclerView.setAdapter(modelListAdapter);
            modelListAdapter.setAdapterData(adapterList);
            modelListAdapter.notifyDataSetChanged();
        } catch (Exception e){
        }
    }
}
