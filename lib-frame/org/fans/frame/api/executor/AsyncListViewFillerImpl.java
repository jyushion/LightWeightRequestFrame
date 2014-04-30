package org.fans.frame.api.executor;

import java.util.Collection;

import org.fans.frame.adapter.ListAdapter;
import org.fans.frame.api.executor.DialogTaskExecutor.TaskResultPicker;
import org.fans.frame.api.packet.ApiRequest;
import org.fans.frame.api.packet.ApiResponse;

import android.content.Context;
import android.text.TextUtils;
import android.widget.HeaderViewListAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.android.volley.VolleyError;

/**
 * 对一个可分页的ListView进行异步填充,在填充之前，必须保证已经完成对 Adapter的配置
 * 
 * @author LuDaiqian
 * 
 */
public class AsyncListViewFillerImpl implements AsyncListViewFiller, TaskResultPicker {

	private ListView mListView;
	private Context mContext;
	@SuppressWarnings("rawtypes")
	private ListAdapter mAdapter;
	private ApiRequest request;
	private OnFilledListenr mOnFilledListenr;
	// private int mFailure;

	private CollectionFetcher fetcher;
	private DialogTaskExecutor executor = DialogTaskExecutor.DEFAULT_TASK_EXECUTOR;
	private String faildMsg;

	public AsyncListViewFillerImpl(Context context, ApiRequest request, ListView listView) {
		mContext = context;
		mListView = listView;
		this.request = request;
		if (mListView.getAdapter() instanceof HeaderViewListAdapter) {
			HeaderViewListAdapter wrapper = (HeaderViewListAdapter) mListView.getAdapter();
			mAdapter = (ListAdapter<?>) wrapper.getWrappedAdapter();
		} else
			mAdapter = (ListAdapter<?>) mListView.getAdapter();
		if (mAdapter == null || mAdapter.getList() == null) {
			throw new NullPointerException("you must set ListView and list before fill list");
		}

	}

	public void setFaildMsg(String faildMsg) {
		this.faildMsg = faildMsg;
	}

	@Override
	public void startFillList() {
		executor.execute(mContext, false, this, request);
	}

	public void setCollectionFetcher(CollectionFetcher fetcher) {
		this.fetcher = fetcher;
	}

	@Override
	public boolean isEmpty() {
		return mAdapter.getList().size() == 0;
	}

	public void setOnFilledListenr(OnFilledListenr onFillListenr) {
		this.mOnFilledListenr = onFillListenr;
	}

	public interface OnFilledListenr {
		public void onFilled(ApiResponse<?> response);
	}

	@SuppressWarnings("unchecked")
	@Override
	public void doStuffWithResult(ApiRequest request, ApiResponse<?> result) {

		if (result.isSuccess()) {
			if (mOnFilledListenr != null)
				mOnFilledListenr.onFilled(result);
			Collection<?> collection = null;
			if (fetcher != null) {
				collection = fetcher.fetch(result);
			} else {
				collection = (Collection<?>) result.getData();
			}
			if (collection != null)
				mAdapter.addAll(collection);
		}
	}

	@Override
	public void onPrepareExecute(ApiRequest... requests) {

	}

	@Override
	public void onRequestFailed(ApiRequest request, String reason, VolleyError error) {
		// if (mFailure++ < MAX_FAILURES) {
		// executor.execute(mContext, false, this, request);
		// } else {
		// ToastMaster.popToast(mContext, "加载失败");
		if (!TextUtils.isEmpty(faildMsg))
			Toast.makeText(mContext, faildMsg, Toast.LENGTH_SHORT).show();
		// }
	}
}
