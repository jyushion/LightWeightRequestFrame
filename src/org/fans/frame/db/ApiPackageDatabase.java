package org.fans.frame.db;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

/**
 * 存放普通javabean的通用数据库,这个设计的一般般，可以用可以不用
 * 
 * @author ludaiqian@126.com
 * 
 * @param <T>
 */
public class ApiPackageDatabase<T> extends DBBase {
	private static final String SEPARATOR = ",";
	private ApiPacketBuilder<T> builder;
	private String tableName;
	private String idFieldName;
	private Class<T> klass;
	private DeleteStrategy<T> deleteStrategy;

	/**
	 * 根据给的类名称来创建一张表，存储数据必须是该类实例化的对象
	 * 
	 * @param context
	 * @param klass
	 *            存放的ApiPacket类型
	 * @param idFieldName
	 *            作为id的字段名称
	 */
	public ApiPackageDatabase(Context context, Class<T> klass, String idFieldName) {
		super(context);
		SQLiteDatabase db = getDB();
		this.idFieldName = idFieldName;
		this.klass = klass;
		builder = new ApiPacketBuilder<T>(klass);
		StringBuilder sql = new StringBuilder();
		tableName = klass.getSimpleName();
		sql.append("CREATE TABLE IF NOT EXISTS ").append(tableName).append("(");
		List<String> numberColumns = builder.getNumberTypeColumns();
		// 备注: 其实sqlite是不区分类型的。INTEGER VARCHAR之类的限制是无效的
		for (String column : numberColumns) {
			if (idFieldName.equals(column)) {
				sql.append(column).append(" INTEGER NOT NULL UNIQUE").append(SEPARATOR);
			} else {
				sql.append(column).append(" INTEGER").append(SEPARATOR);
			}
		}
		List<String> charSequenceColunms = builder.getCharSequenceTypeColumns();
		for (int i = 0; i < charSequenceColunms.size(); i++) {
			String column = charSequenceColunms.get(i);
			if (idFieldName.equals(column)) {
				sql.append(column).append(" VARCHAR NOT NULL UNIQUE").append(SEPARATOR);
			} else {
				sql.append(column).append(" VARCHAR").append(SEPARATOR);

			}

		}
		sql.append("_id INTEGER PRIMARY KEY AUTOINCREMENT)");
		db.execSQL(sql.toString());
		db.close();
	}

	public void setDeleteStrategy(DeleteStrategy<T> deleteStrategy) {
		this.deleteStrategy = deleteStrategy;
	}

	/**
	 * 此方法囊括增、删、改
	 * 
	 * @param topic
	 * @return
	 * @throws IllegalAccessException
	 * @throws IllegalArgumentException
	 */
	private int update(SQLiteDatabase db, T info) throws IllegalArgumentException, IllegalAccessException {
		Field f = findField(idFieldName, klass);
		f.setAccessible(true);
		String[] whereArgs = { String.valueOf(f.get(info)) };
		boolean shouldDelete = shouldDelete(info);
		if (shouldDelete) {
			int count = db.delete(tableName, idFieldName + "= ?", whereArgs);
			return count > 0 ? DELETE : NO_EFFECT;
		} else {
			ContentValues values = builder.deconstruct(info);
			int rowCount = db.update(tableName, values, idFieldName + "=? ", whereArgs);
			if (rowCount == 0) {
				long column = db.insert(tableName, null, values);
				return column != -1 ? ADD : NO_EFFECT;
			}
			return UPDATE;
		}
	}

	/**
	 * 判断是否删除
	 * 
	 * @param info
	 * @return
	 */
	private boolean shouldDelete(T info) {
		if (deleteStrategy != null)
			return deleteStrategy.shouldDelete(info);
		return false;
	}

	public int update(T info) {
		SQLiteDatabase db = null;
		try {
			db = getDB();
			return update(db, info);
		} catch (Exception e) {
			e.printStackTrace();
			return NO_EFFECT;
		} finally {
			if (db != null)
				db.close();
		}
	}

	public void update(List<T> infos) {
		SQLiteDatabase db = null;
		try {
			db = getDB();
			for (T info : infos) {
				update(db, info);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (db != null)
				db.close();
		}
	}

	// public int u
	public List<T> query() {
		SQLiteDatabase db = getDB(true);
		ArrayList<T> values = new ArrayList<T>();
		String[] columns = builder.getColumnsNames();
		Cursor query = db.query(tableName, columns, null, null, null, null, null);
		if (query != null) {
			query.moveToFirst();
			while (!query.isAfterLast()) {
				T value = builder.build(query);
				values.add(value);
				query.moveToNext();
			}
		}
		if (query != null)
			query.close();
		db.close();
		return values;
	}

	// public int u
	public List<T> query(String selection, String arg) {
		return query(selection, arg, null);
	}

	// public int u
	public List<T> query(String selection, String arg, String orderBy) {
		SQLiteDatabase db = getDB(true);
		ArrayList<T> values = new ArrayList<T>();
		String[] columns = builder.getColumnsNames();
		Cursor query = db.query(tableName, columns, selection != null ? selection + "= ?" : null,
				arg != null ? new String[] { arg } : null, null, null, orderBy);
		if (query != null) {
			query.moveToFirst();
			while (!query.isAfterLast()) {
				T value = builder.build(query);
				values.add(value);
				query.moveToNext();
			}
		}
		if (query != null)
			query.close();
		db.close();
		return values;
	}

	public T query(String id) {
		T value = null;
		SQLiteDatabase db = getDB();
		String[] whereArgs = { id };
		String[] columns = builder.getColumnsNames();
		Cursor query = db.query(tableName, columns, idFieldName + "= ?", whereArgs, null, null, null);
		if (query != null) {
			query.moveToFirst();
			if (!query.isAfterLast()) {
				value = builder.build(query);
				return value;
			}
		}
		if (query != null)
			query.close();
		db.close();
		return value;
	}

	@SuppressWarnings("rawtypes")
	private Field findField(String fieldName, Class klass) {
		Field f = null;
		Class<?> clazz = klass;
		while (clazz != Object.class) {
			try {
				f = klass.getDeclaredField(fieldName);
			} catch (NoSuchFieldException e) {
				// e.printStackTrace();
			}
			if (f != null) {
				return f;
			}
			clazz = clazz.getSuperclass();

		}
		return null;
	}

}
