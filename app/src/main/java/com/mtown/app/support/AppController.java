package com.mtown.app.support;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.AssetManager;
import android.graphics.Typeface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.provider.Settings;
import android.telephony.TelephonyManager;
import android.text.TextUtils;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.mtown.app.R;


import java.util.Locale;
import java.util.Random;


public class AppController extends Application {

    private static AppController mInstance;
    public static final String TAG = AppController.class.getSimpleName();
    static SharedPreferences.Editor editor;
    static SharedPreferences prefs;
    public static boolean isProduction = true;
    public final static int TYPE_MODEL=1,TYPE_MODEL_SEARCH=2;
    public static String modelIds;
    public static String modelCode;


    public static int randomNumberInRange(int min, int max) {
        Random random = new Random();
        return random.nextInt((max - min) + 1) + min;
    }

    public static SharedPreferences getSharedPref(Context pContext) {
        if (prefs == null)
            prefs = pContext.getSharedPreferences(pContext.getString(R.string.tagAppData), 0);
        return prefs;
    }

    public static SharedPreferences.Editor getSharedPrefEditor(Context pContext) {
        if (editor == null)
            editor = pContext.getSharedPreferences(pContext.getString(R.string.tagAppData), 0).edit();
        return editor;
    }

    public static synchronized AppController getInstance() {
        return mInstance;
    }

    private RequestQueue mRequestQueue;

    public <T> void addToRequestQueue(Request<T> req) {
        req.setTag(TAG);
        getRequestQueue().add(req);
    }

    public <T> void addToRequestQueue(Request<T> req, String tag) {
        // set the default tag if tag is empty
        req.setTag(TextUtils.isEmpty(tag) ? TAG : tag);
        getRequestQueue().add(req);
    }

    public void cancelPendingRequests(Object tag) {
        if (mRequestQueue != null)
            mRequestQueue.cancelAll(tag);
    }

    public RequestQueue getRequestQueue() {
        if (mRequestQueue == null)
            mRequestQueue = Volley.newRequestQueue(getApplicationContext());
        return mRequestQueue;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mInstance = this;
        VolleyHelper.initQueues(this);
    }

    //Get the current version
    public static boolean getSystenVersion() {
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                return true;
            } else {
                return false;
            }
        } catch (Exception e) {
        }
        return false;
    }

    //Setting application font Bold,Regular
    public static Typeface getDefaultBoldFont(Context context) {
        AssetManager am = context.getApplicationContext().getAssets();
        return Typeface.createFromAsset(am, String.format(Locale.US, "fonts/%s", "Montserrat-SemiBold.otf"));
    }

    public static Typeface getDefaultFont(Context context) {
        AssetManager am = context.getApplicationContext().getAssets();
        return Typeface.createFromAsset(am, String.format(Locale.US, "fonts/%s", "OpenSans-Regular.ttf"));
    }

    public static String getDeviceIMEI(Context context) {
        try {
            String imei = null;
            TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
            if (tm != null)
                imei = tm.getDeviceId();
            if (imei == null)
                imei = Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);
            return imei;
        } catch (SecurityException e) {
            return null;
        }
    }

    public static boolean isConnectingToInternet(Context context) {
        boolean wifiDataAvailable = false;
        boolean mobileDataAvailable = false;
        try {
            ConnectivityManager conManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo[] networkInfo = conManager.getAllNetworkInfo();
            for (NetworkInfo netInfo : networkInfo) {
                if (netInfo.getTypeName().equalsIgnoreCase("WIFI"))
                    if (netInfo.isConnected())
                        wifiDataAvailable = true;
                if (netInfo.getTypeName().equalsIgnoreCase("MOBILE"))
                    if (netInfo.isConnected())
                        mobileDataAvailable = true;
            }
        } catch (Exception e) {
        }
        return wifiDataAvailable || mobileDataAvailable;
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
    }
}
