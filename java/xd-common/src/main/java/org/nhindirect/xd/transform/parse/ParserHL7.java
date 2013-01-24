/* 
Copyright (c) 2010, NHIN Direct Project
All rights reserved.

Authors:
   Vincent Lewis     vincent.lewis@gsihealth.com
 
Redistribution and use in source and binary forms, with or without modification, are permitted provided that the following conditions are met:

Redistributions of source code must retain the above copyright notice, this list of conditions and the following disclaimer.
Redistributions in binary form must reproduce the above copyright notice, this list of conditions and the following disclaimer 
in the documentation and/or other materials provided with the distribution.  Neither the name of the The NHIN Direct Project (nhindirect.org). 
nor the names of its contributors may be used to endorse or promote products derived from this software without specific prior written permission.
THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, 
THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS 
BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE 
GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, 
STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF 
THE POSSIBILITY OF SUCH DAMAGE.
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
 //    private static final HashMap<String, String> siemens = new HashMap<String, String>();
   
// based on old XDR stuff
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

     public static List<String> parseDirectRecipients(DirectDocuments documents) {

        List<String> ret = new ArrayList();
        for (String recipient : documents.getSubmissionSet().getIntendedRecipient()) {

                String address = StringUtils.splitPreserveAllTokens(recipient, "|")[2];
                ret.add(parseXTN(address));

        }
        return ret;
    }
    public static String parseXTN(String address){
        return StringUtils.splitPreserveAllTokens(address, "^")[3];
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

     
       
        return ret;
    }
}
