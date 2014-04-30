package org.fans.frame.adapter;

import org.fans.frame.adapter.ListAdapter;
import org.fans.frame.api.executor.DialogTaskExecutor;
import org.fans.frame.api.executor.DialogTaskExecutor.TaskResultPicker;
import org.fans.frame.api.packet.ApiRequest;
import org.fans.frame.api.packet.ApiResponse;
import org.fans.frame.utils.Utils;

import android.content.Context;

import com.android.volley.VolleyError;

public abstract class NetworkAdapter<T> extends ListAdapter<T> implements TaskResultPicker {
	// private DialogTask task;
	private DialogTaskExecutor executor;

	public NetworkAdapter(Context context) {
		super(context);
		executor =Utils.getDefaultExecutor(context);
	}

	/**
	 * 向服务端发出一个或多个请求
	 * 
	 * realise* @param apiRequests
	 */
	public void asynRequest(final ApiRequest... apiRequests) {
		asynRequest(true, apiRequests);
	}

	/**
	 * 向服务端发出一个或多个请求
	 * 
	 * @param showDialog
	 *            是否显示对话框
	 * @param apiRequests
	 *            请求队列
	 */
	public void asynRequest(boolean showDialog, final ApiRequest... apiRequests) {
		executor.execute(mContext, showDialog, this, apiRequests);
	}

	@Override
	public void doStuffWithResult(ApiRequest request, ApiResponse<?> result) {

	}

	@Override
	public void onPrepareExecute(ApiRequest... requests) {

	}

	@Override
	public void onRequestFailed(ApiRequest request, String reason, VolleyError error) {

	}

}
