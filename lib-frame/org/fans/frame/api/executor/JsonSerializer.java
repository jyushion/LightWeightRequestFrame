package org.fans.frame.api.executor;

import java.lang.reflect.Type;

import org.fans.frame.api.packet.ApiPacket;
import org.fans.frame.api.packet.ApiResponse;

import com.google.gson.FieldNamingStrategy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class JsonSerializer implements Serializer {
	public Gson gson;

	public static final JsonSerializer DEFAULT = new JsonSerializer(ParamsBuilder.DEFAULT_STRATEGY);

	public JsonSerializer(FieldNamingStrategy strategy) {
		super();
		gson = new GsonBuilder().setFieldNamingStrategy(strategy).create();
	}

	@Override
	public String serialize(ApiPacket apiPacket) {
		return gson.toJson(apiPacket);
	}

	@Override
	public ApiResponse<?> deserialize(String json, Type type) {
		return gson.fromJson(json, type);

	}


}
