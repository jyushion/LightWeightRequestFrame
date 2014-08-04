package org.fans.frame.api.executor;

import org.fans.frame.adapter.ListAdapter;

/**
 * 可分页ListView
 * 
 * @author ludq@hyxt.com
 * 
 */
public interface PageableListView {

	public void setOnFooterRefreshListener(OnFooterRefreshListener footerRefreshListener);

	public void onFooterRefreshComplete();

	public void onAllFooterRefreshComplete();

	public ListAdapter<?> getListAdapter();

	public void reset();

	public interface OnFooterRefreshListener {
		public void onRefresh(PageableListView pageableListView);
	}

}