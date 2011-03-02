<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
	pageEncoding="ISO-8859-1"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<%@ include file="/WEB-INF/jsp/include.jsp"%>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title><fmt:message key="dns.title" /></title>
<script>
$(document).ready(function()
	{
	    $("#dnsAList").tablesorter();
	    $("#dnsA4List").tablesorter();
	    $("#dnsCnameList").tablesorter();
	    $("#dnsMXList").tablesorter();
	    $("#dnsCertList").tablesorter();
	    $("#dnsSrvList").tablesorter();
	});
</script>
</head>
<body>
<%@ include file="/WEB-INF/jsp/header.jsp"%>

<h3>DNS Resolver Configuration</h3>

<br><br>

<c:if test="${param.serviceError == 1}">
	<div class="error">
	<p class="error">An internal service error has occurred. Error
	details are:<br>
	${param.errorDetails}</p>
	</div>
</c:if>
<fieldset><legend>&quot;A&quot; Records</legend>
<fieldset style="width: 95%;"><spring:url
	value="/config/dns/addADNSRecord" var="formUrladdARecord" /> <form:form
	id="aEntryForm" modelAttribute="AdnsForm"
	action="${fn:escapeXml(formUrladdARecord)}" cssClass="cleanform"
	method="POST" enctype="multipart/form-data">
	<form:hidden path="id" />
	<table cellpadding="1px" cellspacing="1px" id="dnsATable">
		<tr>
			<th><form:label path="name">Name
	                        <form:errors path="name" cssClass="error" />
			</form:label></th>
			<th><form:label path="dest">IP Address
	                          <form:errors path="dest" cssClass="error" />
			</form:label></th>
			<th><form:label title="In seconds" path="ttl">TTL
	                         <form:errors path="ttl" cssClass="error" />
			</form:label></th>
		</tr>
		<tr>
			<td><form:input path="name" /></td>
			<td><form:input id="dest" path="dest" /></td>
			<td><form:input maxlength="8" id="ttl" path="ttl" /></td>
		</tr>
	</table>
	<button name="submitType" id="submitType" type="submit"
		value="newDNSRecord">Add Record</button>
</form:form></fieldset>


<c:if test="${not empty dnsARecordResults}">
	<fieldset style="width: 95%;">
	<div id="aList" style="width: 100%; overflow: auto;"><spring:url
		value="/config/dns/removesettings" var="formUrlRemoveDns" /> <form:form
		id="RemoveARecords" modelAttribute="AdnsForm"
		action="${fn:escapeXml(formUrlRemoveDns)}" cssClass="cleanform"
		method="POST" enctype="multipart/form-data">
		<table cellpadding="1px" cellspacing="1px" id="dnsAList"
			class="tablesorter">
			<thead>
				<tr>
					<th width="35%">Host</th>
					<th width="35%">Points To</th>
					<th width="20%">TTL</th>
					<th width="10%">Remove</th>
				</tr>
			</thead>
			<tbody>
				<!--  Put the data from the searchResults attribute here -->
				<c:forEach var="entry" items="${dnsARecordResults}"
					varStatus="rowCounter">
					<tr>
						<td width="35%">${entry.name}</a></td>
						<td width="35%"><c:out value="${entry.dest}" /></td>
						<td width="25%">${entry.ttl}</td>
						<td width="10%"><form:checkbox path="remove"
							value="${entry.id}" /></td>
					</tr>
				</c:forEach>
			</tbody>
			<tfoot>
				<tr>
					<th width="35%"></th>
					<th width="35%"></th>
					<th width="20%"></th>
					<th width="10%"></th>
				</tr>
			</tfoot>
		</table>
		<!-- Wire this up to jQuery to add an input row to the table. Don't submit it all until the final submit is done -->
		<button name="submitType" id="submitType" type="submit"
			value="deleteADnsEntries">Remove Selected As</button>
	</form:form></div>
	</fieldset>
</c:if></fieldset>

