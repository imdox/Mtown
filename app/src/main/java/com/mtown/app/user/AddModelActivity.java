package com.mtown.app.user;

import android.app.DatePickerDialog;
import android.content.ClipData;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Gallery;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.mtown.app.R;
import com.mtown.app.dao.ModelDAO;
import com.mtown.app.home.MainActivity;
import com.mtown.app.support.AppController;
import com.mtown.app.support.GalleryImageAdapter;
import com.mtown.app.support.GalleryURLImageAdapter;
import com.mtown.app.support.Validation;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.util.ArrayList;
import java.util.Calendar;

public class AddModelActivity extends AppCompatActivity {

    private RadioGroup radioSexGroup;
    private RadioButton radioSexButton;
    private EditText edtFirstName,edtLastName,txtEmail,edtAbout,edtHeight,edtWeight,edtSkinColor,edtEyeColor,edtExperience,
            edtDesignation,txtLanguages,txtLoginEmail,txtLoginPassword;
    //txtMobile
    private Button btnAddEditProfile,btnProfileImage,btnSelectCover;
    private TextView txtDate;

    private int isUpdate = 0;
    private ProgressBar progressBar;
    private RadioButton radioMale,radioFemale;
    private int REQ_CODE = 100;
    private Gallery gallery;
    private Uri mImageUri;
    private JSONObject jsonObject;
    private int mYear, mMonth, mDay;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_model);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setHomeButtonEnabled(true);
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        progressBar = (ProgressBar)findViewById(R.id.progressBar);
        btnAddEditProfile = findViewById(R.id.btnAddEditProfile);
        btnProfileImage = findViewById(R.id.btnProfileImage);
        btnSelectCover = findViewById(R.id.btnSelectCover);

        edtFirstName = findViewById(R.id.edtFirstName);
        edtLastName = findViewById(R.id.edtLastName);
        //txtMobile = findViewById(R.id.txtMobile);
        txtEmail = findViewById(R.id.txtEmail);
        edtAbout = findViewById(R.id.edtAbout);
        edtHeight = findViewById(R.id.edtHeight);
        edtWeight = findViewById(R.id.edtWeight);
        edtSkinColor = findViewById(R.id.edtSkinColor);
        edtEyeColor = findViewById(R.id.edtEyeColor);
        edtExperience = findViewById(R.id.edtExperience);
        edtDesignation = findViewById(R.id.edtDesignation);
        txtDate = findViewById(R.id.edtAge);
        txtLanguages = findViewById(R.id.txtLanguages);
        txtLoginEmail = findViewById(R.id.txtLoginEmail);
        txtLoginPassword = findViewById(R.id.txtLoginPassword);

        txtDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    final Calendar c = Calendar.getInstance();
                    mYear = c.get(Calendar.YEAR);
                    mMonth = c.get(Calendar.MONTH);
                    mDay = c.get(Calendar.DAY_OF_MONTH);


                    DatePickerDialog datePickerDialog = new DatePickerDialog(AddModelActivity.this,
                            new DatePickerDialog.OnDateSetListener() {

                                @Override
                                public void onDateSet(DatePicker view, int year,int monthOfYear, int dayOfMonth) {
                                    txtDate.setText(dayOfMonth + "-" + (monthOfYear + 1) + "-" + year);
                                }
                            }, mYear, mMonth, mDay);
                    datePickerDialog.show();
                }catch (Exception e){
                }
            }
        });


        edtFirstName.setTypeface(AppController.getDefaultFont(AddModelActivity.this));
        edtLastName.setTypeface(AppController.getDefaultFont(AddModelActivity.this));
        //txtMobile.setTypeface(AppController.getDefaultFont(AddModelActivity.this));
        txtEmail.setTypeface(AppController.getDefaultFont(AddModelActivity.this));
        edtAbout.setTypeface(AppController.getDefaultFont(AddModelActivity.this));
        edtHeight.setTypeface(AppController.getDefaultFont(AddModelActivity.this));
        edtWeight.setTypeface(AppController.getDefaultFont(AddModelActivity.this));
        edtSkinColor.setTypeface(AppController.getDefaultFont(AddModelActivity.this));
        edtEyeColor.setTypeface(AppController.getDefaultFont(AddModelActivity.this));
        edtExperience.setTypeface(AppController.getDefaultFont(AddModelActivity.this));
        edtDesignation.setTypeface(AppController.getDefaultFont(AddModelActivity.this));
        txtDate.setTypeface(AppController.getDefaultFont(AddModelActivity.this));
        txtLanguages.setTypeface(AppController.getDefaultFont(AddModelActivity.this));
        txtLoginEmail.setTypeface(AppController.getDefaultFont(AddModelActivity.this));
        txtLoginPassword.setTypeface(AppController.getDefaultFont(AddModelActivity.this));
        btnAddEditProfile.setTypeface(AppController.getDefaultBoldFont(AddModelActivity.this));
        btnProfileImage.setTypeface(AppController.getDefaultBoldFont(AddModelActivity.this));
        btnSelectCover.setTypeface(AppController.getDefaultBoldFont(AddModelActivity.this));

        radioMale  = (RadioButton) findViewById(R.id.radioMale);
        radioFemale  = (RadioButton) findViewById(R.id.radioFemale);

        radioMale.setTypeface(AppController.getDefaultFont(AddModelActivity.this));
        radioFemale.setTypeface(AppController.getDefaultFont(AddModelActivity.this));

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

                if (AppController.isConnectingToInternet(AddModelActivity.this)) {
                  /*  if (!Validation.hasMobileText(txtMobile)) {
                    } else*/

                        if (!Validation.hasText(edtFirstName)) {
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
                    }else {
                        createUpdatePI();
                    }
                } else{
                    Toast.makeText(AddModelActivity.this, "Please connect to internet", Toast.LENGTH_SHORT).show();
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
            txtEmail.setText(modelDAOS.get(0).getEmail());
            edtAbout.setText(modelDAOS.get(0).getAbout_you());
            edtHeight.setText(modelDAOS.get(0).getHeight());
            edtWeight.setText(modelDAOS.get(0).getWeight());
            edtSkinColor.setText(modelDAOS.get(0).getSkin_color());
            edtEyeColor.setText(modelDAOS.get(0).getEye_color());
            edtExperience.setText(modelDAOS.get(0).getExperience());
            edtDesignation.setText(modelDAOS.get(0).getDesignation());
            txtDate.setText(modelDAOS.get(0).getAge());
            txtLanguages.setText(modelDAOS.get(0).getKnown_languages());
            if(modelDAOS.get(0).getGender().equals("Male")){
                radioMale.setChecked(true);
                radioFemale.setChecked(false);
            }else{
                radioMale.setChecked(false);
                radioFemale.setChecked(true);
            }
            if(modelDAOS.get(0).getProfile_image().length()>5){
                imgData = (ImageView)findViewById(R.id.selectedImg);
                Glide.with(AddModelActivity.this).load(modelDAOS.get(0).getProfile_image().toString().trim())
                        .into(imgData);
            }
            if(modelDAOS.get(0).getModel_images().length>4){
                gallery = (Gallery) findViewById(R.id.gallery);
                gallery.setSpacing(5);
                final GalleryURLImageAdapter galleryImageAdapter = new GalleryURLImageAdapter(this,modelDAOS.get(0).getModel_images());
                gallery.setAdapter(galleryImageAdapter);
            }
            btnAddEditProfile.setText("Update Profile");
        }

        //txtMobile.setText(AppController.getSharedPref(AddModelActivity.this).getString("mobile",""));
        jsonObject = new JSONObject();
        encodedCImageList = new ArrayList<>();
        encodedPImageList = new ArrayList<>();
    }

    private ArrayList<Uri> imagesCUriList;
    private ArrayList<Uri> imagesPUriList;
    private ArrayList<String> encodedCImageList;
    private ArrayList<String> encodedPImageList;
    private ArrayList<Uri> mArrayUri;
    private String imageURI;
    private boolean isUserProfile=true;
    private ImageView imgData;
    private String profileExt;
    private ArrayList<String> extension;

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
                       // double size = imageURI.length() / 1024;
                        //Toast.makeText(AddEditUserActivity.this,"Size : " +String.valueOf(size),Toast.LENGTH_SHORT).show();
                        profileExt = imageURI.substring(imageURI.lastIndexOf("."));
                        Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), mImageUri);
                        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                        bitmap.compress(Bitmap.CompressFormat.JPEG, 80, byteArrayOutputStream);
                        String encodedImage = Base64.encodeToString(byteArrayOutputStream.toByteArray(), Base64.DEFAULT);
                        encodedPImageList.add(encodedImage);
                        cursor.close();
                        imgData = (ImageView)findViewById(R.id.selectedImg);
                        imgData.setImageURI(mImageUri);
                    }else {
                        Toast.makeText(this, "You haven't choose profile image", Toast.LENGTH_LONG).show();
                    }
                }else{
                    String[] filePathColumnC = { MediaStore.Images.Media.DATA };
                    imagesCUriList = new ArrayList<Uri>();
                    extension = new ArrayList<String>();
                    encodedCImageList.clear();
                    if (data.getClipData() != null) {
                        ClipData mClipData = data.getClipData();
                        mArrayUri = new ArrayList<Uri>();
                        if((mClipData.getItemCount()>5) || (mClipData.getItemCount()<5) ){
                            Toast.makeText(this, "You can choose only 5 images.", Toast.LENGTH_LONG).show();
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
                            File file = new File(imageURI);
                            if(file.exists()) {
                                double bytes = file.length();
                                double kilobytes = (bytes / 1024);
                                Toast.makeText(AddModelActivity.this,"file size : "+kilobytes,Toast.LENGTH_SHORT).show();
                            }
                            extension.add(imageURI.substring(imageURI.lastIndexOf(".")));
                            Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), uri);
                            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                            bitmap.compress(Bitmap.CompressFormat.JPEG, 80, byteArrayOutputStream);
                            String encodedImage = Base64.encodeToString(byteArrayOutputStream.toByteArray(), Base64.DEFAULT);
                            encodedCImageList.add(encodedImage);
                            cursor.close();
                        }
                        gallery = (Gallery) findViewById(R.id.gallery);
                        gallery.setSpacing(5);
                        final GalleryImageAdapter galleryImageAdapter= new GalleryImageAdapter(this,mArrayUri);
                        gallery.setAdapter(galleryImageAdapter);
                    }
                }
            } else {
                Toast.makeText(this, "You haven't choose any images.", Toast.LENGTH_LONG).show();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        super.onActivityResult(requestCode, resultCode, data);
    }

    private void createUpdatePI(){
        try{
            progressBar.setVisibility(View.VISIBLE);
            JSONArray jsonCArray = new JSONArray();
            JSONArray jsonPArray = new JSONArray();

            try{
                for (String encoded: encodedPImageList){
                    jsonPArray.put(encoded);
                }

                for (String encoded: encodedCImageList){
                    jsonCArray.put(encoded);
                }
            }catch (Exception e){
            }

            try {
                String strExt = "";
                try{
                     strExt = extension.toString().replace("[","");
                }catch (Exception e){

                }
                jsonObject.put("profile_ext", profileExt);
                jsonObject.put("model_ext", strExt.replace("]",""));
                jsonObject.put("profile_image", jsonPArray);
                jsonObject.put("model_images", jsonCArray);
                jsonObject.put(getString(R.string.tag), "add_model_by_admin");
                jsonObject.put("id", AppController.getSharedPref(AddModelActivity.this).getString(getString(R.string.tagModelId),"0"));
                jsonObject.put("firstname", edtFirstName.getText().toString().trim());
                jsonObject.put("lastname", edtLastName.getText().toString().trim());
               // jsonObject.put("mobile", txtMobile.getText().toString().trim());
                jsonObject.put("about_you", edtAbout.getText().toString().trim());
                jsonObject.put("email", txtEmail.getText().toString().trim());
                jsonObject.put("age", txtDate.getText().toString().trim());
                jsonObject.put("gender", radioSexButton.getText().toString().trim());
                jsonObject.put("experience", edtExperience.getText().toString().trim());
                jsonObject.put("designation", edtDesignation.getText().toString().trim());
                jsonObject.put("height", edtHeight.getText().toString().trim());
                jsonObject.put("weight", edtWeight.getText().toString().trim());
                jsonObject.put("skin_color", edtSkinColor.getText().toString().trim());
                jsonObject.put("eye_color", edtEyeColor.getText().toString().trim());
                jsonObject.put("known_languages", txtLanguages.getText().toString().trim());
                jsonObject.put("user_id", AppController.getSharedPref(AddModelActivity.this).getString(getString(R.string.tagUserId),""));
                jsonObject.put("mobile", txtLoginEmail.getText().toString().trim());
                jsonObject.put("password", txtLoginPassword.getText().toString().trim());

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
                                    AppController.getSharedPrefEditor(AddModelActivity.this).putString(getString(R.string.tagIsProfile), "1").commit();
                                    AppController.getSharedPrefEditor(AddModelActivity.this).putString(getString(R.string.tagModelId), jsonObject.getString(getString(R.string.tagModelId))).commit();
                                    Toast.makeText(AddModelActivity.this, jsonObject.getString(getString(R.string.tagSuccessMsg)), Toast.LENGTH_SHORT).show();
                                    finish();
                                } else {
                                    Toast.makeText(AddModelActivity.this, jsonObject.getString(getString(R.string.tagSuccessMsg)),Toast.LENGTH_LONG).show();
                                }
                            } catch (Exception e) {
                                Toast.makeText(AddModelActivity.this, "Oops! Something went wrong.",Toast.LENGTH_LONG).show();
                                e.printStackTrace();
                            }
                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError volleyError) {
                    progressBar.setVisibility(View.GONE);
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
