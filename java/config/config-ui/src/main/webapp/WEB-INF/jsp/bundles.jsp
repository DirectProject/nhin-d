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
		<div id="bundle-table-col">
			<h3>Trusted Bundles</h3>
	<c:choose>
		<c:when test="${not empty trustBundles}">
	
                 <table  id="trustBundlesTable" class="data" style="font-size:10px; width:100%;">
                    <thead>
                        <tr>
                            <th width="10"></th>
                            <th width="200">Bundle Name</th>                            
                            <th width="">Thumb</th>
                            <th width="" nowrap>Created</th>
                            <th width="" nowrap>Last Refresh</th>
                            <th width="" nowrap>Refresh Interval</th>
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
                                    <td></td>
                                    <td><c:out value="${trustBundle.bundleName}"/><br/>
                                        <em><c:out value="${trustBundle.bundleURL}" /></em>
                                    </td>
                                    
                                    <td></td>
                                    <td></td>
                                    <td></td>
                                    <td></td>                                
                                </tr>
                            </c:forEach>
                    </tbody>

            </table>
	
		</c:when>
		<c:otherwise>
			
			<div>There are no trust bundles configured for your HISP yet.</div>
			
		</c:otherwise>
	</c:choose>
	
			
			
		</div>
		<div id="add-new-bundle-form">
			<h3>Add New Trust Bundle</h3>
			
			<spring:url	value="/config/bundles/addbundle" var="formURLaddBundle" />
			<form:form modelAttribute="bundleForm" action="${fn:escapeXml(formURLaddBundle)}" cssClass="cleanform" method="POST" enctype="multipart/form-data">
				
				<form:hidden path="id" />
				
				<c:if test="${certerror == true}">
                    <p style="color:red;">Please upload a valid X.509 certificate</p>
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
	</div>
	
	
	<br clear="both"/>
	
	
	
	


<%@ include file="/WEB-INF/jsp/footer.jsp"%>