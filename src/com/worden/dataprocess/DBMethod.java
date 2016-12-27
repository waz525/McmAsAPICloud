package com.worden.dataprocess;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.fh.mplus.demo.SSOChecker;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.worden.common.CommonUtil;
import com.worden.common.GsonUtil;
import com.worden.common.JWTUtil;
import com.worden.db.Cell;
import com.worden.db.MysqlUtils;
import com.worden.db.Row;

/**
 * 数据库操作类
 * @author Worden
 *
 */
public class DBMethod {

	private  MysqlUtils mysql ;
	
	
	public DBMethod() {
		//mysql = new MysqlUtils("jdbc/"+CommonUtil.dataBaseName);
		//mysql = new MysqlUtils("192.168.159.3:3306","root","123456","MobileOA") ;
		mysql = new MysqlUtils(CommonUtil.dbHost+":"+CommonUtil.dbPort,CommonUtil.dbUser,CommonUtil.dbPassword,CommonUtil.dataBaseName) ;
	}
	
	/**
	 * 将数据库查询结果转换成json
	 * @param rowList
	 * @return
	 */
	public String RowList2Json(Row[] rowList ) {
		String rst = "";
		
		if( rowList != null && rowList.length > 0 ) {
			
			for( Row row :rowList) {
				if ( rst.length() > 0 ) rst +="," ;
				Cell[] cellList = row.getCellList() ;
				Map<String,Object> tmp = new HashMap<String,Object>() ;
				for( Cell cell : cellList  ) {
					
					tmp.put(cell.getColumnName(), cell.getValue());

				}
				rst += GsonUtil.Map2Json(tmp) ; ;
			}
			
			if(rowList.length > 1 ) {
				rst = "["+rst+"]" ;
			}
			
		}
		
		if( rst.length() == 0 ) rst =  "[]" ;
		
		return rst ;
	}
	
	/**
	 * 处理Get请求，将查询出的数据转换成json字符串
	 * @param table_name
	 * @param object_id
	 * @param exist_flag
	 * @param filter_content
	 * @return
	 */
	public String doGetQuery( String table_name , String object_id , String exist_flag, String filter_content ) {

		//首先判断表是否存在，后台必须建表，且包含 createdAt , updatedAt两个保留字段
		if( ! mysql.isTableExist(table_name, CommonUtil.dataBaseName)) {
			return GsonUtil.SimpleJsonString("ERROR","Table Not Exist") ;
		}
		
		if( object_id != null ) {
			
			if(  object_id.equalsIgnoreCase("count") ) {
				int num = mysql.getCount(table_name);
				return  GsonUtil.SimpleJsonString("COUNT",num);
			}
			
			
			if( exist_flag != null ) {  // 查询object_id的是否存在
				if( exist_flag.equalsIgnoreCase("exists")) {
					if( mysql.isTableIdExist(table_name, object_id) ) {
						return GsonUtil.SimpleJsonString("EXIST",true);
					} else {
						return GsonUtil.SimpleJsonString("EXIST",false);
					}
				} else {
					return GsonUtil.SimpleJsonString("ERROR","CMD "+exist_flag+" Not Supported");
				}
			}else {  // 查询object_id的所有字段数据
				String sql = "select * from "+table_name+" where id ='"+object_id+"' " ;
				return RowList2Json(mysql.Query(sql));
			}
		} else {
			if(filter_content != null  ) {// 根据filter查询 表里的数据 ,返回json字符串组
		        Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd HH:mm:ss").create();
				Filter filter = gson.fromJson(filter_content, Filter.class);
				
				String sql =  filter.getSqlString(table_name, object_id);
				//CommonUtil.PrintInfo("DBMethod - doGetQuery - sql : "+sql);
				String rst = RowList2Json(mysql.Query( sql )) ;
				//保证返回数据库json数组
				if( rst.indexOf('[') != 0) rst =  "["+rst+"]" ;
				return  rst ; 
				
				
			} else { // 查询 表里所有的数据
				String sql = "select * from "+table_name ;
				return RowList2Json(mysql.Query(sql));
			}
		}
		
		
	}
	

