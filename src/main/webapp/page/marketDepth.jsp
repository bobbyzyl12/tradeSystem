<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ page import="javax.servlet.*,javax.servlet.http.*"%>
<c:set var="ctx" value="${pageContext.request.contextPath }"></c:set>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta charset="utf-8">
<meta name="viewport" content="width=device-width, initial-scale=1">
<meta name="keywords" content="admin, dashboard, bootstrap, template, flat, modern, theme, responsive, fluid, retina, backend, html5, css, css3">
<meta name="description" content="">
<meta name="author" content="ThemeBucket">
<link rel="shortcut icon" href="#" type="image/png">
<%
	String name = (String)session.getAttribute("name");
%>
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
<script src="${ctx}/AdminEx/js/scripts.js"></script>

<title>search market depth</title>

<script type="text/javascript">
function uniqueArray(data){  
	   data = data || [];  
	   var a = {};  
	   for (var i=0; i<data.length; i++) {  
	       var v = data[i];  
	       if (typeof(a[v]) == 'undefined'){  
	            a[v] = 1;  
	       }  
	   };  
	   data.length=0;  
	   for (var i in a){  
	        data[data.length] = i;  
	   }  
	   return data;  
	}  

$(function(){
	$("#searchMarketDepthButton").click(function(){
		var productID = $("#productID").val();
		$.ajax({                           	  
			url: '${ctx}/marketDepth/searchMarketDepth',       //处理测试页面                 
			type: 'POST',                  
			data: {productID:productID},                
			success: function (msg){
				var div = document.getElementById("ChartArea");
				if(msg==""){
					div.style.visibility="hidden";
				}
				else{
					div.style.visibility="visible";

					var arr = msg;
				
					var ChartLabels = new Array();
					for(var i in arr) {
						ChartLabels.push(arr[i].price);
					}
					uniqueArray(ChartLabels);
					for(var i in ChartLabels) {
						ChartLabels[i] = parseInt(ChartLabels[i]);
					}
					
					var data1 = new Array();
					var data2 = new Array();
					var backgroundColor1 = new Array();
					var backgroundColor2 = new Array();
					var broderColor1 = new Array();
					var broderColor2 = new Array();
					
					for(var i in ChartLabels){
						data1.push(parseInt(0));
						data2.push(parseInt(0));
						backgroundColor1.push('rgba(255, 99, 132, 0.2)');
						backgroundColor2.push('rgba(54, 162, 235, 0.2)');
						broderColor1.push('rgba(255,99,132,1)');
						broderColor2.push('rgba(54, 162, 235, 1)');
					}
					for(var i in arr) {
						if(arr[i].ifBuy==1){
							for(var j in ChartLabels){
								if(arr[i].price == ChartLabels[j]){
									data1[j] = data1[j]+arr[i].quantity;
								}
							}
						}
						else if(arr[i].ifBuy==0){
							for(var j in ChartLabels){
								if(arr[i].price == ChartLabels[j]){
									data2[j] = data2[j]+arr[i].quantity;	
								}
							}
						}
					}
					var ctx = document.getElementById("myChart").getContext('2d');
					var myChart = new Chart(ctx, {
					    type: 'bar',
					    data: {
					        labels: ChartLabels,
					        datasets: [{
					            label: 'Buy',
					            data: data1,
					            backgroundColor: backgroundColor1,
					            borderColor: broderColor1,
					            borderWidth: 1
					        },
					        {
					            label: 'Sell',
					            data: data2,
					            backgroundColor: backgroundColor2,
					            borderColor: broderColor2,
					            borderWidth: 1
					        },
					        ]
					    },
					    options: {
					        scales: {
					            yAxes: [{
					                ticks: {
					                    beginAtZero:true
					                }
					            }]
					        }
					    }
					});
				}
			}            
		});             
	});
});
</script>
</head>
<body>
<section>
    <!-- left side start-->
    <div class="left-side sticky-left-side">
        <div class="logo">
            <img src="${ctx}/img/temp.png" alt="">
        </div>
        <ul class="nav nav-pills nav-stacked custom-nav">
            <li>
                <a href="#"><i class="fa fa-file-text"></i> <span>市场深度</span></a>
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
                        <div class="row">
                          <input type="text" id="productID" style="display:inline;margin:10px;width:80%;" class="form-control" placeholder="product id" />
        					<button style="display:inline;margin:10px 10px 10px 30px;position:relative;"id="searchMarketDepthButton" class="search-button">搜索</button>
                        </div>
                    </div>
                </div>
	        	<div class="col-md-12">
	              <div class="panel" data-collapsed="0">
                    <div class="panel-body">
                        <div class="row">
                          <div style="width:800px;height:400px;text-align:center;margin-left:auto;margin-right:auto;" id="ChartArea" style="visibility: hidden;">
								<canvas id="myChart" width="800px" height="400px" ></canvas>
							</div>
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