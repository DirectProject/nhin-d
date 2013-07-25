<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
	pageEncoding="ISO-8859-1"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">

<%@ include file="/WEB-INF/jsp/include.jsp"%>

<script src="<c:url value="/resources/swfupload.js" />"></script>
<script src="<c:url value="/resources/jquery-asyncUpload-0.1.js" />"></script>

<script type="text/javascript">


$('#updatePolicyForm').submit(function(evt){
        evt.preventDefault();        
        
        formData = $('#updatePolicyForm').serialize();       

        $.ajax({
            url: $('#updatePolicyForm').attr('action'), type: 'POST',
            data: formData,
            success: function(html) {
                alert('test');
                //resultTable = $('#update', html);
                //$('#bookSearchResults').html(resultTable);
            }
        }); 
    });






// Set up async upload feature
$(function() {

    $("#fileData").makeAsyncUploader({
        upload_url: "<c:url value="/config/policies/checkLexiconFile"/>", 
        flash_url: '<c:url value="/resources/swfupload.swf"/>',
        button_image_url: '<c:url value="/resources/blankButton.png"/>',
        post_params: {
            //"lexicon": $('#policyLexicon').val()
        }
    });
});


</script>

<div style="float:right"><a class="close-reveal-modal" href="#">Close</a></div>
<br clear="both"/>
<h3>Update Policy</h3>

<div class="baseForm" style="margin:0 auto;margin-top:5px;text-align:center;width:100%;">

<div style="text-align:left;width:100%;">
<spring:url value="/config/policies/updatePolicy" var="formURLupdatePolicy" />
<form:form id="updatePolicyForm" modelAttribute="policyForm" action="${fn:escapeXml(formURLupdatePolicy)}"  cssClass="cleanform" method="POST" enctype="multipart/form-data">

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
        <label>Lexicon:</label><br/>
        <form:select path="policyLexicon">
            <form:options items="${lexiconNames}"/>
        </form:select>
    </div>	  

    <div id="newPolicyFileInput" style="padding:10px;">            
        <form:input path="fileData" id="fileData" type="file"/>
    </div>       

    <div id="editExistingPolicyInput" style="padding:10px;">            
       <form:textarea path="policyContent" cols="60" rows="30"/>
    </div>

    <div class="form-submit-area">
        <button name="submitType" class="submit" id="submitType" type="submit" value="newpolicy">Update Policy</button>
    </div>

</form:form>
</div>

</div>