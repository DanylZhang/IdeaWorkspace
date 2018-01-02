<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page language="java" import="java.util.*" pageEncoding="UTF-8" %>
<%@ include file="/WEB-INF/back_page/head.jsp" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
    <title>babasport-list</title>
    <script type="text/javascript">
        //全选
        function checkBox(name, checked) {
            $('input[name="' + name + '"]').attr('checked', checked);
        }

        //删除
        function optDelete(name, isDisplay, pageNo) {
            //判断 input checkbox checked count
            var size = $('input[name="ids"]:checked').size();
            if (size == 0) {
                alert("请至少选择一个");
                return;
            }
            if (!confirm('你确定删除吗？')) {
                return;
            }
            //jquery 提交 form 表单
            $('#jvForm').attr('action', "/control/brand/delete.html?name=" + name + "&isDisplay=" + isDisplay + "&pageNo=" + pageNo);
            $('#jvForm').submit()
        }
    </script>
</head>
<body>
<div class="box-positon">
    <div class="rpos">当前位置: 品牌管理 - 列表</div>
    <form class="ropt">
        <input class="add" type="button" value="添加" onclick="window.location.href='/control/brand/add.html'"/>
    </form>
    <div class="clear"></div>
</div>
<div class="body-box">
    <form action="/control/brand/list.html" method="post" style="padding-top:5px;">
        品牌名称: <input type="text" name="name" value="${name}"/>
        <select name="isDisplay">
            <option value="true" <c:if test="${isDisplay}">selected="selected"</c:if>>是</option>
            <option value="false" <c:if test="${!isDisplay}">selected="selected"</c:if>>否</option>
        </select>
        <input type="submit" class="query" value="查询"/>
    </form>

    <form id="jvForm" method="post">
        <table cellspacing="1" cellpadding="0" border="0" width="100%" class="pn-ltable">
            <thead class="pn-lthead">
            <tr>
                <th width="20"><input type="checkbox" onclick="checkBox('ids',this.checked)"/></th>
                <th>品牌ID</th>
                <th>品牌名称</th>
                <th>品牌图片</th>
                <th>品牌描述</th>
                <th>排序</th>
                <th>是否可用</th>
                <th>操作选项</th>
            </tr>
            </thead>
            <tbody class="pn-ltbody">

            <c:forEach items="${pagination.list}" var="brand">
                <tr bgcolor="#ffffff" onmouseout="this.bgColor='#ffffff'" onmouseover="this.bgColor='#eeeeee'">
                    <td><input type="checkbox" name="ids" value="${brand.id}"/></td>
                    <td align="center">${brand.id}</td>
                    <td align="center">${brand.name}</td>
                    <td align="center"><img width="40" height="40" src="${brand.allImgUrl}"/></td>
                    <td align="center">${brand.description}</td>
                    <td align="center">${brand.sort}</td>
                    <td align="center">
                        <c:if test="${brand.isDisplay}">是</c:if>
                        <c:if test="${!brand.isDisplay}">否</c:if>
                    </td>
                    <td align="center">
                        <a class="pn-opt" href="/control/brand/edit.html?id=${brand.id}">修改</a>
                        |
                        <a class="pn-opt"
                           onclick="javascript:$('input[name=\'ids\'][value=\'${brand.id}\']:checkbox').attr('checked','checked');optDelete('${name}',${isDisplay},${pagination.pageNo});"
                           href="#">删除</a>
                    </td>
                </tr>
            </c:forEach>

            </tbody>
        </table>
    </form>

    <div class="page pb15">
        <span class="r inb_a page_b">
            <c:forEach items="${pagination.pageView}" var="page">
                ${page}
            </c:forEach>
        </span>
    </div>
    <div style="margin-top:15px;">
        <input class="del-button" type="button" value="删除"
               onclick="optDelete('${name}',${isDisplay},${pagination.pageNo});"/></div>
</div>
</body>
</html>