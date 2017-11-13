<%--
  Created by IntelliJ IDEA.
  User: DELL
  Date: 2017/10/17
  Time: 11:17
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>文件上传</title>
</head>
<body>
<fieldset>
    <legend>文件上传</legend>
    <form action="/upload" method="post" enctype="multipart/form-data">
        <input type="file" name="multipartFile"><br>
        <input type="submit" value="提交">
    </form>
</fieldset>
</body>
</html>