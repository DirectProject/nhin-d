/* 
 Copyright (c) 2010, Direct Project
 All rights reserved.

 Authors:
    Umesh Madan     umeshma@microsoft.com
    Joe Shook	    jshook@kryptiq.com

Redistribution and use in source and binary forms, with or without modification, are permitted provided that the following conditions are met:

Redistributions of source code must retain the above copyright notice, this list of conditions and the following disclaimer.
Redistributions in binary form must reproduce the above copyright notice, this list of conditions and the following disclaimer in the documentation and/or other materials provided with the distribution.
Neither the name of The Direct Project (directproject.org) nor the names of its contributors may be used to endorse or promote products derived from this software without specific prior written permission.
THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 
*/
using System;
using System.Xml.Serialization;

namespace Health.Direct.SmtpAgent
{
    /// <summary>
    /// Configuration settings that control how and when MDNs are generated. 
    /// The SMTPAgent can, if AutoResponse is true, generate MDN Acks automatically
    /// </summary>
    public class NotificationSettings
    {
        const string DefaultText = "Direct Security Agent";
        const string GENERAL_DSN_OPTION = "General";
        const string RELIABLE_DSN_OPTION = "ReliableAndTimely";

        bool m_autoResponse = false;
        bool m_alwaysAck = true;
        bool m_useIncomingAnchorsToSend = false;
        string m_productName = DefaultText;
        bool m_gatewayIsDestination;
        string m_autoDSNFailueCreation;
        bool? m_autoDSNForTimelyAndReliable;
        bool? m_autoDSNForGeneral;


        public NotificationSettings()
        {
        }

        /// <summary>
        /// Automatically send processed notifications when messages have been successfully processed (security, trust etc)
        /// </summary>
        [XmlElement]
        public bool AutoResponse
        {
            get
            {
                return m_autoResponse;
            }
            set
            {
                m_autoResponse = value;
            }
        }
        
        /// <summary>
        /// Always issue an processed notificatoin, even if the caller did not request one
        /// </summary>
        [XmlElement]
        public bool AlwaysAck
        {
            get
            {
                return m_alwaysAck;
            }
            set
            {
                m_alwaysAck = value;
            }
        }

        /// <summary>
        /// Auto issue a dispatched notification
        /// It is more typical to not set this and allow the destination network or client 
        /// send a dispatched notification.  But if the incoming folder or routes are 
        /// considered the utlimate destination setting this is appropriate. 
        /// </summary>
        [XmlElement]
        public bool GatewayIsDestination
        {
            get { return m_gatewayIsDestination; }
            set { m_gatewayIsDestination = value; }
        }

        [XmlElement]
        public string AutoDsnFailueCreation
        {
            get { return m_autoDSNFailueCreation; }
            set { m_autoDSNFailueCreation = value; }
        }

        public bool AutoDSNForTimelyAndReliable
        {
            get
            {
                if (m_autoDSNForTimelyAndReliable.HasValue)
                {
                    return m_autoDSNForTimelyAndReliable.GetValueOrDefault(false);
                }
                foreach(var option in AutoDsnFailueCreation.Split(','))
                {
                    if (string.Equals(option, RELIABLE_DSN_OPTION, StringComparison.OrdinalIgnoreCase))
                    {
                        m_autoDSNForTimelyAndReliable = true;
                        return true;
                    }
                }
                m_autoDSNForTimelyAndReliable = false;
                return false;
            }
        }

        public bool AutoDSNForGeneral
        {
            get
            {
                if (m_autoDSNForGeneral.HasValue)
                {
                    return m_autoDSNForGeneral.GetValueOrDefault(false);
                }
                foreach (var option in AutoDsnFailueCreation.Split(','))
                {
                    if (string.Equals(option, GENERAL_DSN_OPTION, StringComparison.OrdinalIgnoreCase))
                    {
                        m_autoDSNForGeneral = true;
                        return true;
                    }
                }
                m_autoDSNForGeneral = false;
                return false;
            }
        }


        /// <summary>
        /// Optional explanation text to include when sending MDN Acks. 
        /// </summary>
        [XmlElement]
        public string Text
        {
            get;
            set;
        }
        
        [XmlIgnore]
        public bool HasText
        {
            get
            {
                return (!string.IsNullOrEmpty(this.Text));
            }
        }
        
        /// <summary>
        /// Optional name of the sending entity. Default is 'Direct Security Agent'
        /// </summary>
        [XmlElement]
        public string ProductName
        {
            get
            {
                return m_productName;
            }
            set
            {
                m_productName = string.IsNullOrEmpty(value) ? DefaultText : value;
            }
        }
        
        [XmlElement]
        public bool UseIncomingTrustAnchorsToSend
        {
            get
            {
                return m_useIncomingAnchorsToSend;
            }
            set
            {
                m_useIncomingAnchorsToSend = value;
            }
        }
               
        

        public void Validate()
        {
        }
    }
}