/* 
 * Copyright (c) 2010, NHIN Direct Project
 * All rights reserved.
 *  
 * Redistribution and use in source and binary forms, with or without 
 * modification, are permitted provided that the following conditions are met:
 * 
 * 1. Redistributions of source code must retain the above copyright 
 *    notice, this list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright 
 *    notice, this list of conditions and the following disclaimer in the 
 *    documentation and/or other materials provided with the distribution.  
 * 3. Neither the name of the the NHIN Direct Project (nhindirect.org)
 *    nor the names of its contributors may be used to endorse or promote products 
 *    derived from this software without specific prior written permission.
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY 
 * EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED 
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE 
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY 
 * DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES 
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; 
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND 
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT 
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS 
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package org.nhindirect.xd.transform.util.type;

import org.apache.commons.lang.StringUtils;

/**
 * Enumeration of commonly used MIME types.
 * 
 * @author beau
 */
public enum MimeType
{
    /*
     * When adding new MimeType values, the DirectDocumentType class should be
     * updated with an appropriate new entry.
     */
    
    TEXT_PLAIN("text/plain", "txt"), 
    TEXT_XML("text/xml", "xml"), 
    TEXT_HTML("text/html", "html"),
    TEXT_CDA_XML("text/cda+xml", "xml"), 
    APPLICATION_CCR("application/ccr", "xml"), 
    APPLICATION_XML("application/xml", "xml"), 
    APPLICATION_PDF("application/pdf", "pdf"), 
    MULTIPART_MIXED("multipart/mixed", "???"),
    MULTIPART("multipart", "???"),
    UNKNOWN(null, "txt");

    private String type;
    private String suffix;

    /**
     * Enumeration constructor.
     * 
     * @param type
     *            The MIME type.
     */
    private MimeType(String type, String suffix)
    {
        this.type = type;
        this.suffix = suffix;
    }

    /**
     * Determine if the input matches the current element by first comparing
     * equalsIgnoreCase and then comparing startsWith.
     * 
     * @param type
     *            The MIME type to compare.
     * @return true if the string is a reasonable match, false otherwise.
     */
    public boolean matches(String type)
    {
        if (StringUtils.equalsIgnoreCase(type, this.type))
            return true;
        if (StringUtils.startsWith(type, this.type))
            return true;

        return false;
    }
    
    /**
     * Lookup a MimeType enumeration by type.
     * 
     * @param type
     *            The type to use for lookup.
     * @return the matching MimeType or UNKNOWN if not found.
     */
    public static MimeType lookup(String type)
    {
        for (MimeType m : values())
        {
            if (m.matches(type))
                return m;
        }
        
        return UNKNOWN;
    }

    /**
     * Return the type.
     * 
     * @return the type.
     */
    public String getType()
    {
        return type;
    }
    
    /**
     * Return the suffix.
     * 
     * @return the suffix.
     */
    public String getSuffix()
    {
        return suffix;
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Enum#toString()
     */
    @Override
    public String toString()
    {
        return type;
    }

}