<fieldset><legend>&quot;AAAA&quot (IPv6) Records</legend>
<fieldset style="width: 95%;"><spring:url
	value="/config/dns/addA4DNSRecord" var="formUrladdARecord" /> <form:form
	id="a4EntryForm" modelAttribute="AAdnsForm"
	action="${fn:escapeXml(formUrladdARecord)}" cssClass="cleanform"
	method="POST" enctype="multipart/form-data">
	<form:hidden path="id" />
	<table cellpadding="1px" cellspacing="1px" id="dnsA4Table">
		<tr>
			<th><form:label path="name">Name
                            <form:errors path="name" cssClass="error" />
			</form:label></th>
			<th><form:label path="dest">IP Address
                              <form:errors path="dest" cssClass="error" />
			</form:label></th>
			<th><form:label title="In seconds" path="ttl">TTL
                             <form:errors path="ttl" cssClass="error" />
			</form:label></th>
		</tr>
		<tr>
			<td><form:input path="name" /></td>
			<td><form:input id="dest" path="dest" /></td>
			<td><form:input maxlength="8" id="ttl" path="ttl" /></td>
		</tr>
	</table>
	<button name="submitType" id="submitType" type="submit"
		value="newA4DNSRecord">Add Record</button>
</form:form></fieldset>

<c:if test="${not empty dnsA4RecordResults}">

	<div id="a4List" style="width: 100%; overflow: auto;">
	<fieldset style="width: 95%;"><spring:url
		value="/config/dns/removesettings" var="formUrlRemoveDns" /> <form:form
		id="RemoveA4Entries" modelAttribute="AAdnsForm"
		action="${fn:escapeXml(formUrlRemoveDns)}" cssClass="cleanform"
		method="POST" enctype="multipart/form-data">
		<form:hidden path="type" value="AAAA" />
		<table cellpadding="1px" cellspacing="1px" id="dnsA4List"
			class="tablesorter">
			<thead>
				<tr>
					<th width="35%">Host</th>
					<th width="35%">Points To</th>
					<th width="20%">TTL</th>
					<th width="10%">Remove</th>
				</tr>
			</thead>
			<tbody>
				<!--  Put the data from the searchResults attribute here -->
				<c:forEach var="entry" items="${dnsA4RecordResults}"
					varStatus="rowCounter">
					<tr>
						<td width="35%">${entry.name}</a></td>
						<td width="35%">${entry.dest} </td>
						<td width="25%">${entry.ttl}</td>
						<td width="10%"><form:checkbox path="remove"
							value="${entry.id}" /></td>
					</tr>
				</c:forEach>
			</tbody>
			<tfoot>
				<tr>
					<th width="35%"></th>
					<th width="35%"></th>
					<th width="20%"></th>
					<th width="10%"></th>
				</tr>
			</tfoot>
		</table>
		<!-- Wire this up to jQuery to add an input row to the table. Don't submit it all until the final submit is done -->
		<button name="submitType" id="submitType" type="submit"
			value="deleteA4DnsEntries">Remove Selected A4s</button>
	</form:form></fieldset>
	</div>
</c:if></fieldset>

<fieldset><legend>&quot;CNAME&quot; (Alias) Records</legend>
<fieldset style="width: 95%;"><spring:url
	value="/config/dns/addCNAMEDNSRecord" var="formUrladdCnameRecord" /> <form:form
	id="cnameEntryForm" modelAttribute="CdnsForm"
	action="${fn:escapeXml(formUrladdCnameRecord)}" cssClass="cleanform"
	method="POST" enctype="multipart/form-data">
	<form:hidden path="id" />
	<form:hidden path="type" value="CNAME" />
	<table cellpadding="1px" cellspacing="1px" id="dnsCnameTable">
		<tr>
			<th><form:label path="name">Name
                            <form:errors path="name" cssClass="error" />
			</form:label></th>
			<th><form:label path="dest">Alias for
                              <form:errors path="dest" cssClass="error" />
			</form:label></th>
			<th><form:label title="In seconds" path="ttl">TTL
                             <form:errors path="ttl" cssClass="error" />
			</form:label></th>
		</tr>
		<tr>
			<td><form:input path="name" /></td>
			<td><form:input id="dest" path="dest" /></td>
			<td><form:input maxlength="8" id="ttl" path="ttl" /></td>
		</tr>
	</table>
	<button name="submitType" id="submitType" type="submit"
		value="newDNSRecord">Add Record</button>
