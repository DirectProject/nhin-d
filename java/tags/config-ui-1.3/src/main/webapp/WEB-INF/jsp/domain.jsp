<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
         pageEncoding="ISO-8859-1"%><!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
    <head>
        <%@ include file="/WEB-INF/jsp/include.jsp"%>
        <meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
        <title><fmt:message key="domain.title" /></title>

        <script type="text/javascript" src="/config-ui/resources/jquery.leanModal.min.js"></script>
        
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


            #lean_overlay {
                position: fixed;
                z-index:100;
                top: 0px;
                left: 0px;
                height:100%;
                width:100%;
                background: #000;
                display: none;
            }


        </style>
        <script type="text/javascript" charset="utf-8">
            $(document).ready(function() {

                

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

                if (window.location.hash == "#tab3") {
                    $(".tab_content").hide(); //Hide all content
                    $("#listtab3").addClass("active").show(); //Activate first tab
                    $("#tab3").show(); //Show first tab content
                }


                //On Click Event
                $("ul.tabs li").click(function() {
                    $("ul.tabs li").removeClass("active"); //Remove any "active" class
                    $(this).addClass("active"); //Add "active" class to selected tab
                    $(".tab_content").hide(); //Hide all tab content
                    var activeTab = $(this).find("a").attr("href"); //Find the rel attribute value to identify the active tab + content
                    $(activeTab).fadeIn(); //Fade in the active content
                    return false;
                });

                
		$('#assignBundles').load('/config-ui/config/bundles/assignBundlesForm');

                <c:if test="${not empty domainId}">

                // Attach listener to incoming/outgoing checkboxes
                $('[name="incoming"]').change( function() {
                    // Update incoming status for this bundle on the domain
                    var checkboxId = $(this).attr('id').split('_');

                    var direction = checkboxId[0];
                    var bundle = checkboxId[1];
                    var directionValue = ($(this).attr('checked') == true) ? 1 : 0;
                    
                    var data = { domainId: ${domainId}, bundle: bundle, direction: direction, directionValue: directionValue };

                    $.post('/config-ui/config/domain/updateBundleDirection',data, function(data) {
                                                
                    });
                });
                
                $('[name="outgoing"]').change( function() {
                    // Update outgoing status for this bundle on the domain

                    var checkboxId = $(this).attr('id').split('_');

                    var direction = checkboxId[0];
                    var bundle = checkboxId[1];
                    var directionValue = ($(this).attr('checked') == true) ? 1 : 0;

                    
                    var data = { domainId: ${domainId}, bundle: bundle, direction: direction, directionValue: directionValue };

                    $.post('/config-ui/config/domain/updateBundleDirection',data, function(data) {
                        window.location.href = "/config-ui/config/domain?id=${domainId}#tab3";
                    });
                });

               

                </c:if>
               

            });

         
            $(function() {                
                $('a[rel*=leanModal]').leanModal({ top : 200, closeButton: ".modal_close" });		
            });

        
            $(function() {
                $('a[name=addMoreBundles]').click(function() {
                    $('#addMoreBundles').load('/config-ui/config/bundles/addMoreBundlesForm?domainId=${domainId}');
                });
            });

             function selectAllBoxes() {

                    var checkBoxes = $(':checkbox[name=bundlesSelected]'); 

                    if($('#bundleCheckbox').attr('checked')) {    
                        checkBoxes.attr('checked', 'checked');            
                    } else {
                        checkBoxes.attr('checked', '');
                    }
                }

<c:if test="${not empty domainId}">
 function removeBundles() {
                    
                    var bundles = "";
                    $(':checkbox[name=bundlesToRemove]:checked').each( function() {
                        bundles += $(this).val() + ":";
                     } );
                     
                     bundles = bundles.substr(0, bundles.length-1);
                    var data = { domainId: ${domainId}, bundles: bundles };
                    console.log(data);                    
                    $.post('/config-ui/config/domain/removeBundles', data, function(data) {
                                 window.location.reload();             
                    });
                    

                    
                }
