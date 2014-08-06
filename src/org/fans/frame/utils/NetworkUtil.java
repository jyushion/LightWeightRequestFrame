package org.fans.frame.utils;

import android.annotation.SuppressLint;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.provider.Settings;
import android.telephony.TelephonyManager;
import android.text.TextUtils;

/**
 * 
 */
public class NetworkUtil {

	@SuppressLint("InlinedApi")
	public static int getSystemNetwork(Context context) {
		int netType = NetType.NONE;
		ConnectivityManager mgr = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

		NetworkInfo info = mgr.getActiveNetworkInfo();
		int connectType = ConnectivityManager.TYPE_DUMMY;
		int mobileType = TelephonyManager.NETWORK_TYPE_UNKNOWN;
		if (info != null && info.isAvailable()) {
			connectType = info.getType();
			switch (connectType) {
			case ConnectivityManager.TYPE_ETHERNET:
				netType = NetType.CABLE;
				break;

			case ConnectivityManager.TYPE_WIFI:
			case ConnectivityManager.TYPE_WIMAX:
				netType = NetType.WIFI;
				break;

			case ConnectivityManager.TYPE_MOBILE:
			case ConnectivityManager.TYPE_MOBILE_DUN:
			case ConnectivityManager.TYPE_MOBILE_SUPL:
			case ConnectivityManager.TYPE_MOBILE_MMS:
			case ConnectivityManager.TYPE_MOBILE_HIPRI: {
				TelephonyManager tmgr = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
				mobileType = tmgr.getNetworkType();
				switch (mobileType) {
				case TelephonyManager.NETWORK_TYPE_LTE://
					// TODO 4g
					netType = NetType.G4;
					break;
				case TelephonyManager.NETWORK_TYPE_UMTS:
				case TelephonyManager.NETWORK_TYPE_EVDO_0:
				case TelephonyManager.NETWORK_TYPE_EVDO_A:
				case TelephonyManager.NETWORK_TYPE_HSDPA:
				case TelephonyManager.NETWORK_TYPE_HSUPA:
				case TelephonyManager.NETWORK_TYPE_HSPA:
				case TelephonyManager.NETWORK_TYPE_EVDO_B:
				case TelephonyManager.NETWORK_TYPE_EHRPD:
				case TelephonyManager.NETWORK_TYPE_HSPAP:
					netType = NetType.G3;
					break;

				case TelephonyManager.NETWORK_TYPE_GPRS:
				case TelephonyManager.NETWORK_TYPE_EDGE:
				case TelephonyManager.NETWORK_TYPE_CDMA:
				case TelephonyManager.NETWORK_TYPE_1xRTT:
				case TelephonyManager.NETWORK_TYPE_IDEN:

				case TelephonyManager.NETWORK_TYPE_UNKNOWN:

				default:
					netType = NetType.G2;
					break;
				}
			}
				break;
			default:
				break;
			}
		}
		return netType;
	}

	public static boolean isAirplaneModeOn(Context context) {
		return Settings.System.getInt(context.getContentResolver(), Settings.System.AIRPLANE_MODE_ON, 0) != 0;
	}

	/**
	 * 判断当前网络连接是否为wifi
	 * 
	 * @param context
	 * @return
	 */
	public static boolean isWifiEnabled(Context context) {
		try {
			ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
			NetworkInfo info = cm.getActiveNetworkInfo();
			String typeName = info.getTypeName().toLowerCase(); // WIFI/MOBILE
			if (typeName.equals("wifi")) {
				return true;
			} else {
				return false;
			}
		} catch (Exception e) {
			return false;
		}
	}

	public static boolean isMobileNetWork(Context context) {
		try {
			ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
			NetworkInfo info = cm.getActiveNetworkInfo();
			return isMobileNetworkInfo(info);
		} catch (Exception e) {
			return false;
		}
	}

	/**
	 * 判断此网络是否为mobile 注：适应三星新定义的双卡双待mobile类型
	 * 
	 * @param netInfo
	 * @return
	 */
	public static boolean isMobileNetworkInfo(final NetworkInfo netInfo) {
		if (null == netInfo) {
			return false;
		}
		if (ConnectivityManager.TYPE_MOBILE == netInfo.getType() || ConnectivityManager.TYPE_MOBILE + 50 == netInfo.getType()) {
			return true;
		} else {
			return false;
		}
	}

