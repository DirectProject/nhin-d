/* 
 Copyright (c) 2014, Direct Project
 All rights reserved.

 Authors:
    Joe Shook     Joseph.Shook@Surescripts.com
  
Redistribution and use in source and binary forms, with or without modification, are permitted provided that the following conditions are met:

Redistributions of source code must retain the above copyright notice, this list of conditions and the following disclaimer.
Redistributions in binary form must reproduce the above copyright notice, this list of conditions and the following disclaimer in the documentation and/or other materials provided with the distribution.
Neither the name of The Direct Project (directproject.org) nor the names of its contributors may be used to endorse or promote products derived from this software without specific prior written permission.
THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 
*/
using System;
using System.Collections.Generic;
using System.IO;
using System.Linq;
using System.Reflection;
using System.Text;
using System.Threading.Tasks;
using Health.Direct.Agent;
using Health.Direct.Agent.Tests;
using Health.Direct.Common.Container;
using Health.Direct.Common.Diagnostics;
using Health.Direct.DatabaseAuditor;
using Health.Direct.SmtpAgent.Config;
using Health.Direct.SmtpAgent.Diagnostics;
using Moq;
using Org.BouncyCastle.Asn1.Cms;
using Xunit;

namespace Health.Direct.SmtpAgent.Tests
{
    public class TestAuditor : SmtpAgentTester
    {
        SmtpAgent m_agent;
        static AuditorSettings m_settings;

        static TestAuditor()
        {
            AgentTester.EnsureStandardMachineStores();
            m_settings = AuditorSettings.Load("DatabaseAuditorSettings.xml");
        }


        public TestAuditor()
        {
            CleanAuditEventTable();

            string programData = Path.Combine(Environment.GetFolderPath(Environment.SpecialFolder.CommonApplicationData), @"DirectProject\auditors");
            string myDataPath = Path.Combine(programData, "DatabaseAuditorSettings.xml");
            Directory.CreateDirectory(programData);
            File.Copy("DatabaseAuditorSettings.xml", myDataPath, true);
        }

        [Fact]
        public void Test()
        {
            string configPath = GetSettingsPath("TestSmtpAgentAuditConfig.xml");
            SmtpAgentSettings settings = SmtpAgentSettings.LoadSettings(configPath);
            m_agent = SmtpAgentFactory.Create(settings);
            Assert.DoesNotThrow(() => m_agent.ProcessMessage(this.LoadMessage(string.Format(TestMessage, Guid.NewGuid()))));
            Assert.Throws<OutgoingAgentException>(() => m_agent.ProcessMessage(this.LoadMessage(BadMessage)));
        }

        [Fact]
        public void TestEndToEndInternalMessage()
        {
            string configPath = GetSettingsPath("TestSmtpAgentAuditConfig.xml");
            SmtpAgentSettings settings = SmtpAgentSettings.LoadSettings(configPath);
            m_agent = SmtpAgentFactory.Create(settings);

            Assert.True(IoC.Resolve<IAuditor<IBuildAuditLogMessage>>() != null);
            Assert.Equal(0, AuditEventCount);
            m_agent.Settings.InternalMessage.EnableRelay = true;
            Assert.DoesNotThrow(() => RunEndToEndTest(this.LoadMessage(string.Format(TestMessage, Guid.NewGuid()))));
            Assert.DoesNotThrow(() => RunEndToEndTest(this.LoadMessage(CrossDomainMessage)));
            m_agent.Settings.InternalMessage.EnableRelay = false;
            Assert.Equal(4, AuditEventCount);
        }

        [Fact]
        public void TestBad_DatabaseAuditorSettings()
        {
            string configPath =  GetSettingsPath("TestSmtpAgentAuditConfig_BadAuditor_Defaults.xml");
            SmtpAgentSettings settings = SmtpAgentSettings.LoadSettings(configPath);
            m_agent = SmtpAgentFactory.Create(settings);

            Assert.Equal(0, AuditEventCount);
            m_agent.Settings.InternalMessage.EnableRelay = true;
            //
            // Failes to load DatabaseAuditor.Auditor and defaults to EventLogAuditor
            //
            Assert.True(IoC.Resolve<IAuditor<IBuildAuditLogMessage>>() is SmtpAgentEventLogAuditor);
            Assert.DoesNotThrow(() => RunEndToEndTest(this.LoadMessage(string.Format(TestMessage, Guid.NewGuid()))));
            m_agent.Settings.InternalMessage.EnableRelay = false;
            Assert.Equal(0, AuditEventCount);
        }

