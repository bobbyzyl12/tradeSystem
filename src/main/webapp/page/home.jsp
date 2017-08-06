<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ page import="javax.servlet.*,javax.servlet.http.*"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<%
	String name = (String)session.getAttribute("name");
%>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/jquery.min.js"></script>
<html>
<head>
    <title>摩根大作业</title>
    <meta charset="utf-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0, maximum-scale=1.0">
    <meta name="keywords" content="admin, dashboard, bootstrap, template, flat, modern, theme, responsive, fluid, retina, backend, html5, css, css3">
    <meta name="description" content="">
    <meta name="author" content="ThemeBucket">
    <link rel="shortcut icon" href="#" type="image/png">

    <!--icheck-->
    <link href="${pageContext.request.contextPath}/AdminEx/js/iCheck/skins/minimal/minimal.css" rel="stylesheet">
    <link href="${pageContext.request.contextPath}/AdminEx/js/iCheck/skins/square/square.css" rel="stylesheet">
    <link href="${pageContext.request.contextPath}/AdminEx/js/iCheck/skins/square/red.css" rel="stylesheet">
    <link href="${pageContext.request.contextPath}/AdminEx/js/iCheck/skins/square/blue.css" rel="stylesheet">

    <!--dashboard calendar-->
    <link href="${pageContext.request.contextPath}/AdminEx/css/clndr.css" rel="stylesheet">

    <!--Morris Chart CSS -->
    <link rel="stylesheet" href="${pageContext.request.contextPath}/AdminEx/js/morris-chart/morris.css">

    <!--common-->
    <link href="${pageContext.request.contextPath}/AdminEx/css/style.css" rel="stylesheet">
    <link href="${pageContext.request.contextPath}/AdminEx/css/style-responsive.css" rel="stylesheet">

</head>
<body class="sticky-header" style="font-family:'楷体';">
<section>
    <!-- left side start-->
    <div class="left-side sticky-left-side">
        <div class="logo">
            <img src="${pageContext.request.contextPath}/AdminEx/images/temp.png" alt="">
        </div>
        <!--sidebar nav start-->
        <ul class="nav nav-pills nav-stacked custom-nav">
            <!-- li>
                <a href="user.jsp"><i class="fa fa-cogs"></i> <span>个人中心</span></a>
            </li-->
            <li>
                <a href="${ctx}/MySystem/BrokerCompany/FindAllCompany3"/><i class="fa fa-file-text"></i> <span>市场深度</span></a>
            </li>
            <li>
            	<a href="${ctx}/MySystem/BrokerCompany/FindAllCompany2"><i class="fa fa-bar-chart-o"></i> <span>下单</span></a>
            </li>

            <li>
            <a href="${ctx}/MySystem/Orders/viewOrder?status=0"><i class="fa fa-tasks"></i> <span>查看交易</span></a>
            </li>
            <li id="logo"><a href="${ctx}/MySystem/Trader/logOut"><i class="fa fa-sign-in"></i> <span>退出</span></a></li>
        </ul>
    </div>
    <div class="main-content">
        <div class="header-section">
            <a class="toggle-btn"><i class="fa fa-bars"></i></a>
            <div class="menu-right">
                <ul class="notification-menu">
                    <li>
                        <a href="#" class="btn btn-default dropdown-toggle" data-toggle="dropdown">
                            <!-- <img src="images/photos/user-avatar.png" alt="" /> -->
                             <%
							if (name != null) {
								out.print(name);
							}else{
								out.print("未登录,请返回主页");
							}
							%>
                            <span class="caret"></span>
                        </a>
                        <ul class="dropdown-menu dropdown-menu-usermenu pull-right">
                            <li id="shit"><a href="${ctx}/MySystem/Trader/logOut"><i class="fa fa-sign-out"></i>退出</a></li>
                        </ul>
                    </li>
                </ul>
            </div>
        </div>
    </div>
</section>

<!-- Placed js at the end of the document so the pages load faster -->
<script src="${pageContext.request.contextPath}/AdminEx/js/jquery-1.10.2.min.js"></script>
<script src="${pageContext.request.contextPath}/AdminEx/js/jquery-ui-1.9.2.custom.min.js"></script>
<script src="${pageContext.request.contextPath}/AdminEx/js/jquery-migrate-1.2.1.min.js"></script>
<script src="${pageContext.request.contextPath}/AdminEx/js/bootstrap.min.js"></script>
<script src="${pageContext.request.contextPath}/AdminEx/js/modernizr.min.js"></script>
<script src="${pageContext.request.contextPath}/AdminEx/js/jquery.nicescroll.js"></script>

<!--easy pie chart-->
<script src="${pageContext.request.contextPath}/AdminEx/js/easypiechart/jquery.easypiechart.js"></script>
<script src="${pageContext.request.contextPath}/AdminEx/js/easypiechart/easypiechart-init.js"></script>

<!--Sparkline Chart-->
<script src="${pageContext.request.contextPath}/AdminEx/js/sparkline/jquery.sparkline.js"></script>
<script src="${pageContext.request.contextPath}/AdminEx/js/sparkline/sparkline-init.js"></script>

<!--icheck -->
<script src="${pageContext.request.contextPath}/AdminEx/js/iCheck/jquery.icheck.js"></script>
<script src="${pageContext.request.contextPath}/AdminEx/js/icheck-init.js"></script>

<!-- jQuery Flot Chart-->
<script src="${pageContext.request.contextPath}/AdminEx/js/flot-chart/jquery.flot.js"></script>
<script src="${pageContext.request.contextPath}/AdminEx/js/flot-chart/jquery.flot.tooltip.js"></script>
<script src="${pageContext.request.contextPath}/AdminEx/js/flot-chart/jquery.flot.resize.js"></script>

<!--common scripts for all pages-->
<script src="${pageContext.request.contextPath}/AdminEx/js/scripts.js"></script>

</body>
</html>