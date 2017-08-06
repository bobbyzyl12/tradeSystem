<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ page import="javax.servlet.*,javax.servlet.http.*"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<%
	String name = (String)session.getAttribute("name");
%>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/jquery.min.js"></script>
<script type="text/javascript">
//$(function(){
	//$(document).ready(function(){
		function fuc(){
		//$("#findorder").on("change",function(){
			var select=$("#findorder").val();
			//alert("select	"+select);
			var ice=0;
			var che=document.getElementsByName("ice");
			if (che[0].checked) ice=che[0].value;
			 //alert("ice	"+ice);
			var status=Number(select)+Number(ice);
			//alert(status);
            var str="${ctx}/MySystem/Orders/viewOrder?status="+status;
            window.location.href = str;
		}
		//});
$(function(){
	$(document).ready(function(){	
		$(".submit_button2").click(function(){
			var select=$("#findorder").val();
			//alert("select	"+select);
			var ice=0;
			var che=document.getElementsByName("ice");
			if (che[0].checked) ice=che[0].value;
			 //alert("ice	"+ice);
			var status=Number(select)+Number(ice);
			alert($(this).prev().val());
			var traderOrderId=$(this).prev().val();
			$.ajax({                           	  
		   		url: '${ctx}/MySystem/Orders/cancelOrder', 
		        type: 'POST',                  
		       data:{'traderorderid':traderOrderId},
		       success: function (msg) { 
		       	alert(msg);
		       	var str="${ctx}/MySystem/Orders/viewOrder?status="+status;
		       	window.location.href = str;
		       }
			});
		});
		$(".submit_button").click(function(){
			alert($(this).prev().val());
		var traderOrderId=$(this).prev().val();
		 var div = document.getElementById("showorderdetail");
		div.style.visibility="visible";
		var tr = document.getElementById("tab2");
		var rowscount = tr.rows.length; 
		  
		//循环删除行,从最后一行往前删除 
		for(i=rowscount - 1;i > 0; i--){ 
		  tr.deleteRow(i); 
		} 
		
		var select=$("#findorder").val();
		//alert("select	"+select);
		var ice=0;
		var che=document.getElementsByName("ice");
		 if (che[0].checked) ice=che[0].value;
		 //alert("ice	"+ice);
		 var status=Number(select)+Number(ice);
		alert(status);
		//删光tr
		if(status<4)
		{
			$.ajax({                           	  
		   		url: '${ctx}/MySystem/OrderDetail/findOrderDetail', 
		        type: 'POST',                  
		       data:{'traderOrderId':traderOrderId},
		       dataType:"json",
		       success: function (OrderDetailList) { 
		       		//alert(OrderDetailList);
		       		var str = JSON.stringify(OrderDetailList);
		       		alert(str);
		       		if(str=="") return;
		       		//objSelect.options.length=0;
		       		if(OrderDetailList!=null)
		       		{	
		       			var productjson=eval('('+str+')');
		       			alert("长度为："+productjson.length);
		       			for (var i=0;i<productjson.length;i++)
		       			{
		       				var trcomp="<tr>"+
		       				"<td align=center>"+productjson[i].amount+"</td>"+
		       				"<td align=center>"+productjson[i].price+"</td>"+
		       				"<td align=center>"+productjson[i].time+"</td>"+
		       				"</tr>";
		       				$("#tab2 tr:last-child").after(trcomp);
		       				//var temp=productjson[i].tProductId+";"+productjson[i].productName;
		        			//alert(temp)
		        			
		        			//objSelect.options.add(new Option(productjson[i].productName,temp));
		       			}
		    		}
		         }
			});
		}
		else
		{                 
            var str2="${ctx}/MySystem/Orders/viewIce?orderid="+traderOrderId;
            window.open(str2);            
		}
		});
   });
});
</script>
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
    <link href="${pageContext.request.contextPath}/AdminEx/js/iCheck/skins/minimal/red.css" rel="stylesheet">
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

	<link href="${pageContext.request.contextPath}/AdminEx/js/advanced-datatable/css/demo_page.css" rel="stylesheet" />
  	<link href="${pageContext.request.contextPath}/AdminEx/js/advanced-datatable/css/demo_table.css" rel="stylesheet" />
  	<link rel="stylesheet" href="${pageContext.request.contextPath}/AdminEx/js/data-tables/DT_bootstrap.css" />

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
        
        <div class="wrapper">
            <div class="row">
                 <div class="col-md-12">

                 	<div class="panel" data-collapsed="0">

                    	<div class="panel-heading">
                        	<div class="panel-title">
                                <strong>查看订单</strong>
                            </div>
                        </div>
	                        <div class="col-sm-9 icheck">
								<div class="minimal-red">
									<div class="checkbox">
	         							<input type="checkbox" id="ice" name="ice" value=4
	         								<c:if test="${status>3}">
	         									checked
	         								</c:if>
	          								<c:if test="${status<4}">
	          									unchecked
	          								</c:if>
	         							>冰山单
	    							</div>
									<div class="col-md-7 form-group">
	                         			<select type="text" id="findorder" name="findorder" class="form-control input-lg m-bot15" style="font-family:'楷体';">
											<option value="0"
												<c:if test="${status==0||status==4}">
													selected
												</c:if>
											>未交易订单</option>
											<option value="1"
												<c:if test="${status==1||status==5}">
													selected
												</c:if>
											>部分完成订单</option>
											<option value="2"
												<c:if test="${status==2||status==6}">
													selected
												</c:if>
											>已完成订单</option>
											<option value="3"
												<c:if test="${status==3||status==7}">
													selected
												</c:if>
											>已取消订单</option>
										</select>	
	                         		</div>
	                        	 </div>
	                        	 <div class="col-md-6 form-group">
	                        	 <button id="temp" name="temp" class="btn btn-primary btn-lg" onclick="javascript:fuc();">
	                        	 查询</button>
	                    </div> 
	                    </div>
	                    
                        <div class="panel-body">
                        	<div class="adv-table">
        						<table  class="display table table-bordered table-striped" id="tab">
        							<thead>
        								<tr>
            								<th>订单号</th>
            								<th>交易公司</th>
            								<th>类型</th>
            								<th>产品</th>
            								<th>买/卖</th>
            								<th>预期量</th>
            								<th>完成量</th>
            								<th>开始</th>
            								<c:if test="${status==2||status==3||status==6||status==7}">
            									<th>结束</th>
            								</c:if>
            								<th>预定金额</th>
            								<th>警告线</th>
            								<c:if test="${status==1||status==0||status==5||status==4}">
            									<th>取消订单</th>
            								</c:if>
            								<c:if test="${status!=0}">
            									<th>查看详情</th>
            								</c:if>
        								</tr>
        							</thead>
        							<tbody>
        							<c:forEach items="${order}" var="order">
        								<tr class="gradeX" id="tr"> 
            								<td>${order.traderOrderId}</td>
            								<td>${order.brokerCompanyName}</td>
            								<td>
            									<c:choose>
				  									<c:when test="${order.orderType == 0}">
				  										市价单
				  									</c:when>
				  									<c:when test="${order.orderType == 1}">
				  										限价单
				  									</c:when>
				  									<c:when test="${order.orderType == 2}">
				  										止损单
				  									</c:when>
				  									<c:otherwise>
				  										已取消
    												</c:otherwise>
				  								</c:choose>
            								</td>
            								<td>${order.productName}</td>
            								<td>
            									<c:if test="${order.ifBuy==0}">
				  									卖出
				  								</c:if>
				  								<c:if test="${order.ifBuy==1}">
				  									买入
            									</c:if>
            								</td>
            								<td>${order.targetNumber}</td>
            								<td>${order.completeNumber}</td>
            								<td>${order.startTime}</td>
            								<c:if test="${status==3||status==2||status==7||status==6}">
            									<td>${order.completeTime}</td>
            								</c:if>
            								<td>${order.setPrice}</td>
            								<td>
            									<c:if test="${order.orderType==2}">
            										${order.alarmPrice}
            									</c:if>
            								</td>
            								<c:if test="${status==0||status==1||status==4||status==5}">
            									<td>
            										<input type='hidden' id="temp" name='traderOrderId' value='${order.traderOrderId}'/>
                                   					<input type="button" id="button" class="submit_button2" value="取消"/>
            									</td>
            								</c:if>
            								<c:if test="${status!=0}">
            									<td>
            										<input type='hidden' id="traderOrderId" name='traderOrderId' value='${order.traderOrderId}'/>
                                   					<input type="button" id="button" class="submit_button" value="详情"/>	
            									</td>
            								</c:if>
        								</tr>
        							</c:forEach>
        						</tbody>
        					</table>
        				</div>
                    
	                    <div align="center">
	               			<a href="${ctx}/MySystem/Orders/viewOrder?status=${status}">首页</a>
	               			<c:if test="${pageModel.pageNo>1}"><a href="${ctx}/MySystem/Orders/viewOrder?status=${status}&pageNo=${pageModel.pageNo-1}">上一页</a></c:if>
	               			<c:if test="${pageModel.pageNo<pageModel.totalpage}"><a href="${ctx}/MySystem/Orders/viewOrder?status=${status}&pageNo=${pageModel.pageNo+1}">下一页</a></c:if>
	               			<c:if test="${pageModel.totalpage!=0}"><a href="${ctx}/MySystem/Orders/viewOrder?status=${status}&pageNo=${pageModel.totalpage}">尾页</a></c:if>
	                                    		  总页数:${pageModel.totalpage}
	                                          	  总数量:${pageModel.totalrecode}
	                                          	  当前页:${pageModel.pageNo}
	                    </div>
	                    
	                    <div id='showorderdetail' style="visibility: hidden;">
	         				<table id="tab2">
	  							<tr>
	  								<th align=center style="vertical-align: middle;">数量</th>
	  								<th align=center style="vertical-align: middle;">价格</th>
	  								<th align=center style="vertical-align: middle;">时间</th>
	         					<tr>
	         					<tr id="tr2">
	  							</tr>
	  						</table>
	         			</div>
                    
                    </div>
                </div>
             </div>
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
<script type="${pageContext.request.contextPath}/AdminEx/text/javascript" language="javascript" src="${pageContext.request.contextPath}/AdminEx/js/advanced-datatable/js/jquery.dataTables.js"></script>
<script type="${pageContext.request.contextPath}/AdminEx/text/javascript" src="${pageContext.request.contextPath}/AdminEx/js/data-tables/DT_bootstrap.js"></script>
<!-- jQuery Flot Chart-->
<script src="${pageContext.request.contextPath}/AdminEx/js/flot-chart/jquery.flot.js"></script>
<script src="${pageContext.request.contextPath}/AdminEx/js/flot-chart/jquery.flot.tooltip.js"></script>
<script src="${pageContext.request.contextPath}/AdminEx/js/flot-chart/jquery.flot.resize.js"></script>

<!--common scripts for all pages-->
<script src="${pageContext.request.contextPath}/AdminEx/js/scripts.js"></script>

</body>
</html>