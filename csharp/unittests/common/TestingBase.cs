/* 
 Copyright (c) 2010, Direct Project
 All rights reserved.

 Authors:
    Chris Lomonico chris.lomonico@surescripts.com
    John Theisen john.theisen@krypitq.com
    Umesh Madan umeshma@microsoft.com

  
Redistribution and use in source and binary forms, with or without modification, are permitted provided that the following conditions are met:

Redistributions of source code must retain the above copyright notice, this list of conditions and the following disclaimer.
Redistributions in binary form must reproduce the above copyright notice, this list of conditions and the following disclaimer in the documentation and/or other materials provided with the distribution.
Neither the name of The Direct Project (directproject.org) nor the names of its contributors may be used to endorse or promote products derived from this software without specific prior written permission.
THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
*/
using System;
using Xunit;
using Xunit.Extensions;

namespace Health.Direct.Common.Tests
{
    public class TestingBase
    {
        // if true dump will be sent to the delegate specified by DumpLine
        private readonly bool m_dumpEnabled;

        // the lines used to separate error and success respectively
        private const int PreambleWidth = 50;
        private static readonly string ErrorLinePreamble = new string('!', PreambleWidth);
        private static readonly string SuccessLinePreamble = new string('-', PreambleWidth);

        /// <summary>
        /// Default ctor. Will log to <see cref="Console.Out"/>.
        /// </summary>
        protected TestingBase() : this(true)
        {
        }

        /// <summary>
        /// Logs to <see cref="Console.Out"/> if <paramref name="dumpEnabled"/> is <c>true</c>.
        /// </summary>
        /// <param name="dumpEnabled"><c>true</c> if dump output will be display</param>
        protected TestingBase(bool dumpEnabled)
        {
            m_dumpEnabled = dumpEnabled;
        }

        /// <summary>
        /// Dump the <paramref name="msg"/> to the output with a preamble line of '!'s
        /// </summary>
        /// <remarks>
        /// Will not dump to output if the dump was disable with the ctor set false.
        /// </remarks>
        /// <param name="msg">The message to dump</param>
        protected void DumpError(string msg)
        {
            if (!m_dumpEnabled) return;

            DumpLine(ErrorLinePreamble);
            Dump(msg);
        }

        /// <summary>
        /// Dump the <paramref name="msg"/> to the output with a preamble line of '-'s
        /// </summary>
        /// <remarks>
        /// Will not dump to output if the dump was disable with the ctor set false.
        /// </remarks>
        /// <param name="msg">The message to dump</param>
        protected void DumpSuccess(string msg)
        {
            if (!m_dumpEnabled) return;

            DumpLine(SuccessLinePreamble);
            Dump(msg);
        }

        /// <summary>
        /// Dump the message created by calling <see cref="string.Format(string,object[])"/>
        /// with <paramref name="format"/> and <paramref name="args"/>. A preamble of '-'s will
        /// begin the message.
        /// </summary>
        /// <param name="format">The format of the message</param>
        /// <param name="args">The values to format</param>
        protected void DumpSuccess(string format, params object[] args)
        {
            DumpSuccess(string.Format(format, args));
        }

        /// <summary>
        /// Dump the <paramref name="msg"/> to the output. The output will include the timestamp in UTC.
        /// </summary>
        /// <remarks>
        /// Will not dump to output if the dump was disable with the ctor set false.
        /// </remarks>
        /// <param name="msg">The message to dump</param>
        protected void Dump(string msg)
        {
            if (!m_dumpEnabled) return;

            DumpLine(string.Format("{0:mm:ss.ff} - {1}", DateTime.UtcNow, msg));
        }

        /// <summary>
        /// Dump the message created by calling <see cref="string.Format(string,object[])"/> 
        /// with <paramref name="format"/> and <paramref name="args"/>.
        /// </summary>
        /// <param name="format">The format of the message</param>
        /// <param name="args">The values to format</param>
        protected void Dump(string format, params object[] args)
        {
            Dump(string.Format(format, args));
        }

        private static void DumpLine(string msg)
        {
            Console.Out.WriteLine(msg);
        }
    }
    
    public static class AssertEx
    {
        public static Exception Throws(Action action)
        {
            try
            { 
                action();
            }
            catch(Exception ex)
            {
                return ex;
            }
            
            Assert.True(false, "Method did not throw exception!");
            return null;
        }

        public static T Throws<T>(Action action)
            where T : Exception
        {
            try
            {
                try
                {
                    action();
                }
                catch(T ex)
                {
                    return ex;
                }
            }
            catch
            {
            }
                        
            Assert.True(false, string.Format("Method did not throw exception of type {0}", typeof(T)));
            
            return null;
        }
    }   
}