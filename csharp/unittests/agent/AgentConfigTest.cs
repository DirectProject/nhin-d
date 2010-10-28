/* 
 Copyright (c) 2010, Direct Project
 All rights reserved.

 Authors:
    Umesh Madan     umeshma@microsoft.com
    John Theisen    jtheisen@kryptiq.com
  
Redistribution and use in source and binary forms, with or without modification, are permitted provided that the following conditions are met:

Redistributions of source code must retain the above copyright notice, this list of conditions and the following disclaimer.
Redistributions in binary form must reproduce the above copyright notice, this list of conditions and the following disclaimer in the documentation and/or other materials provided with the distribution.
Neither the name of The Direct Project (directproject.org) nor the names of its contributors may be used to endorse or promote products derived from this software without specific prior written permission.
THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 
*/

using Health.Direct.Agent.Config;

using Xunit;

namespace Health.Direct.Agent.Tests
{
    public class AgentConfigTest
    {
        public const string TestXml = @"
            <AgentSettings>
                <Domain>exampledomain.com</Domain>
                <PrivateCerts>
                    <MachineResolver>
                        <Name>NHINDPrivate</Name>
                    </MachineResolver>
                </PrivateCerts>
                <PublicCerts>
                    <DnsResolver>
                        <ServerIP>8.8.8.8</ServerIP>
                        <Timeout>5000</Timeout>
                    </DnsResolver>
                </PublicCerts>
                <Anchors>
                    <MachineResolver>
                        <Incoming>
                            <Name>NHINDAnchors</Name>
                        </Incoming>
                        <Outgoing>
                            <Name>NHINDAnchors</Name>
                        </Outgoing>
                    </MachineResolver>
                </Anchors>
                <Trust>
                    <MaxIssuerChainLength>4</MaxIssuerChainLength>
                    <RevocationCheckMode>Offline</RevocationCheckMode>
                    <Timeout>10000</Timeout>
                    <ProblemFlags>
                      <Flag>NotTimeValid</Flag>
                      <Flag>Revoked</Flag>
                      <Flag>NotSignatureValid</Flag>
                      <Flag>InvalidBasicConstraints</Flag>
                      <Flag>CtlNotTimeValid</Flag>
                      <Flag>CtlNotSignatureValid</Flag>
                    </ProblemFlags>
                </Trust>
            </AgentSettings>
        ";
        
        static AgentConfigTest()
        {
            AgentTester.EnsureStandardMachineStores();
        }
        
        public AgentConfigTest()
        {
        }
           
        [Fact]
        public void TestConfig()
        {
            AgentSettings settings = AgentSettings.Load(TestXml);   
            DirectAgent agent = settings.CreateAgent();
        }
    }
}