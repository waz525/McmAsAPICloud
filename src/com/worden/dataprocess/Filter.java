package com.worden.dataprocess;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;


import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import com.google.gson.internal.LinkedTreeMap;
import com.worden.common.CommonUtil;
import com.worden.common.GsonUtil;
/**
 * 处理查询请求的Fiter
 * <br>支持 fields（列表或json）、order、where、limit、skip
 * <br>支持 sql
 * 
 * @author Worden
 *
 */
public class Filter {
	public Object fields ;
	private String order ;
	public JsonObject where ;
	private int limit = 20 ;
	private int skip = 0 ;
	private String sql ;
	private String[] operators = { "gt" ,"gte","lt","lte","ne","like","nlike"}  ;

	/**
	 * 如果sql不为空，返回sql；否则组合sql
	 * @param table_name
	 * @param object_id
	 * @return
	 */
	public String getSqlString(String table_name , String object_id ) {
		if( this.sql != null ) {
			return this.sql ;
		} else {
			return this.produceQuerySql(table_name, object_id) ;
		}
	}


	/**
	 * 根据filter内容，组合成sql语句
	 * @param table_name
	 * @param object_id 对象名，一般查询无此值
	 * @return
	 */
	@SuppressWarnings("unchecked")
	private String produceQuerySql(String table_name , String object_id ) {
		String rst = "select " ;
		if( this.fields == null){ //未有 fields 字段
			rst += "* " ;
		} else if( fields instanceof ArrayList) { // fields 为数列形式
			List<Object> fieldList =(List<Object>)fields;
			String tmp = "" ;
			for( Object str : fieldList) {
				if( tmp.length() > 0 ) tmp +=", ";
				tmp +=str+" " ;
			}
			rst += tmp ;
		} else if( fields instanceof LinkedTreeMap) { // fields 为json形式
			Map<String, Object> fieldMap = (LinkedTreeMap<String, Object>)fields;
			String tmp = "" ;
			for( Entry<String, Object> entry : fieldMap.entrySet()) {
				if( Boolean.parseBoolean(entry.getValue().toString()) ) {
					if( tmp.length() > 0 ) tmp +=", ";
					tmp +=entry.getKey()+" " ;
				}
			}

			rst += tmp ;
		} else {
			rst += "* " ;
		}
		
		rst += "from "+table_name+" " ;
		
		String whereStr = "" ;
		
		if( object_id != null && ! object_id.isEmpty()) {
			whereStr += " id = '"+object_id+"' ";
		}
		

		if( this.where != null ) {
			Map<String, Object> whereMap = GsonUtil.JsontoMap(this.where) ;
			whereStr += DistillWhereMap("",whereMap,1 );
			
		}
		
		if( whereStr.length() > 0 ) {
			rst += "where "+whereStr+" ";
		}
		
		
		if( this.order != null ) {
			rst += "order by "+this.order+" ";
		}
		
		rst += "limit "+this.skip+","+this.limit+" " ;

		return rst ;
	}
	
	/**
	 * 组合where语句
	 * @return
	 */
	public String getWhereString() {
		if( this.where == null ) return "1 = 1" ;
		Map<String, Object> whereMap = GsonUtil.JsontoMap(this.where) ;
		return DistillWhereMap("",whereMap,1 );
		
	}
	
