<div id="header">
    <a href="http://nhindirect.org/" id="bannerLeft">
        <img src="<c:url value="/resources/images/direct_logo.png" />" alt="Direct Project" border="0" />
    </a>
    <div class="clear"></div>
</div>

<div class="logout-bar">
    <sec:authorize ifAllGranted="ROLE_ADMIN">
        <table width="100%">
            <tr>
                <td align="left">
                    <a href="<c:url value="/config/main"/>">Manage Domains</a> &middot;
                    <a href="<c:url value="/config/main/search?domainName=&submitType=gotosettings"/>">Settings</a> &middot;
                    <a href="<c:url value="/config/main/search?domainName=&submitType=gotocertificates"/>">Manage Certificates</a> &middot;
                    <a href="<c:url value="/config/main/search?domainName=&submitType=gotodns"/>">DNS Entries</a>
                </td>
                <td align="right">You are logged in. <button name="logoutBtn" id="logoutBtn" type="submit" onclick="document.location.href='<c:url value="/j_spring_security_logout"/>';">Log out</button></td>
        </table>

    </sec:authorize>
</div>


<div id="main">