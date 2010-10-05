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

package org.nhindirect;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;

/**
 * Abstract representation of a message sent via Direct.
 * 
 * @author beau
 */
public class DirectMessage
{
    private String sender;
    private Collection<String> receivers;
    private Collection<DirectDocument> documents;
    private String body;
    private String subject;

    public String getSender()
    {
        return sender;
    }

    public DirectMessage(String sender, Collection<String> receivers)
    {
        this.sender = sender;
        this.receivers = receivers;

        this.documents = new HashSet<DirectDocument>();
    }

    public void addDocument(String document)
    {
        DirectDocument doc = new DirectDocument();
        DirectDocument.Metadata metadata = doc.new Metadata();

        doc.setData(document);
        doc.setMetadata(metadata);

        documents.add(doc);
    }

    public void addDocument(String document, String meta)
    {
        DirectDocument doc = new DirectDocument();
        DirectDocument.Metadata metadata = doc.new Metadata();

        doc.setData(document);

        metadata.setXml(meta);
        doc.setMetadata(metadata);

        documents.add(doc);
    }

    public void setSender(String sender)
    {
        this.sender = sender;
    }

    public Collection<String> getReceivers()
    {
        return receivers;
    }

    public void setReceivers(Collection<String> receivers)
    {
        this.receivers = receivers;
    }

    public Collection<DirectDocument> getDocuments()
    {
        return Collections.unmodifiableCollection(documents);
    }

    public String getBody()
    {
        return body;
    }

    public void setBody(String body)
    {
        this.body = body;
    }

}
