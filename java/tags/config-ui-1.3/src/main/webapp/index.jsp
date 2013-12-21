<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN"
   "http://www.w3.org/TR/html4/loose.dtd">
<html>
    <head>
<%@ include file="./WEB-INF/jsp/include.jsp" %>

	<%-- Redirected because we can't set the welcome page to a virtual URL. --%>
	<c:redirect url="config/main"/>

        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title><fmt:message key="project.name" /> RI Java Configuration UI</title>
    </head>
    <body>	    
        <h1>Hello <fmt:message key="project.name" /> Administrator!</h1>
    </body>
</html>
