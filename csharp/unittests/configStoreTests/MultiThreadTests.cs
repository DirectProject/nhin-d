/* 
 Copyright (c) 2010, Direct Project
 All rights reserved.

 Authors:
    Umesh Madan umeshma@microsoft.com
  
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
using System.Threading;
using System.Net.Mail;
using System.Data.SqlClient;
using Xunit;
using Xunit.Extensions;

namespace Health.Direct.Config.Store.Tests
{
    public class MultiThreadTests : ConfigStoreTestBase
    {
        public const string ConnectionStringTimeout = @"Data Source=.\SQLEXPRESS;Initial Catalog=DirectConfig;Integrated Security=SSPI;Connection Timeout=120";
        static ConfigStore s_store;
        const string TestDomain = "unittests.com";
        
        Domain m_domain;
        
        static MultiThreadTests()
        {
            s_store = new ConfigStore(ConnectionStringTimeout, TimeSpan.FromMinutes(2));
        }
        
        [Fact]
        public void TestAddress()
        {
            m_domain = EnsureDomain();
            //
            // Create test addresses
            //
            TestThread<string>[] threads = new TestThread<string>[2];
            for (int i = 0; i < threads.Length; ++i)
            {
                threads[i] = new TestThread<string>(this.EnsureAddress, CreateAddressStrings(i, 50));
            }
            
            TestThread<string>.Start(threads);
            TestThread<string>.Wait(threads);
        }
        
        Domain EnsureDomain()
        {
            Domain domain;
            if ((domain = s_store.Domains.Get(TestDomain)) == null)
            {
                domain = new Domain(TestDomain);
                s_store.Domains.Add(domain);
            }
            
            return domain;
        }
        
        void EnsureAddress(string address)
        {
            Address addressObj;
            if ((addressObj = s_store.Addresses.Get(address)) != null)
            {
                s_store.Addresses.Remove(addressObj.EmailAddress);
            }
            addressObj = new Address(m_domain.ID, address);            
            s_store.Addresses.Add(addressObj);
            //this.UpdateAddress(address);
        }
        
        void UpdateAddress(string address)
        {
            Address addressObj = s_store.Addresses.Get(address);
            addressObj.Status = (addressObj.Status == EntityStatus.Enabled) ? EntityStatus.Disabled : EntityStatus.Enabled;
            s_store.Addresses.Update(addressObj);
        }
                
        static string[] CreateAddressStrings(int prefix, int count)
        {
            string[] addresses = new string[count];
            for (int i = 0; i < count; ++i)
            {
                addresses[i] = string.Format("a{0}.{1}@{2}", prefix, i, TestDomain);
            }
            return addresses;
        }     
    }
    
    public class TestThread<T>
    {
        public const int MaxAttempts = 3;

        Thread m_thread;
        int m_success;
        int m_failure;
        int m_zombie;
        int m_deadlock;
        Action<T> m_action;
        T[] m_inputs;

        public TestThread(Action<T> action, T[] inputs)
        {
            m_action = action;
            m_inputs = inputs;
        }

        public void Start()
        {            
            m_thread = new Thread(this.Run);
            m_thread.Start();
        }
        
        public static void Start(TestThread<T>[] threads)
        {        
            for (int i = 0; i < threads.Length; ++i)
            {
                threads[i].Start();
            }
        }
        
        public static void Wait(TestThread<T>[] threads)
        {
            for (int i = 0; i < threads.Length; ++i)
            {
                threads[i].Wait();
                Assert.True(threads[i].Worked, threads[i].Status());
            }            
        }
                
        public void Wait()
        {
            m_thread.Join();
        }
                
        void Run()
        {
            for(int i = 0; i < m_inputs.Length; ++i)
            {
                try
                {
                    int attempts = 0;
                    while (attempts < MaxAttempts)
                    {
                        try
                        {
                            ++attempts;
                            m_action(m_inputs[i]);
                            break;
                        }
                        catch
                        {
                            if (attempts == MaxAttempts)
                            {
                                throw;
                            }
                        }
                    }
                    ++m_success;
                }
                catch(Exception ex)
                {
                    SqlException se = ex as SqlException;
                    if (se != null && (se.Number == (int)SqlErrorCodes.Deadlock))
                    {
                        ++m_deadlock;
                    }
                    else if (ex is InvalidOperationException)
                    {
                        ++m_zombie;
                    }
                    else
                    {
                        ++m_failure;
                    }
                }
            }
        }       
        
        public bool Worked
        {
            get {return (m_failure == 0);}
        }     
        
        public string Status()
        {
            return string.Format("Success {0}, Deadlock {1}, Zombie {2}, Failure {3}", m_success, m_deadlock, m_zombie, m_failure);
        }        
    }
}
