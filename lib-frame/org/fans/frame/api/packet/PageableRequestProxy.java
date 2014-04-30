package org.fans.frame.api.packet;


/**
 * 可分页的page，是{@link PageableRequestImpl}的代理 ，方便对{@link PageableRequestImpl}
 * 分页参数的设置
 * 
 * @author LuDaiqian
 * 
 */
public class PageableRequestProxy implements ApiRequest {

	private PageableRequest request;
	private int totalCount = 0;
	private int pageCount = 0;
	public static int pageSize = 10;
	/**
	 * 页面总数是否已设置
	 */
	private boolean pageCountSet = false;
	/**
	 * 是否加载完毕
	 */
	private boolean reachEnd = false;

	public PageableRequestProxy(PageableRequest request) {
		setRequest(request);
	}

	public PageableRequestProxy() {

	}

	public void setRequest(PageableRequest request) {
		this.request = request;
		if (request.getCurrentPage() < 1 || request.getPageSize() < 1)
			reset();
	}

	/**
	 * 重置
	 */
	public void reset() {
		setCurrentPage(1);
		setPageSize(pageSize);
		pageCountSet = false;
		reachEnd = false;
		totalCount = 0;
		pageCount = 0;
	}

	public final int getTotalCount() {
		return totalCount;
	}

	/**
	 * 设置总页数
	 * 
	 * @param totalCount
	 */
	public final void setTotalCount(int totalCount) {
		this.totalCount = totalCount;
		pageCount = (totalCount + request.getPageSize() - 1) / request.getPageSize();
		pageCountSet = true;
	}

	public int getPageCount() {
		return pageCount;
	}

	public void setPageCount(int pageCount) {
		this.pageCount = pageCount;
		pageCountSet = true;
	}

	/**
	 * 是否还有下一页，由于在第一次请求时，并不知道总的条数，所以第一次请求总是返回{@code true}
	 * 
	 * @return
	 */
	public boolean hasNextPage() {
		// return params.getCurrentpage() == 1 || (params.getCurrentpage() - 1)
		// * params.getPagesize() < totalCount;
		// System.err.println("currentpage :" + request.getCurrentPage() +
		// ", pageCount:" + pageCount);
		if (reachEnd)
			return false;
		return !pageCountSet || request.getCurrentPage() <= pageCount;
	}

	public void setReachEnd(boolean reachEnd) {
		reachEnd = true;
	}

	/**
	 * 设置每一页加载的条数
	 * 
	 * @param pageSize
	 */
	public void setPageSize(int pageSize) {
		request.setPageSize(pageSize);
	}

	public void setCurrentPage(int currentPage) {
		if (currentPage < 0)
			throw new IllegalArgumentException();
		request.setCurrentPage(currentPage);
	}

	public boolean isFirstPage() {
		return request.getCurrentPage() == 1;
	}

	public int getCurrentPage() {
		return request.getCurrentPage();
	}

	/**
	 * 设置参数为加载下一页
	 */
	public void toNextPage() {
		request.setCurrentPage(request.getCurrentPage() + 1);
	}

	/**
	 * 设置参数为加载上一页
	 */
	public void toPreviousPage() {
		if (request.getCurrentPage() > 1)
			request.setCurrentPage(request.getCurrentPage() - 1);
	}

	public final PageableRequest getRequest() {
		return request;
	}

	// public int getPageCount() {
	// return pageCount;
	// }
	//
	// public void setPageCount(int pageCount) {
	// this.pageCount = pageCount;
	// countSet = true;
	// }

	public String getMethod() {
		return request.getMethod();
	}

	// public String getCheckCode() {
	// return request.getCheckCode();
	// };

}
