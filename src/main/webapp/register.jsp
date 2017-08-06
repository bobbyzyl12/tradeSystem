<%@ page language="java" import="java.util.*"
	contentType="text/html; charset=utf-8" pageEncoding="utf-8"%>
	<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">

<!-- 注册页面 -->
<html>
<head>
<title>RegisterPage</title>
<c:if test="${note!=null}">
	${note}
	</c:if>
<!-- 引入的样式和js脚本 -->
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


<!-- 注册界面 -->
<body class="easyui-layout" >

<!-- 注册背景图片 -->
<div id="div1"><img src="${ctx}/MySystem/pictures/bg.jpg" /></div><br/><br/>
		<center>
			<font color="white" ><h1 color=#F00>申请新账户!</h1></font>
		</center>
<br/><br/>

		<div class="login-form">
		<!-- 填写信息区 -->
<form action ="${ctx}/MySystem/Trader/register" method="POST">  
				<div class="form-text">				
					<font color="white" size="5">真实名字：</font><input id="username" type="text" name="username" class="text" value = 'USERNAME'
						onfocus="this.value = '';"
						onblur="if (this.value == '') {this.value = 'USERNAME';}"><br/>
	
					<font color="white" size="5">密码：</font><input id="password" type="password" name="password" value="Password" onfocus="this.value = '';"
						onblur="if (this.value == '') {this.value = 'Password';}"><br/>
					<font color="white" size="5">再次输入密码：</font><input id="password2" type="password" name="password2" value="Password2" onfocus="this.value = '';"
						onblur="if (this.value == '') {this.value = 'Password2';}"><br/>			
					<font color="white" size="5">身份证：</font><input id="IDnumber" type="text" name="IDnumber" class="text" value="IDnumber"
						onfocus="this.value = '';"
						onblur="if (this.value == '') {this.value = 'IDnumber';}"><br/>
					
					
					<font color="white" size="5">登录名：</font><input id="loginname" type="text" name="loginname" class="text" value = 'loginname'
						onfocus="this.value = '';"
						onblur="if (this.value == '') {this.value = 'loginname';}"><br/>
				</div>
				<input id="bnt" type="submit" value="注册"></form>
			
			<!-- 返回登录 -->
			<p>
				<a href="${ctx}/MySystem/index.jsp"><font color="white" >返回登录页面!</font></a>
			</p>
		</div>
<!-- 底栏 -->
	<div id="div2" style="height: 20px;">
		<font color="white" >Copyright @ MyCompany. All rights reserved.</font>
	</div>
</body>
</html>