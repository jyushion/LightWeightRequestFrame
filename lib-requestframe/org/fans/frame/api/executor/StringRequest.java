package org.fans.frame.api.executor;

import java.util.HashMap;
import java.util.Map;

import com.android.volley.AuthFailureError;
import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.android.volley.VolleyError;

/**
 * 扩展{@link com.android.volley.toolbox.StringRequest} 增强对Post等方式的支持
 * 
 * @author ludq@hyxt.com
 * 
 */
public class StringRequest extends com.android.volley.toolbox.StringRequest {
	private HashMap<String, String> params;
	private HashMap<String, String> headers;
	private boolean requestFailed = false;

	private byte[] body;

	StringRequest(int method, String url, Listener<String> listener, ErrorListener errorListener) {
		super(method, url, listener, errorListener);
	}

	StringRequest(String url, Listener<String> listener, ErrorListener errorListener) {
		super(url, listener, errorListener);
	}

	/**
	 * 设置Post方式请求时的参数Map
	 * 
	 * @param params
	 */
	public void setParams(HashMap<String, String> params) {
		this.params = params;
	}

	@Override
	protected Map<String, String> getParams() throws AuthFailureError {
		return params;
	}

	public void setBody(byte[] body) {
		this.body = body;
	}

	@Override
	public byte[] getBody() throws AuthFailureError {
		return body != null ? body : super.getBody();
	}

	@Override
	public byte[] getPostBody() throws AuthFailureError {
		return body != null ? body : super.getPostBody();
	}

	/**
	 * 添加Post方式请求时的参数
	 * 
	 * @param key
	 * @param value
	 */
	public void addParam(String key, String value) {
		if (params == null)
			params = new HashMap<String, String>();
		params.put(key, value);
	}

	/**
	 * 添加header
	 * 
	 * @param key
	 * @param value
	 */
	public void addHeader(String key, String value) {
		if (headers == null)
			headers = new HashMap<String, String>();
		headers.put(key, value);
	}

	public boolean hasHadRequestFailed() {
		return requestFailed;
	}

	/**
	 * 设置Header
	 * 
	 * @param headers
	 */
	public void setHeaders(HashMap<String, String> headers) {
		this.headers = headers;
	}

	@Override
	public Map<String, String> getHeaders() throws AuthFailureError {
		return headers != null ? headers : super.getHeaders();
	}

	@Override
	public void deliverError(VolleyError error) {
		super.deliverError(error);
		markFailed();
	}

	public void markFailed() {
		requestFailed = true;
	}
}
