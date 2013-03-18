<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
	pageEncoding="ISO-8859-1"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<%@ include file="/WEB-INF/jsp/include.jsp"%>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title><fmt:message key="domain.title" /></title>
<!-- I got this from http://www.sohtanaka.com/web-design/simple-tabs-w-css-jquery/ -->
<style type="text/css">
ul.tabs {
	margin: 0;
	padding: 0;
	float: left;
	list-style: none;
	height: 32px;
	border-bottom: 1px solid #999;
	border-left: 1px solid #999;
	width: 110%;
}

ul.tabs li {
	float: left;
	margin: 0;
	padding: 0;
	height: 31px;
	line-height: 31px;
	border: 1px solid #999;
	border-left: none;
	margin-bottom: -1px;
	background: #e0e0e0;
	overflow: hidden;
	position: relative;
}

ul.tabs li a {
	text-decoration: none;
	color: #000;
	display: block;
	font-size: 1.2em;
	padding: 0 20px;
	border: 1px solid #fff;
	outline: none;
}

ul.tabs li a:hover {
	background: #ccc;
}

html ul.tabs li.active,html ul.tabs li.active a:hover {
	background: #fff;
	border-bottom: 1px solid #fff;
}

.tab_container {
	border: 1px solid #999;
	border-top: none;
	clear: both;
	float: left;
	width: 110%;
	background: #fff;
	-moz-border-radius-bottomright: 5px;
	-khtml-border-radius-bottomright: 5px;
	-webkit-border-bottom-right-radius: 5px;
	-moz-border-radius-bottomleft: 5px;
	-khtml-border-radius-bottomleft: 5px;
	-webkit-border-bottom-left-radius: 5px;
}

.tab_content {
	padding: 20px;
	font-size: 1.2em;
}

.tab_content h2 {
	font-weight: normal;
	padding-bottom: 10px;
	border-bottom: 1px dashed #ddd;
	font-size: 1.8em;
}

.tab_content h3 a {
	color: #254588;
}

.tab_content img {
	float: left;
	margin: 0 20px 20px 0;
	border: 1px solid #ddd;
	padding: 5px;
}
</style>
<script type="text/javascript" charset="utf-8">
	$(document).ready(function() {

		//Default Action
		$(".tab_content").hide(); //Hide all content
		$("ul.tabs li:first").addClass("active").show(); //Activate first tab
		$(".tab_content:first").show(); //Show first tab content
		
		//On Click Event
		$("ul.tabs li").click(function() {
			$("ul.tabs li").removeClass("active"); //Remove any "active" class
			$(this).addClass("active"); //Add "active" class to selected tab
			$(".tab_content").hide(); //Hide all tab content
			var activeTab = $(this).find("a").attr("href"); //Find the rel attribute value to identify the active tab + content
			$(activeTab).fadeIn(); //Fade in the active content
			return false;
		});

	});
	$(document).ready(function() 
		    { 
		    } 
	); 
		
	</script>
</head>
<body>
<%@ include file="/WEB-INF/jsp/header.jsp"%>
<div id="form">
<fieldset>
<h3>NHIN Direct Java Reference Implementation - Manage Domains</h3>
   <form action="<c:url value="/j_spring_security_logout"/>">
           <button style="float:right;" name="logoutBtn" id="logoutBtn" type="submit">Log out</button></td>
       </form>
</fieldset>
<spring:url value="/config/domain/saveupdate" var="formUrl" /> 
  <form:form
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
		                <form:errors path="postmasterEmail" cssClass="error" />
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
<c:choose>
	<c:when test='${empty action || action == "Add" }'>
	</c:when>
	<c:otherwise>
		<ul class="tabs">
			<li><a href="#tab1">Addresses</a></li>
			<li><a href="#tab2">Anchors</a></li>
		</ul>
		<div class="container">
		<div class="tab_container">
		<div id="tab1" class="tab_content">
		<fieldset style="width: 90%;" title="AddressesGroup"><legend>Addresses:</legend>
		<fieldset style="width: 90%;" title="Address"><spring:url
			value="/config/domain/addaddress" var="formUrladdaddress" /> <form:form
			modelAttribute="addressForm"
			action="${fn:escapeXml(formUrladdaddress)}" cssClass="cleanform"
			method="POST">
			<form:hidden path="id" />
			<table cellpadding="1px" cellspacing="1px" id="addressTable">
				<tr>
					<th><form:label path="displayName">Display name:
							            <form:errors path="displayName" cssClass="error" />
					</form:label></th>
					<th><form:input path="displayName" /></th>
					<td>&nbsp;</td>
				</tr>
				<tr>
					<th><form:label path="emailAddress">E-Mail Address:
							            <form:errors path="emailAddress" cssClass="error" />
					</form:label></th>
					<th><form:input path="emailAddress" /></th>
                    <td>&nbsp;</td>
				</tr>
                <tr>
                    <th><form:label path="endpoint">Endpoint:
                                        <form:errors path="endpoint" cssClass="error" />
                    </form:label></th>
                    <th><form:input path="endpoint" /></th>
                    <td>(an XD* endpoint, or actual email destination)</td>
                </tr>
				<tr>
					<th><form:label path="aStatus">Status: 
						                <form:errors path="aStatus" cssClass="error" />
					</form:label></th>
					<th><form:select path="aStatus">
						<form:options items="${statusList}" />
					</form:select></th>
                    <td>&nbsp;</td>
				</tr>
				<tr>
					<th><form:label path="type">Type:
							            <form:errors path="type" cssClass="error" />
					</form:label></th>
					<th><form:input path="type" /></th>
                    <td>&nbsp;</td>
				</tr>
			</table>
			<button name="submitType" id="submitType" type="submit"
				value="newaddress">Add Address</button>
		</form:form></fieldset>
	</c:otherwise>