</c:if>
             
             function selectAllBundles() {

                    var checkBoxes = $(':checkbox[name=bundlesToRemove]'); 

                    if($('#bundleCheckbox').attr('checked')) {    
                        checkBoxes.attr('checked', 'checked');            
                    } else {
                        checkBoxes.attr('checked', '');
                    }
                }

                
    

        </script>
    </head>
    <body>
        <%@ include file="/WEB-INF/jsp/header.jsp"%>


        <h2>Manage Domains</h2>

        
        <h3>Add New Domain to HISP</h3>
        
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

                <c:if test='${empty action || action == "Add" }'>
                <tr><td>
                    Selected Trust Bundles:<br/>
                    (<a rel="leanModal" name="AssignBundles" href="#assignBundles">Select Trust Bundles</a>)
                    </td>
                    <td align="top"><div id="bundlesList"></div></td></tr>
                </c:if>

            </table>
                   
            <form:hidden path="selectedBundles"/>
            <div id="assignBundles" class="roundedCorners" style="display:none;background:white;padding:10px;width:500px;height:auto;"></div>


            <p><c:choose>
                    <c:when test='${empty action || action == "Add" }'>
                        <button name="submitType" id="submitType" type="submit" value="add">Add</button>
                    </c:when>
                    <c:otherwise>
                        <button name="submitType" id="submitType" type="submit"
                                value="update">Update</button>
                    </c:otherwise>
                </c:choose>
                &nbsp;<a href="/config-ui/config/main">Cancel</a>
            </p>
    </form:form>


    <c:choose>
        <c:when test='${empty action || action == "Add" }'>
        </c:when>
        
        <c:otherwise>
            <ul class="tabs" style="width:100%">
                <li id="listtab1"><a href="#tab1" onclick="window.location.href='#tab1';">Addresses</a></li>
                <li id="listtab2"><a href="#tab2" onclick="window.location.href='#tab2';">Anchors</a></li>
                <li id="listtab3"><a href="#tab3" onclick="window.location.href='#tab3';">Trust Bundles</a></li>
            </ul>
            <div class="container">
                <div class="tab_container">
                    <div id="tab1" class="tab_content">
                        

                    <div style="width:300px;float:right">

                        <fieldset style="width: 90%;" title="AddressesGroup"><legend><h3>Add New Address</h3></legend>
                            <fieldset style="width: 90%;" title="Address"><spring:url
                                    value="/config/domain/addaddress" var="formUrladdaddress" /> <form:form
                                    modelAttribute="addressForm"
                                    action="${fn:escapeXml(formUrladdaddress)}" cssClass="cleanform"
                                    method="POST">
                                    <form:hidden path="id" />

                                    <div class="quickform">

                                    <div>
                                    <label>Display Name:</label><br/>
                                    <form:errors path="displayName" cssClass="error" />
                                    <form:input path="displayName" />
                                    </div>
                                    
                                    <div>
                                    <label>Email Address:</label><br/>
                                    <form:errors path="emailAddress" cssClass="error" />
                                    <form:input path="emailAddress" />
                                    </div>

                                    <div>
                                    <label>Endpoint (XD* endpoint or email destination):</label><br/>
                                    <form:errors path="endpoint" cssClass="error" />
                                    <form:input path="endpoint" />
                                    </div>

                                    <div>
                                    <label>Status:</label><br/>
                                    <form:errors path="aStatus" cssClass="error" />
                                    <form:select path="aStatus">
                                        <form:options items="${statusList}" />
                                    </form:select>
                                    </div>

                                    <div>
                                    <label>Type:</label><br/>
                                    <form:errors path="type" cssClass="error" />
                                    <form:input path="type" />
                                    </div>

                                    </div>

                                   
                                    <button name="submitType" id="submitType" type="submit"
                                            value="newaddress">Add Address</button>
                            </form:form></fieldset>

                            </div>


            </c:otherwise>
        </c:choose>

                <c:if test="${not empty addressesResults}">
                <div style="">
                    
                    <spring:url
                            value="/config/domain/removeaddresses" var="formUrlremove" /> <form:form
                            modelAttribute="simpleForm" action="${fn:escapeXml(formUrlremove)}"
                            cssClass="cleanform" method="POST">
                            <form:hidden path="id" />

                            <div class="box" style="width:auto;margin-right:310px;">
                                <div class="header">
                                    <h3>Addresses</h3>
                                </div>
                                <div class="content no-padding" style="">
                                    <table id="table-domains" class="table" style="width:100%;font-size:12px;margin-bottom:0">
                                        <thead>
                                        <tr>
                                            <th width="10"></th>
                                            <th>Email Address</th>
                                            <th>Display Name</th>
                                            <th>Endpoint</th>
                                            <th>Type</th>
                                            <th>Status</th>

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
                                                 <td><form:checkbox path="remove"
                                                               value="${address.id}" /></td>
                                                <td><a
                                                        href='mailto:${address.emailAddress}'>${address.emailAddress}</a></td>
                                                <td><c:out value="${address.displayName}" /></td>
                                                <td><c:out value="${address.endpoint}" /></td>
                                                <td><c:out value="${address.type}" /></td>
                                                <td><c:out value="${address.status}" /></td>

                                            </tr>
                                        </c:forEach>
                                    </tbody>
                                    </table>
                                </div>
                            </div>

                            <div id="tablelist"  style="width:100%">
                                <table cellpadding="1px" cellspacing="1px" id="addressTable"
                                       class="fancyTable" style="font-size:12px;">
                                    

                                </table>
                            </div>
                            <!-- Wire this up to jQuery to add an input row to the table.
                                                     Don't submit it all until the final submit is done -->
                            <button name="submitType" id="submitType" type="submit" value="delete">Remove
            Selected Addresses</button>
                    </form:form>
