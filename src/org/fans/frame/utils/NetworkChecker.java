package org.fans.frame.utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

public class NetworkChecker {
	private static final String TAG = "NetworkAvailable";

	/**
	 * 检测网络连接
	 * 
	 * @param context
	 * @return 有网络连接返回true,否则false
	 */
	public static boolean isNetworkAvailable(Context context) {
		Log.i(TAG, "isNetworkAvailable");
		NetworkInfo info = getActiveNetworkInfo(context);
		return null != info && info.isAvailable();
	}

	private static NetworkInfo getActiveNetworkInfo(Context context) {
		Log.i(TAG, "getActiveNetWorkInfo...");
		ConnectivityManager cmManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo info = null;
		try {
			info = cmManager.getActiveNetworkInfo();
		} catch (Exception e) {
			Log.i(TAG, e.toString());
		}
		return info;
	}
}
