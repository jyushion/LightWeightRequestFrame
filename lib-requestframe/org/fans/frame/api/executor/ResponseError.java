package org.fans.frame.api.executor;

import com.android.volley.VolleyError;

import org.fans.frame.api.packet.Error;

/**
 * 服务器返回错误信息的封装
 * 
 * @author Ludaiqian
 * @since 1.0
 */
public class ResponseError extends VolleyError {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private Error error;

	ResponseError(Error error) {
		super();
		this.error = error;
	}

	public Error getError() {
		return error;
	}

}
