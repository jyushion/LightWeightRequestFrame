package org.fans.frame.utils;

import java.io.File;

import org.fans.frame.api.executor.DialogProvider;
import org.fans.frame.api.executor.DialogTaskExecutor;
import org.fans.frame.api.executor.JsonSerializer;
import org.fans.frame.api.executor.ParamsBuilder;
import org.fans.frame.api.executor.ParamsBuilderProvider;
import org.fans.frame.api.packet.ApiRequest;
import org.fans.frame.api.toolbox.DefaultParamsBuilder;
import org.fans.frame.widget.LoadingDialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Environment;
import android.os.StatFs;

import com.android.volley.Request.Method;

public class Utils {

	private static final long LOW_STORAGE_THRESHOLD = 1024 * 1024 * 15;

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
	public static File getDiskCachePath(Context context, String uniqueName) {

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

		if (VersionUtils.hasFroyo()) {
			return context.getExternalCacheDir();

		}
		// Before Froyo we need to construct the external cache dir ourselves

		final String cacheDir = "/Android/data/" + context.getPackageName() + "/cache/";

		return new File(Environment.getExternalStorageDirectory().getPath() + cacheDir);

	}

	private static String getExternalStoragePath() {
		// 获取SdCard状态
		String state = android.os.Environment.getExternalStorageState();
		// 判断SdCard是否存在并且是可用的
		if (android.os.Environment.MEDIA_MOUNTED.equals(state)) {
			if (android.os.Environment.getExternalStorageDirectory().canWrite()) {
				return android.os.Environment.getExternalStorageDirectory().getPath();
			}
		}
		return null;
	}

	public static boolean checkSdCardAvailable() {

		String state = Environment.getExternalStorageState();
		File sdCardDir = Environment.getExternalStorageDirectory();
		if (android.os.Environment.MEDIA_MOUNTED.equals(state) && sdCardDir.canWrite()) {
			if (getAvailableStore(sdCardDir.toString()) > LOW_STORAGE_THRESHOLD) {
				try {
					// File f = new
					// File(Environment.getExternalStorageDirectory(), "_temp");
					// f.createNewFile();
					// f.delete();
					return true;
				} catch (Exception e) {
				}
			}
		}
		return false;

	}

	/**
	 * 
	 * 获取存储卡的剩余容量，单位为字节
	 * 
	 * @param filePath
	 * 
	 * @return availableSpare
	 */

	private static long getAvailableStore(String filePath) {
		// 取得sdcard文件路径
		StatFs statFs = new StatFs(filePath);
		// 获取block的SIZE
		long blocSize = statFs.getBlockSize();
		// 获取BLOCK数量
		long totalBlocks = statFs.getBlockCount();
		// 可使用的Block的数量
		long availaBlock = statFs.getAvailableBlocks();
		long availableSpare = availaBlock * blocSize;
		return availableSpare;

	}

}
