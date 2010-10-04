/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.nhindirect.nhindclient;

import java.util.ArrayList;
import java.util.UUID;
import org.apache.commons.lang.StringUtils;
import org.nhindirect.xdclient.XDClient;
import org.nhindirect.xdm.XDMMailClient;

/**
 *
 * @author vlewis
 */
public class NHINDClient {
    
      public String sendRefferal(String endpoint, String metadata, ArrayList docs, String messageId) throws Exception {
         //TODO not sure if the endpoint should be a param or derived from the metadata
        if (StringUtils.isBlank(endpoint)) {
            throw new IllegalArgumentException("Endpoint must not be blank");
        }
        if (metadata == null) {
            throw new IllegalArgumentException("metadata must not be null");
        }
        if (docs == null) {
            throw new IllegalArgumentException("metadata must not be null");
        }
        String response = null;


        if (endpoint.indexOf('@') > 0) {
           XDMMailClient xmc = new XDMMailClient("localhost");

           String body = "data attached";
           //TODO fix these two to use metadata
           String from = "vlewis@lewistower.com";
           String recipient = endpoint;          
           ArrayList to = new ArrayList();
           to.add(recipient);

           String suffix = "xml";
           xmc.sendMail( messageId, from , to , metadata,  body, docs, suffix);
           response = "urn:oasis:names:tc:ebxml-regrep:ResponseStatusType:Success";
        } else {
           XDClient xdc = new XDClient();
           response = xdc.sendRequest(endpoint, metadata, docs);
        }
        return response;
      }
   
}
