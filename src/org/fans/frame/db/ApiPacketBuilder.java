package org.fans.frame.db;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import org.fans.frame.api.executor.PacketFieldExcluder;
import org.fans.frame.api.packet.ApiPacket;

import android.content.ContentValues;
import android.database.Cursor;

import com.google.gson.reflect.TypeToken;

public class ApiPacketBuilder<T> extends DatabaseBuilder<T> {

	private Class<T> klass;
	private ArrayList<Field> fields;
	private String[] fieldNames;
	private PacketFieldExcluder excluder;

	ApiPacketBuilder(Class<T> klass) {
		super();
		this.klass = klass;
		getFields(klass);
		excluder = PacketFieldExcluder.DEFAULT;

	}

	private void getFields(Class<T> klass) {
		fields = new ArrayList<Field>();
		Class<?> clazz = klass;
		while (clazz != Object.class) {
			Field[] fields = clazz.getDeclaredFields();
			for (Field field : fields) {
				// 该字段是否被忽略
				boolean ignore = excluder.excludeField(field);
				if (ignore) {
					continue;
				}
				this.fields.add(field);
			}
			if (clazz == ApiPacket.class)
				break;
			clazz = clazz.getSuperclass();
		}
	}

	@Override
	public T build(Cursor c) {
		try {
			T packet = klass.newInstance();
			for (Field f : fields) {
				f.setAccessible(true);
				Class<?> rawType = TypeToken.get(f.getType()).getRawType();
				if (Short.class.isAssignableFrom(rawType) || short.class.isAssignableFrom(rawType))
					f.set(packet, (short) c.getInt(c.getColumnIndex(f.getName())));
				else if (Integer.class.isAssignableFrom(rawType) || int.class.isAssignableFrom(rawType))
					f.set(packet, c.getInt(c.getColumnIndex(f.getName())));
				else if (Long.class.isAssignableFrom(rawType) || long.class.isAssignableFrom(rawType)) {
					f.set(packet, c.getLong(c.getColumnIndex(f.getName())));
				} else if (Float.class.isAssignableFrom(rawType) || float.class.isAssignableFrom(rawType)) {
					f.set(packet, c.getFloat(c.getColumnIndex(f.getName())));
				} else if (Double.class.isAssignableFrom(rawType) || double.class.isAssignableFrom(rawType)) {
					f.set(packet, c.getDouble(c.getColumnIndex(f.getName())));
				} else if (Boolean.class.isAssignableFrom(rawType) || Boolean.class.isAssignableFrom(rawType)) {
					// 处理boolean类型
					f.set(packet, c.getInt(c.getColumnIndex(f.getName())) == 1 ? true : false);
				} else if (Character.class.isAssignableFrom(rawType) || char.class.isAssignableFrom(rawType)) {
					// 处理char类型
					String value = c.getString(c.getColumnIndex(f.getName()));
					f.set(packet, (value != null && value.length() > 0) ? value.charAt(0) : null);
				} else if (CharSequence.class.isAssignableFrom(rawType)) {
					f.set(packet, c.getString(c.getColumnIndex(f.getName())));
				}

			}
			return (T) packet;
		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException(e);
		}
	}

	@Override
	public ContentValues deconstruct(T t) {
		ContentValues values = new ContentValues();
		for (Field f : fields) {
			f.setAccessible(true);
			try {

				Method m = values.getClass().getMethod("put", String.class, getRawType(f));
				m.invoke(values, f.getName(), f.get(t));
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return values;
	}

	public Class<?> getRawType(Field f) {
		Class<?> type = excluder.processPrimitiveType(TypeToken.get(f.getType()).getRawType());
		// 转换char类型为String
		if (type == Character.class || type == null) {
			return String.class;
		}
		// 转换Boolean类型为int
		if (type == Boolean.class) {
			return Integer.class;
		}
		return type;
	}

	public String[] getColumnsNames() {
		if (fieldNames == null) {
			fieldNames = new String[fields.size()];
			for (int i = 0; i < fields.size(); i++) {
				fieldNames[i] = fields.get(i).getName();
			}
		}
		return fieldNames;
	}

	public List<String> getNumberTypeColumns() {
		ArrayList<String> primitiveTypes = new ArrayList<String>();
		for (int i = 0; i < fields.size(); i++) {
			Field f = fields.get(i);
			Class<?> rawType = TypeToken.get(f.getType()).getRawType();
			if (excluder.processPrimitiveType(rawType) != Character.class && excluder.isPrimitive(rawType)) {
				primitiveTypes.add(f.getName());
			}
		}
		return primitiveTypes;
	}

	public List<String> getCharSequenceTypeColumns() {
		ArrayList<String> charSequenceTypes = new ArrayList<String>();
		for (int i = 0; i < fields.size(); i++) {
			Field f = fields.get(i);
			Class<?> rawType = TypeToken.get(f.getType()).getRawType();
			if (CharSequence.class.isAssignableFrom(rawType) || excluder.processPrimitiveType(rawType) == Character.class) {
				charSequenceTypes.add(f.getName());
			}
		}
		return charSequenceTypes;
	}
}
