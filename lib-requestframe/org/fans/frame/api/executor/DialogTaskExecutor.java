package org.fans.frame.api.executor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.fans.frame.FansApplication;
import org.fans.frame.api.packet.ApiRequest;
import org.fans.frame.api.packet.ApiResponse;
import org.fans.frame.utils.Logger;

import android.content.Context;
import android.content.DialogInterface;

import com.android.volley.Request.Method;
import com.android.volley.RequestQueue;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;

/**
 * 提供对异步任务的配置，执行、取消及回调。
 * 
 * @author Ludaiqian
 * @since 1.0
 */
public class DialogTaskExecutor {

	// private DialogTask task;
	private Map<String, DialogTask> requestsCache;

	private Map<Context, List<DialogTask>> contextCache;
	private ParamsBuilderProvider paramsBuildersProvider;
	public static final DialogTaskExecutor DEFAULT_TASK_EXECUTOR = new DialogTaskExecutor();
	private KeyGenerator keyGenerator;
	private Serializer serializer;
	private DialogProvider dialogProvider;
	private String url;
	private int method = Method.POST;
	private boolean stop = false;
	private boolean configured = false;
	private RequestQueue requestQueue;

	private DialogTaskExecutor() {
		requestsCache = new HashMap<String, DialogTask>();
		requestQueue = Volley.newRequestQueue(FansApplication.getInstance());
		contextCache = new HashMap<Context, List<DialogTask>>();
	}

	public static DialogTaskExecutor getInstance() {
		return DEFAULT_TASK_EXECUTOR;
	}

	/**
	 * 配置一些不易变的参数，若为null将会使用默认值
	 * 
	 * 
	 * @param url
	 * @param method
	 * @param serializer
	 * @param paramsBuildersProvider
	 *            提供{@link ParamsBuilder}
	 * @param keyGenerator
	 *            key生成器
	 * @param loadingDialog
	 *            加载条
	 * @return
	 */
	public DialogTaskExecutor config(String url, int method, Serializer serializer, ParamsBuilderProvider paramsBuildersProvider,
			DialogProvider provider, KeyGenerator keyGenerator) {
		this.url = url;
		this.method = method;
		this.serializer = serializer;
		this.paramsBuildersProvider = paramsBuildersProvider;
		this.keyGenerator = keyGenerator;
		this.dialogProvider = provider;
		configured = true;
		return this;
	}

	/**
	 * 执行一个请求，默认用Request对象的toString标志其唯一性
	 * 
	 * @param context
	 * @param showDialog
	 *            是否显示对话框
	 * @param responseInUIThread
	 *            是否需要在UI线程中返回
	 * @param resultPicker
	 * @param requests
	 */

	public void execute(final Context context, boolean showDialog, final TaskResultPicker resultPicker,
			final ApiRequest... requests) {
		// requestsCache.put(generateKey(true, request), value);
		checkIfReleased();
		checkNecessaryFeildsIfNull();
		final String key = keyGenerator != null ? keyGenerator.generateKey(requests) : //
				DEFAULT_KEY_GENERATOR.generateKey(requests);
		if (!requestsCache.containsKey(key)) {
			DialogTask task = new DialogTask(context, requestQueue) {
				@Override
				public void onPreExecute() {
					super.onPreExecute();
					// requestQueue.start();
					resultPicker.onPrepareExecute(requests);
				}

				@Override
				public void doStuffWithResult(ApiRequest request, ApiResponse<?> result) {
					if (result.isSuccess())
						resultPicker.doStuffWithResult(request, result);
					else {
						VolleyError error = null;
						if (result.getError() != null) {
							error = new ResponseError(result.getError());
						}
						resultPicker.onRequestFailed(request, result.getMessage(), error);
					}

				}

				@Override
				public void onRequestFailed(ApiRequest request, String reason, VolleyError error) {
					super.onRequestFailed(request, reason, error);
					resultPicker.onRequestFailed(request, reason, error);
				}

				@Override
				public void onCancel(DialogInterface dialog) {
					super.onCancel(dialog);
				}

				@Override
				protected void onFinishExecuted() {
					super.onFinishExecuted();
					requestsCache.remove(key);
					List<DialogTask> taskList = contextCache.get(context);
					if (taskList != null) {
						// contextCache.remove(`)
						taskList.remove(this);
						if (taskList.size() == 0) {
							// taskList.clear();
							contextCache.remove(context);
						}
					}
				}
			};
			cacheContextTask(context, task);
			requestsCache.put(key, task);
			task.setUrl(url);
			task.setMethod(method);
			task.setParamsBuilderProvider(paramsBuildersProvider);
			task.setSerializer(serializer);
			if (dialogProvider != null)
				task.setProgressDialog(dialogProvider.provide(context));
			task.setDialogVisible(showDialog);
			task.execute(requests);
		}
	}

