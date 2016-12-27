package com.worden.dataprocess;

import java.util.ArrayList;
import java.util.Map;

import com.worden.common.GsonUtil;

public class PostData {
	/**
	 * 1: INSERT ; 2: UPDATE ; 3:DELETE
	 */
	public int method ;
	public String[] fields = null ;
	public String[] values = null ;
	
	public PostData() {
		this.method = -1 ;
	}

	/**
	 * 判断是否包含key；key大写
	 * @param key
	 * @return
	 */
	public boolean isContainsKey( Map<String,Object> map, String key ) {
		if( map.containsKey(key.toUpperCase()) ) {
			return true ;
		}
		return false ;
	}
	
	/**
	 * 根据key值获取value；key大写；存在则返回null
	 * @param map
	 * @param key
	 * @return
	 */
	public String getValueString( Map<String,Object> map, String key ) {
		if( map.containsKey(key.toUpperCase()) ) {
			String str = map.get(key.toUpperCase()).toString() ;
			return str.substring(str.indexOf("\"")+1,str.lastIndexOf("\""));
		}
		return null ;
	}
	
	/**
	 * 解析POST数据，组成字段和值的队列
	 * @param postContent
	 * @param tableDesc
	 */
	public void distillPostData(String postContent, String[] tableDesc ) {
		
		Map<String, Object> postMap = GsonUtil.JsontoMapUpperKey(postContent);
		
		if( postContent.isEmpty()) {
			return ;
		}

		String md = getValueString(postMap, "_method") ;
		if( md != null ) {
			md = md.toUpperCase();
			if( md.equals("DELETE")) { 
				this.method = 3 ;
			} else if( md.equals("PUT")){ // UPDATE
				this.method = 2 ;
				ArrayList<String> fieldList = new ArrayList<String>() ;
				ArrayList<String> valueList = new ArrayList<String>() ;
				for( String field : tableDesc) {
					String t = getValueString(postMap, field) ;
					if( t != null ) {
						fieldList.add(field) ;
						valueList.add(t) ;
					}
				}
				if( ! fieldList.isEmpty()) this.fields = (fieldList.toArray(new String[0])) ;
				if( ! valueList.isEmpty()) this.values = valueList.toArray(new String[0]) ;
			}
		} else { //INSERT
			this.method = 1 ;
			ArrayList<String> fieldList = new ArrayList<String>() ;
			ArrayList<String> valueList = new ArrayList<String>() ;
			for( String field : tableDesc) {
				String t = getValueString(postMap, field) ;
				if( t != null ) {
					fieldList.add(field) ;
					valueList.add(t) ;
				}
			}
			if( ! fieldList.isEmpty()) this.fields = fieldList.toArray(new String[0]) ;
			if( ! valueList.isEmpty()) this.values = valueList.toArray(new String[0]) ;
			
		}
		
	}

	
}
