package com.thirdpay.statistics;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.thirdpay.utils.LogUtils;

public class JSON {

	
	
	
	public static String toMapJson(Map<String,String> map){
		JSONObject jsonObject = new JSONObject();
		Set<Entry<String, String>> set=map.entrySet();
		Iterator<Entry<String, String>>  iterator =  set.iterator();
		while(iterator.hasNext()){
			Entry<String, String> entry = iterator.next();
			String key = entry.getKey();
			String value = entry.getValue();
			
			try {
				jsonObject.put(key, value);
			} catch (JSONException e) {
				e.printStackTrace();
				return null;
			}
		}
		
		return jsonObject.toString();
		
	}
	
	public static Map<String,String> toJsonMap(String json){
		Map<String,String> map = new HashMap<String, String>();
		try {
			JSONObject jsonObj = new JSONObject(json);
			Iterator<String> iterator = jsonObj.keys();
			while (iterator.hasNext()) {
				String key =  iterator.next();
				map.put(key, jsonObj.getString(key));
			}
			
		} catch (JSONException e) {
			e.printStackTrace();
			return null;
		}
		
		
		
		return map;
		
	}
	
	
	
	/**
	 * 把一个Bean对象转变成json
	 * @param obj
	 * @return
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public static String toJsonString(Object obj) {
		Class clazz = obj.getClass();
		JSONObject jsonObject = new JSONObject();
		Field[] fields = clazz.getDeclaredFields();
		//LogUtils.e("JSON---toJsonString--fieldsLength():"+fields.length);
		for (Field field : fields) {
			String fieldName = field.getName();
			//LogUtils.e("JSON---toJsonString--fieldName():"+fieldName);
			Class type = field.getType();
			String newfieldName = "get"
					+ fieldName.substring(0, 1).toUpperCase()
					+ fieldName.substring(1);
			//LogUtils.e("JSON---toJsonString--newfieldName():"+newfieldName);
			try {
				Method m = clazz.getMethod(newfieldName, null);
				Object value = m.invoke(obj, null);
				//LogUtils.e("JSON---toJsonString--value:"+value);
				//System.out.println(value);
				if(value!=null){
					jsonObject.put(field.getName(), value);
					//LogUtils.e("JSON---toJsonString--field.getName():"+field.getName()+"--value:"+value);
				}
				
			} catch (NoSuchMethodException e) {
				LogUtils.e("JSON---toJsonString--NoSuchMethodException-e.getMessage():"+e.getMessage());
				e.printStackTrace();
				return null;
			} catch (IllegalAccessException e) {
				LogUtils.e("JSON---toJsonString--IllegalAccessException");
				e.printStackTrace();
				return null;
			} catch (IllegalArgumentException e) {
				LogUtils.e("JSON---toJsonString--IllegalArgumentException");
				e.printStackTrace();
				return null;
			} catch (InvocationTargetException e) {
				LogUtils.e("JSON---toJsonString--InvocationTargetException");
				e.printStackTrace();
				return null;
			} catch (JSONException e) {
				LogUtils.e("JSON---toJsonString--JSONException");
				e.printStackTrace();
				return null;
			}
		}

		return jsonObject.toString();
	}

	/**
	 * 解析json转变成一个对象
	 * @param json
	 * @param entityclass
	 * @return
	 */
	public static <T> T parseObject(String json, Class<T> entityclass) {
		T obj = null;
		try {
			obj = entityclass.newInstance();
			JSONObject root = new JSONObject(json);

			Class superClass = entityclass.getSuperclass();
			String superClassName = superClass.getName();

			Field[] fields = obj.getClass().getDeclaredFields();
			for (Field field : fields) {
				String fieldName = field.getName();
				try {
					Class type = field.getType();
					if ("serialVersionUID".equals(fieldName)) {
						continue;
					} else if ("java.util.List".equals(type.getName())) {

						String jsonStr = root.getString(fieldName);
						if (jsonStr == null || "".equals(jsonStr)) {
							continue;
						}

						JSONArray jsonArray = root.getJSONArray(fieldName);
						String newfieldName = "set"
								+ fieldName.substring(0, 1).toUpperCase()
								+ fieldName.substring(1);
						Method m = entityclass
								.getMethod(newfieldName, new Class[] { Class
										.forName("java.util.List") });
						ParameterizedType pt = (ParameterizedType) field
								.getGenericType();
						Class clz = (Class) pt.getActualTypeArguments()[0];
						List list_t3 = new ArrayList();

						for (int i = 0; i < jsonArray.length(); i++) {
							JSONObject josnObj = jsonArray.getJSONObject(i);
							Object t2 = parseObject(josnObj.toString(), clz);
							list_t3.add(t2);
						}

						m.invoke(obj, new Object[] { list_t3 });
					} else if ("java.lang.String".equals(type.getName())) {
						Method m = getMethod(entityclass, fieldName, type);
						String value = root.getString(fieldName);
						m.invoke(obj, new Object[] { value });
					} else if ("java.lang.Long".equals(type.getName())) {

						Method m = getMethod(entityclass, fieldName, type);
						Long value = root.getLong(fieldName);
						m.invoke(obj, new Object[] { value });

					} else if ("java.lang.Integer".equals(type.getName())) {

						Method m = getMethod(entityclass, fieldName, type);
						Integer value = root.getInt(fieldName);
						m.invoke(obj, new Object[] { value });

					} else if ("java.lang.Boolean".equals(type.getName())) {

						Method m = getMethod(entityclass, fieldName, type);
						Boolean value = root.getBoolean(fieldName);
						m.invoke(obj, new Object[] { value });

					} else if ("java.lang.Double".equals(type.getName())) {

						Method m = getMethod(entityclass, fieldName, type);
						Double value = root.getDouble(fieldName);
						m.invoke(obj, new Object[] { value });

					} else if ("long".equals(type.getName())) {

						Method m = getMethod(entityclass, fieldName, type);
						long value = root.getLong(fieldName);
						m.invoke(obj, new Object[] { value });

					} else if ("int".equals(type.getName())) {

						Method m = getMethod(entityclass, fieldName, type);
						int value = root.getInt(fieldName);
						m.invoke(obj, new Object[] { value });

					} else if ("boolean".equals(type.getName())) {

						Method m = getMethod(entityclass, fieldName, type);
						boolean value = root.getBoolean(fieldName);
						m.invoke(obj, new Object[] { value });

					} else {
						String newfieldName = "set"
								+ fieldName.substring(0, 1).toUpperCase()
								+ fieldName.substring(1);
						String fieldNameJson = root.getString(fieldName);
						Object t2 = parseObject(fieldNameJson, type);
						Method m = entityclass.getMethod(newfieldName,
								new Class[] { Class.forName(type.getName()) });
						m.invoke(obj, new Object[] { t2 });
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		} catch (InstantiationException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (JSONException e1) {
			e1.printStackTrace();
		}
		return obj;

	}

	/**
	 * 获取类中的方法
	 * @param entityclass
	 * @param fieldName
	 * @param type
	 * @return
	 * @throws NoSuchMethodException
	 * @throws ClassNotFoundException
	 */
	private static <T> Method getMethod(Class<T> entityclass, String fieldName,
			Class type) throws NoSuchMethodException, ClassNotFoundException {
		String newfieldName = "set" + fieldName.substring(0, 1).toUpperCase()
				+ fieldName.substring(1);

		Method m = entityclass.getMethod(newfieldName, new Class[] { type });
		return m;
	}

}
