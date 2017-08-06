<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<script type="text/javascript" src="${ctx}/js/jquery.min.js"></script>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8">
<title>Insert title here</title>
</head>
<body>
查看所有的brokerCompany信息
					<table id="tab">
  						<tr>
  							<th align=center style="vertical-align: middle;">公司id</th>
  							<th align=center style="vertical-align: middle;">公司名称</th>
		  					<th align=center style="vertical-align: middle;">公司地址</th>
		  					<th align=center style="vertical-align: middle;">公司电话</th>
		  					<th align=center style="vertical-align: middle;">公司IP</th>
		  					<th align=center style="vertical-align: middle;">公司状态</th>
  						</tr>
  		 
  						<c:forEach items="${brokerCompanyList}"  var="company">
     					<tr>
  							<td align=center>${company.brokerCompanyId}</td>
				  			<td align=center>${company.brokerCompanyName}</td>
				  			<td align=center>${company.brokerCompanyAddress}</td>
				  			<td align=center>${company.brokerCompanyPhoneNum}</td>
				  			<td align=center>${company.ipaddress}</td>
				  			<td align=center>
				  			<c:if test="${company.status==0}">
				  				正常
				  			</c:if>
				  			<c:if test="${company.status==1}">
				  				异常
				  			</c:if>
				  			</td>
				    	</c:forEach>
  					</table>
  					</div>
				<div align="center">
               					<a href="${pageContext.request.contextPath}/BrokerCompany/FindAllCompany">首页</a>
               					<c:if test="${pageModel.pageNo>1}"><a href="${ctx}/MySystem/BrokerCompany/FindAllCompany?pageNo=${pageModel.pageNo-1}">上一页</a></c:if>
               					<c:if test="${pageModel.pageNo<pageModel.totalpage}"><a href="${ctx}/MySystem/BrokerCompany/FindAllCompany?pageNo=${pageModel.pageNo+1}">下一页</a></c:if>
               					<c:if test="${pageModel.totalpage!=0}"><a href="${ctx}/MySystem/BrokerCompany/FindAllCompany?pageNo=${pageModel.totalpage}">尾页</a></c:if>
                                           	 总页数:${pageModel.totalpage}
                                          	  总数量:${pageModel.totalrecode}
                                          	  当前页:${pageModel.pageNo}
         		</div>
			
</body>
</html>