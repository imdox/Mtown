package com.mtown.app.auth;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
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
import com.mtown.app.home.MainActivity;
import com.mtown.app.support.AppController;
import com.mtown.app.support.Validation;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class AuthActivity extends AppCompatActivity implements  View.OnClickListener{

    //Home Page
    private TextView txtLoginMsgText;
    private ProgressBar progressBar;


    //Login Page
    private EditText txtLoginEmail, txtLoginPassword;
    private Button btnLogin;
    private CheckBox chkShowPassword;

    //Forget password
    private TextView txtForgetPassword;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_auth);
        getSupportActionBar().hide();

        progressBar = (ProgressBar) findViewById(R.id.progressBar);

      //xml binding - Login
        txtLoginEmail = (EditText) findViewById(R.id.txtLoginEmail);
        txtLoginPassword = (EditText) findViewById(R.id.txtLoginPassword);
        btnLogin = (Button) findViewById(R.id.btnLogin);
        chkShowPassword = (CheckBox) findViewById(R.id.chkShowPassword);
        txtLoginMsgText = (TextView) findViewById(R.id.txtLoginMsgText);

        //Xml Binding - Forget password
        txtForgetPassword = (TextView) findViewById(R.id.txtForgetPassword);


        //set Font
         txtLoginMsgText.setTypeface(AppController.getDefaultFont(AuthActivity.this));


        txtLoginEmail.setTypeface(AppController.getDefaultFont(AuthActivity.this));
        txtLoginPassword.setTypeface(AppController.getDefaultFont(AuthActivity.this));
        btnLogin.setTypeface(AppController.getDefaultBoldFont(AuthActivity.this));
        txtForgetPassword.setTypeface(AppController.getDefaultFont(AuthActivity.this));

        //Set OnClick Listener
          btnLogin.setOnClickListener(this);
        txtForgetPassword.setOnClickListener(this);

        //Add onCheckedListener
        chkShowPassword.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (!isChecked) {
                    //Show password
                    txtLoginPassword.setTransformationMethod(PasswordTransformationMethod.getInstance());
                } else {
                    //Hide password
                    txtLoginPassword.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                }
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {

            case R.id.btnLogin:
                if (AppController.isConnectingToInternet(AuthActivity.this)) {
                    if (!Validation.hasMobileText(txtLoginEmail)) {
                    } else if (!Validation.hasText(txtLoginPassword)) {
                    } else {
                        userLogin();
                    }
                } else{
                    Toast.makeText(AuthActivity.this, "Please connect to internet", Toast.LENGTH_SHORT).show();
                }

            break;

            default:
                break;
        }
    }

    public void userLogin() {
        try {
            progressBar.setVisibility(View.VISIBLE);
            StringRequest strReq = new StringRequest(Request.Method.POST, getString(R.string.defaultURL), new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    try {
                        progressBar.setVisibility(View.GONE);
                        JSONObject jsonObject = new JSONObject(response);
                        if (jsonObject.getString(getString(R.string.tagStatus)).equals(getString(R.string.tagStatusValue))) {
                            JSONObject jsonObjectData = jsonObject.getJSONObject(getString(R.string.tagResult));
                            String strOTP = jsonObjectData.getString(getString(R.string.tagOTP));
                            if(strOTP.equals("0")){
                                AppController.getSharedPrefEditor(AuthActivity.this).putBoolean(getString(R.string.tagIsLoginUser), true).commit();
                                AppController.getSharedPrefEditor(AuthActivity.this).putString(getString(R.string.tagUserId), jsonObjectData.getString(getString(R.string.tagUserId))).commit();
                                AppController.getSharedPrefEditor(AuthActivity.this).putString(getString(R.string.tagGroupType), jsonObjectData.getString(getString(R.string.tagGroupType))).commit();
                                AppController.getSharedPrefEditor(AuthActivity.this).putString(getString(R.string.tagIsProfile), jsonObjectData.getString(getString(R.string.tagIsProfile))).commit();
                                AppController.getSharedPrefEditor(AuthActivity.this).putString(getString(R.string.tagModelId), jsonObjectData.getString(getString(R.string.tagModelId))).commit();

                                Toast.makeText(AuthActivity.this, jsonObject.getString(getString(R.string.tagSuccessMsg)), Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent(AuthActivity.this, MainActivity.class);
                                startActivity(intent);
                                finish();
                            }else {
                                AppController.getSharedPrefEditor(AuthActivity.this).putBoolean(getString(R.string.tagIsLoginUser), true).commit();
                                AppController.getSharedPrefEditor(AuthActivity.this).putString(getString(R.string.tagUserId), jsonObjectData.getString(getString(R.string.tagUserId))).commit();
                                AppController.getSharedPrefEditor(AuthActivity.this).putString(getString(R.string.tagGroupType), jsonObjectData.getString(getString(R.string.tagGroupType))).commit();
                                AppController.getSharedPrefEditor(AuthActivity.this).putString(getString(R.string.tagIsProfile), jsonObjectData.getString(getString(R.string.tagIsProfile))).commit();
                                AppController.getSharedPrefEditor(AuthActivity.this).putString(getString(R.string.tagModelId), jsonObjectData.getString(getString(R.string.tagModelId))).commit();

                                Toast.makeText(AuthActivity.this, jsonObject.getString(getString(R.string.tagSuccessMsg)), Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent(AuthActivity.this, MainActivity.class);
                                startActivity(intent);
                                finish();
                            }
                        } else {
                            Toast.makeText(AuthActivity.this, jsonObject.getString(getString(R.string.tagSuccessMsg)), Toast.LENGTH_SHORT).show();
                        }
                    } catch (Exception e) {
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
                    params.put(getString(R.string.tag), "login");
                    params.put("mobile", txtLoginEmail.getText().toString().trim());
                    params.put("password", txtLoginPassword.getText().toString().trim());
                    return params;
                }
            };
            strReq.setRetryPolicy(new DefaultRetryPolicy(10000, 2, DefaultRetryPolicy.DEFAULT_TIMEOUT_MS));
            AppController.getInstance().addToRequestQueue(strReq);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
