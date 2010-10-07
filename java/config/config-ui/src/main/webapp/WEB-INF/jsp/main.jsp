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
    <img src="/resources/images/logo.png">
	<center><h3>NHIN Direct Java Reference Implememtation - Manage Domains</h3></center>
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
	<!-- 	<button type="submit">Search</button> -->
		<button name="submitType" id="submitType" type="submit" value="search">Search</button>		
<!-- 		<button type="submit" onclick='this.form.action = "domain"; return true;' style="align:right;">New Domain</button> -->
        <button name="submitType" id="submitType" type="submit" value="newdomain">New Domain</button>
		
		</p>
	</form:form>
	</fieldset>
	</div>
	<c:if test="${not empty searchResults}">
	<div id="dynamic">
	   <form:form id="removeDomainForm" action="../domain/remove" cssClass="cleanform" method="POST" commandName="removeDomainForm" >
		<table cellpadding="1px" cellspacing="0" class="display" id="domainTable">
			<thead>
				<tr>
					<th width="30%">Name</th>
					<th width="35%">Postmaster</th>
					<th width="20%">Status</th>
					<th width="15%">Remove</th>
				</tr>
			</thead>
			<tbody>				
				<!--  Put the data from the searchResults attribute here -->
				<c:forEach var="domain" items="${searchResults}" varStatus="rowCounter">
				<c:choose>
					<c:when test="${rowCounter.count % 2 == 0}">
						<tr class="evenRow">
					</c:when>
					<c:otherwise>
						<tr class="oddRow">
					</c:otherwise>
				</c:choose>
				    <td width="30%"><a href='../domain/viewDomain?id=<c:out value="${domain.id}"/>'>'${domain.domainName}'</a></td>  
				    <td width="35%"><c:out value="${domain.postmasterAddressId}"/></td>
				    <td width="20%"><c:out value="${domain.status}"/></td>
				    <td width="15%">TBD</td>
				</tr>
				</c:forEach>	
			</tbody>
			<tfoot>
		        <tr>
		            <th width="30%"></th>
                    <th width="35%"></th>
                    <th width="20%"></th>
                    <th width="15%"></th>
		        </tr>
			</tfoot>
		</table>
		</form:form>
	</div>
	</c:if>
</body>
</html>