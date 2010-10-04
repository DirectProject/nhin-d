<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
	pageEncoding="ISO-8859-1"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
	<html>
	<head>
	<%@ include file="/WEB-INF/jsp/include.jsp"%>
	<META  http-equiv="Content-Type"  content="text/html;charset=UTF-8">
	<title><fmt:message key="welcome.title" /></title>
	</head>
	<body>
	<%@ include file="/WEB-INF/jsp/header.jsp" %>
	<div id="form">
	    <fieldset class="formInfo">
	       <h2>NHIN Direct Java Reference Implememtation - Login</h2>
	    </fieldset>
	    <fieldset>
			<form:form id="loginForm" action="login" cssClass="cleanform" commandName="loginForm" method="PUT">
			   <p>Please enter your userid and password to continue.</p>  
			   <form:label path="userid">Userid:</form:label> 
			   <form:input path="userid" /> <form:errors path="userid" cssClass="error" />
			   <form:label path="password">Password: 
			       
			   </form:label>
			   <form:password path="password"/><form:errors path="password" cssClass="error" />
			   <p>
			       <button type="submit">Submit</button>
			   </p>
			</form:form>
		</fieldset>
	</div>
<c:if test="${!ajaxRequest}">
	</body>
	</html>
</c:if>