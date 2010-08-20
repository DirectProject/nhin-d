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
using System.IO;
using System.Security.Cryptography.X509Certificates;
using System.Net.Mail;
using NHINDirect.Tools.Command;
using NHINDirect.Config.Store;
using NHINDirect.Config.Client;
using NHINDirect.Config.Client.CertificateService;

namespace NHINDirect.Config.Command
{
    public class CertificateCommands
    {
        CertificateStoreClient m_client;
        
        public CertificateCommands()
        {
            m_client = new CertificateStoreClient();
        }
        
        /// <summary>
        /// Import a certificate file...
        /// </summary>
        /// <param name="args"></param>
        public void Command_CertificateAdd(string[] args)
        {
            string owner = args.GetRequiredValue(0);
            string path = args.GetRequiredValue(1);
            string password = args.GetOptionalValue(2, string.Empty);
            
            Certificate cert = new Certificate(owner, File.ReadAllBytes(path), password);
            m_client.AddCertificate(cert);
        }
        public void Usage_CertificateAdd()
        {
            Console.WriteLine("Import a certificate from a file and push it into the store.");
            Console.WriteLine("    certificateadd owner filepath [password]");
        }
        
        public void Command_CertificateGetByID(string[] args)
        {
            long certificateID = args.GetRequiredValue<int>(0);
            CertificateGetOptions options = GetOptions(args, 1);            
            
            this.Print(m_client.GetCertificate(certificateID, options));            
        }
        public void Usage_CertificateGetByID()
        {
            Console.WriteLine("Retrieve a certificate by its id.");
            Console.WriteLine("    certificateget certificateID [options]");
            PrintOptionsUsage();
        }
        
        public void Command_CertificateGet(string[] args)
        {
            string owner = args.GetRequiredValue(0);            
            CertificateGetOptions options = GetOptions(args, 1);
            
            Certificate[] certs = m_client.GetCertificatesForOwner(owner, options);            
            this.Print(certs);
        }
        public void Usage_CertificateGet()
        {
            Console.WriteLine("Retrieve all certificates for an owner.");
            Console.WriteLine("    certificateget owner [options]");
            PrintOptionsUsage();
        }
        
        public void Command_CertificateStatusSet(string[] args)
        {
            string owner = args.GetRequiredValue(0);
            EntityStatus status = args.GetRequiredEnum<EntityStatus>(1);
            
            m_client.SetCertificateStatusForOwner(owner, status);
        }
        public void Usage_CertificateStatusSet()
        {
            Console.WriteLine("Set the status for ALL certificates for an owner.");
            Console.WriteLine("    certificatestatusset owner");
        }
        
        public void Command_CertificateResolve(string[] args)
        {
            MailAddress address = new MailAddress(args.GetRequiredValue(0));            
            ConfigCertificateResolver resolver = new ConfigCertificateResolver(m_client);
            X509Certificate2Collection matches = resolver.GetCertificates(address);
            Print(matches);
        }       
        public void Usage_CertificateResolve()
        {
            Console.WriteLine("Resolve the certificate for an email address - duplicates what the agent would do.");
            Console.WriteLine("    certificateResolve emailAddress");
        }
        
        internal static CertificateGetOptions GetOptions(string[] args, int firstArg)
        {
            CertificateGetOptions options = new CertificateGetOptions();
            options.IncludeData = args.GetOptionalValue<bool>(firstArg, false);
            options.IncludePrivateKey = args.GetOptionalValue<bool>(firstArg + 1, false);
            return options;
        }
        internal static void PrintOptionsUsage()
        {
            Console.WriteLine("\tOptions:");
            Console.WriteLine("\t [certData] [privatekey]");
            Console.WriteLine("\t certData: (True/False) Fetch certificate data");
            Console.WriteLine("\t privateKey: (True/False) Include private key");
        }
                
        void Print(Certificate[] certs)
        {
            if (certs == null || certs.Length == 0)
            {
                Console.WriteLine("No certificates found");
                return;
            }

            foreach (Certificate cert in certs)
            {
                this.Print(cert);
                CommandUI.PrintSectionBreak();
            }
        }
        
        void Print(Certificate cert)
        {
            CommandUI.Print("Owner", cert.Owner);
            CommandUI.Print("Thumbprint", cert.Thumbprint); 
            CommandUI.Print("ID", cert.ID);
            CommandUI.Print("CreateDate", cert.CreateDate);
            CommandUI.Print("ValidStart", cert.ValidStartDate);
            CommandUI.Print("ValidEnd", cert.ValidEndDate);
            CommandUI.Print("Status", cert.Status);
            
            if (cert.HasData)
            {
                X509Certificate2 x509 = cert.ToX509Certificate();
                Print(x509);
            }
        }
        
        internal static void Print(X509Certificate2Collection certs)
        {
            if (certs.IsNullOrEmpty())
            {
                Console.WriteLine("No certificates found");
                return;
            }
            
            foreach(X509Certificate2 cert in certs)
            {
                Print(cert);
                CommandUI.PrintSectionBreak();
            }
        }        
        
        internal static void Print(X509Certificate2 x509)
        {
            CommandUI.Print("Subject", x509.Subject);
            CommandUI.Print("SerialNumber", x509.SerialNumber);
            CommandUI.Print("Issuer", x509.Issuer);
            CommandUI.Print("HasPrivateKey", x509.HasPrivateKey);
        }
    }
}
