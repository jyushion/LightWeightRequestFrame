package org.fans.frame.api.executor;

import java.lang.reflect.Type;

import org.fans.frame.api.packet.ApiPacket;
import org.fans.frame.api.packet.ApiResponse;

/**
 * 序列化转换器
 * 
 * @author Ludaiqian
 * 
 */
public interface Serializer {
	
	/**
	 * 序列化
	 * 
	 * @param apiPacket
	 * @return
	 */
	public String serialize(ApiPacket apiPacket);

	/**
	 * 反序列化
	 * 
	 * @param json
	 * @param type
	 * @return
	 */
	public ApiResponse<?> deserialize(String json, Type type);

}