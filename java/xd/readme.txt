Greetings

BUILD PROCESS

In order to run the XDR/XDM unit tests in a standalone java jdk,The java mail api must be in the classpath. The simplest way I know how to do this is 
to download the zipped api from:

 http://www.oracle.com/technetwork/java/index-138643.html

then get the mail.jar in the zip and place it into your $JAVA_HOME/jre/lib/ext directory.  

Currently there is one primary junit test which operates on the XDR class during
the "maven install" build. The java\xd\src\main\resources\request.xml is the source for the XDR
ProvideAndRegister message. By editting the email portion (currently vlewis@lewistower.com) of the intendedRecipient slot
in this file you can insert a test email address where the test XDM message will be sent.
Currently the entire field looks like this

  <rim:Slot name="intendedRecipient">
             <rim:ValueList>
  			   <rim:Value>|vlewis@lewistower.com^Smith^John^^^Dr^^^&amp;1.3.6.1.4.1.21367.3100.1&amp;ISO</rim:Value>
              </rim:ValueList>
   </rim:Slot>

If this value is used instead,
 <rim:Value>|john.smith@happyvalleyclinic.nhindirect.org^Smith^John^^^Dr^^^&amp;1.3.6.1.4.1.21367.3100.1&amp;ISO</rim:Value>
The XDR service acts as a forward and sends the ProvideAndRegister message on to another XDR service which is currently:

http://shinnytest.gsihealth.com:8080/DocumentRepository_Service/DocumentRepository?wsdl";

It is envisioned that the relationship between NHIND ID (the email address in the recipient value) and the type of delivery (XDM or XDR forward)
will be persisted in some provider addressing mechanism that is TBD to my knowledge



DEPLOYMENT in TOMCAT

The deployment has been tested using apache-tomcat-6.0.29 . This can be downloaded from http://tomcat.apache.org/download-60.cgi. Once Tomcat 
is installed and running, the xd.war file can be copied from the hg\java\xd\target directory to the TOMCAT_HOME\webapps directory where it will 
hot-deploy automatically. This deployment runs on port 8080 as is. 

This implementation can be tested out using the SOAP-UI test dirctory \hg\java\xd\soapui.  Currently only the ccd test is working (not the pdf) . 

Vince Lewis