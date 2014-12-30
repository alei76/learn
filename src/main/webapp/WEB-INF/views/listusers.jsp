<%@ page language="java" contentType="text/html; charset=utf-8"
	pageEncoding="utf-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8">
<script type="text/javascript"
	src="${pageContext.request.contextPath }/resources/js/jquery-1.9.1.js"></script>
</head>
<body>
	<form action="/user/search">
		搜索关键词：<input name="keyword" type="text">
		<input name="start" type="hidden" value="0">
		<input name="pagesize" type="hidden" value="20">
		 <input type="submit" value="搜索" />
	</form>
	
	<c:if test="${queryResult.searchresultsize>0}" var="res">
		共查到[<b><font color='red'>${queryResult.searchresultsize}</font></b>]处结果!<br/>
		<table>
		<tbody>
		<c:forEach items="${queryResult.searchresult }" var="user">
			<tr>
			<td>登录帐号 :<c:out value="${user.loginEmail }" escapeXml="false"></c:out></td>
			<td>密码 :<c:out value="${user.password }" escapeXml="false"></c:out></td>
			<td>姓名 :<c:out value="${user.name }" escapeXml="false"></c:out></td>
			<td>手机号 :<c:out value="${user.phone }" escapeXml="false"></c:out></td>
			<td>身份证号:<c:out value="${user.identification }" escapeXml="false"></c:out></td>
			</tr>
		</c:forEach>
		</tbody>
		</table>
	</c:if>
	<c:if test="${queryResult.searchresultsize<=0}" >无此记录！</c:if>
</body>
</html>