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
using System;
using System.Collections.Generic;
using System.IO;
using System.Net.Mail;
using System.Security.Cryptography.X509Certificates;
using Health.Direct.Common;
using Health.Direct.Common.Certificates;
using Xunit;
using Xunit.Extensions;

namespace Health.Direct.Agent.Tests
{
    /// <summary>
    /// Helper Class that does Chores needed by actual tests...
    /// </summary>
    public class AgentTester
    {
        public const string DefaultDomainA = "redmond.hsgincubator.com";
        public const string DefaultDomainB = "nhind.hsgincubator.com";
        
        DirectAgent m_agentA;
        DirectAgent m_agentB;
        string m_messageFolder;
        
        public AgentTester(DirectAgent agentA, DirectAgent agentB)
        {
            if (agentA == null)
            {
                throw new ArgumentNullException("agentA");
            }

            if (agentB == null)
            {
                throw new ArgumentNullException("agentB");
            }
            
            m_agentA = agentA;
            m_agentB = agentB;
            m_messageFolder = Path.Combine(Directory.GetCurrentDirectory(), "TestMessages");
        }
        
        public DirectAgent AgentA
        {
            get
            {
                return m_agentA;
            }
            set
            {
                m_agentA = value;
            }
        }
        
        public DirectAgent AgentB
        {
            get
            {
                return m_agentB;
            }
            set
            {
                m_agentB = value;
            }
        }
        
        public string MessageFolder
        {
            get
            {
                return m_messageFolder;
            }
            set
            {
                if (string.IsNullOrEmpty(value))
                {
                    throw new ArgumentException("value null or empty", "value");
                }
                
                if (!Directory.Exists(value))
                {
                    throw new DirectoryNotFoundException(value);
                }
                
                m_messageFolder = value;
            }
        }
        
        public void TestEndToEndFile(string messageFilePath)
        {
            this.ProcessEndToEnd(this.ReadMessageText(messageFilePath));
        }
        
        public string ProcessEndToEnd(string messageText)
        {
            string outgoingText = this.ProcessOutgoingToString(messageText);
            string incomingText = this.ProcessIncomingToString(outgoingText);            
            return incomingText;
        }

        public string ProcessOutgoingFileToString(string messageFilePath)
        {
            return this.ProcessOutgoingToString(this.ReadMessageText(messageFilePath));
        }

        public string ProcessOutgoingToString(string messageText)
        {
            return this.ProcessOutgoing(messageText).SerializeMessage();
        }

        public OutgoingMessage ProcessOutgoingFile(string messageFilePath)
        {
            return this.ProcessOutgoing(this.ReadMessageText(messageFilePath));
        }

        public OutgoingMessage ProcessOutgoing(string messageText)
        {
            return m_agentA.ProcessOutgoing(messageText);
        }

        public void ProcessIncomingFile(string messageFilePath)
        {
            this.ProcessIncomingToString(this.ReadMessageText(messageFilePath));
        }

        public string ProcessIncomingToString(string messageText)
        {
            return m_agentB.ProcessIncoming(messageText).SerializeMessage();
        }
        
        public string ReadMessageText(string messageFilePath)
        {
            if (!Path.IsPathRooted(messageFilePath))
            {
                messageFilePath = Path.Combine(m_messageFolder, messageFilePath);
            }
            
            return File.ReadAllText(messageFilePath);
        }
        
        public static void CheckErrorCode<TException, TError>(Action operation, TError expected)
            where TException : DirectException<TError>
        {
            TError error = default(TError);
            try
            {
                operation();
            }
            catch (Exception ex)
            {
                Assert.True(ex is TException);
                error = ((TException)ex).Error;
            }
            Assert.True(error.Equals(expected));
        }
        
        public static AgentTester CreateDefault()
        {
            DirectAgent agentA = new DirectAgent(AgentTester.DefaultDomainA);
            DirectAgent agentB = new DirectAgent(AgentTester.DefaultDomainB);
            
            return new AgentTester(agentA, agentB);
        }
        
        public static AgentTester CreateTest()
        {
            return CreateTest(Directory.GetCurrentDirectory());
        }
        
        public static AgentTester CreateTest(string basePath)
        {
            DirectAgent agentA = CreateAgent(AgentTester.DefaultDomainA, MakeCertificatesPath(basePath, "redmond"));
            DirectAgent agentB = CreateAgent(AgentTester.DefaultDomainB, MakeCertificatesPath(basePath, "nhind"));
            return new AgentTester(agentA, agentB);
        }
        
        public static DirectAgent CreateAgent(string domain, string certsBasePath)
        {
            MemoryX509Store privateCerts = LoadPrivateCerts(certsBasePath);
            MemoryX509Store publicCerts = LoadPublicCerts(certsBasePath);
            TrustAnchorResolver anchors = new TrustAnchorResolver(
                (IX509CertificateStore) LoadIncomingAnchors(certsBasePath),
                (IX509CertificateStore) LoadOutgoingAnchors(certsBasePath));

            return new DirectAgent(domain, privateCerts.CreateResolver(), publicCerts.CreateResolver(), anchors);
        }
        
        static string MakeCertificatesPath(string basePath, string agentFolder)
        {
            return Path.Combine(basePath, Path.Combine("Certificates", agentFolder));
        }
        
        static MemoryX509Store LoadPrivateCerts(string certsBasePath)
        {
            return LoadCertificates(Path.Combine(certsBasePath, "Private"));
        }

        static MemoryX509Store LoadPublicCerts(string certsBasePath)
        {
            return LoadCertificates(Path.Combine(certsBasePath, "Public"));
        }
        
