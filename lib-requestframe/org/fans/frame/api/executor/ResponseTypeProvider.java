package org.fans.frame.api.executor;

import java.lang.reflect.Type;
import java.util.HashMap;

/**
 * 配置方法对应的响应类型
 * 
 * @author Ludaiqian
 * @since 1.0
 */
public class ResponseTypeProvider {

	private HashMap<String, Type> responseMap = new HashMap<String, Type>();
	private Type defaultType;
	private static ResponseTypeProvider instance;

	public Type getApiResponseType(String methodName) {
		Type responseType = responseMap.get(methodName);
		return responseType != null ? responseType : defaultType;
	}

	public void registerResponseType(String methodName, Type responseType) {
		responseMap.put(methodName, responseType);
	}

	public void setDefaultType(Type defaultType) {
		this.defaultType = defaultType;
	}

	public static synchronized ResponseTypeProvider getInstance() {
		if (instance == null) {
			instance = new ResponseTypeProvider();
		}
		return instance;
	}

	public static synchronized void setInstance(ResponseTypeProvider instance) {
		ResponseTypeProvider.instance = instance;
	}

}
