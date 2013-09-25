<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
	pageEncoding="ISO-8859-1"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<%@ include file="/WEB-INF/jsp/include.jsp"%>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title><fmt:message key="policies.title" /></title>

<script type="text/javascript" src="/config-ui/resources/jquery.leanModal.min.js"></script>

<script type="text/javascript">
$(document).ready(function() {    
    
    // Set up modals
     $('a[class=revealLink]').click(function(e) {
        e.preventDefault();   
        $('#myModal').html('Loading...');
        
        $('#myModal').load($(this).attr('href'), function() {
            $('#myModal').reveal({ animation: 'fadeAndPop',                   
                            animationspeed: 300,    
                            dismissmodalclass: 'close-reveal-modal' });
        });
	
     });


    if(window.location.hash == "" || window.location.hash == "#tab1") {                
        $(".tab_content").hide(); //Hide all content
        $("#listtab1").addClass("active").show(); //Activate first tab
        $("#tab1").show(); //Show first tab content
    } 

    if (window.location.hash == "#tab2") {
        $(".tab_content").hide(); //Hide all content
        $("#listtab2").addClass("active").show(); //Activate first tab
        $("#tab2").show(); //Show first tab content
    }

     //On Click Event
    $("ul.tabs li").click(function() {
        $("ul.tabs li").removeClass("active"); //Remove any "active" class
        $(this).addClass("active"); //Add "active" class to selected tab
        $(".tab_content").hide(); //Hide all tab content
        var activeTab = ($(this).find("a").attr("href") != '') ? $(this).find("a").attr("href") : $(this).find("button").attr("title"); //Find the rel attribute value to identify the active tab + content
        $(activeTab).fadeIn(); //Fade in the active content
        return false;
    });           
               
    
});

$(function() {                
    $('a[rel*=leanModal], button[rel*=leanModal]').leanModal({ top : 200, closeButton: ".modal_close" });		
});

function openUpdatePolicyForm(id) {
    
$('#updatePolicy').load('config');
}




</script>


</head>
<body>
<%@ include file="/WEB-INF/jsp/header.jsp"%>

    <h2>Manage Security Policies</h2>
<div id="myModal" class="reveal-modal">Loading...</div>

  

    <div id="newGroupPolicy" class="roundedCorners" style="display:none;background:white;padding:10px;width:400px;height:auto;"></div>
    <div id="newPolicy" class="roundedCorners" style="display:none;background:white;padding:10px;width:400px;height:auto;"></div>
    <div id="updatePolicy" class="roundedCorners" style="display:none;background:white;padding:10px;width:400px;height:auto;"></div>

    <ul class="tabs" style="width:100%">
                <li id="listtab1"><a href="#tab1" onclick="window.location.href='#tab1';">Policies</a></li>
                <li id="listtab2"><a href="#tab2" onclick="window.location.href='#tab2';">Policy Groups</a></li>                
            </ul>
            <div class="container">
                <div class="tab_container">
                    
                    <div id="tab1" class="tab_content">
                                               
                        <a href="/config-ui/config/policies/newPolicyForm" class="revealLink" data-reveal-ajax="true">Add New Policy</a>

                           <c:choose>
                            <c:when test="${not empty policies}">
	
                                <spring:url value="/config/policies/removePolicies" var="formURLRemovePolicies" />                                
                                
                                <form:form modelAttribute="policyForm" action="${fn:escapeXml(formURLRemovePolicies)}" cssClass="cleanform" method="POST">
                                
                                    <form:hidden path="id" />
                 
                                    <div class="box" style="margin-top:10px;margin-bottom:5px;">
                                    <div class="header">
                                        <h3>Policies</h3>
                                    </div>
                                    
                                    <div class="content no-padding">
                                        
                                        <table cellpadding="1px" cellspacing="1px" id="policiesTable" class="table" style="width:100%;margin-bottom:0;font-size:12px;">	
			
                                            <thead>
                                                <tr>
                                                    <th><input type="checkbox" onclick="var checkBoxes = $(':checkbox[name=policiesSelected]');checkBoxes.attr('checked', !checkBoxes.attr('checked'));"/></th>
                                                    <th>Policy Name</th> 
                                                    <th width="100">Lexicon</th>
                                                    <th width="120">Created</th>

                                                    <!--
                                                    <th width="">URL</th>
                                                    <th width="">Checksum</th>
                                                    <th width="" >Created</th>
                                                    <th width="" >Current As Of</th>
                                                    <th width="" >Last Refresh</th>
                                                    <th width="10">Refresh Interval</th>
                                                    -->
                                                </tr>
                                            </thead>
                                            <tbody>
                                                    <!--  Put the data from the searchResults attribute here -->
                                                    <c:forEach var="policy" items="${policies}" varStatus="rowCounter">
                                                        <c:choose>
                                                            <c:when test="${rowCounter.count % 2 == 0}">
                                                                <tr class="evenRow">
                                                            </c:when>
                                                            <c:otherwise>
                                                                <tr class="oddRow">
                                                            </c:otherwise>
                                                        </c:choose>
                                                            <td width=20><form:checkbox path="policiesSelected" value="${policy.id}" /></td>
                                                            <td><a href="/config-ui/config/policies/updatePolicyForm?id=<c:out value="${policy.id}"/>" class="revealLink" data-reveal-ajax="true"><c:out value="${policy.policyName}"/></a></td>
                                                            <td><c:out value="${policy.lexicon}"/></td>
                                                            <td><fmt:formatDate value="${policy.createTime.time}" pattern="MM/dd/yyyy hh:mm" /></td>



                                                            <!--
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
                                                            -->
                                                        </tr>
                                                    </c:forEach>
                                            </tbody>

                </table> 
</div></div>

                <button type="submit" id="submitType" value="delete">Delete</button>                
                
                </form:form>

            </c:when>
            <c:otherwise>

                                <br/>No policies configured yet.

                            </c:otherwise>
                            </c:choose>

                            



                    </div>

                    <div id="tab2" class="tab_content">

                        <a rel="leanModal" name="newPolicyGroup" href="#newPolicyGroup">Add New Policy Group</a>

                    </div>


                </div>
            </div>
    </ul>
			
	
	
	
	
	


<%@ include file="/WEB-INF/jsp/footer.jsp"%>