        static MemoryX509Store LoadIncomingAnchors(string certsBasePath)
        {
            return LoadCertificates(Path.Combine(certsBasePath, "IncomingAnchors"));
        }

        static MemoryX509Store LoadOutgoingAnchors(string certsBasePath)
        {
            return LoadCertificates(Path.Combine(certsBasePath, "OutgoingAnchors"));
        }
        
        public static MemoryX509Store LoadCertificates(string folderPath)
        {
            if (string.IsNullOrEmpty(folderPath))
            {
                throw new ArgumentException("value was null or empty", "folderPath");
            }
            
            if (!Directory.Exists(folderPath))
            {
                throw new DirectoryNotFoundException("Directory not found: " + folderPath);
            }
            
            MemoryX509Store certStore = new MemoryX509Store();
            
            string[] files = Directory.GetFiles(folderPath);
            for (int i = 0; i < files.Length; ++i)
            {
                string file = files[i];
                string ext = Path.GetExtension(file) ?? string.Empty;
                ext = ext.ToLower();
                
                switch(ext)
                {
                    default:
                        certStore.ImportKeyFile(file, X509KeyStorageFlags.MachineKeySet | X509KeyStorageFlags.Exportable | X509KeyStorageFlags.PersistKeySet);
                        break;
                    
                    case ".pfx":
                        certStore.ImportKeyFile(file, "passw0rd!", X509KeyStorageFlags.MachineKeySet | X509KeyStorageFlags.Exportable | X509KeyStorageFlags.PersistKeySet);
                        break;
                }
            } 
            
            return certStore;           
        }
        
        /// <summary>
        /// Sets up standard stores for Testing
        /// WARNING: This may require elevated permissions
        /// </summary>
        public static void EnsureStandardMachineStores()
        {
            SystemX509Store.CreateAll();
            
            string basePath = Directory.GetCurrentDirectory();
            string redmondCertsPath = MakeCertificatesPath(basePath, "redmond");
            string nhindCertsPath = MakeCertificatesPath(basePath, "nhind");
            
            SystemX509Store store;
            using(store = SystemX509Store.OpenPrivateEdit())
            {
                InstallCerts(store, LoadPrivateCerts(redmondCertsPath));
                InstallCerts(store, LoadPrivateCerts(nhindCertsPath));
            }

            using (store = SystemX509Store.OpenExternalEdit())
            {
                InstallCerts(store, LoadPublicCerts(redmondCertsPath));
                InstallCerts(store, LoadPublicCerts(nhindCertsPath));
            }

            using (store = SystemX509Store.OpenAnchorEdit())
            {
                InstallCerts(store, LoadIncomingAnchors(redmondCertsPath));
                InstallCerts(store, LoadOutgoingAnchors(redmondCertsPath));

                InstallCerts(store, LoadIncomingAnchors(nhindCertsPath));
                InstallCerts(store, LoadOutgoingAnchors(nhindCertsPath));
            }
        }        
                
        static void InstallCerts(IX509CertificateStore store, IEnumerable<X509Certificate2> certs)
        {
            foreach(X509Certificate2 cert in certs)
            {
                if (!store.Contains(cert))
                {
                    store.Add(cert);
                }
            }
        }
    }

    /// <summary>
    /// Introduces some simple confusion into cert resolution, to force decryption failures
    /// </summary>
    public class BadCertResolver : ICertificateResolver
    {
        ICertificateResolver m_a;
        ICertificateResolver m_b;
        bool m_includeGood;
                
        public BadCertResolver(ICertificateResolver a, ICertificateResolver b, bool includeGood)
        {
            m_a = a;
            m_b = b;
            m_includeGood = includeGood;
        }
        
        public X509Certificate2Collection GetCertificates(MailAddress address)
        {
            X509Certificate2Collection certs;
            
            //
            // Very trivial - deliberately 'confuses' certs - returns certs meant for redmond when the caller
            // asked for nhind and vice-versa. 
            //
            if (address.Host.Equals("redmond.hsgincubator.com", StringComparison.OrdinalIgnoreCase))
            {
                certs = m_b.GetCertificates(new MailAddress(address.User + '@' + "nhind.hsgincubator.com"));
                if (m_includeGood)
                {
                    certs.Add(m_a.GetCertificates(address));
                }
            }
            else
            {
                certs = m_a.GetCertificates(new MailAddress(address.User + '@' + "redmond.hsgincubator.com"));
                if (m_includeGood)
                {
                    certs.Add(m_b.GetCertificates(address));
                }
            }

            return certs;
        }
        
        public X509Certificate2Collection GetCertificatesForDomain(string domain)
        {
            // Not supported
            return null;
        }
    }


    public class NullResolver : ICertificateResolver
    {
        bool m_returnEmpty = false;

        public NullResolver()
            : this(false)
        {
        }

        public NullResolver(bool returnEmpty)
        {
            m_returnEmpty = returnEmpty;
        }

        public X509Certificate2Collection GetCertificates(MailAddress address)
        {
            return (m_returnEmpty) ? new X509Certificate2Collection() : null;
        }

        public X509Certificate2Collection GetCertificatesForDomain(string domain)
        {
            return (m_returnEmpty) ? new X509Certificate2Collection() : null;
        }
    }

    public class ThrowingCertResolver : ICertificateResolver
    {
        public ThrowingCertResolver()
        {
        }

        public X509Certificate2Collection GetCertificates(MailAddress address)
        {
            throw new InvalidOperationException();
        }

        public X509Certificate2Collection GetCertificatesForDomain(string domain)
        {
            throw new InvalidOperationException();
        }
    }
}