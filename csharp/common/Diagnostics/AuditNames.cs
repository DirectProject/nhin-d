/* 
 Copyright (c) 2010, Direct Project
 All rights reserved.

 Authors:
    John Theisen     jtheisen@kryptiq.com
  
Redistribution and use in source and binary forms, with or without modification, are permitted provided that the following conditions are met:

Redistributions of source code must retain the above copyright notice, this list of conditions and the following disclaimer.
Redistributions in binary form must reproduce the above copyright notice, this list of conditions and the following disclaimer in the documentation and/or other materials provided with the distribution.
Neither the name of The Direct Project (directproject.org) nor the names of its contributors may be used to endorse or promote products derived from this software without specific prior written permission.
THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 
*/
namespace Health.Direct.Common.Diagnostics
{
    ///<summary>
    /// AuditNames is a convience class containing the category names used with
    /// the <see cref="IAuditor.Log(string)"/> and <see cref="IAuditor.Log(string,string)"/> methods.
    ///</summary>
    /// <example>
    /// IAuditor auditor = GetAuditor();
    /// auditor.Log(AuditNames.Message.IncomingAccepted);
    /// </example>
    public static class AuditNames
    {
        ///<summary>
        /// The message specific <see cref="AuditNames"/> names.
        ///</summary>
        public static class Message
        {
            ///<summary>
            /// Audit message indicating the incoming SMTP Message has been accepted.
            ///</summary>
            public const string IncomingAccepted = "Incoming Message Accepted";

            ///<summary>
            /// Audit message indicating the incoming SMTP Message has been rejected.
            ///</summary>
            public const string IncomingRejected = "Incoming Message Rejected";

            /// <summary>
            /// Audit message indicating the outgoing STMP Message has been sent.
            /// </summary>
            public const string OutgoingSent = "Outgoing Message Sent";

            /// <summary>
            /// Audit message indicating the outgoing STMP Message has been rejected.
            /// </summary>
            public const string OutgoingRejected = "Outgoing Message Rejected";

            /// <summary>
            /// Audit message indicating the message was rejected before we could determine what it is
            /// </summary>
            public const string MessageRejected = "Message Rejected";
            
            /// <summary>
            /// Return the appropriate Audit message for accepted messages
            /// </summary>
            /// <param name="isIncoming">Incoming message?</param>
            /// <returns>audit text</returns>
            public static string GetAcceptedMessage(bool isIncoming)
            {
                return isIncoming ? IncomingAccepted : OutgoingSent;
            }
            
            /// <summary>
            /// Returns the appropriate Audit message for rejected messages
            /// </summary>
            /// <param name="isIncoming">Incoming message?</param>
            /// <returns>audit text</returns>
            public static string GetRejectedMessage(bool? isIncoming)
            {
                if (isIncoming == null)
                {
                    return MessageRejected;
                }
                
                return isIncoming.Value ? IncomingRejected : OutgoingRejected;
            }
        }
    }
}