        [Fact]
        public void TestMissing_DatabaseAuditorSettings()
        {
            string configPath = GetSettingsPath("TestSmtpAgentAuditConfig.xml");
            SmtpAgentSettings settings = SmtpAgentSettings.LoadSettings(configPath);
            SimpleComponentSettings[] components = new SimpleComponentSettings[1];
            Mock<SimpleComponentSettings>  auditComponentMock = new Mock<SimpleComponentSettings>(){CallBase = true};
            auditComponentMock.SetupAllProperties();
            components[0] = auditComponentMock.Object;
            components[0].Scope = InstanceScope.Singleton;
            components[0].Service = "Health.Direct.SmtpAgent.Diagnostics.IAuditor`1[[Health.Direct.SmtpAgent.Diagnostics.IBuildAuditLogMessage, Health.Direct.SmtpAgent]], Health.Direct.SmtpAgent";
            components[0].Type = "Health.Direct.SmtpAgent.Tests.LocalTestAuditorSettingsMissing`1[[Health.Direct.SmtpAgent.Tests.LocalBuildAuditLogMessage, Health.Direct.SmtpAgent.Tests]], Health.Direct.SmtpAgent.Tests";
            settings.Container.Components = components;
            

            m_agent = SmtpAgentFactory.Create(settings);

            //
            // Not really asserting the exception.  It would take some architecture changes to get the IoC in a more testable state.
            //
            auditComponentMock.Verify(c => c.CreateInstance(), Times.Once );

            Assert.Equal(0, AuditEventCount);
            m_agent.Settings.InternalMessage.EnableRelay = true;

            // 
            // Failes to find connection string info in DatabaseAuditorSettings.xml file so loads default EventLogAuditor
            //
            Assert.True(IoC.Resolve<IAuditor<IBuildAuditLogMessage>>() is SmtpAgentEventLogAuditor);
            Assert.DoesNotThrow(() => RunEndToEndTest(this.LoadMessage(string.Format(TestMessage, Guid.NewGuid()))));
            m_agent.Settings.InternalMessage.EnableRelay = false;
            Assert.Equal(0, AuditEventCount);
        }

        /// <summary>
        /// <see cref="System.Type.GetType()"/> for explanation of string format
        /// </summary>
        [Fact]
        public void LoadGenericTypeTest()
        {
            var genericType = System.Type.GetType("Health.Direct.SmtpAgent.Diagnostics.IAuditor`1[[Health.Direct.SmtpAgent.Diagnostics.IBuildAuditLogMessage, Health.Direct.SmtpAgent]], Health.Direct.SmtpAgent", true);
            Assert.NotNull(genericType);
        }

        [Fact]
        public void Test_LocalAuditorSettings()
        {
            string configPath = GetSettingsPath("TestSmtpAgentAuditConfig.xml");
            SmtpAgentSettings settings = SmtpAgentSettings.LoadSettings(configPath);
            SimpleComponentSettings[] components = new SimpleComponentSettings[1];
            

            SimpleComponentSettings localAuditComponent = new SimpleComponentSettings();
            localAuditComponent.Scope = InstanceScope.Singleton;
            localAuditComponent.Service = "Health.Direct.SmtpAgent.Diagnostics.IAuditor`1[[Health.Direct.SmtpAgent.Diagnostics.IBuildAuditLogMessage, Health.Direct.SmtpAgent]], Health.Direct.SmtpAgent";
            localAuditComponent.Type = "Health.Direct.SmtpAgent.Tests.LocalTestAuditor`1[[Health.Direct.SmtpAgent.Tests.LocalBuildAuditLogMessage, Health.Direct.SmtpAgent.Tests]], Health.Direct.SmtpAgent.Tests";
            components[0] = localAuditComponent;
            
            settings.Container.Components = components;
            m_agent = SmtpAgentFactory.Create(settings);
            Assert.True(IoC.Resolve<IAuditor<IBuildAuditLogMessage>>() is LocalTestAuditor<LocalBuildAuditLogMessage>);
            
            Assert.Equal(0, AuditEventCount);
            m_agent.Settings.InternalMessage.EnableRelay = true;
            Assert.DoesNotThrow(() => RunEndToEndTest(this.LoadMessage(string.Format(TestMessage, Guid.NewGuid()))));
            Assert.DoesNotThrow(() => RunEndToEndTest(this.LoadMessage(CrossDomainMessage)));
            m_agent.Settings.InternalMessage.EnableRelay = false;
            Assert.Equal(4, AuditEventCount);
        }


