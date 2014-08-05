package org.fans.frame.widget;

import org.fans.frame.adapter.ListAdapter;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.fans.frame.R;

public class LazyloadListView extends RelativeLayout implements OnScrollListener, org.fans.frame.api.executor.LazyLoadListView {

	// empty view
	private FrameLayout mEmptyView;
	private ProgressBar mEmptyLoading;
	private TextView mEmptyNoData;
	/**
	 * 初始化状态
	 */
	private final static int FOOTER_INITIATED_STATE = 100;
	/**
	 * 可加载状态
	 */
	private final static int FOOTER_PREPARE_TO_REFRESH = 101;
	/**
	 * 正在加载状态
	 */
	private final static int FOOTER_ON_REFRESH = 102;
	/**
	 * 加载完毕状态
	 */
	private final static int FOOTER_LOAD_FINISH = 103;
	/**
	 * 加载错误状态
	 */
	private final static int FOOTER_REFRESH_ERROR = 104;
	private int footerState;
	// footer
	private LinearLayout mFooterView;
	private ProgressBar mFooterLoading;
	private TextView mFooterLoadingMoreLable;

	private ListAdapter<?> mAdapter;

	private ListView mListView;

	private OnFooterRefreshListener mFooterRefreshListener;

	private int mNoDataPrompt = R.string.no_data;

	public void setNoDataPrompt(int resId) {
		mNoDataPrompt = resId;
	}

	public ListView getListView() {
		return mListView;
	}

	private int mNoDataDrawablePrompt = R.drawable.refresh_button_selector;

	public void setNoDataDrawablePrompt(int resId) {
		mNoDataDrawablePrompt = resId;
		if (mNoDataDrawablePrompt != 0 && mEmptyNoData != null) {
			mEmptyNoData.setCompoundDrawablesWithIntrinsicBounds(0, resId, 0, 0);
		}
	}

	private int mLoadFailPrompt = R.string.load_faild;

	public void setLoadFailPrompt(int resId) {
		mLoadFailPrompt = resId;
	}

	private OnClickListener retryClick = new OnClickListener() {

		@Override
		public void onClick(View v) {
			mEmptyLoading.setVisibility(View.VISIBLE);
			mEmptyNoData.setVisibility(View.GONE);
			// 数据未初始化，只要不加载完毕，均可加载
			if (footerState != FOOTER_LOAD_FINISH && mFooterRefreshListener != null) {
				onRefresh();
			}
		}
	};

