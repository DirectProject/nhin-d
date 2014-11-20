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

package org.nhindirect.xd.soap.type;

import org.apache.commons.lang.StringUtils;

/**
 * Enumeration representing valid metadata levels for the direct:metadata-level
 * SOAP header.
 * 
 * @author beau
 */
public enum MetadataLevelEnum
{
    MINIMAL("minimal"), 
    XDS("XDS");

    private String level;

    private MetadataLevelEnum(String level)
    {
        this.level = level;
    }

    /**
     * Return the value of level.
     * 
     * @return the value of level.
     */
    public String getLevel()
    {
        return level;
    }

    /**
     * Return the MetadataLevelEnum object matching the provided level.
     * 
     * @param level
     *            The level to use for the lookup.
     * @return the MetadataLevelEnum object matching the provided level, or null
     *         if not found.
     */
    public MetadataLevelEnum lookup(String level)
    {
        for (MetadataLevelEnum e : values())
        {
            if (StringUtils.equalsIgnoreCase(e.getLevel(), level))
                return e;
        }

        return null;
    }
}