	/**
	 * 使用递归的方式，组成where语句
	 * @param lastKey 上一层的key值
	 * @param whereMap 待处理的where
	 * @param flag 1：and；2：or
	 * @return
	 */
	@SuppressWarnings("unchecked")
	private String DistillWhereMap( String lastKey ,Map<String, Object> whereMap , int flag) {
		String rst = "" ;
		for( Entry<String, Object> entry : whereMap.entrySet()) {

			if( rst.length() > 0 ) {
				if( flag == 1 ) rst += " and " ;
				else rst += " or " ;
			}
			
			String key = entry.getKey();
			Object value = entry.getValue() ;
			if( key.equalsIgnoreCase("or") &&  value instanceof ArrayList ) {//处理 or
				List<Object> values =(List<Object>)value;
				String tmp = "" ;
				for( Object val : values) {
					if( tmp.length() > 0 )  tmp += " or " ;
					tmp += DistillWhereMap("or",(Map<String, Object>)val , 2 ) ;
				}
				if( tmp.length() > 0 ) rst += "( "+tmp+" ) " ;
			} else if( key.equalsIgnoreCase("and") &&  value instanceof ArrayList ) {//处理 and
				List<Object> values =(List<Object>)value;
				String tmp = "" ;
				for( Object val : values) {
					if( tmp.length() > 0 )  tmp += " and " ;
					tmp += DistillWhereMap("and",(Map<String, Object>)val , 1 ) ;
				}
				if( tmp.length() > 0 ) rst += "( "+tmp+" ) " ;
			} else if( key.equalsIgnoreCase("between") ) { //组成between语句
				List<Object> values =(List<Object>)value;
				rst += " ( "+lastKey + " between '" + CommonUtil.DeleteAroundDoubleQuotationMarks(values.get(0).toString())+"' and '"+ CommonUtil.DeleteAroundDoubleQuotationMarks(values.get(1).toString())+"' ) " ;

			} else if( key.equalsIgnoreCase("inq") ) { //组成in语句
				List<Object> values =(List<Object>)value;
				String tmp = "" ;
				for( Object str : values ) {
					if( tmp.length() > 0 ) tmp +=", " ;
					tmp += "'"+CommonUtil.DeleteAroundDoubleQuotationMarks(str.toString())+"' " ;
				}
				if( tmp.length() > 0 ) {
					rst += lastKey + " in ( "+tmp+") "  ;
				}
			} else if( key.equalsIgnoreCase("nin") ) {//组成not in语句
				List<Object> values =(List<Object>)value;
				String tmp = "" ;
				for( Object str : values ) {
					if( tmp.length() > 0 ) tmp +=", " ;
					tmp += "'"+CommonUtil.DeleteAroundDoubleQuotationMarks(str.toString())+"' " ;
				}
				if( tmp.length() > 0 ) {
					rst += lastKey + " not in ( "+tmp+") "  ;
				}
			} else if( value instanceof HashMap ) { //处理 Map类value
				rst += DistillWhereMap(key,(Map<String, Object>)value , flag ) ;
			} else if( value instanceof JsonPrimitive ) { //组成value为值的语句
				List<String> operatorList =  Arrays.asList(operators );
				//断送是否包含运算符
				if( operatorList.contains(key)) {
					rst += " ( "+ this.GetOperatorString(lastKey, key, CommonUtil.DeleteAroundDoubleQuotationMarks(value.toString()))+" ) ";
				} else {
					rst += " ( "+ key + " = '"+CommonUtil.DeleteAroundDoubleQuotationMarks(value.toString())+"' ) " ;
				}
			} 

		}
		
		return rst ;
	}

	/**
	 * 根据运算符组合判断语句
	 * @param key
	 * @param operator
	 * @param value
	 * @return
	 */
	private String GetOperatorString(String key, String operator, String value) {
		String rst = "" ;
		if(operator.equalsIgnoreCase("gt") ) {
			rst = key + " > '"+ value + "'" ;
		} else if(operator.equalsIgnoreCase("gte") ) {
			rst = key + " >= '"+ value + "'"  ;
		} else if(operator.equalsIgnoreCase("lt") ) {
			rst = key + " < '"+ value + "'"  ;
		} else if(operator.equalsIgnoreCase("lte") ) {
			rst = key + " <= '"+ value + "'"  ;
		} else if(operator.equalsIgnoreCase("ne") ) {
			rst = key + " <> '"+ value + "'"  ;
		} else if(operator.equalsIgnoreCase("like") ) {
			rst = key + " like '%"+ value + "%'"  ;
		} else if(operator.equalsIgnoreCase("nlike") ) {
			rst = key + " not like '"+ value + "'"  ;
		}
		
		return rst;
	}

	
}
