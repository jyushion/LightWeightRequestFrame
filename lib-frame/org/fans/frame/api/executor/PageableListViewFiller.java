package org.fans.frame.api.executor;

import java.util.Collection;

import org.fans.frame.adapter.ListAdapter;
import org.fans.frame.api.executor.DialogTaskExecutor.TaskResultPicker;
import org.fans.frame.api.executor.PageableListView.OnFooterRefreshListener;
import org.fans.frame.api.packet.ApiRequest;
import org.fans.frame.api.packet.ApiResponse;
import org.fans.frame.api.packet.PageableRequest;
import org.fans.frame.api.packet.PageableRequestProxy;
import org.fans.frame.api.packet.PageableResponse;

import android.content.Context;
import android.text.TextUtils;
import android.widget.Toast;

import com.android.volley.VolleyError;

/**
 * 对一个可分页的ListView:{@link PageableListView}进行异步填充,在填充之前，必须保证已经完成对 Adapter的配置
 * {@link PageableListView#setAdapter(ListAdapter)}
 * 
 * 
 * @author LuDaiqian
 * 
 */
public class PageableListViewFiller implements AsyncListViewFiller, TaskResultPicker {

	protected static final String TAG = "PageableListViewFiller";
	private PageableListView mListView;
	private Context mContext;
	private PageableRequestProxy mRequestProxy;
	@SuppressWarnings("rawtypes")
	private ListAdapter mAdapter;
	// private int mFailure;
	private OnFilledListenr mOnFilledListenr;
	private CollectionFetcher fetcher;
	private DialogTaskExecutor executor = DialogTaskExecutor.DEFAULT_TASK_EXECUTOR;
	private String faildMsg;

	public PageableListViewFiller(Context context, PageableRequest request, PageableListView listView) {
		mContext = context;
		mRequestProxy = new PageableRequestProxy(request);
		mListView = listView;
		mAdapter = mListView.getListAdapter();
		if (mAdapter == null || mAdapter.getList() == null) {
			throw new NullPointerException("you must set ListView and list before fill list");
		}
		mListView.setOnFooterRefreshListener(new OnFooterRefreshListener() {

			@Override
			public void onRefresh(PageableListView listView) {
				System.out.println("onrefresh");
				if (mRequestProxy.hasNextPage()) {
					executeTask();
				}
			}
		});
	}

	public void setFaildMsg(String faildMsg) {
		this.faildMsg = faildMsg;
	}

	public void startFillList() {
		executeTask();
	}

	public void setCollectionFetcher(CollectionFetcher fetcher) {
		this.fetcher = fetcher;
	}

	private void executeTask() {
		// System.out.println(mRequestProxy);
		// System.out.println(mRequestProxy.getRequest());
		executor.execute(mContext, isEmpty(), this, mRequestProxy.getRequest());

	}

	private void prepareNext() {
		mRequestProxy.toNextPage();
		if (mRequestProxy.hasNextPage()) {
			mListView.onFooterRefreshComplete();
		} else {
			mListView.onAllFooterRefreshComplete();
		}
	}

	public boolean isEmpty() {
		return mAdapter.getList().size() == 0;
	}

	public void setOnFilledListenr(OnFilledListenr onFillListenr) {
		this.mOnFilledListenr = onFillListenr;
	}

	public interface OnFilledListenr {
		public void onFilled(int currentPage, boolean hasNextPage, PageableRequest currentRequest, ApiResponse<?> response);
	}

	public void setCurrentPage(int currentPage) {
		mRequestProxy.setCurrentPage(currentPage);
		if (!mRequestProxy.hasNextPage()) {
			mListView.onAllFooterRefreshComplete();
		}
	}

	public void reset() {
		mRequestProxy.reset();
		mAdapter.clear();
		mListView.reset();
	}

	public void setRequest(PageableRequest request) {
		mRequestProxy.setRequest(request);
	}

	public PageableRequest getRequest() {
		return mRequestProxy.getRequest();
	}

	@SuppressWarnings("unchecked")
	@Override
	public void doStuffWithResult(ApiRequest request, ApiResponse<?> result) {
		// System.out.println("result:" + result);
		Collection<?> collection = null;
		if (fetcher != null) {
			collection = fetcher.fetch(result);
		} else {
			collection = (Collection<?>) result.getData();
		}
		if (result instanceof PageableResponse) {
			PageableResponse<?> response = (PageableResponse<?>) result;
			mRequestProxy.setReachEnd(response.isReachEnd());
			if (response.getPageCount() > 0)
				mRequestProxy.setPageCount(response.getPageCount());
			if (response.getTotalCount() > 0)
				mRequestProxy.setTotalCount(response.getTotalCount());

		}
		// 当成功返回且数据内容为空，则判定为加载完毕。
		boolean reachEnd = collection == null || collection.size() == 0;
		mRequestProxy.setReachEnd(reachEnd);
		// if (mRequestProxy.getCurrentPage() == 1)
		// mAdapter.clear();
		if (collection != null)
			mAdapter.addAll(collection);
		// Log.i(TAG, "successful add all.......");
		prepareNext();
		if (mOnFilledListenr != null)
			mOnFilledListenr.onFilled(mRequestProxy.getCurrentPage(), mRequestProxy.hasNextPage(), (PageableRequest) request,
					result);
	}

	@Override
	public void onPrepareExecute(ApiRequest... requests) {

	}

	@Override
	public void onRequestFailed(ApiRequest request, String reason, VolleyError error) {
		// if (mFailure++ < MAX_FAILURES) {
		// executeTask();
		// } else {
		// ToastMaster.popToast(mContext, R.string.load_faild);
		// }
		if (!TextUtils.isEmpty(faildMsg))
			Toast.makeText(mContext, faildMsg, Toast.LENGTH_SHORT).show();
		mListView.onFooterRefreshComplete();
	}
}
