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

    <h2>Manage Certificates</h2>
	
		<p style="padding:5px;border:1px solid #bbb;background-color:#fcfccf;"><strong>NOTE:</strong> Please select a DER encoded certificate. Private certificate files should be pkcs12 encoded files with no encryption on both the file and private key stored in the file.</p>
	
<c:choose>
	<c:when test='${empty action || action == "Add" }'>
	</c:when>
	<c:otherwise>
		
		<h4>Upload New Certificate</h4>
		
                <c:if test="${certerror == true}">
                    <p style="color:red;">Please upload only DER encoded certificates</p>
                </c:if>
		
		<div style="padding:0 10px;">
		
		<spring:url
			value="/config/certificates/addcertificate" var="formUrladdcertificate" />
		<form:form modelAttribute="certificateForm"
			action="${fn:escapeXml(formUrladdcertificate)}" cssClass="cleanform"
			method="POST" enctype="multipart/form-data">
			<form:hidden path="id" />
			<table id="certificateTable"  border="0" style="margin:10px;">
				<tr>
					<td width="100">
						<form:label for="fileData" path="fileData">Certificate:</form:label>
					</td>
					<td align=left>
						<form:input path="fileData" id="certificatefile" type="file"/>
					</td>
				</tr>
				<tr>
					<td><form:label path="status">Status:
											                <form:errors path="status" cssClass="error" />
					</form:label></td>
					<td><form:select path="status">
						<form:options items="${statusList}" />
					</form:select></td>
				</tr>
			</table>
			<button name="submitType" id="submitType" type="submit" value="newcertificate">Add Certificate</button>
			
		</form:form>

	</div>
		
	</c:otherwise>
	
	
	
</c:choose> <c:if test="${not empty certificatesResults}">

	<h4>Stored Certificates</h4>

	<spring:url
		value="/config/certificates/removecertifcates" var="formUrlcertificates" />
	<form:form modelAttribute="certificateForm"
		action="${fn:escapeXml(formUrlcertificates)}" cssClass="cleanform"
		method="POST">
		<form:hidden path="id" />
		<div id="tablelist" style="width:100%;overflow:auto;">
			<table  id="certificatesTable" class="data" style="font-size:10px">
				<thead>
					<tr>
						<th width="10"></th>
						<th width="200">Owner</th>
                        <th width="40">Private</th>
						<th width="">Thumb</th>
						<th width="" nowrap>Created</th>
						<th width="" nowrap>Start Date</th>
						<th width="" nowrap>End Date</th>
						<th width="">Status</th>
						
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
						<td><form:checkbox path="remove"
							value="${certificates.id}" /></td>
						
						<td>${certificates.owner}</td>
                                                        <td>
                                                            <c:choose>
                                                                <c:when test="${certificates.privateKey}">Yes</c:when>
                                                                <c:otherwise>No</c:otherwise>
                                                            </c:choose>
                                                        </td>
                                                        <td><c:out value="${certificates.thumbprint}" /></td>
                                                
                                                <td><fmt:formatDate
							value="${certificates.createTime.time}"
							pattern="MM/dd/yyyy, hh:mm" /></td>
						<td><fmt:formatDate
							value="${certificates.validStartDate.time}"
							pattern="MM/dd/yyyy, hh:mm" /></td>
						<td><fmt:formatDate
							value="${certificates.validEndDate.time}"
							pattern="MM/dd/yyyy, hh:mm" /></td>
						<td><c:out value="${certificates.status}" /></td>
						</tr>
					</c:forEach>
				</tbody>
			
			</table>
		</div>
		<!-- Wire this up to jQuery to add an input row to the table.
					                 Don't submit it all until the final submit is done -->
		<button name="submitType" id="submitType" type="submit"
			value="deletecertificate">Remove Selected</button>
	</form:form></fieldset>

</c:if>


<%@ include file="/WEB-INF/jsp/footer.jsp"%>