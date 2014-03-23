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
using System;
using System.Collections.Generic;
using System.Linq;
using System.IO;
using Health.Direct.Agent;
using Health.Direct.Agent.Tests;
using Health.Direct.Common.Mail;
using Health.Direct.Config.Store;
using Health.Direct.Common.Container;
using Health.Direct.Common.Routing;
using Xunit;

namespace Health.Direct.SmtpAgent.Tests
{
    public class TestRouter : SmtpAgentTester
    {
        SmtpAgent m_agent;
        Dictionary<string, int> m_routeCounts;

        static TestRouter()
        {
            AgentTester.EnsureStandardMachineStores();
        }

        public TestRouter()
        {
            m_routeCounts = new Dictionary<string, int>();
        }

        [Fact]
        public void TestSerialization()
        {
            Assert.DoesNotThrow(() => EnsureAgent());
            Assert.True(m_agent.Router.Count >= 0);
            foreach (Route route in m_agent.Router)
            {
                Assert.DoesNotThrow(() => route.Validate());
            }
        }

        [Fact]
        public void TestBasic()
        {
            EnsureAgent();

            IncomingMessage envelope = this.CreateEnvelope();
            //
            // The idea here:
            //   1. We have multiple addresses of each type, and 1 of no type
            //   2. We want to make sure that the router:
            //      a. Picks a route
            //      b. Calls the route method exactly ONCE for each message folder
            //   3. Verify that at the end, the DomainRecipients has 1 recipient
            //
            m_agent.Router.Route(new DummySmtpMessage(), envelope, null);
            //
            // Verify counts
            //
            this.CheckRoutedCounts(1);

            Assert.True(envelope.HasDomainRecipients && envelope.DomainRecipients.Count == 1);
        }

        [Fact]
        public void TestRoundRobin()
        {
            EnsureAgent();
            IncomingMessage envelope = this.CreateEnvelope();

            foreach (FolderRoute route in m_agent.Router)
            {
                RoundRobinTest(route);
            }
        }

        [Fact]
        public void TestRouteFailureAll()
        {
            EnsureAgent();
            this.SetRouteHandlers(this.FailCopy);

            this.RouteCannedMessage();
            //
            // All routing should fail
            //
            this.CheckRoutedCounts(0);
            //
            // All routing should fail again
            //
            m_routeCounts.Clear();
            this.SetRouteHandlers(this.ThrowCopy);
            this.RouteCannedMessage();

            this.CheckRoutedCounts(0);
        }

        [Fact]
        public void TestRouteFailure()
        {
            EnsureAgent();
            m_routeCounts.Clear();
            //
            // We will force some routes to fail, then make sure the router can pick alternatives
            //
            this.SetRouteHandlers(this.ThrowCopyConditional);
            foreach (FolderRoute route in m_agent.Router)
            {
                this.SetRoutesToFail(route, 2);
            }
            this.RouteCannedMessage();
            this.CheckRoutedCounts(1);
        }

        void RoundRobinTest(FolderRoute route)
        {
            int folderCount = route.CopyFolders.Length;
            int prevFolder = -1;
            int folderIndex = -1;

            DummySmtpMessage message = new DummySmtpMessage();
            for (int i = 0; i <= folderCount; ++i)
            {
                folderIndex = -1;
                Assert.True(route.LoadBalancer.ProcessRoundRobin(message, out folderIndex));
                if (i == folderCount)
                {
                    Assert.True(folderIndex == 0);
                }
                else
                {
                    Assert.True(folderIndex > prevFolder && folderIndex < folderCount);
                }
                prevFolder = folderIndex;
            }
        }

        void AssignTypesToRecipients(DirectAddressCollection recipients)
        {
            Address address;
            foreach (DirectAddress recipient in recipients)
            {
                address = null;
                switch (recipient.Host.ToLower())
                {
                    default:
                        break;

                    case "nhind.hsgincubator.com":
                        if (!recipient.User.Equals("frank", StringComparison.OrdinalIgnoreCase))
                        {
                            address = new Address();
                            address.Type = "SMTP";
                        }
                        break;

                    case "redmond.hsgincubator.com":
                        address = new Address();
                        address.Type = "XDR";
                        break;
                }

                recipient.Tag = address;
            }
        }

        IncomingMessage CreateEnvelope()
        {
            Message message = Message.Load(MultiToMessage);
            IncomingMessage envelope = new IncomingMessage(message);
            envelope.EnsureRecipientsCategorizedByDomain(m_agent.Domains);
            this.AssignTypesToRecipients(envelope.DomainRecipients);
            return envelope;
        }

        void InitRouter()
        {
            LoadAgent();
        }

        void SetRouteHandlers(Func<ISmtpMessage, string, bool> handler)
        {
            foreach (FolderRoute route in m_agent.Router)
            {
                route.CopyMessageHandler = handler;
            }
        }

        //
        // Set routes that have backup/redundant folders to fail partially
        //
        void SetRoutesToFail(FolderRoute route, int failureFraction)
        {
            int countToFail = route.CopyFolders.Length / failureFraction;
            for (int i = 0; i < countToFail; ++i)
            {
                route.CopyFolders[i] = string.Empty;
            }
        }

        void SetRoutesToFailRandom(FolderRoute route, int failureFraction)
        {
            Random random = new Random();
            int countToFail = route.CopyFolders.Length / failureFraction;
            for (int i = 0; i < countToFail; ++i)
            {
                int failIndex = random.Next(0, route.CopyFolders.Length);
                route.CopyFolders[failIndex] = string.Empty;
            }
        }

