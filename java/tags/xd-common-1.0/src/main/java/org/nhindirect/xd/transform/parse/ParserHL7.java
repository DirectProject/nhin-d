/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.nhindirect.xd.transform.parse;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import org.apache.commons.lang.StringUtils;
import org.nhindirect.xd.common.DirectDocuments;

/**
 *
 * @author vlewis
 */
public class ParserHL7 {
     private static final HashMap<String, String> siemens = new HashMap<String, String>();
    static {
        siemens.put("07210", "Ferdinand.Vendetti@allscript.direct.com");
        siemens.put("07311", "Mary.Ehlers@allscript.direct.com");
        siemens.put("100001", "Eddy.Ropside@allscript.direct.com");
        siemens.put("100002", "Joseph.Hoffman@allscript.direct.com");
        siemens.put("100003", "Knapp.Julie@allscript.direct.com");
        siemens.put("100004", "McDermott.Thomas@allscript.direct.com");
        siemens.put("pknapp", "Patrick.Knapp@allscript.direct.com");
    }


    public static List<String> parseRecipients(DirectDocuments documents) {

        List<String> ret = new ArrayList();
        for (String recipient : documents.getSubmissionSet().getIntendedRecipient()) {
            if(recipient.startsWith("|")){
                String address = StringUtils.remove(recipient, "|");
                ret.add(StringUtils.splitPreserveAllTokens(address, "^")[0]);
            }else{
                String id = getId(recipient);
                ret.add(id);
            }
        }
        return ret;
    }
    public static String getId(String recipient){
        String ret = null;
        byte[] testp = recipient.getBytes();
        int pcount = 0;
        for (byte p : testp) {

            if ((char) p == '|') {
                pcount++;
            }
        }
        System.out.println("count = " + pcount);
        if (pcount > 1) {
            String address = StringUtils.splitPreserveAllTokens(recipient, "|")[2];
            String test = StringUtils.splitPreserveAllTokens(address, "^")[3];
            int stop = test.indexOf("<");
            if (stop > 0) {
                test = test.substring(0, stop);
            }
            System.out.println(test);
            ret = test;
        } else {
            String address = StringUtils.splitPreserveAllTokens(recipient, "|")[1];
            String test = StringUtils.splitPreserveAllTokens(address, "^")[0];
            System.out.println(test);
            ret = test;
        }
        System.out.println("about to test ret " + ret);

         //vpl strictly test

        if(siemens.containsKey(ret)){
            ret=siemens.get(ret);
        }
       
        return ret;
    }
}
