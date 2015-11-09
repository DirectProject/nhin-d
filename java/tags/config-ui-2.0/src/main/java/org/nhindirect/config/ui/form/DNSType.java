package org.nhindirect.config.ui.form;
/* 
Copyright (c) 2010, Direct Project
All rights reserved.

Authors:
   Pat Pyette     ppyette@inpriva.com
 
Redistribution and use in source and binary forms, with or without modification, are permitted provided that the following conditions are met:

Redistributions of source code must retain the above copyright notice, this list of conditions and the following disclaimer.
Redistributions in binary form must reproduce the above copyright notice, this list of conditions and the following disclaimer in the documentation and/or other materials provided with the distribution.
Neither the name of The Direct Project (directproject.org) nor the names of its contributors may be used to endorse or promote products derived from this software without specific prior written permission.
THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.

*/


import java.util.ArrayList;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public enum DNSType
{
    A     (1),
    CNAME (5),
    SOA   (6),
    MX    (15),
    AAAA  (28),
    SRV   (33),
    CERT  (37),
    NS    (2),
    SPF   (99)
    ;
    
    private static final Map<Integer,DNSType> lookup = new HashMap<Integer, DNSType>();
    private static final Map<String, DNSType> sLookup = new HashMap<String, DNSType>();
    
    static 
    {
        for(DNSType s : EnumSet.allOf(DNSType.class))
        {
            lookup.put(s.getValue(), s);
            sLookup.put(s.toString(), s);
        }    
    }
    
    private int value;
    
    private DNSType(int value)
    {
        this.value = value;
    }
    
    public int getValue()
    {
        return value;
    }
    
    public static DNSType get(int code) { 
        return lookup.get(code); 
    }
        
    public static DNSType get(String value) {
        return sLookup.get(value);   
    }
    
    public static List<DNSType> getAll() {
        ArrayList<DNSType> result = new ArrayList<DNSType>(lookup.values());
        return result;
    }
            
}