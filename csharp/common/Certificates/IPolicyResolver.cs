/* 
 Copyright (c) 2014, Direct Project
 All rights reserved.

 Authors:
    Joe Shook     Joseph.Shook@Surescipts.com
  
Redistribution and use in source and binary forms, with or without modification, are permitted provided that the following conditions are met:

Redistributions of source code must retain the above copyright notice, this list of conditions and the following disclaimer.
Redistributions in binary form must reproduce the above copyright notice, this list of conditions and the following disclaimer in the documentation and/or other materials provided with the distribution.
Neither the name of The Direct Project (directproject.org) nor the names of its contributors may be used to endorse or promote products derived from this software without specific prior written permission.
THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 
*/


using System;
using System.Collections.Generic;
using System.Configuration;
using System.Linq;
using System.Net.Mail;
using System.Security.Cryptography.X509Certificates;
using System.Text;
using System.Threading.Tasks;
using Health.Direct.Policy;

namespace Health.Direct.Common.Certificates
{
    /// <summary>
    /// Supports resolution of policies
    /// If no policy for address, methods retur null
    /// Throws exception if there is an error during retrieval, such as network issues
    /// Implementations may use implementation specific caching policies
    /// </summary>
    public interface IPolicyResolver
    {
        /// <summary>
        /// Return the certificate policies for 
        /// </summary>
        /// <param name="address"></param>
        /// <returns></returns>
        IList<IPolicyExpression> GetOutgoingPolicy(MailAddress address);

        /// <summary>
        /// 
        /// </summary>
        /// <param name="address"></param>
        /// <returns></returns>
        IList<IPolicyExpression> GetIncomingPolicy(MailAddress address);
    }

    public class PolicyResolver : IPolicyResolver
    {


        public IList<IPolicyExpression> GetOutgoingPolicy(MailAddress address)
        {
            throw new NotImplementedException();
        }

        public IList<IPolicyExpression> GetIncomingPolicy(MailAddress address)
        {
            throw new NotImplementedException();
        }
    }
}
