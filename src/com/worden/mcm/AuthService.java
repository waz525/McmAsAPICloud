package com.worden.mcm;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.worden.common.CommonUtil;
import com.worden.common.GsonUtil;
import com.worden.dataprocess.DBMethod;

/**
 * Servlet 处理  用户登录和退出
 */
@WebServlet("/AuthService")
public class AuthService extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public AuthService() {
        super();
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doPost(request, response);
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		

		request.setCharacterEncoding("UTF-8");
		response.setCharacterEncoding("UTF-8");
		response.setContentType("application/json; charset=utf-8"); 
		response.setHeader("Access-Control-Allow-Origin", "*");


		PrintWriter out = response.getWriter();

		String reqTableName = request.getParameter("reqTableName");
		//login or logout
		String reqMethod = request.getParameter("reqMethod");
		
		DBMethod dbm = new DBMethod() ;
		
		if(reqMethod.equalsIgnoreCase("login") ) { //登录
			String username = request.getParameter("username") ;
			String password = request.getParameter("password") ;
			String deviceid = request.getParameter("deviceid") ;			
			
			out.print( dbm.doCheckUserLogin(reqTableName, username, password, deviceid) );

		} else if(reqMethod.equalsIgnoreCase("loginByThirdpart") ) { //验证第三方登录
			String accessparam = request.getParameter("accessparam") ;
			String deviceid = request.getParameter("deviceid") ;
			
			out.print( dbm.loginByThirdpart(reqTableName, accessparam , deviceid) );
			
		} else if(reqMethod.equalsIgnoreCase("logout") ) { //退出，直接返回一个json串
			out.println(GsonUtil.SimpleJsonString("accessToken","0"));
		} else if(reqMethod.equalsIgnoreCase("checkLogin") ) { //使用token登录
			CommonUtil.WriteLog("../logs/login.log","Type: Torken ; User ID: "+request.getParameter("id") );
			out.print(GsonUtil.SimpleJsonString("LOGIN",true));
		} else {
			out.println(GsonUtil.SimpleJsonString("ERROR","Invalid AuthService Method"));
		}
		
		out.flush();
		out.close();
		
		
	}

}
