<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
	pageEncoding="ISO-8859-1"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
    <head>
    <%@ include file="/WEB-INF/jsp/include.jsp"%>
    <META ?http-equiv="Content-Type" ?content="text/html;charset=UTF-8">
    <title><fmt:message key="settings.title" /></title>
    </head>
    <body>
    <%@ include file="/WEB-INF/jsp/header.jsp" %>



    <h2>Agent Settings</h2>

<fieldset style="width: 97%;" title="Setting">
	<spring:url	value="/config/settings/addsetting" var="formUrladdsetting" />
	<form:form	modelAttribute="settingsForm" action="${fn:escapeXml(formUrladdsetting)}" cssClass="cleanform" method="POST">
	<table cellpadding="1px" cellspacing="1px" id="settingsTable">
		<tr>
			<td width=50>
				<form:label path="key">Key:
					<form:errors path="key" cssClass="error" />
				</form:label>
			</td>
			<td><form:input path="key" cssStyle="width:300px" /></td>
		</tr>
		<tr>
			<td><form:label path="value">Value:
							            <form:errors path="value" cssClass="error" />
			</form:label></td>
			<td><form:input path="value"  cssStyle="width:300px"/></td>
		</tr>
	</table>
	<button name="submitType" id="submitType" type="submit" value="newsetting">Add Setting</button>
	<button name="submitType" id="submitType" type="submit" value="cancel">Cancel</button>
</form:form></fieldset>


<h4>Saved Settings</h4>

<c:choose>
	
	<c:when test="${not empty settingsResults}">

	<spring:url value="/config/settings/removesettings" var="formUrlremove" />
		<form:form modelAttribute="simpleForm" action="${fn:escapeXml(formUrlremove)}" cssClass="cleanform" method="POST">
		<form:hidden path="id" />
		<table id="settingsTable" class="data">
			<thead>
				<tr>
					<th width=20></th>
					<th width="400">Name</th>
					<th width="">Value</th>
					
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
					<td><form:checkbox path="remove" value="${setting.name}" /></td>
					<td ><!--<a
						href='../setting?id=<c:out value="${setting.id}"/>'>-->${setting.name}<!--</a>--></td>
					<td><c:out value="${setting.value}" /></td>
					
					</tr>
				</c:forEach>
			</tbody>
			
		</table>
		<!-- Wire this up to jQuery to add an input row to the table.
					                 Don't submit it all until the final submit is done -->
		<button name="submitType" id="submitType" type="submit" value="delete">Remove
		Selected</button>
	</form:form>

</div>
</c:when>

<c:otherwise>There are no settings yet.</c:otherwise>


</c:choose>

<br/><br/>

<%@ include file="/WEB-INF/jsp/footer.jsp"%>