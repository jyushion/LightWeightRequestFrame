package org.fans.frame.api.packet.demo;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;

import org.fans.frame.api.executor.JsonSerializer;
import org.fans.frame.api.executor.ParamsBuilder;
import org.fans.frame.api.packet.ApiRequest;

/**
 * json传递方式的示例
 * 
 * @author ludaiqian@126.com
 * 
 */
public class JsonParamsBuilder extends ParamsBuilder {

	private HashMap<String, String> header;

	public JsonParamsBuilder(ApiRequest request) {
		super(request);
		header = new HashMap<String, String>();
		header.put("Content-Type", "text/xml;charset=UTF-8");
	}

	/**
	 * 由于getPostBody返回内容不为空，因此此方法返回内容将被覆盖
	 */
	@Override
	public HashMap<String, String> getPostParams() {
		return null;
	}

	@Override
	public HashMap<String, String> getHeaders() {
		return header;
	}

	@Override
	public String toHttpGetUrl() {
		throw new UnsupportedOperationException();
	}

	@Override
	public byte[] getPostBody() {
		try {
			return JsonSerializer.DEFAULT.serialize(getRequest()).getBytes("UTF-8");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
			throw new RuntimeException(e);
		}
	}
}
