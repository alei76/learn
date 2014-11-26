<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8" isELIgnored="false"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>Insert title here</title>
<script type="text/javascript"
	src="<%=request.getContextPath()%>/jquery/jquery-1.4.4.js"></script>

</head>
<body>
	<form action="fileUpload" method="post" enctype="multipart/form-data">
	<input type="file" name="file">
	<input type="submit" value="upload">
	</form>
</body>
</html>