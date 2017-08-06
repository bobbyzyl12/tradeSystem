<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ page import="javax.servlet.*,javax.servlet.http.*"%>
<c:set var="ctx" value="${pageContext.request.contextPath }"></c:set>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<%
	String name = (String)session.getAttribute("name");
%>
<meta charset="utf-8">
  <meta name="viewport" content="width=device-width, initial-scale=1">
  <link rel="stylesheet" href="http://apps.bdimg.com/libs/bootstrap/3.3.0/css/bootstrap.min.css">  
  <link href="${ctx}/css/page.css" rel="stylesheet" type="text/css" />
<link href="${ctx}/css/mybutton.css" rel="stylesheet" type="text/css" />
<title>search market depth</title>

<!--导入的样式和js脚本  -->
<script type="text/javascript" src="${ctx}/js/jquery.min.js"></script>
<script type="text/javascript" src="${ctx}/js/bootstrap.min.js"></script>
<script type="text/javascript" src="${ctx}/js/Chart.js"></script>
<link href="${ctx}/css/mybutton.css" rel="stylesheet">
<!--icheck-->
    <link href="${ctx}/AdminEx/js/iCheck/skins/minimal/minimal.css" rel="stylesheet">
    <link href="${ctx}/AdminEx/js/iCheck/skins/square/square.css" rel="stylesheet">
    <link href="${ctx}/AdminEx/js/iCheck/skins/square/red.css" rel="stylesheet">
    <link href="${ctx}/AdminEx/js/iCheck/skins/square/green.css" rel="stylesheet">
    <link href="${ctx}/AdminEx/js/iCheck/skins/minimal/minimal.css" rel="stylesheet">
    <link href="${ctx}/AdminEx/js/iCheck/skins/minimal/blue.css" rel="stylesheet">
    <link href="${ctx}/AdminEx/js/iCheck/skins/minimal/yellow.css" rel="stylesheet">
    <link href="${ctx}/AdminEx/js/iCheck/skins/minimal/purple.css" rel="stylesheet">
    
    <link href="${ctx}/AdminEx/css/clndr.css" rel="stylesheet">

    <!--Morris Chart CSS -->
    <link rel="stylesheet" href="${ctx}/AdminEx/js/morris-chart/morris.css">

    <!--common-->
    <link href="${ctx}/AdminEx/css/style.css" rel="stylesheet">
    <link href="${ctx}/AdminEx/css/style-responsive.css" rel="stylesheet">
    
    <!--common scripts for all pages-->
<!-- Placed js at the end of the document so the pages load faster -->
<script src="${ctx}/AdminEx/js/jquery-1.10.2.min.js"></script>
<script src="${ctx}/AdminEx/js/jquery-ui-1.9.2.custom.min.js"></script>
<script src="${ctx}/AdminEx/js/jquery-migrate-1.2.1.min.js"></script>
<script src="${ctx}/AdminEx/js/bootstrap.min.js"></script>
<script src="${ctx}/AdminEx/js/modernizr.min.js"></script>
<script src="${ctx}/AdminEx/js/jquery.nicescroll.js"></script>

<!--easy pie chart-->
<script src="${ctx}/AdminEx/js/easypiechart/jquery.easypiechart.js"></script>
<script src="${ctx}/AdminEx/js/easypiechart/easypiechart-init.js"></script>

<!--Sparkline Chart-->
<script src="${ctx}/AdminEx/js/sparkline/jquery.sparkline.js"></script>
<script src="${ctx}/AdminEx/js/sparkline/sparkline-init.js"></script>

<!--icheck -->
<script src="${ctx}/AdminEx/js/iCheck/jquery.icheck.js"></script>
<script src="${ctx}/AdminEx/js/icheck-init.js"></script>

<!-- jQuery Flot Chart-->
<script src="${ctx}/AdminEx/js/flot-chart/jquery.flot.js"></script>
<script src="${ctx}/AdminEx/js/flot-chart/jquery.flot.tooltip.js"></script>
<script src="${ctx}/AdminEx/js/flot-chart/jquery.flot.resize.js"></script>

