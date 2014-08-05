package org.fans.frame.api.packet;

/**
 * 
 * @author Ludaiqian
 * @since 1.0
 */
public interface PageableRequest extends ApiRequest {
	/**
	 * 当前第几页
	 * 
	 * @return
	 */
	public int getCurrentPage();

	/**
	 * 每页几条数据
	 * 
	 * @return
	 */
	public int getPageSize();

	/**
	 * 设置当前页
	 * 
	 * @param currentPage
	 */
	public void setCurrentPage(int currentPage);

	/**
	 * 设置每页几条数据
	 * 
	 * @param pageSize
	 */
	public void setPageSize(int pageSize);

}