	/**
	 * 解析一般post数据，并执行数据库，返回结果json
	 * @param table_name
	 * @param object_id
	 * @param post_content
	 * @return
	 */
	public String doPostContent( String table_name , String object_id , String post_content , String filter_content ) {
		
		//首先判断表是否存在，后台必须建表，且包含 createdAt , updatedAt两个保留字段
		if( ! mysql.isTableExist(table_name, CommonUtil.dataBaseName)) {
			return GsonUtil.SimpleJsonString("ERROR","Table Not Exist") ;
		}
		//解析Post内容
		PostData pData = new PostData() ;
		pData.distillPostData(post_content,  mysql.getTableDesc(table_name));
		
		if( pData.method != 3 && pData.fields == null) {
			return GsonUtil.SimpleJsonString("ERROR","No Object Value") ;
		}
		
		if( pData.method == 1 ) { // INSERT
			
			String fields = "" ;
			String values = "" ;
			for( int i = 0 ; i<pData.fields.length ; i++) {
				if( fields.length() > 0 ) fields += ", ";
				fields += pData.fields[i] ;
				if( values.length() > 0 ) values += ", ";
				values +="'"+pData.values[i]+"' " ;
			}
			//加入对 createdAt , updatedAt两个保留字段处理
			fields += ", createdAt , updatedAt ";
			values += ", NOW() , NOW() " ;
			
			//获取id，24位，并保证不重复；并加入fields和values
			int idLen = 24 ;
			String id = CommonUtil.GetRandomCodes(idLen) ;
			while( mysql.getCount(table_name, " id = '"+id+"' ") == 1 ) {
				id = CommonUtil.GetRandomCodes(idLen) ;
			}
			fields = "id, "+fields ;
			values = "'"+id+"', "+values ;
			
			//执行sql，如果结果返回不是1，则返回错误
			int rst = mysql.Insert(table_name, fields, values) ;
			if( rst == 1 ) {
				return  RowList2Json(mysql.Query(table_name, fields, " id = '"+id+"' ")) ;
			} else {
				return  GsonUtil.SimpleJsonString("ERROR","Insert Error "+rst) ;
			}
			
		} else if( pData.method == 2 ) { // UPDATE
			if( filter_content == null ) {
				if( !mysql.isTableIdExist(table_name, object_id) ) {
					return GsonUtil.SimpleJsonString("ERROR","Object Not Exist") ;
				}
				if( pData.fields != null &&  pData.values != null ) {
	
					String fields = "" ;
					String str = "" ;
					for( int i = 0 ; i<pData.fields.length ; i++) {
						if( fields.length() > 0 ) fields += ", ";
						fields += pData.fields[i] ;
						if( str.length() > 0 ) str += ", ";
						str += pData.fields[i]+" = '"+pData.values[i]+"' " ;
					}
					//同步修改updatedAt的值
					str += ", updatedAt = NOW() ";
					//执行SQL
					int rst = mysql.Update(table_name, str,  " id = '"+object_id+"' ")  ;
					if( rst == 1) {
						return  RowList2Json(mysql.Query(table_name, fields+",updatedAt", " id = '"+object_id+"' ")) ;
					} else {
						return  GsonUtil.SimpleJsonString("ERROR","Update Data Error "+rst) ;
					}
				}
			} else {//有filter参数，修改一类数据

		        Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd HH:mm:ss").create();
				Filter filter = gson.fromJson(filter_content, Filter.class);
				
				String whereStr = filter.getWhereString();
				

				if( pData.fields != null &&  pData.values != null && whereStr.length() > 0) {
	
					String fields = "" ;
					String str = "" ;
					for( int i = 0 ; i<pData.fields.length ; i++) {
						if( fields.length() > 0 ) fields += ", ";
						fields += pData.fields[i] ;
						if( str.length() > 0 ) str += ", ";
						str += pData.fields[i]+" = '"+pData.values[i]+"' " ;
					}
					//同步修改updatedAt的值
					str += ", updatedAt = NOW() ";
					//执行SQL
					int rst = mysql.Update(table_name, str, whereStr )  ;
					if( rst == 1) {
						String rstStr = RowList2Json(mysql.Query(table_name, fields+",updatedAt",  whereStr)) ;
						if( rstStr.indexOf('[') != 0) rstStr =  "["+rstStr+"]" ;
						return  rstStr ;
					}
				}
			}
			
		} else if( pData.method == 3 ) { // DELETE

			if( filter_content == null ) {
				if( !mysql.isTableIdExist(table_name, object_id) ) {
					return GsonUtil.SimpleJsonString("ERROR","Object not exist") ;
				}
				int rst = mysql.Delete(table_name, " id = '"+object_id+"' ") ;
				if(rst == 1)  {
					return GsonUtil.SimpleJsonString("DELETE",true) ;
				} else {
					return GsonUtil.SimpleJsonString("DELETE",false) ;
				}
			} else {//删除多个对象

		        Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd HH:mm:ss").create();
				Filter filter = gson.fromJson(filter_content, Filter.class);
				
				String whereStr = filter.getWhereString();
				

				if( whereStr.length() > 0) {

					int rst = mysql.Delete(table_name, whereStr) ;
					if(rst > 0 )  {
						return GsonUtil.SimpleJsonString("DELETE",true) ;
					} else {
						return GsonUtil.SimpleJsonString("DELETE",false) ;
					}
				}
			}
		}
		
		return "[]" ;
	}
	
