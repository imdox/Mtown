package com.mtown.app.user;

import android.content.ClipData;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Gallery;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
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
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.mtown.app.R;
import com.mtown.app.dao.ModelDAO;
import com.mtown.app.home.MainActivity;
import com.mtown.app.support.AppController;
import com.mtown.app.support.GalleryImageAdapter;
import com.mtown.app.support.Validation;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class AddEditUserActivity extends AppCompatActivity {

    private RadioGroup radioSexGroup;
    private RadioButton radioSexButton;
    private EditText edtFirstName,edtLastName,txtMobile,txtEmail,edtAbout,edtHeight,edtWeight,edtSkinColor,edtEyeColor,edtExperience,
            edtDesignation,edtAge,txtLanguages;
    private Button btnAddEditProfile,btnProfileImage,btnSelectCover;

    private int isUpdate = 0;
    private ProgressBar progressBar;
    private RadioButton radioMale,radioFemale;
    private int REQ_CODE = 100;
    private Gallery gallery;
    private Uri mImageUri;
    private JSONObject jsonObject;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_edit_user);

        progressBar = (ProgressBar)findViewById(R.id.progressBar);
        btnAddEditProfile = findViewById(R.id.btnAddEditProfile);
        btnProfileImage = findViewById(R.id.btnProfileImage);
        btnSelectCover = findViewById(R.id.btnSelectCover);

        edtFirstName = findViewById(R.id.edtFirstName);
        edtLastName = findViewById(R.id.edtLastName);
        txtMobile = findViewById(R.id.txtMobile);
        txtEmail = findViewById(R.id.txtEmail);
        edtAbout = findViewById(R.id.edtAbout);
        edtHeight = findViewById(R.id.edtHeight);
        edtWeight = findViewById(R.id.edtWeight);
        edtSkinColor = findViewById(R.id.edtSkinColor);
        edtEyeColor = findViewById(R.id.edtEyeColor);
        edtExperience = findViewById(R.id.edtExperience);
        edtDesignation = findViewById(R.id.edtDesignation);
        edtAge = findViewById(R.id.edtAge);
        txtLanguages = findViewById(R.id.txtLanguages);
        imgData = (ImageView)findViewById(R.id.selectedImg);

        edtFirstName.setTypeface(AppController.getDefaultFont(AddEditUserActivity.this));
        edtLastName.setTypeface(AppController.getDefaultFont(AddEditUserActivity.this));
        txtMobile.setTypeface(AppController.getDefaultFont(AddEditUserActivity.this));
        txtEmail.setTypeface(AppController.getDefaultFont(AddEditUserActivity.this));
        edtAbout.setTypeface(AppController.getDefaultFont(AddEditUserActivity.this));
        edtHeight.setTypeface(AppController.getDefaultFont(AddEditUserActivity.this));
        edtWeight.setTypeface(AppController.getDefaultFont(AddEditUserActivity.this));
        edtSkinColor.setTypeface(AppController.getDefaultFont(AddEditUserActivity.this));
        edtEyeColor.setTypeface(AppController.getDefaultFont(AddEditUserActivity.this));
        edtExperience.setTypeface(AppController.getDefaultFont(AddEditUserActivity.this));
        edtDesignation.setTypeface(AppController.getDefaultFont(AddEditUserActivity.this));
        edtAge.setTypeface(AppController.getDefaultFont(AddEditUserActivity.this));
        txtLanguages.setTypeface(AppController.getDefaultFont(AddEditUserActivity.this));
        btnAddEditProfile.setTypeface(AppController.getDefaultBoldFont(AddEditUserActivity.this));
        btnProfileImage.setTypeface(AppController.getDefaultBoldFont(AddEditUserActivity.this));
        btnSelectCover.setTypeface(AppController.getDefaultBoldFont(AddEditUserActivity.this));

        radioMale  = (RadioButton) findViewById(R.id.radioMale);
        radioFemale  = (RadioButton) findViewById(R.id.radioFemale);

        radioMale.setTypeface(AppController.getDefaultFont(AddEditUserActivity.this));
        radioFemale.setTypeface(AppController.getDefaultFont(AddEditUserActivity.this));

        radioSexGroup = findViewById(R.id.radioSex);

        btnProfileImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                isUserProfile=true;
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, "Choose application"), REQ_CODE);
            }
        });

        btnSelectCover.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                isUserProfile=false;
                Intent intentData = new Intent();
                intentData.setType("image/*");
                intentData.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
                intentData.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intentData, "Choose application"), REQ_CODE);
            }
        });

        btnAddEditProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // get selected radio button from radioGroup
                int selectedId = radioSexGroup.getCheckedRadioButtonId();
                // find the radiobutton by returned id
                radioSexButton =  findViewById(selectedId);
                boolean isValidData = false;

                if (AppController.isConnectingToInternet(AddEditUserActivity.this)) {
                    if (!Validation.hasMobileText(txtMobile)) {
                    } else if (!Validation.hasText(edtFirstName)) {
                    } else if (!Validation.hasText(edtLastName)) {
                    } else if (!Validation.hasText(txtEmail)) {
                    } else if (!Validation.hasText(edtAbout)) {
                    } else if (!Validation.hasText(edtHeight)) {
                    } else if (!Validation.hasText(edtWeight)) {
                    } else if (!Validation.hasText(edtSkinColor)) {
                    } else if (!Validation.hasText(edtEyeColor)) {
                    } else if (!Validation.hasText(edtExperience)) {
                    } else if (!Validation.hasText(edtDesignation)) {
                    } else if (!Validation.hasText(txtLanguages)) {
                    } else if (!Validation.hasText(edtAge)) {
                    } else {
                        createUpdatePI();
                    }
                } else{
                    Toast.makeText(AddEditUserActivity.this, "Please connect to internet", Toast.LENGTH_SHORT).show();
                }
            }
        });
        //get Intent
        Intent intentData = getIntent();
        isUpdate = intentData.getIntExtra(getString(R.string.tagIndex),0);

        if(isUpdate==1){
            ArrayList<ModelDAO> modelDAOS = MainActivity.profileDetails;
            edtFirstName.setText(modelDAOS.get(0).getFirstname());
            edtLastName.setText(modelDAOS.get(0).getLastname());
            txtMobile.setText(modelDAOS.get(0).getMobile());
            txtEmail.setText(modelDAOS.get(0).getEmail());
            edtAbout.setText(modelDAOS.get(0).getAbout_you());
            edtHeight.setText(modelDAOS.get(0).getHeight());
            edtWeight.setText(modelDAOS.get(0).getWeight());
            edtSkinColor.setText(modelDAOS.get(0).getSkin_color());
            edtEyeColor.setText(modelDAOS.get(0).getEye_color());
            edtExperience.setText(modelDAOS.get(0).getExperience());
            edtDesignation.setText(modelDAOS.get(0).getDesignation());
            edtAge.setText(modelDAOS.get(0).getAge());
            txtLanguages.setText(modelDAOS.get(0).getKnown_languages());
            if(modelDAOS.get(0).getGender().equals("Male")){
                radioMale.setChecked(true);
                radioFemale.setChecked(false);
            }else{
                radioMale.setChecked(false);
                radioFemale.setChecked(true);
            }
            if(modelDAOS.get(0).getProfile_image().length()>5){
                Glide.with(AddEditUserActivity.this).load(modelDAOS.get(0).getProfile_image()).into(imgData);
            }
        }

        jsonObject = new JSONObject();
        encodedCImageListold = new ArrayList<>();
        encodedPImageList = new ArrayList<>();

    }

    private ArrayList<Uri> imagesCUriList;
    private ArrayList<Uri> imagesPUriList;
    private ArrayList<String> encodedCImageListold;
    private ArrayList<String> encodedPImageList;
    private ArrayList<Uri> mArrayUri;
    private String imageURI;
    private boolean isUserProfile=true;
    private ImageView imgData;

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        try {
            // When an Image is picked
            if (requestCode == REQ_CODE && resultCode == RESULT_OK
                    && null != data) {
                // Get the Image from data



                if(isUserProfile){
                    String[] filePathColumnP = { MediaStore.Images.Media.DATA };
                    imagesPUriList = new ArrayList<Uri>();
                    encodedPImageList.clear();
                    if(data.getData()!=null){

                        mImageUri=data.getData();

                        // Get the cursor
                        Cursor cursor = getContentResolver().query(mImageUri,
                                filePathColumnP, null, null, null);
                        // Move to first row
                        cursor.moveToFirst();

                        int columnIndex = cursor.getColumnIndex(filePathColumnP[0]);
                        imageURI  = cursor.getString(columnIndex);
                        Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), mImageUri);
                        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                        bitmap.compress(Bitmap.CompressFormat.JPEG, 80, byteArrayOutputStream);
                        String encodedImage = Base64.encodeToString(byteArrayOutputStream.toByteArray(), Base64.DEFAULT);
                        encodedPImageList.add(encodedImage);
                        cursor.close();
                        imgData.setImageURI(mImageUri);
                    }else {
                        Toast.makeText(this, "You haven't picked Image", Toast.LENGTH_LONG).show();
                    }
                }else{
                    String[] filePathColumnC = { MediaStore.Images.Media.DATA };
                    imagesCUriList = new ArrayList<Uri>();
                    encodedCImageListold.clear();
                    if (data.getClipData() != null) {
                        ClipData mClipData = data.getClipData();
                        mArrayUri = new ArrayList<Uri>();
                        if((mClipData.getItemCount()>5) || (mClipData.getItemCount()<5) ){
                            Toast.makeText(this, "Please select 5 images.", Toast.LENGTH_LONG).show();
                            return;
                        }

                        for (int i = 0; i < mClipData.getItemCount(); i++) {

                            ClipData.Item item = mClipData.getItemAt(i);
                            Uri uri = item.getUri();
                            mArrayUri.add(uri);
                            // Get the cursor
                            Cursor cursor = getContentResolver().query(uri, filePathColumnC, null, null, null);
                            // Move to first row
                            cursor.moveToFirst();

                            int columnIndex = cursor.getColumnIndex(filePathColumnC[0]);
                            imageURI  = cursor.getString(columnIndex);
                            Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), uri);
                            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                            bitmap.compress(Bitmap.CompressFormat.JPEG, 80, byteArrayOutputStream);
                            String encodedImage = Base64.encodeToString(byteArrayOutputStream.toByteArray(), Base64.DEFAULT);
                            encodedCImageListold.add(encodedImage);
                            cursor.close();
                        }
                        gallery = (Gallery) findViewById(R.id.gallery);
                        gallery.setSpacing(2);
                        final GalleryImageAdapter galleryImageAdapter= new GalleryImageAdapter(this,mArrayUri);
                        gallery.setAdapter(galleryImageAdapter);
                    }
                }
            } else {
                Toast.makeText(this, "You haven't picked Image", Toast.LENGTH_LONG).show();
            }
        } catch (Exception e) {
        }

        super.onActivityResult(requestCode, resultCode, data);
    }

   /* private void createUpdateProfile(){
        try {
            progressBar.setVisibility(View.VISIBLE);
            StringRequest strReq = new StringRequest(Request.Method.POST, getString(R.string.defaultURL), new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    try {
                        progressBar.setVisibility(View.GONE);
                        JSONObject jsonObject = new JSONObject(response);
                        if(jsonObject.getString(getString(R.string.tagStatus)).equals(getString(R.string.tagStatusValue))){
                            AppController.getSharedPrefEditor(AddEditUserActivity.this).putString(getString(R.string.tagIsProfile), "1").commit();
                            AppController.getSharedPrefEditor(AddEditUserActivity.this).putString(getString(R.string.tagModelId), jsonObject.getString(getString(R.string.tagModelId))).commit();
                            Toast.makeText(AddEditUserActivity.this, jsonObject.getString(getString(R.string.tagSuccessMsg)), Toast.LENGTH_SHORT).show();
                            finish();
                        } else {
                            Toast.makeText(AddEditUserActivity.this, jsonObject.getString(getString(R.string.tagSuccessMsg)),Toast.LENGTH_LONG).show();
                        }
                    } catch (Exception e) {
                        Toast.makeText(AddEditUserActivity.this, "Oops! Something went wrong.",Toast.LENGTH_LONG).show();
                        e.printStackTrace();
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Toast.makeText(AddEditUserActivity.this, "Oops! Something went wrong.",Toast.LENGTH_LONG).show();
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
                    params.put(getString(R.string.tag), "create_update_model");
                    params.put("id", AppController.getSharedPref(AddEditUserActivity.this).getString(getString(R.string.tagModelId),"0"));
                    params.put("firstname", edtFirstName.getText().toString().trim());
                    params.put("lastname", edtLastName.getText().toString().trim());
                    params.put("mobile", txtMobile.getText().toString().trim());
                    params.put("about_you", edtAbout.getText().toString().trim());
                    params.put("profile_image", "");
                    params.put("model_images", "");
                    params.put("email", txtEmail.getText().toString().trim());
                    params.put("age", edtAge.getText().toString().trim());
                    params.put("gender", radioSexButton.getText().toString().trim());
                    params.put("experience", edtExperience.getText().toString().trim());
                    params.put("designation", edtDesignation.getText().toString().trim());
                    params.put("height", edtHeight.getText().toString().trim());
                    params.put("weight", edtWeight.getText().toString().trim());
                    params.put("skin_color", edtSkinColor.getText().toString().trim());
                    params.put("eye_color", edtEyeColor.getText().toString().trim());
                    params.put("known_languages", txtLanguages.getText().toString().trim());
                    params.put("user_id", AppController.getSharedPref(AddEditUserActivity.this).getString(getString(R.string.tagUserId),""));
                  return params;
                }
            };
            strReq.setRetryPolicy(new DefaultRetryPolicy(10000, 2, DefaultRetryPolicy.DEFAULT_TIMEOUT_MS));
            AppController.getInstance().addToRequestQueue(strReq);
        } catch (Exception e) {
        }
    }
*/
    private void createUpdatePI(){
        try{
            progressBar.setVisibility(View.VISIBLE);
            JSONArray jsonCArray = new JSONArray();
            JSONArray jsonPArray = new JSONArray();
            if (encodedPImageList.isEmpty()){
                Toast.makeText(this, "Please select profile image first.", Toast.LENGTH_SHORT).show();
                return;
            }

            if (encodedCImageListold.isEmpty()){
                Toast.makeText(this, "Please select 5 cover images.", Toast.LENGTH_SHORT).show();
                return;
            }

            for (String encoded: encodedPImageList){
                jsonPArray.put(encoded);
            }

            for (String encoded: encodedCImageListold){
                jsonCArray.put(encoded);
            }

            try {
                jsonObject.put("profile_image", jsonPArray);
                jsonObject.put("model_images", jsonCArray);
                jsonObject.put(getString(R.string.tag), "create_update_model");
                jsonObject.put("id", AppController.getSharedPref(AddEditUserActivity.this).getString(getString(R.string.tagModelId),"0"));
                jsonObject.put("firstname", edtFirstName.getText().toString().trim());
                jsonObject.put("lastname", edtLastName.getText().toString().trim());
                jsonObject.put("mobile", txtMobile.getText().toString().trim());
                jsonObject.put("about_you", edtAbout.getText().toString().trim());
                jsonObject.put("profile_image", "");
                jsonObject.put("model_images", "");
                jsonObject.put("email", txtEmail.getText().toString().trim());
                jsonObject.put("age", edtAge.getText().toString().trim());
                jsonObject.put("gender", radioSexButton.getText().toString().trim());
                jsonObject.put("experience", edtExperience.getText().toString().trim());
                jsonObject.put("designation", edtDesignation.getText().toString().trim());
                jsonObject.put("height", edtHeight.getText().toString().trim());
                jsonObject.put("weight", edtWeight.getText().toString().trim());
                jsonObject.put("skin_color", edtSkinColor.getText().toString().trim());
                jsonObject.put("eye_color", edtEyeColor.getText().toString().trim());
                jsonObject.put("known_languages", txtLanguages.getText().toString().trim());
                jsonObject.put("user_id", AppController.getSharedPref(AddEditUserActivity.this).getString(getString(R.string.tagUserId),""));

            } catch (JSONException e) {
                Log.e("JSONObject Here", e.toString());
            }
            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, getString(R.string.defaultURL), jsonObject,
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject jsonObject) {
                            try {
                                progressBar.setVisibility(View.GONE);
                                if(jsonObject.getString(getString(R.string.tagStatus)).equals(getString(R.string.tagStatusValue))){
                                    AppController.getSharedPrefEditor(AddEditUserActivity.this).putString(getString(R.string.tagIsProfile), "1").commit();
                                    AppController.getSharedPrefEditor(AddEditUserActivity.this).putString(getString(R.string.tagModelId), jsonObject.getString(getString(R.string.tagModelId))).commit();
                                    Toast.makeText(AddEditUserActivity.this, jsonObject.getString(getString(R.string.tagSuccessMsg)), Toast.LENGTH_SHORT).show();
                                    finish();
                                } else {
                                    Toast.makeText(AddEditUserActivity.this, jsonObject.getString(getString(R.string.tagSuccessMsg)),Toast.LENGTH_LONG).show();
                                }
                            } catch (Exception e) {
                                Toast.makeText(AddEditUserActivity.this, "Oops! Something went wrong.",Toast.LENGTH_LONG).show();
                                e.printStackTrace();
                            }
                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError volleyError) {
                    Log.e("Error",volleyError.toString());
                    progressBar.setVisibility(View.GONE);
                    Toast.makeText(getApplication(), "Error in uploading images.", Toast.LENGTH_SHORT).show();
                }
            });
            jsonObjectRequest.setRetryPolicy(new DefaultRetryPolicy( 200*30000,
                    DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                    DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
            Volley.newRequestQueue(this).add(jsonObjectRequest);
        }catch (Exception e){
        }
    }
}
