<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
	pageEncoding="ISO-8859-1"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">

<%@ include file="/WEB-INF/jsp/include.jsp"%>



<div style="float:right"><a class="close-reveal-modal" href="#">Close</a></div>
<br clear="both"/>
<h3>Add New Policy</h3>

<div class="baseForm" style="margin:0 auto;margin-top:5px;text-align:center;width:400px;">

<div style="text-align:left;width:400px;">
<spring:url value="/config/policies/addpolicy" var="formURLaddPolicy" />
<form:form modelAttribute="policyForm" action="${fn:escapeXml(formURLaddPolicy)}" cssClass="cleanform" method="POST" enctype="multipart/form-data">

        <form:hidden path="id" />

        <c:if test="${EmptyPolicyNameError == true}">
            <p style="color:red;">Please enter a policy name</p>
        </c:if>

        <c:if test="${DupePolicyError == true}">
            <p style="color:red;">This policy name has been taken</p>
        </c:if>

        <c:if test="${invalidSyntaxError == true}">
            <p style="color:red;">Please upload a policy with a valid syntax</p>
        </c:if>

        <c:if test="${InvalidLexiconError == true}">
            <p style="color:red;">Please enter a valid lexicon</p>
        </c:if>                                

        <div>
        <label>Name:</label><br/>
        <form:input path="policyName" cssStyle="width:360px"/>
        </div>
								

        <div>
            <label>Policy File:</label><br/>
            <form:input path="fileData" id="fileData" type="file"/>
        </div>

        <div>
            <label>Lexicon:</label><br/>
            

            <form:select path="policyLexicon">
                <form:options items="${lexiconNames}"/>
            </form:select>

   
            
        </div>

<div class="form-submit-area">
        <button name="submitType" class="submit" id="submitType" type="submit" value="newpolicy">Add Policy</button>
</div>

</form:form>
</div>

</div>