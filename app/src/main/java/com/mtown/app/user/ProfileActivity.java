package com.mtown.app.user;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.TooltipCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.mtown.app.R;
import com.mtown.app.dao.ModelDAO;
import com.mtown.app.home.MainActivity;
import com.mtown.app.support.AppController;
import com.mtown.app.support.TouchImageView;

import java.util.ArrayList;

public class ProfileActivity extends AppCompatActivity {
    public static ArrayList<ModelDAO> modelProfileDAOS;

    //Stores the text to swipe.
    private LayoutInflater inflater;    //Used to create individual pages
    private ViewPager viewPager;               //Reference to class to swipe views
    private int currentPage=0;
    private int imageCount = 0;
    private ImageView imgBack;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        getSupportActionBar().hide();
        //getSupportActionBar().setTitle("User Profile");
        //get Intent
        Intent intentData = getIntent();
        currentPage = intentData.getIntExtra(getString(R.string.tagIndex),0);

        if(currentPage>-1){
            modelProfileDAOS = MainActivity.modelDAOS;
        }else {
            modelProfileDAOS = MainActivity.profileDetails;
        }

        imgBack = (ImageView)findViewById(R.id.imgBack);
        //get an inflater to be used to create single pages
        inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        //Reference ViewPager defined in activity
        viewPager =(ViewPager)findViewById(R.id.viewPager);
        //set the adapter that will create the individual pages
        viewPager.setAdapter(new ModelProfileAdapter());
        viewPager.setCurrentItem(currentPage);

        imgBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }


    //Implement PagerAdapter Class to handle individual page creation
    class ModelProfileAdapter extends PagerAdapter {
        @Override
        public int getCount() {
            //Return total pages, here one for each data item
            return modelProfileDAOS.size();
        }
        //Create the given page (indicated by position)
        @Override
        public Object instantiateItem(ViewGroup container, final int position) {
            View page = inflater.inflate(R.layout.profile_details, null);

            //set Font
            ((TextView)page.findViewById(R.id.txtHeadingAbout)).setTypeface(AppController.getDefaultBoldFont(ProfileActivity.this));
            ((TextView)page.findViewById(R.id.user_profile_name)).setTypeface(AppController.getDefaultFont(ProfileActivity.this));
            ((TextView)page.findViewById(R.id.user_profile_short_bio)).setTypeface(AppController.getDefaultFont(ProfileActivity.this));
            ((TextView)page.findViewById(R.id.txtHeadingAboutValue)).setTypeface(AppController.getDefaultFont(ProfileActivity.this));
            ((TextView)page.findViewById(R.id.txtHeadingDetails)).setTypeface(AppController.getDefaultBoldFont(ProfileActivity.this));
            ((TextView)page.findViewById(R.id.txtAge)).setTypeface(AppController.getDefaultFont(ProfileActivity.this));
            ((TextView)page.findViewById(R.id.txtHeight)).setTypeface(AppController.getDefaultFont(ProfileActivity.this));
            ((TextView)page.findViewById(R.id.txtWeight)).setTypeface(AppController.getDefaultFont(ProfileActivity.this));
            ((TextView)page.findViewById(R.id.txtSkinColor)).setTypeface(AppController.getDefaultFont(ProfileActivity.this));
            ((TextView)page.findViewById(R.id.txtEyeColor)).setTypeface(AppController.getDefaultFont(ProfileActivity.this));
            ((TextView)page.findViewById(R.id.txtLanguages)).setTypeface(AppController.getDefaultFont(ProfileActivity.this));
            ((TextView)page.findViewById(R.id.txtAge)).setTypeface(AppController.getDefaultFont(ProfileActivity.this));

            if(currentPage==-1 || AppController.getSharedPref(ProfileActivity.this).getString(getString(R.string.tagGroupType), "").equals("admin")){
                ((ImageView)page.findViewById(R.id.btnEditProfile)).setVisibility(View.VISIBLE);
                ((ImageView)page.findViewById(R.id.btnEditProfile)).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        ArrayList<ModelDAO> dataList = new ArrayList<ModelDAO>();
                        dataList.add(modelProfileDAOS.get(position));
                        MainActivity.profileDetails = dataList;
                        Intent intent = new Intent(ProfileActivity.this,AddEditUserActivity.class);
                        intent.putExtra(getString(R.string.tagIndex), 1);
                        startActivity(intent);
                    }
                });
            }else {
                ((ImageView)page.findViewById(R.id.btnEditProfile)).setVisibility(View.GONE);
           }


            if(modelProfileDAOS.get(position).getStatus().equals("1")){
                ((ImageView)page.findViewById(R.id.imgStatus)).setImageResource(R.drawable.ic_check_circle_green_24dp);
            }else {
                ((ImageView)page.findViewById(R.id.imgStatus)).setImageResource(R.drawable.ic_check_circle_black_24dp);
                TooltipCompat.setTooltipText(((ImageView)page.findViewById(R.id.imgStatus)), "Profile not approved.");
                if(AppController.getSharedPref(ProfileActivity.this).getString(getString(R.string.tagGroupType), "").equals("admin")){
                    ((TextView)page.findViewById(R.id.txtRequestAudition)).setVisibility(View.VISIBLE);
                }else {
                    ((TextView)page.findViewById(R.id.txtRequestAudition)).setVisibility(View.GONE);
                }
            }

            ((TextView)page.findViewById(R.id.user_profile_name)).setText(modelProfileDAOS.get(position).getFirstname() +" "+ modelProfileDAOS.get(position).getLastname());
            ((TextView)page.findViewById(R.id.user_profile_short_bio)).setText(modelProfileDAOS.get(position).getDesignation());

            Glide.with(ProfileActivity.this).load(modelProfileDAOS.get(position).getProfile_image()).into(((ImageView)page.findViewById(R.id.user_profile_photo)));
            int index = AppController.randomNumberInRange(0,(modelProfileDAOS.get(position).getModel_images().length-1));
            Glide.with(ProfileActivity.this).load(modelProfileDAOS.get(position).getModel_images()[index]).into(((ImageView)page.findViewById(R.id.header_cover_image)));

            ((TextView)page.findViewById(R.id.txtHeadingAboutValue)).setText(modelProfileDAOS.get(position).getAbout_you());
            ((TextView)page.findViewById(R.id.txtAge)).setText("Age : "+ modelProfileDAOS.get(position).getAge());
            ((TextView)page.findViewById(R.id.txtHeight)).setText("Height : "+ modelProfileDAOS.get(position).getHeight());
            ((TextView)page.findViewById(R.id.txtWeight)).setText("Weight : "+ modelProfileDAOS.get(position).getWeight());
            ((TextView)page.findViewById(R.id.txtSkinColor)).setText("Skin Color : "+ modelProfileDAOS.get(position).getSkin_color());
            ((TextView)page.findViewById(R.id.txtEyeColor)).setText("Eye Color : "+ modelProfileDAOS.get(position).getEye_color());
            ((TextView)page.findViewById(R.id.txtLanguages)).setText("Languages : "+ modelProfileDAOS.get(position).getKnown_languages());

            // add button listener
            ((ImageView)page.findViewById(R.id.header_cover_image)).setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View arg0) {
                    imageCount = (modelProfileDAOS.get(position).getModel_images().length);
                    currentPage = position;
                    // custom dialog
                    final Dialog dialog = new Dialog(ProfileActivity.this,R.style.full_screen_dialog);
                    dialog.setContentView(R.layout.dialog);
                    dialog.setTitle("");
                    dialog.getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT);

                    //get an inflater to be used to create single pages
                    LayoutInflater inflaterDialog = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                    //Reference ViewPager defined in activity
                    ViewPager viewPager =(ViewPager) dialog.findViewById(R.id.modelImagePager);
                    //set the adapter that will create the individual pages
                    viewPager.setAdapter(new ModelImagesAdapter());
                    viewPager.setCurrentItem(0);

                    dialog.show();
                }
            });

            //Add the page to the front of the queue
            ((ViewPager) container).addView(page, 0);
            return page;
        }
        @Override
        public boolean isViewFromObject(View arg0, Object arg1) {
            //See if object from instantiateItem is related to the given view
            //required by API
            return arg0==(View)arg1;
        }
        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            ((ViewPager) container).removeView((View) object);
            object=null;
        }
    }


    //Implement PagerAdapter Class to handle individual page creation
    class ModelImagesAdapter extends PagerAdapter {
        @Override
        public int getCount() {
            //Return total pages, here one for each data item
            return imageCount;
        }
        //Create the given page (indicated by position)
        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            View page = inflater.inflate(R.layout.image_swiper, null);

            Glide.with(ProfileActivity.this).load(modelProfileDAOS.get(currentPage).getModel_images()[position]).into(((ImageView)page.findViewById(R.id.imgModel)));
           // ((TouchImageView)page.findViewById(R.id.imgModel)).setMaxZoom(5f);
            //Add the page to the front of the queue
            ((ViewPager) container).addView(page, 0);
            return page;
        }
        @Override
        public boolean isViewFromObject(View arg0, Object arg1) {
            //See if object from instantiateItem is related to the given view
            //required by API
            return arg0==(View)arg1;
        }
        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            ((ViewPager) container).removeView((View) object);
            object=null;
        }
    }
}
