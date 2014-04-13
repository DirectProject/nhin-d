<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
	pageEncoding="ISO-8859-1"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<%@ include file="/WEB-INF/jsp/include.jsp"%>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title><fmt:message key="domain.title" /></title>
<script type="text/javascript" charset="utf-8">
	$(document).ready(function() {
		$("#addressTable").tablesorter();
		$("#anchorsTable").tablesorter();
		$("#dnsTable").tablesorter();
	});
</script>
</head>
<body>
<%@ include file="/WEB-INF/jsp/header.jsp"%>
<div id="form">
<fieldset>
<h3>NHIN Direct Java Reference Implementation - Manage Domains</h3>
<form action="<c:url value="/j_spring_security_logout"/>">
<button style="float: right;" name="logoutBtn" id="logoutBtn"
	type="submit">Log out</button>
</td>
</form>
</fieldset>
<spring:url value="/config/domain/saveupdate" var="formUrl" /> <form:form
	id="domainForm" action="${fn:escapeXml(formUrl)}" cssClass="cleanform"
	commandName="domainForm" method="POST">
	<div class=error"><form:errors path="*" cssClass="error" /></div>
	<c:if test='${not empty msg && not empty msg["msg"]}'>
		<div class="error"><fmt:message key='${msg["msg"]}' /></div>
	</c:if>
	<form:hidden path="id" />
	<form:hidden path="postmasterEmailAddressId" />
	<table>
		<tr>
			<td><form:label path="domainName">Domain Name: 
                        <form:errors path="domainName" cssClass="error" />
			</form:label></td>
			<td><form:input path="domainName" /></td>
		</tr>
		<c:choose>
			<c:when test='${empty action || action == "Add" }'>
				<tr>
					<td><form:label path="postmasterEmail">Postmaster E-Mail Address:
                        <form:errors path="postmasterEmail"
							cssClass="error" />
					</form:label></td>
					<td><form:input path="postmasterEmail" /></td>
				</tr>
			</c:when>
			<c:otherwise>
			</c:otherwise>
		</c:choose>
		<tr>
			<td><form:label path="status">Status: 
                    <form:errors path="status" cssClass="error" />
			</form:label></td>
			<td><form:select path="status">
				<form:options items="${statusList}" />
			</form:select></td>
		</tr>
	</table>
	<p><c:choose>
		<c:when test='${empty action || action == "Add" }'>
			<button name="submitType" id="submitType" type="submit" value="add">Add</button>
		</c:when>
		<c:otherwise>
			<button name="submitType" id="submitType" type="submit"
				value="update">Update</button>
		</c:otherwise>
	</c:choose>
	<button name="submitType" id="submitType" type="submit" value="cancel">Cancel</button>
	</p>
</form:form></div>
 <c:if test='${not empty action && action != "Add" }'>
    <!-- Display tabs -->
    <div id="tabs" class="tabs">
		<ul>
			<li><a href="#tabs-1">Addresses</a></li>
			<li><a href="#tabs-2">Anchors</a></li>
			<li><a href="#tabs-3">DNS Entries</a></li>
		</ul>
		<div id="tabs-1">
		  <fieldset style="width: 90%;" title="AddressesGroup"><legend>Addresses</legend>
		      <fieldset style="width: 90%;" title="Address"><spring:url
			     value="/config/domain/addaddress" var="formUrladdaddress" /> <form:form
			     modelAttribute="addressForm"
				action="${fn:escapeXml(formUrladdaddress)}" cssClass="cleanform"
				method="POST">
			     <form:hidden path="id" />
				    <table cellpadding="1px" cellspacing="1px" id="addressTable">
					   <tr>
						  <th><form:label path="displayName">Display name:
	                                        <form:errors path="displayName"
								cssClass="error" />
						  </form:label></th>
						  <th><form:input path="displayName" /></th>
						  <td>&nbsp;</td>
					   </tr>
					   <tr>
							<th><form:label path="emailAddress">E-Mail Address:
		                                        <form:errors path="emailAddress"
									cssClass="error" />
							</form:label></th>
							<th><form:input path="emailAddress" /></th>
							<td>&nbsp;</td>
						</tr>
						<tr>
							<th><form:label path="endpoint">Endpoint:
		                                        <form:errors path="endpoint"
									cssClass="error" />
							</form:label></th>
							<th><form:input path="endpoint" /></th>
							<td>(an XD* endpoint, or actual email destination)</td>
						</tr>
						<tr>
							<th><form:label path="aStatus">Status: 
		                                        <form:errors path="aStatus"
									cssClass="error" />
							</form:label></th>
							<th><form:select path="aStatus">
								<form:options items="${statusList}" />
							</form:select></th>
							<td>&nbsp;</td>
						</tr>
						<tr>
							<th><form:label path="type">Type:
		                                        <form:errors path="type"
									cssClass="error" />
							</form:label></th>
							<th><form:input path="type" /></th>
							<td>&nbsp;</td>
						</tr>
					</table>
				    <button name="submitType" id="submitType" type="submit"
					        value="newaddress">Add Address</button>
			     </form:form>
		      </fieldset>
