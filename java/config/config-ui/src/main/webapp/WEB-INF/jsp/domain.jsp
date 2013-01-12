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


               

            });

         
            $(function() {                
                $('a[rel*=leanModal]').leanModal({ top : 200, closeButton: ".modal_close" });		
            });

        
             function selectAllBoxes() {

                    var checkBoxes = $(':checkbox[name=bundlesSelected]'); 

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
            <div id="assignBundles" style="display:none;background:white;padding:10px;width:500px;height:auto;"></div>


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
                    <div style="width:auto;max-width:730px;float:left;">
                        <h3>Addresses</h3>
                        <fieldset style="width: 95%;" title="Addresses"><spring:url
                                value="/config/domain/removeaddresses" var="formUrlremove" /> <form:form
                                modelAttribute="simpleForm" action="${fn:escapeXml(formUrlremove)}"
                                cssClass="cleanform" method="POST">
                                <form:hidden path="id" />
                                <div id="tablelist"  style="width:100%;">
                                    <table cellpadding="1px" cellspacing="1px" id="addressTable"
                                           class="fancyTable" style="font-size:12px;width:100%;">
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
                                                            href='../address?id=<c:out value="${address.id}"/>'>${address.emailAddress}</a></td>
                                                    <td><c:out value="${address.displayName}" /></td>
                                                    <td><c:out value="${address.endpoint}" /></td>
                                                    <td><c:out value="${address.type}" /></td>
                                                    <td><c:out value="${address.status}" /></td>
                                                   
                                                </tr>
                                            </c:forEach>
                                        </tbody>
                                        
                                    </table>
                                </div>
                                <!-- Wire this up to jQuery to add an input row to the table.
					                 Don't submit it all until the final submit is done -->
                                <button name="submitType" id="submitType" type="submit" value="delete">Remove
		Selected Addresses</button>
                        </form:form></fieldset>
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

                        <fieldset style="width: 99%;" title="anchorgroup"><legend><h3>Anchors</h3></legend>
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
                                    <table id="anchorsTable"
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
            <div id="tab3" class="tab_content">

                <h3>Trust Bundles</h3>

                <c:choose>
                
                <c:when test="${not empty trustBundles}">
                <table class="fancyTable" width=100% style="font-size:12px">
                    <tr>
                        <th width=10><input type="checkbox" id="bundleCheckbox" onclick="selectAllBoxes();"/></th>
                        <th>Bundle Name</th>
                        <th>Anchors</th>
                        <th width=20>In</th>
                        <th width=20>Out</th>
                    </tr>

                
                <c:forEach var="trustBundle" items="${trustBundles}" varStatus="rowCounter">
                    <c:choose>
                        <c:when test="${rowCounter.count % 2 == 0}">
                            <tr class="evenRow">
                        </c:when>
                        <c:otherwise>
                            <tr class="oddRow">
                        </c:otherwise>
                    </c:choose>

                        <td width=10><input name="bundlesSelected" type="checkbox" value="${trustBundle.trustBundle.id}|${trustBundle.trustBundle.bundleName}"/></td>
                        <td>${trustBundle.trustBundle.bundleName}<br/>

                        </td>     
                        <td> 
                                <c:forEach items="${anchorMap[trustBundle.trustBundle.bundleName]}" var="anchor">
                                    ${anchor.data}<br/>
                                </c:forEach>

                        </td>
                        <td></td>
                        <td></td>
                    </tr>
                </c:forEach>
                </table>
                <button name="submitType" id="submitType" type="button" value="assignBundles" onclick="selectBundles();">Remove Selected</button>
                </c:when>
                <c:otherwise>
                   There are no bundles associated with this domain.
                </c:otherwise>
                </c:choose>


            </div>

            <c:choose>
                <c:when test='${empty action || action == "Add" }'>
                </c:when>
                <c:otherwise>
                </div>
            </div>
        </c:otherwise>
    </c:choose>

<%@ include file="/WEB-INF/jsp/footer.jsp"%>