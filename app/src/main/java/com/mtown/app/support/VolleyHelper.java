package com.mtown.app.support;

import android.content.Context;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;


public class VolleyHelper {

	private static RequestQueue mImageRequestQueue;
	private static RequestQueue mRequestQueue;

	public static RequestQueue getImageRequestQueue() {

		if (mImageRequestQueue != null)
			return mImageRequestQueue;
		else
			throw new IllegalStateException(
					"Image RequestQueue not initialized");
	}

	public static RequestQueue getRequestQueue() {

		if (mRequestQueue != null)
			return mRequestQueue;
		else
			throw new IllegalStateException("RequestQueue not initialized");
	}

	/**
	 * Create separate queue for image and data
	 * 
	 * @param context
	 */
	public static void initQueues(Context context) {
		mRequestQueue = Volley.newRequestQueue(context);
		mImageRequestQueue = Volley.newRequestQueue(context);

	}

}
