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
using System.ServiceModel;
using DnsResolver;
using NHINDirect.Certificates;
using NHINDirect.Tools.Command;
using NHINDirect.Config.Store;
using NHINDirect.Config.Client;
using NHINDirect.Config.Client.CertificateService;

namespace NHINDirect.Config.Command
{
    /// <summary>
    /// Commands to manage certificates
    /// </summary>
    public class CertificateCommands
    {        
        public CertificateCommands()
        {
        }
        //---------------------------------------
        //
        // Commands
        //
        //---------------------------------------
      
        /// <summary>
        /// Import a certificate file and add it to the config service store
        /// </summary>
        public void Command_Certificate_Add(string[] args)
        {
            string filePath = args.GetRequiredValue(0);
            string password = args.GetOptionalValue(1, string.Empty);
            
            MemoryX509Store certStore = LoadCerts(filePath, password);            
            PushCerts(certStore, false);
        }        
        public void Usage_Certificate_Add()
        {
            Console.WriteLine("Import a certificate from a file and push it into the store.");
            Console.WriteLine("    filepath [password]");
            Console.WriteLine("\t filePath: path fo the certificate file. Can be .DER, .CER or .PFX");
            Console.WriteLine("\t password: (optional) file password");
        }

        /// <summary>
        /// Retrieve a certificate by its ID
        /// </summary>
        public void Command_Certificate_ByID_Get(string[] args)
        {
            long certificateID = args.GetRequiredValue<int>(0);
            CertificateGetOptions options = GetOptions(args, 1);

            this.Print(ConfigConsole.Current.CertificateClient.GetCertificate(certificateID, options));
        }
        public void Usage_Certificate_ByID_Get()
        {
            Console.WriteLine("Retrieve a certificate by its id.");
            Console.WriteLine("    certificateID [options]");
            Console.WriteLine("\t certificateID: ");
            PrintOptionsUsage();
        }
        
        /// <summary>
        /// Get all certificates for an owner
        /// </summary>
        public void Command_Certificate_Get(string[] args)
        {
            string owner = args.GetRequiredValue(0);            
            CertificateGetOptions options = GetOptions(args, 1);
            
            Certificate[] certs = ConfigConsole.Current.CertificateClient.GetCertificatesForOwner(owner, options); 
            this.Print(certs);
        }
        public void Usage_Certificate_Get()
        {
            Console.WriteLine("Retrieve all certificates for an owner.");
            Console.WriteLine("    owner [options]");
            Console.WriteLine("\t owner: Certificate owner");
            PrintOptionsUsage();
        }
        
        /// <summary>
        /// Set the status of a certificate
        /// </summary>
        public void Command_Certificate_Status_Set(string[] args)
        {
            string owner = args.GetRequiredValue(0);
            EntityStatus status = args.GetRequiredEnum<EntityStatus>(1);
            
            ConfigConsole.Current.CertificateClient.SetCertificateStatusForOwner(owner, status);
        }
        public void Usage_Certificate_Status_Set()
        {
            Console.WriteLine("Set the status for ALL certificates for an OWNER.");
            Console.WriteLine("     owner status");
            Console.WriteLine("\t owner: Certificate owner");
            Console.WriteLine("\t status: {0}", Extensions.EntityStatusString);
        }
        
        /// <summary>
        /// Remove certificate
        /// </summary>
        public void Command_Certificate_Remove(string[] args)
        {
            long certificateID = args.GetRequiredValue<long>(0);
            
            ConfigConsole.Current.CertificateClient.RemoveCertificate(certificateID);
        }
        public void Usage_Certificate_Remove()
        {
            Console.WriteLine("Remove certificate with given ID");
            Console.WriteLine("    certificateID");
        }
        
        /// <summary>
        /// Mirrors what the production gateway does
        /// </summary>
        public void Command_Certificate_Resolve(string[] args)
        {
            MailAddress owner = new MailAddress(args.GetRequiredValue(0));
            CertificateGetOptions options = GetOptions(args, 1);

            Certificate[] certs = ConfigConsole.Current.CertificateClient.GetCertificatesForOwner(owner.Address, options);
            if (certs.IsNullOrEmpty())
            {
                certs = ConfigConsole.Current.CertificateClient.GetCertificatesForOwner(owner.Host, options);
            }
            this.Print(certs);
        }
        public void Usage_Certificate_Resolve()
        {
            Console.WriteLine("Resolves certificates for an owner - like the Smtp Gateway would.");
            Console.WriteLine("    owner [options]");
            Console.WriteLine("\t owner: Certificate owner");
            PrintOptionsUsage();
        }
        
        /// <summary>
        /// Export certs in zone file format
        /// </summary>
        public void Command_Certificate_Export(string[] args)
        {
            string owner = args.GetRequiredValue(0);
            string outputFile = args.GetOptionalValue(1, null);
            
            CertificateGetOptions options = new CertificateGetOptions() { IncludeData = true, IncludePrivateKey = false};
            Certificate[] certs = ConfigConsole.Current.CertificateClient.GetCertificatesForOwner(owner, options);
            if (certs.IsNullOrEmpty())
            {
                Console.WriteLine("No certificates found");
                return;
            }            
            ExportCerts(certs, outputFile);
        }        
        public void Usage_Certificate_Export()
        {
            Console.WriteLine("Export certificates for an owner in zone file format");
            Console.WriteLine("    owner [outputFile]");
            Console.WriteLine("\t owner: certificate owner");
            Console.WriteLine("\t outputFile: (Optional) Export to file. Else write to Console");
        }