	/**
	 * 校验用户登录
	 * @param table_name
	 * @param username
	 * @param password
	 * @return
	 */
	public String loginByThirdpart( String table_name ,  String accessparam, String deviceid) {
		CommonUtil.PrintInfo("accessparam",accessparam);
		
		String username = accessparam.split(",")[0]  ;
		username = username.substring(username.indexOf("=")+1) ;
		username =  new String ( CommonUtil.decodeBase64( username ) ) ;
		
		String tPassword = accessparam.split(",")[1] ;
		tPassword = tPassword.substring(tPassword.indexOf("=")+1) ;
		tPassword = new String ( CommonUtil.decodeBase64( tPassword ) ) ;
		
		String tToken = accessparam.split(",")[2] ;
		tToken =  tToken.substring(tToken.indexOf("=")+1, tToken.lastIndexOf("}")) ;
		tToken = new String ( CommonUtil.decodeBase64( tToken ) ) ;
		
		CommonUtil.PrintInfo("loginByThirdpart",username,tPassword,tToken);
		
		if( mysql.getCount("user", " username = '"+username+"'") == 0 ) {
			return GsonUtil.SimpleJsonString("ERROR","User not in list");
		}
		
		
		//校验设备号是否已被使用
		if( deviceid != null && deviceid.length() > 0 ) {
			if( mysql.getCount("user", "deviceid = '"+deviceid+"' and username <> '"+username+"'") > 0 ) {
				return GsonUtil.SimpleJsonString("ERROR","Device Used");
			}
		}
		
		if( CommonUtil.isCheckForMPlus == 1 ) {
			String ssoResult = SSOChecker.Check(CommonUtil.MPlusUrl, tToken) ;
			CommonUtil.PrintInfo("SSOChecker",ssoResult);
			Map<String, Object> ssoResultMap = GsonUtil.JsontoMap(ssoResult) ;
			String resultCode = ssoResultMap.get("resultCode").toString() ;
			
			if ( ! resultCode.equalsIgnoreCase("\"0\"")) {
				return GsonUtil.SimpleJsonString("ERROR","Third Server Error");
			}
		}
		
		Row[] row = mysql.Query(table_name, "id,createdAt,updatedAt", " username = '"+username+"'  ");

		String userId = "" ;
		Cell[] cellList = row[0].getCellList() ;
		Map<String,Object> map = new HashMap<String,Object>() ;
		for( Cell cell : cellList  ) {
			
			if( cell.getColumnName().equalsIgnoreCase("id") ) {
				userId = cell.getValue().toString() ;
				map.put("userId", userId);
			} else {
				map.put(cell.getColumnName(), cell.getValue());
			}
		}
		map.put("id", JWTUtil.createToken(userId));
		map.put("ttl", CommonUtil.tokenTTL);
		
		CommonUtil.WriteLog("../logs/login.log","Type: Thirdpart ; User ID: "+userId );
		
		return GsonUtil.Map2Json(map) ;
	}

	
	/**
	 * 校验用户登录
	 * @param table_name
	 * @param username
	 * @param password
	 * @return
	 */
	public String doCheckUserLogin( String table_name , String username , String password, String deviceid) {
		//校验设备号是否已被使用
		if( deviceid != null && deviceid.length() > 0 ) {
			if( mysql.getCount("user", "deviceid = '"+deviceid+"' and username <> '"+username+"'") > 0 ) {
				return GsonUtil.SimpleJsonString("ERROR","Device Used");
			}
		}
		//验证用户名和密码是否正确
		if( mysql.getCount(table_name, " username = '"+username+"' and password = '"+password+"' ") == 0 ) {
			return GsonUtil.SimpleJsonString("ERROR","Username or Password Error");
		}
		
		Row[] row = mysql.Query(table_name, "id,createdAt,updatedAt", " username = '"+username+"' and password = '"+password+"' ");
		
		
		String userId = "" ;
		Cell[] cellList = row[0].getCellList() ;
		Map<String,Object> map = new HashMap<String,Object>() ;
		for( Cell cell : cellList  ) {
			
			if( cell.getColumnName().equalsIgnoreCase("id") ) {
				userId = cell.getValue().toString() ;
				map.put("userId", userId);
			} else {
				map.put(cell.getColumnName(), cell.getValue());
			}
		}
		map.put("id", JWTUtil.createToken(userId));
		map.put("ttl", CommonUtil.tokenTTL);
		
		CommonUtil.WriteLog("../logs/login.log","Type: UserPwd ; User ID: "+userId );
		
		return GsonUtil.Map2Json(map) ;
	}

