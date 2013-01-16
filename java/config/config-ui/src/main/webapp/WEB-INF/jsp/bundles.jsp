<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
	pageEncoding="ISO-8859-1"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<%@ include file="/WEB-INF/jsp/include.jsp"%>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title><fmt:message key="bundles.title" /></title>

<script type="text/javascript" src="/config-ui/resources/jquery.leanModal.min.js"></script>

<script type="text/javascript">
$(document).ready(function() {
    $('#newBundle').load('/config-ui/config/bundles/newBundleForm');
});

$(function() {                
    $('a[rel*=leanModal]').leanModal({ top : 200, closeButton: ".modal_close" });		
});
</script>


</head>
<body>
<%@ include file="/WEB-INF/jsp/header.jsp"%>

    <h2>Manage Trust Bundles</h2>
	
    <a rel="leanModal" name="newBundle" href="#newBundle">Add New Bundle</a>

    <br/>

    <div id="newBundle" class="roundedCorners" style="display:none;background:white;padding:10px;width:300px;height:auto;"></div>

	<div>

            

		
			
	<c:choose>
            <c:when test="${not empty trustBundles}">
	
                <spring:url value="/config/bundles/removebundle" var="formURLRemoveBundles" />
                <spring:url value="/config/bundles/refreshBundles" var="formURLRefreshBundles" />
                <form:form modelAttribute="bundleForm" action="${fn:escapeXml(formURLRemoveBundles)}" cssClass="cleanform" method="POST">
		<form:hidden path="id" />
                 
<div class="box" style="margin-top:10px;margin-bottom:5px;">
        <div class="header">
            <h3>Trust Bundles</h3>
        </div>
<div class="content no-padding">
<table cellpadding="1px" cellspacing="1px" id="trustBundlesTable" class="table" style="width:100%;margin-bottom:0;font-size:10px;">	
			
                    <thead>
                        <tr>
                            <th><input type="checkbox" onclick="var checkBoxes = $(':checkbox[name=bundlesSelected]');checkBoxes.attr('checked', !checkBoxes.attr('checked'));"/></th>
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
                                        <a rel="leanModal" name="anchorList" href="#anchors_${trustBundle.id}">View Anchors</a>
                                        <div id="anchors_${trustBundle.id}" class="roundedCorners" style="display:none;background:white;padding:10px;width:350px;height:auto;">
                                        <div style="float:right"><a class="modal_close" style="cursor:pointer">Close</a></div>
                                        <br clear="both"/>
                                        <h3 style="color:black">Anchor List</h3>
                                        <div style="overflow:auto;height:250px;">
                                        <ul class="anchorList block-list"> 
                                        <c:forEach items="${bundleMap[trustBundle.bundleName]}" var="anchor">
                                            <li>${anchor.value}</li>
                                        </c:forEach>
                                        </ul>
                                        </div>
                                        </div>
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
</div></div>

                <button type="submit" id="submitType" value="delete">Delete</button>
                <button id="submitType" value="refresh" onclick="$('#bundleForm').attr('action','${fn:escapeXml(formURLRefreshBundles)}');$('#bundleForm').submit();">Refresh Bundle</button>
                
                </form:form>

            </c:when>
            <c:otherwise>
			
                <div>There are no trust bundles configured for your HISP yet.</div>
			
            </c:otherwise>
	</c:choose>
	
			
			
		</div>
			
	
	
	
	
	


<%@ include file="/WEB-INF/jsp/footer.jsp"%>