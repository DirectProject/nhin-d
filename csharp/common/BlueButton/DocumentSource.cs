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
using Health.Direct.Common.Mime;

namespace Health.Direct.Common.BlueButton
{
    /*
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
    /// Document Source object
    /// </summary>
    public class DocumentSource : IEnumerable<KeyValuePair<string, string>>, IComparable<DocumentSource>
    {
        /// <summary>
        /// The Document-Source field defines well known parameters
        /// </summary>
        public static class ParameterNames
        {
            /// <summary>
            /// This parameter supplies the name the attached document
            /// </summary>
            public const string Name = "name";
            /// <summary>
            /// This parameter supplies the source of the attached document named by the name parameter
            /// </summary>
            public const string Source = "source";
        }
        
        /// <summary>
        /// Standard, well known document source names
        /// </summary>
        public static class StandardSources
        {
            /// <summary>
            /// The source of this document is unknown
            /// </summary>
            public const string Unknown = "UNKNOWN";
            /// <summary>
            /// The document was generated or aggregated from information acquired from multiple sources
            /// </summary>
            public const string Mixed = "MIXED";
            /// <summary>
            /// The source of the document is the patient
            /// </summary>
            public const string Patient = "PATIENT";
        }
        
        string m_name;
        string m_source;
        
        /// <summary>
        /// Constructor
        /// The document has an unknown source
        /// </summary>
        /// <param name="documentName"></param>                
        public DocumentSource(string documentName)
            : this(documentName, (string) null)
        {
        }
        
        /// <summary>
        /// Constructor
        /// </summary>
        /// <param name="documentName">Document name</param>
        /// <param name="source">Mail address from which the document originated</param>        
        public DocumentSource(string documentName, MailAddress source)
            : this(documentName, source.ToString())
        {
        }
        
        /// <summary>
        /// Constructor
        /// </summary>
        /// <param name="documentName">Document name</param>
        /// <param name="source">Any arbitrary string</param>
        public DocumentSource(string documentName, string source)
        {   
            if (string.IsNullOrEmpty(documentName))
            {
                throw new ArgumentException("documentName");
            }
            m_name = documentName;
            this.Source = source;
        }
        
        /// <summary>
        /// Construct DocumentSource information for the given mail attachment
        /// The FileName associated with the attachment is automatically used
        /// </summary>
        /// <param name="attachment"></param>
        /// <param name="source"></param>        
        public DocumentSource(Attachment attachment, string source)
        {
            if (attachment == null)
            {
                throw new ArgumentNullException("attachment");
            }
            string docName = null;
            if (attachment.ContentDisposition != null)
            {
                docName = attachment.ContentDisposition.FileName;
            }
            if (string.IsNullOrEmpty(docName) && attachment.ContentType != null)
            {
                docName = attachment.ContentType.Name;
            }
            this.DocumentName = docName;
            this.Source = source;
        }
        
        /// <summary>
        /// Construct DocumentSource information for the given mail attachment
        /// The FileName associated with the attachment is automatically used
        /// </summary>
        /// <param name="attachment"></param>
        /// <param name="source"></param>
        public DocumentSource(Attachment attachment, MailAddress source)
            : this(attachment, source.ToString())
        {
        }
        
        /// <summary>
        /// The name of the document
        /// </summary>                
        public string DocumentName
        {
            get 
            { 
                return m_name;
            }
            set
            {
                if (string.IsNullOrEmpty(value))
                {
                    throw new ArgumentException("DocumentName");
                }
                m_name = value;
            }
        }
        
        /// <summary>
        /// The source of the document
        /// </summary>
        public string Source
        {
            get
            {
                return m_source;
            }
            set
            {
                if (string.IsNullOrEmpty(value))
                {
                    m_source = StandardSources.Unknown;
                }
                else
                {
                    m_source = value;
                }
            }
        }
        
        /// <summary>
        /// Returns true if this object contains the source for the given document name
        /// </summary>
        /// <param name="documentName"></param>
        /// <returns></returns>
        public bool IsSourceForDocument(string documentName)
        {
            return MimeStandard.Equals(documentName, this.DocumentName);
        }
        
        /// <summary>
        /// Deserialize the field value of a Document-Source field
        /// </summary>
        /// <param name="text"></param>
        /// <returns></returns>
        public static DocumentSource Deserialize(string text)
        {
            if (string.IsNullOrEmpty(text))
            {
                throw new ArgumentException("text");
            }    
            string name = null;
            string value = null;        
            foreach(KeyValuePair<string, string> fieldParam in MimeFieldParameters.Read(text))
            {   
                if (MimeStandard.Equals(fieldParam.Key, ParameterNames.Name))
                {
                    name = fieldParam.Value;
                }
                else if (MimeStandard.Equals(fieldParam.Key, ParameterNames.Source))
                {
                    value = fieldParam.Value;
                }
            }
            
            if (string.IsNullOrEmpty(name))
            {   
                throw new ArgumentException("Invalid Document Source");
            }
            
            return new DocumentSource(name, value);
        }
        
        /// <summary>
        /// Return the parameters in this Document-Source
        /// </summary>
        /// <returns></returns>
        public IEnumerator<KeyValuePair<string, string>> GetEnumerator()
        {
            yield return new KeyValuePair<string, string>(ParameterNames.Name, m_name);
            yield return new KeyValuePair<string, string>(ParameterNames.Source, m_source);
        }
        
        /// <summary>
        /// Serialize to a string
        /// </summary>
        /// <returns></returns>
        public string Serialize()
        {
            StringBuilder sb = new StringBuilder();
            MimeFieldParameters.Write(sb, this, true);
            return sb.ToString();
        }
        
        /// <summary>
        /// Serialize to a string
        /// </summary>
        /// <returns></returns>
        public override string ToString()
        {
            return this.Serialize();
        }
        
        /// <summary>
        /// Compare two document sources
        /// </summary>
        /// <param name="other"></param>
        /// <returns></returns>
        public int CompareTo(DocumentSource other)
        {
            if (other == null)
            {
                return 1;
            }
            
            int cmp = MimeStandard.Comparer.Compare(this.DocumentName, other.DocumentName);
            if (cmp == 0)
            {
                cmp = MimeStandard.Comparer.Compare(this.Source, other.Source);
            }
            
            return cmp;
        }

        #region IEnumerable Members

        System.Collections.IEnumerator System.Collections.IEnumerable.GetEnumerator()
        {
            return this.GetEnumerator();
        }

        #endregion
    }
}
