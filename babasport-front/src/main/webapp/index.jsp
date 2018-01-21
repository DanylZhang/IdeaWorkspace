<%@ page language="java" import="java.util.*" pageEncoding="UTF-8" %>
<%@ page import="java.net.URLEncoder" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
    <title>新巴巴运动网-前台首页</title>
</head>
<body>
<%response.sendRedirect("/product/list.html?keyword=" + URLEncoder.encode("瑜伽服", "UTF-8"));%>
</body>
</html>