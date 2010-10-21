/* 
 Copyright (c) 2010, Direct Project
 All rights reserved.

 Authors:
    Umesh Madan     umeshma@microsoft.com
  
Redistribution and use in source and binary forms, with or without modification, are permitted provided that the following conditions are met:

Redistributions of source code must retain the above copyright notice, this list of conditions and the following disclaimer.
Redistributions in binary form must reproduce the above copyright notice, this list of conditions and the following disclaimer in the documentation and/or other materials provided with the distribution.
Neither the name of the The Direct Project (nhindirect.org). nor the names of its contributors may be used to endorse or promote products derived from this software without specific prior written permission.
THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 
*/
using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.IO;

namespace NHINDirect.Diagnostics
{
    /// <summary>
    /// Thread safe Log File
    /// </summary>
    public class LogFile : IDisposable
    {
        LogWriter m_writer;

        /// <summary>
        /// Initializes an instace with the specified <paramref name="writer"/>
        /// </summary>
        /// <param name="writer">The <see cref="LogWriter"/> this file uses to write.</param>
        public LogFile(LogWriter writer)
        {
            if (writer == null)
            {
                throw new ArgumentNullException();
            }
            m_writer = writer;
        }
        
        /// <summary>
        /// Logs an exception
        /// </summary>
        /// <param name="exception">The exception to log</param>
        public void WriteError(Exception exception)
        {
            lock(m_writer)
            {
                m_writer.WriteError(exception);
            }
        }

        /// <summary>
        /// Logs an error with a cusom message
        /// </summary>
        /// <param name="message">The message to log</param>
        public void WriteError(string message)
        {
            lock (m_writer)
            {
                m_writer.WriteLine(LogEventType.Error, message);
            }
        }

        /// <summary>
        /// Writes an object string representation to the log.
        /// </summary>
        /// <param name="type">The <see cref="LogEventType"/> severity level.</param>
        /// <param name="message">The object whose string representation will be to logged.</param>
        public void WriteLine(LogEventType type, object message)
        {
            lock(m_writer)
            {
                m_writer.WriteLine(type, message);
            }
        }

        /// <summary>
        /// Writes a message to the log
        /// </summary>
        /// <param name="type">The <see cref="LogEventType"/> severity level.</param>
        /// <param name="message">The message to log.</param>
        public void WriteLine(LogEventType type, string message)
        {
            lock(m_writer)
            {
                m_writer.WriteLine(type, message);
            }
        }

        /// <summary>
        /// Writes a message of nonstandard severity level to the log.
        /// </summary>
        /// <param name="type">The non-standard message type</param>
        /// <param name="message">The message to log.</param>
        public void WriteLine(string type, string message)
        {
            lock (m_writer)
            {
                m_writer.WriteLine(type, message);
            }
        }

        /// <summary>
        /// Writes an informative message to the log (severity level <see cref="LogEventType.Info"/>)
        /// </summary>
        /// <param name="message">The message to log.</param>
        public void WriteLine(string message)
        {
            lock(m_writer)
            {
                m_writer.WriteLine(LogEventType.Info, message); 
            }
        }
        
        /// <summary>
        /// Frees resources for this instance.
        /// </summary>
        public void Dispose()
        {
            if (m_writer != null)
            {
                m_writer.Close();
                m_writer = null;
            }
        }
    }
}
