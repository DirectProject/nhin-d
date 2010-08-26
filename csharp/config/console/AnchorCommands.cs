/* 
 Copyright (c) 2010, NHIN Direct Project
 All rights reserved.

 Authors:
    Umesh Madan     umeshma@microsoft.com
  
Redistribution and use in source and binary forms, with or without modification, are permitted provided that the following conditions are met:

Redistributions of source code must retain the above copyright notice, this list of conditions and the following disclaimer.
Redistributions in binary form must reproduce the above copyright notice, this list of conditions and the following disclaimer in the documentation and/or other materials provided with the distribution.
Neither the name of the The NHIN Direct Project (nhindirect.org). nor the names of its contributors may be used to endorse or promote products derived from this software without specific prior written permission.
THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 
*/
using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Security.Cryptography.X509Certificates;
using System.IO;
using System.Net.Mail;
using System.ServiceModel;
using NHINDirect.Tools.Command;
using NHINDirect.Config.Store;
using NHINDirect.Config.Client;
using NHINDirect.Config.Client.CertificateService;

namespace NHINDirect.Config.Command
{
    public class AnchorCommands
    {
        AnchorStoreClient m_client;
        
        public AnchorCommands()
        {
            m_client = ConfigConsole.Settings.AnchorManager.CreateAnchorStoreClient();
        }
        
        public void Command_AnchorAdd(string[] args)
        {
            string owner = args.GetRequiredValue(0);
            string filePath = args.GetRequiredValue(1);
            string password = args.GetOptionalValue(2, string.Empty);
            
            this.PushCerts(owner, CertificateCommands.LoadCerts(filePath, password));
        }
        public void Usage_AnchorAdd()
        {
            Console.WriteLine("Import an anchor certificate from a file and push it into the store.");
            Console.WriteLine("    anchoradd owner filepath [password]");
        }
        
        public void Command_AnchorGetByID(string[] args)
        {
            long anchorID = args.GetRequiredValue<int>(0);
            CertificateGetOptions options = CertificateCommands.GetOptions(args, 1);

            Anchor[] anchors = m_client.GetAnchors(new long[] { anchorID }, options);
            this.Print(anchors);
        }
        public void Usage_AnchorGetByID()
        {
            Console.WriteLine("Get an anchor by its id.");
            Console.WriteLine("    anchorgetbyid anchorID [options]");
            CertificateCommands.PrintOptionsUsage();
        }
                
        public void Command_AnchorsGet(string[] args)
        {
            string owner = args.GetRequiredValue(0);
            CertificateGetOptions options = CertificateCommands.GetOptions(args,1);
     
            Anchor[] anchors = m_client.GetAnchorsForOwner(owner, options);
            this.Print(anchors);
        }
        public void Usage_AnchorsGet()
        {
            Console.WriteLine("Get all anchors for an owner.");
            Console.WriteLine("  anchorsget owner [options]");
            CertificateCommands.PrintOptionsUsage();
        }
           
        public void Command_AnchorsResolve(string[] args)
        {
            MailAddress mail = new MailAddress(args.GetRequiredValue(0));
            ConfigAnchorResolver resolver = new ConfigAnchorResolver(ConfigConsole.Settings.AnchorManager);
            
            X509Certificate2Collection matches = resolver.IncomingAnchors.GetCertificates(mail);
            CertificateCommands.Print(matches);
            
            matches = resolver.OutgoingAnchors.GetCertificates(mail);
            CertificateCommands.Print(matches);
        }
        public void Usage_AnchorsResolve()
        {
            Console.WriteLine("Resolve anchors like the agent would.");
            Console.WriteLine("    anchorsresolve emailaddress");
        }
        
        public void Command_AnchorsList(string[] args)
        {
            CertificateGetOptions options = CertificateCommands.GetOptions(args, 0);
            foreach(Anchor anchor in m_client.EnumerateAnchors(10, options))
            {
                this.Print(anchor);
                CommandUI.PrintSectionBreak();
            }
        }
        public void Usage_AnchorsList()
        {
            Console.WriteLine("List all anchors");
            CertificateCommands.PrintOptionsUsage();
        }

        void PushCerts(string owner, IEnumerable<X509Certificate2> certs)
        {
            foreach (X509Certificate2 cert in certs)
            {
                try
                {
                    m_client.AddAnchor(new Anchor(owner, cert, true, true));
                    Console.WriteLine("Added {0}", cert.Subject);
                }
                catch(FaultException<ConfigStoreFault> ex)
                {
                    if (ex.Detail.Error == ConfigStoreError.UniqueConstraint)
                    {
                        Console.WriteLine("Exists {0}", cert.Subject);
                    }
                }
            }
        }
        
        void Print(Anchor[] anchors)
        {
            if (anchors == null || anchors.Length == 0)
            {
                Console.WriteLine("No certificates found");
                return;
            }
            foreach (Anchor cert in anchors)
            {
                this.Print(cert);
                CommandUI.PrintSectionBreak();
            }
        }

        void Print(Anchor cert)
        {
            CommandUI.Print("Owner", cert.Owner);
            CommandUI.Print("Thumbprint", cert.Thumbprint);
            CommandUI.Print("ID", cert.ID);
            CommandUI.Print("CreateDate", cert.CreateDate);
            CommandUI.Print("ValidStart", cert.ValidStartDate);
            CommandUI.Print("ValidEnd", cert.ValidEndDate);
            CommandUI.Print("ForIncoming", cert.ForIncoming);
            CommandUI.Print("ForOutgoing", cert.ForOutgoing);
            
            if (cert.HasData)
            {
                X509Certificate2 x509 = cert.ToX509Certificate();
                CertificateCommands.Print(x509);
            }
        }
    }
}
