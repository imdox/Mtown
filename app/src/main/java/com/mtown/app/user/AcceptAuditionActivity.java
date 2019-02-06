package com.mtown.app.user;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.ClipData;
import android.content.DialogInterface;
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
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Gallery;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
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
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.mtown.app.R;
import com.mtown.app.admin.RequestListActivity;
import com.mtown.app.dao.AuditionReqDAO;
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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class AcceptAuditionActivity extends AppCompatActivity {

   private TextView txtGalleryH;
    private EditText editUserComment;
    private Button btnSelectImages, btnAcceptRequest;

    private ProgressBar progressBar;
    private int REQ_CODE = 100;
    private Gallery gallery;
    private Uri mImageUri;
    private JSONObject jsonObject;
    private int position;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_accept_audition);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setHomeButtonEnabled(true);
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        //get Intent
        Intent intentData = getIntent();
        position = intentData.getIntExtra(getString(R.string.tagIndex),0);

        progressBar = (ProgressBar)findViewById(R.id.progressBar);
        btnAcceptRequest = findViewById(R.id.btnAcceptRequest);
        btnSelectImages = findViewById(R.id.btnSelectImages);
        editUserComment = findViewById(R.id.editUserComment);

        txtGalleryH = findViewById(R.id.txtGalleryH);

        editUserComment.setTypeface(AppController.getDefaultFont(AcceptAuditionActivity.this));

        txtGalleryH.setTypeface(AppController.getDefaultBoldFont(AcceptAuditionActivity.this));
        btnSelectImages.setTypeface(AppController.getDefaultBoldFont(AcceptAuditionActivity.this));
        btnAcceptRequest.setTypeface(AppController.getDefaultBoldFont(AcceptAuditionActivity.this));

        btnSelectImages.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intentData = new Intent();
                intentData.setType("image/*");
                intentData.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
                intentData.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intentData, "Choose application"), REQ_CODE);
            }
        });

        btnAcceptRequest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                try{
                    if (AppController.isConnectingToInternet(AcceptAuditionActivity.this)) {
                        jsonCArray = new JSONArray();
                        try{
                            for (String encoded: encodedCImageList){
                                jsonCArray.put(encoded);
                            }
                        }catch (Exception e){
                        }
                        if (!Validation.hasMobileText(editUserComment)) {
                            editUserComment.setError("Please enter your comment");
                        } else if (jsonCArray.length()!=5) {
                            Toast.makeText(AcceptAuditionActivity.this, "Please select 5 images", Toast.LENGTH_SHORT).show();
                        } else {
                            try{
                                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(AcceptAuditionActivity.this);
                                alertDialogBuilder.setMessage("Are you sure, You want to accept?");
                                alertDialogBuilder.setPositiveButton("yes",
                                        new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface arg0, int arg1) {
                                                arg0.dismiss();
                                                AuditionReqDAO auditionReqDAO = RequestListActivity.auditionReqDAOS.get(position);
                                                acceptRequest(auditionReqDAO.getModel_id(),auditionReqDAO.getAudition_id(),"1",editUserComment.getText().toString().trim(),position);
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
                            }catch (Exception e){
                            }}
                    } else{
                        Toast.makeText(AcceptAuditionActivity.this, "Please connect to internet", Toast.LENGTH_SHORT).show();
                    }
                }catch (Exception e){
                }
            }
        });

        jsonObject = new JSONObject();
        encodedCImageList = new ArrayList<>();
    }

    private ArrayList<Uri> imagesCUriList;
    private ArrayList<String> encodedCImageList;
    private ArrayList<Uri> mArrayUri;
    private String imageURI;
    private ArrayList<String> extension;

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        try {
            // When an Image is picked
            if (requestCode == REQ_CODE && resultCode == RESULT_OK
                    && null != data) {
                // Get the Image from data

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
            } else {
                Toast.makeText(this, "You haven't choose any images.", Toast.LENGTH_LONG).show();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        super.onActivityResult(requestCode, resultCode, data);
    }

    private void acceptRequest(final String strModelId,final String strAuditionId,final String strConfirmationId,final String strComment,final int position){
        try{
            progressBar.setVisibility(View.VISIBLE);
            try {
                String strExt = extension.toString().replace("[","");
                jsonObject.put(getString(R.string.tag), "update_audition_detail");
                jsonObject.put("model_id", strModelId);
                jsonObject.put("audition_id", strAuditionId);
                jsonObject.put("confirmation", strConfirmationId);
                jsonObject.put("comment", strComment);
                jsonObject.put("audition_images", jsonCArray);
                jsonObject.put("audition_images_ext", strExt.replace("]",""));

            } catch (JSONException e) {
            }

            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, getString(R.string.defaultURL), jsonObject,
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject jsonObject) {
                            try {
                                progressBar.setVisibility(View.GONE);
                                if(jsonObject.getString(getString(R.string.tagStatus)).equals(getString(R.string.tagStatusValue))){
                                    RequestListActivity.auditionReqDAOS.get(position).setMt_conformation(strConfirmationId);
                                    RequestListActivity.auditionReqDAOS.get(position).setMt_comment(strComment);
                                    Toast.makeText(AcceptAuditionActivity.this, jsonObject.getString(getString(R.string.tagSuccessMsg)),Toast.LENGTH_LONG).show();
                                    finish();
                                } else {
                                    Toast.makeText(AcceptAuditionActivity.this, jsonObject.getString(getString(R.string.tagSuccessMsg)),Toast.LENGTH_LONG).show();
                                }
                            } catch (Exception e) {
                                Toast.makeText(AcceptAuditionActivity.this, "Oops! Something went wrong.",Toast.LENGTH_LONG).show();
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
    private JSONArray jsonCArray;
}
