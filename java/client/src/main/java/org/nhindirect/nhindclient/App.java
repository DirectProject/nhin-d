package org.nhindirect.nhindclient;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.UUID;

/**
 * Hello world!
 *
 */
public class App {

    public static void main(String[] args) {
        App app = new App();
        String endpoint = args[0];
        app.run(endpoint);


    }

    void run(String endpoint) {
        try {
            String doc = getDoc();
            String meta = getMeta();
            ArrayList docs = new ArrayList();
            docs.add(doc);
            NHINDClient ndc = new NHINDClient();
            String messageId = UUID.randomUUID().toString();

            ndc.sendRefferal(endpoint, meta, docs, messageId);
        } catch (Exception x) {
            x.printStackTrace();
        }
    }

    private String getDoc() throws Exception {
        InputStream is = this.getClass().getResourceAsStream("/META-INF/main/resources/CCD.xml");
        byte[] theBytes = new byte[is.available()];
        is.read(theBytes);
        return new String(theBytes);

    }

    private String getMeta() throws Exception {
        InputStream is = this.getClass().getResourceAsStream("/META-INF/main/resources/meta.xml");
        byte[] theBytes = new byte[is.available()];
        is.read(theBytes);
        return new String(theBytes);

    }
}
