package com.worden.db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;

import com.worden.common.CommonUtil;

/**
 * mysql数据库 操作类
 * @author Worden
 *
 */
public class MysqlUtils {

	private Connection connection = null ;
	private Statement stmt  ;
	
	public MysqlUtils(String PoolName) {
		 getConnection(PoolName) ;		
	}
	
	public MysqlUtils(String serverInfo,String userName,String userPasswd,String dbName) {
		getConnection( serverInfo, userName, userPasswd, dbName);
	}

	
	/**
	 * 设置连接
	 * @param holdone
	 */
	public void setConnection(Connection holdone) {
		this.connection = holdone ;
		try{
			stmt = connection.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE,ResultSet.CONCUR_UPDATABLE);
		}catch( SQLException e) {
			e.printStackTrace();
		}
	}
	
	
	/**
	 * 根据连接池串建立连接
	 * @param PoolName
	 */
	public void getConnection(String PoolName) {
		DataSource ds = null ;
		try{
			InitialContext ctx = null;
			ctx = new InitialContext();
			//ds = (DataSource)ctx.lookup("java:comp/env/jdbc/myweb");
			ds = (DataSource)ctx.lookup("java:comp/env/"+PoolName);
			connection =  ds.getConnection() ;
			stmt = connection.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE,ResultSet.CONCUR_UPDATABLE);
		}catch(NamingException e)
		{
			e.printStackTrace() ;
		}catch(SQLException sqle)
		{
			sqle.printStackTrace();
		}
	}
	
	/**
	 * 根据用户名等属性建立连接
	 * @param serverInfo
	 * @param userName
	 * @param userPasswd
	 * @param dbName
	 */
	public void getConnection(String serverInfo,String userName,String userPasswd,String dbName) {
		try{
			String url="jdbc:mysql://"+serverInfo+"/"+dbName+"?user="+userName+"&password="+userPasswd;
			Class.forName("com.mysql.jdbc.Driver").newInstance();
			connection = DriverManager.getConnection(url);
			stmt = connection.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE,ResultSet.CONCUR_UPDATABLE);
		}catch (SQLException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		} 
		
	}
	
	/**
	 * 关闭连接
	 */
	public void Close() {
		try{
			if( this.stmt != null )
			{
				stmt.close();
				stmt = null ;				
			}
			if( this.connection != null )
			{
				connection.close() ;
				connection = null ;
			}
		}catch( SQLException e )
		{
			e.printStackTrace() ;
		}
	}
	
	
	public String[] getTableDesc(String table_name ) {
		ArrayList<String> rst = new ArrayList<String>();
		try {
			ResultSet rs = stmt.executeQuery("SELECT * FROM "+table_name + " WHERE 1 <> 1") ;
			ResultSetMetaData rsmd = rs.getMetaData();
			for (int i = 1; i <= rsmd.getColumnCount(); i++) {
				rst.add(rsmd.getColumnName(i) ) ;
			}
			
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return rst.toArray(new String[0]);
	}
	
	/**
	 * 将Result转换为RowList
	 * @param rs ResultSet
	 * @return
	 */
	public Row[] Result2RowList(ResultSet rs ) {
		Row[] rst = null;
		try {
			ResultSetMetaData rsmd = rs.getMetaData();
			ArrayList<Cell> colList = new ArrayList<Cell>() ; 
			
			for (int i = 1; i <= rsmd.getColumnCount(); i++) {
				Cell cell = new Cell();
				cell.setColumnValues( rsmd.getColumnName(i), rsmd.getColumnType(i));
				colList.add(cell);
								
			} 
			
			Cell[] columnList = (com.worden.db.Cell[] )colList.toArray( new  Cell[0] );
			
			//取得记数条数，并初始化Row[]
			//rs.last();
			//rst = new Row[ rs.getRow() ];
			//rs.beforeFirst();

			
			ArrayList<Row> rowList = new ArrayList<Row>() ; 
			while ( rs.next() ) {
				colList = new ArrayList<Cell>() ;
				
				for( int i = 1 ; i<=rsmd.getColumnCount() ; i++) {
					Cell cell = new Cell();
					cell.setValues(columnList[i-1].getColumnName(), rs.getObject(i),columnList[i-1].getType());
					colList.add(cell);
				}
				
				Row r = new Row();
				r.setCellList( (com.worden.db.Cell[] )colList.toArray( new  Cell[0] ));
				rowList.add(r) ;
			}
			
			rst = (com.worden.db.Row[] )rowList.toArray( new  Row[0] );
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return rst;
	}

	/**
	 * 查询sql
	 * @param sql
	 * @return
	 */
	public Row[] Query( String sql )   {
		try {
			CommonUtil.PrintInfo("MysqlUtils - Query - sql : "+sql);
			return Result2RowList( stmt.executeQuery(sql) );
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * 根据 table_name、content 查询
	 * @param content
	 * @param table_name
	 * @return
	 */
	public Row[] Query( String table_name, String content  )  {
		String sql = "select "+content+" from "+table_name ;
		return Query(sql) ;
	}
	
	/**
	 * 根据 table_name、content、qualification 查询
	 * @param content
	 * @param table_name
	 * @param qualification
	 * @return
	 */
	public Row[] Query( String table_name , String content , String qualification )  {
		String sql = "select "+content+" from "+table_name+" where "+qualification ;
		return Query(sql) ;
	}
	
	/**
	 * 执行sql
	 * @param sql
	 * @return
	 */
	public int Update( String sql ) {
		int rst = 0 ;
		CommonUtil.PrintInfo("MysqlUtils - Update - sql : "+sql);
		try {
			rst = stmt.executeUpdate( sql ) ;
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return rst ;
	}
	
	/**
	 * 根据 table_name、content、qualification 执行
	 * @param table_name
	 * @param content
	 * @param qualification
	 * @return
	 */
	public int Update( String table_name , String content , String qualification ) {
		String sql = "update "+table_name+" set "+content+" where "+qualification ;
		return Update(sql);
	}
	
	/**
	 * 根据 table_name、field、values 插入数据
	 * @param table_name
	 * @param field
	 * @param values
	 * @return
	 */
	public int Insert( String table_name , String field , String values ) {
		String sql = "insert into "+table_name+" ( "+field+" ) values ( "+values+" )" ;
		CommonUtil.PrintInfo("MysqlUtils - Insert - sql : "+sql);
		int rst = 0 ;
		try {
			rst = stmt.executeUpdate( sql ) ;
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return rst ;
	}
	
	/**
	 * 根据 table_name、qualification 删除数据
	 * @param table_name
	 * @param qualification
	 * @return
	 */
	public int Delete( String table_name , String qualification ) {
		String sql = "delete from "+table_name+" where "+qualification+" ";
		CommonUtil.PrintInfo("MysqlUtils - Delete - sql : "+sql);
		int rst = 0 ;
		try {
			rst = stmt.executeUpdate( sql ) ;
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return rst ;
	}
	
	/**
	 * 获取表数据量
	 * @param table_name
	 * @return
	 */
	public int getCount( String table_name ) {
		return getCount(table_name, "1 = 1");
	}
	
	/**
	 * 根据条件，获取数据量
	 * @param table_name
	 * @param qualification
	 * @return
	 */
	public int getCount( String table_name , String qualification ) {
		String sql = "select count(*) from "+table_name+" where "+qualification ;
		CommonUtil.PrintInfo("MysqlUtils - getCount - sql : "+sql);
		ResultSet rs;
		int count = 0 ;
		try {
			rs = stmt.executeQuery(sql);
			rs.next() ;
			count = rs.getInt(1) ;
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return count ;
	}
	
	/**
	 * 判断表是否存在
	 * @param table_name
	 * @param database_name
	 * @return
	 */
	public boolean isTableExist(String table_name , String database_name ) {
		/*
		if( getCount("information_schema.tables" , "TABLE_SCHEMA = '"+database_name+"' and TABLE_NAME = '"+table_name+"' ") == 0 ) {
			return false ;
		} else {
			return true ;
		}
		*/
		return true;
	}
	
	/**
	 * 判断表是否存在
	 * @param table_name
	 * @param database_name
	 * @return
	 */
	public boolean isTableIdExist(String table_name , String table_id ) {
		if( getCount(table_name ,  "id = '"+table_id+"' ") == 0 ) {
			return false ;
		} else {
			return true ;
		}
	}
	
	/**
	 * 获取第 rowNum 行 第columnIndex 列的整数
	 * @param rs
	 * @param rowNum
	 * @param columnIndex
	 * @return
	 */
	public int getIntResult(ResultSet rs ,int rowNum ,int columnIndex) {
		int rst = -1 ;
		try {
			for( int i = 0 ; i < rowNum ; i++) {
				rs.next() ;
			}				
			rst = rs.getInt(columnIndex) ;
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return rst ;
	}
	
	/**
	 * 获取第 rowNum 行 第columnIndex 列的字符串
	 * @param rs
	 * @param rowNum
	 * @param columnIndex
	 * @return
	 */
	public String getStringResult(ResultSet rs ,int rowNum ,int columnIndex) {
		String rst = null ;
		try {
			for( int i = 0 ; i < rowNum ; i++) {
				rs.next() ;
			}				
			rst = rs.getString(columnIndex) ;
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return rst ;
	}
}
