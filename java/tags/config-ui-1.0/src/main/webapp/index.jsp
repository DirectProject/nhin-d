<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN"
   "http://www.w3.org/TR/html4/loose.dtd">
<html>
    <head>
<%@ include file="./WEB-INF/jsp/include.jsp" %>

		<%-- Redirected because we can't set the welcome page to a virtual URL. --%>
		<c:redirect url="config/main"/>

        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>NHIN Direct RI Java Configurtion UI - Welcome Page</title>
    </head>
    <body>	    
        <h1>Hello NHIN-Direct Administrator!</h1>
    </body>
</html>