</c:if>

<c:if test="${not empty addressesResults}">
    <spring:url value="/config/domain/removeaddresses" var="formUrlremove" /> 
    <form:form
		modelAttribute="simpleForm" action="${fn:escapeXml(formUrlremove)}"
		cssClass="cleanform" method="POST">
		<form:hidden path="id" />
		<div id="tablelist" style="width: 100%; overflow: auto;">
			<table cellpadding="1px" cellspacing="1px" id="addressTable"
				class="tablesorter">
				<thead>
					<tr>
						<th width="25%">Email Address</th>
						<th width="18%">Display Name</th>
						<th width="17%">Endpoint</th>
						<th width="15%">Type</th>
						<th width="15%">Status</th>
						<th width="10%">Sel</th>
					</tr>
				</thead>
				<tbody>
					<!--  Put the data from the searchResults attribute here -->
					<c:forEach var="address" items="${addressesResults}" varStatus="rowCounter">		
					    <tr>
							<td width="25%"><a href="../address?id=${address.id}">${address.emailAddress}</a></td>
							<td width="18%">${address.displayName}" /></td>
							<td width="17%">${address.endpoint}" /></td>
							<td width="15%">${address.type}" /></td>
							<td width="15%">${address.status}" /></td>
							<td width="10%"><form:checkbox path="remove" value="${address.id}" /></td>
						</tr>
					</c:forEach>
				</tbody>
				<tfoot>
					<tr>
						<th width="25%"></th>
						<th width="18%"></th>
						<th width="17%"></th>
						<th width="15%"></th>
						<th width="15%"></th>
						<th width="10%"></th>
					</tr>
				</tfoot>
			</table>
		</div>
		<!-- Wire this up to jQuery to add an input row to the table.  
                                     Don't submit it all until the final submit is done -->
		<button name="submitType" id="submitType" type="submit" value="delete">Remove Selected</button>
	</form:form></fieldset>
</c:if>
</div>
<div id="tabs-2">
<c:if test='${not empty action && action != "Add" }'>
		<fieldset style="width: 95%;" title="anchor"><spring:url
			value="/config/domain/addanchor" var="formUrladdanchor" /> <form:form
			modelAttribute="anchorForm"
			action="${fn:escapeXml(formUrladdanchor)}" cssClass="cleanform"
			method="POST" enctype="multipart/form-data">
			<form:hidden path="id" />
				<table cellpadding="1px" cellspacing="1px" id="anchorTable">
					<tr>
						<th><form:label for="fileData" path="fileData">Certificate:</form:label>
						</th>
						<th><form:input path="fileData" id="certificatefile"
							type="file" /></th>
					</tr>
					<tr>
						<th><form:label path="incoming">Incoming:
	                                <form:errors path="incoming"
								cssClass="error" />
						</form:label></th>
						<th><form:checkbox path="incoming" /></th>
					</tr>
					<tr>
						<th><form:label path="outgoing">Outgoing:
	                                <form:errors path="outgoing"
								cssClass="error" />
						</form:label></th>
						<th><form:checkbox path="outgoing" /></th>
					</tr>
					<tr>
						<th><form:label path="status">Status: 
	                                                    <form:errors
								path="status" cssClass="error" />
						</form:label></th>
						<th><form:select path="status">
							<form:options items="${statusList}" />
						</form:select></th>
					</tr>
					<tr>
						<th></th>
						<th></th>
					</tr>
				</table>
			<button name="submitType" id="submitType" type="submit"
				value="newanchor">Add anchor</button>
		</form:form></fieldset>
    </c:if>
