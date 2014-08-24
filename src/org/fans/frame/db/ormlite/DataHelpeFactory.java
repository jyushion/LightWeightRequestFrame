package org.fans.frame.db.ormlite;

import java.util.HashMap;

import android.content.Context;

import com.j256.ormlite.android.apptools.OpenHelperManager;

public class DataHelpeFactory {

	private static final DataHelpeFactory instance = new DataHelpeFactory();
	@SuppressWarnings("rawtypes")
	private static HashMap<Class, DataBaseListener> listeners = new HashMap<Class, DataHelpeFactory.DataBaseListener>();

	public static DataHelpeFactory getInstance() {
		return instance;
	}

	@SuppressWarnings("unchecked")
	public static <T, ID> DataHelper<T, ID> newDataHelper(Context context, Class<T> type) {
		return OpenHelperManager.getHelper(context, DataHelper.class, type);

	}

//	public static void releaseHelper() {
//		OpenHelperManager.releaseHelper();
//	}

	@SuppressWarnings("rawtypes")
	public static HashMap<Class, DataBaseListener> getListeners() {
		return listeners;
	}

	public static <T> void addListeners(Class<?> t, DataBaseListener<T> listener) {
		listeners.put(t, listener);
	}

	public interface DataBaseListener<T> {
		public void onDataInserted(T data);

		public void onDateDeleted(T data);

		public void onDataUpdated(T data);
		public void onDataCleared();
	}

}
