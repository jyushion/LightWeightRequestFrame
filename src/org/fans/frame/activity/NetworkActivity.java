package org.fans.frame.activity;

import org.fans.frame.api.executor.DialogTaskExecutor;
import org.fans.frame.api.executor.DialogTaskExecutor.TaskResultPicker;
import org.fans.frame.api.packet.ApiRequest;
import org.fans.frame.api.packet.ApiResponse;
import org.fans.frame.utils.NetworkUtil;
import org.fans.frame.utils.ToastMaster;
import org.fans.frame.utils.Utils;

import android.os.Bundle;

import com.android.volley.VolleyError;
import com.fans.frame.R;

public class NetworkActivity extends BaseActivity implements TaskResultPicker {

	// private DialogTask task;
	private DialogTaskExecutor executor;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		executor = Utils.getDefaultExecutor(this);
	}

	@Override
	protected void onStart() {
		super.onStart();
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
		executor.execute(this, showDialog, this, apiRequests);
	}

	/**
	 * 向服务端发出一个或多个请求
	 * 
	 * realise* @param apiRequests
	 */
	public void asynRequest(final ApiRequest... apiRequests) {
		asynRequest(true, apiRequests);
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		executor.cancle(this);
		// executor.stop()在退出应用之前调用以便释放资源。
		// executor.stop();
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
	protected void toastWithDefault() {
		if(!NetworkUtil.isNetworkAvailable(this)){
			ToastMaster.popToast(this, R.string.network_unavailable);
		}else{
			ToastMaster.popToast(this, R.string.request_failed);
		}
	}

	@Override
	public void onPrepareExecute(ApiRequest... requests) {

	}

}
