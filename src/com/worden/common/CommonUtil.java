package com.worden.common;


import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Properties;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 通用类，常用函数和常量
 * @author Worden
 *
 */
public class CommonUtil {

	
	/**
	 * token生存周期(s)，默认7天
	 */
	public static int tokenTTL = 604800 ;
	/**
	 * 数据库服务器IP，保存于WEB-INF/config/dbconfig.properties
	 */
	public static String  dbHost = GetPropertiesValue("dbconfig.properties", "Host");

	/**
	 * 数据库服务器端口，保存于WEB-INF/config/dbconfig.properties
	 */
	public static String  dbPort = GetPropertiesValue("dbconfig.properties", "Port");

	/**
	 * 数据库用户名，保存于WEB-INF/config/dbconfig.properties
	 */
	public static String  dbUser = GetPropertiesValue("dbconfig.properties", "User");

	/**
	 * 数据库用户密码，保存于WEB-INF/config/dbconfig.properties
	 */
	public static String  dbPassword = GetPropertiesValue("dbconfig.properties", "Password");

	/**
	 * 数据库名，保存于WEB-INF/config/dbconfig.properties
	 */
	public static String  dataBaseName = GetPropertiesValue("dbconfig.properties", "DBName");

	/**
	 * 是否对M-Plus进行用户验证，保存于WEB-INF/config/MPlusServer.properties
	 */
	public static int  isCheckForMPlus = Integer.parseInt( GetPropertiesValue("MPlusServer.properties", "isCheckForMPlus") );
	
	/**
	 * M-Plus进行用户验证URL，保存于WEB-INF/config/MPlusServer.properties
	 */
	public static String  MPlusUrl = GetPropertiesValue("MPlusServer.properties", "MPlusUrl");
	
	/**
	 * 读取配置文件设置
	 * @param propFile
	 * @param key
	 * @return
	 */
	public static String GetPropertiesValue(String propFile , String key) {

		Properties pro = new Properties();
		try{
			FileInputStream in = new FileInputStream(CommonUtil.class.getResource("/").getPath()+"../config/"+propFile);
			pro.load(in); 
		}catch(FileNotFoundException e){
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return pro.getProperty(key); 
	}
	
	
	/**
	 * iso8859-1转化UTF-8
	 * @param old
	 * @return
	 */
	public static String ISO2UTF( String old )
	{
		String newer = null ;
		try {
			newer = new String( old.getBytes("iso8859-1"),"utf-8") ;
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		return newer ;
	}

	/**
	 * UTF-8转化iso8859-1
	 * @param old
	 * @return
	 */
	public static String UTF2ISO( String old )
	{
		String newer = null ;
		try {
			newer = new String( old.getBytes("utf-8"),"iso8859-1") ;
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		return newer ;
	}

	/**
	 * iso8859-1 转化 gb2312
	 * @param old
	 * @return
	 */
	public static String ISO2GB( String old ) {
		String newer = null ;
		try {
			newer = new String( old.getBytes("iso8859-1"),"gb2312") ;
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		return newer ;
	}
	
	/**
	 * gb2312 转化 iso8859-1
	 * @param old
	 * @return
	 */
	public static String GB2ISO( String old ) {
		String newer = null ;
		try {
			newer = new String( old.getBytes("gb2312"),"iso8859-1") ;
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		return newer ;
	}
	

	/**
	 * gb2312 转化 UTF-8
	 * @param old
	 * @return
	 */
	public static String GB2UTF( String old ) {
		String newer = null ;
		try {
			newer = new String( old.getBytes("gb2312"),"utf-8") ;
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		return newer ;
	}

	/**
	 * UTF-8 转化 gb2312 
	 * @param old
	 * @return
	 */
	public static String UTF2GB( String old ) {
		String newer = null ;
		try {
			newer = new String( old.getBytes("utf-8"),"gb2312") ;
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		return newer ;
	}
	
	/**
	 * 将字符串由 oldEncode 转化 newEncode
	 * @param old
	 * @param oldEncode
	 * @param newEncode
	 * @return
	 */
	public static String ChangeCharacterEncoding(String old , String oldEncode , String newEncode) {

		String newer = null ;
		try {
			newer = new String( old.getBytes(oldEncode),newEncode) ;
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		return newer ;
	}
	

	/**
	 * 打印到标准输出
	 * @param info
	 * @param args
	 */
	public static void PrintInfo( String... infos) {
        for (String s : infos) {  
            System.out.print(" ==> "+s+" <== " );  
        }
        System.out.println();
	}
	
	/**
	 * 将日志信息输出到文件中
	 * @param fileName
	 * @param logInfo
	 */
	public static void WriteLog( String fileName , String logInfo) {
		PrintWriter out = null;
		try {
			out = new PrintWriter(new FileOutputStream(fileName, true));
			out.println( ( new SimpleDateFormat("[yyyy-MM-dd HH:mm:ss]")).format(new Date()) + " "+logInfo);
			out.close();
		} catch (FileNotFoundException e){
		    e.printStackTrace();
	    }
	}

	/**
	 * 正则表达式提取
	 * @param content
	 * @param regex
	 * @param Index
	 * @return
	 */
	public static String[] GetRexgexStrings( String content , String regex ,  int Index )
	{
		ArrayList<String> list = new ArrayList<String>() ;
		Pattern pattern = Pattern.compile(regex);
		Matcher matcher = pattern.matcher(content);
		while(matcher.find())
		{
			list.add(matcher.group(Index));
		}
		String[] strs = ( String[] ) list.toArray( new String[0] ) ;
		return strs ;
	}
	
	/**
	 * 取一定长度的随机字符串,最大为32
	 * @param len
	 * @return
	 */
	public static String GetRandomCodes(int len) {
        String s = null;
        try {
            s = new String(UUID.randomUUID().toString().replaceAll("-", "").substring(0, (len>32?32:len)));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return s;
    }
	
	/**
	 * 去除决首尾双引号
	 * @param oldStr
	 * @return
	 */
	public static String DeleteAroundDoubleQuotationMarks(String oldStr) {
		int start = oldStr.indexOf("\"") ;
		int end = oldStr.lastIndexOf("\"") ;
		if( start >= 0 && end > 0 && end > start) {
			return oldStr.substring(start+1,end) ;
		}else {
			return oldStr ;
		}
		
	}


	/***
	 * encode by Base64
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public static String encodeBase64(byte[]input) {
		
		try {
			Class clazz = Class.forName("com.sun.org.apache.xerces.internal.impl.dv.util.Base64");
			Method mainMethod= clazz.getMethod("encode", byte[].class);
			mainMethod.setAccessible(true);
			Object retObj=mainMethod.invoke(null, new Object[]{input});
			return (String)retObj;
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NoSuchMethodException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SecurityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null ;
	}
	/***
	 * decode by Base64
	 */
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public static byte[] decodeBase64(String input) {
		try {
			Class clazz=Class.forName("com.sun.org.apache.xerces.internal.impl.dv.util.Base64");
			Method mainMethod;
			mainMethod = clazz.getMethod("decode", String.class);
			mainMethod.setAccessible(true);
			 Object retObj=mainMethod.invoke(null, input);
			 return (byte[])retObj;
		} catch (NoSuchMethodException | SecurityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null ;
	}


}
