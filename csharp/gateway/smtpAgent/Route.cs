/* 
 Copyright (c) 2010, Direct Project
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
using System.Xml.Serialization;

namespace Health.Direct.SmtpAgent
{
    /// <summary>
    /// You can associate an AddressType with e-mail addresses. 
    /// Message for a particular AddressType can then be routed to message handlers
    /// Currently, those handlers inherit from Route and implement Process(ISmtpMessage)
    /// </summary>
    public abstract class Route
    {
        string m_addressType;

        /// <summary>
        /// Constructor
        /// </summary>
        public Route()
        {
        }
        
        /// <summary>
        /// Marker to indicate which router can route an email address
        /// </summary>
        [XmlElement]
        public string AddressType
        {
            get
            {
                return m_addressType;
            }
            set
            {
                if (string.IsNullOrEmpty(value))
                {
                    throw new ArgumentException("AddressType");
                }
                
                m_addressType = value;
            }
        }

        /// <summary>
        /// 
        /// </summary>
        public event Action<Route, Exception> Error;

        /// <summary>
        /// Initialize router
        /// </summary>
        public abstract void Init();
        
        /// <summary>
        /// Process message though route
        /// </summary>
        /// <param name="message"></param>
        /// <returns></returns>
        public abstract bool Process(ISmtpMessage message);

        /// <summary>
        /// Validate route config settings.
        /// </summary>
        public virtual void Validate()
        {
            if (string.IsNullOrEmpty(AddressType))
            {
                throw new SmtpAgentException(SmtpAgentError.MissingAddressTypeInRoute);
            }
        }
                
        /// <summary>
        /// 
        /// </summary>
        /// <param name="ex"></param>
        protected void NotifyError(Exception ex)
        {
            if (Error != null)
            {
                try
                {
                    Error(this, ex);
                }
                catch
                {
                    // ignored
                }
            }
        }

        /// <summary>
        /// Mark route as failed
        /// </summary>
        public bool FailedDelivery { get; set; }

        /// <summary>
        /// Copy route
        /// </summary>
        /// <returns></returns>
        public abstract Route Clone();
    }
}
