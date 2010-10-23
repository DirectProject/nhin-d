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

namespace Health.Direct.Common.Diagnostics
{
    ///<summary>
    /// Provides logging interface and utility functions.
    ///</summary>
    public interface ILogger
    {
        ///<summary>
        /// Determines if logging is enabled for the Debug level.
        ///</summary>
        bool IsDebugEnabled { get; }
        ///<summary>
        /// Writes the diagnostic message at the Debug level.
        ///</summary>
        ///<param name="message">A <c>string</c> to be written.</param>
        void Debug(string message);

        ///<summary>
        /// Writes the diagnostic message at the Debug level.
        ///</summary>
        ///<param name="obj">An <c>object</c> to be written.</param>
        void Debug(object obj);

        ///<summary>
        /// Writes the diagnostic message at the Debug level using the specified parameters.
        ///</summary>
        ///<param name="message">A <c>string</c> containing format items.</param>
        ///<param name="args">Arguments to format</param>
        void Debug(string message, params object[] args);

        ///<summary>
        /// Writes the diagnostic message and exception at the Debug level.
        ///</summary>
        ///<param name="message">A <c>string</c> to be written.</param>
        ///<param name="exception">An exception to be logged.</param>
        void Debug(string message, Exception exception);

        ///<summary>
        /// Determines if logging is enabled for the Error level.
        ///</summary>
        bool IsErrorEnabled { get; }

        ///<summary>
        /// Writes the diagnostic message at the Error level.
        ///</summary>
        ///<param name="message">A <c>string</c> to be written.</param>
        void Error(string message);

        ///<summary>
        /// Writes the diagnostic message at the Error level.
        ///</summary>
        ///<param name="obj">An <c>object</c> to be written.</param>
        void Error(object obj);

        ///<summary>
        /// Writes the diagnostic message at the Error level using the specified parameters.
        ///</summary>
        ///<param name="message">A <c>string</c> containing format items.</param>
        ///<param name="args">Arguments to format</param>
        void Error(string message, params object[] args);

        ///<summary>
        /// Writes the diagnostic message and exception at the Error level.
        ///</summary>
        ///<param name="message">A <c>string</c> to be written.</param>
        ///<param name="exception">An exception to be logged.</param>
        void Error(string message, Exception exception);

        ///<summary>
        /// Determines if logging is enabled for the Fatal level.
        ///</summary>
        bool IsFatalEnabled { get; }

        ///<summary>
        /// Writes the diagnostic message at the Fatal level.
        ///</summary>
        ///<param name="message">A <c>string</c> to be written.</param>
        void Fatal(string message);

        ///<summary>
        /// Writes the diagnostic message at the Fatal level.
        ///</summary>
        ///<param name="obj">An <c>object</c> to be written.</param>
        void Fatal(object obj);

        ///<summary>
        /// Writes the diagnostic message at the Fatal level using the specified parameters.
        ///</summary>
        ///<param name="message">A <c>string</c> containing format items.</param>
        ///<param name="args">Arguments to format</param>
        void Fatal(string message, params object[] args);

        ///<summary>
        /// Writes the diagnostic message and exception at the Fatal level.
        ///</summary>
        ///<param name="message">A <c>string</c> to be written.</param>
        ///<param name="exception">An exception to be logged.</param>
        void Fatal(string message, Exception exception);

        ///<summary>
        /// Determines if logging is enabled for the Info level.
        ///</summary>
        bool IsInfoEnabled { get; }

        ///<summary>
        /// Writes the diagnostic message at the Info level.
        ///</summary>
        ///<param name="message">A <c>string</c> to be written.</param>
        void Info(string message);

        ///<summary>
        /// Writes the diagnostic message at the Info level.
        ///</summary>
        ///<param name="obj">An <c>object</c> to be written.</param>
        void Info(object obj);

        ///<summary>
        /// Writes the diagnostic message at the Info level using the specified parameters.
        ///</summary>
        ///<param name="message">A <c>string</c> containing format items.</param>
        ///<param name="args">Arguments to format</param>
        void Info(string message, params object[] args);

        ///<summary>
        /// Writes the diagnostic message and exception at the Info level.
        ///</summary>
        ///<param name="message">A <c>string</c> to be written.</param>
        ///<param name="exception">An exception to be logged.</param>
        void Info(string message, Exception exception);

        ///<summary>
        /// Determines if logging is enabled for the Trace level.
        ///</summary>
        bool IsTraceEnabled { get; }

        ///<summary>
        /// Writes the diagnostic message at the Trace level.
        ///</summary>
        ///<param name="message">A <c>string</c> to be written.</param>
        void Trace(string message);

        ///<summary>
        /// Writes the diagnostic message at the Trace level.
        ///</summary>
        ///<param name="obj">An <c>object</c> to be written.</param>
        void Trace(object obj);

        ///<summary>
        /// Writes the diagnostic message at the Trace level using the specified parameters.
        ///</summary>
        ///<param name="message">A <c>string</c> containing format items.</param>
        ///<param name="args">Arguments to format</param>
        void Trace(string message, params object[] args);

        ///<summary>
        /// Writes the diagnostic message and exception at the Trace level.
        ///</summary>
        ///<param name="message">A <c>string</c> to be written.</param>
        ///<param name="exception">An exception to be logged.</param>
        void Trace(string message, Exception exception);

        ///<summary>
        /// Determines if logging is enabled for the Warn level.
        ///</summary>
        bool IsWarnEnabled { get; }

        ///<summary>
        /// Writes the diagnostic message at the Warn level.
        ///</summary>
        ///<param name="message">A <c>string</c> to be written.</param>
        void Warn(string message);

        ///<summary>
        /// Writes the diagnostic message at the Warn level.
        ///</summary>
        ///<param name="obj">An <c>object</c> to be written.</param>
        void Warn(object obj);

        ///<summary>
        /// Writes the diagnostic message at the Warn level using the specified parameters.
        ///</summary>
        ///<param name="message">A <c>string</c> containing format items.</param>
        ///<param name="args">Arguments to format</param>
        void Warn(string message, params object[] args);

        ///<summary>
        /// Writes the diagnostic message and exception at the Warn level.
        ///</summary>
        ///<param name="message">A <c>string</c> to be written.</param>
        ///<param name="exception">An exception to be logged.</param>
        void Warn(string message, Exception exception);
    }
}