	//垃圾回收处理，关闭mysql实例
	public void finalize(){
		mysql.Close();
	}
	
	/**
	 * 获取表数据量
	 * @param table_name
	 * @return
	 */
	public String doGetTableCount( String table_name , String filter_content) {

		//首先判断表是否存在，如果不存在，则报错返回
		if( ! mysql.isTableExist(table_name, CommonUtil.dataBaseName)) {
			return GsonUtil.SimpleJsonString("ERROR","Table Not Exist") ;
		}
		
		int num = 0 ;
		
		if(filter_content != null  ) {// 根据filter查询 表里的数据 ,返回json字符串组
	        Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd HH:mm:ss").create();
			Filter filter = gson.fromJson(filter_content, Filter.class);
			
			num = mysql.getCount(table_name,filter.getWhereString());
		} else {
			num = mysql.getCount(table_name);
		}
		
		return  GsonUtil.SimpleJsonString("COUNT",num);
	}
	
	/**
	 * 建表，自动增加 id 、createdAt、updatedAt三个字段
	 * @param table_name
	 * @param post_content
	 * @return
	 */
	public String doCreateTable( String table_name , String post_content) {
		//首先判断表是否存在，如果存在，则报错返回
		if( mysql.isTableExist(table_name, CommonUtil.dataBaseName)) {
			return GsonUtil.SimpleJsonString("ERROR","Table Exist") ;
		}
		
		String sql = "" ;
		Map<String, Object> map = GsonUtil.JsontoMap(post_content);
		for( Entry<String, Object> entry : map.entrySet()) {
			String str = entry.getValue().toString() ;
			sql += entry.getKey()+" "+str.substring(str.indexOf("\"")+1,str.lastIndexOf("\""))+", " ;
		}
		
		if( sql.length() > 0 ) {
			sql = "create table "+table_name+" ( id varchar(30), "+sql+" createdAt DATETIME , updatedAt DATETIME )" ;
		} else {
			return GsonUtil.SimpleJsonString("ERROR","Table Filed Error") ;
		}

		if( mysql.Update(sql) != 0 ) {
			return GsonUtil.SimpleJsonString("ERROR","Table Create Error") ;
		}
		
		return doTableFields(table_name);
		
	}
	
