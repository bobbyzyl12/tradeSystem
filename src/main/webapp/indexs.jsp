<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<c:set var="ctx" value="${pageContext.request.contextPath }"></c:set>

<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<script type="text/javascript" src="${ctx}/js/jquery.min.js"></script>
<link rel="stylesheet" href="//apps.bdimg.com/libs/jqueryui/1.10.4/css/jquery-ui.min.css">
<script src="//apps.bdimg.com/libs/jquery/1.10.2/jquery.min.js"></script>
<link rel="stylesheet" href="css/validationEngine.jquery.css">
<script src="//apps.bdimg.com/libs/jqueryui/1.10.4/jquery-ui.min.js"></script>
<link rel="stylesheet" href="jqueryui/style.css">
<title>测试首页</title>
<script type="text/javascript">
</script>
<style type="text/css">
	html, body {
		margin: 0;
			padding: 0;
			height: 100%;
		}

		html, body, div, input {
			box-sizing: border-box;
		}

		body {
			background-image: url(${ctx}/img/bg.jpg);
			background-size: cover;
		}

		body, h1, input {
			color: #fff;
			outline: 0 none;
		}

		
</style>

</head>
<body>
	<form action="${ctx}/page/jumpToHomePage" method="post">
			<input class="btn" type="submit" value="点击进入"/>
	</form>
	<form action="${ctx}/page/jumpToTestPage" method="post">
			<input class="btn" type="submit" value="测试页面"/>
	</form>
</body>
</html>