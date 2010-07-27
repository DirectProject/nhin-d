using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.IO;
using NHINDirect.Agent;
using NHINDirect.Certificates;
using System.Security.Cryptography.X509Certificates;

namespace AgentTests
{
    /// <summary>
    /// Helper Class that does Chores needed by actual tests...
    /// </summary>
    public class AgentTester
    {
        public const string DefaultDomainA = "redmond.hsgincubator.com";
        public const string DefaultDomainB = "nhind.hsgincubator.com";
        
        NHINDAgent m_agentA;
        NHINDAgent m_agentB;
        string m_messageFolder;
        
        public AgentTester(NHINDAgent agentA, NHINDAgent agentB)
        {
            if (agentA == null || agentB == null)
            {
                throw new ArgumentNullException();
            }
            
            m_agentA = agentA;
            m_agentB = agentB;
            m_messageFolder = Path.Combine(Directory.GetCurrentDirectory(), "TestMessages");
        }
        
        public NHINDAgent AgentA
        {
            get
            {
                return m_agentA;
            }
        }
        
        public NHINDAgent AgentB
        {
            get
            {
                return m_agentB;
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
                    throw new ArgumentException();
                }
                
                if (!Directory.Exists(value))
                {
                    throw new DirectoryNotFoundException();
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
        
        public static AgentTester CreateDefault()
        {
            NHINDAgent agentA = new NHINDAgent(AgentTester.DefaultDomainA);
            NHINDAgent agentB = new NHINDAgent(AgentTester.DefaultDomainB);
            
            return new AgentTester(agentA, agentB);
        }
        
        public static AgentTester CreateTest()
        {
            return CreateTest(Directory.GetCurrentDirectory());
        }
        
        public static AgentTester CreateTest(string basePath)
        {
            NHINDAgent agentA = CreateAgent(AgentTester.DefaultDomainA, Path.Combine(basePath, @"Certificates\redmond"));
            NHINDAgent agentB = CreateAgent(AgentTester.DefaultDomainB, Path.Combine(basePath, @"Certificates\nhind"));
            return new AgentTester(agentA, agentB);
        }
        
        public static NHINDAgent CreateAgent(string domain, string agentBasePath)
        {
            MemoryX509Store privateCerts = LoadCertificates(Path.Combine(agentBasePath, "Private"));
            MemoryX509Store publicCerts = LoadCertificates(Path.Combine(agentBasePath, @"Public"));
            TrustAnchorResolver anchors = new TrustAnchorResolver(
                                                (IX509CertificateStore) LoadCertificates(Path.Combine(agentBasePath, @"IncomingAnchors")),
                                                (IX509CertificateStore) LoadCertificates(Path.Combine(agentBasePath, @"OutgoingAnchors")));

            return new NHINDAgent(domain, privateCerts.Index(), publicCerts.Index(), anchors);
        }
                
        public static MemoryX509Store LoadCertificates(string folderPath)
        {
            if (string.IsNullOrEmpty(folderPath))
            {
                throw new ArgumentException();
            }
            
            if (!Directory.Exists(folderPath))
            {
                throw new DirectoryNotFoundException();
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
                        certStore.ImportKeyFile(file, X509KeyStorageFlags.DefaultKeySet);
                        break;
                    
                    case ".pfx":
                        certStore.ImportKeyFile(file, "passw0rd!", X509KeyStorageFlags.DefaultKeySet);
                        break;
                }
            } 
            
            return certStore;           
        }
    }
}
