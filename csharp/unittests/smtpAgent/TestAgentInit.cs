using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.IO;
using NHINDirect.Agent;
using NHINDirect.SmtpAgent;
using AgentTests;
using Xunit;
using Xunit.Extensions;

namespace SmtpAgentTests
{
    /// <summary>
    /// Test various agent initializations
    /// </summary>
    public class TestAgentInit
    {
        MessageArrivalEventHandler m_handler;

        public TestAgentInit()
        {
            m_handler = new MessageArrivalEventHandler();
        }
        
        public static IEnumerable<object[]> ConfigFileNames
        {
            get
            {
                yield return new[] { "TestSmtpAgentConfig.xml" };
                yield return new[] { "TestSmtpAgentConfigService.xml" };
            }
        }
        
        [Theory(Skip = "Requires Config Service to be running on the local server")]
        [PropertyData("ConfigFileNames")]
        public void TestWithService(string fileName)
        {
            m_handler.InitFromConfigFile(Fullpath(fileName));            
        }
        
        string Fullpath(string fileName)
        {
            string folderPath = Path.Combine(Directory.GetCurrentDirectory(), "SmtpAgentTestFiles");
            return Path.Combine(folderPath, fileName);
        }
    }
}
