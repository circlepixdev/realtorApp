package com.circlepix.android.data;

import android.util.JsonWriter;

import java.io.ByteArrayOutputStream;
import java.io.OutputStreamWriter;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class PresentationWriter {

	public static String toJson(Presentation b) {

		ByteArrayOutputStream baos = null;
		JsonWriter writer = null;

		try {
			baos = new ByteArrayOutputStream();
			writer = new JsonWriter(new OutputStreamWriter(baos, "UTF-8"));

			writer.beginObject();

			Field[] fields = Presentation.class.getDeclaredFields();
			for (Field field : fields) {
				Object fieldValue = runGetter(field, b);
				writer.name(field.getName()).value(
						(fieldValue == null) ? null : fieldValue.toString());
			}

			writer.endObject();
			writer.flush();
			String json = baos.toString();
			return json;
		} catch (Exception e) {
			return "error";
		} finally {
			try {
				baos.close();
			} catch (Exception e) {
			}
			try {
				writer.close();
			} catch (Exception e) {
			}
		}

	}

	public static Object runGetter(Field field, Presentation b) {
		// MZ: Find the correct method
		for (Method method : Presentation.class.getMethods()) {
			if (((method.getName().startsWith("get")) && (method.getName().length() == (field.getName().length() + 3)))
					|| ((method.getName().startsWith("is")) && (method.getName().length() == (field.getName().length() + 2)))) {
				if (method.getName().toLowerCase().endsWith(field.getName().toLowerCase())) {
					try {
						return method.invoke(b);
					} catch (IllegalAccessException e) {
						// Logger.fatal("Could not determine method: " +
						// method.getName());
					} catch (InvocationTargetException e) {
						// Logger.fatal("Could not determine method: " +
						// method.getName());
					}
				}
			}
		}

		return null;
	}
}