<c:if test="${not empty anchorsResults}">
	<fieldset style="width: 95%;" title="anchors">
	<spring:url value="/config/domain/removeanchors" var="formUrlremoveanchor" /> 
	<form:form
		modelAttribute="anchorForm"
		action="${fn:escapeXml(formUrlremoveanchor)}" cssClass="cleanform"
		method="POST" enctype="multipart/form-data">
		<form:hidden path="id" />
		<div id="tablelist" style="width: 100%; overflow: auto;">
			<table cellpadding="1px" cellspacing="1px" id="anchorsTable"
				class="tablesorter">
				<thead>
					<tr>
						<th width="13%">Trusted Domain or User</th>
						<th width="12%">Owner</th>
						<th width="15%">Thumb</th>
						<th width="15%">Create</th>
						<th width="15%">Start</th>
						<th width="15%">End</th>
						<th width="7%">Stat</th>
						<th width="3%">In</th>
						<th width="3%">Out</th>
						<th width="3%">Sel</th>
					</tr>
				</thead>
				<tbody>
					<!--  Put the data from the searchResults attribute here -->
					<c:forEach var="anchors" items="${anchorsResults}" varStatus="rowCounter">
					    <tr>
							<td width="13%">${anchors.trusteddomainoruser}</td>
							<td width="12%"><a href="../anchor?id=${anchors.id}">${anchors.owner}</a></td>
							<td width="15%">${anchors.thumbprint} /></td>
							<td width="15%"><fmt:formatDate
								value="${anchors.createTime.time}" pattern="MM/dd/yyyy, hh:mm" /></td>
							<td width="15%"><fmt:formatDate
								value="${anchors.validStartDate.time}" pattern="MM/dd/yyyy, hh:mm" /></td>
							<td width="15%"><fmt:formatDate
								value="${anchors.validEndDate.time}" pattern="MM/dd/yyyy, hh:mm" /></td>
							<td width="15%">${anchors.status}</td>
							<td width="7%">${anchors.incoming}</td>
							<td width="3%">${anchors.outgoing}</td>
							<td width="3%"><form:checkbox path="remove"
								value="${anchors.id}" /></td>
						</tr>
					</c:forEach>
				</tbody>
				<tfoot>
					<tr>
						<th width="13%"></th>
						<th width="12%"></th>
						<th width="15%"></th>
						<th width="15%"></th>
						<th width="15%"></th>
						<th width="15%"></th>
						<th width="7%"></th>
						<th width="3%"></th>
						<th width="3%"></th>
						<th width="3%"></th>
					</tr>
				</tfoot>
			</table>
		</div>
		<!-- Wire this up to jQuery to add an input row to the table. Don't submit it all until the final submit is done -->
		<button name="submitType" id="submitType" type="submit"
			value="deleteanchors">Remove Selected</button>
	</form:form></fieldset>
</c:if>
</div>

