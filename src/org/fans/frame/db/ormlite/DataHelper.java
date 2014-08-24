package org.fans.frame.db.ormlite;

import org.fans.frame.api.packet.demo.response.Response;

import android.content.Context;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.j256.ormlite.android.apptools.OrmLiteSqliteOpenHelper;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;

/**
 * 这个Ormlite不好用，改了之后还是不好用。
 * 
 * @author daiqian
 * 
 * @param <T>
 * @param <ID>
 */
public class DataHelper<T, ID> extends OrmLiteSqliteOpenHelper {
	private static final String DATABASE_NAME = "OrmliteDemo.db";
	public static int DATABASE_VERSION = 1;

	// private Dao<Person, Integer> simpledataDao = null;
	private DaoWapper<T, ID> daoWapper;

	private Class<T> type;

	public DataHelper(Context context, Class<T> type) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
		this.type = type;
	}

	@Override
	public void onCreate(SQLiteDatabase arg0, ConnectionSource connectionSource) {
		try {
			Log.e(DataHelper.class.getName(), "开始创建数据库");
			try {
				// 注意这里只会调用一次，必须吧所有需要创建表的对象写在这里。
				// TableUtils.createTable(connectionSource, type);

				TableUtils.createTable(connectionSource, Response.class);
			} catch (java.sql.SQLException e) {
				e.printStackTrace();
			}

		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, ConnectionSource connectionSource, int arg2, int arg3) {
		System.out.println("onUpgrade()...");
		try {
			// 注意这里升级只会调用一次,把所有对象对应的表都在这里升级
			// TableUtils.dropTable(connectionSource, type, true);
			TableUtils.dropTable(connectionSource, Response.class, true);
			onCreate(db, connectionSource);
		} catch (SQLException e) {
			e.printStackTrace();
		} catch (java.sql.SQLException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void close() {
		super.close();
	}

	public DaoWapper<T, ID> getDao() throws SQLException {
		if (daoWapper == null) {
			try {
				// Get a DAO for our class.
				// This uses the DaoManager to cache the DAO for future gets.
				// 获取java bean对应的DAO
				Dao<T, ID> dao = getDao(type);
				daoWapper = new DaoWapper<T, ID>(dao,type);
			} catch (java.sql.SQLException e) {
				e.printStackTrace();
			}
		}
		return daoWapper;
	}

}
