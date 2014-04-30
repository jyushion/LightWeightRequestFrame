package org.fans.frame.db;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelper extends SQLiteOpenHelper {

	private static final int VERSION = 2;
	private int versionCode = VERSION;
	private List<OnDatabaseUpdatedListener> onDatabaseUpdatedListeners;

	private boolean isDBUpdated = false;

	// 在SQLiteOepnHelper的子类当中，必须有该构造函数
	protected DatabaseHelper(Context context, String name, CursorFactory factory, int version) {
		// 必须通过super调用父类当中的构造函数
		super(context, name, factory, version);
		onDatabaseUpdatedListeners = new ArrayList<DatabaseHelper.OnDatabaseUpdatedListener>();
	}

	protected DatabaseHelper(Context context, String name) {
		this(context, name, VERSION);
	}

	protected DatabaseHelper(Context context, String name, int version) {
		this(context, name, null, version);
	}

	// 该函数是在第一次创建数据库的时候执行,实际上是在第一次得到SQLiteDatabse对象的时候，才会调用这个方法
	@Override
	public void onCreate(SQLiteDatabase db) {
		// TODO Auto-generated method stub
		// System.out.println("create a Database");
		// execSQL函数用于执行SQL语句
		// db.execSQL("create table user(id int,name varchar(20))");
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// TODO Auto-generated method stub
		System.out.println("update a Database ");
		System.err.println("old version:" + oldVersion + ",new " + newVersion);
		if (newVersion > oldVersion) {
			// db.de
			// int versionValue=Utils.getSharedPreferences(context, versionKey,
			// 0);
			// if(VERSION>versionValue)
			isDBUpdated = true;
			versionCode = newVersion;
			for (OnDatabaseUpdatedListener onDatabaseUpdatedListener : onDatabaseUpdatedListeners) {
				onDatabaseUpdatedListener.onDatabaseUpdated(db, oldVersion, newVersion);
			}
		}
	}

	public int getVersionCode() {
		return versionCode;
	}

	public boolean isDBUpdated() {
		System.out.println("is dbupdate?" + isDBUpdated);
		return isDBUpdated;
	}

	public void addOnDatabaseUpdatedListener(OnDatabaseUpdatedListener onDatabaseUpdatedListener) {
		onDatabaseUpdatedListeners.add(onDatabaseUpdatedListener);
	}

	public interface OnDatabaseUpdatedListener {
		public void onDatabaseUpdated(SQLiteDatabase db, int oldVersion, int newVersion);
	}
}