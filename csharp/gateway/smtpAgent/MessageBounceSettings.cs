/* 
 Copyright (c) 2010, NHIN Direct Project
 All rights reserved.

 Authors:
    Umesh Madan     umeshma@microsoft.com
  
Redistribution and use in source and binary forms, with or without modification, are permitted provided that the following conditions are met:

Redistributions of source code must retain the above copyright notice, this list of conditions and the following disclaimer.
Redistributions in binary form must reproduce the above copyright notice, this list of conditions and the following disclaimer in the documentation and/or other materials provided with the distribution.
Neither the name of the The NHIN Direct Project (nhindirect.org). nor the names of its contributors may be used to endorse or promote products derived from this software without specific prior written permission.
THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 
*/
using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Xml.Serialization;
using System.IO;

namespace NHINDirect.SmtpAgent
{
    public class MessageBounceSettings
    {
        bool m_enableIncoming = false;
        bool m_enableOutgoing = true;
        
        BounceMessageTemplate m_outgoingTemplate = BounceMessageTemplate.Default;
        BounceMessageTemplate m_incomingTemplate = BounceMessageTemplate.Default;
        
        public MessageBounceSettings()
        {
        }
        
        [XmlElement("MailPickupFolder")]
        public string MailPickupFolder
        {
            get;
            set;
        }
        
        /// <summary>
        /// Enabled by default
        /// </summary>
        [XmlElement("EnableForOutgoing")]
        public bool EnableForOutgoing
        {
            get
            {
                return m_enableOutgoing;
            }
            set
            {
                m_enableOutgoing = value;
            }
        }
        
        /// <summary>
        /// Disabled by default
        /// </summary>
        [XmlElement("EnableForIncoming")]
        public bool EnableForIncoming
        {
            get
            {
                return m_enableIncoming;
            }
            set
            {
                m_enableIncoming = value;
            }
        }
         
        [XmlElement("OutgoingTemplate")]
        public BounceMessageTemplate OutgoingTemplate
        {
            get
            {
                return m_outgoingTemplate;
            }
            set
            {
                m_outgoingTemplate = value;
            }
        }

        [XmlIgnore]
        public bool HasOutgoingTemplate
        {
            get
            {
                return (m_outgoingTemplate != null);
            }
        }

        [XmlElement("IncomingTemplate")]
        public BounceMessageTemplate IncomingTemplate
        {
            get
            {
                return m_incomingTemplate;
            }
            set
            {
                m_incomingTemplate = value;
            }
        }
        
        [XmlIgnore]
        public bool HasIncomingTemplate
        {
            get
            {
                return (m_incomingTemplate != null);
            }
        }
                        
        public void Validate()
        {
            if (string.IsNullOrEmpty(this.MailPickupFolder))
            {
                throw new SmtpAgentException(SmtpAgentError.MissingMailPickupFolder);
            }
            
            if (!Directory.Exists(this.MailPickupFolder))
            {
                throw new SmtpAgentException(SmtpAgentError.MailPickupFolderDoesNotExist);
            }
            
            if (this.EnableForOutgoing)
            {
                if (!this.HasOutgoingTemplate)
                {
                    throw new SmtpAgentException(SmtpAgentError.MissingBounceTemplateOutgoing);
                }
                
                this.OutgoingTemplate.Validate();
            }

            if (this.EnableForIncoming)
            {
                if (!this.HasIncomingTemplate)
                {
                    throw new SmtpAgentException(SmtpAgentError.MissingBounceTemplateIncoming);
                }

                this.IncomingTemplate.Validate();
            }            
        }
    }
    
    public class BounceMessageTemplate
    {
        internal static BounceMessageTemplate Default = new BounceMessageTemplate {
            Subject = "Delivery Status Notification (Failure)",
            Body = "The following recipients are not trusted:"
            };
            
        public BounceMessageTemplate()
        {
        }
        
        [XmlElement("Subject")]
        public string Subject
        {
            get;
            set;
        }

        [XmlElement("Body")]
        public string Body
        {
            get;
            set;
        }
        
        public void Validate()
        {
            if (string.IsNullOrEmpty(this.Subject) || string.IsNullOrEmpty(this.Body))
            {
                throw new SmtpAgentException(SmtpAgentError.InvalidBounceMessageTemplate);
            }
        }
    }
}
