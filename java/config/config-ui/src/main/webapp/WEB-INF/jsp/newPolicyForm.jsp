<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
	pageEncoding="ISO-8859-1"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">

<%@ include file="/WEB-INF/jsp/include.jsp"%>

<script type="text/javascript" src="http://bp.yahooapis.com/2.4.21/browserplus-min.js"></script>
<script type="text/javascript" src="<c:url value="/resources/plupload/js/plupload.full.js"/>"></script>


<script type="text/javascript">

$( document ).ready(function() {



    $(function() {
            var uploader = new plupload.Uploader({
                    runtimes : 'gears,html5,flash,silverlight,browserplus',
                    browse_button : 'pickfiles',
                    container : 'container',
                    max_file_size : '10mb',
                    url : '<c:url value="/config/policies/checkLexiconFile"/>',
                    flash_swf_url : '<c:url value="/resources/plupload/js/plupload.flash.swf"/>',
                    silverlight_xap_url : '<c:url value="/resources/plupload/js/plupload.silverlight.xap"/>',
                    filters : [
                            {title : "Image files", extensions : "xml,txt"},
                            {title : "Zip files", extensions : "zip"}
                    ],
                    resize : {width : 320, height : 240, quality : 90}
            });

            uploader.bind('Init', function(up, params) {
                    $('#filelist').html("<div>Current runtime: " + params.runtime + "</div>");
            });

            $('#uploadfiles').click(function(e) {
                    uploader.start();
                    e.preventDefault();
            });

            uploader.init();

            uploader.bind('FilesAdded', function(up, files) {
                    $.each(files, function(i, file) {
                            $('#filelist').append(
                                    '<div id="' + file.id + '">' +
                                    file.name + ' (' + plupload.formatSize(file.size) + ') <b></b>' +
                            '</div>');
                    });

                    up.refresh(); // Reposition Flash/Silverlight



                    uploader.start();		
            });

            uploader.bind('UploadProgress', function(up, file) {
                    $('#' + file.id + " b").html(file.percent + "%");
            });

            uploader.bind('Error', function(up, err) {
                    $('#filelist').append("<div>Error: " + err.code +
                            ", Message: " + err.message +
                            (err.file ? ", File: " + err.file.name : "") +
                            "</div>"
                    );

                    up.refresh(); // Reposition Flash/Silverlight
            });

            uploader.bind('BeforeUpload', function (up, file) {            
                up.settings.multipart_params = {lexicon: $('#policyLexicon').val()}
            });

            uploader.bind('FileUploaded', function(up, file, response) {
                var json = $.parseJSON(response.response);            



                // Handle file validation
                if(json.Status != 'Success') {
                    $('#lexiconError').show().css('color','red').html('File was not validated properly');                
                } else {     
                    $('#lexiconError').show().css('color','green').html('File was validated properly');
                }

                $('#policyContent').val(json.Content);

            });


    });
});

// Detect change to policy input and validate real-time
$('#policyContent').bind('input propertychange', function() {
    validatePolicyContent();
});

// Detect change to policy type and validate real-time
$('#policyLexicon').change(function() { 
    validatePolicyContent();
});

function validatePolicyContent() {
    policyContent = $('#policyContent').val();

    request = $.ajax({
        url: "<c:url value="/config/policies/checkPolicyContent"/>", 
        type: "post", 
        data: { lexicon: $('#policyLexicon').val(), content: policyContent },
        success: function(msg) {
            msg = JSON.parse(msg);

            if(msg.Status != 'Success') {
                $('#lexiconError').show().css('color','red').html('File was not validated properly');                
            } else {     
               $('#lexiconError').show().css('color','green').html('File was validated properly');
            }
        }
        
    });
}



$('#newPolicyForm').submit(function(evt){
    evt.preventDefault();        

    formData = $('#newPolicyForm').serialize();

    $.ajax({
        url: $('#newPolicyForm').attr('action'), type: 'POST',
        data: formData,
        success: function(html) {
            console.log(html);
        }
    }); 
});


</script>


<div style="float:right"><a class="close-reveal-modal" href="#">Close</a></div>
<br clear="both"/>
<h3>Add New Policy</h3>

<div class="baseForm" style="margin:0 auto;margin-top:5px;text-align:center;width:100%;">

<div style="text-align:left;width:400px;">
<spring:url value="/config/policies/addPolicy" var="formURLaddPolicy" />
<form:form id="newPolicyForm" modelAttribute="policyForm" action="${fn:escapeXml(formURLaddPolicy)}" cssClass="cleanform" method="POST" enctype="multipart/form-data">

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
								

        <div id="container">
            Import policy file: <a id="pickfiles" href="#" style="height:22px;"><button style="height:22px;padding:2px 3px;">Browse files...</button></a>    
        </div>
        
        <div>
            <label>Lexicon:</label><br/>
            

            <form:select path="policyLexicon">
                <form:options items="${lexiconNames}"/>
            </form:select>

            <div id="lexiconError" style="display:none;font-style:italic;padding:0;margin:10px;"></div>

            <div id="editExistingPolicyInput" style="padding:10px 0px;">            
               <form:textarea path="policyContent" cols="60" rows="30"/>
            </div>

            
        </div>

<div class="form-submit-area">
        <button name="submitType" class="submit" id="submitType" type="submit" value="newpolicy">Add Policy</button>
</div>

</form:form>
</div>

</div>