	public static boolean is3Gor4G(Context context) {
		// 联通的3G UMTS HSDPA，移动和联通的2G为GPRS/EGDE，电信2G为CDMA，电信的3G为EVDO
		try {
			TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
			int type = tm.getNetworkType();
			if (type == TelephonyManager.NETWORK_TYPE_HSDPA || type == TelephonyManager.NETWORK_TYPE_LTE
					|| type == TelephonyManager.NETWORK_TYPE_UMTS || type == TelephonyManager.NETWORK_TYPE_HSPAP
					|| type == TelephonyManager.NETWORK_TYPE_HSPA || type == TelephonyManager.NETWORK_TYPE_EVDO_0
					|| type == TelephonyManager.NETWORK_TYPE_EHRPD || type == TelephonyManager.NETWORK_TYPE_EVDO_A
					|| type == TelephonyManager.NETWORK_TYPE_HSUPA || type == TelephonyManager.NETWORK_TYPE_EVDO_B)
				return true;
		} catch (Exception e) {
			// 权限？？
		}
		return false;
	}

	public static boolean isNetSupport(Context context) {
		ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
		if (cm == null)
			return false;

		// return cm.getActiveNetworkInfo().isAvailable();
		try {
			NetworkInfo[] info = cm.getAllNetworkInfo();
			if (info != null) {
				for (int i = 0; i < info.length; i++) {
					if (info[i].getState() == NetworkInfo.State.CONNECTED)
						return true;
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;

	}

	public static int getNetworkType(Context context) {
		int type = -1;
		ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
		if (cm != null) {
			NetworkInfo networkInfo = cm.getActiveNetworkInfo();
			if (networkInfo != null) {
				type = networkInfo.getType();
			}
		}
		return type;
	}

	/*
	 * 调用traceRoute跟踪对应host的访问情况 Handler nethdl = new Handler(){
	 * 
	 * @Override public void handleMessage(Message msg) { if(msg!=null &&
	 * msg.what==TraceConstants.TraceAction.TRACE_COMPLETE.ordinal()){
	 * Toast.makeText(getApplicationContext(), "r: "+msg.obj, 1).show(); } } };
	 * NetworkUtil.diagnoseNetwork(BaseApplication.getContext(),"113.143.11.34",
	 * nethdl);
	 */

	// ------------------ common -------------------
	/**
	 * 检测网络连接
	 * 
	 * @param context
	 * @return 有网络连接返回true,否则false
	 */
	public static boolean isNetworkAvailable(Context context) {
		NetworkInfo info = getActiveNetworkInfo(context);
		return null != info && info.isAvailable();
	}

	private static NetworkInfo getActiveNetworkInfo(Context context) {
		ConnectivityManager cmManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo info = null;
		try {
			info = cmManager.getActiveNetworkInfo();
		} catch (Exception e) {
		}
		return info;
	}

	public static boolean isWifiConnected(Context context) {
		if (context == null) {
			return false;
		}
		ConnectivityManager connMgr = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo activeNetworkInfo = connMgr.getActiveNetworkInfo();
		return activeNetworkInfo != null && activeNetworkInfo.getType() == ConnectivityManager.TYPE_WIFI;
	}

	/**
	 * 获取当前设置的电话号码 <BR>
	 * 
	 */
	public String getNativePhoneNumber(Context context) {
		TelephonyManager telephonyManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
		String NativePhoneNumber = null;
		NativePhoneNumber = telephonyManager.getLine1Number();
		return NativePhoneNumber;
	}

	/**
	 * Telecom service providers获取手机服务商信息 <br>
	 * 需要加入权限<uses-permission
	 * android:name="android.permission.READ_PHONE_STATE"/> <br>
	 */
	public int getProvidersName(Context context) {
		TelephonyManager telephonyManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
		int provider = MobileOperator.UNKONWN;// 空
		// 返回唯一的用户ID;就是这张卡的编号神马的
		String imsi = telephonyManager.getSubscriberId();
		// IMSI号前面3位460是国家，紧接着后面2位00 02是中国移动，01是中国联通，03是中国电信。
		if (!TextUtils.isEmpty(imsi)) {
			if (imsi.startsWith("46000") || imsi.startsWith("46002")) {
				provider = MobileOperator.CMCC;// "中国移动"
			} else if (imsi.startsWith("46001")) {
				provider = MobileOperator.CUCC;// "中国联通"
			} else if (imsi.startsWith("46003")) {
				provider = MobileOperator.CTCC;// "中国电信"
			}
		}
		return provider;
	}

	interface NetType {
		int NONE = 0;
		int WIFI = 1;
		int G2 = 2;
		int G3 = 3;
		int G4 = 4;
		int CABLE = 5;

	}

	interface MobileOperator {
		// 中国移动
		int CMCC = 0;
		// 中国联通
		int CUCC = 1;
		// 中国电信
		int CTCC = 2;
		int UNKONWN = -1;
	}
}