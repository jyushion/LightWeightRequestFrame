package org.fans.frame.utils;

import android.util.Log;

/**
 * Utility class for LogCat.
 * 
 * @author ludq@hyxt.com
 */
public class Logger {

	private final static String TAG = "Logger";
	private final static boolean debug = true;

	/**
	 * log.i
	 */
	public static void i(String msg) {
		if (debug) {
			Log.i(TAG, msg);
		}
	}

	public static void i(String tag, String msg) {
		if (debug) {
			Log.i(tag, msg);
		}
	}

	/**
	 * log.d
	 */
	public static void d(String msg) {
		if (debug) {
			Log.d(TAG, msg);
		}
	}

	/**
	 * log.d
	 */
	public static void d(String tag, String msg) {
		if (debug) {
			Log.d(tag, msg);
		}
	}

	/**
	 * log.e
	 */
	public static void e(String msg) {
		if (debug) {
			Log.e(TAG, msg);
		}
	}

	/**
	 * log.e
	 */
	public static void e(String tag, String msg) {
		if (debug) {
			Log.e(tag, msg);
		}
	}

	public static String makeLogTag(Class<?> cls) {
		return cls.getSimpleName();
	}

	public static void d(Class<?> cls, String msg) {
		if (debug)
			Log.d(makeLogTag(cls), msg);
	}

	public static void e(Class<?> cls, String msg) {
		if (debug)
			Log.e(makeLogTag(cls), msg);
	}

	public static void i(Class<?> cls, String msg) {
		if (debug)
			Log.i(makeLogTag(cls), msg);
	}

	public static void v(Class<?> cls, String msg) {
		if (debug)
			Log.v(makeLogTag(cls), msg);
	}

	public static void w(Class<?> cls, String msg) {
		if (debug)
			Log.w(makeLogTag(cls), msg);
	}
}
