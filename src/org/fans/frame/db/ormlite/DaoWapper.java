package org.fans.frame.db.ormlite;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;

import org.fans.frame.db.ormlite.DataHelpeFactory.DataBaseListener;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.Dao.CreateOrUpdateStatus;
import com.j256.ormlite.stmt.QueryBuilder;

public class DaoWapper<T, ID> {

	private Dao<T, ID> dao;
	private Class<T> type;

	public DaoWapper(Dao<T, ID> dao,Class<T> type) {
		super();
		this.dao = dao;
		this.type=type;
	}

	/**
	 * 查询全部
	 * 
	 * @return
	 * @throws SQLException
	 */
	public List<T> query() throws SQLException {
		return dao.queryForAll();
	}

	/**
	 * 查询
	 * 
	 * @param id
	 * @return
	 * @throws SQLException
	 */
	public T query(ID id) throws SQLException {
		HashMap<String, Object> simpledataMap = new HashMap<String, Object>();
		simpledataMap.put("id", id);
		List<T> result = dao.queryForFieldValues(simpledataMap);
		if (result != null && result.size() > 0)
			return result.get(0);
		return null;
	}

	/**
	 * 查询
	 * 
	 * @param args
	 * @return
	 * @throws SQLException
	 */
	public List<T> query(HashMap<String, Object> args) throws SQLException {
		return dao.queryForFieldValues(args);
	}

	/**
	 * 分页查询
	 * 
	 * @param offset
	 * @param limit
	 * @param columnOrderBy
	 * @param asc
	 * @return
	 * @throws SQLException
	 */
	public List<T> pagingQuery(long offset, long limit, String columnOrderBy, boolean asc) throws SQLException {
		QueryBuilder<T, ID> builder = dao.queryBuilder();
		builder.offset(offset);
		builder.limit(limit);
		builder.orderBy(columnOrderBy, asc);
		return builder.query();
	}

	/**
	 * 记录条数
	 * 
	 * @return
	 * @throws SQLException
	 */
	public long count() throws SQLException {
		return dao.queryBuilder().countOf();
	}

	/**
	 * 添加或者更新
	 * 
	 * @param data
	 * @throws SQLException
	 */
	@SuppressWarnings("unchecked")
	public void insertOrUpdate(T data) throws SQLException {

		CreateOrUpdateStatus status = dao.createOrUpdate(data);
		DataBaseListener<T> l = DataHelpeFactory.getListeners().get(type);
		if (l != null) {
			if (status.isCreated()) {
				l.onDataInserted(data);
			} else if (status.isUpdated()) {
				l.onDataUpdated(data);
			}
		}
	}

	/**
	 * 添加
	 * 
	 * @param data
	 * @throws SQLException
	 */
	// public int delete(){}
	public void insert(T data) throws SQLException {
		dao.create(data);
		@SuppressWarnings("unchecked")
		DataBaseListener<T> l = DataHelpeFactory.getListeners().get(type);
		if (l != null) {
			l.onDataInserted(data);
		}
	}

	/**
	 * 更新
	 * 
	 * @param data
	 * @throws SQLException
	 */
	public void update(T data) throws SQLException {
		dao.update(data);

		@SuppressWarnings("unchecked")
		DataBaseListener<T> l = DataHelpeFactory.getListeners().get(type);
		if (l != null) {
			l.onDataUpdated(data);
		}
	}

	/**
	 * 清空
	 * 
	 * @throws SQLException
	 */
	public void clear() throws SQLException {
		dao.deleteBuilder().delete();

		@SuppressWarnings("unchecked")
		DataBaseListener<T> l = DataHelpeFactory.getListeners().get(type);
		if (l != null) {
			l.onDataCleared();
		}
	}

}
