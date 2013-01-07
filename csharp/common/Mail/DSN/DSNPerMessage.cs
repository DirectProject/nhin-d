/* 
 Copyright (c) 2010, Direct Project
 All rights reserved.

 Authors:
    Joe Shook     jshook@kryptiq
  
Redistribution and use in source and binary forms, with or without modification, are permitted provided that the following conditions are met:

Redistributions of source code must retain the above copyright notice, this list of conditions and the following disclaimer.
Redistributions in binary form must reproduce the above copyright notice, this list of conditions and the following disclaimer in the documentation and/or other materials provided with the distribution.
Neither the name of The Direct Project (directproject.org) nor the names of its contributors may be used to endorse or promote products derived from this software without specific prior written permission.
THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 
*/

using System.Text;
using Health.Direct.Common.Mime;

namespace Health.Direct.Common.Mail.DSN
{
    /// <summary> 
    /// Encapsulate Per-Message headers
    /// </summary>
    /// <remarks>
    /// rfc 3464    2.2    Per-Message DSN fields
    /// </remarks>
    public class DSNPerMessage
    {
        
        ///<summary>
        /// Initialize the per-message-fields for DSN
        ///</summary>
        ///<param name="reportingMtaName">DSN reporting-mta</param>
        ///<param name="originalMessageId">DSN extension field, <see cref="DSNStandard.Fields.OriginalMessageID"/></param>
        public DSNPerMessage(string reportingMtaName, string originalMessageId)
        {
            ReportingMtaName = reportingMtaName;
            OriginalMessageId = originalMessageId;

        }
        

        internal DSNPerMessage(HeaderCollection fields)
        {
            ReportingMtaName = DSNParser.ParseReportingMTA(fields.GetValue(DSNStandard.Fields.ReportingMTA));
            OriginalMessageId = fields.GetValue((DSNStandard.Fields.OriginalMessageID));
        }

        /// <summary>
        /// DSN Reporting-MTA 
        /// </summary>
        public string ReportingMtaName { get; internal set; }

        ///<summary>
        /// DSN extension field, <see cref="DSNStandard.Fields.OriginalMessageID"/>
        ///</summary>
        public string OriginalMessageId { get; internal set; }

        /// <summary>
        /// Constructs the appropriate headers suitable for inclusion in an DSN report
        /// </summary>
        /// <returns>A string representation of the disposition suitable for inclusion in the DSN headers</returns>
        public override string ToString()
        {
            var permMessagePart = new StringBuilder();
            //
            // Reporting-MTA 
            //
            permMessagePart.Append(DSNStandard.Fields.ReportingMTA);
            permMessagePart.Append(": ");
            permMessagePart.Append(DSNStandard.MtaNameType.Dns.AsString());
            permMessagePart.Append(';');
            permMessagePart.AppendLine(ReportingMtaName);
            //
            // X-Original-Message-ID
            //
            permMessagePart.Append(DSNStandard.Fields.OriginalMessageID);
            permMessagePart.Append(": ");
            permMessagePart.Append(OriginalMessageId);
            

            return permMessagePart.ToString();
        }
    }
}
