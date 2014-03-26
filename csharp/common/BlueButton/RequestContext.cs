/* 
 Copyright (c) 2013, Direct Project
 All rights reserved.

 Authors:
    Umesh Madan     umeshma@microsoft.com
  
Redistribution and use in source and binary forms, with or without modification, are permitted provided that the following conditions are met:

Redistributions of source code must retain the above copyright notice, this list of conditions and the following disclaimer.
Redistributions in binary form must reproduce the above copyright notice, this list of conditions and the following disclaimer in the documentation and/or other materials provided with the distribution.
Neither the name of The Direct Project (directproject.org) nor the names of its contributors may be used to endorse or promote products derived from this software without specific prior written permission.
THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 
*/
using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Net.Mail;
using System.IO;
using Health.Direct.Common.Mime;
using Health.Direct.Common.Mail;

namespace Health.Direct.Common.BlueButton
{
    /*
        Blue Button recommends that a Direct message may have an associated Request context. 

     The request context may contain metadata about the message's contents, such as attachments. 
        
     ** General Format **
        - A message’s request context is contained in a RequestContext document
        - The RequestContext document is a UTF8 encoded text document
        - The document is attached to the message with the name request.txt
        - The document is a collection of RFC5322 fields (also called “Headers”)
            Line folding and other features of fields are fully supported      
       Fields:
        - Field names are case-insensitive
        - A Field's value may have multiple Field Parameters

     ** Document-Source field **
     Use the Document-Source field to supply the SOURCE of documents attached to a message.
        E.g.
        Document-Source: name="visitCCD.xml";source="toby.mcduff@direct.hospital.com"
        Document-Source: name="healthSummaryCCD.xml";source="MIXED"
        Document-Source: name="labResult.pdf";source="Toby Labs"
        Document-Source: name="Notes.txt";source="UNKNOWN"

     The VALUE of a Document-Source Field contains one or more field parameters seperated by a semi-colon.
        - Field parameters are name value pairs, seperated by a ';'
            E.g. name1="one";name2="two"
       
            parameter := name "=" quotedText            
            name := 
                RFC 5322 compliant - no special characters (see section 3.2)
                Leading whitespace is ignored
                name is case-insensitive
            quotedText := 
                '"' text '"'
                Can contain escaped quotes:  ‘\\”’

        Document-Source defines the following WELL-KNOWN parameters:
            name: The name of the attached document
            source: The source of the attached document
        
        Attached documents are named using the Content-Disposition MIME header.
     
        The value of the *name* parameter is used to find an attachment with a matching Content-Disposition. 
     
        The value of the *source* parameter can be:
            * A Direct email address
            * Reserved LITERALS:
                - UNKNOWN   The source of the document is unknown
                - MIXED     The document is an aggregate of information that came from multiple of sources
                - PATIENT
            * Any arbitary descriptive string.

        There may be multiple Document-Source fields in a RequestContext document
        There should be a one Document-Source field for each attached document.
    */
    /// <summary>
    /// BlueButton Request Context. 
    /// </summary>
    public class RequestContext : MimeFields
    {
        /// <summary>
        /// Request contexts are attached with this well known name
        /// </summary>
        public const string AttachmentName = "request.txt";
        /// <summary>
        /// Well known field names
        /// </summary>                
        public static class FieldNames
        {
            /// <summary>
            /// The DocumentSource field supplies the source of an attachment
            /// </summary>
            public const string DocumentSource = "Document-Source";
        }
        
        /// <summary>
        /// Construct a request context
        /// </summary>
        public RequestContext()
        {
        }
        
        /// <summary>
        /// Construct a context from the request context bodys
        /// </summary>
        /// <param name="requestContextBody"></param>
        public RequestContext(string requestContextBody)
        {
            this.Deserialize(requestContextBody);
        }
        
        /// <summary>
        /// Return all document source fields in this context
        /// </summary>
        /// <returns></returns>
        public IEnumerable<DocumentSource> GetDocumentSources()
        {
            return (
                from fieldValue in this.GetValueForField(FieldNames.DocumentSource)
                select DocumentSource.Deserialize(fieldValue)
            );
        }
        
        /// <summary>
        /// Remove all document source fields from this context
        /// </summary>
        public void RemoveDocumentSources()
        {
            this.Remove(FieldNames.DocumentSource);
        }
        
        /// <summary>
        /// Add a new document source to the context
        /// </summary>
        /// <param name="source"></param>
        public void Add(DocumentSource source)
        {
            if (source == null)
            {
                throw new ArgumentNullException("source");
            }
            string fieldValue = source.ToString();
            this.Add(FieldNames.DocumentSource, fieldValue);
        }
        
        /// <summary>
        /// Serialize the Request Context to a Mail Attachment
        /// </summary>
        /// <returns></returns>        
        public Attachment ToAttachment()
        {
            return this.ToAttachment(AttachmentName);
        }
        
        /// <summary>
        /// Serialize the Request Context to a Mime Entity
        /// </summary>
        /// <returns></returns>
        public MimeEntity ToMimeEntity()        
        {
            return this.ToMimeEntity(AttachmentName);
        }        
    }
}