</form:form></fieldset>
<c:if test="${not empty dnsCnameRecordResults}">

	<div id="cnameList" style="width: 100%; overflow: auto;">
	<fieldset style="width: 95%;"><spring:url
		value="/config/dns/removesettings" var="formUrlRemoveDns" /> <form:form
		id="RemoveCnameEntries" modelAttribute="CdnsForm"
		action="${fn:escapeXml(formUrlRemoveDns)}" cssClass="cleanform"
		method="POST" enctype="multipart/form-data">
		<form:hidden path="type" value="CNAME" />
		<table cellpadding="1px" cellspacing="1px" id="dnsCnameList"
			class="tablesorter">
			<thead>
				<tr>
					<th width="35%">Host</th>
					<th width="35%">Alias For</th>
					<th width="20%">TTL</th>
					<th width="10%">Remove</th>
				</tr>
			</thead>
			<tbody>
				<!--  Put the data from the searchResults attribute here -->
				<c:forEach var="entry" items="${dnsCnameRecordResults}"
					varStatus="rowCounter">
					<tr>
						<td width="35%">${entry.name}</a></td>
						<td width="35%">${entry.dest} </td>
						<td width="25%">${entry.ttl}</td>
						<td width="10%"><form:checkbox path="remove"
							value="${entry.id}" /></td>
					</tr>
				</c:forEach>
			</tbody>
			<tfoot>
				<tr>
					<th width="35%"></th>
					<th width="35%"></th>
					<th width="20%"></th>
					<th width="10%"></th>
				</tr>
			</tfoot>
		</table>
		<!-- Wire this up to jQuery to add an input row to the table. Don't submit it all until the final submit is done -->
		<button name="submitType" id="submitType" type="submit"
			value="deleteCNAMEDnsEntries">Remove Selected CNAMEs</button>
	</form:form></fieldset>
	</div>
</c:if></fieldset>

<fieldset><legend>&quot;MX&quot; (Mail Exchange)
Records</legend>
<fieldset style="width: 95%;"><spring:url
	value="/config/dns/addMXDNSRecord" var="formUrladdMXRecord" /> <form:form
	id="mxEntryForm" modelAttribute="MXdnsForm"
	action="${fn:escapeXml(formUrladdMXRecord)}" cssClass="cleanform"
	method="POST" enctype="multipart/form-data">
	<form:hidden path="id" />
	<form:hidden path="type" value="MX" />
	<table cellpadding="1px" cellspacing="1px" id="dnsMXTable">
		<tr>
			<th><form:label title="Lower number is higher priority"
				path="priority">Priority
                             <form:errors path="priority"
					cssClass="error" />
			</form:label></th>
			<td><form:input maxlength="5" path="priority" /></td>
		</tr>
		<tr>

			<th><form:label path="name">Host
                            <form:errors path="name" cssClass="error" />
			</form:label></th>
			<td><form:input path="name" /></td>
		</tr>
		<tr>

			<th><form:label path="dest">Points To
                              <form:errors path="dest" cssClass="error" />
			</form:label></th>
			<td><form:input id="dest" path="dest" /></td>
		</tr>
		<tr>

			<th><form:label title="In seconds" path="ttl">TTL
                             <form:errors path="ttl" cssClass="error" />
			</form:label></th>
			<td><form:input maxlength="8" id="ttl" path="ttl" /></td>
		</tr>
	</table>
	<button name="submitType" id="submitType" type="submit"
		value="newDNSRecord">Add Record</button>
</form:form></fieldset>


<c:if test="${not empty dnsMxRecordResults}">

	<fieldset style="width: 95%;"><spring:url
		value="/config/dns/removesettings" var="formUrlRemoveDns" /> <form:form
		id="RemoveMxEntries" modelAttribute="MXdnsForm"
		action="${fn:escapeXml(formUrlRemoveDns)}" cssClass="cleanform"
		method="POST" enctype="multipart/form-data">
		<form:hidden path="type" value="MX" />
	<div id="mxList" style="width: 100%; overflow: auto;">
		<table cellpadding="1px" cellspacing="1px" id="dnsMXList"
			class="tablesorter">
			<thead>
				<tr>
					<th width="15%">Priority</th>
					<th width="30%">Host</th>
					<th width="30%">Alias For</th>
					<th width="15%">TTL</th>
					<th width="10%">Remove</th>
				</tr>
			</thead>
			<tbody>
				<!--  Put the data from the searchResults attribute here -->
				<c:forEach var="entry" items="${dnsMxRecordResults}"
					varStatus="rowCounter">
					<tr>
						<td width="15%">${entry.priority}</td>
						<td width="30%">${entry.name}</a></td>
						<td width="30%">${entry.dest} </td>
						<td width="15%">${entry.ttl}</td>
						<td width="10%"><form:checkbox path="remove"
							value="${entry.id}" /></td>
					</tr>
				</c:forEach>
			</tbody>
			<tfoot>
				<tr>
					<th width="15%"></th>
					<th width="30%"></th>
					<th width="30%"></th>
					<th width="15%"></th>
					<th width="10%"></th>
				</tr>
			</tfoot>
		</table>
		</div>
		<!-- Wire this up to jQuery to add an input row to the table. Don't submit it all until the final submit is done -->
		<button name="submitType" id="submitType" type="submit"
			value="deleteMXDnsEntries">Remove Selected MXs</button>
	</form:form></fieldset>