        void RouteCannedMessage()
        {
            IncomingMessage envelope = this.CreateEnvelope();
            m_agent.Router.Route(new DummySmtpMessage(), envelope, null);
        }

        void CheckRoutedCounts(int expectedCount)
        {
            foreach (int count in m_routeCounts.Values)
            {
                Assert.True(count == expectedCount);
            }
        }

        void EnsureAgent()
        {
            if (m_agent == null)
            {
                m_agent = LoadAgent();
            }

            this.SetRouteHandlers(this.MessageCopy);
        }

        SmtpAgent LoadAgent()
        {
            return LoadAgent("TestSmtpAgentConfig.xml");
        }

        SmtpAgent LoadAgent(string configFile)
        {
            return SmtpAgentFactory.Create(GetSettingsPath(configFile));
        }

        bool MessageCopy(ISmtpMessage message, string destinationFolder)
        {
            this.UpdateRouteCount(destinationFolder);
            return true;
        }

        void UpdateRouteCount(string destinationFolder)
        {
            int count = 0;
            if (!m_routeCounts.TryGetValue(destinationFolder, out count))
            {
                count = 1;
                m_routeCounts[destinationFolder] = count;
            }
            else
            {
                m_routeCounts[destinationFolder] = count++;
            }

        }

        bool FailCopy(ISmtpMessage message, string destinationFolder)
        {
            return false;
        }

        bool ThrowCopy(ISmtpMessage message, string destinationFolder)
        {
            throw new DirectoryNotFoundException(destinationFolder);
        }

        bool ThrowCopyConditional(ISmtpMessage message, string destinationFolder)
        {
            if (string.IsNullOrEmpty(destinationFolder))
            {
                throw new DirectoryNotFoundException(destinationFolder);
            }

            this.UpdateRouteCount(destinationFolder);
            return true;
        }
    }

    public class TestExtendedRoutes : SmtpAgentTester
    {
        [Fact]
        public void TestSmtpValidation()
        {
            SmtpMessageForwarder forwarder = new SmtpMessageForwarder();
            SmtpSettings settings = new SmtpSettings();
            Assert.Throws<SmtpAgentException>(() => forwarder.Settings = null);
            Assert.Throws<SmtpAgentException>(() => forwarder.Settings = settings);
            settings.Server = "foo";
            Assert.DoesNotThrow(() => forwarder.Settings = settings);
        }

        [Fact]
        public void TestPluginValidation()
        {
            PluginRoute plugin = new PluginRoute();
            Assert.Throws<SmtpAgentException>(() => plugin.Validate());
        }

        [Fact]
        public void TestFromXml()
        {
            SmtpAgent agent = null;

            Assert.DoesNotThrow(() => agent = SmtpAgentFactory.Create(GetSettingsPath("TestPlugin.xml")));
            Assert.True(agent.Router.Count == 3);

            Route[] routes = agent.Router.ToArray();

            ValidateHttpReceivers(routes[0], 2, "http://foo/one");
            ValidateHttpReceivers(routes[1], 1, "http://bar/one");
            ValidateSmtpReceivers(routes[2], 2, "foo.xyz");

            //
            // Pump a few messages through 
            //
            CDOSmtpMessage message = new CDOSmtpMessage(base.LoadMessage(MultiToMessage));
            for (int i = 0; i < 4; ++i)
            {
                for (int j = 0; j < routes.Length - 1; ++j) // Not testing the last route, which is Smtp
                {
                    Assert.True(routes[j].Process(message));
                }
            }
        }

        void ValidateSmtpReceivers(Route route, int count, string server)
        {
            PluginRoute pluginRoute = route as PluginRoute;
            Assert.NotNull(pluginRoute);
            Assert.NotNull(pluginRoute.Receivers);
            Assert.True(pluginRoute.Receivers.Length == count);
            for (int i = 0; i < pluginRoute.Receivers.Length; ++i)
            {
                SmtpMessageForwarder smtp = pluginRoute.Receivers[i] as SmtpMessageForwarder;
                Assert.NotNull(smtp);
                Assert.NotNull(smtp.Settings);
                if (i == 0)
                {
                    Assert.True(smtp.Settings.Server == server);
                }
            }
        }

        void ValidateHttpReceivers(Route route, int count, string url)
        {
            PluginRoute pluginRoute = route as PluginRoute;
            Assert.NotNull(pluginRoute);
            Assert.NotNull(pluginRoute.Receivers);
            Assert.True(pluginRoute.Receivers.Length == count);
            for (int i = 0; i < pluginRoute.Receivers.Length; ++i)
            {
                HttpReceiver httpReceiver = pluginRoute.Receivers[i] as HttpReceiver;
                Assert.NotNull(httpReceiver);
                Assert.NotNull(httpReceiver.Settings);
                if (i == 0)
                {
                    Assert.True(httpReceiver.Settings.Url == url);
                }
            }
        }
    }

    public class HttpReceiver : IPlugin, IReceiver<ISmtpMessage>
    {
        public HttpReceiver()
        {
        }

        public HttpSettings Settings;

        public void Init(PluginDefinition pluginDef)
        {
            this.Settings = pluginDef.DeserializeSettings<HttpSettings>();
        }

        public bool Receive(ISmtpMessage data)
        {
            return (this.Settings.Succeed && !string.IsNullOrEmpty(data.GetMessageText()));
        }
    }

    public class HttpSettings
    {
        public string Url;
        public int Timeout;
        public bool Succeed = true;
    }
}