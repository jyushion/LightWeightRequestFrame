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
 * @author Ludaiqian
 * @since 1.0
 */
public class StringRequest extends com.android.volley.toolbox.StringRequest {
	private HashMap<String, String> params;
	private HashMap<String, String> headers;
	private boolean requestFailed = false;

	private byte[] body;
	private String bodyContentType;

	private FormFile[] formFiles;
	private FormHeader[] formHeader;

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

	public void setBodyContentType(String bodyContentType) {
		this.bodyContentType = bodyContentType;
	}

	// return getBodyContentType();
	@Override
	public String getBodyContentType() {
		return bodyContentType != null ? bodyContentType : super.getBodyContentType();
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

	public void addHeadersForPostFormFiles() {
		// addHeader("Accept",
		// "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,*/*;q=0.8");
		// addHeader("Charsert", "UTF-8");
		addHeader("Content-Type", "multipart/form-data; boundary=" + BOUNDARY);
	}

	public void setFormFiles(FormFile[] formFiles) {
		this.formFiles = formFiles;
	}

	public void setFormHeader(FormHeader[] formHeader) {
		this.formHeader = formHeader;
	}

	@Override
	public FormFile[] getPostFormFiles() {
		return formFiles != null ? formFiles : super.getPostFormFiles();
	}

	@Override
	public FormHeader[] getPostFormFileHeaders() {
		return formHeader != null ? formHeader : super.getPostFormFileHeaders();
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

	/**
	 * 添加Header
	 * 
	 * @param headers
	 */
	public void addHeaders(HashMap<String, String> headers) {
		if (headers == null)
			headers = new HashMap<String, String>();
		this.headers.putAll(headers);
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
		markFailed();
		super.deliverError(error);
	}

	public void markFailed() {
		requestFailed = true;
	}
}
