package org.fans.frame.api.packet.demo;

import java.lang.reflect.Type;

import org.apache.http.client.HttpClient;
import org.fans.frame.api.executor.DialogTask;
import org.fans.frame.api.executor.JsonSerializer;
import org.fans.frame.api.executor.RequestBuilder;
import org.fans.frame.api.executor.ResponseTypeProvider;
import org.fans.frame.api.executor.StringRequest;
import org.fans.frame.api.packet.ApiRequest;
import org.fans.frame.api.packet.ApiResponse;
import org.fans.frame.api.toolbox.DefaultParamsBuilder;
import org.fans.frame.utils.Constants;

import android.net.http.AndroidHttpClient;

import com.android.volley.NetworkResponse;
import com.android.volley.VolleyError;
import com.android.volley.Request.Method;
import com.android.volley.toolbox.BasicNetwork;
import com.android.volley.toolbox.HttpClientStack;
import com.android.volley.toolbox.HttpStack;

/**
 * <pre>
 * Api地址  ?
 * 此类提供API方法名列表及API请求示例，
 * 注意这里作为示例的请求方法并不会用到实际的请求工作中,
 * 具体执行请求的工作放在{@link DialogTask}里统一处理
 * </pre>
 * 
 * @author ludq@hyxt.com
 * 
 */
public class ServiceApi {
	// 注册
	public static final String REGISTER = "Register";
	// 登陆
	public static final String LOGIN = "Login";

	public static void init() {
		// ResponseTypeProvider.getInstance().setDefaultType(defaultType);
		// ResponseTypeProvider.getInstance().registerResponseType(methodName,
		// responseType);
	}

	/**
	 * 注册示例
	 * 
	 * @param register
	 * @return
	 * @throws VolleyError
	 */
	// public static ApiResponse<String> register(Register register) throws
	// VolleyError {
	// register.setMethod(REGISTER);
	// return performRequest(register);
	// }

	/**
	 * 登陆示例
	 * 
	 * @throws VolleyError
	 * 
	 * 
	 */
	// public static ApiResponse<String> login(Login login) throws VolleyError{
	// login.setMethod(LOGIN);
	// return performRequest(l);
	// }

	// -------------------------------------------------------------------------
	/**
	 * 执行请求并获取响应
	 * 
	 * @param apiRequest
	 * @return
	 * @throws VolleyError
	 */
	@SuppressWarnings("unchecked")
	public static <T> ApiResponse<T> performRequest(ApiRequest apiRequest) throws VolleyError {

		String userAgent = "volley/0";
		HttpClient client = AndroidHttpClient.newInstance(userAgent);
		HttpStack httpStack = new HttpClientStack(client);
		BasicNetwork network = new BasicNetwork(httpStack);
		StringRequest request = buildRequest(apiRequest);
		NetworkResponse response = network.performRequest(request);
		String data = new String(response.data);
		// 通过方法名获取响应类型
		Type clazz = ResponseTypeProvider.getInstance().getApiResponseType(apiRequest.getMethod());
		return (ApiResponse<T>) JsonSerializer.DEFAULT.deserialize(data, clazz);
	}

	private static StringRequest buildRequest(ApiRequest apiRequest) {
		StringRequest request = new RequestBuilder()//
				.setUrl(Constants.URL)//
				.setMethod(Method.GET)//
				// .setListner(listener)//
				// .setErrorListener(errorListener)//
				.setParamsBuilder(new DefaultParamsBuilder(apiRequest))
				// .setApiRequest(apiRequest)//
				.build();
		return request;
	}

	// public static ParamsBuilder newParamsBuilder(ApiRequest apiRequest){
	//
	// }
}