</div>
<br clear="both"/>
                </c:if>
                </fieldset>
            </div>


            <div id="tab2" class="tab_content">
                <c:choose>
                    <c:when test='${empty action || action == "Add" }'>
                    </c:when>
                    <c:otherwise>

                        
                            <spring:url value="/config/domain/addanchor" var="formUrladdanchor" /> 
                            <form:form modelAttribute="anchorForm" action="${fn:escapeXml(formUrladdanchor)}" cssClass="cleanform" method="POST" enctype="multipart/form-data">
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

                                <div style="margin-bottom:10px">
                                    <button name="submitType" id="submitType" type="submit" value="newanchor">Add anchor</button>
                                </div>
                            </form:form>
                        </c:otherwise>
                    </c:choose>

                    <c:if test="${not empty anchorsResults}">



                        <spring:url value="/config/domain/removeanchors" var="formUrlremoveanchor" /> 
                        <form:form modelAttribute="anchorForm" action="${fn:escapeXml(formUrlremoveanchor)}" cssClass="cleanform" method="POST" enctype="multipart/form-data">
                        <form:hidden path="id" />

                        <div class="box" style="width:auto;margin-bottom:5px;">
                            <div class="header">
                                <h3>Anchors</h3>
                            </div>
                            <div class="content no-padding">


                                <table id="table-domains" class="table" style="width:100%;margin-bottom:0;font-size:12px;">
                                    <thead>
                                        <tr>
                                            <th></th>
                                            <th>Trusted Domain or User</th>
                                            <th>Owner</th>
                                            <th>Thumb</th>
                                            <th>Create</th>
                                            <th>Start</th>
                                            <th>End</th>
                                            <th>Stat</th>
                                            <th>In</th>
                                            <th>Out</th>

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
                                                <td><form:checkbox path="remove"
                                                               value="${anchors.id}" /></td>
                                                <td><c:out value="${anchors.trusteddomainoruser}" /></td>
                                                <td><a href='#tab2'>${anchors.owner}</a></td>
                                                <td><c:out value="${anchors.thumbprint}" /></td>
                                                <td><fmt:formatDate
                                                    value="${anchors.createTime.time}" pattern="MM/dd/yyyy, hh:mm" /></td>
                                                <td><fmt:formatDate
                                                    value="${anchors.validStartDate.time}" pattern="MM/dd/yyyy, hh:mm" /></td>
                                                <td><fmt:formatDate
                                                    value="${anchors.validEndDate.time}" pattern="MM/dd/yyyy, hh:mm" /></td>
                                                <td><c:out value="${anchors.status}" /></td>
                                                <td><c:out value="${anchors.incoming}" /></td>
                                                <td><c:out value="${anchors.outgoing}" /></td>

                                            </tr>
                                        </c:forEach>
                                    </tbody>

                                </table>
                            </div>
                        </div>                                
                        <!-- Wire this up to jQuery to add an input row to the table. Don't submit it all until the final submit is done -->
                        <button name="submitType" id="submitType" type="submit"
                                value="deleteanchors">Remove Selected Anchors</button>
                            

                               
                        </form:form>
                </c:if>
            </div>




            <div id="tab3" class="tab_content">
            
            <c:choose>
                    <c:when test='${empty action || action == "Add" }'>
                    </c:when>
                    <c:otherwise>

                <h3>Trust Bundles</h3>
                
                <div id="addMoreBundles" class="roundedCorners" style="display:none;background:white;padding:10px;width:500px;height:auto;"></div>

                <div style="margin:5px 0">
                    <a rel="leanModal" name="addMoreBundles" href="#addMoreBundles">Assign Additional Trust Bundles</a>
