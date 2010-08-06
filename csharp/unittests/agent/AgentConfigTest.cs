using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using NUnit.Framework;
using NHINDirect.Agent;
using NHINDirect.Agent.Config;

namespace AgentTests
{
    [TestFixture]
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
            </AgentSettings>
        ";
        
        [SetUp]
        public void Init()
        {
            AgentTester.EnsureStandardMachineStores();
        }
           
        [Test]
        public void TestConfig()
        {
            AgentSettings settings = AgentSettings.Load(TestXml);   
            NHINDAgent agent = settings.CreateAgent();
        }
    }
}
