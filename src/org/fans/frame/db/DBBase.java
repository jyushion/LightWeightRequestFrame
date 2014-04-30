package org.fans.frame.db;

import org.fans.frame.db.DatabaseHelper.OnDatabaseUpdatedListener;
import org.fans.frame.utils.Constants;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

public abstract class DBBase {
	protected Context mContext;
	public static final int DELETE = 0;
	public static final int ADD = 1;
	public static final int UPDATE = 2;
	public static final int NO_EFFECT = -1;
	private static DatabaseHelper dbHelper;

	public DBBase(Context context) {
		this.mContext = context;
		if (dbHelper == null)
			dbHelper = new DatabaseHelper(context, Constants.DB_NAME);
	}

	protected static SQLiteDatabase getDB(boolean readOnly) {
		if (readOnly)
			return dbHelper.getReadableDatabase();
		else
			return dbHelper.getWritableDatabase();
	}

	protected static SQLiteDatabase getDB() {
		return getDB(false);
	}

	public static boolean isDBUpdated() {
		return dbHelper.isDBUpdated();
	}
	public void addOnDatabaseUpdatedListener(OnDatabaseUpdatedListener onDatabaseUpdatedListener) {
		dbHelper.addOnDatabaseUpdatedListener(onDatabaseUpdatedListener);
	}

	public static int getVersionCode() {
		return dbHelper.getVersionCode();
	}
}
