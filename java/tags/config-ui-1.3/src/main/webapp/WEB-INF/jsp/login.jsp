<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
         pageEncoding="ISO-8859-1"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html style="background:#184B84;background-repeat: no-repeat;
    background-attachment: fixed;/* IE10 Consumer Preview */ 
background-image: -ms-linear-gradient(bottom, #184B84 0%, #366FAC 100%);

/* Mozilla Firefox */ 
background-image: -moz-linear-gradient(bottom, #184B84 0%, #366FAC 100%);

/* Opera */ 
background-image: -o-linear-gradient(bottom, #184B84 0%, #366FAC 100%);

/* Webkit (Safari/Chrome 10) */ 
background-image: -webkit-gradient(linear, left bottom, left top, color-stop(0, #184B84), color-stop(1, #366FAC));

/* Webkit (Chrome 11+) */ 
background-image: -webkit-linear-gradient(bottom, #184B84 0%, #366FAC 100%);

/* W3C Markup, IE10 Release Preview */ 
background-image: linear-gradient(to top, #184B84 0%, #366FAC 100%);">
    <head>
        <%@ include file="/WEB-INF/jsp/include.jsp"%>
        <META http-equiv="Content-Type" content="text/html;charset=UTF-8">
        <title><fmt:message key="welcome.title" /></title>
    </head>
    <body style="background:none;color:white;font-family: 'PT Sans',sans-serif;font-size: 16px;">

        <div style="text-align:center;width:325px;margin:0 auto;margin-top:50px;">
            <img src="<c:url value="/resources/images/direct-project-logo2.png" />" alt="Direct Project" border="0" />
            <br/>





        <fieldset class="formInfo">
            <h3>Configuration Login</h3>
        </fieldset>
        <c:if test="${param.login_error == 1}">
            <div class="error">
                <p class="error">Invalid username or password</p>
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
            <form:form id="loginForm" action="j_spring_security_check" method="PUT">

                <table border="0" align="center">
                    <tr>
                        <td width=75>Username: </td>
                        <td><input type="text" name="j_username"/></td>
                    </tr>
                    <tr>
                        <td width=75>Password: </td>
                        <td><input type="password" name="j_password"/></td>
                    </tr>
                    <tr>
                        <td colspan="2" align="right"><button type="submit">Login</button></td>
                    </tr>
                </table>

            </form:form>
        </fieldset>

        </div>

    </body>
</html>