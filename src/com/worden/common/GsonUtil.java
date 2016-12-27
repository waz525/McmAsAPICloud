package com.worden.common;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;

/**
 * 利用Gson封装，解析和转换json
 * @author Worden
 *
 */
public class GsonUtil {

	/**
	 * 将json类转换成Map对象
	 * @param jsonStr
	 * @return
	 */
	public static Map<String, Object> JsontoMap(JsonObject json){
		Map<String, Object> map = new HashMap<String, Object>(); 
		
		Set<Entry<String, JsonElement>> entrySet = json.entrySet();
		for (Iterator<Entry<String, JsonElement>> iter = entrySet.iterator(); iter.hasNext(); ){
			Entry<String, JsonElement> entry = iter.next();
			String key = entry.getKey();
			Object value = entry.getValue();
			
			if(value instanceof JsonArray) {
				//map.put((String) key, JsontoList(((JsonArray) value).toString())); 
				map.put((String) key, JsontoList(((JsonArray) value).getAsJsonArray())); 
			} else if(value instanceof JsonObject) {
				map.put((String) key, JsontoMap(((JsonObject) value).getAsJsonObject()));
			} else {
				map.put((String) key, value);  
			}
		}
		return map;
	}

	/**
	 * 将json字符串转换成Map对象
	 * @param jsonStr
	 * @return
	 */
	public static Map<String, Object> JsontoMap(String jsonStr){
		Map<String, Object> map = new HashMap<String, Object>(); 
		
		JsonParser parser = new JsonParser();
		JsonObject json = parser.parse(jsonStr).getAsJsonObject();
		Set<Entry<String, JsonElement>> entrySet = json.entrySet();
		for (Iterator<Entry<String, JsonElement>> iter = entrySet.iterator(); iter.hasNext(); ){
			Entry<String, JsonElement> entry = iter.next();
			String key = entry.getKey();
			Object value = entry.getValue();
			
			if(value instanceof JsonArray) {
				map.put((String) key, JsontoList(((JsonArray) value).toString())); 
			} else if(value instanceof JsonObject) {
				map.put((String) key, JsontoMap(((JsonObject) value).toString()));
			} else {
				map.put((String) key, value);  
			}
		}
		return map;
	}

	/**
	 * 将json字符串转换成Map对象；key为大写
	 * @param jsonStr
	 * @return
	 */
	public static Map<String, Object> JsontoMapUpperKey(String jsonStr){
		Map<String, Object> map = new HashMap<String, Object>(); 
		
		JsonParser parser = new JsonParser();
		JsonObject json = parser.parse(jsonStr).getAsJsonObject();
		Set<Entry<String, JsonElement>> entrySet = json.entrySet();
		for (Iterator<Entry<String, JsonElement>> iter = entrySet.iterator(); iter.hasNext(); ){
			Entry<String, JsonElement> entry = iter.next();
			String key = entry.getKey().toUpperCase();
			Object value = entry.getValue();
			
			if(value instanceof JsonArray) {
				map.put((String) key, JsontoList(((JsonArray) value).toString())); 
			} else if(value instanceof JsonObject) {
				map.put((String) key, JsontoMapUpperKey(((JsonObject) value).toString()));
			} else {
				map.put((String) key, value);  
			}
		}
		return map;
	}

	/**
	 * 将json字符串转换成List对象
	 * @param jsonStr
	 * @return
	 */
	public static List<Object> JsontoList( String jsonStr){
		JsonParser parser = new JsonParser();
		JsonArray json = parser.parse(jsonStr).getAsJsonArray();
		List<Object> list = new ArrayList<Object>();
		for (int i=0; i<json.size(); i++){ 
			Object value = json.get(i); 
			if(value instanceof JsonArray){
				list.add(JsontoList(((JsonArray) value).toString()));
			} else if(value instanceof JsonObject){ 
				list.add(JsontoMap(((JsonObject) value).toString()));
			} else{
				list.add(value); 
			}
			
		}
		return list;
	}


	/**
	 * 将json数组类转换成List对象
	 * @param jsonStr
	 * @return
	 */
	public static List<Object> JsontoList( JsonArray json){

		List<Object> list = new ArrayList<Object>();
		for (int i=0; i<json.size(); i++){ 
			Object value = json.get(i); 
			if(value instanceof JsonArray){
				list.add(JsontoList(((JsonArray) value).toString()));
			} else if(value instanceof JsonObject){ 
				list.add(JsontoMap(((JsonObject) value).toString()));
			} else{
				list.add(value); 
			}
			
		}
		return list;
	}


	/**
	 * 根据key、value生成一个简单json字符串
	 * @param key
	 * @param value
	 * @return
	 */
	public static String SimpleJsonString( String key , Object value ) {
		Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd HH:mm:ss").serializeNulls().create();
		Map<String,Object> map = new HashMap<String, Object>();
		map.put(key, value) ;
		return gson.toJson(map);
	}
	
	/**
	 * 将map对象转换成json字符串
	 * @param map
	 * @return
	 */
	public static String Map2Json(Map<String,Object> map) {
		Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd HH:mm:ss").serializeNulls().create();
		return gson.toJson( map , new TypeToken<Map<String,Object>>(){}.getType()) ;
	}
	
	/**
	 * 将object转化成json字符串
	 * @param obj
	 * @return
	 */
	public static String Object2Json(Object obj) {
		Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd HH:mm:ss").serializeNulls().create();
		return gson.toJson(obj);
		
	}

}
