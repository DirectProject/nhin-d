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

using System;
using System.Net.Mail;
using System.Text;
using Health.Direct.Common.Mime;

namespace Health.Direct.Common.Mail.DSN
{
    /// <summary> 
    /// Encapsulate Per-Recipient headers
    /// </summary>
    /// <remarks>
    /// rfc 3464    2.3    Per-Recipient DSN fields
    /// </remarks>
    public class DSNPerRecipient
    {

        ///<summary>
        /// Initialize the per-recipient-fields for DSN
        ///</summary>
        ///<param name="action">DSN Action</param>
        ///<param name="classSubCode">Status code class</param>
        ///<param name="subjectSubCode">Status code subject</param>
        ///<param name="finalRecipient">Final recipient field</param>
        public DSNPerRecipient(DSNStandard.DSNAction action, int classSubCode, string subjectSubCode , MailAddress finalRecipient)
        {
            Action = action;
            Status = DSNStandard.DSNStatus.GetStatus(classSubCode, subjectSubCode);
            FinalRecipient = finalRecipient;
        }

        internal DSNPerRecipient(HeaderCollection fields)
        {
            //
            // Required Fields
            //
            Action = (DSNStandard.DSNAction)Enum.Parse(typeof(DSNStandard.DSNAction), fields.GetValue(DSNStandard.Fields.Action),true);
            Status = fields.GetValue((DSNStandard.Fields.Status));
            FinalRecipient = DSNParser.ParseFinalRecipient(fields.GetValue((DSNStandard.Fields.FinalRecipient)));

            //
            // Optional Fields
            //
            HeaderCollection otherFields = new HeaderCollection();
            otherFields.Add(fields, DSNStandard.PerRecipientOptionalFields);
            OtherFields = otherFields;
        }

        

        /// <summary>
        /// DSN Action 
        /// </summary>
        public DSNStandard.DSNAction Action { get; internal set; }
        
        ///<summary>
        /// DSN Status code
        ///</summary>
        public string Status { get; internal set; }

        ///<summary>
        /// Final-Recipient 
        ///</summary>
        public MailAddress FinalRecipient { get; internal set; }

        /// <summary>
        /// All the known optional headers.
        /// </summary>
        public HeaderCollection OtherFields { get; internal set; }

        /// <summary>
        /// Constructs the appropriate headers suitable for inclusion in an DSN report
        /// </summary>
        /// <returns>A string representation of the disposition suitable for inclusion in the DSN headers</returns>
        public override string ToString()
        {
            var perRecipientPart = new StringBuilder();
            //
            // final-recipient
            //
            perRecipientPart.Append(DSNStandard.Fields.FinalRecipient);
            perRecipientPart.Append(": ");
            perRecipientPart.Append(DSNStandard.AddressType_Mail);
            perRecipientPart.Append(';');
            perRecipientPart.AppendLine(FinalRecipient.Address);
            //
            // Action
            //
            perRecipientPart.Append(DSNStandard.Fields.Action);
            perRecipientPart.Append(": ");
            perRecipientPart.AppendLine(Action.AsString());
            //
            // Status Codes
            //
            perRecipientPart.Append(DSNStandard.Fields.Status);
            perRecipientPart.Append(": ");
            perRecipientPart.Append(Status);
            


            return perRecipientPart.ToString();
        }
    }
}
