package org.fans.frame.api.toolbox;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;

import org.fans.frame.api.executor.JsonSerializer;
import org.fans.frame.api.executor.ParamsBuilder;
import org.fans.frame.api.packet.ApiRequest;

public class JsonParamsBuilder extends ParamsBuilder {

	public JsonParamsBuilder(ApiRequest request) {
		super(request);
	}

	@Override
	public HashMap<String, String> getPostParams() {
		return null;
	}

	@Override
	public HashMap<String, String> getHeaders() {
		return null;
	}

	@Override
	public String toHttpGetUrl() {
		return null;
	}

	@Override
	public byte[] getPostBody() {
		try {
			byte[] result = JsonSerializer.DEFAULT.serialize(getRequest()).getBytes("UTF-8");
			return result;
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		return null;
	}

	@Override
	public String getContentType() {
		// return super.getContentType();
		return "text/xml;charset=UTF-8";
	}
}
