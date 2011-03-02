<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
         pageEncoding="ISO-8859-1"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
    <head>
        <%@ include file="/WEB-INF/jsp/include.jsp"%>
        <META ?http-equiv="Content-Type" ?content="text/html;charset=UTF-8">
            <title><fmt:message key="welcome.title" /></title>
    </head>

    <body>

        <%@ include file="/WEB-INF/jsp/header.jsp" %>
        <h2>Manage Domains </h2>
		
<p style="padding:5px;border:1px solid #bbb;background-color:#fcfccf;"><strong>Tip:</strong> You can return all available domains by simply hitting the search button.</p>
	
		
		<h4>Find Existing Domains</h4>
		
			

        <div id="form">
        
            <spring:url value="/config/main/search" var="formUrl"/>
            <form:form id="searchDomainForm" action="${fn:escapeXml(formUrl)}" cssClass="cleanform" commandName="searchDomainForm" method="GET">


                <table style="margin:10px;">
					<tr>
						<td width=120>
							<form:label path="domainName">Domain Name:
			                    <form:errors path="domainName" cssClass="error" />
			                </form:label>
			</td><td><form:input path="domainName" cssClass="text-input" cssStyle="width:220px;" /></td>
			</tr>
					<tr>
						<td><label>Status:</label></td>
						<td>
                    		<form:select path="status">
							              <form:option value="" label="All"/>
							              <form:options path="status" items="${statusList}"/>
							          </form:select>


			</td>
		</tr>
		<tr><td colspan=100% style="padding-top:5px"><button name="submitType" id="submitType" type="submit" value="search">Search</button> &nbsp; <a href="/config-ui/config/main/search?domainName=&submitType=newdomain">Create New Domain</a></td></tr>

</table>
                    	<!--<form:radiobuttons path="status" items="${statusList}"/>-->
                </div>

            </form:form>

			 <c:if test="${not empty searchResults}">

		            <h4>Domain Search Results</h4>

		            <div id="dynamic">
		                <spring:url value="/config/domain/remove" var="formUrlremove"/>
		                <form:form name="removeForm" modelAttribute="simpleForm" action="${fn:escapeXml(formUrlremove)}" method="POST" >
		                    <table class="data" id="domain-table">
		                        <thead>
		                            <tr>
		                                <th></th>
										<th>Name</th>
		                                <th>Postmaster</th>
		                                <th>Status</th>
		                                <th>Created</th>
		                                <th>Updated</th>
		                                
		                            </tr>
		                        </thead>
		                        <tbody>
		                            <!--  Put the data from the searchResults attribute here -->
		                            <c:forEach var="domain" items="${searchResults}" varStatus="rowCounter">
		                                <tr>
		                                    <spring:url value="/config/domain?id=${domain.id}" var="formUrlclick"/>
											<td><form:checkbox path="remove" value="${domain.id}" /></td>
		                                    
		<td><a href='${fn:escapeXml(formUrlclick)}'>${domain.domainName}</a></td>
		                                    <td>${domain.postMasterEmail}</td>
		                                    <td>${domain.status}</td>
		                                    <td><fmt:formatDate value="${domain.createTime.time}" pattern="MM/dd/yyyy, hh:mm"/></td>
		                                    <td><fmt:formatDate value="${domain.updateTime.time}" pattern="MM/dd/yyyy, hh:mm"/></td>
		                                    
		                                </tr>
		                            </c:forEach>
		                        </tbody>
		                        
		                    </table>
		                    <button name="submitType" id="submitType" type="submit" value="delete">Delete</button>
		                </form:form>
		            </div>
		        </c:if>
        
        </div>
       
        <%@ include file="/WEB-INF/jsp/footer.jsp"%>