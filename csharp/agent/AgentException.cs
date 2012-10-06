/* 
 Copyright (c) 2010, Direct Project
 All rights reserved.

 Authors:
    Umesh Madan     umeshma@microsoft.com
  
Redistribution and use in source and binary forms, with or without modification, are permitted provided that the following conditions are met:

Redistributions of source code must retain the above copyright notice, this list of conditions and the following disclaimer.
Redistributions in binary form must reproduce the above copyright notice, this list of conditions and the following disclaimer in the documentation and/or other materials provided with the distribution.
Neither the name of The Direct Project (directproject.org) nor the names of its contributors may be used to endorse or promote products derived from this software without specific prior written permission.
THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 
*/
using Health.Direct.Common;

namespace Health.Direct.Agent
{
    /// <summary>
    /// Represents agent exceptions.
    /// </summary>
    public class AgentException : DirectException<AgentError>
    {
        /// <summary>
        /// Creates an exception with an associated agent error status.
        /// </summary>
        /// <param name="error">The <see cref="AgentError"/> status</param>
        public AgentException(AgentError error)
            : base(error)
        {
        }

        /// <summary>
        /// Creates an exception with an associated agent error status and custom message
        /// </summary>
        /// <param name="error">The <see cref="AgentError"/> status</param>
        /// <param name="message">The custom message for this error</param>
        public AgentException(AgentError error, string message)
            : base(error, message)
        {
        }
        
    }


    /// <summary>
    /// Represents agent exceptions for outgoing message.
    /// </summary>
    public class OutgoingAgentException : AgentException
    {
        /// <summary>
        /// Creates an exception with an associated agent error status.
        /// </summary>
        /// <param name="error">The <see cref="AgentError"/> status</param>
        public OutgoingAgentException(AgentError error)
            : base(error)
        {
        }

        /// <summary>
        /// Creates an exception with an associated agent error status and custom message
        /// </summary>
        /// <param name="error">The <see cref="AgentError"/> status</param>
        /// <param name="message">The custom message for this error</param>
        public OutgoingAgentException(AgentError error, string message)
            : base(error, message)
        {
        }

    }
}