package org.fans.frame.api.executor;

import com.android.volley.VolleyError;

import org.fans.frame.api.packet.Error;

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
