using System;
using System.Collections.Generic;
using System.Linq;
using System.Security.Cryptography.X509Certificates;
/* 
 Copyright (c) 2013, Direct Project
 All rights reserved.

 Authors:
    Joseph Shook    jshook@kryptiq.com
  
Redistribution and use in source and binary forms, with or without modification, are permitted provided that the following conditions are met:

Redistributions of source code must retain the above copyright notice, this list of conditions and the following disclaimer.
Redistributions in binary form must reproduce the above copyright notice, this list of conditions and the following disclaimer in the documentation and/or other materials provided with the distribution.
Neither the name of The Direct Project (directproject.org) nor the names of its contributors may be used to endorse or promote products derived from this software without specific prior written permission.
THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 
*/

using System.Text;
using Health.Direct.Common.Certificates;
using Health.Direct.Common.Extensions;

namespace Health.Direct.ModSpec3.ResolverPlugins.Cert
{
    public class Validator
    {

        public static X509Certificate2Collection FilterValidCerts(
            X509Certificate2Collection certs, 
            X509ChainPolicy policy, 
            X509ChainStatusFlags problemFlags,
            Action<Exception> notification)
        {
            var validCerts = new X509Certificate2Collection();
            if (certs == null) return null;
            foreach (var cert in certs)
            {
                X509Chain chainBuilder = new X509Chain();
                chainBuilder.ChainPolicy = policy.Clone();

                try
                {
                    // We're using the system class as a helper to merely build the chain
                    // However, we will review each item in the chain ourselves, because we have our own rules...
                    chainBuilder.Build(cert);
                    X509ChainElementCollection chainElements = chainBuilder.ChainElements;

                    // If we don't have a trust chain, then we obviously have a problem...
                    if (chainElements.IsNullOrEmpty())
                    {
                        notification(new Exception(string.Format("Can't find a trust chain: {0} ", cert.Subject)));
                        return null;
                    }


                    // walk the chain starting at the leaf and see if we hit any issues before the anchor
                    foreach (X509ChainElement chainElement in chainElements)
                    {
                        if (ChainElementHasProblems(chainElement, problemFlags))
                        {
                            //this.NotifyProblem(chainElement);

                            notification(new Exception(string.Format("Chain Element has problem {0}", Summarize(chainElement, problemFlags))));
                            // Whoops... problem with at least one cert in the chain. Stop immediately
                            return null;
                        }
                    }
                }
                catch (Exception ex)
                {
                    //this.NotifyError(certificate, ex);
                    // just eat it and drop out to return null
                    notification(ex);
                    return null;
                }
                validCerts.Add(cert);
            }
            return validCerts;
        }


        static bool ChainElementHasProblems(X509ChainElement chainElement, X509ChainStatusFlags problemFlags)
        {
            // If the builder finds problems with the cert, it will provide a list of "status" flags for the cert
            X509ChainStatus[] chainElementStatus = chainElement.ChainElementStatus;

            // If the list is empty or the list is null, then there were NO problems with the cert
            if (chainElementStatus.IsNullOrEmpty())
            {
                return false;
            }

            // Return true if there are any status flags we care about
            return chainElementStatus.Any(s => (s.Status & problemFlags) != 0);
        }



        //this code comes from AgentDiagnostics.  Need a better way to hook ino Agent.
        //Agent hooks TrustChainValidator and the build in DnsCertResolver, but the DnsCertResolver is strongly 
        //linked rather than via interface for notifications and exceptions.
        static string Summarize(X509ChainElement chainElement, X509ChainStatusFlags problemFlags)
        {
            StringBuilder builder = new StringBuilder();
            builder.Append(chainElement.Certificate.ExtractEmailNameOrName());
            builder.Append(";");
            foreach (X509ChainStatus status in chainElement.ChainElementStatus)
            {
                if ((status.Status & problemFlags) != 0)
                {
                    builder.Append(status.Status);
                }
            }

            return builder.ToString();
        }
    }
}