</c:if>

</fieldset>

<fieldset><legend>&quot;SRV&quot; (Service) Records</legend>
<fieldset style="width: 95%;"><spring:url
	value="/config/dns/addSRVDNSRecord" var="formUrladdSrvRecord" /> <form:form
	id="srvEntryForm" modelAttribute="SrvdnsForm"
	action="${fn:escapeXml(formUrladdSrvRecord)}" cssClass="cleanform"
	method="POST" enctype="multipart/form-data">
	<form:hidden path="id" />
	<table cellpadding="1px" cellspacing="1px" id="dnsSrvTable">
		<tr>
			<th><form:label path="service">Service
                            <form:errors path="service" cssClass="error" />
			</form:label></th>
			<td><form:input path="service" /></td>
		</tr>
		<tr>
			<th><form:label path="protocol">Protocol
                            <form:errors path="protocol"
					cssClass="error" />
			</form:label></th>
			<td><form:input path="protocol" /></td>
		</tr>
		<tr>
			<th><form:label title="Domain Name for which this is valid"
				path="name">Domain
                            <form:errors path="name" cssClass="error" />
			</form:label></th>
			<td><form:input path="name" /></td>
		</tr>
		<tr>
			<th><form:label title="In seconds" path="ttl">TTL
                            <form:errors path="ttl" cssClass="error" />
			</form:label></th>
			<td><form:input maxlength="8" path="ttl" /></td>
		</tr>
		<tr>
			<th><form:label title="Lower number is higher priority"
				path="priority">Priority
                             <form:errors path="priority"
					cssClass="error" />
			</form:label></th>
			<td><form:input maxlength="5" path="priority" /></td>
		</tr>
		<tr>
			<th><form:label path="weight">Weight
                             <form:errors path="weight" cssClass="error" />
			</form:label></th>
			<td><form:input maxlength="8" path="weight" /></td>
		</tr>
		<tr>
			<th><form:label path="port">Port
                             <form:errors path="port" cssClass="error" />
			</form:label></th>
			<td><form:input maxlength="5" path="port" /></td>
		</tr>
		<tr>
			<th><form:label
				title="The canonical hostname of the machine providing the service"
				path="dest">Target
                             <form:errors path="dest" cssClass="error" />
			</form:label></th>
			<td><form:input path="dest" /></td>
		</tr>
	</table>
	<button name="submitType" id="submitType" type="submit"
		value="newDNSRecord">Add Record</button>
</form:form></fieldset>

<c:if test="${not empty dnsSrvRecordResults}">

	<div id="certList" style="width: 100%; overflow: auto;">
	<fieldset style="width: 95%;"><spring:url
		value="/config/dns/removesettings" var="formUrlRemoveDns" /> <form:form
		id="RemoveSrvEntries" modelAttribute="SrvdnsForm"
		action="${fn:escapeXml(formUrlRemoveDns)}" cssClass="cleanform"
		method="POST" enctype="multipart/form-data">
		<form:hidden path="type" value="SRV" />
		<table cellpadding="1px" cellspacing="1px" id="dnsSrvList"
			class="tablesorter">
			<thead>
				<tr>
					<th width="15%">Service</th>
					<th width="15%">Protocol</th>
					<th width="15%">Domain</th>
					<th width="10%">TTL</th>
					<th width="7%">Priority</th>
					<th width="10%">Weight</th>
					<th width="7%">Port</th>
					<th width="15%">Target</th>
					<th width="6%">Remove</th>
				</tr>
			</thead>
			<tbody>
				<!--  Put the data from the searchResults attribute here -->
				<c:forEach var="entry" items="${dnsSrvRecordResults}"
					varStatus="rowCounter">
					<tr>
						<td width="15%">${entry.service}</td>
						<td width="15%">${entry.protocol}</td>
						<td width="15%">${entry.name}</td>
						<td width="10%">${entry.ttl}</td>
						<td width="7%">${entry.priority}</td>
						<td width="10%">${entry.weight}</td>
						<td width="7%">${entry.port}</td>
						<td width="15%">${entry.target}</td>
						<td width="6%"><form:checkbox path="remove"
							value="${entry.id}" /></td>
					</tr>
				</c:forEach>
			</tbody>
			<tfoot>
				<tr>
					<th width="15%"></th>
					<th width="15%"></th>
					<th width="15%"></th>
					<th width="10%"></th>
					<th width="7%"></th>
					<th width="10%"></th>
					<th width="7%"></th>
					<th width="15%/"></th>
					<th width="6%"></th>
				</tr>
			</tfoot>
		</table>
		<!-- Wire this up to jQuery to add an input row to the table. Don't submit it all until the final submit is done -->
		<button name="submitType" id="submitType" type="submit"
			value="deleteSRVDnsEntries">Remove Selected SRVs</button>
	</form:form></fieldset>
	</div>
