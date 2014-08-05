package org.fans.frame.api.executor;

import java.util.HashMap;

import org.fans.frame.api.packet.ApiRequest;

import com.android.volley.Request;
import com.android.volley.Request.Method;
import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;

/**
 * 根据给定{@link ApiRequest} 构建一个{@link Request}
 * 
 * @author Ludaiqian
 * @since 1.0
 */
public class RequestBuilder {
	private String url;
	private int method = Method.GET;
	private Listener<?> listener;
	private ErrorListener errorListener;
	private ParamsBuilder paramsBuilder;

	public RequestBuilder setUrl(String url) {
		this.url = url;
		return this;
	}

	/**
	 * GET POST {@link Method}
	 * 
	 * @param method
	 * @return
	 */
	public RequestBuilder setMethod(int method) {
		this.method = method;
		return this;
	}

	public RequestBuilder setListner(Listener<?> listener) {
		this.listener = listener;
		return this;
	}

	public RequestBuilder setErrorListener(ErrorListener errorListener) {
		this.errorListener = errorListener;
		return this;
	}

	public RequestBuilder setParamsBuilder(ParamsBuilder paramsBuilder) {
		this.paramsBuilder = paramsBuilder;
		return this;
	}

	// public ApiRequestUrlBuilder

	@SuppressWarnings("unchecked")
	public StringRequest build() {
		checkNecessaryFeildsIfNull();
		paramsBuilder.setUrl(url);
		String requestUrl;
		HashMap<String, String> params = null;
		if (method != Method.POST && method != Method.PUT) {
			requestUrl = paramsBuilder.toHttpGetUrl();
		} else {
			requestUrl = paramsBuilder.getRequestUrl();
			params = paramsBuilder.getPostParams();
		}
		StringRequest request = new StringRequest(method, requestUrl, (Listener<String>) listener, errorListener);
		request.setHeaders(paramsBuilder.getHeaders());
		request.setBodyContentType(paramsBuilder.getContentType());
		request.setParams(params);
		request.setFormHeader(paramsBuilder.getFormHeaders());
		request.setBody(paramsBuilder.getPostBody());
		request.setFormFiles(paramsBuilder.getFormFiles());
		return request;

	}

	private void checkNecessaryFeildsIfNull() {
		if (url == null)
			throw new NullPointerException("url cannot be null.");
		if (paramsBuilder == null)
			throw new NullPointerException("paramsBuilder cannot be null.");
	}

}
