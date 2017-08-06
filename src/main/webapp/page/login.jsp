<%@ page language="java" import="java.util.*"
	contentType="text/html; charset=utf-8" pageEncoding="utf-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<c:set var="ctx" value="${pageContext.request.contextPath }"></c:set>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<!--登录页面  -->
<html>
<head>
<title>LoginPage</title>

<!--导入的样式和js脚本  -->
<link rel="stylesheet"
	href="${pageContext.request.contextPath}/jquery-easyui/themes/icon.css"></link>
<script type="text/javascript"
	src="${pageContext.request.contextPath}/jquery-easyui/jquery.min.js"></script>

<link rel="stylesheet"
	href="${pageContext.request.contextPath}/jquery-easyui/themes/default/easyui.css"></link>
<script type="text/javascript"
	src="${pageContext.request.contextPath}/jquery-easyui/jquery.easyui.min.js"></script>
<script type="text/javascript"
	src="${pageContext.request.contextPath}/jquery-easyui/locale/easyui-lang-zh_CN.js"></script>

<!-- 样式 -->
<style type="text/css">
a:link{color:#AA0000;text-decoration:none}
a:visited{color:#AA0000;text-decoration:none}

input[type="text"] {
	font-family: 'Open Sans', sans-serif;
	width: 40%;
	padding: 1em 1em 1em 1em;
	color: #3D3E6A;
	font-size: 14px;
	outline: none;
	background: #f5f6f7;
	border: none;
	font-weight: 100;
	margin-bottom: 1em;
	border: 1px solid #A8AEC5;
	border-radius: .4em;
	-webkit-border-radius: 0.4em;
	-o-border-radius: 0.4em;
	-moz-border-radius: 0.4em;
}

input[type="password"] {
	font-family: 'Open Sans', sans-serif;
	width: 40%;
	padding: 1em 1em 1em 1em;
	color: #3D3E6A;
	font-size: 14px;
	outline: none;
	background: #f5f6f7;
	border: none;
	margin-bottom: 1em;
	border: 1px solid #A8AEC5;
	border-radius: .4em;
	-webkit-border-radius: 0.4em;
	-o-border-radius: 0.4em;
	-moz-border-radius: 0.4em;
}

input[type="button"] {
	font-family: 'Open Sans', sans-serif;
	width:216px;
	height:40px;
	background:url("${pageContext.request.contextPath}/img/bg40.jpg") repeat-x left top;
	padding-bottom:2px;
	color:#FFF;
	position: absolute;
	top: 100%;
	right: 29%;
}

.login-form {
	width: 33%;
	height: 100%;
	margin: 0 auto;
	text-align: center;
	position: relative;
}

.copy-right {
	text-align: center;
	padding-top: 10em;
	color: #fff;
}
div#div1{ 
position:fixed; 
top:0; 
left:0; 
bottom:0; 
right:0; 
z-index:-1; 
} 
div#div1 > img { 
height:100%; 
width:100%; 
border:0; 
} 
div#div2{ 
position:absolute;
left:41%;
bottom:0;

} 


</style>

</head>

<!-- 登录用户界面 -->
<body class="easyui-layout" >
<!-- 登录界面背景图片 -->
<div id="div1"><img src="${pageContext.request.contextPath}/img/bg.jpg" /></div>
	<div style="height: 100px;">
		<center>
			<font color="white" >
				<h1>欢迎来到交易系统</h1></font>
				<div style="color:#fff;">
	<c:if test="${note!=null}">
	${note}
	</c:if>
	<div>
		</center>
	</div>
	
	
	<div ><center><div ><img src="../img/avtar.png" /><br/></div></center>
		<div class="login-form">
		<form action ="${ctx}/broker/login" method="POST">  
		<div class="form-text" >
				<input id="loginname" type="text" name="loginname" class="text" value = 'USERNAME'
						onfocus="this.value = '';"
						onblur="if (this.value == '') {this.value = 'loginname';}"><br/>  
				<input id="password" type="password" name="password" value = 'password'
						onfocus="this.value = '';"
						onblur="if (this.value == '') {this.value = 'password';}"> </div>
				<input id="btn" type="submit" value="登录">   
    </form>
	<!-- 新用户注册 
				<p>
					<font color="white" >新用户？<a href="${ctx}/page/jumpToRegister">点击这里进行申请!</a></font>
				</p>
	-->
		</div>
	</div>
	
	<!-- 底栏 -->
	<div id="div2" style="height: 20px;">
		<font color="white" >Copyright @ MyCompany. All rights reserved.</font>
	</div>
</body>
</html>
  