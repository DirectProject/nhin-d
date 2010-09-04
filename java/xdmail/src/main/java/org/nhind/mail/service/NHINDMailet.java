

package org.nhind.mail.service;



import ihe.iti.xds_b._2007.ProvideAndRegisterDocumentSetRequestType;
import javax.mail.MessagingException;
import java.net.URI;
import java.net.URISyntaxException;
import java.text.MessageFormat;
import java.util.logging.Logger;
import javax.xml.namespace.QName;
import org.apache.mailet.Mail;
import org.apache.mailet.MailetConfig;
import org.apache.mailet.base.GenericMailet;
import org.nhind.util.XMLUtils;

/**
 * An Apache James Mailet that converts clinical messages into IHE Cross-Enterprise Document Reliability (XDR)
 * messages and transmits them to an XDR Document Recipient via IHE XDS.b Provide and Register transaction (ITI-41).
 */
public class NHINDMailet extends GenericMailet {

      private static final Logger LOGGER = Logger.getLogger(NHINDMailet.class.getPackage().getName());
    MailetConfig mc =null;

    

    /**
     * Relax the OHT IHE Profiles CDA validation to allow meta-data extraction to work correctly on
     * CDA instances that carry CCD or HITSP C83 extensions
     */
    static {
        System.setProperty("relax.validation", "true");
    }

  

      @Override
    public void service(Mail mail) throws MessagingException {

        try {
             System.out.println("TEST MAILET");
              LOGGER.info("XDRMailet receiving  mail");
              MimeXDSTransformer mxt = new MimeXDSTransformer();
              boolean forwardToXdr = true;// this should be based on some routing lookup
              String endpoint = "http://localhost:8080/xd/services/DocumentRepository_Service";// ditto
              if(forwardToXdr){
                mxt.forward(endpoint , mail.getMessage());
              }else{
                //forward it to another email server based on routing iformation
              }
              mail.setState(Mail.GHOST);
        } catch (Throwable e) {
            e.printStackTrace();
            LOGGER.severe("XDRMailet deliver failure" + e.getMessage());
            throw new RuntimeException(e);
        }
    }
    @Override
    public void init() throws MessagingException {

     System.out.println("TEST MAILET");


    }
     @Override
    public void init(MailetConfig newConfig) throws MessagingException {

     System.out.println("TEST MAILET CONFIG INIT");


    }


}