</c:if>
</fieldset>



<fieldset><legend>&quot;SOA&quot; (Start of Authority) Records</legend>
<fieldset style="width: 95%;"><spring:url
	value="/config/dns/addSOADNSRecord" var="formUrladdSoaRecord" /> <form:form
	id="soaEntryForm" modelAttribute="SoadnsForm"
	action="${fn:escapeXml(formUrladdSoaRecord)}" cssClass="cleanform"
	method="POST" enctype="multipart/form-data">
	<form:hidden path="id" />
	<table cellpadding="1px" cellspacing="1px" id="dnsSoaTable">
		<tr>
			<th><form:label path="name">Name
                            <form:errors path="name" cssClass="error" />
			</form:label></th>
			<td><form:input path="name" /></td>
		</tr>
		<tr>
			<th><form:label path="admin">Host Master
                            <form:errors path="admin"
					cssClass="error" />
			</form:label></th>
			<td><form:input path="admin" /></td>
		</tr>
		<tr>
			<th><form:label title="Name Server"
				path="domain">Name Server
                            <form:errors path="domain" cssClass="error" />
			</form:label></th>
			<td><form:input path="domain" /></td>
		</tr>
		<tr>
			<th><form:label title="In seconds" path="ttl">TTL
                            <form:errors path="ttl" cssClass="error" />
			</form:label></th>
			<td><form:input maxlength="8" path="ttl" /></td>
		</tr>
		<tr>
			<th><form:label title="Lower number is higher priority"
				path="expire">Expire
                             <form:errors path="expire"
					cssClass="error" />
			</form:label></th>
			<td><form:input maxlength="5" path="expire" /></td>
		</tr>
		<tr>
			<th><form:label path="minimum">Minimum
                             <form:errors path="minimum" cssClass="error" />
			</form:label></th>
			<td><form:input maxlength="8" path="minimum" /></td>
		</tr>
		<tr>
			<th><form:label path="refresh">Refresh
                             <form:errors path="refresh" cssClass="error" />
			</form:label></th>
			<td><form:input maxlength="5" path="refresh" /></td>
		</tr>
		<tr>
			<th><form:label
				title="serial"
				path="serial">Serial
                             <form:errors path="serial" cssClass="error" />
			</form:label></th>
			<td><form:input path="serial" /></td>
		</tr>
		<tr>
			<th><form:label
				title="retry"
				path="retry">Retry
                             <form:errors path="retry" cssClass="error" />
			</form:label></th>
			<td><form:input path="retry" /></td>
		</tr>
	</table>
	<button name="submitType" id="submitType" type="submit"
		value="newDNSRecord">Add Record</button>
</form:form></fieldset>

