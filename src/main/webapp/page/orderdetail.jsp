<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ page import="javax.servlet.*,javax.servlet.http.*"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<script type="text/javascript" src="${pageContext.request.contextPath}/js/jquery.min.js"></script>
<script type="text/javascript">
$(function(){	
	 $("#btn").click(function()
	 {
		 $.ajax({                           	  
         	url: '${ctx}/MySystem/OrderDetail/addOrderDetail', 
         	type: 'POST',                  
        	data:{'orderDetailId':$("#orderDetailId").val(),
        		'traderOrderId':$("#traderOrderId").val(),
        		'brokerOrderId':$("#brokerOrderId").val(),
        		'otherSideBrokerOrderId':$("#otherSideBrokerOrderId").val(),
        		'amount':$('#amount').val(),
        		'price':$("#price").val()
        		},
        	success: function (msg) { 
         	alert(msg);
         	}
		 }); 
	 });
});
</script>
<title>Insert title here</title>
</head>
<body>
	<input type="text" id="orderDetailId" name="orderDetailId"  placeholder="orderDetail的ID"/>
	<br><input type="text" id="traderOrderId" name="traderOrderId"  placeholder="我的OrderId"/>
	<br><input type="text" id="brokerOrderId" name="brokerOrderId"  placeholder="broker的OrderId"/>
	<br><input type="text" id="otherSideBrokerOrderId" name="otherSideBrokerOrderId"  placeholder="别的trader的OrderId"/>
	<br><input type="text" id="amount" name="amount"  placeholder="成交数量"/>
	<br><input type="text" id="price" name="price"  placeholder="每个单价"/>
	<br><button id="btn" type="button">增加OrderDetail</button>
</body>
</html>