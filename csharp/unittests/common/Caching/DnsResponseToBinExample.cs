/* 
 Copyright (c) 2010, Direct Project
 All rights reserved.

 Authors:
    Chris Lomonico chris.lomonico@surescripts.com
  
Redistribution and use in source and binary forms, with or without modification, are permitted provided that the following conditions are met:

Redistributions of source code must retain the above copyright notice, this list of conditions and the following disclaimer.
Redistributions in binary form must reproduce the above copyright notice, this list of conditions and the following disclaimer in the documentation and/or other materials provided with the distribution.
Neither the name of the The Direct Project (nhindirect.org). nor the names of its contributors may be used to endorse or promote products derived from this software without specific prior written permission.
THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
*/
using System;
using System.IO;
using DnsResolver;

using Xunit.Extensions;

namespace Health.Direct.Common.Tests.Caching
{
    public class DnsResponseToBinExample
    {
        private readonly DnsClient m_client;
        private readonly string m_apppath = string.Empty;

        const string PublicDns = "8.8.8.8";         // Google


        public DnsResponseToBinExample(){
		
            m_client = new DnsClient(PublicDns) {Timeout = TimeSpan.FromSeconds(10) };
            m_apppath =Environment.CurrentDirectory + @"\metadata\dns responses";
        }

        [Theory]
        [InlineData("www.microsoft.com")]
        [InlineData("www.yahoo.com")]
        [InlineData("www.google.com")]
        [InlineData("www.apple.com")]
        [InlineData("www.bing.com")]
        [InlineData("nhind.hsgincubator.com")]
        [InlineData("hvnhind.hsgincubator.com")]
        [InlineData("www.nhindirect.org")]
        [InlineData("www.epic.com")]
        [InlineData("www.cerner.com")]
        [InlineData("www.ibm.com")]
        private void CreateAResponseDumps(string domain)
        {
            DnsBuffer buff = new DnsBuffer(DnsStandard.MaxUdpMessageLength * 2);
            m_client.Resolve(DnsRequest.CreateA(domain)).Serialize(buff);
            byte[] bytes = buff.CreateReader().ReadBytes();
            using (FileStream s = new FileStream(Path.Combine(m_apppath, string.Format("a.{0}.bin", domain)),FileMode.OpenOrCreate)){
                s.Write(bytes
                        , 0
                        , bytes.Length);
                s.Close();
            }

        }

        [Theory]
        [InlineData("www.microsoft.com")]
        [InlineData("www.yahoo.com")]
        [InlineData("www.google.com")]
        [InlineData("www.apple.com")]
        [InlineData("www.bing.com")]
        [InlineData("nhind.hsgincubator.com")]
        [InlineData("hvnhind.hsgincubator.com")]
        [InlineData("www.nhindirect.org")]
        [InlineData("www.epic.com")]
        [InlineData("www.cerner.com")]
        [InlineData("www.ibm.com")]
        private void CreateMXResponseDumps(string domain)
        {
            DnsBuffer buff = new DnsBuffer(DnsStandard.MaxUdpMessageLength * 2);
            m_client.Resolve(DnsRequest.CreateMX(domain)).Serialize(buff);
            byte[] bytes = buff.CreateReader().ReadBytes();
            using (FileStream s = new FileStream(Path.Combine(m_apppath, string.Format("mx.{0}.bin", domain)), FileMode.OpenOrCreate))
            {
                s.Write(bytes
                        , 0
                        , bytes.Length);
                s.Close();
            }
        }


    }
}