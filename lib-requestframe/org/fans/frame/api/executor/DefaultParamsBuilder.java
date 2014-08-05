package org.fans.frame.api.executor;

import java.lang.reflect.Field;
import java.util.HashMap;

import org.fans.frame.api.packet.ApiRequest;
import org.fans.frame.api.packet.Header;
/**
 * 默认的ParamsBuilder
 * @author Ludaiqian
 * @since 1.0
 *
 */
public class DefaultParamsBuilder extends ParamsBuilder {
	private PacketFieldExcluder excluder = PacketFieldExcluder.DEFAULT;

	private String paramsString;
	private HashMap<String, String> params;
	private HashMap<String, String> headers;

	public DefaultParamsBuilder(ApiRequest request) {
		super(request);

	}

	@Override
	public HashMap<String, String> getPostParams() {
		if (params == null) {
			initParams();
		}

		return params;
	}

	public String genarateParamsString() {
		if (paramsString == null)
			initParams();
		return paramsString;
	}

	public void initParams() {
		if (strategy == null)
			strategy = DEFAULT_STRATEGY;
		StringBuilder buffer = new StringBuilder();
		ApiRequest request = getRequest();
		params = new HashMap<String, String>();
		headers = new HashMap<String, String>();
		Class<?> clazz = request.getClass();
		while (clazz != Object.class) {
			Field[] fields = clazz.getDeclaredFields();
			for (Field field : fields) {
				// field.geta
				field.setAccessible(true);
				if (checkFieldSerialialbe(field)) {
					// properties.add(field);
					try {
						Object value = field.get(request);
						String key = strategy.translateName(field);
						if (value != null) {
							Header header = field.getAnnotation(Header.class);
							if (header != null) {
								headers.put(key, value.toString());
							} else {
								buffer.append(key).append("=").append("value").append("&");
								params.put(key, value.toString());
							}

						}
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}
			clazz = clazz.getSuperclass();
		}
		// 删除最后一个&
		if (buffer.length() > 0)
			buffer.deleteCharAt(buffer.length() - 1);
		paramsString = buffer.toString();
	}

	public boolean checkFieldSerialialbe(Field f) {
		return !excluder.excludeField(f);
	}

	@Override
	public HashMap<String, String> getHeaders() {
		if (headers == null) {
			initParams();
		}
		return headers;
	}

	@Override
	public String toHttpGetUrl() {
		String requestUrl = getRequestUrl();
		return requestUrl + (requestUrl.contains("?") ? "&" : "?") + genarateParamsString();
	}

}
