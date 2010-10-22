using Health.Direct.Common.Container;

namespace Health.Direct.Common.Diagnostics
{
    ///<summary>
    /// The interface that provides access to the audit sub-system. Any implementation
    /// should provide an implementation of this interface and register it with your
    /// own container <see cref="IoC"/>
    ///</summary>
    public interface IAuditor
    {
        ///<summary>
        /// Log to the audit sub-system that a specific category event has occurred.
        ///</summary>
        ///<param name="category">A category usually named in <see cref="AuditNames"/></param>
        void Log(string category);

        ///<summary>
        /// Log to the audit sub-system that a specific category event has occurred with optional
        /// message.
        ///</summary>
        ///<param name="category">A category usually named in <see cref="AuditNames"/></param>
        ///<param name="message">A string message to include with the audited event.</param>
        void Log(string category, string message);
    }
}