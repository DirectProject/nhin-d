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

package org.nhindirect.xd.common;

import java.io.File;
import java.util.Collection;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Abstract representation of a message sent via Direct.
 * 
 * @author beau
 */
public class DirectMessage
{
    private String sender;
    private Collection<String> receivers;
    private DirectDocuments directDocuments;
    private String body;
    private String subject;

    @SuppressWarnings("unused")
    private static final Log LOGGER = LogFactory.getFactory().getInstance(DirectMessage.class);

    /**
     * Create a new DirectMessage object.
     * 
     * @param sender
     *            The value of sender.
     * @param receivers
     *            The value of receivers.
     */
    public DirectMessage(String sender, Collection<String> receivers)
    {
        this.sender = sender;
        this.receivers = receivers;

        this.directDocuments = new DirectDocuments();
    }

    /**
     * Add a document to the message.
     * 
     * @param document
     *            The document to add.
     */
    @Deprecated
  //  public void addDocument(String document)
   // {
  //      DirectDocument2 doc = new DirectDocument2();
  //      doc.setData(document);

  //      directDocuments.getDocuments().add(doc);
   // }

    public void addDocument(File file) throws Exception
    {
        DirectDocument2 document = new DirectDocument2(file);

        directDocuments.getDocuments().add(document);
    }

    public void addDocument(DirectDocument2 document)
    {
        directDocuments.getDocuments().add(document);
    }

    /**
     * Get the value of sender.
     * 
     * @return the value of sender.
     */
    public String getSender()
    {
        return sender;
    }

    /**
     * Set the value of sender.
     * 
     * @param sender
     *            The value of sender.
     */
    public void setSender(String sender)
    {
        this.sender = sender;
    }

    /**
     * Get the value of receivers.
     * 
     * @return the value of receivers.
     */
    public Collection<String> getReceivers()
    {
        return receivers;
    }

    /**
     * Set the value of receivers.
     * 
     * @param receivers
     *            The value of receivers.
     */
    public void setReceivers(Collection<String> receivers)
    {
        this.receivers = receivers;
    }

    /**
     * Get the value of body.
     * 
     * @return the body.
     */
    public String getBody()
    {
        return body;
    }

    /**
     * Set the value of body.
     * 
     * @param body
     *            The body to set.
     */
    public void setBody(String body)
    {
        this.body = body;
    }

    /**
     * Set the value of subject.
     * 
     * @param subject
     *            The subject to set.
     */
    public void setSubject(String subject)
    {
        this.subject = subject;
    }

    /**
     * Get the value of subject.
     * 
     * @return the subject.
     */
    public String getSubject()
    {
        return subject;
    }

    /**
     * @return the directDocuments
     */
    public DirectDocuments getDirectDocuments()
    {
        return directDocuments;
    }

    /**
     * @param directDocuments
     *            the directDocuments to set
     */
    public void setDirectDocuments(DirectDocuments directDocuments)
    {
        this.directDocuments = directDocuments;
    }

}
