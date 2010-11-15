<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
	pageEncoding="ISO-8859-1"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
    <head>
    <%@ include file="/WEB-INF/jsp/include.jsp"%>
    <META  http-equiv="Content-Type"  content="text/html;charset=UTF-8">
    <title><fmt:message key="settings.title" /></title>
    </head>
    <body>
    <%@ include file="/WEB-INF/jsp/header.jsp" %>
    <div id="form">
    <fieldset>
    <h3>NHIN Direct Java Reference Implementation - Manage Agent Settings</h3>
    <form action="<c:url value="/j_spring_security_logout"/>">
           <button style="float:right;" name="logoutBtn" id="logoutBtn" type="submit">Log out</button></td>
       </form>
    </fieldset>
<fieldset style="width: 100%;" title="Setting">
	<spring:url	value="/config/settings/addsetting" var="formUrladdsetting" /> 
	<form:form	modelAttribute="settingsForm" action="${fn:escapeXml(formUrladdsetting)}" cssClass="cleanform" method="POST">
	<table cellpadding="1px" cellspacing="1px" id="settingsTable">
		<tr>
			<th>
				<form:label path="key">Key:
					<form:errors path="key" cssClass="error" />
				</form:label>
			</th>
			<th><form:input path="key" /></th>
		</tr>
		<tr>
			<th><form:label path="value">Value:
							            <form:errors path="value" cssClass="error" />
			</form:label></th>
			<th><form:input path="value" /></th>
		</tr>
	</table>
	<button name="submitType" id="submitType" type="submit" value="newsetting">Add Setting</button>
	<button name="submitType" id="submitType" type="submit" value="cancel">Cancel</button>
</form:form></fieldset>
<c:if test="${not empty settingsResults}">
<fieldset style="width: 100%;" title="Settings">
	<spring:url value="/config/settings/removesettings" var="formUrlremove" /> 
		<form:form modelAttribute="simpleForm" action="${fn:escapeXml(formUrlremove)}" cssClass="cleanform" method="POST">
		<form:hidden path="id" />
		<table cellpadding="1px" cellspacing="1px" id="settingsTable"
			class="tablesorter">
			<thead>
				<tr>
					<th width="45%">name</th>
					<th width="45%">value</th>
					<th width="10%">remove</th>
				</tr>
			</thead>
			<tbody>
				<!--  Put the data from the searchResults attribute here -->
				<c:forEach var="setting" items="${settingsResults}"
					varStatus="rowCounter">
					<c:choose>
						<c:when test="${rowCounter.count % 2 == 0}">
							<tr class="evenRow">
						</c:when>
						<c:otherwise>
							<tr class="oddRow">
						</c:otherwise>
					</c:choose>
					<td width="45%"><a
						href='../setting?id=<c:out value="${setting.id}"/>'>'${setting.name}'</a></td>
					<td width="45%"><c:out value="${setting.value}" /></td>
					<td width="15%"><form:checkbox path="remove" value="${setting.name}" /></td>
					</tr>
				</c:forEach>
			</tbody>
			<tfoot>
				<tr>
					<th width="45%"></th>
					<th width="45%"></th>
					<th width="10%"></th>
				</tr>
			</tfoot>
		</table>
		<!-- Wire this up to jQuery to add an input row to the table.  
					                 Don't submit it all until the final submit is done -->
		<button name="submitType" id="submitType" type="submit" value="delete">Remove
		Selected</button>
	</form:form>
</fieldset>
</div>
</c:if>
</body>
</html>