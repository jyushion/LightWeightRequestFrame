package org.fans.frame.api.packet.demo.request;

import org.fans.frame.api.packet.ApiRequest;

public class BaseApiRequest implements ApiRequest {

	private String method;

	public void setMethod(String method) {
		this.method = method;
	}

	@Override
	public String getMethod() {
		return method;
	}

}
