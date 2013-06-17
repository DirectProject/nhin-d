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
using System.Text;
using System.Xml.Serialization;
using Health.Direct.Common.Container;
using Health.Direct.Common.Extensions;
using System.IO;
using Xunit;
using Xunit.Extensions;

namespace Health.Direct.Common.Tests.Container
{
    public class PluginFacts
    {
        const string PluginXml = @"
            <PluginTest>
                <X>Foo</X>
                <Y>Bar</Y>
                <Plugin>
                    <TypeName>Health.Direct.Common.Tests.Container.DummyPlugin, Health.Direct.Common.Tests</TypeName>
                    <Settings>
                        <FirstName>Biff</FirstName>
                        <LastName>Hooper</LastName>
                    </Settings>
                </Plugin> 
                <Plugin>
                    <TypeName>Health.Direct.Common.Tests.Container.DummyPlugin, Health.Direct.Common.Tests</TypeName>
                    <Settings>
                        <FirstName>Toby</FirstName>
                        <LastName>McDuff</LastName>
                    </Settings>
                </Plugin> 
            </PluginTest>
        ";

        [Fact]
        public void TestXml()
        {
            XmlSerializer serializer = new XmlSerializer(typeof(DummyPluginContainer));
            using (StringReader reader = new StringReader(PluginXml))
            {
                DummyPluginContainer container = null;
                Assert.DoesNotThrow(() => container = (DummyPluginContainer)serializer.Deserialize(reader));
                Assert.False(container.Plugins.IsNullOrEmpty());
                foreach (PluginDefinition def in container.Plugins)
                {
                    Assert.True(def.HasSettings);
                }
                VerifyDummy(container.Plugins[0], "Biff");
                VerifyDummy(container.Plugins[1], "Toby");
            }
        }

        void VerifyDummy(PluginDefinition def, string expectedName)
        {
            DummyPlugin plugin = null;
            Assert.DoesNotThrow(() => plugin = def.Create<DummyPlugin>());
            Assert.True(plugin.Settings != null);
            Assert.True(plugin.Settings.FirstName == expectedName);
        }
    }

    [XmlType("PluginTest")]
    public class DummyPluginContainer
    {
        public string X;
        public string Y;
        [XmlElement("Plugin")]
        public PluginDefinition[] Plugins;
    }

    public class DummySettings
    {
        public DummySettings()
        {
        }
        public string FirstName;
        public string LastName;
    }

    public class DummyPlugin : IPlugin
    {
        public DummyPlugin()
        {
        }
        public DummySettings Settings
        {
            get;
            set;
        }
        public void Init(PluginDefinition pluginDef)
        {
            Assert.DoesNotThrow(() => this.Settings = pluginDef.DeserializeSettings<DummySettings>());
        }
    }
}
