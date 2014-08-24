package org.fans.frame.fragment;

import java.util.HashMap;
import java.util.HashSet;

import org.fans.frame.api.executor.DialogTaskExecutor;
import org.fans.frame.api.executor.DialogTaskExecutor.TaskResultPicker;
import org.fans.frame.api.packet.ApiRequest;
import org.fans.frame.api.packet.ApiResponse;
import org.fans.frame.utils.Logger;
import org.fans.frame.utils.NetworkUtil;
import org.fans.frame.utils.ToastMaster;
import org.fans.frame.utils.Utils;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.os.Bundle;

import com.android.volley.VolleyError;
import com.fans.frame.R;

public abstract class NetworkFragment extends BaseFragment implements TaskResultPicker {

	// private DialogTask task;
	private DialogTaskExecutor executor;
	protected Dialog dialog;
	private HashSet<ApiRequest[]> requests = new HashSet<ApiRequest[]>();

	public interface RequestState {
		public static final int REQUEST_PREPARED = 0;
		public static final int REQUEST_SUCCESSED = 1;
		public static final int REQUEST_FAILED = 1;
	}

	private HashMap<ApiRequest, Integer> requestStates;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		executor = Utils.getDefaultExecutor(getActivity());
		requestStates = new HashMap<ApiRequest, Integer>();
	}

	private TaskResultPicker filter = new TaskResultPicker() {

		@Override
		public void onRequestFailed(ApiRequest request, String reason, VolleyError error) {
			if (!isAdded()) {
				Logger.e("not attatched to activity");
			} else {
				NetworkFragment.this.onRequestFailed(request, reason, error);

				requestStates.put(request, RequestState.REQUEST_FAILED);
			}
		}

		@Override
		public void onPrepareExecute(ApiRequest... requests) {

		}

		@Override
		public void doStuffWithResult(ApiRequest request, ApiResponse<?> result) {
			if (!isAdded()) {
				Logger.e("not attatched to activity");
			} else {
				requestStates.put(request, RequestState.REQUEST_SUCCESSED);
				NetworkFragment.this.doStuffWithResult(request, result);
			}
		}
	};

	/**
	 * 向服务端发出一个或多个请求
	 * 
	 * @param showDialog
	 *            是否显示对话框
	 * @param apiRequests
	 *            请求队列
	 */
	public void asynRequest(boolean showDialog, final ApiRequest... apiRequests) {
		for (ApiRequest request : apiRequests) {
			requestStates.put(request, RequestState.REQUEST_PREPARED);
		}
		requests.add(apiRequests);
		executor.execute(getActivity(), showDialog, filter, apiRequests);
	}

	/**
	 * 向服务端发出一个或多个请求
	 * 
	 * @param apiRequests
	 */
	public void asynRequest(final ApiRequest... apiRequests) {
		asynRequest(true, apiRequests);

	}

	/**
	 * 只运行一次
	 */
	public void onPrepareData() {
		dialog = new ProgressDialog(getActivity());
	}

	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		for (ApiRequest[] request : requests)
			executor.cancle(request);
		requests.clear();
	}

	/**
	 * 请求成功的回调
	 * 
	 * @param request
	 * @param result
	 */
	public void doStuffWithResult(ApiRequest request, ApiResponse<?> result) {

	}

	/**
	 * 请求失败的回调
	 * 
	 * @param request
	 * @param reason
	 * @param error
	 */
	public void onRequestFailed(ApiRequest request, String reason, VolleyError error) {
		toastWithDefault();
	}

	@Override
	public void onPrepareExecute(ApiRequest... requests) {

	}

	public int getRequestState(ApiRequest request) {
		return requestStates.get(request);
	}

	protected void toastWithDefault() {
		if (!NetworkUtil.isNetworkAvailable(getActivity())) {
			ToastMaster.popToast(getActivity(), R.string.network_unavailable);
		} else {
			ToastMaster.popToast(getActivity(), R.string.request_failed);
		}
	}
}