        [Fact]
        public void TestEndToEndInternalMessage_WithMultipleAuditors()
        {
            string configPath = GetSettingsPath("TestSmtpAgentAuditConfig.xml");
            SmtpAgentSettings settings = SmtpAgentSettings.LoadSettings(configPath);

            SimpleComponentSettings[] components = new SimpleComponentSettings[2];

            //
            // Retain configured
            //
            components[0] = settings.Container.Components[0];

            //
            // Add a second auditor
            //
            SimpleComponentSettings localAuditComponent = new SimpleComponentSettings();
            localAuditComponent.Scope = InstanceScope.Singleton;
            localAuditComponent.Service = "Health.Direct.SmtpAgent.Diagnostics.IAuditor`1[[Health.Direct.SmtpAgent.Diagnostics.IBuildAuditLogMessage, Health.Direct.SmtpAgent]], Health.Direct.SmtpAgent";
            localAuditComponent.Type = "Health.Direct.SmtpAgent.Tests.LocalTestAuditor`1[[Health.Direct.SmtpAgent.Tests.LocalBuildAuditLogMessage, Health.Direct.SmtpAgent.Tests]], Health.Direct.SmtpAgent.Tests";
            components[1] = localAuditComponent;

            settings.Container.Components = components;
            m_agent = SmtpAgentFactory.Create(settings);

            Assert.True(IoC.Resolve<IAuditor<IBuildAuditLogMessage>>() != null);
            Assert.Equal(0, AuditEventCount);
            m_agent.Settings.InternalMessage.EnableRelay = true;
            Assert.DoesNotThrow(() => RunEndToEndTest(this.LoadMessage(string.Format(TestMessage, Guid.NewGuid()))));
            Assert.DoesNotThrow(() => RunEndToEndTest(this.LoadMessage(CrossDomainMessage)));
            m_agent.Settings.InternalMessage.EnableRelay = false;
            Assert.Equal(8, AuditEventCount);
        }

        [Fact]
        public void Test_DatabaseAuditor_FullAuditMessageBuilder()
        {
            string configPath = GetSettingsPath("TestSmtpAgentAuditConfig.xml");
            SmtpAgentSettings settings = SmtpAgentSettings.LoadSettings(configPath);
            SimpleComponentSettings[] components = new SimpleComponentSettings[1];


            SimpleComponentSettings localAuditComponent = new SimpleComponentSettings();
            localAuditComponent.Scope = InstanceScope.Singleton;
            localAuditComponent.Service = "Health.Direct.SmtpAgent.Diagnostics.IAuditor`1[[Health.Direct.SmtpAgent.Diagnostics.IBuildAuditLogMessage, Health.Direct.SmtpAgent]], Health.Direct.SmtpAgent";
            localAuditComponent.Type = "Health.Direct.DatabaseAuditor.Auditor`1[[Health.Direct.DatabaseAuditor.FullAuditMessageBuilder, Health.Direct.DatabaseAuditor]], Health.Direct.DatabaseAuditor";
            components[0] = localAuditComponent;
            
            settings.Container.Components = components;
            m_agent = SmtpAgentFactory.Create(settings);

            
            var auditor = IoC.Resolve<IAuditor<IBuildAuditLogMessage>>();
            Assert.True(auditor is DatabaseAuditor.Auditor<FullAuditMessageBuilder>);

            Assert.Equal(0, AuditEventCount);
            m_agent.Settings.InternalMessage.EnableRelay = true;
            Assert.DoesNotThrow(() => RunEndToEndTest(this.LoadMessage(string.Format(TestMessage, Guid.NewGuid()))));
            Assert.DoesNotThrow(() => RunEndToEndTest(this.LoadMessage(CrossDomainMessage)));
            m_agent.Settings.InternalMessage.EnableRelay = false;
            Assert.Equal(4, AuditEventCount);

            using (var db = new AuditContext().CreateContext(m_settings))
            {
                foreach (AuditEvent auditEvent in db.AuditEvents)
                {
                    Console.WriteLine(auditEvent.Message);
                }
            }
        }

