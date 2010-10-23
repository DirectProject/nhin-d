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
using System;
using System.Diagnostics;

namespace Health.Direct.Common.Diagnostics
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
        private readonly IMethodTracer m_tracer;

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
        /// Trace the begin and end of a block of code using the <paramref name="logger"/>.
        /// The method name will be obtained by grabbing the name of the method one
        /// frame up the StackFrame.
        ///</summary>
        ///<param name="logger">The logger to log to at the Trace level</param>
        public MethodTracer(ILogger logger)
            : this(new StackTrace().GetFrame(1).GetMethod().Name, logger)
        {
        }

        ///<summary>
        /// Trace the begin and end of a block of code using the <paramref name="logger"/>
        ///</summary>
        ///<param name="methodName">The method name or prefix of the string to use.</param>
        ///<param name="logger">The logger to log to at the Trace level</param>
        public MethodTracer(string methodName, ILogger logger)
        {
            if (logger == null)
            {
                throw new ArgumentNullException("logger");
            }

            if (logger.IsTraceEnabled)
            {
                m_tracer = new InternalMethodTracer(methodName, logger);
            }
            else
            {
                m_tracer = new DummyMethodTracer();
            }
        }

        /// <summary>
        /// If <see cref="ILogger.IsTraceEnabled"/> is <c>true</c> then it will log the methodName
        /// to <see cref="ILogger.Trace(string)"/>.
        /// </summary>
        public void Dispose()
        {
            m_tracer.Dispose();
        }

        interface IMethodTracer : IDisposable
        {
        }

        class DummyMethodTracer : IMethodTracer
        {
            public void Dispose()
            {
            }
        }

        /// <summary>
        /// The InternalMethodTracer assumes that <see cref="ILogger.IsTraceEnabled"/> is true so we don't
        /// need to repeatedly test the <see cref="ILogger.IsTraceEnabled"/>. 
        /// </summary>
        class InternalMethodTracer : IMethodTracer
        {
            private const string MethodBeginSuffix = "_Begin";
            private const string MethodEndSuffix = "_End";

            private readonly ILogger m_logger;
            private readonly string m_methodName;

            public InternalMethodTracer(string methodName, ILogger logger)
            {
                if (string.IsNullOrEmpty(methodName))
                {
                    throw new ArgumentException("methodName was null or empty", "methodName");
                }
                if (logger == null)
                {
                    throw new ArgumentNullException("logger");
                }

                m_logger = logger;
                m_methodName = methodName;
                m_logger.Trace(methodName + MethodBeginSuffix);
            }

            public void Dispose()
            {
                m_logger.Trace(m_methodName + MethodEndSuffix);
            }
        }
    }
}