</c:choose>

<c:if test="${not empty addressesResults}">
	<fieldset style="width: 95%;" title="Addresses"><spring:url
		value="/config/domain/removeaddresses" var="formUrlremove" /> <form:form
		modelAttribute="simpleForm" action="${fn:escapeXml(formUrlremove)}"
		cssClass="cleanform" method="POST">
		<form:hidden path="id" />
		<div id="tablelist" style="width:100%;overflow:auto;">
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
				<c:forEach var="address" items="${addressesResults}"
					varStatus="rowCounter">
					<c:choose>
						<c:when test="${rowCounter.count % 2 == 0}">
							<tr class="evenRow">
						</c:when>
						<c:otherwise>
							<tr class="oddRow">
						</c:otherwise>
					</c:choose>
					<td width="25%"><a
						href='../address?id=<c:out value="${address.id}"/>'>'${address.emailAddress}'</a></td>
					<td width="18%"><c:out value="${address.displayName}" /></td>
					<td width="17%"><c:out value="${address.endpoint}" /></td>
					<td width="15%"><c:out value="${address.type}" /></td>
					<td width="15%"><c:out value="${address.status}" /></td>
					<td width="10%"><form:checkbox path="remove"
						value="${address.id}" /></td>
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
		<button name="submitType" id="submitType" type="submit" value="delete">Remove
		Selected Addresses</button>
	</form:form></fieldset>
</c:if>
</fieldset>
</div>
 <div id="tab2" class="tab_content">
 <c:choose>
	<c:when test='${empty action || action == "Add" }'>
	</c:when>
	<c:otherwise>
 
<fieldset style="width: 99%;" title="anchorgroup"><legend>Anchors:</legend>
<fieldset style="width: 95%;" title="anchor"><spring:url
	value="/config/domain/addanchor" var="formUrladdanchor" /> <form:form
	modelAttribute="anchorForm" action="${fn:escapeXml(formUrladdanchor)}"
	cssClass="cleanform" method="POST" enctype="multipart/form-data">
	<form:hidden path="id" />
	<table cellpadding="1px" cellspacing="1px" id="anchorTable">
		<tr>
			<th>
				<form:label for="fileData" path="fileData">Certificate:</form:label>
			</th>
			<th>
				<form:input path="fileData" id="certificatefile" type="file"/>
			</th>
		</tr>
		<tr>
			<th><form:label path="incoming">Incoming:
					            <form:errors path="incoming" cssClass="error" />
			</form:label></th>
			<th>
			<form:checkbox path="incoming" />
			</th>
		</tr>
		<tr>
			<th><form:label path="outgoing">Outgoing:
					            <form:errors path="outgoing" cssClass="error" />
			</form:label></th>
			<th>
			<form:checkbox path="outgoing" />
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
		<tr>
			<th></th>
			<th></th>
		</tr>
	</table>
	<button name="submitType" id="submitType" type="submit"
		value="newanchor">Add anchor</button>
</form:form></fieldset>
	</c:otherwise>
</c:choose>
<c:if test="${not empty anchorsResults}">
	<fieldset style="width: 95%;" title="anchors"><spring:url
		value="/config/domain/removeanchors" var="formUrlremoveanchor" /> <form:form
		modelAttribute="anchorForm"
		action="${fn:escapeXml(formUrlremoveanchor)}" cssClass="cleanform"
		method="POST" enctype="multipart/form-data">
		<form:hidden path="id" />
		<div id="tablelist" style="width:100%;overflow:auto;">
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
				<c:forEach var="anchors" items="${anchorsResults}"
					varStatus="rowCounter">
					<c:choose>
						<c:when test="${rowCounter.count % 2 == 0}">
							<tr class="evenRow">
						</c:when>
						<c:otherwise>
							<tr class="oddRow">
						</c:otherwise>
					</c:choose>
					<td width="13%"><c:out value="${anchors.trusteddomainoruser}" /></td>
					<td width="12%"><a
						href='../anchor?id=<c:out value="${anchors.id}"/>'>'${anchors.owner}'</a></td>
					<td width="15%"><c:out value="${anchors.thumbprint}" /></td>
					<td width="15%"><fmt:formatDate
						value="${anchors.createTime.time}" pattern="MM/dd/yyyy, hh:mm" /></td>
					<td width="15%"><fmt:formatDate
						value="${anchors.validStartDate.time}" pattern="MM/dd/yyyy, hh:mm" /></td>
					<td width="15%"><fmt:formatDate
						value="${anchors.validEndDate.time}" pattern="MM/dd/yyyy, hh:mm" /></td>
					<td width="15%"><c:out value="${anchors.status}" /></td>
					<td width="7%"><c:out value="${anchors.incoming}" /></td>
					<td width="3%"><c:out value="${anchors.outgoing}" /></td>
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
			value="deleteanchors">Remove Selected Anchors</button>
	</form:form></fieldset>
</c:if></fieldset>
</div>

<c:choose>
	<c:when test='${empty action || action == "Add" }'>
	</c:when>
	<c:otherwise>
</div>
</div>
</c:otherwise>
</c:choose>

</body>
</html>
