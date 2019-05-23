package com.enhinck.bean;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.sql.ResultSet;

public class BeanUtil {
	public static Object copyLeft2Right(Object obj1, Class<?> classType2) {
		Object obj2 = null;
		try {
			obj2 = classType2.newInstance();
		} catch (InstantiationException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		}
		copyLeft2Right(obj1, obj2);
		return obj2;
	}

	public static void copyLeft2Right(Object obj1, Object obj2) {
		Class<?> classType1 = obj1.getClass();
		Class<?> classType2 = obj2.getClass();
		Field[] fields = classType1.getDeclaredFields();
		for (int i = 0; i < fields.length; i++) {
			String fieldName = fields[i].getName();
			Class<?> filedType = fields[i].getType();
			String methodGetName = "get" + getMethodName(fieldName);
			String methodSetName = "set" + getMethodName(fieldName);
			try {
				// Obj1读取
				Method method1 = classType1.getMethod(methodGetName);
				Object obj1Value = method1.invoke(obj1);
				// Obj2写入
				Method method2 = classType2.getMethod(methodSetName, filedType);
				method2.invoke(obj2, obj1Value);
			} catch (Exception e) {

			}
		}
	}

	public static Object[] getInsertSqlObjs(Object temple) {
		Class<?> classType = temple.getClass();
		Field[] fields = classType.getDeclaredFields();
		Object[] objects = new Object[fields.length];
		for (int i = 0; i < fields.length; i++) {
			String fieldName = fields[i].getName();
			String methodGetName = "get" + getMethodName(fieldName);
			try {
				Method method = classType.getMethod(methodGetName);
				Object value = method.invoke(temple);
				objects[i] = value;
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		return objects;
	}
	
	public static Object[] getInsertSqlObjs(Object temple,boolean getSupper) {
		Class<?> classType = temple.getClass();
		Field[] fields = classType.getDeclaredFields();
		Object[] objects = new Object[fields.length];
		for (int i = 0; i < fields.length; i++) {
			String fieldName = fields[i].getName();
			String methodGetName = "get" + getMethodName(fieldName);
			try {
				Method method = classType.getMethod(methodGetName);
				Object value = method.invoke(temple);
				objects[i] = value;
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		if(getSupper){
			Class<?> supperclassType = classType.getSuperclass();
			Field[] fields2 = supperclassType.getDeclaredFields();
			Object[] objects2 = new Object[fields.length+fields2.length];
			for (int i = 0; i < fields.length; i++) {
				objects2[i] = objects[i];
			}
			for (int i = 0; i < fields2.length; i++) {
				String fieldName = fields2[i].getName();
				String methodGetName = "get" + getMethodName(fieldName);
				try {
					Method method = supperclassType.getMethod(methodGetName);
					Object value = method.invoke(temple);
					objects2[fields.length+i] = value;
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			return objects2;
		}
		
		
		

		return objects;
	}

	public static String getInsertSql(Class<?> classType) {
		Field[] fields = classType.getDeclaredFields();
		String tableName = classType.getSimpleName().toLowerCase();
		final StringBuilder bsql = new StringBuilder();
		bsql.append("insert into " + tableName + " ( ");
		for (int i = 0; i < fields.length; i++) {
			String fieldName = fields[i].getName();
			bsql.append(fieldName);
			if (i != fields.length - 1)
				bsql.append(" , ");
		}
		bsql.append(" )  VALUES (");
		for (int i = 0; i < fields.length; i++) {
			bsql.append(" ?");
			if (i != fields.length - 1)
				bsql.append(" ,");
		}
		bsql.append(")");
		return bsql.toString();
	}
	
	public static String getInsertSql(Class<?> classType,boolean getSupper) {
		Field[] fields = classType.getDeclaredFields();
		Field[] fields2 = new Field[0];
		if(getSupper){
			 fields2 = classType.getSuperclass().getDeclaredFields();
		}
	
		String tableName = classType.getSimpleName().toLowerCase();
		final StringBuilder bsql = new StringBuilder();
		bsql.append("insert into " + tableName + " (");
		for (int i = 0; i < fields.length; i++) {
			String fieldName = fields[i].getName();
			bsql.append(" ");
			bsql.append(fieldName);
			bsql.append(" ,");
		}
	
		for (int i = 0; i < fields2.length; i++) {
			String fieldName = fields2[i].getName();
			bsql.append(" ");
			bsql.append(fieldName);
			bsql.append(" ,");
		}
		
		bsql.deleteCharAt(bsql.length()-1);
		
		bsql.append(" )  VALUES (");
		for (int i = 0; i < fields.length; i++) {
			bsql.append(" ");
			bsql.append("?");
			bsql.append(" ,");
		}
		for (int i = 0; i < fields2.length; i++) {
			bsql.append(" ");
			bsql.append("?");
			bsql.append(" ,");
		}
		bsql.deleteCharAt(bsql.length()-1);
		bsql.append(")");
		return bsql.toString();
	}
	

	public static String getSelectSql(Object obj, String where) {
		Class<?> classType = obj.getClass();
		Field[] fields = classType.getDeclaredFields();
		String tableName = classType.getSimpleName().toLowerCase();
		final StringBuilder bsql = new StringBuilder();
		bsql.append("select ");
		for (int i = 0; i < fields.length; i++) {
			String fieldName = fields[i].getName();
			bsql.append(fieldName);
			if (i != fields.length - 1)
				bsql.append(" , ");
		}
		bsql.append(" from " + tableName);
		bsql.append(" ").append(where);
		return bsql.toString();
	}

	public static <T> T getDbData(Class<T> classType, ResultSet rs) {
		T object = null;
		try {
			object = classType.newInstance();
			Field[] fields = classType.getDeclaredFields();
			for (int i = 0; i < fields.length; i++) {
				String fieldName = fields[i].getName();
				Class<?> filedType = fields[i].getType();
				String methodSetName = "set" + getMethodName(fieldName);
				Method method = classType.getMethod(methodSetName, filedType);
				if (rs.getObject(fieldName) != null)
					method.invoke(object, rs.getObject(fieldName));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		return object;
	}

	public static String getMethodName(String fildeName) {
		byte[] items = fildeName.getBytes();
		items[0] = (byte) ((char) items[0] - 'a' + 'A');
		return new String(items);
	}

	

	public static Object getFieldValue(Object obj, String fieldName) {
		Class<?> classType = obj.getClass();
		try {

			String methodSetName = "get" + getMethodName(fieldName);
			Method method = classType.getMethod(methodSetName);
			return method.invoke(obj);
		} catch (Exception e) {
			e.printStackTrace();
		}

		return null;
	}

}
