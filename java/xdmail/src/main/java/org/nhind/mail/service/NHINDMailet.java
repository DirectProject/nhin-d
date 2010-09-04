package org.nhind.mail.service;

import java.util.logging.Logger;

import javax.mail.MessagingException;

import org.apache.mailet.Mail;
import org.apache.mailet.MailetConfig;
import org.apache.mailet.base.GenericMailet;

/**
 * An Apache James Mailet that converts clinical messages into IHE
 * Cross-Enterprise Document Reliability (XDR) messages and transmits them to an
 * XDR Document Recipient via IHE XDS.b Provide and Register transaction
 * (ITI-41).
 */
public class NHINDMailet extends GenericMailet {

    MailetConfig mc = null;
    
    /**
     * Class logger
     */
    private static final Logger LOGGER = Logger.getLogger(NHINDMailet.class.getPackage().getName());
    
    /*
     * Relax the OHT IHE Profiles CDA validation to allow meta-data extraction
     * to work correctly on CDA instances that carry CCD or HITSP C83 extensions
     */
    static {
        System.setProperty("relax.validation", "true");
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.apache.mailet.base.GenericMailet#service(org.apache.mailet.Mail)
     */
    @Override
    public void service(Mail mail) throws MessagingException {
        LOGGER.info("NHINDMailet receiving  mail");
        
        try {
            MimeXDSTransformer mxt = new MimeXDSTransformer();
            boolean forwardToXdr = true;// this should be based on some routing
            // lookup
            String endpoint = "http://localhost:8080/xd/services/DocumentRepository_Service";// ditto
            if (forwardToXdr) {
                mxt.forward(endpoint, mail.getMessage());
            } else {
                // forward it to another email server based on routing
                // iformation
            }
            mail.setState(Mail.GHOST);
        } catch (Throwable e) {
            e.printStackTrace();
            LOGGER.severe("NHINDMailet delivery failure" + e.getMessage());
            throw new RuntimeException(e);
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.apache.mailet.base.GenericMailet#init()
     */
    @Override
    public void init() throws MessagingException {
        System.out.println("TEST MAILET");
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.apache.mailet.base.GenericMailet#init(org.apache.mailet.MailetConfig)
     */
    @Override
    public void init(MailetConfig newConfig) throws MessagingException {
        System.out.println("TEST MAILET CONFIG INIT");
    }

}
