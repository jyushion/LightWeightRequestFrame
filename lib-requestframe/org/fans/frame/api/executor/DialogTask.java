/*
 * Copyright (C) 2009 Teleca Poland Sp. z o.o. <android@teleca.com>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.fans.frame.api.executor;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.ListIterator;

import org.fans.frame.api.packet.ApiRequest;
import org.fans.frame.api.packet.ApiResponse;
import org.fans.frame.api.toolbox.DefaultParamsBuilder;
import org.fans.frame.utils.Logger;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;

import com.android.volley.Request;
import com.android.volley.Request.Method;
import com.android.volley.RequestQueue;
import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.android.volley.VolleyError;

/**
 * 执行一个任务队列，并可以显示一个进度条，当所有任务完成时，这个进度条才会消失<br>
 * Wrapper around UserTasks &ProgressDialog
 * 
 * @author Lukasz Wisniewski
 * @author Ludaiqian
 * @since 1.0
 */
public abstract class DialogTask implements DialogInterface.OnCancelListener {

	private Dialog mProgressDialog;
	protected Context mContext;
	// private int mFailMsg;
	private RequestQueue requestQueue;
	private ArrayList<StringRequest> runningRequests;
	private boolean dialogVisible = true;
	private Serializer serializer;
	private ParamsBuilderProvider provider;
	private String url;
	private int method = Method.POST;
	private HashMap<ApiRequest, Request<?>> requestsCache;

	public DialogTask(Context context, RequestQueue requestQueue) {
		this.mContext = context;
		this.requestQueue = requestQueue;
		runningRequests = new ArrayList<StringRequest>();
		requestsCache = new HashMap<ApiRequest, Request<?>>();
	}

	public void setSerializer(Serializer serializer) {
		this.serializer = serializer;
	}

	public void setRequestQueue(RequestQueue requestQueue) {
		this.requestQueue = requestQueue;
	}

	public void setProgressDialog(Dialog progressDialog) {
		this.mProgressDialog = progressDialog;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public void onPreExecute() {
		if (dialogVisible) {
			// --------
			// if (mProgressDialog == null) {
			// mProgressDialog = new LoadingDialog(mContext);
			// }
			// --------
			if (mProgressDialog != null) {
				mProgressDialog.setCanceledOnTouchOutside(false);

				mProgressDialog.show();
				mProgressDialog.setOnCancelListener(this);
			}
		}
	}

	public void execute(ApiRequest... requests) {
		// this.requests=requests ;
		onPreExecute();
		for (final ApiRequest apiRequest : requests) {
			if (apiRequest.getMethod() == null) {
				throw new VerifyError("request method can not be null");
			}
			/**
			 * 成功响应监听
			 */
			Listener<String> listener = new Listener<String>() {

				@Override
				public void onResponse(String response) {
					Type clazz = ResponseTypeProvider.getInstance().getApiResponseType(apiRequest.getMethod());
					System.out.println("result:" + response);
					final ApiResponse<?> apiResponse = serializer != null ? serializer.deserialize(response, clazz)
							: JsonSerializer.DEFAULT.deserialize(response, clazz);
					onProcessing(apiRequest, apiResponse);
				}
			};
			/**
			 * 失败响应监听
			 */
			ErrorListener errorListener = new ErrorListener() {

				@Override
				public void onErrorResponse(final VolleyError error) {
					onRequestFailed(apiRequest, null, error);
				}
			};
			StringRequest request = new RequestBuilder()
					//
					.setUrl(url)
					//
					// .setNamespace(Constants.NAMESPACE)//

					// .setSoapAction(Constants.SOAP_ACTION)//
					// .setMethod(apiRequest.getMethod())//
					.setMethod(method)
					.setParamsBuilder(provider != null ? provider.provide(apiRequest) : new DefaultParamsBuilder(apiRequest))//
					.setListner(listener)//
					.setErrorListener(errorListener)//
					// .setApiRequest(apiRequest)//
					.build();
			requestQueue.add(request);
			runningRequests.add(request);
			// requests.a
			// requestQueue.size();
			requestsCache.put(apiRequest, request);

		}
	}

	public void setParamsBuilderProvider(ParamsBuilderProvider provider) {
		this.provider = provider;
	}

	public void onProcessing(ApiRequest request, ApiResponse<?> result) {
		doStuffWithResult(request, result);
		checkIfTaskFinished();
	}

	private void checkIfTaskFinished() {
		ListIterator<StringRequest> iterator = runningRequests.listIterator();
		while (iterator.hasNext()) {
			StringRequest request = iterator.next();
			if (request.hasHadRequestFailed() || request.hasHadResponseDelivered()) {
				iterator.remove();
			}
		}
		if (runningRequests.size() == 0) {
			onTaskFinished();
		}

	}

	public static ParamsBuilder newParamsBuilder(ApiRequest request) {
		return new DefaultParamsBuilder(request);
	}

	// private void dismissDialog() {
	// if (mProgressDialog != null)
	// mProgressDialog.dismiss();
	// }
	/**
	 * 当队列的全部request执行完成或者取消时调用
	 */
	protected void onFinishExecuted() {
		Logger.i("finished executed:"+this);
	}

	/**
	 * Very abstract function hopefully very meaningful name, executed when
	 * result is other than null
	 * 
	 * @param result
	 * @return
	 */
	public abstract void doStuffWithResult(ApiRequest request, ApiResponse<?> result);

	public void onRequestFailed(ApiRequest request, String reason, VolleyError error) {
		checkIfTaskFinished();

	}

	public boolean isDialogVisible() {
		return dialogVisible;
	}

	public void setDialogVisible(boolean visible) {
		this.dialogVisible = visible;
	}

	public void dismissDialog() {
		if (mProgressDialog != null)
			mProgressDialog.dismiss();
		// requestQueue.stop();
	}

	public static ParamsBuilder getParamsBuilder(Class<? extends ParamsBuilder> type, ApiRequest request) {
		if (type != null)
			try {
				return type.getConstructor(ApiRequest.class).newInstance(request);
			} catch (Exception e) {

			}
		return new DefaultParamsBuilder(request);
	}

	public void setMethod(int method) {
		this.method = method;
	}

	@Override
	public void onCancel(DialogInterface dialog) {
		cancleRequests();
		onFinishExecuted();
		// return ;
	}

	public void cancle() {
		cancleRequests();
		onTaskFinished();
	}

	public void cancle(ApiRequest apiRquest) {
		Request<?> request = requestsCache.remove(apiRquest);
		request.cancel();
		if (requestsCache.size() == 0) {
			onTaskFinished();
			//
		}
	}

	private void onTaskFinished() {
		dismissDialog();
		onFinishExecuted();
	}

	private void cancleRequests() {
		for (Request<?> cache : requestsCache.values()) {
			cache.cancel();
		}
		requestsCache.clear();
	}

}
