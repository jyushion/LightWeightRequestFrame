package org.fans.frame.api.packet.demo.response;

import org.fans.frame.api.packet.PageableResponse;

public class PageableResposneImpl<T> extends BaseApiResponse<T> implements PageableResponse<T> {
	/**
	 * 接口返回数据总量
	 */
	private int totalCount = 0;
	/**
	 * 接口返回页面总数
	 */
	private int pageCount = 0;
	/**
	 * 接口返回是否已经加载完毕
	 */
	private boolean reachEnd = false;


	public void setTotalCount(int totalCount) {
		this.totalCount = totalCount;
	}

	public int getPageCount() {
		return pageCount;
	}

	public void setPageCount(int pageCount) {
		this.pageCount = pageCount;
	}

	public boolean isReachEnd() {
		return reachEnd;
	}

	public void setReachEnd(boolean reachEnd) {
		this.reachEnd = reachEnd;
	}

	@Override
	public int getTotalCount() {
		return totalCount;
	}

}
