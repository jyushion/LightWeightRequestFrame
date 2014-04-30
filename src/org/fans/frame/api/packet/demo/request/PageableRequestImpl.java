package org.fans.frame.api.packet.demo.request;

import org.fans.frame.api.packet.Name;
import org.fans.frame.api.packet.PageableRequest;

public class PageableRequestImpl extends BaseApiRequest implements PageableRequest {
	@Name("paqgeIndex")
	private int currentPage = 1;
	@Name("pageSize")
	private int pageSize = 10;

	public void setCurrentPage(int currentPage) {
		this.currentPage = currentPage;
	}

	public void setPageSize(int pageSize) {
		this.pageSize = pageSize;
	}

	public int getCurrentPage() {
		return currentPage;
	}

	public int getPageSize() {
		return pageSize;
	}


}
