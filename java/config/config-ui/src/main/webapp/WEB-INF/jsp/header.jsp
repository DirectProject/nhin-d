<div id="header">
    <a href="<c:url value="/config/main"/>" id="bannerLeft">
        <img src="<c:url value="/resources/images/direct_logo.png" />" alt="Direct Project" border="0" />
    </a>
    <div class="clear"></div>
</div>

<div class="logout-bar">
    <sec:authorize ifAllGranted="ROLE_ADMIN">
        <table width="100%" style="margin:0">
            <tr>
                <td align="left">
                    <ul class="ul-navbar">
						<!--<li><a href="<c:url value="/config"/>">Home</li>-->
						<li><a href="<c:url value="/config/main"/>">Manage Domains</a></li>
						<li><a href="<c:url value="/config/main/search?domainName=&submitType=gotosettings"/>">Agent Settings</a></li>
						<li><a href="<c:url value="/config/main/search?domainName=&submitType=gotocertificates"/>">Certificates</a></li>
						<li><a href="<c:url value="/config/main/search?domainName=&submitType=gotodns"/>">DNS Entries</a></li>
					</ul>

                    
                </td>
                <td align="right">You are logged in. <button name="logoutBtn" id="logoutBtn" type="submit" onclick="document.location.href='<c:url value="/j_spring_security_logout"/>';">Log out</button></td>
        </table>

    </sec:authorize>
</div>


<div id="main">