        [Fact]
        public void Test_DatabaseAuditor_HeaderAuditMessageBuilder()
        {
            string configPath = GetSettingsPath("TestSmtpAgentAuditConfig.xml");
            SmtpAgentSettings settings = SmtpAgentSettings.LoadSettings(configPath);
            SimpleComponentSettings[] components = new SimpleComponentSettings[1];


            SimpleComponentSettings localAuditComponent = new SimpleComponentSettings();
            localAuditComponent.Scope = InstanceScope.Singleton;
            localAuditComponent.Service = "Health.Direct.SmtpAgent.Diagnostics.IAuditor`1[[Health.Direct.SmtpAgent.Diagnostics.IBuildAuditLogMessage, Health.Direct.SmtpAgent]], Health.Direct.SmtpAgent";
            localAuditComponent.Type = "Health.Direct.DatabaseAuditor.Auditor`1[[Health.Direct.DatabaseAuditor.HeaderAuditMessageBuilder, Health.Direct.DatabaseAuditor]], Health.Direct.DatabaseAuditor";
            components[0] = localAuditComponent;

            settings.Container.Components = components;
            m_agent = SmtpAgentFactory.Create(settings);
            Assert.True(IoC.Resolve<IAuditor<IBuildAuditLogMessage>>() is DatabaseAuditor.Auditor<HeaderAuditMessageBuilder>);

            Assert.Equal(0, AuditEventCount);
            m_agent.Settings.InternalMessage.EnableRelay = true;
            Assert.DoesNotThrow(() => RunEndToEndTest(this.LoadMessage(string.Format(TestMessage, Guid.NewGuid()))));
            Assert.DoesNotThrow(() => RunEndToEndTest(this.LoadMessage(CrossDomainMessage)));
            m_agent.Settings.InternalMessage.EnableRelay = false;
            Assert.Equal(4, AuditEventCount);

            using (var db = new AuditContext().CreateContext(m_settings))
            {
                foreach (AuditEvent auditEvent in db.AuditEvents)
                {
                    Console.WriteLine(auditEvent.Message);
                }
            }
        }



        CDO.Message RunEndToEndTest(CDO.Message message)
        {
            m_agent.ProcessMessage(message);
            message = this.LoadMessage(message);
            base.VerifyOutgoingMessage(message);

            m_agent.ProcessMessage(message);
            message = this.LoadMessage(message);

            if (m_agent.Settings.InternalMessage.EnableRelay)
            {
                base.VerifyIncomingMessage(message);
            }
            else
            {
                base.VerifyOutgoingMessage(message);
            }
            return message;
        }

        private int AuditEventCount
        {
            get
            {
                using (var db = new AuditContext().CreateContext(m_settings))
                {
                    return db.AuditEvents.Count();
                }
            }
        }
        private void CleanAuditEventTable()
        {
            using (var db = new AuditContext().CreateContext(m_settings))
            {
                db.ObjectContext.ExecuteStoreCommand("Delete From AuditEvents");
                db.SaveChanges();
            }
        }
    }

    public class LocalTestAuditorSettingsMissing<T> : IAuditor<IBuildAuditLogMessage> where T : IBuildAuditLogMessage, new()
    {
        static AuditorSettings m_settings;

        public LocalTestAuditorSettingsMissing()
        {
            m_settings = AuditorSettings.Load("DatabaseAuditorSettingsMissing.xml");
            BuildAuditLogMessage = new T();
        }

        public void Log(string category)
        {
            using (var db = new AuditContext().CreateContext(m_settings))
            {
                AuditEvent auditEvent = new AuditEvent(category);
                db.AuditEvents.Add(auditEvent);
                db.SaveChanges();
            }
        }


        public void Log(string category, string message)
        {
            using (var db = new AuditContext().CreateContext(m_settings))
            {
                AuditEvent auditEvent = new AuditEvent(category, message);
                db.AuditEvents.Add(auditEvent);
                db.SaveChanges();
            }
        }

        public IBuildAuditLogMessage BuildAuditLogMessage { get; private set; }
    }

    public class LocalTestAuditor<T> : IAuditor<IBuildAuditLogMessage> where T : IBuildAuditLogMessage, new()
    {
        static AuditorSettings m_settings;

        
        public LocalTestAuditor()
        {
            m_settings = AuditorSettings.Load("DatabaseAuditorSettings.xml");
            BuildAuditLogMessage = new T();
        }

        public void Log(string category)
        {
            using (var db = new AuditContext().CreateContext(m_settings))
            {
                AuditEvent auditEvent = new AuditEvent(category);
                db.AuditEvents.Add(auditEvent);
                db.SaveChanges();
            }
        }


        public void Log(string category, string message)
        {
            using (var db = new AuditContext().CreateContext(m_settings))
            {
                AuditEvent auditEvent = new AuditEvent(category, message);
                db.AuditEvents.Add(auditEvent);
                db.SaveChanges();
            }
        }

        public IBuildAuditLogMessage BuildAuditLogMessage { get; private set; }
    }

    public class LocalBuildAuditLogMessage : IBuildAuditLogMessage
    {
        public string Build(ISmtpMessage message)
        {
            MessageEnvelope envelope = message.GetEnvelope();
            return string.Format("MAILFROM={0};RCPTTO={1}", envelope.Message.FromValue, envelope.Message.ToValue);
        }
    }
}