	/**
	 * 增加字段
	 * @param table_name
	 * @param post_content
	 * @return
	 */
	public String doAddField( String table_name , String post_content) {
		//首先判断表是否存在，如果不存在，则报错返回
		if( ! mysql.isTableExist(table_name, CommonUtil.dataBaseName)) {
			return GsonUtil.SimpleJsonString("ERROR","Table Not Exist") ;
		}
		 
		String[] fields = mysql.getTableDesc(table_name);
		List<String> fieldList = Arrays.asList(fields);
		Map<String,Object> map = GsonUtil.JsontoMap(post_content);
		for( Entry<String, Object> entry : map.entrySet()) {//先判断字段是否存在，存在则报错
			String str = entry.getKey() ;
			if( fieldList.contains(str) ){
				return GsonUtil.SimpleJsonString("ERROR","Field '"+str+"' Aready Exist") ;
			}
		}
		
		for( Entry<String, Object> entry : map.entrySet()) {
			String str = entry.getValue().toString() ;
			str = str.substring(str.indexOf("\"")+1,str.lastIndexOf("\"")) ;
			String sql = "alter table "+table_name+" add "+entry.getKey()+" "+str+" " ;
			if( mysql.Update(sql) < 0  ) {
				return GsonUtil.SimpleJsonString("ERROR","Add Field Error") ;
			}
		}

		
		return doTableFields(table_name);
		
	}

	/**
	 * 删除字段
	 * @param table_name
	 * @param post_content
	 * @return
	 */
	public String doDelField( String table_name , String post_content) {
		//首先判断表是否存在，如果不存在，则报错返回
		if( ! mysql.isTableExist(table_name, CommonUtil.dataBaseName)) {
			return GsonUtil.SimpleJsonString("ERROR","Table Not Exist") ;
		}


		String[] fields = mysql.getTableDesc(table_name);
		List<String> fieldList = Arrays.asList(fields);
		Map<String,Object> map = GsonUtil.JsontoMap(post_content);
		for( Entry<String, Object> entry : map.entrySet()) {//先判断字段是否存在，不存在则报错
			String str = entry.getKey() ;
			if( !fieldList.contains(str) ){
				return GsonUtil.SimpleJsonString("ERROR","Field '"+str+"' Not Exist") ;
			}
		}
	
		for( Entry<String, Object> entry : map.entrySet()) {
			if( entry.getValue().toString().indexOf("true") > -1 ) {
				String sql = "alter table "+table_name+" drop  "+entry.getKey()+" " ;
				if( mysql.Update(sql) < 0  ) {
					return GsonUtil.SimpleJsonString("DeleteField",false) ;
				}
			}
		}
		return GsonUtil.SimpleJsonString("DeleteField",true) ;
		
	}

	/**
	 * 删除表
	 * @param table_name
	 * @return
	 */
	public String doDropTable( String table_name) {
		//首先判断表是否存在，如果不存在，则报错返回
		if( ! mysql.isTableExist(table_name, CommonUtil.dataBaseName)) {
			return GsonUtil.SimpleJsonString("ERROR","Table Not Exist") ;
		}

		
		String sql = "drop table "+table_name+" " ;
		if( mysql.Update(sql)  < 0 ) {
			return GsonUtil.SimpleJsonString("DROP",false) ;
		}
		return GsonUtil.SimpleJsonString("DROP",true) ;
		
	}
	
	/**
	 * 获取表字段列表
	 * @param table_name
	 * @return {"Fields":[字段列表]}
	 */
	public String doTableFields(String table_name) {
		//首先判断表是否存在，如果不存在，则报错返回
		if( ! mysql.isTableExist(table_name, CommonUtil.dataBaseName)) {
			return GsonUtil.SimpleJsonString("ERROR","Table Not Exist") ;
		}
		
		String[] tableDesc = mysql.getTableDesc(table_name) ;
		return GsonUtil.SimpleJsonString("Fields",tableDesc) ;
		
	}
	
}