	public LazyloadListView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init(context, attrs);
	}

	public LazyloadListView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init(context, attrs);
	}

	private void init(Context context, AttributeSet attrs) {

		LayoutInflater.from(context).inflate(R.layout.lazy_list_view, this);

		// TypedArray a =
		// context.obtainStyledAttributes(attrs,R.styleable.LazyloadView);

		mListView = (ListView) findViewById(R.id.list_view);

		mEmptyView = (FrameLayout) findViewById(R.id.loading);
		mEmptyView.setVisibility(View.GONE);
		mEmptyLoading = (ProgressBar) findViewById(R.id.progressbar);
		mEmptyNoData = (TextView) findViewById(R.id.retry);
		mEmptyNoData.setOnClickListener(retryClick);
		footerState = FOOTER_INITIATED_STATE;
		mListView.setEmptyView(mEmptyView);
		mListView.setOnScrollListener(this);

		createFooterView();
	}

	private View createFooterView() {

		mFooterView = (LinearLayout) LayoutInflater.from(getContext()).inflate(R.layout.footer, null);
		mListView.addFooterView(mFooterView, null, false);
		mFooterView.invalidate();
		mFooterView.setVisibility(View.GONE);
		mFooterLoading = (ProgressBar) findViewById(R.id.footer_progressbar);
		mFooterLoadingMoreLable = (TextView) findViewById(R.id.footer_retry);
		mFooterLoadingMoreLable.setText(R.string.loading_more);
		// mFooterLoadingMoreLable.setVisibility(View.VISIBLE);
		// mFooterLoading.setVisibility(View.VISIBLE);

		mFooterView.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// 手动刷新用于可加载状态，或者错误状态
				if ((footerState == FOOTER_PREPARE_TO_REFRESH || footerState == FOOTER_REFRESH_ERROR)
						&& mFooterRefreshListener != null) {
					mFooterLoading.setVisibility(View.VISIBLE);
					mFooterLoadingMoreLable.setText(R.string.loading_more);
					onRefresh();
				}
			}
		});

		return mFooterView;
	}

	//
	// @Override
	// public void setAdapter(ListAdapter adapter) {
	//
	// if(adapter instanceof LazyAdapter){
	// mAdapter = (LazyAdapter<?>) adapter;
	// mAdapter.setLazyloadListView(this);
	// mListView.setAdapter(mAdapter);
	// } else {
	// throw new
	// RuntimeException("the Adapter is not LazyListAdapter , must use the LazyListAdapter.");
	// }
	// }
	//

	public void setAdapter(ListAdapter<?> adapter) {
		this.mAdapter = adapter;
		mListView.setAdapter(mAdapter);
	}

	public void addHeaderView(View v) {
		mListView.addHeaderView(v, null, false);
	}

	public void setOnFooterRefreshListener(OnFooterRefreshListener listener) {
		mFooterRefreshListener = listener;
	}

	@Override
	public void onScrollStateChanged(AbsListView view, int scrollState) {

	}

	@Override
	public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
		if (mListView.getChildAt(0) != null && mListView.getChildAt(0).getTop() < 0
				&& mFooterView.getVisibility() != View.VISIBLE) {
			if (footerState == FOOTER_INITIATED_STATE)
				enableFooterForRefresh();
		}
		if (mAdapter != null) {
			mAdapter.onScroll(view, firstVisibleItem, visibleItemCount, totalItemCount);
			// 自动刷新仅限于可加载状态
			if (footerState == FOOTER_PREPARE_TO_REFRESH && mListView.getLastVisiblePosition() >= mAdapter.getCount()
					&& mAdapter.getCount() > 0) {
				onRefresh();

			}
		}

	}

	private void onRefresh() {
		footerState = FOOTER_ON_REFRESH;
		if (mFooterRefreshListener != null) {
			mFooterRefreshListener.onRefresh(LazyloadListView.this);
		}
	}

	@Override
	protected void onLayout(boolean changed, int l, int t, int r, int b) {
		super.onLayout(changed, l, t, r, b);
		if (mAdapter.getCount() <= 0) {
			mFooterView.setVisibility(View.GONE);
		} else if (mFooterView.getVisibility() != View.VISIBLE) {
			if (footerState == FOOTER_INITIATED_STATE)
				enableFooterForRefresh();
		}
	}

	private void enableFooterForRefresh() {
		System.out.println("enable footer");
		mFooterView.setVisibility(View.VISIBLE);
		mFooterLoadingMoreLable.setVisibility(View.VISIBLE);
		mFooterLoading.setVisibility(View.VISIBLE);
		if (footerState == FOOTER_INITIATED_STATE)
			footerState = FOOTER_PREPARE_TO_REFRESH;
		mFooterLoadingMoreLable.setText(R.string.loading_more);
		mFooterView.invalidate();
	}

	public void setOnItemClickListener(OnItemClickListener listener) {
		mListView.setOnItemClickListener(listener);
	}

	@Override
	public void onFooterRefreshComplete() {
		if (footerState == FOOTER_ON_REFRESH)
			footerState = FOOTER_PREPARE_TO_REFRESH;
		if (mAdapter.getCount() == 0) {
			mEmptyLoading.setVisibility(View.GONE);
			mEmptyNoData.setVisibility(View.VISIBLE);
			mEmptyNoData.setText(mLoadFailPrompt);
		} else if (mAdapter.getCount() > 0) {
			// fix bug
			mEmptyView.setVisibility(View.GONE);
			mListView.setVisibility(View.VISIBLE);

		}
		if (mListView.getFirstVisiblePosition() == 0 && mListView.getLastVisiblePosition() >= mListView.getAdapter().getCount()) {
			mFooterView.setVisibility(View.GONE);
		}
	}

	@Override
	public void onAllFooterRefreshComplete() {
		footerState = FOOTER_LOAD_FINISH;
		mFooterLoading.setVisibility(View.GONE);
		mFooterLoadingMoreLable.setText(R.string.load_finish);
		if ((mListView.getChildAt(0) == null || (mListView.getChildAt(0) != null && mListView.getChildAt(0).getTop() >= 0))
				&& mListView.getFirstVisiblePosition() == 0 && mListView.getLastVisiblePosition() >= mAdapter.getCount() - 1) {
			mFooterView.setVisibility(View.GONE);
		}
		// 如果没有数据，显示之
		if (mAdapter.getCount() == 0) {
			mEmptyNoData.setVisibility(View.VISIBLE);
			mEmptyNoData.setText(mNoDataPrompt);
		}
	}

	@Override
	public ListAdapter<?> getListAdapter() {
		return mAdapter;
	}

	@Override
	public void reset() {
		footerState = FOOTER_INITIATED_STATE;
		mFooterLoadingMoreLable.setVisibility(View.GONE);
		mFooterLoading.setVisibility(View.GONE);
		mFooterView.setVisibility(View.GONE);
	}

	@Override
	public void onLoadingFailed(int faildPage) {
		footerState = FOOTER_REFRESH_ERROR;
		if (mListView.getFirstVisiblePosition() == 0 && mListView.getLastVisiblePosition() >= mListView.getAdapter().getCount()) {
			mFooterView.setVisibility(View.GONE);
		}
		if (mAdapter.getCount() == 0) {
			mEmptyLoading.setVisibility(View.GONE);
			mEmptyNoData.setVisibility(View.VISIBLE);
			mEmptyNoData.setText(mLoadFailPrompt);
		} else {
			mFooterLoading.setVisibility(View.GONE);
			mFooterLoadingMoreLable.setVisibility(View.VISIBLE);
			mFooterLoadingMoreLable.setText(R.string.load_faild);
		}
	}
}
