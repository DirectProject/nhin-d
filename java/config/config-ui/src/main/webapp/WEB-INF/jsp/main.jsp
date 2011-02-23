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
        <h3>Manage Domains</h3>

		<a href="/config-ui/config/main/search?domainName=&submitType=newdomain">Create a new domain</a>
		<br/>

		<br/>

        <div id="form">
        <fieldset>
            <spring:url value="/config/main/search" var="formUrl"/>
            <form:form id="searchDomainForm" action="${fn:escapeXml(formUrl)}" cssClass="cleanform" commandName="searchDomainForm" method="GET">


         		<fieldset>
				<legend style="font-weight:bold">Search for Existing Domains:</legend>
                <table>
					<tr>
						<td width=120>
							<form:label path="domainName">Domain Name:
			                    <form:errors path="domainName" cssClass="error" />
			                </form:label>
			</td><td><form:input path="domainName" cssClass="text-input" /></td>
			</tr>
					<tr>
						<td><label>Search filter:</label></td>
						<td>
                    		<form:select path="status">
							              <form:option value="" label="Please Select"/>
							              <form:options path="status" items="${statusList}"/>
							          </form:select>


			</td>
		</tr>
		<tr><td colspan=100%><button name="submitType" id="submitType" type="submit" value="search">Search</button></td></tr>

</table></fieldset>
                    	<!--<form:radiobuttons path="status" items="${statusList}"/>-->
                </div>

            </form:form>
        </fieldset>
        </div>
        <c:if test="${not empty searchResults}">

            <h3>Domain Search Results</h3>

            <div id="dynamic">
                <spring:url value="/config/domain/remove" var="formUrlremove"/>
                <form:form name="removeForm" modelAttribute="simpleForm" action="${fn:escapeXml(formUrlremove)}" cssClass="cleanform" method="POST" >
                    <table class="tablesorter" id="domain-table">
                        <thead>
                            <tr>
                                <th>Name</th>
                                <th>Postmaster</th>
                                <th>Status</th>
                                <th>Created</th>
                                <th>Updated</th>
                                <th>Remove</th>
                            </tr>
                        </thead>
                        <tbody>
                            <!--  Put the data from the searchResults attribute here -->
                            <c:forEach var="domain" items="${searchResults}" varStatus="rowCounter">
                                <tr>
                                    <spring:url value="/config/domain?id=${domain.id}" var="formUrlclick"/>
                                    <td><a href='${fn:escapeXml(formUrlclick)}'>${domain.domainName}</a></td>
                                    <td>${domain.postMasterEmail}</td>
                                    <td>${domain.status}</td>
                                    <td><fmt:formatDate value="${domain.createTime.time}" pattern="MM/dd/yyyy, hh:mm"/></td>
                                    <td><fmt:formatDate value="${domain.updateTime.time}" pattern="MM/dd/yyyy, hh:mm"/></td>
                                    <td><form:checkbox path="remove" value="${domain.id}" /></td>

                                </tr>
                            </c:forEach>
                        </tbody>
                        <tfoot>
                            <tr>
                                <th>Name</th>
                                <th>Postmaster</th>
                                <th>Status</th>
                                <th>Created</th>
                                <th>Updated</th>
                                <th>Remove</th>
                            </tr>
                        </tfoot>
                    </table>
                    <button name="submitType" id="submitType" type="submit" value="delete">Delete</button>
                </form:form>
            </div>
        </c:if>
        <%@ include file="/WEB-INF/jsp/footer.jsp"%>