	private void cacheContextTask(Context context, DialogTask task) {
		List<DialogTask> tasks = contextCache.get(context);
		if (tasks == null) {
			tasks = new ArrayList<DialogTask>();
			contextCache.put(context, tasks);
		}
		tasks.add(task);
	}

	private void checkIfReleased() {
		if (stop)
			throw new IllegalStateException("executor was stoped.");
	}

	private void checkNecessaryFeildsIfNull() {
		if (url == null)
			throw new NullPointerException("url is missing,please config url before execute");
	}

	public void execute(Context context, final TaskResultPicker resultPicker, final ApiRequest... requests) {
		execute(context, true, resultPicker, requests);
	}

	private static final KeyGenerator DEFAULT_KEY_GENERATOR = new KeyGenerator() {

		@Override
		public String generateKey(ApiRequest... requestArray) {
			StringBuilder buffer = new StringBuilder();
			for (ApiRequest request : requestArray) {
				buffer.append(request.toString()).append("&");
			}
			// + requestArray.toString() + "&" //
			// + requestArray.length;
			return buffer.toString();
		}
	};

	public interface TaskResultPicker {
		void doStuffWithResult(ApiRequest request, ApiResponse<?> result);

		void onPrepareExecute(ApiRequest... requests);

		void onRequestFailed(ApiRequest request, String reason, VolleyError error);
	}

	public interface KeyGenerator {
		public String generateKey(ApiRequest... requestArray);
	}

	public void cancleAll() {
		for (Entry<String, DialogTask> entry : requestsCache.entrySet()) {
			entry.getValue().cancle();
		}
		requestsCache.clear();
		contextCache.clear();
	}

	/**
	 * 取消一个现有的任务队列或任务队列
	 */
	public void cancle(ApiRequest... apiRequest) {
		KeyGenerator keyGenerator = this.keyGenerator == null ? DEFAULT_KEY_GENERATOR : this.keyGenerator;
		DialogTask task = requestsCache.remove(keyGenerator.generateKey(apiRequest));
		if (task != null) {
			task.cancle();
		}
		Logger.d("cancel task :" + task);
	}

	/**
	 * 取消与context相关的任务
	 * 
	 * @param context
	 */
	public void cancle(Context context) {
		List<DialogTask> taskList = contextCache.remove(context);
		if (taskList != null) {
			for (DialogTask task : taskList) {
				task.cancle();
				Logger.d("cancel task :" + task);
			}
		}
	}

	public DialogTask findTask(ApiRequest... apiRequest) {
		KeyGenerator keyGenerator = this.keyGenerator == null ? DEFAULT_KEY_GENERATOR : this.keyGenerator;
		DialogTask task = requestsCache.get(keyGenerator.generateKey(apiRequest));
		return task;
	}

	// public void re

	public boolean isConfigured() {
		return configured;
	}

	public boolean isStopped() {
		return stop;
	}

	public void restart() {
		requestQueue.start();
		stop = false;
	}

	public void stop() {
		try {

			cancleAll();
			requestQueue.stop();
		} catch (Exception e) {
			e.printStackTrace();
		}
		stop = true;
	}
}