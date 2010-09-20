using System;
using System.Diagnostics;

namespace NHINDirect.Diagnostics
{
    ///<summary>
    /// A convience class that allows the implementor to surround a block of code
    /// and automatically trace the begin and end of the code (even when an exception
    /// occurs).
    ///</summary>
    /// <example>
    /// // here is an example of usage
    /// using (new MethodTracer("methodName", this))
    /// {
    ///     // your method's code here
    /// }
    /// </example>
    public class MethodTracer : IDisposable
    {
        private const string MethodBeginSuffix = "_Begin";
        private const string MethodEndSuffix = "_End";

        private readonly ILogger m_logger;
        private readonly string m_methodName;

        ///<summary>
        /// Trace the begin and end of a block of code. 
        ///</summary>
        ///<param name="methodName">The method name or prefix of the string to use.</param>
        ///<param name="logOwner">The owner object to obtain the logger for</param>
        public MethodTracer(string methodName, object logOwner)
            : this(methodName, Log.For(logOwner))
        {
        }

        ///<summary>
        /// Trace the begin and end of a block of code using the <paramref name="logger"/>
        ///</summary>
        ///<param name="methodName">The method name or prefix of the string to use.</param>
        ///<param name="logger">The logger to log to at the Trace level</param>
        public MethodTracer(string methodName, ILogger logger)
        {
            m_logger = logger;
            m_methodName = methodName;
            m_logger.Trace(methodName + MethodBeginSuffix);
        }

        ///<summary>
        /// Trace the begin and end of a block of code using the <paramref name="logger"/>.
        /// The method name will be obtained by grabbing the name of the method one
        /// frame up the StackFrame.
        ///</summary>
        ///<param name="logger">The logger to log to at the Trace level</param>
        public MethodTracer(ILogger logger)
        {
            m_logger = logger;
            if (m_logger.IsTraceEnabled)
            {
                m_methodName = new StackTrace().GetFrame(1).GetMethod().Name;
            }

            m_logger.Trace(m_methodName + MethodBeginSuffix);
        }

        /// <summary>
        /// If <see cref="ILogger.IsTraceEnabled"/> is <c>true</c> and the <see cref="m_methodName"/>
        /// is not null or empty then it will log the <see cref="m_methodName"/> + the <see cref="MethodEndSuffix"/>
        /// to <see cref="ILogger.Trace(string)"/>.
        /// </summary>
        public void Dispose()
        {
            if (m_logger.IsTraceEnabled && !string.IsNullOrEmpty(m_methodName))
            {
                m_logger.Trace(m_methodName + MethodEndSuffix);
            }
        }
    }
}