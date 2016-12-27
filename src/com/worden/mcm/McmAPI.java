package com.worden.mcm;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.worden.dataprocess.DBMethod;

/**
 * 处理数据的Servlet，接受从UrlFilter转发的访问并返回结果
 */
@WebServlet("/McmAPI")
public class McmAPI extends HttpServlet {
	private static final long serialVersionUID = 1L;
	
	/**
     * @see HttpServlet#HttpServlet()
     */
    public McmAPI() {
        super();
        
    }


	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

		request.setCharacterEncoding("UTF-8");
		response.setCharacterEncoding("UTF-8");
		response.setContentType("application/json; charset=utf-8");
		response.setHeader("Access-Control-Allow-Origin", "*");
		
		PrintWriter out = response.getWriter();
		
		//HttpServletRequest req = (HttpServletRequest) request;
		String reqTableName = request.getParameter("reqTableName");
		String reqObjectID = request.getParameter("reqObjectID");
		String reqExist = request.getParameter("reqExist");
		String filterStr = request.getParameter("filter");
		
		DBMethod dbm = new DBMethod() ;
		
		if( reqObjectID !=null && reqObjectID.equalsIgnoreCase("count") ) { //统计表数据条数
			out.println( dbm.doGetTableCount(reqTableName, filterStr) );
		} else if ( reqObjectID !=null && reqObjectID.equalsIgnoreCase("TableFields") ) { //查询表字段列表
			out.println( dbm.doTableFields(reqTableName) );
		} else {
			out.println( dbm.doGetQuery(reqTableName, reqObjectID, reqExist, filterStr) );
		}
		
		out.flush();
		out.close();
	   
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
		String reqObjectID = request.getParameter("reqObjectID");
		String postContent = request.getParameter("postContent");
		String filterStr = request.getParameter("filter");

		
		DBMethod dbm = new DBMethod() ;
			
		if( reqObjectID !=null && reqObjectID.equalsIgnoreCase("CreateTable") ) { //建表
			out.println(dbm.doCreateTable(reqTableName, postContent)) ;
		} else if( reqObjectID !=null && reqObjectID.equalsIgnoreCase("DropTable") ){ //删表
			out.println(dbm.doDropTable(reqTableName)) ;
		} else if( reqObjectID !=null && reqObjectID.equalsIgnoreCase("AddField") ){ //增加字段
			out.println(dbm.doAddField(reqTableName, postContent)) ;
		} else if( reqObjectID !=null && reqObjectID.equalsIgnoreCase("DelField") ){ //删除字段
			out.println(dbm.doDelField(reqTableName, postContent)) ;
		} else { //处理一般POST数据
			out.println(dbm.doPostContent(reqTableName, reqObjectID, postContent, filterStr) );
		}
		
		out.flush();
		out.close();
		
	}

}
