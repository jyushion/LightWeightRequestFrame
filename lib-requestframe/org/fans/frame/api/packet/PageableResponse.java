package org.fans.frame.api.packet;

/**
 * 返回可分页的数据若存在标志，使用可分页的PageableResponse更为恰当
 * 
 * @author Ludaiqian
 * 
 * @param <T>
 */
public interface PageableResponse<T> extends ApiResponse<T> {

	/**
	 * 接口返回数据总量
	 */
	public int getTotalCount();

	// public void setTotalCount(int totalCount);
	/**
	 * 接口返回页面总数
	 */
	public int getPageCount();

	// public void setPageCount(int pageCount);
	/**
	 * 接口返回是否已经加载完毕
	 */
	public boolean isReachEnd();

	// public void setReachEnd(boolean reachEnd);

}