<!--common scripts for all pages-->
</head>
<script type="text/javascript">
$(function(){
	var currentPage=Number($("#pageNo").text());
	var pageNum=Number($("#totalPage").text());
	
	$("#page_btn2").text(currentPage-2);
	$("#page_btn3").text(currentPage-1);
	$("#page_btn4").text(currentPage);
	$("#page_btn5").text(currentPage+1);
	$("#page_btn6").text(currentPage+2);
	$("#page_btn7").text(pageNum);
	
	//改变当前页的button样式
	$("#page_btn4").css("background-color","#65CEA7");
	$("#page_btn4").css("border","1px solid #ddd");
	$("#page_btn4").css("color","#fff");
	
	//先处理"上一页"和"下一页"的情况
	if(currentPage==1)	//如果当前页为首页
	{
		$("#prePage").hide();	
	}
	
	if(currentPage==pageNum)	//如果当前页为末页
	{
		$("#sufPage").hide();
	}
	
	//处理当前页小于等于3的特殊情况
	if(currentPage<=3){
		$("#prePoint").hide();
		$("#page_btn1").hide();
	}
	else if(currentPage==4){
		$("#prePoint").hide();
	}
	
	if(currentPage==1)
	{
		$("#page_btn2").hide();
		$("#page_btn3").hide();
	}
	else if(currentPage==2)
	{
		$("#page_btn2").hide();
	}
	
	if(currentPage>=pageNum-2){
		$("#sufPoint").hide();
		$("#page_btn7").hide();
	}
	else if(currentPage==pageNum-3){
		$("#sufPoint").hide();
	}
	
	if(currentPage==pageNum)
	{
		$("#page_btn5").hide();
		$("#page_btn6").hide();
	}
	
	if(currentPage==pageNum-1)
	{
		$("#page_btn6").hide();
	}
});
</script>
<body>
<section>
    <!-- left side start-->
    <div class="left-side sticky-left-side">
        <div class="logo">
            <img src="${ctx}/img/temp.png" alt="">
        </div>
        <ul class="nav nav-pills nav-stacked custom-nav">
            <li>
                <a href="${ctx}/page/jumpToMarketDepth"><i class="fa fa-file-text"></i> <span>市场深度</span></a>
            </li>
            <li>
                <a href="${ctx}/page/jumpToOrderSearch"><i class="fa fa-tasks"></i>所有订单</a>
            </li>
			<li>
                <a href="${ctx}/page/jumpToPendingOrderSearch"><i class="fa fa-tasks"></i>未完成订单</a>
            </li>
            <li>
                <a href="${ctx}/page/jumpToCompletedOrderSearch"><i class="fa fa-tasks"></i>已完成订单</a>
            </li>
            <li id="logo"><a href="${ctx}/broker/logOut"><i class="fa fa-sign-in"></i> <span>退出</span></a></li>
        </ul>
    </div>
    <div class="main-content" >
        <div class="header-section">
            <a class="toggle-btn"><i class="fa fa-bars"></i></a>
            	
            <div class="menu-right">
                <ul class="notification-menu">
                    <li>
                        <a href="#" class="btn btn-default dropdown-toggle" data-toggle="dropdown">
                            <%
							if (name != null) {
								out.print(name);
							}else{
								out.print("未登录,请返回主页");
							}
							%>
                            <span class="caret"></span>
                        </a>
                    </li>
                </ul>
            </div>    
    	</div>
	    <div class="wrapper">
	        <div class="row">
	        	<div class="col-md-12">     
	              	<div class="panel" data-collapsed="0">
                    	<div class="panel-body">
                        	<table class="display table table-bordered table-striped" style="text-align:center;">
								<thead>
									<tr>
										<th width=50px style="text-align:center;">订单ID</th>
				  						<th width=100px style="text-align:center;">订单发出公司名</th>
									  	<th width=80px style="text-align:center;">订单类型</th>
									  	<th width=50px style="text-align:center;">购买/出售</th>
									  	<th width=50px style="text-align:center;">产品编号</th>
									  	<th width=50px style="text-align:center;">设定数量</th>
									  	<th width=50px style="text-align:center;">完成数量</th>
									  	<th width=80px style="text-align:center;">当前状态</th>
									  	<th width=100px style="text-align:center;">开始时间</th>
									  	<th width=50px style="text-align:center;">定价</th>
									  	<th width=50px style="text-align:center;">触发价格</th>
									  	<th width=100px style="text-align:center;"></th>
										</tr>
									</thead>
										<tbody>
											<c:forEach items="${pageModel.datas}" var="order">
								            <tr>
								          		<td><a href="${ctx}/page/jumpToOrderDetail?orderId=${order.brokerOrderId}">${order.brokerOrderId}</a></td>
								  				<td>${order.traderCompanyName}</td>
								  				<td>
													<c:choose>
								    					<c:when test="${order.orderType == '0'}">
								       						<span>市价单</span>
								   						</c:when>
								    					<c:when test="${order.orderType=='1'}">
								       						<span>限价单</span>
								   						</c:when>
								   						<c:when test="${order.orderType=='1'}">
								       						<span>止损单</span>
								   						</c:when>
								   						<c:when test="${order.orderType=='1'}">
								       						<span>取消单</span>
								   						</c:when>
													</c:choose>
												</td>
								  				<td>
								  					<c:choose>
								    					<c:when test="${order.ifBuy == '0'}">
								       						<span>出售</span>
								   						</c:when>
								    					<c:when test="${order.ifBuy=='1'}">
								       						<span>购买</span>
								   						</c:when>
													</c:choose>
								  				</td>
								  				<td>${order.bProductId}</td>
								  				<td>${order.targetNumber}</td>
								  				<td>${order.completeNumber}</td>
								  				<td>
								  					<c:choose>
								    					<c:when test="${order.status == '0'}">
								       						<span style="color:#4d4dff;">未开始</span>
								   						</c:when>
								    					<c:when test="${order.status=='1'}">
								       						<span style="color:#00b300;">已开始</span>
								   						</c:when>
								   						<c:when test="${order.status=='2'}">
								       						<span style="color: #004d00;">已完成</span>
								   						</c:when>
								   						<c:when test="${order.status=='3'}">
								       						<span style="color: #aaa;">已取消</span>
								   						</c:when>
													</c:choose>
												</td>
								  				<td>${order.startTime}</td>
								  				<td>
								  					<c:choose>
								    					<c:when test="${order.orderType == '0'}">
								       						<span></span>
								   						</c:when>
								    					<c:when test="${order.orderType=='1'}">
								       						<span>${order.setPrice}</span>
								   						</c:when>
								   						<c:when test="${order.orderType=='1'}">
								       						<span>${order.setPrice}</span>
								   						</c:when>
								   						<c:when test="${order.orderType=='1'}">
								       						<span>${order.setPrice}</span>
								   						</c:when>
													</c:choose>
								  				</td>
								  				<td>${order.alarmPrice}</td>
								  				<td><a href="${ctx}/page/jumpToOrderDetail?orderId=${order.brokerOrderId}"><button class="detail-button">交易信息</button></a></td>
								         	</tr>
								         </c:forEach>
										</tbody>
									</table>
									<div class="pages" align="center">
								<a href="${ctx}/page/jumpToPendingOrderSearch?pageNo=${pageModel.pageNo-1}"><button class="page_btn" style="width:100px" id="prePage">上一页</button></a>

								<a href="${ctx}/page/jumpToPendingOrderSearch"><button class="page_btn" id="page_btn1">1</button></a>
							
								<span class="pages_span" id="prePoint">...</span>
							
								<a href="${ctx}/page/jumpToPendingOrderSearch?pageNo=${pageModel.pageNo-2}"><button class="page_btn" id="page_btn2"></button></a>
							
								<a href="${ctx}/page/jumpToPendingOrderSearch?pageNo=${pageModel.pageNo-1}"><button class="page_btn" id="page_btn3"></button></a>
							
								<a><button class="page_btn" id="page_btn4"></button></a>
							
								<a href="${ctx}/page/jumpToPendingOrderSearch?pageNo=${pageModel.pageNo+1}"><button class="page_btn" id="page_btn5"></button></a>
							
								<a href="${ctx}/page/jumpToPendingOrderSearch?pageNo=${pageModel.pageNo+2}"><button class="page_btn" id="page_btn6"></button></a>
							
								<span class="pages_span" id="sufPoint">...</span>
							
								<a href="${ctx}/page/jumpToPendingOrderSearch?pageNo=${pageModel.totalpage}"><button class="page_btn" id="page_btn7"></button></a>
							
								<a href="${ctx}/page/jumpToPendingOrderSearch?pageNo=${pageModel.pageNo+1}"><button class="page_btn" style="width:100px;" id="sufPage">下一页</button></a>
								<!-- 临时存放数据用 -->
								<p style="display:none" id="totalPage">${pageModel.totalpage}</p>
								<p style="display:none" id="pageNo">${pageModel.pageNo}</p>
					</div>
                    	</div>
                	</div>
	        </div>
        	</div>
        
        <!--footer section start-->
        <footer style="position:fixed;bottom:0;">
            2017 &copy; <a href="#" target="_blank"></a>
        </footer>
        <!--footer section end-->
        </div>
    </div>
</section>
	
</body>
</html>