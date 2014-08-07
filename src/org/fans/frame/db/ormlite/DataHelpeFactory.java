package org.fans.frame.db.ormlite;

import android.content.Context;

import com.j256.ormlite.android.apptools.OpenHelperManager;

public class DataHelpeFactory {

	@SuppressWarnings("unchecked")
	public static <T, ID> DataHelper<T, ID> newDataHelper(Context context, Class<T> type) {
		return OpenHelperManager.getHelper(context, DataHelper.class, type);

	}
}
