<!--<div id="header">
    
    <div class="clear"></div>
</div>-->

<div id="header_main">
<a href="<c:url value="/config/main"/>" id="bannerLeft"><img src="<c:url value="/resources/images/direct-project-logo2.png" />" alt="Direct Project" border="0" /></a>
</div>
<div id="nav_sub">
 <sec:authorize ifAllGranted="ROLE_ADMIN">
    <ul id="nav_sub_links">
        <li><a href="<c:url value="/config/main"/>">Manage Domains</a></li>
        <li><a href="<c:url value="/config/main/search?domainName=&submitType=gotosettings"/>">Agent Settings</a></li>
        <li><a href="<c:url value="/config/main/search?domainName=&submitType=gotocertificates"/>">Certificates</a></li>
        <li><a href="<c:url value="/config/main/search?domainName=&submitType=gotodns"/>">DNS Entries</a></li>
        <li><a href="<c:url value="/config/main/search?domainName=&submitType=ManageTrustBundles"/>">Trust Bundles</a></li>
        <li><a href="<c:url value="/config/main/search?domainName=&submitType=ManagePolicies"/>">Policies</a></li>
    </ul>

    <div style="float:right;line-height: 38px;padding-right:10px;">
    You are logged in. <a href="<c:url value="/j_spring_security_logout"/>">Log out</a>
</div>
    </sec:authorize>
</div>

<div id="main">