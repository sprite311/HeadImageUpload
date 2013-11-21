<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<%
String path = request.getContextPath();
String basePath = request.getScheme()+"://"+request.getServerName()+":"+request.getServerPort()+path+"/";
%>

<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
  <head>
    <base href="<%=basePath%>">
    
    <title>discuz图片上传</title>
	<meta http-equiv="pragma" content="no-cache">
	<meta http-equiv="cache-control" content="no-cache">
	<meta http-equiv="expires" content="0">    
	<meta http-equiv="keywords" content="keyword1,keyword2,keyword3">
	<meta http-equiv="description" content="This is my page">
	<!--
	<link rel="stylesheet" type="text/css" href="styles.css">
	-->
  </head>
  
  <body>
    <div style="float: right;width: 50%">
    大图:<img id="avatarpic_big" alt="大图"  width="150" height="150" src=""/> </br>
    中图:<img id="avatarpic_middle" alt="中图"  width="120" height="120" src=""/> </br>
    小图:<img id="avatarpic_small" alt="小图"  width="48" height="48" src=""/> 
    </div>
    <div style="float: left;width: 50%"><jsp:include page="/avatar.jhtml" flush="true"></jsp:include> </div>
  </body>
</html>
