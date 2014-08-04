package org.fans.frame.api.executor;

import org.fans.frame.adapter.ListAdapter;

/**
 * 可分页ListView
 * 
 * @author ludq@hyxt.com
 * 
 */
public interface LazyLoadListView {

	public void setOnFooterRefreshListener(OnFooterRefreshListener footerRefreshListener);

	public void onFooterRefreshComplete();

	public void onAllFooterRefreshComplete();

	public void onLoadingFailed(int faildPage);

	public ListAdapter<?> getListAdapter();

	public void reset();

	public interface OnFooterRefreshListener {
		public void onRefresh(LazyLoadListView pageableListView);
	}

}