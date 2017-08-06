<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ page import="javax.servlet.*,javax.servlet.http.*"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<%
	String name = (String)session.getAttribute("name");
%>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/jquery.min.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/jquery.min.js"></script>
<script type="text/javascript">
$(function(){
	$("#product").on("change",function(){
		$("#product option[value='0']").remove();
		var objSelect = document.getElementById("ordertype");
		objSelect.options.length=0;
		objSelect.options.add(new Option('请选择交易类型',3));
		$.ajax({                           	  
            url: '${ctx}/MySystem/MarketDepth/findMarketDepth',                  
            type: 'POST',                  
            data: {'brokerdetail':$("#brokercompany").val(),
        			'productdetail':$("#product").val()
            	}, 
            success: function (msg) 
            {  
            	//alert(msg);
            	if(msg=="true")
            	{
            		objSelect.options.add(new Option('市价单',0));
            		objSelect.options.add(new Option('限价单',1));
            		objSelect.options.add(new Option('止损单',2));
            	}
            	else
            	{
            		objSelect.options.add(new Option('限价单',1));
            		objSelect.options.add(new Option('止损单',2));
            	}
            }
		}); 
	}); 
	$("#ordertype").on("change",function(){
		$("#ordertype option[value='3']").remove();
		var ordertype=$("#ordertype").val();
		var temp = document.getElementById("alarmprice");
		var temp2 = document.getElementById("hehe");
		temp.style.visibility="hidden";
		temp2.style.visibility="hidden";
		//alert(ordertype);
		if(ordertype==2)
		{
			//alert("止损单");
			
			temp.style.visibility="visible";
		}
		else if(ordertype==1)
		{
			temp2.style.visibility="visible";
		
		}
	 }); 
	$("#brokercompany").on("change",function(){
		$("#brokercompany option[value='0']").remove();
		var companyid=$("#brokercompany").val();
		var objSelect = document.getElementById("product");
		if (companyid!= null) 
		{
			$.ajax({                           	  
                url: '${ctx}/MySystem/Product/productDetail',                  
                type: 'POST',                  
                data: {brokerdetail:companyid}, 
                dataType:"json",
                success: function (productList) 
                {  
                	//alert("productList="+productList);
                	var str = JSON.stringify(productList);
                	//alert(str);
                	objSelect.options.length=0;
                	if(productList!=null)
                	{
                		var productjson=eval('('+str+')');
                		objSelect.options.add(new Option('请选择交易产品',0));
                		for (var i=0;i<productjson.length;i++)
                		{
                			var temp=productjson[i].tProductId+";"+productjson[i].productName;
                			//alert(temp)
                			objSelect.options.add(new Option(productjson[i].productName,temp));
                		}
                	}
                 }
             });          
		}
		else
			objSelect.options.length=0;
	});
	
	 $("#btn").click(function()
	 {
		 var ack=0;
		 var iceberg=0;
		 var iceamount=0;
		 var che=document.getElementsByName("ack");
		 if (che[1].checked) ack=che[1].value;
		 if(ack==0)
		{
			alert("请确认下单");
			return;
		}
		 if(che[0].checked) 
		{
			 iceberg=che[0].value;
			 var iceamount=0;
			 iceamount=$("#iceamount").val();
			 alert(iceamount);
			 if(iceamount==0||Number(iceamount)>Number($("#targetnumber").val()))
			 {
				 alert("冰山单每个小单必须为小于总数的正整数");
				 return;
			 }
			 var alarm=$("#alarmprice").val();
			 //alert(alarm);
		}
		 $.ajax({                           	  
         	url: '${ctx}/MySystem/Orders/addOrder', 
         	type: 'POST',                  
        	data:{'brokerdetail':$("#brokercompany").val(),
        		'productdetail':$("#product").val(),
        		'ordertype':$("#ordertype").val(),
        		'ifbuy':$('input:radio:checked').val(),
        		'targetnumber':$("#targetnumber").val(),
        		'setprice':$("#setprice").val(),
        		'alarmprice':$("#alarmprice").val(),
        		'iceberg':iceberg,
        		'iceamount':iceamount
        		},
        	success: function (msg) { 
         		alert(msg);
        		window.location.href = "${ctx}/MySystem/BrokerCompany/FindAllCompany2";
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
    <link href="${pageContext.request.contextPath}/AdminEx/js/iCheck/skins/minimal/minimal.css" rel="stylesheet">
    <link href="${pageContext.request.contextPath}/AdminEx/js/iCheck/skins/square/square.css" rel="stylesheet">
    <link href="${pageContext.request.contextPath}/AdminEx/js/iCheck/skins/square/red.css" rel="stylesheet">
    <link href="${pageContext.request.contextPath}/AdminEx/js/iCheck/skins/square/blue.css" rel="stylesheet">
	<link href="${pageContext.request.contextPath}/AdminEx/js/iCheck/skins/minimal/minimal.css" rel="stylesheet">
    <link href="${pageContext.request.contextPath}/AdminEx/js/iCheck/skins/square/square.css" rel="stylesheet">
    <link href="${pageContext.request.contextPath}/AdminEx/js/iCheck/skins/square/red.css" rel="stylesheet">
    <link href="${pageContext.request.contextPath}/AdminEx/js/iCheck/skins/square/green.css" rel="stylesheet">
    <link href="${pageContext.request.contextPath}/AdminEx/js/iCheck/skins/minimal/minimal.css" rel="stylesheet">
    <link href="${pageContext.request.contextPath}/AdminEx/js/iCheck/skins/minimal/red.css" rel="stylesheet">
    <link href="${pageContext.request.contextPath}/AdminEx/js/iCheck/skins/minimal/yellow.css" rel="stylesheet">
    <link href="${pageContext.request.contextPath}/AdminEx/js/iCheck/skins/minimal/purple.css" rel="stylesheet">

    <link href="${pageContext.request.contextPath}/AdminEx/js/iCheck/skins/square/square.css" rel="stylesheet">
    <link href="${pageContext.request.contextPath}/AdminEx/js/iCheck/skins/square/red.css" rel="stylesheet">
    <link href="${pageContext.request.contextPath}/AdminEx/js/iCheck/skins/square/green.css" rel="stylesheet">
    <link href="${pageContext.request.contextPath}/AdminEx/js/iCheck/skins/square/blue.css" rel="stylesheet">
    <link href="${pageContext.request.contextPath}/AdminEx/js/iCheck/skins/square/yellow.css" rel="stylesheet">
    <link href="${pageContext.request.contextPath}/AdminEx/js/iCheck/skins/square/purple.css" rel="stylesheet">

    <link href="${pageContext.request.contextPath}/AdminEx/js/iCheck/skins/flat/grey.css" rel="stylesheet">
    <link href="${pageContext.request.contextPath}/AdminEx/js/iCheck/skins/flat/red.css" rel="stylesheet">
    <link href="${pageContext.request.contextPath}/AdminEx/js/iCheck/skins/flat/green.css" rel="stylesheet">
    <link href="${pageContext.request.contextPath}/AdminEx/js/iCheck/skins/flat/blue.css" rel="stylesheet">
    <link href="${pageContext.request.contextPath}/AdminEx/js/iCheck/skins/flat/yellow.css" rel="stylesheet">
    <link href="${pageContext.request.contextPath}/AdminEx/js/iCheck/skins/flat/purple.css" rel="stylesheet">
	
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
        <div class="wrapper">
            <div class="row">
                 <div class="col-md-12">
                 	<div class="panel" data-collapsed="0">

                    	<div class="panel-heading">
                        	<div class="panel-title">
                                <strong>下单</strong>
                            </div>
                        </div>

                        <div class="panel-body">
                        	<div class="col-md-4 form-group">
                            	<select type="text" id="brokercompany" name="brokercompany" placeholder="选择交易公司" class="form-control input-lg m-bot15" style="font-family:'楷体';">
									<option value="0" selected>请选择交易公司</option>
									<c:forEach items="${brokerCompanyList}"  var="company"> 
										<option value="${company.brokerCompanyId};${company.brokerCompanyName}">${company.brokerCompanyName}</option>
									</c:forEach>
								</select>
							</div>
                            
                            <div class="col-md-4 form-group">
                            	<select type="text" id="product" name="product" placeholder="选择交易产品" class="form-control input-lg m-bot15" style="font-family:'楷体';">
									<option value="0" selected>请选择交易产品</option>
								</select>
                            </div>
                            
                            <div class="col-md-4 form-group">
                            	<select type="text" id="ordertype" name="ordertype" placeholder="选择交易类型" class="form-control input-lg m-bot15" style="font-family:'楷体';">
									<option value="3" selected>请选择交易类型</option>
								</select>
                            </div>        
                            
                            <div class="col-md-4 form-group">
								<div class="col-sm-9 icheck">
									<div class="square-red">
                            			<label>买入</label>
                   						<input type="radio" name="radiobutton" value=1 checked="checked">
                    				</div>
                   					<div class="square-green">
                          				<label>卖出</label>
                   						<input type="radio" name="radiobutton" value=0>
              						</div>
              					</div>
              				</div>

							<div class="col-md-4 form-group">
								<input type="text" id="targetnumber" name="targetnumber"  placeholder="购买数量" class="form-control input-lg m-bot15" style="font-family:'楷体';"/>
							</div>
							
							<div class="col-md-4 form-group">
								<input type="text" id="setprice" name="setprice"  placeholder="设定价格" class="form-control input-lg m-bot15" style="font-family:'楷体';"/>
							</div>
							
							<div class="col-md-4 form-group">
								<input type="text" id="alarmprice" name="alarmprice" style="visibility: hidden;font-family:'楷体';" placeholder="止损单的警戒线价格" class="form-control input-lg m-bot15" />
							</div>
							
							<div id="hehe" style="visibility: hidden;">
							<div class="col-sm-9 icheck" > 
							    <div class="minimal-yellow">
							    	<div class="checkbox">
         								<input type="checkbox" id="ack" name="ack" value=1 unchecked >
                       						冰山单
                       				</div>
                       			</div>
                       			<div class="col-md-4 form-group">
         						<input type="text" id="iceamount" name="iceamount" placeholder="冰山订单数量" class="form-control input-lg m-bot15" style="font-family:'楷体';"/>
    						</div>
                       		</div>
                       		</div>
                       		
                       		
    						
    						<div class="col-sm-9 icheck">
                                <div class="minimal-red">
									<div class="checkbox">
         								<input type="checkbox" id="ack" name="ack" value=1 unchecked>
                       						确认下单
    								</div>
    							</div>
    						</div>
							
							<div class="col-md-12 form-group">
                            	<button id="btn" type="button" class="btn btn-primary btn-lg">下单</button>
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

<!-- jQuery Flot Chart-->
<script src="${pageContext.request.contextPath}/AdminEx/js/flot-chart/jquery.flot.js"></script>
<script src="${pageContext.request.contextPath}/AdminEx/js/flot-chart/jquery.flot.tooltip.js"></script>
<script src="${pageContext.request.contextPath}/AdminEx/js/flot-chart/jquery.flot.resize.js"></script>

<!--common scripts for all pages-->
<script src="${pageContext.request.contextPath}/AdminEx/js/scripts.js"></script>

</body>
</html>