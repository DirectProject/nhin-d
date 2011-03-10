/* 
 Copyright (c) 2010, Direct Project
 All rights reserved.

 Authors:
    Umesh Madan     umeshma@microsoft.com

 * Redistribution and use in source and binary forms, with or without modification, are permitted provided that the following conditions are met:

Redistributions of source code must retain the above copyright notice, this list of conditions and the following disclaimer.
Redistributions in binary form must reproduce the above copyright notice, this list of conditions and the following disclaimer in the documentation and/or other materials provided with the distribution.
Neither the name of The Direct Project (directproject.org) nor the names of its contributors may be used to endorse or promote products derived from this software without specific prior written permission.
THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 
*/
using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.IO;
using Health.Direct.Common.Routing;
using Xunit;
using Xunit.Extensions;

namespace Health.Direct.Common.Tests.Routing
{
    //
    // NOTE: MORE COMPLETE TESTS are in SMTPAGENT
    //
    public class LoadBalancerFacts
    {
        string m_root;
        
        public LoadBalancerFacts()
        {
            m_root = EnsureRootFolder("routerUnitTest");
        }
        
        [Fact]
        public void TestNoRoute()
        {
            string receiverRoot = EnsureCleanFolder(Path.Combine(m_root, "receivers"));
            FolderBalancer<KeyValuePair<string, string>> balancer = new FolderBalancer<KeyValuePair<string,string>>(this.TextCopy);
            Assert.False(balancer.Process(new KeyValuePair<string,string>("foo", "Hello")));
        }
                
        [Theory]
        [InlineData(1)]
        [InlineData(2)]
        [InlineData(3)]
        [InlineData(4)]
        public void TestRoundRobin(int folderCount)
        {
            string receiverRoot = EnsureCleanFolder(Path.Combine(m_root, "receivers"));   
            FolderBalancer<KeyValuePair<string, string>> balancer = this.CreateBalancer(folderCount);
            
            int fileNumber = -1;
            //
            // These should all succeed
            //
            for (int i = 0; i < folderCount; ++i)
            {
                ++fileNumber;
                Process(balancer, fileNumber.ToString(), i);
            }
            
            ++fileNumber;
            Process(balancer, fileNumber.ToString(), 0);
        }

        public static IEnumerable<object[]> FailureParams
        {
            get
            {
                for (int i = 1; i <= 4; ++i)
                {
                    for (int j = 1; j <= i; ++j)
                    {
                        yield return new object[] {i, j};
                    }                    
                }
            }
        }
        
        [Theory]
        [PropertyData("FailureParams")]
        public void TestFailure(int folderCount, int failureCount)
        {
            string receiverRoot = EnsureCleanFolder(Path.Combine(m_root, "receivers"));
            FolderBalancer<KeyValuePair<string, string>> balancer = this.CreateBalancer(folderCount);
            //
            // Fail folders by deleting them
            //
            for (int i = 0; i < failureCount; ++i)
            {
                Directory.Delete(balancer.Receivers[i]);
            }
            
            int fileNumber = -1;            
            string fileName = null;
            if (failureCount < folderCount)
            {
                //
                // These should all succeed
                //
                for (int i = 0; i < folderCount; ++i)
                {
                    ++fileNumber;
                    fileName = fileNumber.ToString();
                    Assert.DoesNotThrow(() => balancer.Process(new KeyValuePair<string, string>(fileName, "Hello")));
                    Assert.True(Locate(balancer, fileName) >= 0);
                }
            }
            else
            {
                //
                // These should all fail
                //
                for (int i = 0; i < folderCount; ++i)
                {
                    ++fileNumber;
                    fileName = fileNumber.ToString();
                    Assert.False(balancer.Process(new KeyValuePair<string, string>(fileName, "Hello")));
                    Assert.True(Locate(balancer, fileName) < 0);
                }
            }
        }
        
        FolderBalancer<KeyValuePair<string, string>> CreateBalancer(int folderCount)
        {
            return new FolderBalancer<KeyValuePair<string, string>>(m_root, folderCount, this.TextCopy);            
        }

        void Process(FolderBalancer<KeyValuePair<string, string>> balancer, string fileName, int expectedIndex)
        {
            Assert.DoesNotThrow(() => balancer.Process(new KeyValuePair<string, string>(fileName, "Hello")));
            Assert.True(File.Exists(Path.Combine(balancer.Receivers[expectedIndex], fileName)));
        }
        
        int Locate(FolderBalancer<KeyValuePair<string, string>> balancer, string fileName)
        {
            string[] folders = balancer.Receivers;
            for (int i = 0; i < folders.Length; ++i)
            {
                try
                {
                    if (File.Exists(Path.Combine(folders[i], fileName)))
                    {
                        return i;
                    }
                }
                catch
                {
                }
            }
            
            return -1;
        }
        
        string EnsureRootFolder(string name)
        {
            string path = Path.Combine(Path.GetTempPath(), name);
            return EnsureCleanFolder(path);
        }
        
        string EnsureCleanFolder(string path)
        {
            if (Directory.Exists(path))
            {
                Directory.Delete(path, true);
            }
            Directory.CreateDirectory(path);
            return path;
        }        
        
        bool TextCopy(KeyValuePair<string, string> file, string folderPath)
        {
            try
            {
                File.WriteAllText(Path.Combine(folderPath, file.Key), file.Value);
                return true;
            }
            catch
            {
            }
            
            return false;
        }
        
        
    }
}