<div id="tabs-3">
<c:if test='${not empty action && action != "Add" }'>
        <fieldset style="width: 95%;" title="DNS">
        <spring:url value="/config/domain/addDNSEntry" var="formUrladdDNSEntry" /> 
        <form:form modelAttribute="dnsForm" action="${fn:escapeXml(formUrladdDNSEntry)}" cssClass="cleanform"
            method="POST" enctype="multipart/form-data">
            <form:hidden path="id" />
                <form:hidden path="id" />
                    <table cellpadding="1px" cellspacing="1px" id="dnsTable">
                       <tr>
                          <td>
                            <form:label path="name">Name:
                                <form:errors path="name" cssClass="error" />
                            </form:label>
                          </td>
                          <td>
                            <form:input path="name" />
                          </td>
                          <td>&nbsp;</td>
                       </tr>
                       <tr>
                          <td>
                             <form:label path="type">Record Type:
                                <form:errors path="type" cssClass="error" />
                             </form:label>
                          </td>
                          <td>
                              <form:select id="dnsTypeSelector" path="type" onchange="dnsTypeSelected();">
                                  <form:option value="-" label="--Please Select"/>
                                  <form:options items="${dnsTypes}"/>
                              </form:select>     
                          </td>
                          <td>&nbsp;</td>
                        </tr>
                        <tr>
                            <td>
                                <form:label path="name">Name/Host:
                                   <form:errors path="name" cssClass="error" />
                                </form:label>
                            </td>
                            <td>
                              <form:input id="dnsHost" path="name"/> 
                            </td>
                            <td>&nbsp;</td>
                        </tr>
                        <tr>
                            <td>
                               <form:label path="dest">Destination: 
                                  <form:errors path="aStatus" cssClass="error" />
                               </form:label>
                            </td>
                            <td>
                                <form:input id="dest" path="dest"/>
                            </td>
                            <td>&nbsp;</td>
                        </tr>
                        <tr>
                            <td>
                               <form:label path="ttl">Time to Live: 
                                  <form:errors path="ttl" cssClass="error" />
                               </form:label>
                            </td>
                            <td>
                                <form:input maxlength="6" id="ttl" path="ttl"/>
                            </td>
                            <td>(In seconds)</td>
                        </tr>
                        <tr>
                            <td>
                                <form:label path="priority">Priority:
                                   <form:errors path="priority" cssClass="error" />
                                </form:label>
                            </td>
                            <td><form:input id="priority" maxlength="3" disabled="true" path="priority"/></td>
                            <td>&nbsp;</td>
                        </tr>
                        <tr>
                            <td>
                                <form:label path="service">Service:
                                   <form:errors path="service" cssClass="error" />
                                </form:label>
                            </td>
                            <td><form:input id="service" disabled="true" path="service"/></td>
                            <td>&nbsp;</td>
                        </tr>
                        <tr>
                            <td>
                                <form:label path="protocol">Protocol:
                                   <form:errors path="protocol" cssClass="error" />
                                </form:label>
                            </td>
                            <td>
                                <form:select id="protocol" disabled="true" path="protocol">
                                    <form:option value="TCP"/>
                                    <form:option value="UDP"/>
                                </form:select>
                            </td>
                            <td>&nbsp;</td>
                        </tr>
                        <tr>
                            <td>
                                <form:label path="weight">Weight:
                                   <form:errors path="weight" cssClass="error" />
                                </form:label>
                            </td>
                            <td><form:input id="weight" disabled="true" path="weight"/></td>
                            <td>&nbsp;</td>
                        </tr>                        
                        <tr>
                            <td>
                                <form:label path="port">Port:
                                   <form:errors path="port" cssClass="error" />
                                </form:label>
                            </td>
                            <td><form:input id="port" disabled="true" path="port"/></td>
                            <td>&nbsp;</td>
                        </tr>                         
                    </table>
                    <button name="submitType" id="submitType" type="submit"
                            value="newDNSRecord">Add Record</button>
        </form:form>
     </fieldset>
    </c:if>
<c:if test="${not empty dnsResults}">
    <fieldset style="width: 95%;" title="dnsEntries">
	    <spring:url value="/config/domain/removeDnsEntries" var="formUrlremoveDns" /> 
	    <form:form
	        modelAttribute="dnsForm"
	        action="${fn:escapeXml(formUrlremoveDns)}" cssClass="cleanform"
	        method="POST" enctype="multipart/form-data">
	        <form:hidden path="id" />
	        <div id="tablelist" style="width: 100%; overflow: auto;">
	        <table cellpadding="1px" cellspacing="1px" id="dnsTable"
	            class="tablesorter">
	            <thead>
	                <tr>
	                    <th width="20%">Record Type/Name</th>
	                    <th width="15%">Destination</th>
	                    <th width="15%">TTL</th>
	                    <th width="10%">Priority</th>
	                    <th width="20%">SRV Name</th>
	                    <th width="15%">Weight</th>
	                    <th width="5%">Remove</th>
	                </tr>
	            </thead>
	            <tbody>
	                <!--  Put the data from the searchResults attribute here -->
	                <c:forEach var="entry" items="${dnsResults}" varStatus="rowCounter">
	                    <tr>
	                        <td width="20%"><a href="../dnsEntry?id=${entry.type}">${entry.type}/${entry.name}</a></td>
	                        <td width="15%">${entry.dest} /></td>
	                        <td width="15%">${entry.ttl}</td>
	                        <td width="10%">${entry.priority}</td>
	                        <td width="20%">${entry.srvName}</td>
	                        <td width="15%">${entry.weight}</td>	                        
	                        <td width="5%"><form:checkbox path="remove"
	                            value="${anchors.id}" /></td>
	                    </tr>
	                </c:forEach>
	            </tbody>
	            <tfoot>
	                <tr>
	                    <th width="20%"></th>
	                    <th width="15%"></th>
	                    <th width="15%"></th>
	                    <th width="10%"></th>
	                    <th width="20%"></th>
	                    <th width="15%"></th>
	                    <th width="5%"></th>
	                </tr>
	            </tfoot>
	        </table>
        </div>
        <!-- Wire this up to jQuery to add an input row to the table. Don't submit it all until the final submit is done -->
        <button name="submitType" id="submitType" type="submit"
            value="deleteDnsEntries">Remove Selected</button>
    </form:form></fieldset>
</c:if>
</div>

<c:if test='${not empty action && action != "Add" }'>
        </div>
    </div>
</c:if>
</body>
</html>
