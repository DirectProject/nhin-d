using System;
using System.Diagnostics;

namespace Health.Direct.Common.Diagnostics
{
    ///<summary>
    /// This helper/utility class allows us to have a single entry point when writing
    /// to the EventLog.
    ///</summary>
    public static class EventLogHelper
    {
        private const string DefaultSourceName = "Health.Direct";
        private const int DefaultEventID = 1;

        ///<summary>
        /// Write to the EventLog with the type set to <see cref="EventLogEntryType.Error"/>.
        ///</summary>
        /// <remarks>If this fails it will 'eat' the exception.</remarks>
        ///<param name="source">The source name</param>
        ///<param name="message">The message to log</param>
        public static void WriteError(string source, string message)
        {
            WriteLog(source, EventLogEntryType.Error, DefaultEventID, message);
        }

        ///<summary>
        /// Write to the EventLog with the type set to <see cref="EventLogEntryType.Information"/>.
        ///</summary>
        /// <remarks>If this fails it will 'eat' the exception.</remarks>
        ///<param name="source">The source name</param>
        ///<param name="message">The message to log</param>
        public static void WriteInformation(string source, string message)
        {
            WriteLog(source, EventLogEntryType.Information, DefaultEventID, message);
        }

        ///<summary>
        /// Write to the EventLog with the type set to <see cref="EventLogEntryType.Warning"/>. 
        /// If this fails it will 'eat' the exception.
        ///</summary>
        /// <remarks>If this fails it will 'eat' the exception.</remarks>
        ///<param name="source">The source name</param>
        ///<param name="message">The message to log</param>
        public static void WriteWarning(string source, string message)
        {
            WriteLog(source, EventLogEntryType.Warning, DefaultEventID, message);
        }

        private static void WriteLog(string source, EventLogEntryType type, int eventID, string message)
        {
            try
            {
                EventLog.WriteEntry(source ?? DefaultSourceName, message, type, eventID);
            }
            catch (Exception ex)
            {
                try
                {
                    Log.For(typeof(EventLogHelper))
                        .Warn("While writing to the EventLog. Original message was - " + message, ex);
                }
                catch
                {
                    // eat this exception
                }
            }
        }
    }
}