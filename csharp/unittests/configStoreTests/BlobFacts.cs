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
using System.IO;
using System.Linq;
using System.Text;
using System.Data.Linq;
using System.Data.SqlClient;
using Health.Direct.Common.Collections;
using Health.Direct.Common.Extensions;
using Health.Direct.Config.Store;
using Xunit;
using Xunit.Extensions;

namespace Health.Direct.Config.Store.Tests
{
    public class BlobFacts : ConfigStoreTestBase
    {
        ConfigStore m_store;
        
        public BlobFacts()
        {
            m_store = CreateConfigStore();
        }

        [Fact, AutoRollback]
        public void TestAdd()
        {
            TestUserSettings settingsSrc = TestUserSettings.Create();
            string name = Guid.NewGuid().ToString("D");

            NamedBlob blobSrc = new NamedBlob(name, settingsSrc);
            
            Assert.DoesNotThrow(() => m_store.Blobs.Add(blobSrc));
            Assert.Throws<SqlException>(() => m_store.Blobs.Add(blobSrc));
        }

        [Fact, AutoRollback]
        public void TestUpdate()
        {
            TestUserSettings settingsSrc = TestUserSettings.Create();
            string name = Guid.NewGuid().ToString("D");

            NamedBlob blobSrc = new NamedBlob(name, settingsSrc);            
            Assert.DoesNotThrow(() => m_store.Blobs.Add(blobSrc));
            
            for (int i = 0; i < 10; ++i)
            {
                NamedBlob blobGet = null;
                Assert.DoesNotThrow(() => blobGet = m_store.Blobs.Get(name));
                
                TestUserSettings settingsDest = null;
                
                Assert.DoesNotThrow(() => settingsDest = blobGet.GetObject<TestUserSettings>());

                string newFirstName = settingsDest.FirstName + "," + i.ToString();
                settingsDest.FirstName = newFirstName;
                
                Assert.DoesNotThrow(() => m_store.Blobs.Update(new NamedBlob(name, settingsDest)));

                Assert.DoesNotThrow(() => blobGet = m_store.Blobs.Get(name));

                Assert.DoesNotThrow(() => settingsDest = blobGet.GetObject<TestUserSettings>());
                Assert.True(string.Equals(settingsDest.FirstName, newFirstName));
            }
        }
        
        [Fact, AutoRollback]        
        public void TestRoundtrip()
        {
            TestUserSettings settingsSrc = TestUserSettings.Create();
            string name = Guid.NewGuid().ToString("D");

            NamedBlob blobSrc = new NamedBlob(name, settingsSrc);
            Assert.DoesNotThrow(() => m_store.Blobs.Add(blobSrc));
            
            NamedBlob blobGet = null;
            Assert.DoesNotThrow(() => blobGet = m_store.Blobs.Get(name));
            
            TestUserSettings settingsDest = null;
            Assert.DoesNotThrow(() => settingsDest = blobGet.GetObject<TestUserSettings>());
            Assert.True(settingsSrc.Compare(settingsDest));
        }
        
        [Fact]
        public void TestSerialization()
        {
            TestUserSettings settingsSrc = TestUserSettings.Create();
            string name = Guid.NewGuid().ToString("D");
            
            NamedBlob blobSrc = null;
            NamedBlob blobDest = null;
            
            Assert.DoesNotThrow(() => blobSrc = new NamedBlob(name, settingsSrc));
            Assert.True(!blobSrc.Data.IsNullOrEmpty());

            Assert.DoesNotThrow(() => blobDest = new NamedBlob(name, blobSrc.Data));
            
            TestUserSettings settingsDest = null;
            Assert.DoesNotThrow(() => settingsDest = blobDest.GetObject<TestUserSettings>());
            
            Assert.True(settingsSrc.Compare(settingsDest));
        }
        
        [Fact, AutoRollback]
        public void TestContains()
        {
            TestUserSettings settingsSrc = TestUserSettings.Create();
            string name = Guid.NewGuid().ToString("D");

            NamedBlob blobSrc = new NamedBlob(name, settingsSrc);
            m_store.Blobs.Add(blobSrc);
            String tempPath = Path.GetTempPath();
            using(StreamWriter log = new System.IO.StreamWriter(Path.Combine(tempPath, "linq.log")))
            {
                log.AutoFlush = true;
                                
                using(ConfigDatabase db = m_store.CreateReadContext())
                {
                    db.Log = log;
                    Assert.True(m_store.Blobs.Contains(db, name));            
                }
                
                //
                // Add an additional 3 blobs. Then, when we list by prefix, we'll get 4
                //                
                for (int i = 0; i < 3; ++i)
                {
                    NamedBlob blob = new NamedBlob(name + "_" + i.ToString(), settingsSrc);
                    m_store.Blobs.Add(blob);
                }

                using (ConfigDatabase db = m_store.CreateReadContext())
                {
                    Assert.True(m_store.Blobs.ListNamesStartWith(db, name).Count() == 4);
                }
            }
        }
    }
    
    public class TestUserSettings
    {
        public TestUserSettings()
        {
        }
        
        public string FirstName;
        public string LastName;
        public string Address;
        
        public bool Compare(TestUserSettings other)
        {
            return (
                    string.Equals(this.FirstName, other.FirstName)
                &&  string.Equals(this.LastName, other.LastName)
                &&  string.Equals(this.Address, other.Address)
            );
        }
        
        public static TestUserSettings Create()
        {
            return new TestUserSettings()
            {
                FirstName = "Billy",
                LastName = "Bunter",
                Address = "The Remove, Grayfairs School"
            };
        }
    }
}
