package org.fans.frame.db.ormlite;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.stmt.QueryBuilder;

public class DaoWapper<T, ID> {

	private Dao<T, ID> dao;

	public DaoWapper(Dao<T, ID> dao) {
		super();
		this.dao = dao;
	}

	public List<T> query() throws SQLException {
		return dao.queryForAll();
	}

	public T query(ID id) throws SQLException {
		HashMap<String, Object> simpledataMap = new HashMap<String, Object>();
		simpledataMap.put("id", id);
		List<T> result = dao.queryForFieldValues(simpledataMap);
		if (result != null && result.size() > 0)
			return result.get(0);
		return null;
	}

	public List<T> query(HashMap<String, Object> args) throws SQLException {
		return dao.queryForFieldValues(args);
	}

	public List<T> pagingQuery(long offset, long limit, String columnOrderBy, boolean asc) throws SQLException {
		QueryBuilder<T, ID> builder = dao.queryBuilder();
		builder.offset(offset);
		builder.limit(limit);
		builder.orderBy(columnOrderBy, asc);
		return builder.query();
	}

	public long count() throws SQLException {
		return dao.queryBuilder().countOf();
	}

	public void insertOrUpdate(T data) throws SQLException {
		dao.createOrUpdate(data);
	}

	// public int delete(){}
	public void insert(T data) throws SQLException {
		dao.create(data);
	}

	public void update(T data) throws SQLException {
		dao.update(data);
	}
}
