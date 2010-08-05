Greetings

Currently there is one primary junit test which operates on the XDR class during
the build. The java\xd\src\main\resources\request.xml is the source for the XDR
ProvideAndRegister message. By editting the email portion of the intendedRecipient slot
in this file you can insert a test email address where the test XDM message will be sent.
Currently the field looks like this

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

Vince Lewis