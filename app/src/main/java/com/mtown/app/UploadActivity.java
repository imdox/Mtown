package com.mtown.app;

import android.app.ProgressDialog;
import android.content.ClipData;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
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
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.mtown.app.support.GalleryImageAdapter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;

public class UploadActivity extends AppCompatActivity implements View.OnClickListener{

    private TextView messageText, noImage;
    private Button btnProfile,btnImages,uploadButton;
    private EditText etxtUpload;
    private ProgressDialog dialog = null;
    private JSONObject jsonObject;
    private ArrayList<Uri> imagesUriList;
    private ArrayList<String> encodedImageList;
    private String imageURI;
    private ImageView imgData;
    private ArrayList<Uri> mArrayUri;
    private Gallery gallery;
    private boolean isUserProfile=true;
    private int REQCODE = 100;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload);

        imgData = (ImageView)findViewById(R.id.selectedImg);
        btnProfile = (Button)findViewById(R.id.button_profile);
        btnImages = (Button)findViewById(R.id.button_images);

        uploadButton = (Button)findViewById(R.id.uploadButton);
        messageText  = (TextView)findViewById(R.id.messageText);
        noImage  = (TextView)findViewById(R.id.noImage);
        etxtUpload = (EditText)findViewById(R.id.etxtUpload);

        uploadButton.setOnClickListener(UploadActivity.this);
        btnProfile.setOnClickListener(UploadActivity.this);
        btnImages.setOnClickListener(UploadActivity.this);


        dialog = new ProgressDialog(this);
        dialog.setMessage("Uploading Image...");
        dialog.setCancelable(false);

        jsonObject = new JSONObject();
        encodedImageList = new ArrayList<>();

    }

    private Uri mImageUri;

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.button_profile:
                isUserProfile = true;
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, "Choose application"), REQCODE);
                break;
            case R.id.button_images:
                isUserProfile = false;
                Intent intentData = new Intent();
                intentData.setType("image/*");
                intentData.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
                intentData.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intentData, "Choose application"), REQCODE);
                break;
            case R.id.uploadButton:
                dialog.show();

                JSONArray jsonArray = new JSONArray();

                if (encodedImageList.isEmpty()){
                    dialog.dismiss();
                    Toast.makeText(this, "Please select some images first.", Toast.LENGTH_SHORT).show();
                    return;
                }

                for (String encoded: encodedImageList){
                    jsonArray.put(encoded);
                }

                try {
                    jsonObject.put("imageName", etxtUpload.getText().toString().trim());
                    jsonObject.put("imageList", jsonArray);
                    jsonObject.put("user_profile", jsonArray);
                } catch (JSONException e) {
                    Log.e("JSONObject Here", e.toString());
                }
                JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, "urlUpload", jsonObject,
                        new Response.Listener<JSONObject>() {
                            @Override
                            public void onResponse(JSONObject jsonObject) {
                                Log.e("Message from server", jsonObject.toString());
                                dialog.dismiss();
                                messageText.setText("Images Uploaded Successfully");
                                Toast.makeText(getApplication(), "Images Uploaded Successfully", Toast.LENGTH_SHORT).show();
                            }
                        }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError volleyError) {
                        Toast.makeText(getApplication(), "Error Occurred", Toast.LENGTH_SHORT).show();
                        dialog.dismiss();
                    }
                });
                jsonObjectRequest.setRetryPolicy(new DefaultRetryPolicy( 200*30000,
                        DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                        DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
                Volley.newRequestQueue(this).add(jsonObjectRequest);
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        try {
            // When an Image is picked
            if (requestCode == REQCODE && resultCode == RESULT_OK
                    && null != data) {
                // Get the Image from data

                String[] filePathColumn = { MediaStore.Images.Media.DATA };
                imagesUriList = new ArrayList<Uri>();
                encodedImageList.clear();
                if(isUserProfile){
                    if(data.getData()!=null){

                        mImageUri=data.getData();

                        // Get the cursor
                        Cursor cursor = getContentResolver().query(mImageUri,
                                filePathColumn, null, null, null);
                        // Move to first row
                        cursor.moveToFirst();

                        int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                        imageURI  = cursor.getString(columnIndex);
                        Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), mImageUri);
                        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                        bitmap.compress(Bitmap.CompressFormat.JPEG, 80, byteArrayOutputStream);
                        String encodedImage = Base64.encodeToString(byteArrayOutputStream.toByteArray(), Base64.DEFAULT);
                        encodedImageList.add(encodedImage);
                        cursor.close();
                        imgData.setImageURI(mImageUri);
                    }else {
                        Toast.makeText(this, "You haven't picked Image", Toast.LENGTH_LONG).show();
                    }
                }else{
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
                            Cursor cursor = getContentResolver().query(uri, filePathColumn, null, null, null);
                            // Move to first row
                            cursor.moveToFirst();

                            int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                            imageURI  = cursor.getString(columnIndex);
                            Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), uri);
                            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                            bitmap.compress(Bitmap.CompressFormat.JPEG, 80, byteArrayOutputStream);
                            String encodedImage = Base64.encodeToString(byteArrayOutputStream.toByteArray(), Base64.DEFAULT);
                            encodedImageList.add(encodedImage);
                            cursor.close();
                        }
                        gallery = (Gallery) findViewById(R.id.gallery);
                        gallery.setSpacing(1);

                        gallery.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                            public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
                                // show the selected Image
                                imgData.setImageURI(mArrayUri.get(position));
                            }
                        });
                        final GalleryImageAdapter galleryImageAdapter= new GalleryImageAdapter(this,mArrayUri);
                        gallery.setAdapter(galleryImageAdapter);
                        imgData.setImageURI(mArrayUri.get(0));
                    }
                }
            } else {
                Toast.makeText(this, "You haven't picked Image", Toast.LENGTH_LONG).show();
            }
        } catch (Exception e) {
        }

        super.onActivityResult(requestCode, resultCode, data);
    }
}
