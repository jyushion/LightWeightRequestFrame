package org.fans.frame.utils;

import java.io.File;

import org.fans.frame.api.executor.DefaultParamsBuilder;
import org.fans.frame.api.executor.DialogProvider;
import org.fans.frame.api.executor.DialogTaskExecutor;
import org.fans.frame.api.executor.JsonSerializer;
import org.fans.frame.api.executor.ParamsBuilder;
import org.fans.frame.api.executor.ParamsBuilderProvider;
import org.fans.frame.api.packet.ApiRequest;
import org.fans.frame.widget.LoadingDialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Build;
import android.os.Environment;

import com.android.volley.Request.Method;

public class Utils {

	public static DialogTaskExecutor getDefaultExecutor(Context context) {
		DialogTaskExecutor executor = DialogTaskExecutor.getInstance();
		if (!executor.isConfigured()) {

			ParamsBuilderProvider provider = new ParamsBuilderProvider() {

				@Override
				public ParamsBuilder provide(ApiRequest request) {
					return new DefaultParamsBuilder(request);
				}
			};
			DialogProvider dialogProvider = new DialogProvider() {

				@Override
				public Dialog provide(Context context) {
					return new LoadingDialog(context);
				}
			};
			executor.config(Constants.URL, Method.POST, JsonSerializer.DEFAULT, provider, dialogProvider, null);
		}
		return executor;
	}

	/**
	 * Get a usable cache directory (external if available, internal otherwise).
	 * 
	 * @param context
	 *            The context to use
	 * @param uniqueName
	 *            A unique directory name to append to the cache dir
	 * @return The cache dir
	 */
	public static File getDiskCacheDir(Context context, String uniqueName) {

		// Check if media is mounted or storage is built-in, if so, try and use
		// external cache dir
		// otherwise use internal cache dir
		final String cachePath = Utils.isExternalStorageMounted() ? Utils.getExternalCacheDir(context).getPath() : context
				.getCacheDir().getPath();

		return new File(cachePath + File.separator + uniqueName);
	}

	/*
	 * 外部存储设备是否就绪
	 */
	public static boolean isExternalStorageMounted() {

		if (!Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)
				|| !Environment.getExternalStorageDirectory().canWrite()) {
			// No SD card found.
			return false;
		}
		return true;
	}

	/**
	 * 
	 * Get the external app cache directory.
	 * 
	 * 
	 * 
	 * @param context
	 *            The context to use
	 * 
	 * @return The external cache dir
	 */

	public static File getExternalCacheDir(Context context) {

		if (Utils.hasFroyo()) {
			return context.getExternalCacheDir();

		}
		// Before Froyo we need to construct the external cache dir ourselves

		final String cacheDir = "/Android/data/" + context.getPackageName() + "/cache/";

		return new File(Environment.getExternalStorageDirectory().getPath() + cacheDir);

	}

	/**
	 * 
	 * Android 2.2为 “Froyo”“冻酸奶”
	 * */
	public static boolean hasFroyo() {
		// Can use static final constants like FROYO, declared in later versions
		// of the OS since they are inlined at compile time. This is guaranteed
		// behavior.
		return Build.VERSION.SDK_INT >= Build.VERSION_CODES.FROYO;//
	}

	/**
	 * Android 2.3为 Gingerbread（姜饼）
	 * */
	public static boolean hasGingerbread() {
		return Build.VERSION.SDK_INT >= Build.VERSION_CODES.GINGERBREAD;
	}

	/**
	 * Android 3.0为 Honeycomb蜂巢
	 * */
	public static boolean hasHoneycomb() {
		return Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB;
	}

	/**
	 * Android 3.1
	 * */
	public static boolean hasHoneycombMR1() {
		return Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR1;
	}

	/**
	 * Android 4.1. 果冻豆
	 * */
	public static boolean hasJellyBean() {
		return Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN;
	}

}
