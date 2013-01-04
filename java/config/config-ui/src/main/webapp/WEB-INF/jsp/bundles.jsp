<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
	pageEncoding="ISO-8859-1"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<%@ include file="/WEB-INF/jsp/include.jsp"%>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title><fmt:message key="bundles.title" /></title>
</head>
<body>
<%@ include file="/WEB-INF/jsp/header.jsp"%>

    <h2>Manage Trust Bundles</h2>
	
	<div>

            <div id="side-form">
			<h3>Add New Trust Bundle</h3>
			
			<spring:url value="/config/bundles/addbundle" var="formURLaddBundle" />
			<form:form modelAttribute="bundleForm" action="${fn:escapeXml(formURLaddBundle)}" cssClass="cleanform" method="POST" enctype="multipart/form-data">
				
				<form:hidden path="id" />
				
                                <c:if test="${EmptyBundleError == true}">
                                    <p style="color:red;">Please enter a bundle name</p>
                                </c:if>

                                <c:if test="${DupeBundleError == true}">
                                    <p style="color:red;">This bundle name has been taken</p>
                                </c:if>

				<c:if test="${signingCertError == true}">
                                    <p style="color:red;">Please upload a valid X.509 certificate</p>
                                </c:if>

                                <c:if test="${URLError == true}">
                                    <p style="color:red;">Please enter a valid URL</p>
                                </c:if>                                
			
				<div>
				<label>Name:</label><br/>
				<form:input path="bundleName"/>
				</div>
			
				<div>
				<label>Trust Bundle URL:</label><br/>
				<form:input path="trustURL"/>
				</div>								
				
				<div>
				<label>Signing Certificate:</label>
				<form:input path="fileData" id="fileData" type="file"/>
				</div>
				
				<div>
				<label>Refresh Interval (hours):</label><br/>
				<form:input path="refreshInterval" cssStyle="width:60px"/>
				</div>
				
			<div class="form-submit-area">
				<button name="submitType" class="submit" id="submitType" type="submit" value="newbundle">Add Trust Bundle</button>
			</div>
			
		</form:form>
			
		</div>

		<div id="bundle-table-col">
			
	<c:choose>
            <c:when test="${not empty trustBundles}">
	
                <spring:url value="/config/bundles/removebundle" var="formURLRemoveBundles" />
                <spring:url value="/config/bundles/refreshBundles" var="formURLRefreshBundles" />
                <form:form modelAttribute="bundleForm" action="${fn:escapeXml(formURLRemoveBundles)}" cssClass="cleanform" method="POST">
		<form:hidden path="id" />
                 <table  id="trustBundlesTable" class="fancyTable" style="width:auto;">
                    <thead>
                        <tr>
                            <th width="10"></th>
                            <th>Bundle Name</th>
                            <th width="">URL</th>
                            <th width="">Checksum</th>
                            <th width="" >Created</th>
                            <th width="" >Current As Of</th>
                            <th width="" >Last Refresh</th>
                            <th width="10">Refresh Interval</th>
                        </tr>
                    </thead>
                    <tbody>
                            <!--  Put the data from the searchResults attribute here -->
                            <c:forEach var="trustBundle" items="${trustBundles}" varStatus="rowCounter">
                                <c:choose>
                                    <c:when test="${rowCounter.count % 2 == 0}">
                                        <tr class="evenRow">
                                    </c:when>
                                    <c:otherwise>
                                        <tr class="oddRow">
                                    </c:otherwise>
                                </c:choose>
                                    <td><form:checkbox path="bundlesSelected" value="${trustBundle.id}" /></td>
                                    <td><c:out value="${trustBundle.bundleName}"/><br/>
                                        
                                    </td>
                                    <td><a href="<c:out value="${trustBundle.bundleURL}"/>" target="_blank"><c:out value="${trustBundle.bundleURL}"/></a></td>
                                    <td><c:out value="${trustBundle.checkSum}"/></td>
                                    <td><fmt:formatDate value="${trustBundle.createTime.time}" pattern="MM/dd/yyyy hh:mm" /></td>
                                    <td><fmt:formatDate value="${trustBundle.lastSuccessfulRefresh.time}" pattern="MM/dd/yyyy hh:mm" /></td>
                                    <td><fmt:formatDate value="${trustBundle.lastRefreshAttempt.time}" pattern="MM/dd/yyyy hh:mm" /></td>
                                    <td><fmt:formatNumber type="number" maxFractionDigits="0" value="${trustBundle.refreshInterval/3600}"/></td>                                
                                </tr>
                            </c:forEach>
                    </tbody>

                </table>
                <button type="submit" id="submitType" value="delete">Delete</button>
                <button id="submitType" value="refresh" onclick="$('#bundleForm').attr('action','${fn:escapeXml(formURLRefreshBundles)}');$('#bundleForm').submit();">Refresh Bundle</button>
                
                </form:form>

            </c:when>
            <c:otherwise>
			
                <div>There are no trust bundles configured for your HISP yet.</div>
			
            </c:otherwise>
	</c:choose>
	
			
			
		</div>
			
	</div>
	
	
	<br clear="both"/>
	
	
	
	


<%@ include file="/WEB-INF/jsp/footer.jsp"%>