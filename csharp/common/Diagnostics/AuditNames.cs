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
        }
    }
}