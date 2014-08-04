package org.fans.frame.api.executor;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

public final class PacketFieldExcluder {
	public static final PacketFieldExcluder DEFAULT = new PacketFieldExcluder();

	private int modifiers = Modifier.TRANSIENT | Modifier.STATIC;
	private boolean serializeInnerClasses = false;

	public boolean excludeField(Field field) {
		if ((modifiers & field.getModifiers()) != 0) {
			return true;
		}

		if (field.isSynthetic()) {
			return true;
		}

		if (!serializeInnerClasses && isInnerClass(field.getType())) {
			return true;
		}

		if (isAnonymousOrLocal(field.getType())) {
			return true;
		}
		if (!isPrimitive(field.getType()) && !isCharacterType(field.getType())) {
			return true;
		}
		return false;
	}

	private boolean isCharacterType(Class<?> type) {
		return CharSequence.class.isAssignableFrom(type);
	}

	public boolean isPrimitive(Class<?> type) {
		if (Boolean.class.isAssignableFrom(type) || boolean.class.isAssignableFrom(type))
			return true;
		else if (Byte.class.isAssignableFrom(type) || byte.class.isAssignableFrom(type))
			return true;
		else if (Character.class.isAssignableFrom(type) || char.class.isAssignableFrom(type))
			return true;
		else if (Double.class.isAssignableFrom(type) || double.class.isAssignableFrom(type))
			return true;
		else if (Float.class.isAssignableFrom(type) || float.class.isAssignableFrom(type))
			return true;
		else if (Integer.class.isAssignableFrom(type) || int.class.isAssignableFrom(type))
			return true;
		else if (Long.class.isAssignableFrom(type) || long.class.isAssignableFrom(type))
			return true;
		else if (Short.class.isAssignableFrom(type) || short.class.isAssignableFrom(type))
			return true;
		return false;
	}

	/**
	 * 是否为枚举类、匿名类或者是native 的类
	 * 
	 * @param clazz
	 * @return
	 */
	private boolean isAnonymousOrLocal(Class<?> clazz) {
		return !Enum.class.isAssignableFrom(clazz) && (clazz.isAnonymousClass() || clazz.isLocalClass());
	}

	/**
	 * 是内部类
	 * 
	 * @param clazz
	 * @return
	 */
	private boolean isInnerClass(Class<?> clazz) {
		return clazz.isMemberClass() && !isStatic(clazz);
	}

	/**
	 * 是静态的类
	 * 
	 * @param clazz
	 * @return
	 */
	private boolean isStatic(Class<?> clazz) {
		return (clazz.getModifiers() & Modifier.STATIC) != 0;
	}

	//
	// public Object convert(Type rawType, Object value) {
	// if (value == null)
	// return null;
	// if (!(value instanceof CharSequence))
	// return value;
	// String valueStr = value.toString();
	// if ("".equals(valueStr))
	// return null;
	// @SuppressWarnings("rawtypes")
	// Class type = TypeToken.get(rawType).getRawType();
	// if (CharSequence.class.isAssignableFrom(type))
	// return valueStr;
	// if (Boolean.class.isAssignableFrom(type) ||
	// boolean.class.isAssignableFrom(type))
	// return Boolean.valueOf(valueStr);
	// else if (Byte.class.isAssignableFrom(type) ||
	// byte.class.isAssignableFrom(type))
	// return Byte.valueOf(valueStr);
	// else if (Character.class.isAssignableFrom(type) ||
	// char.class.isAssignableFrom(type))
	// return valueStr.charAt(0);
	// else if (Double.class.isAssignableFrom(type) ||
	// double.class.isAssignableFrom(type))
	// return Double.valueOf(valueStr);
	// else if (Float.class.isAssignableFrom(type) ||
	// float.class.isAssignableFrom(type))
	// return Float.valueOf(valueStr);
	// else if (Integer.class.isAssignableFrom(type) ||
	// int.class.isAssignableFrom(type))
	// return Integer.valueOf(valueStr);
	// else if (Long.class.isAssignableFrom(type) ||
	// long.class.isAssignableFrom(type))
	// return Long.valueOf(valueStr);
	// else if (Short.class.isAssignableFrom(type) ||
	// short.class.isAssignableFrom(type))
	// return Short.valueOf(valueStr);
	// return null;
	// }
	public Class<?> processPrimitiveType(Class<?> type) {
		if (boolean.class.isAssignableFrom(type))
			return Boolean.class;
		else if (byte.class.isAssignableFrom(type))
			return Byte.class;
		else if (char.class.isAssignableFrom(type))
			return Character.class;
		else if (double.class.isAssignableFrom(type))
			return Double.class;
		else if (float.class.isAssignableFrom(type))
			return Float.class;
		else if (int.class.isAssignableFrom(type))
			return Integer.class;
		else if (long.class.isAssignableFrom(type))
			return Long.class;
		else if (short.class.isAssignableFrom(type))
			return Short.class;
		return null;
	}

}