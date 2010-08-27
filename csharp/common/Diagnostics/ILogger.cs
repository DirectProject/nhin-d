/* 
 Copyright (c) 2010, NHIN Direct Project
 All rights reserved.

 Authors:
    John Theisen     john.theisen@kryptiq.com
  
Redistribution and use in source and binary forms, with or without modification, are permitted provided that the following conditions are met:

Redistributions of source code must retain the above copyright notice, this list of conditions and the following disclaimer.
Redistributions in binary form must reproduce the above copyright notice, this list of conditions and the following disclaimer in the documentation and/or other materials provided with the distribution.
Neither the name of the The NHIN Direct Project (nhindirect.org). nor the names of its contributors may be used to endorse or promote products derived from this software without specific prior written permission.
THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 
*/
using System;

namespace NHINDirect.Diagnostics
{
	public interface ILogger
	{
		bool IsDebugEnabled();
		void Debug(string message);
		void Debug(string message, params object[] args);
		void Debug(string message, Exception exception);

		bool IsErrorEnabled();
		void Error(string message);
		void Error(string message, params object[] args);
		void Error(string message, Exception exception);

		bool IsFatalEnabled();
		void Fatal(string message);
		void Fatal(string message, params object[] args);
		void Fatal(string message, Exception exception);

		bool IsInfoEnabled();
		void Info(string message);
		void Info(string message, params object[] args);
		void Info(string message, Exception exception);

		bool IsTraceEnabled();
		void Trace(string message);
		void Trace(string message, params object[] args);
		void Trace(string message, Exception exception);

		bool IsWarnEnabled();
		void Warn(string message);
		void Warn(string message, params object[] args);
		void Warn(string message, Exception exception);
	}
}
