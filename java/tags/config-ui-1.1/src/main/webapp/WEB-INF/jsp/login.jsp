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
	      <h3>NHIN Direct Java Reference Implementation - Login</h3>
	    </fieldset>
	    <c:if test="${param.login_error == 1}">
		    <div class="error">
		       <p class="error">Invalid user name or password supplied</p>
		    </div>
	    </c:if>
	    <!-- 
	    <c:if test="${((param.logout == 1) && (param.login_error != 1))}">
	        <div class="info">
	           <p>Successfully logged out</p>
	        </div>
        </c:if>
          -->
	    <fieldset>
			<form:form id="loginForm" action="j_spring_security_check" cssClass="cleanform" method="PUT">
			   <p>Please enter your userid and password to continue.</p>  
			   <table border="0">
			     <tr>
			         <td align="right">Userid:</td>
			         <td><input type="text" name="j_username"/></td>
			     </tr>
			     <tr>
			         <td align="right">Password:</td>
			         <td><input type="password" name="j_password"/></td>
			     </tr>
			   </table>
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
