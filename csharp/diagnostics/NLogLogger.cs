/* 
 Copyright (c) 2010, Direct Project
 All rights reserved.

 Authors:
    John Theisen     john.theisen@kryptiq.com
  
Redistribution and use in source and binary forms, with or without modification, are permitted provided that the following conditions are met:

Redistributions of source code must retain the above copyright notice, this list of conditions and the following disclaimer.
Redistributions in binary form must reproduce the above copyright notice, this list of conditions and the following disclaimer in the documentation and/or other materials provided with the distribution.
Neither the name of The Direct Project (directproject.org) nor the names of its contributors may be used to endorse or promote products derived from this software without specific prior written permission.
THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 
*/
using System;

using Health.Direct.Common.Diagnostics;

using NLog;

namespace Health.Direct.Diagnostics.NLog
{
    class NLogLogger : ILogger
    {
        private readonly Logger m_logger;

        internal NLogLogger(Logger mLogger)
        {
            m_logger = mLogger;
        }

        public bool IsDebugEnabled
        {
            get { return m_logger.IsDebugEnabled; }
        }

        public void Debug(string message)
        {
            m_logger.Debug(message);
        }

        public void Debug(object obj)
        {
            m_logger.Debug(obj);
        }

        public void Debug(string message, params object[] args)
        {
            m_logger.Debug(message, args);
        }

        public void Debug(string message, Exception exception)
        {
            m_logger.DebugException(message, exception);
        }

        public bool IsErrorEnabled
        {
            get { return m_logger.IsErrorEnabled; }
        }

        public void Error(string message)
        {
            m_logger.Error(message);
        }

        public void Error(object obj)
        {
            m_logger.Error(obj);
        }

        public void Error(string message, params object[] args)
        {
            m_logger.Error(message, args);
        }

        public void Error(string message, Exception exception)
        {
            m_logger.ErrorException(message, exception);
        }

        public bool IsFatalEnabled
        {
            get { return m_logger.IsFatalEnabled; }
        }

        public void Fatal(string message)
        {
            m_logger.Fatal(message);
        }

        public void Fatal(object obj)
        {
            m_logger.Fatal(obj);
        }

        public void Fatal(string message, params object[] args)
        {
            m_logger.Fatal(message, args);
        }

        public void Fatal(string message, Exception exception)
        {
            m_logger.FatalException(message, exception);
        }

        public bool IsInfoEnabled
        {
            get { return m_logger.IsInfoEnabled; }
        }

        public void Info(string message)
        {
            m_logger.Info(message);
        }

        public void Info(object obj)
        {
            m_logger.Info(obj);
        }

        public void Info(string message, params object[] args)
        {
            m_logger.Info(message, args);
        }

        public void Info(string message, Exception exception)
        {
            m_logger.InfoException(message, exception);
        }

        public bool IsTraceEnabled
        {
            get { return m_logger.IsTraceEnabled; }
        }

        public void Trace(string message)
        {
            m_logger.Trace(message);
        }

        public void Trace(object obj)
        {
            m_logger.Trace(obj);
        }

        public void Trace(string message, params object[] args)
        {
            m_logger.Trace(message, args);
        }

        public void Trace(string message, Exception exception)
        {
            m_logger.TraceException(message, exception);
        }

        public bool IsWarnEnabled
        {
            get { return m_logger.IsWarnEnabled; }
        }

        public void Warn(string message)
        {
            m_logger.Warn(message);
        }

        public void Warn(object obj)
        {
            m_logger.Warn(obj);
        }

        public void Warn(string message, params object[] args)
        {
            m_logger.Warn(message, args);
        }

        public void Warn(string message, Exception exception)
        {
            m_logger.WarnException(message, exception);
        }
    }
}