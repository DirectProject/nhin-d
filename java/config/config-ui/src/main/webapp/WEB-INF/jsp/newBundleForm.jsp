<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
	pageEncoding="ISO-8859-1"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">

<%@ include file="/WEB-INF/jsp/include.jsp"%>



<div style="float:right"><a class="modal_close" href="#">Close</a></div>
<br clear="both"/>
<h3>Add New Trust Bundle</h3>

<div class="baseForm" style="margin:0 auto;margin-top:5px;text-align:center;width:260px;">

<div style="text-align:left;width:300px;">
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
        <label>Signing Certificate:</label><br/>
        <form:input path="fileData" id="fileData" type="file"/>
        </div>

        <div>
        <label>Refresh Interval (hours):</label><br/>
        <form:input path="refreshInterval" cssStyle="width:60px" value="72"/>
        </div>

<div class="form-submit-area">
        <button name="submitType" class="submit" id="submitType" type="submit" value="newbundle">Add Trust Bundle</button>
</div>

</form:form>
</div>

</div>