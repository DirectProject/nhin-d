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

using System.Collections.Generic;
using System.IO;
using System.Reflection;
using Xunit;
using Xunit.Extensions;
using Health.Direct.Common.Container;
using Health.Direct.Common.Diagnostics;
using Health.Direct.Agent.Config;
using Health.Direct.Common.Certificates;

namespace Health.Direct.SmtpAgent.Integration.Tests
{
    /// <summary>
    /// Test various agent initializations
    /// 
    /// To enabled these tests
    /// 
    /// run   ...\csharp\setenv.bat
    /// then  ...\csharp\msb.bat
    /// then  ...\csharp\gateway\devInstall\install_withservice.bat
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

        [Theory]
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