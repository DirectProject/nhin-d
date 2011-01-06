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
<div id="form">
    <%@ include file="/WEB-INF/jsp/header.jsp" %>
	<fieldset>
	<h3>NHIN Direct Java Reference Implementation - Manage Domains</h3>
	   <form action="<c:url value="/j_spring_security_logout"/>">
	       <button style="float:right;" name="logoutBtn" id="logoutBtn" type="submit">Log out</button></td>
	   </form>
	</fieldset>
	<fieldset>
	<spring:url value="/config/main/search" var="formUrl"/>
	<form:form id="searchDomainForm" action="${fn:escapeXml(formUrl)}" cssClass="cleanform" commandName="searchDomainForm" method="GET">
				<p>Enter the Domain search criteria below, or click the button 
			to add a new domain</p>
			<form:label path="domainName">Domain Name: 
		       <form:errors path="domainName" cssClass="error" />
			</form:label> 
			<form:input path="domainName" /> 
			
			<!-- TODO Align the buttons with the corresponding label -->
			<fieldset>
			 <form:radiobuttons path="status" items="${statusList}"/> 
	        </fieldset>
		<p>
		<button name="submitType" id="submitType" type="submit" value="search">Search</button>		
        <button name="submitType" id="submitType" type="submit" value="newdomain">New Domain</button>
		<button name="submitType" id="submitType" type="submit" value="gotosettings">Go To Settings</button>
		<button name="submitType" id="submitType" type="submit" value="gotocertificates">Go To Certificates</button>
		</p>
	</form:form>
	</fieldset>
	</div>
	<c:if test="${not empty searchResults}">
	<div id="dynamic">
	<script type="text/javascript">
	jQuery(document).ready(function(){
		jQuery("#domain-table").tablesorter(); 
	     });
    </script>
		<spring:url value="/config/domain/remove" var="formUrlremove"/>
	    <form:form name="removeForm" modelAttribute="simpleForm" action="${fn:escapeXml(formUrlremove)}" cssClass="cleanform" method="POST" >
		<table class="tablesorter" id="domain-table">
			<thead>
				<tr>
					<th>Name</th>
					<th>Postmaster</th>
					<th>Status</th>
					<th>Created</th>
					<th>Updated</th>
					<th>Remove</th>
				</tr>
			</thead>
			<tbody>				
				<!--  Put the data from the searchResults attribute here -->
				<c:forEach var="domain" items="${searchResults}" varStatus="rowCounter">
				<tr>
					<spring:url value="/config/domain?id=${domain.id}" var="formUrlclick"/>
				    <td><a href='${fn:escapeXml(formUrlclick)}'>${domain.domainName}</a></td>  
				    <td>${domain.postMasterEmail}</td>
				    <td>${domain.status}</td>
				    <td><fmt:formatDate value="${domain.createTime.time}" pattern="MM/dd/yyyy, hh:mm"/></td>
				    <td><fmt:formatDate value="${domain.updateTime.time}" pattern="MM/dd/yyyy, hh:mm"/></td>
				    <td><form:checkbox path="remove" value="${domain.id}" /></td>
				    
				</tr>
				</c:forEach>	
			</tbody>
			<tfoot>
		        <tr>
                    <th>Name</th>
                    <th>Postmaster</th>
                    <th>Status</th>
                    <th>Created</th>
                    <th>Updated</th>
                    <th>Remove</th>
		        </tr>
			</tfoot>
		</table>
		<button name="submitType" id="submitType" type="submit" value="delete">Delete</button>
		</form:form>
	</div>
	</c:if>
</body>
</html>