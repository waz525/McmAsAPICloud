package com.worden.db;

import java.sql.Types;

public class ValueType {
	/**
	 * 字符串 VARCHAR 
	 */
	public final static int Error = -1 ; 
	
	/**
	 * 字符串 VARCHAR 
	 */
	public final static int String = 1 ; 
	
	/**
	 * 数字
	 */
	public final static int Number  = 2 ; 
	
	/**
	 * 布尔量选择 ENUM 
	 */
	public final static int Boolean   = 3 ; 
	
	/**
	 * 日期时间 DATETIME 
	 */
	public final static int Date  = 4 ; 
	
	/**
	 * 双精度浮点型数据 DOUBLE 
	 */
	public final static int Double = 5 ; 
	
	
	public static int getValueTypeByColumnType( int columnType) {
		switch (columnType) {
		
		case Types.BIT:
		case Types.INTEGER:
			return Number;
			
		case Types.BOOLEAN:
			return Boolean ;
			
		case Types.DOUBLE:
			return Double ;
			
		case Types.CHAR:
		case Types.VARCHAR:
			return String ;
		
		case Types.DATE:
			return Date;
		
		}
		return ValueType.Error ;
	}
	
	
}