        /// <summary>
        /// Export all Enabled public certificates in zone file format
        /// </summary>
        public void Command_Certificate_Export_All(string[] args)
        {
            string outputFile = args.GetOptionalValue(0, null);
            int chunkSize = args.GetOptionalValue<int>(1, 25);
            
            CertificateGetOptions options = new CertificateGetOptions() { IncludeData = true, IncludePrivateKey = false };
            IEnumerable<Certificate> certs = ConfigConsole.Current.CertificateClient.EnumerateCertificates(chunkSize, options);
            
            ExportCerts(certs, outputFile);
        }
        public void Usage_Certificate_Export_All()
        {
            Console.WriteLine("Export all enabled public certificates in zone file FORMAT");
            Console.WriteLine("You can place this output directly into your zone file");
            Console.WriteLine("     [outputFile] [chunkSize]");
            Console.WriteLine("\t outputFile: (Optional) Export to file. Else write to Console");
            Console.WriteLine("\t chunkSize: (Optional) Enumeration size. Default is 25");
        }
        
        /// <summary>
        /// Export public certs for private keys in the machine store
        /// </summary>
        /// <param name="args"></param>
        public void Command_Certificate_Export_Machine(string[] args)
        {
            string storeName = args.GetOptionalValue(0, "NHINDPrivate");
            string outputFile = args.GetOptionalValue(1, null);
            using (SystemX509Store store = new SystemX509Store(Certificates.Extensions.OpenStoreRead(storeName, StoreLocation.LocalMachine), null))
            {
                ExportCerts(store, outputFile);
            }
        }
        public void Usage_Certificate_Export_Machine()
        {
            Console.WriteLine("Exports public certificates for all certs in the given store");
            Console.WriteLine("    [storeName] [outputFile]");
            Console.WriteLine("\t storeName: (optional) Default is NHINDPrivate.");
            Console.WriteLine("\t outputFile: (optional) Export to file. Else write to Console");
        }
        
        //---------------------------------------
        //
        // Implementation...
        //
        //---------------------------------------
        
        internal static void ExportCerts(IEnumerable<Certificate> certs, string filePath)
        {
            if (string.IsNullOrEmpty(filePath))
            {
                ExportCerts(certs, Console.Out, false);
                return;
            }
                        
            using(StreamWriter writer = new StreamWriter(filePath))
            {
                ExportCerts(certs, writer, true);
            }
        }

        internal static void ExportCerts(IEnumerable<Certificate> certs, TextWriter writer, bool isOutputFile)
        {
            foreach (Certificate cert in certs)
            {
                DnsX509Cert dnsCert = new DnsX509Cert(cert.Data);
                dnsCert.Export(writer, cert.Owner);
                writer.WriteLine();
                
                if (isOutputFile)
                {
                    Console.WriteLine("{0}, {1}, {2}, {3}", cert.Owner, dnsCert.Name, cert.ValidStartDate, cert.ValidEndDate);
                }
            }
        }

        internal static void ExportCerts(IEnumerable<X509Certificate2> certs, string filePath)
        {
            if (string.IsNullOrEmpty(filePath))
            {
                ExportCerts(certs, Console.Out, false);
                return;
            }

            using (StreamWriter writer = new StreamWriter(filePath))
            {
                ExportCerts(certs, writer, true);
            }
        }

        internal static void ExportCerts(IEnumerable<X509Certificate2> certs, TextWriter writer, bool isOutputFile)
        {
            foreach (X509Certificate2 cert in certs)
            {
                DnsX509Cert dnsCert = new DnsX509Cert(cert);
                dnsCert.Export(writer, dnsCert.Name);
                writer.WriteLine();

                if (isOutputFile)
                {
                    Console.WriteLine(dnsCert.Name);
                }
            }
        }
        
        internal static void PushCerts(IEnumerable<X509Certificate2> certs, bool checkForDupes)
        {
            CertificateStoreClient client = ConfigConsole.Current.CertificateClient;
            foreach (X509Certificate2 cert in certs)
            {
                string owner = cert.ExtractEmailNameOrName();
                try
                {
                    if (!checkForDupes || !client.Contains(cert))
                    {
                        client.AddCertificate(new Certificate(owner, cert));                    
                        Console.WriteLine("Added {0}", cert.Subject);
                    }
                    else
                    {
                        Console.WriteLine("Exists {0}", cert.Subject);
                    }
                }
                catch (FaultException<ConfigStoreFault> ex)
                {
                    if (ex.Detail.Error == ConfigStoreError.UniqueConstraint)
                    {
                        Console.WriteLine("Exists {0}", cert.Subject);
                    }
                }
            }
        }

        internal static void PushCerts(IEnumerable<X509Certificate2> certs, bool checkForDupes, EntityStatus status)
        {
            PushCerts(certs, checkForDupes);
            var owners = (from cert in certs
                          select cert.ExtractEmailNameOrName()).Distinct();
            foreach (string owner in owners)
            {
                ConfigConsole.Current.CertificateClient.SetCertificateStatusForOwner(owner, EntityStatus.Enabled);
            }
        }

        internal static MemoryX509Store LoadCerts(string filePath, string password)
        {
            MemoryX509Store certStore = new MemoryX509Store();
            LoadCerts(certStore, filePath, password);
            return certStore;
        }

        internal static void LoadCerts(MemoryX509Store certStore, string filePath, string password)
        {
            string ext = Path.GetExtension(filePath) ?? string.Empty;
            switch (ext.ToLower())
            {
                default:
                    certStore.ImportKeyFile(filePath, X509KeyStorageFlags.Exportable);
                    break;

                case ".pfx":
                    certStore.ImportKeyFile(filePath, password, X509KeyStorageFlags.Exportable);
                    break;
            }
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
            Console.WriteLine("\t options:");
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
