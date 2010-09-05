Deployment of xdmail in James

The jar created by this project needs to be deployed to JAMES_HOME/apps/james/SAR-INF/lib along with two other jars :
    webservices-rt.jar
    apache-mailet-base-1.1.jar

Users need to be added to James of course. The James config file (in JAMES_HOME/apps/james/SAR-INF) needs two additional tags:

Mailet package tag

<mailetpackages>
      <mailetpackage>org.apache.james.transport.mailets</mailetpackage>
      <mailetpackage>org.apache.james.transport.mailets.smime</mailetpackage>
<!-- ADDED THIS TAG -->
	  <mailetpackage>org.nhind.mail.service</mailetpackage>
   </mailetpackages>

For testing purposes I added a matcher tag something like this in the root processor

         <!-- This mailet redirects mail for the user 'postmaster' at any local domain to -->
         <!-- the postmaster address specified for the server. The postmaster address -->
         <!-- is required by rfc822. Do not remove this mailet unless you are meeting -->
         <!-- this requirement through other means (e.g. a XML/JDBCVirtualUserTable mailet) -->
         <mailet match="All" class="PostmasterAlias"/>
       <!-- ADDED THIS TAG -->
         <mailet match="RecipientIs=jsmith@smith.com" class="NHINDMailet"/>

This allows the combined application suite in James and Tomcat (see xd project) to receive an SMTP message
to this recipient with either an XDM attachment, a pure CDA/CCD attachment, or a CDA/CCD in the body, step
it up to an XDR, and forward it to the local XDR running in tomcat where it will be converted to an XDM and
forwarded to the configured recipient, in this case jsmith@smith.com.

Once the routing database is developed, this mechanism will be configurable by recipient based routing endpoint.
For example an SMTP message to jsmith@smith.com can be relayed directly as smtp (email endpoint) or stepped up
to an XDR or XDD (SOAP endpoint).The SOAP message is then relayed to the designated endpoint (HIST) where it can either
be handled, relayed to another HIST (e.g. an EHR) or converted to an XDM and relayed again via SMTP.