</div>

                <c:choose>
                
                <c:when test="${not empty trustBundles}">
                
                    

                    
                                <div class="box" style="width:auto;margin-bottom:5px;">
                                <div class="header">
                                    <h3>Bundles</h3>
                                </div>
                                <div class="content no-padding" style="">

                               
                                    <table id="table-domains" class="table" style="width:100%;margin-bottom:0;font-size:12px;">
<thead>
                        <tr>
                            <th><input type="checkbox" id="bundleCheckbox" onclick="selectAllBundles();"/></th>
                            <th>Bundle Name</th>
                            <th width=150>Anchor Certificates</th>
                            <th width=20>In</th>
                            <th width=20>Out</th>
                        </tr>

</thead>
                    <c:forEach var="trustBundle" items="${trustBundles}" varStatus="rowCounter">
                        <c:choose>
                            <c:when test="${rowCounter.count % 2 == 0}">
                                <tr class="evenRow">
                            </c:when>
                            <c:otherwise>
                                <tr class="oddRow">
                            </c:otherwise>
                        </c:choose>

                            <td width=10><input name="bundlesToRemove" type="checkbox" value="${trustBundle.trustBundle.id}"/></td>
                            <td>${trustBundle.trustBundle.bundleName}<br/>

                            </td>     
                            <td>                                  
                                 <a rel="leanModal" name="anchorList" href="#anchors_${trustBundle.trustBundle.id}">View Anchors</a>
                                    <div id="anchors_${trustBundle.trustBundle.id}" class="roundedCorners" style="display:none;background:white;padding:10px;width:350px;height:auto;">
                                    <div style="float:right"><a class="modal_close" style="cursor:pointer">Close</a></div>
                                    <br clear="both"/>
                                    <h3 style="color:black">Anchor List</h3>
                                    <div style="overflow:auto;height:250px;">
                                    <ul class="anchorList block-list"> 
                                    <c:forEach items="${bundleMap[trustBundle.trustBundle.bundleName]}" var="anchor">
                                        <li>${anchor.value}</li>
                                    </c:forEach>
                                    </ul>
                                    </div>
                                    </div>   
                            </td>
                            <td><input name="incoming" disabled="disabled" id="incoming_${trustBundle.id}" type="checkbox" <c:if test="${trustBundle.incoming}">checked="true"</c:if>/></td>
                            <td><input name="outgoing" disabled="disabled" id="outgoing_${trustBundle.id}" type="checkbox" <c:if test="${trustBundle.outgoing}">checked="true"</c:if>/></td>
                        </tr>
                    </c:forEach>
                    </table>
                    </div>                                
                                
                            </div>
                    <button name="submitType" id="submitType" type="button" value="assignBundles" onclick="removeBundles();">Remove Selected</button>

                

                </c:when>
                <c:otherwise>
                   There are no bundles associated with this domain.
                </c:otherwise>
                </c:choose>


            </div>

                </div>
            </div>
        </c:otherwise>
    </c:choose>

<%@ include file="/WEB-INF/jsp/footer.jsp"%>