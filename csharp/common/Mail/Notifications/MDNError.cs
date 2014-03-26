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
using System.Collections.Generic;
using System.Linq;
using System.Text;

namespace Health.Direct.Common.Mail.Notifications
{
    /// <summary>
    /// MDN processing errors that may trigger <see cref="MDNException"/> exceptions.
    /// </summary>
    public enum MDNError
    {
        /// <summary>
        /// Unexpected error
        /// </summary>
        Unexpected = 0,
        /// <summary>
        /// Cannot send an MDN for an MDN
        /// </summary>
        CannotSendMDNForMDN,
        /// <summary>
        /// Not an MDN message
        /// </summary>
        NotMDN,
        /// <summary>
        /// ReportingUserAgent could not be parsed
        /// </summary>
        InvalidReportingUserAgent,
        /// <summary>
        /// MdnGateway could not be parsed
        /// </summary>
        InvalidMdnGateway,
        /// <summary>
        /// Disposition could not be parsed
        /// </summary>
        InvalidDisposition,
        /// <summary>
        /// MDN Body could not be parsed
        /// </summary>
        InvalidMDNBody,
        /// <summary>
        /// Invalid Fields in the Body of the MDN message
        /// </summary>        
        InvalidMDNFields,
        /// <summary>
        /// Invalid Trigger Type
        /// </summary>
        InvalidTriggerType,
        /// <summary>
        /// Invalid Send Type
        /// </summary>
        InvalidSendType,
        /// <summary>
        /// Invalid Notification Type
        /// </summary>
        InvalidNotificationType,
        /// <summary>
        /// Invalid Final Recipient
        /// </summary>
        InvalidFinalRecipient
    }
}
