package com.worden.db;



/**
 * 用于定义单个值，字段名，值、值类型
 * @author Worden
 *
 */
public class Cell {
	
	private String ColumnName = null;
	
	private Object Value = null;
	
	private int Type = ValueType.Error; 
	
	public void setColumnName( String columnname) {
		this.ColumnName = columnname ;
	}
	
	public void setValue( Object value) {
		this.Value = value ;
	}

	public void setType( int type ) {
		this.Type = ValueType.getValueTypeByColumnType(type) ;
	}
	
	public String getColumnName() {
		return this.ColumnName;
	}
	
	public Object getValue() {
		return this.Value ;
	}
	
	public int getType() {
		return this.Type ;
	}
	
	public void setValues( String columnname, Object value, int type) {
		this.ColumnName = columnname ;
		this.Value = value ;
		this.Type = ValueType.getValueTypeByColumnType(type) ;

	}

	
	public void setValues( Object value, int type) {
		this.ColumnName = null ;
		this.Value = value ;
		this.Type = ValueType.getValueTypeByColumnType(type) ;
		
	}
	
	public void setColumnValues( String columnname,  int type) {
		this.ColumnName = columnname ;
		this.Value = null ;
		this.Type = ValueType.getValueTypeByColumnType(type) ;
	}
	
	
}
