package org.fans.frame.api.executor;

import java.util.Collection;

import org.fans.frame.api.packet.ApiResponse;

/**
 * 从ApiResonse中提取一个集合，集合提取策略由调用者自己指定<br>
 * {@link AsyncListViewFillerImpl}, {@link LazyLoadListViewFiller}
 * 
 * @author Ludaiqian
 * @since 1.0
 */
public interface CollectionFetcher {
	/**
	 * 从ApiResonse中提取集合
	 * 
	 * @param response
	 * @return
	 */
	public Collection<?> fetch(ApiResponse<?> response);
}
