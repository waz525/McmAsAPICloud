package com.worden.mcm;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.worden.common.CommonUtil;
import com.worden.common.GsonUtil;
import com.worden.common.JWTUtil;
import com.worden.common.TokenState;

/**
 * Servlet Filter 用于解析url并进行访问转发  
 */
@WebFilter("/UrlFilter")
public class UrlFilter implements Filter {

    /**
     * Default constructor. 
     */
    public UrlFilter() {
        
    }

	/**
	 * @see Filter#destroy()
	 */
	public void destroy() {
		
	}

	/**
	 * @see Filter#doFilter(ServletRequest, ServletResponse, FilterChain)
	 */
	@SuppressWarnings("deprecation")
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {

		request.setCharacterEncoding("utf-8");
		response.setCharacterEncoding("utf-8");
		//resp.setHeader("Access-Control-Allow-Origin", "http://127.0.0.1/*");
		

		//获取URL信息
		HttpServletRequest req = (HttpServletRequest) request;
		String contextPath = req.getContextPath() ;
		String requestURI = req.getRequestURI();
		String pathInfo = requestURI.substring(requestURI.indexOf(contextPath)+contextPath.length());
		//将url里//替换成/
		while (pathInfo.indexOf("//") > -1 ) pathInfo.replaceAll("//", "/") ;

		
	    /////////////////////////////////////////////////////////////////////////
	    //处理options请求，用于跨域访问.直接返回204
	    if( req.getMethod().equalsIgnoreCase("options") ) {
	    	CommonUtil.PrintInfo("INFO: doFilter options , return HTTP 204 .");
	    	HttpServletResponse resp = (HttpServletResponse) response ;
	    	resp.setStatus(204);//返回HTTP代码204，指示请求成功了但是没有新信息返回。
	    	resp.setHeader("Access-Control-Allow-Origin", "*");  
	    	resp.setHeader("Access-Control-Allow-Methods", "GET,HEAD,PUT,PATCH,POST,DELETE");  
			resp.setHeader("Access-Control-Allow-Headers", "accept, content-type, x-apicloud-appid, x-apicloud-appkey, authorization");  
			return ;
	    }
	    /////////////////////////////////////////////////////////////////////////
	    

	    
		//StringBuilder对象用于组成新的URL
	    StringBuilder sbExpandUrl = new StringBuilder();

	    /////////////////////////////////////////////////////////////////////////
		if( pathInfo.endsWith("/user/login") ) {//处理用户登录，直接转给AuthService处理
			
			//访问/mcm/api/user/login的访问都将转给/AuthService处理
		    sbExpandUrl.append("/AuthService");
		    sbExpandUrl.append("?reqTableName=user");
		    sbExpandUrl.append("&reqMethod=login");
		    sbExpandUrl.append("&username="+request.getParameter("username"));
		    sbExpandUrl.append("&password="+request.getParameter("password"));
		    //CommonUtil.PrintInfo("UrlFilter - AuthService",sbExpandUrl.toString());
		    RequestDispatcher rdsp = request.getRequestDispatcher(sbExpandUrl.toString());
		    rdsp.forward(request, response);
		    
		    return ;
		    
		} else if( pathInfo.endsWith("/user/loginByThirdpart") ) {//第三方调用，认证
			
			//访问/mcm/api/user/loginByThirdpart的访问都将转给/AuthService处理
		    sbExpandUrl.append("/AuthService");
		    sbExpandUrl.append("?reqTableName=user");
		    sbExpandUrl.append("&reqMethod=loginByThirdpart");
		    RequestDispatcher rdsp = request.getRequestDispatcher(sbExpandUrl.toString());
		    rdsp.forward(request, response);
		    
		    return ;
			
		} else {//所有非用户登录，都必须检验token，
			
			//校验accessToken , 其在head里authorization
			String tokenStr=req.getHeader("authorization");
			Map<String, Object> resultMap=JWTUtil.validToken(tokenStr);			
			TokenState state=TokenState.getTokenState((String)resultMap.get("state"));
			if ( state != TokenState.VALID ) {
				HttpServletResponse resp = (HttpServletResponse) response ;
				resp.setCharacterEncoding("UTF-8");
				resp.setContentType("application/json; charset=utf-8"); 
				resp.setHeader("Access-Control-Allow-Origin", "*");
				PrintWriter out = resp.getWriter();
				out.println(GsonUtil.SimpleJsonString("ERROR","Invalid Auth Info"));
				out.flush();
				out.close();
				return ;
			}
			
			
			if(  pathInfo.endsWith("/user/logout") ) {// 用户退出，转由AuthService处理
				
				//访问/mcm/api/user/login的访问都将转给/AuthService处理
			    sbExpandUrl.append("/AuthService"); 
			    sbExpandUrl.append("?reqTableName=user");
			    sbExpandUrl.append("&reqMethod=logout");
			    sbExpandUrl.append("&id="+resultMap.get("id").toString());
			    RequestDispatcher rdsp = request.getRequestDispatcher(sbExpandUrl.toString());
			    rdsp.forward(request, response);
			    
			    return ;
				
			} else if (pathInfo.endsWith("/user/checkLogin")) {//验证token

				//访问/mcm/api/user/login的访问都将转给/AuthService处理
			    sbExpandUrl.append("/AuthService"); 
			    sbExpandUrl.append("?reqTableName=user");
			    sbExpandUrl.append("&reqMethod=checkLogin");
			    sbExpandUrl.append("&id="+resultMap.get("id").toString());
			    RequestDispatcher rdsp = request.getRequestDispatcher(sbExpandUrl.toString());
			    rdsp.forward(request, response);
			  
				return ;
			}
		}
	    /////////////////////////////////////////////////////////////////////////


	    //所有基于/mcm/api/*的访问都将转给/McmAPI处理
		//user/login 和 user/logout 由 /AuthService处理
	    sbExpandUrl.append("/McmAPI");
	    
	    //解析url，将表名、用户ID等出以参数方式传到下一个servlet
	    //形如 /api/user/57b5e3e85be606e34912e0c9 ; reqTableName 为 user ; reqObjectID 为 57b5e3e85be606e34912e0c9
	    String[] pInfos = pathInfo.split("/") ;
	    if(pInfos.length > 2 ) sbExpandUrl.append("?reqTableName="+pInfos[2]);
	    if(pInfos.length > 3 ) sbExpandUrl.append("&reqObjectID="+pInfos[3]); 
	    if(pInfos.length > 4 ) sbExpandUrl.append("&reqExist="+pInfos[4]); 

	    //获取参数（包括POST内容）
		Enumeration<String> paramValues = request.getParameterNames();
	    if( paramValues.hasMoreElements() ) {
	    	
	    	if( pInfos.length <= 2 ) {
	    		sbExpandUrl.append("?");
	    	} else {
	    		sbExpandUrl.append("&");
	    	}
	    	String filterValue = "" ;
	    	if( req.getMethod().equalsIgnoreCase("post") ) {//如果是POST，则将参数组合成json字符串以key为postContent的方式传递到下一个url
	    		Map<String,Object> post = new HashMap<String,Object> ();
	    		while (paramValues.hasMoreElements()) { 
			        String param = (String) paramValues.nextElement(); 
			        String value = request.getParameter(param);
			        if( param.equalsIgnoreCase("filter")) {
			        	filterValue =  java.net.URLEncoder.encode(value) ;
			        }else {
				        post.put(param, value) ;
			        }
	    		}
	    		
	    		sbExpandUrl.append("postContent=");
	    		/*
	    		//设置默认时间的Gson对象
	    		Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd HH:mm:ss").create();
	    		sbExpandUrl.append(java.net.URLEncoder.encode(gson.toJson(post)));
	    		*/
	    		sbExpandUrl.append(java.net.URLEncoder.encode(GsonUtil.Map2Json(post)));
	    		
	    		if( filterValue.length() > 0 ) {
	    			sbExpandUrl.append("&filter="+filterValue);  
	    		}
	    		
	    	} else { //是GET，则将所有参数组合成Query参数传递下去

		    	while (paramValues.hasMoreElements()) {  
			        String param = (String) paramValues.nextElement();  
			        String value = request.getParameter(param);  
			        sbExpandUrl.append(param);  
			        sbExpandUrl.append("=");
			        sbExpandUrl.append(java.net.URLEncoder.encode(value));  
			        if(paramValues.hasMoreElements()) sbExpandUrl.append("&");  
			    }
	    	}
	    }
	    
	    //CommonUtil.PrintInfo("UrlFilter - doFilter - sbExpandUrl : "+sbExpandUrl);

	    /////////////////////////////////////////////////////////////////////////
	    //将所有访问/api/*的请求都转向/McmAPI，的两种方式
	    //方式一： 此跳转会引起url改变
	    //resp.sendRedirect(sbExpandUrl.toString());
	    
	    //方式二：直接处理返回结果
	    RequestDispatcher rdsp = request.getRequestDispatcher(sbExpandUrl.toString());
	    rdsp.forward(request, response);
	    
	    //不再向下传递
		// pass the request along the filter chain
		//chain.doFilter(request, response);
	    /////////////////////////////////////////////////////////////////////////
	}

	/**
	 * @see Filter#init(FilterConfig)
	 */
	public void init(FilterConfig fConfig) throws ServletException {
		// TODO Auto-generated method stub
		
	}

}
