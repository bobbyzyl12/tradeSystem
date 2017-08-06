<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN"
    "http://www.w3.org/TR/html4/loose.dtd">
<c:set var="ctx" value="${pageContext.request.contextPath }"></c:set>

<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>Trader Gateway</title>
    </head>
    <body>
        <div style="text-align: center">
            <h1>Trader Gateway</h1>
            <form action="${ctx}/MySystem/test/Test_Send" method="post">
                <input type="submit" value="Test"/>
            </form>
        </div>
    </body>
</html>
