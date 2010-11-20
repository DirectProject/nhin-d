<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
	pageEncoding="ISO-8859-1"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<%@ include file="/WEB-INF/jsp/include.jsp"%>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title><fmt:message key="certs.title" /></title>
</head>
<body>
<%@ include file="/WEB-INF/jsp/header.jsp"%>
    <div id="form">
    <fieldset>
    <h3>NHIN Direct Java Reference Implementation - Manage Public and Private Certificates</h3>
    <form action="<c:url value="/j_spring_security_logout"/>">
           <button style="float:right;" name="logoutBtn" id="logoutBtn" type="submit">Log out</button></td>
       </form>
    </fieldset>
<c:choose>
	<c:when test='${empty action || action == "Add" }'>
	</c:when>
	<c:otherwise>
		<fieldset style="width: 99%;" title="certificategroup"><legend>Certificates:</legend>
		<fieldset style="width: 95%;" title="certificate"><spring:url
			value="/config/certificates/addcertificate" var="formUrladdcertificate" />
		<form:form modelAttribute="certificateForm"
			action="${fn:escapeXml(formUrladdcertificate)}" cssClass="cleanform"
			method="POST" enctype="multipart/form-data">
			<form:hidden path="id" />
			<table cellpadding="1px" cellspacing="1px" id="certificateTable">
				<tr>
					<th>
						<form:label for="fileData" path="fileData">Certificate:</form:label>
					</th>
					<th>
						<form:input path="fileData" id="certificatefile" type="file"/>
					</th>
				</tr>
				<tr>
					<th><form:label path="status">Status: 
											                <form:errors path="status" cssClass="error" />
					</form:label></th>
					<th><form:select path="status">
						<form:options items="${statusList}" />
					</form:select></th>
				</tr>
			</table>
			<button name="submitType" id="submitType" type="submit" value="newcertificate">Add Certificate</button>
			<button name="submitType" id="submitType" type="submit" value="cancel">Cancel</button>			
		</form:form></fieldset>
	</c:otherwise>
</c:choose> <c:if test="${not empty certificatesResults}">
	<fieldset style="width: 95%;" title="certificates"><spring:url
		value="/config/certificates/removecertifcates" var="formUrlcertificates" />
	<form:form modelAttribute="certificateForm"
		action="${fn:escapeXml(formUrlcertificates)}" cssClass="cleanform"
		method="POST">
		<form:hidden path="id" />
		<div id="tablelist" style="width:100%;overflow:auto;">
			<table cellpadding="1px" cellspacing="1px" id="certificatesTable"
				class="tablesorter">
				<thead>
					<tr>
						<th width="30%">Owner</th>
						<th width="15%">Thumb</th>
						<th width="15%">create Time</th>
						<th width="15%">Start Date</th>
						<th width="15%">End Date</th>
						<th width="7%">Stat</th>
						<th width="3%">Sel</th>
					</tr>
				</thead>
				<tbody>
					<!--  Put the data from the searchResults attribute here -->
					<c:forEach var="certificates" items="${certificatesResults}"
						varStatus="rowCounter">
						<c:choose>
							<c:when test="${rowCounter.count % 2 == 0}">
								<tr class="evenRow">
							</c:when>
							<c:otherwise>
								<tr class="oddRow">
							</c:otherwise>
						</c:choose>
						<td width="30%"><a
							href='../certificate?id=<c:out value="${certificates.id}"/>'>'${certificates.owner}'</a></td>
						<td width="15%"><c:out value="${certificates.thumbprint}" /></td>
						<td width="15%"><fmt:formatDate
							value="${certificates.createTime.time}"
							pattern="MM/dd/yyyy, hh:mm" /></td>
						<td width="15%"><fmt:formatDate
							value="${certificates.validStartDate.time}"
							pattern="MM/dd/yyyy, hh:mm" /></td>
						<td width="15%"><fmt:formatDate
							value="${certificates.validEndDate.time}"
							pattern="MM/dd/yyyy, hh:mm" /></td>
						<td width="7%"><c:out value="${certificates.status}" /></td>
						<td width="3%"><form:checkbox path="remove"
							value="${certificates.id}" /></td>
						</tr>
					</c:forEach>
				</tbody>
				<tfoot>
					<tr>
						<th width="30%"></th>
						<th width="15%"></th>
						<th width="15%"></th>
						<th width="15%"></th>
						<th width="15%"></th>
						<th width="7%"></th>
						<th width="3%"></th>
					</tr>
				</tfoot>
			</table>
		</div>
		<!-- Wire this up to jQuery to add an input row to the table.  
					                 Don't submit it all until the final submit is done -->
		<button name="submitType" id="submitType" type="submit"
			value="deletecertificate">Remove Selected</button>
	</form:form></fieldset>
	
</c:if>
</fieldset>
</body>
</html>