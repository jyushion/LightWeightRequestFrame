package org.fans.frame.db;

public interface DeleteStrategy<T> {

	public boolean shouldDelete(T info);
}