<c:if test="${not empty dnsSOARecordResults}">

	<div id="certList" style="width: 100%; overflow: auto;">
	<fieldset style="width: 95%;"><spring:url
		value="/config/dns/removesettings" var="formUrlRemoveDns" /> <form:form
		id="RemoveSoaEntries" modelAttribute="SoadnsForm"
		action="${fn:escapeXml(formUrlRemoveDns)}" cssClass="cleanform"
		method="POST" enctype="multipart/form-data">
		<form:hidden path="type" value="SOA" />
		<table cellpadding="1px" cellspacing="1px" id="dnsSoaList"
			class="tablesorter">
			<thead>
				<tr>
					<th width="15%">Name</th>
					<th width="15%">Host Master</th>
					<th width="15%">Name Server</th>
					<th width="10%">TTL</th>
					<th width="7%">Expire</th>
					<th width="10%">Min.</th>
					<th width="7%">Refresh</th>
					<th width="8%">Serial</th>
					<th width="7%">Retry</th>
					<th width="6%">Remove</th>
				</tr>
			</thead>
			<tbody>
				<!--  Put the data from the searchResults attribute here -->
				<c:forEach var="entry" items="${dnsSOARecordResults}"
					varStatus="rowCounter">
					<tr>
						<td width="15%">${entry.name}</td>
						<td width="15%">${entry.admin}</td>
						<td width="15%">${entry.domain}</td>
						<td width="10%">${entry.ttl}</td>
						<td width="7%">${entry.expire}</td>
						<td width="10%">${entry.minimum}</td>
						<td width="7%">${entry.refresh}</td>
						<td width="8%">${entry.serial}</td>
						<td width="7%">${entry.retry}</td>
						<td width="6%"><form:checkbox path="remove"
							value="${entry.id}" /></td>
					</tr>
				</c:forEach>
			</tbody>
			<tfoot>
				<tr>
					<th width="15%"></th>
					<th width="15%"></th>
					<th width="15%"></th>
					<th width="10%"></th>
					<th width="7%"></th>
					<th width="10%"></th>
					<th width="7%"></th>
					<th width="15%/"></th>
					<th width="6%"></th>
				</tr>
			</tfoot>
		</table>
		<!-- Wire this up to jQuery to add an input row to the table. Don't submit it all until the final submit is done -->
		<button name="submitType" id="submitType" type="submit"
			value="deleteSOADnsEntries">Remove Selected SOAs</button>
	</form:form></fieldset>
	</div>
</c:if>
</fieldset>

<fieldset><legend>&quot;NS&quot; Records</legend>
<fieldset style="width: 95%;"><spring:url
	value="/config/dns/addNSDNSRecord" var="formUrladdNSRecord" /> <form:form
	id="nsEntryForm" modelAttribute="NSdnsForm"
	action="${fn:escapeXml(formUrladdNSRecord)}" cssClass="cleanform"
	method="POST" enctype="multipart/form-data">
	<form:hidden path="id" />
	<table cellpadding="1px" cellspacing="1px" id="dnsNSTable">
		<tr>
			<th><form:label path="name">Name
	                        <form:errors path="name" cssClass="error" />
			</form:label></th>
			<th><form:label path="dest">Target
	                          <form:errors path="dest" cssClass="error" />
			</form:label></th>
			<th><form:label title="In seconds" path="ttl">TTL
	                         <form:errors path="ttl" cssClass="error" />
			</form:label></th>
		</tr>
		<tr>
			<td><form:input path="name" /></td>
			<td><form:input id="dest" path="dest" /></td>
			<td><form:input maxlength="8" id="ttl" path="ttl" /></td>
		</tr>
	</table>
	<button name="submitType" id="submitType" type="submit"
		value="newDNSRecord">Add Record</button>
</form:form></fieldset>


<c:if test="${not empty dnsNSRecordResults}">
	<fieldset style="width: 95%;">
	<div id="aList" style="width: 100%; overflow: auto;"><spring:url
		value="/config/dns/removesettings" var="formUrlRemoveDns" /> <form:form
		id="RemoveNSRecords" modelAttribute="NSdnsForm"
		action="${fn:escapeXml(formUrlRemoveDns)}" cssClass="cleanform"
		method="POST" enctype="multipart/form-data">
		<table cellpadding="1px" cellspacing="1px" id="dnsNSList"
			class="tablesorter">
			<thead>
				<tr>
					<th width="35%">Name</th>
					<th width="35%">Target</th>
					<th width="20%">TTL</th>
					<th width="10%">Remove</th>
				</tr>
			</thead>
			<tbody>
				<!--  Put the data from the searchResults attribute here -->
				<c:forEach var="entry" items="${dnsNSRecordResults}"
					varStatus="rowCounter">
					<tr>
						<td width="35%">${entry.name}</a></td>
						<td width="35%"><c:out value="${entry.dest}" /></td>
						<td width="25%">${entry.ttl}</td>
						<td width="10%"><form:checkbox path="remove"
							value="${entry.id}" /></td>
					</tr>
				</c:forEach>
			</tbody>
			<tfoot>
				<tr>
					<th width="35%"></th>
					<th width="35%"></th>
					<th width="20%"></th>
					<th width="10%"></th>
				</tr>
			</tfoot>
		</table>
		<!-- Wire this up to jQuery to add an input row to the table. Don't submit it all until the final submit is done -->
		<button name="submitType" id="submitType" type="submit"
			value="deleteNSDnsEntries">Remove Selected NSs</button>
	</form:form></div>
	</fieldset>
</c:if></fieldset>
<%@ include file="/WEB-INF/jsp/footer.jsp"%>