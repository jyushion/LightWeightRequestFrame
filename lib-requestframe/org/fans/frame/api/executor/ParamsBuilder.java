package org.fans.frame.api.executor;

import java.lang.reflect.Field;
import java.util.HashMap;

import org.fans.frame.api.packet.ApiRequest;
import org.fans.frame.api.packet.Name;

import com.android.volley.Request.FormFile;
import com.android.volley.Request.FormHeader;
import com.google.gson.FieldNamingStrategy;

/**
 * 根据ApiPacket生成GET方式的url或者提取Http协议请求头或参数，<br>
 * 生成或提取方式可以由使用者根据接口实际情况来决定 ,<br>
 * 绝大多数情况下，可以由ParamsBuilder继承实例类统一处理，当然，<br>
 * 也可以由每一个继承ApiRequestd的请求分开处理再提供ParamsBuilder继承实例调用
 * 
 * 
 * @author ludq@hyxt.com
 * 
 */
public abstract class ParamsBuilder {
	protected String url;
	/**
	 * <pre>
	 *  默认的属性名->接口参数名转换策略
	 * 当有@{@link Name} 注解时，返回Name注解的值
	 * 否则 直接返回属性名称
	 * </pre>
	 */
	public static final FieldNamingStrategy DEFAULT_STRATEGY = new FieldNamingStrategy() {

		@Override
		public String translateName(Field f) {
			Name key = f.getAnnotation(Name.class);
			return key != null ? key.value() : f.getName();
			// .substring(0,1). toUpperCase() + f.getName ().substring(1)
		}
	};
	private ApiRequest request;
	protected FieldNamingStrategy strategy;

	public void setUrl(String url) {
		this.url = url;
	}

	protected String getUrl() {
		return url;
	}

	public String getRequestUrl() {
		return url;
	}

	public ParamsBuilder(ApiRequest request) {
		// url = Constants.URL;
		this.request = request;
	}

	public ApiRequest getRequest() {
		return request;
	}

	public void setStrategy(FieldNamingStrategy strategy) {
		this.strategy = strategy;
	}

	/**
	 * 从ApiRquest中提取参数
	 * 
	 * @return
	 */
	public abstract HashMap<String, String> getPostParams();

	/**
	 * 从ApiRquest中提取Header
	 * 
	 * @return
	 */
	public abstract HashMap<String, String> getHeaders();

	/**
	 * 生成get提交的url
	 * 
	 * @return
	 */
	public abstract String toHttpGetUrl();

	/**
	 * 不通过key-value方式， 需要提交二进制到服务器的情况下下，可以重写此方法,<br>
	 * 返回非<code>null</code>的情况下，设置任何Post参数都将会无效,即{@link #getPostParams()}是无意义的
	 * 
	 * @see RequestBuilder#build()
	 * @return
	 */
	public byte[] getPostBody() {
		return null;
	}

	public FormHeader[] getFormHeaders(){
		return null;
		
	}
	
	public FormFile[] getFormFiles(){
		return null;
		
	}
	
	public int getType() {
		return 0;
	}
	
	public String getContentType(){
		return null;
	}

}
