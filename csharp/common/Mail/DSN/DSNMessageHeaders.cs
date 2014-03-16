/* 
Copyright (c) 2010, NHIN Direct Project
All rights reserved.

Authors:
   Umesh Madan     umeshma@microsoft.com
   Greg Meyer      gm2552@cerner.com
   Joe Shook       jshook@kryptiq.com
 * 
Redistribution and use in source and binary forms, with or without modification, are permitted provided that the following conditions are met:

Redistributions of source code must retain the above copyright notice, this list of conditions and the following disclaimer.
Redistributions in binary form must reproduce the above copyright notice, this list of conditions and the following disclaimer 
in the documentation and/or other materials provided with the distribution.  Neither the name of the The NHIN Direct Project (nhindirect.org). 
nor the names of its contributors may be used to endorse or promote products derived from this software without specific prior written permission.
THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, 
THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS 
BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE 
GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, 
STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF 
THE POSSIBILITY OF SUCH DAMAGE.
*/

namespace Health.Direct.Common.Mail.DSN
{
    /// <summary>
    /// Contains delivery status notification headers pertaining to the overall message.
    /// </summary>
    public class DSNMessageHeaders
    {
        private string reportingMta;
        private string originalMessageId;
        private DSNStandard.MtaNameType mtaNameType;

        ///<summary>
        /// Initializes an instance
        ///</summary>
        ///<param name="reportingMta">The reporting MTA message</param>
        ///<param name="originalMessageId">The original message id</param>
        ///<param name="mtaNameType">The MTA type</param>
        public DSNMessageHeaders(string reportingMta, string originalMessageId, DSNStandard.MtaNameType mtaNameType)
        {
            this.reportingMta = reportingMta;
            this.originalMessageId = originalMessageId;
            this.mtaNameType = mtaNameType;
        }

        ///<summary>
        /// Get reporting MTA (message transfer agent)
        ///</summary>
        /// <remarks>
        /// <![CDATA[
        /// RFC 3464             Delivery Status Notifications          January 2003
        /// 2.2.2 The Reporting-MTA DSN field
        /// 
        ///          reporting-mta-field =
        ///                "Reporting-MTA" ":" mta-name-type ";" mta-name
        /// 
        ///           mta-name = *text
        /// 
        ///    The Reporting-MTA field is defined as follows:
        /// 
        ///    A DSN describes the results of attempts to deliver, relay, or gateway
        ///    a message to one or more recipients.  In all cases, the Reporting-MTA
        ///    is the MTA that attempted to perform the delivery, relay, or gateway
        ///    operation described in the DSN.  This field is required.
        /// 
        ///    Note that if an SMTP client attempts to relay a message to an SMTP
        ///    server and receives an error reply to a RCPT command, the client is
        ///    responsible for generating the DSN, and the client's domain name will
        ///    appear in the Reporting-MTA field.  (The server's domain name will
        ///    appear in the Remote-MTA field.)
        /// 
        ///    Note that the Reporting-MTA is not necessarily the MTA which actually
        ///    issued the DSN.  For example, if an attempt to deliver a message
        ///    outside of the Internet resulted in a non-delivery notification which
        ///    was gatewayed back into Internet mail, the Reporting-MTA field of the
        ///    resulting DSN would be that of the MTA that originally reported the
        ///    delivery failure, not that of the gateway which converted the foreign
        ///    notification into a DSN.  See Figure 2.
        /// 
        /// 
        /// 
        ///  sender's environment                            recipient's environment
        ///  ............................ ..........................................
        ///                             : :
        ///                         (1) : :                             (2)
        ///    +-----+  +--------+  +--------+  +---------+  +---------+   +------+
        ///    |     |  |        |  |        |  |Received-|  |         |   |      |
        ///    |     |=>|Original|=>|        |->|  From   |->|Reporting|-->|Remote|
        ///    | user|  |   MTA  |  |        |  |   MTA   |  |   MTA   |<No|  MTA |
        ///    |agent|  +--------+  |Gateway |  +---------+  +----v----+   +------+
        ///    |     |              |        |                    |
        ///    |     | <============|        |<-------------------+
        ///    +-----+              |        |(4)                (3)
        ///                         +--------+
        ///                             : :
        ///  ...........................: :.........................................
        /// 
        ///               Figure 2. DSNs in the presence of gateways
        /// 
        ///    (1) message is gatewayed into recipient's environment
        ///    (2) attempt to relay message fails
        ///    (3) reporting-mta (in recipient's environment) returns non-delivery
        ///        notification
        ///    (4) gateway translates foreign notification into a DSN
        /// 
        ///    The mta-name portion of the Reporting-MTA field is formatted
        ///    according to the conventions indicated by the mta-name-type
        ///    sub-field.  If an MTA functions as a gateway between dissimilar mail
        ///    environments and thus is known by multiple names depending on the
        ///    environment, the mta-name sub-field SHOULD contain the name used by
        ///    the environment from which the message was accepted by the
        ///    Reporting-MTA.
        /// 
        ///    Because the exact spelling of an MTA name may be significant in a
        ///    particular environment, MTA names are CASE-SENSITIVE.
        /// ]]>
        /// </remarks>
        ///<returns>reporting MTA (message transfer agent)</returns>
        public string GetReportingMta()
        {
            return reportingMta;
        }


        ///<summary>
        /// A custom header indicating the original message
        /// </summary>
        ///<returns>Original message Id</returns>
        public string GetOriginalMessageId()
        {
            return originalMessageId;
        }


        ///<summary>
        /// An "MTA-name-type" specifies the format of a mail transfer agent name.
        ///</summary>
        ///<returns>MTA name type</returns>
        public DSNStandard.MtaNameType getMtaNameType()
        {
            return mtaNameType;
        }
    }
}