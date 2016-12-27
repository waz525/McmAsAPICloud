<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<%
String path = request.getContextPath();
String basePath = request.getScheme()+"://"+request.getServerName()+":"+request.getServerPort()+path+"/";
//basePath="https://www.fhgc.trade:8477/mcm/";
//basePath="http://10.1.126.14:8080/mcm/";
%>

<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
  <head>
    <base href="<%=basePath%>">
    
    <title>TestShow</title>
     <meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
	<meta http-equiv="pragma" content="no-cache">
	<meta http-equiv="cache-control" content="no-cache">
	<meta http-equiv="expires" content="0">    
	<meta http-equiv="keywords" content="keyword1,keyword2,keyword3">
	<meta http-equiv="description" content="This is my page">
	<script type="text/javascript" src="./jquery.js"></script>
	<!--
	<link rel="stylesheet" type="text/css" href="styles.css">
	-->

  </head>
  
  <body>
    <p id="msg"></p><P> 
	<p id="tmsg"></p><P> 
    <input id="getinfo" type="button" name="getinfo" value="查询测试" >
    
    
    <p>
    <input id="addinfo" type="button" name="addinfo" value="增加对象" >
    <input id="queryinfo" type="button" name="queryinfo" value="查询对象" >
    <input id="modifyinfo" type="button" name="modifyinfo" value="修改对象" >
    <input id="delinfo" type="button" name="delinfo" value="删除对象" >
    <p>
    <input id="modifysameinfo" type="button" name="modifysameinfo" value="修改多个对象" >
    <input id="delsameinfo" type="button" name="delsameinfo" value="删除多个对象" >
    <p>
    <input id="existinfo" type="button" name="existinfo" value="对象是否存在" >
    <input id="countinfo" type="button" name="countinfo" value="统计对象数量" >
    <p>
    <input id="userlogin" type="button" name="userlogin" value="用户登录" >
    <input id="userchecklogin" type="button" name="userchecklogin" value="测试登录" >
    <input id="userlogout" type="button" name="userlogout" value="用户登出" >
    <p>
    <input id="createTable" type="button" name="createTable" value="新建表" >
    <input id="addField" type="button" name="addField" value="增加字段" >
    <input id="delField" type="button" name="delField" value="删除字段" >
    <input id="dropTable" type="button" name="dropTable" value="删除表" >
    <input id="tablefield" type="button" name="tablefield" value="查询表字段" >
  </body>
  
  <script type="text/javascript">
	$(document).ready(function(){
	
		$("#msg").text("这里将显示执行结果。。。");
		$("#tmsg").text("这里将显示执行耗时。。。");
		
	
		var ObjectID = null ;
	
		var token="eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJleHQiOjE0NzQ3NjQwNTUwMTUsInVpZCI6IngwODAxaG1oMGc5Mzk5Y2ZkdTY3b3ptaSIsImlhdCI6MTQ3NDE1OTI1NTAxNX0.c5CckJkNYc1-_7SP4pfh3Fs4lHUGSrzDM2dC1A-9v_c" ;
		
		//登录
		$.ajax({
			"url": "<%=basePath%>api/user/login",
			"method": "POST",
			"cache": false,
			"async":false,
			"data":{
				"username":"fhadmin" ,
				"password":"admin123"
				
			}
		}).success(function (data, status, header) {
		//success body	 
			token=data.id ; 
		}).fail(function (header, status, errorThrown) {
		//fail body
		})
		
		
	
  		$("#getinfo").click(function(){
			var date1=new Date();//开始时间 
  			
		  		var filter = {
		    		fields:["id", "username", "realname", "nextApproval_id", "mobile"],
		    		//fields:{"id": true,  "username": true, "realname": true, "nextApproval_id": true, "mobile": true },
		    		//"skip":1,
		    		"order": "username",
				    "limit": 400,
				  	"where" : {
				  		
				  		"mobile": {"gte":150}
				  	}
				    /*
		    		"where": { 
		    			"nextApproval_id": "A4016",
		    			"id": { "between" :[0,7]},
		    			"mobile": { "inq" :["150","151" ]} ,
		    			
		    			"or": [
		    				{"username":{"like":"X40"}},
		    				{"mobile":{"nlike":"150"}}
		    			],
		    			"or": [
		    				{"mobile": { "nin" :["150","151","152","153"]}},
		    				{"id": { "between" :[0,7]}}
		    			],
		    			"ids": { "between" :[11,12]}
		    		}
		    		*/
				}
			
			$.ajax({
				"url": "<%=basePath%>api/user?filter=" + encodeURIComponent(JSON.stringify(filter)),
				"method": "GET",
				"cache": false,
				"async":false,
				"headers": {
					"authorization":token
				}
			}).success(function (data, status, header) {
			//success body
				$("#msg").text("执行结果: "+JSON.stringify(data));
				
			}).fail(function (header, status, errorThrown) {
			//fail body
				//$("#msg").text("Error: "+JSON.stringify(header));
				$("#msg").empty();
				$("#msg").append(header.responseText);
			});
			var date2=new Date();//结束时间
			var date3=date2.getTime()-date1.getTime()  //时间差的毫秒数
			$("#tmsg").text("耗时: "+date3+" ms");
				
  		});	
  		
  		$("#addinfo").click(function(){	
			var date1=new Date();//开始时间 
  				$("#msg").empty();
  				
  					
				$.ajax({
					//"url": "http://localhost:8080/WebAccess/CharsetServlet",
					"url": "<%=basePath%>api/user",
					"method": "POST",
					"cache": false,
					"async":false,
					"headers": {
						"authorization":token
						
					},
					"data":{
						"username":"CS0001" ,
						"password":"admin123",
						"realname":"测试用户",
						"mobile":"15011112222"
					}
				}).success(function (data, status, header) {
				//success body	 
					$("#msg").text("执行结果: "+JSON.stringify(data));
					ObjectId=data.id;

				}).fail(function (header, status, errorThrown) {
				//fail body
					
					$("#msg").append(header.responseText) ; 
				})
				
			var date2=new Date();//结束时间
			var date3=date2.getTime()-date1.getTime()  //时间差的毫秒数
			$("#tmsg").text("耗时: "+date3+" ms");
			
		
  		});
  		
  		$("#modifyinfo").click(function(){
  			var date1=new Date();//开始时间 
  				$("#msg").empty();		
				$.ajax({
					"url": "<%=basePath%>api/user/"+ObjectId,
					"method": "POST",
					"cache": false,
					"async":false,
					"headers": {
						"authorization":token
						
					},
					"data":{
						"deviceid":"00001111",
						"_method":"PUT"
					}
				}).success(function (data, status, header) {
				//success body	 
					$("#msg").text("执行结果: "+JSON.stringify(data));
				}).fail(function (header, status, errorThrown) {
				//fail body
					
					$("#msg").append(header.responseText) ; 
				})
				
			var date2=new Date();//结束时间
			var date3=date2.getTime()-date1.getTime()  //时间差的毫秒数
			$("#tmsg").text("耗时: "+date3+" ms");
				
		
  		});
  		
  		
  		$("#queryinfo").click(function(){
  		
  			var date1=new Date();//开始时间 
  				
					var filter = {
				    	fields:{"id": true,  "username": true, "realname": true, "deviceid":true , "mobile": true, "createdAt": true  },
				    	//"sql":"select * from user",
				    	"order": "createdAt",
				    	"limit": 400,
				    	"where": {
					    		"realname": {"like": "测试"}
					    		
				    	}
					}
					
  				$("#msg").empty();
				$.ajax({
					"url": "<%=basePath%>api/user?filter=" + encodeURIComponent(JSON.stringify(filter)),
					"method": "GET",
					"cache": false,
					"async":false,
					"headers": {
						"authorization":token
						
					}
				}).success(function (data, status, header) {
				//success body	 
					$("#msg").text("执行结果: "+JSON.stringify(data));
					ObjectId=data[0].id;
					
				}).fail(function (header, status, errorThrown) {
				//fail body
					
					$("#msg").append(header.responseText) ; 
				})
				
			var date2=new Date();//结束时间
			var date3=date2.getTime()-date1.getTime()  //时间差的毫秒数
			$("#tmsg").text("耗时: "+date3+" ms");
				
		
  		});
  		
  		
  		
  		
  		$("#delinfo").click(function(){	
  			var date1=new Date();//开始时间 
  				$("#msg").empty();		
				$.ajax({
					"url": "<%=basePath%>api/user/"+ObjectId,
					"method": "POST",
					"cache": false,
					"async":false,
					"headers": {
						"authorization":token
						
					},
					"data":{
						"_method":"DELETE"
					}
				}).success(function (data, status, header) {
				//success body	 
					$("#msg").text("执行结果: "+JSON.stringify(data));
				}).fail(function (header, status, errorThrown) {
				//fail body
					
					$("#msg").append(header.responseText) ; 
				})
				
			var date2=new Date();//结束时间
			var date3=date2.getTime()-date1.getTime()  //时间差的毫秒数
			$("#tmsg").text("耗时: "+date3+" ms");
				
		
  		});
  		
  		
  		
  		$("#existinfo").click(function(){	
  			var date1=new Date();//开始时间 
  			$("#msg").empty();			
			$.ajax({
				"url": "<%=basePath%>api/user/"+ObjectId+"/exists",
				"method": "GET",
				"cache": false,
				"async":false,
				"headers": {
					"authorization":token
						
				}
			}).success(function (data, status, header) {
			//success body
				$("#msg").text("执行结果: "+JSON.stringify(data));
				
			}).fail(function (header, status, errorThrown) {
			//fail body
				//$("#msg").text("Error: "+JSON.stringify(header));
				$("#msg").empty();
				$("#msg").append(header.responseText);
			});
				
				
			var date2=new Date();//结束时间
			var date3=date2.getTime()-date1.getTime()  //时间差的毫秒数
			$("#tmsg").text("耗时: "+date3+" ms");
				
  		});	
  		
  		$("#countinfo").click(function(){	
  			var date1=new Date();//开始时间 
  			$("#msg").empty();			
			$.ajax({
				"url": "<%=basePath%>api/user/count",
				"method": "GET",
				"cache": false,
				"async":false,
				"headers": {
					"authorization":token
						
				}
			}).success(function (data, status, header) {
			//success body
				$("#msg").text("执行结果: "+JSON.stringify(data));
				
			}).fail(function (header, status, errorThrown) {
			//fail body
				//$("#msg").text("Error: "+JSON.stringify(header));
				$("#msg").empty();
				$("#msg").append(header.responseText);
			});
				
			var date2=new Date();//结束时间
			var date3=date2.getTime()-date1.getTime()  //时间差的毫秒数
			$("#tmsg").text("耗时: "+date3+" ms");
				
				
  		});	
  		
  		
  		
  		$("#modifysameinfo").click(function(){
  			var date1=new Date();//开始时间 
  				$("#msg").empty();	
  				
				var filter = {
				   	"where": {
				    		"realname": {"like": "测试"}
				   	}
				}	
				$.ajax({
					"url": "<%=basePath%>api/user?filter=" + encodeURIComponent(JSON.stringify(filter)),
					"method": "POST",
					"cache": false,
					"async":false,
					"headers": {
						"authorization":token
						
					},
					"data":{
						"deviceid":"00001111",
						"_method":"PUT"
					}
				}).success(function (data, status, header) {
				//success body	 
					$("#msg").text("执行结果: "+JSON.stringify(data));
				}).fail(function (header, status, errorThrown) {
				//fail body
					
					$("#msg").append(header.responseText) ; 
				})
				
			var date2=new Date();//结束时间
			var date3=date2.getTime()-date1.getTime()  //时间差的毫秒数
			$("#tmsg").text("耗时: "+date3+" ms");
				
		
  		});
  		
  		$("#delsameinfo").click(function(){
  			var date1=new Date();//开始时间 
  				$("#msg").empty();	
  				
				var filter = {
				   	"where": {
				    		"realname": {"like": "测试"}
				   	}
				}	
				$.ajax({
					"url": "<%=basePath%>api/user?filter=" + encodeURIComponent(JSON.stringify(filter)),
					"method": "POST",
					"cache": false,
					"async":false,
					"headers": {
						"authorization":token
						
					},
					"data":{
						"_method":"DELETE"
					}
				}).success(function (data, status, header) {
				//success body	 
					$("#msg").text("执行结果: "+JSON.stringify(data));
				}).fail(function (header, status, errorThrown) {
				//fail body
					
					$("#msg").append(header.responseText) ; 
				})
				
			var date2=new Date();//结束时间
			var date3=date2.getTime()-date1.getTime()  //时间差的毫秒数
			$("#tmsg").text("耗时: "+date3+" ms");
				
		
  		});
  		
  		$("#userlogin").click(function(){	
  			var date1=new Date();//开始时间 
  				$("#msg").empty();		
				$.ajax({
					"url": "<%=basePath%>api/user/login",
					"method": "POST",
					"cache": false,
					"async":false,
					"data":{
						"username":"fhadmin" ,
						"password":"admin123"
						
					}
				}).success(function (data, status, header) {
				//success body	 
					$("#msg").text("执行结果: "+JSON.stringify(data));
				}).fail(function (header, status, errorThrown) {
				//fail body
					
					$("#msg").append(header.responseText) ; 
				})
				
		
			var date2=new Date();//结束时间
			var date3=date2.getTime()-date1.getTime()  //时间差的毫秒数
			$("#tmsg").text("耗时: "+date3+" ms");
				
  		});
  		
  		
  		
  		$("#userchecklogin").click(function(){	
  			var date1=new Date();//开始时间 
  			$("#msg").empty();			
			$.ajax({
				"url": "<%=basePath%>api/user/checkLogin",
				"method": "GET",
				"cache": false,
				"async":false,
				"headers": {
					"authorization":token
						
				}
			}).success(function (data, status, header) {
			//success body
				$("#msg").text("执行结果: "+JSON.stringify(data));
				
			}).fail(function (header, status, errorThrown) {
			//fail body
				//$("#msg").text("Error: "+JSON.stringify(header));
				$("#msg").empty();
				$("#msg").append(header.responseText);
			});
				
			var date2=new Date();//结束时间
			var date3=date2.getTime()-date1.getTime()  //时间差的毫秒数
			$("#tmsg").text("耗时: "+date3+" ms");
				
				
  		});	
  		
  		
  		
  		$("#userlogout").click(function(){	
  			var date1=new Date();//开始时间 
  				$("#msg").empty();		
				$.ajax({
					"url": "<%=basePath%>api/user/logout",
					"method": "POST",
					"cache": false,
					"async":false,
					"headers": {
						"authorization":token
						
					}
				}).success(function (data, status, header) {
				//success body	 
					$("#msg").text("执行结果: "+JSON.stringify(data));
				}).fail(function (header, status, errorThrown) {
				//fail body
					
					$("#msg").append(header.responseText) ; 
				})
				
		
			var date2=new Date();//结束时间
			var date3=date2.getTime()-date1.getTime()  //时间差的毫秒数
			$("#tmsg").text("耗时: "+date3+" ms");
				
  		});
  		
  		$("#createTable").click(function(){	
  			var date1=new Date();//开始时间 
  				$("#msg").empty();		
				$.ajax({
					"url": "<%=basePath%>api/testtb/CreateTable",
					"method": "POST",
					"cache": false,
					"async":false,
					"headers": {
						"authorization":token
						
					},
					"data":{
						"userid":"varchar(30)",
						"x":"varchar(30)",
						"y":"varchar(30)",
					}
				}).success(function (data, status, header) {
				//success body	 
					$("#msg").text("执行结果: "+JSON.stringify(data));
				}).fail(function (header, status, errorThrown) {
				//fail body
					
					$("#msg").append(header.responseText) ; 
				})
				
		
			var date2=new Date();//结束时间
			var date3=date2.getTime()-date1.getTime()  //时间差的毫秒数
			$("#tmsg").text("耗时: "+date3+" ms");
				
  		});
  		
  		
  		$("#addField").click(function(){	
  			var date1=new Date();//开始时间 
  				$("#msg").empty();		
				$.ajax({
					"url": "<%=basePath%>api/testtb/AddField",
					"method": "POST",
					"cache": false,
					"async":false,
					"headers": {
						"authorization":token
						
					},
					"data":{
						"z":"varchar(30)"
					}
				}).success(function (data, status, header) {
				//success body	 
					$("#msg").text("执行结果: "+JSON.stringify(data));
				}).fail(function (header, status, errorThrown) {
				//fail body
					
					$("#msg").append(header.responseText) ; 
				})
			
			var date2=new Date();//结束时间
			var date3=date2.getTime()-date1.getTime()  //时间差的毫秒数
			$("#tmsg").text("耗时: "+date3+" ms");
					
		
  		});
  		
  		
  		$("#delField").click(function(){
  			var date1=new Date();//开始时间 	
  				$("#msg").empty();		
				$.ajax({
					"url": "<%=basePath%>api/testtb/DelField",
					"method": "POST",
					"cache": false,
					"async":false,
					"headers": {
						"authorization":token
						
					},
					"data":{
						
						"z":true
					}
				}).success(function (data, status, header) {
				//success body	 
					$("#msg").text("执行结果: "+JSON.stringify(data));
				}).fail(function (header, status, errorThrown) {
				//fail body
					
					$("#msg").append(header.responseText) ; 
				})
			
			var date2=new Date();//结束时间
			var date3=date2.getTime()-date1.getTime()  //时间差的毫秒数
			$("#tmsg").text("耗时: "+date3+" ms");
					
		
  		});
  		
  		$("#dropTable").click(function(){	
  			var date1=new Date();//开始时间 
  				$("#msg").empty();		
				$.ajax({
					"url": "<%=basePath%>api/testtb/DropTable",
					"method": "POST",
					"cache": false,
					"async":false,
					"headers": {
						"authorization":token
						
					},
					"data":{
						
						"force":true
					}
				}).success(function (data, status, header) {
				//success body	 
					$("#msg").text("执行结果: "+JSON.stringify(data));
				}).fail(function (header, status, errorThrown) {
				//fail body
					
					$("#msg").append(header.responseText) ; 
				})
				
		
			var date2=new Date();//结束时间
			var date3=date2.getTime()-date1.getTime()  //时间差的毫秒数
			$("#tmsg").text("耗时: "+date3+" ms");
				
  		});
  		
  		
  		
  		$("#tablefield").click(function(){
  			var date1=new Date();//开始时间 	
  				$("#msg").empty();		
				$.ajax({
					"url": "<%=basePath%>api/testtb/TableFields",
					"method": "GET",
					"cache": false,
					"async":false,
					"headers": {
						"authorization":token
						
					}
				}).success(function (data, status, header) {
				//success body	 
					$("#msg").text("执行结果: "+JSON.stringify(data));
				}).fail(function (header, status, errorThrown) {
				//fail body
					
					$("#msg").append(header.responseText) ; 
				})
				
		
			var date2=new Date();//结束时间
			var date3=date2.getTime()-date1.getTime()  //时间差的毫秒数
			$("#tmsg").text("耗时: "+date3+" ms");
				
  		});
  		
  		
  		
  	});
  				
	  	

  </script>
</html>
