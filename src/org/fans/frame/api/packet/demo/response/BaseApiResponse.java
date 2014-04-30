package org.fans.frame.api.packet.demo.response;

import org.fans.frame.api.packet.ApiResponse;
import org.fans.frame.api.packet.Error;


public class BaseApiResponse<T> implements ApiResponse<T> {
	private String message;

	private T data;

	private int status;

	public boolean isSuccess() {
		return status == 1;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public T getData() {
		return data;
	}

	public void setData(T data) {
		this.data = data;
	}

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	@Override
	public Error getError() {
		return null;
	}

	@Override
	public int getResultCode() {
		return status;
	}
}
