/* 
 Copyright (c) 2010, Direct Project
 All rights reserved.

 Authors:
    Umesh Madan     umeshma@microsoft.com
    Ali Emami       aliemami@microsoft.com
  
Redistribution and use in source and binary forms, with or without modification, are permitted provided that the following conditions are met:

Redistributions of source code must retain the above copyright notice, this list of conditions and the following disclaimer.
Redistributions in binary form must reproduce the above copyright notice, this list of conditions and the following disclaimer in the documentation and/or other materials provided with the distribution.
Neither the name of The Direct Project (directproject.org) nor the names of its contributors may be used to endorse or promote products derived from this software without specific prior written permission.
THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 
*/
using System;
using System.Collections.Generic;
using System.Linq;
using System.IO;
using System.Security.Cryptography.X509Certificates;
using System.Net.Mail;
using System.ServiceModel;
using System.Net;
using Health.Direct.Common.Certificates;
using Health.Direct.Common.DnsResolver;
using Health.Direct.Common.Extensions;
using Health.Direct.Config.Client;
using Health.Direct.Config.Client.CertificateService;
using Health.Direct.Config.Store;
using Health.Direct.Config.Tools;
using Health.Direct.Config.Tools.Command;

namespace Health.Direct.Config.Console.Command
{
    /// <summary>
    /// Commands to manage certificates
    /// </summary>
    public class CertificateCommands : CommandsBase<CertificateStoreClient>
    {        
        //---------------------------------------
        //
        // Commands
        //
        //---------------------------------------

        internal CertificateCommands(ConfigConsole console, Func<CertificateStoreClient> client) 
            : base(console, client)
        {
        }

        /// <summary>
        /// Import a certificate file and add it to the config service store
        /// </summary>
        [Command(Name = "Certificate_Add", Usage = CertificateAddUsage)]
        public void CertificateAdd(string[] args)
        {
            CertificateFileInfo certFileInfo = CertificateFileInfo.Create(0, args);            
            MemoryX509Store certStore = certFileInfo.LoadCerts();
            PushCerts(certStore, false, certFileInfo.Status);
        }
        private const string CertificateAddUsage
            = "Import a certificate from a file and push it into the store."
              + Constants.CRLF + CertificateFileInfo.Usage;

        /// <summary>
        /// Import and add an anchor
        /// </summary>
        [Command(Name = "Certificate_Ensure", Usage = CertificateAddUsage)]
        public void CertificateEnsure(string[] args)
        {
            CertificateFileInfo certFileInfo = CertificateFileInfo.Create(0, args);
            MemoryX509Store certStore = certFileInfo.LoadCerts();
            //
            // This checks for duplicates...(using thumbprints)
            //
            PushCerts(certStore, true, certFileInfo.Status);
        }
    
        
        const X509KeyStorageFlags MachineKeyFlags = X509KeyStorageFlags.MachineKeySet
                                    | X509KeyStorageFlags.Exportable
                                    | X509KeyStorageFlags.PersistKeySet;
        /// <summary>
        /// Import a certificate file and add it to the machine store
        /// </summary>
        [Command(Name = "Certificate_Add_Machine", Usage = CertificateAddMachineUsage)]
        public void CertificateAddMachine(string[] args)
        {
            using (SystemX509Store store = OpenStore(args.GetRequiredValue(0)))
            {
                CertificateFileInfo certFileInfo = CertificateFileInfo.Create(1, args);
                store.ImportKeyFile(certFileInfo.FilePath, certFileInfo.Password, MachineKeyFlags);
            }
        }

        /// <summary>
        /// Import a certificate file and add it to the machine store
        /// </summary>
        [Command(Name = "Certificate_Ensure_Machine", Usage = CertificateAddMachineUsage)]
        public void CertificateEnsureMachine(string[] args)
        {
            using (SystemX509Store store = OpenStore(args.GetRequiredValue(0)))
            {
                CertificateFileInfo certFileInfo = CertificateFileInfo.Create(1, args);
                MemoryX509Store certs = certFileInfo.LoadCerts(MachineKeyFlags);
                foreach(X509Certificate2 cert in certs)
                {
                    string comment = "EXISTS";
                    if (!store.Contains(cert))
                    {
                        store.Add(cert);
                        comment = "ADDED";
                    }
                    
                    WriteLine("{0}: {1}, Thumbprint:{2}", comment, cert.Subject, cert.Thumbprint);
                }
            }
        }

        private static SystemX509Store OpenStore(string storeName)
        {
            SystemX509Store store;
            switch (storeName.ToLower())
            {
                case "public":
                    store = SystemX509Store.OpenExternalEdit();
                    break;
                case "private":
                    store = SystemX509Store.OpenPrivateEdit();
                    break;
                default:
                    throw new ArgumentException(storeName);
            }
            return store;
        }

        private const string CertificateAddMachineUsage
            = "Import a certificate from a file and push it into the named local Machine store."
              + Constants.CRLF + " storeName (Private | Public)"
              + Constants.CRLF + CertificateFileInfo.Usage;

        /// <summary>
        /// Retrieve a certificate by its ID
        /// </summary>
        [Command(Name = "Certificate_ByID_Get", Usage = CertificateByIDGetUsage)]
        public void CertificateByIDGet(string[] args)
        {
            long certificateID = args.GetRequiredValue<int>(0);
            CertificateGetOptions options = new CertificateGetOptions()
            {
                IncludeData = args.GetOptionalValue(1, false),
                IncludePrivateKey = args.GetOptionalValue(2, false)
            };

            string outputFile = args.GetOptionalValue(3, string.Empty);

            Certificate certificate = Client.GetCertificate(certificateID, options);
            this.Print(certificate);

            if (certificate == null)
            {
                return; 
            }

            if (!string.IsNullOrEmpty(outputFile) && certificate.HasData)
            {
                File.WriteAllBytes(outputFile, certificate.Data); 
            }
        }

        private const string CertificateByIDGetUsage
            = "Retrieve a certificate by its id."
              + Constants.CRLF + "    certificateID [certData] [privatekey] [outputFile]"
              + Constants.CRLF + "\t certificateID: the cert ID to get."
              + Constants.CRLF + "\t certData: (True/False) Fetch certificate data"
              + Constants.CRLF + "\t privateKey: (True/False) Include private key"
              + Constants.CRLF + "\t outputFile: (optional) The output filename for the certificate."; 
        
        /// <summary>
        /// Get all certificates for an owner
        /// </summary>
        [Command(Name = "Certificate_Get", Usage = CertificateGetUsage)]
        public void CertificateGet(string[] args)
        {
            string owner = args.GetRequiredValue(0);            
            CertificateGetOptions options = GetOptions(args, 1);
            
            Certificate[] certs = Client.GetCertificatesForOwner(owner, options); 
            this.Print(certs);
        }

        private const string CertificateGetUsage
            = "Retrieve all certificates for an owner."
              + Constants.CRLF + "    owner [options]"
              + Constants.CRLF + "\t owner: Certificate owner"
              + Constants.CRLF + PrintOptionsUsage;
        
        /// <summary>
        /// Set the status of a certificate
        /// </summary>
        [Command(Name = "Certificate_Status_Set", Usage = CertificateStatusSetUsage)]
        public void CertificateStatusSet(string[] args)
        {
            string owner = args.GetRequiredValue(0);
            EntityStatus status = args.GetRequiredEnum<EntityStatus>(1);
            
            Client.SetCertificateStatusForOwner(owner, status);
        }

        private const string CertificateStatusSetUsage
            = "Set the status for ALL certificates for an OWNER."
            + Constants.CRLF + "     owner status"
            + Constants.CRLF + "\t owner: Certificate owner"
            + Constants.CRLF + "\t status: " + Constants.EntityStatusString;
        
        /// <summary>
        /// Remove certificate
        /// </summary>
        [Command(Name = "Certificate_Remove", Usage = CertificateRemoveUsage)]
        public void CertificateRemove(string[] args)
        {
            long certificateID = args.GetRequiredValue<long>(0);
            
            Client.RemoveCertificate(certificateID);
        }

        private const string CertificateRemoveUsage
            = "Remove certificate with given ID"
              + Constants.CRLF + "    certificateID";
        
        /// <summary>
        /// Mirrors what the production gateway does
        /// </summary>
        [Command(Name = "Certificate_Resolve", Usage = CertificateResolveUsage)]
        public void CertificateResolve(string[] args)
        {
            MailAddress owner = new MailAddress(args.GetRequiredValue(0));
            CertificateGetOptions options = GetOptions(args, 1);
            options.Status = EntityStatus.Enabled;  // We only ever resolve Enabled Certs

            Certificate[] certs = Client.GetCertificatesForOwner(owner.Address, options);
            if (certs.IsNullOrEmpty())
            {
                certs = Client.GetCertificatesForOwner(owner.Host, options);
            }
            this.Print(certs);
        }

        private const string CertificateResolveUsage
            = "Resolves certificates for an owner - like the Smtp Gateway would."
              + Constants.CRLF + "    owner [certData] [privateKey]"
              + Constants.CRLF + "\t owner: Certificate owner"
              + Constants.CRLF + "\t certData: (True/False) Fetch certificate data"
              + Constants.CRLF + "\t privateKey: (True/False) Include private key";
        
        /// <summary>
        /// Export certs in zone file format
        /// </summary>
        [Command(Name = "Certificate_Export", Usage = CertificateExportUsage)]
        public void CertificateExport(string[] args)
        {
            string owner = args.GetRequiredValue(0);
            string outputFile = args.GetOptionalValue(1, null);
            
            CertificateGetOptions options = new CertificateGetOptions { IncludeData = true, IncludePrivateKey = false};
            Certificate[] certs = Client.GetCertificatesForOwner(owner, options);
            if (certs.IsNullOrEmpty())
            {
                WriteLine("No certificates found");
                return;
            }            
            ExportCerts(certs, outputFile);
        }

        private const string CertificateExportUsage
            = "Export certificates for an owner in zone file format"
              + Constants.CRLF + "    owner [outputFile]"
              + Constants.CRLF + "\t owner: certificate owner"
              + Constants.CRLF + "\t outputFile: (Optional) Export to file. Else write to Console";

        /// <summary>
        /// Export all Enabled public certificates in zone file format
        /// </summary>
        [Command(Name = "Certificate_Export_All", Usage = CertificateExportAllUsage)]
        public void CertificateExportAll(string[] args)
        {
            string outputFile = args.GetOptionalValue(0, null);
            int chunkSize = args.GetOptionalValue(1, 25);
            
            CertificateGetOptions options = new CertificateGetOptions { IncludeData = true, IncludePrivateKey = false };
            IEnumerable<Certificate> certs = Client.EnumerateCertificates(chunkSize, options);
            
            ExportCerts(certs, outputFile);
        }

        private const string CertificateExportAllUsage
            = "Export all enabled public certificates in zone file FORMAT"
              + Constants.CRLF + "You can place this output directly into your zone file"
              + Constants.CRLF + "     [outputFile] [chunkSize]"
              + Constants.CRLF + "\t outputFile: (Optional) Export to file. Else write to Console"
              + Constants.CRLF + "\t chunkSize: (Optional) Enumeration size. Default is 25";
        
        /// <summary>
        /// Export public certs for private keys in the machine store
        /// </summary>
        /// <param name="args"></param>
        [Command(Name = "Certificate_Export_Machine", Usage = CertificateExportMachineUsage)]
        public void CertificateExportMachine(string[] args)
        {
            string storeName = args.GetOptionalValue(0, "NHINDPrivate");
            string outputFile = args.GetOptionalValue(1, null);
            using (SystemX509Store store = new SystemX509Store(CryptoUtility.OpenStoreRead(storeName, StoreLocation.LocalMachine), null))
            {
                ExportCerts(store, outputFile);
            }
        }

        private const string CertificateExportMachineUsage
            = "Exports public certificates for all certs in the given store"
              + Constants.CRLF + "    [storeName] [outputFile]"
              + Constants.CRLF + "\t storeName: (optional) Default is NHINDPrivate."
              + Constants.CRLF + "\t outputFile: (optional) Export to file. Else write to Console";


        /// <summary>
        /// Export the given CER file in "zone" format
        /// </summary>
        /// <param name="args"></param>
        [Command(Name = "Certificate_Export_FromFile", Usage = CertificateExportFromFileUsage)]
        public void CertificateExportFromFile(string[] args)
        {
            string path = args.GetRequiredValue(0);
            string outputFile = args.GetOptionalValue(1, null);

            IEnumerable<X509Certificate2> certs;
            if (Directory.Exists(path))
            {
                certs = from file in Directory.GetFiles(path, "*.cer")
                        select new X509Certificate2(file);
            }
            else if (File.Exists(path))
            {
                certs = new X509Certificate2Collection(new X509Certificate2(path)).Enumerate();
            }
            else
            {
                throw new FileNotFoundException(path);
            }

            ExportCerts(certs, outputFile);
        }
        const string CertificateExportFromFileUsage =
                "Exports public certificates in given file or folder in zone file format"
              + Constants.CRLF + "    [fileName or folderPath] [outputFile]"
              + Constants.CRLF + "    fileName or foldrePath: If file, exports file. If folder, exports all certificates in folder"
              + Constants.CRLF + "    outputFile: (optional) Export to this file. Else write to Console";
        
        /// <summary>
        /// Resolves certificates for a domain or email address using Dns
        /// </summary>
        /// <param name="args"></param>
        [Command(Name="Certificate_DnsResolve", Usage=CertificateDnsResolveUsage)]
        public void CertificateDnsResolve(string[] args)
        {
            string domain = args.GetRequiredValue(0);
            IPAddress server = IPAddress.Parse(args.GetOptionalValue(1, "8.8.8.8"));
            string fallbackDomain = args.GetOptionalValue(2, null);
            DnsCertResolver resolver = new DnsCertResolver(server, TimeSpan.FromSeconds(5), fallbackDomain);

            MailAddress address = null;
            try
            {
                address = new MailAddress(domain);
            }
            catch
            {
            }

            X509Certificate2Collection certs;
            if (address != null)
            {
                this.WriteLine("Resolving mail address {0}", domain);
                certs = resolver.GetCertificates(address);
            }
            else
            {
                certs = resolver.GetCertificatesForDomain(domain);
            }

            Print(certs);
        }

        private const string CertificateDnsResolveUsage =
            "Resolve certificates for an address or domain using Dns"
            + Constants.CRLF + "   domain or address"
            + Constants.CRLF + "   server : (optional)";


        [Command(Name = "Certificate_List_All", Usage = CertificateListAllUsage)]
        public void CertificateListAll(string[] args)
        {
            string outputFile = args.GetOptionalValue(0, null);
            int chunkSize = args.GetOptionalValue(1, 25);

            CertificateGetOptions options = new CertificateGetOptions { IncludeData = true, IncludePrivateKey = false };
            IEnumerable<Certificate> certs = Client.EnumerateCertificates(chunkSize, options);
            foreach(Certificate cert in certs)
            {
                this.Print(cert);
                CommandUI.PrintSectionBreak();
            }
        }
        private const string CertificateListAllUsage
            = "List all certificates"
              + Constants.CRLF + "  [chunkSize]"
              + Constants.CRLF + "\t chunkSize: (Optional) Enumeration size. Default is 25";

        /// <summary>
        /// Enumerate all certificates looking for ones matching the given file
        /// </summary>
        [Command(Name = "Certificate_Search_ByFile", Usage = CertificateSearchFileUsage)]
        public void CertificateSearchByFile(string[] args)
        {
            CertificateFileInfo certFileInfo = CertificateFileInfo.Create(0, args);
            MemoryX509Store certs = certFileInfo.LoadCerts();

            WriteLine(string.Empty);

            this.Search(
                x => (certs.FirstOrDefault(y => this.Match(x, y)) != null)
            );

        }
        private const string CertificateSearchFileUsage
            = "Search for a certificate similar to those in the given file."
              + Constants.CRLF + "Currently BRUTE FORCE: May be slow."
              + Constants.CRLF + CertificateFileInfo.Usage;

        /// <summary>
        /// Enumerate all certificates looking for ones matching the given file
        /// </summary>
        [Command(Name = "Certificate_Search_ByName", Usage = CertificateSearchNameUsage)]
        public void CertificateSearchByName(string[] args)
        {
            string name = args.GetRequiredValue(0);
            bool exactMatch = args.GetOptionalValue(1, true);

            WriteLine(string.Empty);
            if (exactMatch)
            {
                this.Search(
                    x => this.Match(x, name)
                );
            }
            else
            {
                this.Search(
                    x => this.MatchContains(x, name)
                );
            }
        }
        private const string CertificateSearchNameUsage
            = "Search for a certificate with the given distinguished name."
              + Constants.CRLF + "Currently BRUTE FORCE: May be slow."
              + Constants.CRLF + "    name [exactMatch]"
              + Constants.CRLF + "\t name: distinguished name to look for"
              + Constants.CRLF + "\t exactMatch: (Optional boolean) Do a contains search. Default: true";

        
        //---------------------------------------
        //
        // Implementation...
        //
        //---------------------------------------
        
        internal void ExportCerts(IEnumerable<Certificate> certs, string filePath)
        {
            if (string.IsNullOrEmpty(filePath))
            {
                ExportCerts(certs, System.Console.Out, false);
                return;
            }
                        
            using(StreamWriter writer = new StreamWriter(filePath))
            {
                ExportCerts(certs, writer, true);
            }
        }

        internal void ExportCerts(IEnumerable<Certificate> certs, TextWriter writer, bool isOutputFile)
        {
            foreach (Certificate cert in certs)
            {
                DnsX509Cert dnsCert = new DnsX509Cert(cert.Data);
                dnsCert.Export(writer, cert.Owner);
                writer.WriteLine();
                
                if (isOutputFile)
                {
                    WriteLine("{0}, {1}, {2}, {3}", cert.Owner, dnsCert.Name, cert.ValidStartDate, cert.ValidEndDate);
                }
            }
        }

        internal void ExportCerts(IEnumerable<X509Certificate2> certs, string filePath)
        {
            if (string.IsNullOrEmpty(filePath))
            {
                ExportCerts(certs, System.Console.Out, false);
                return;
            }

            using (StreamWriter writer = new StreamWriter(filePath))
            {
                ExportCerts(certs, writer, true);
            }
        }

        internal void ExportCerts(IEnumerable<X509Certificate2> certs, TextWriter writer, bool isOutputFile)
        {
            foreach (X509Certificate2 cert in certs)
            {
                DnsX509Cert dnsCert = new DnsX509Cert(cert);
                dnsCert.Export(writer, dnsCert.Name);
                writer.WriteLine();

                if (isOutputFile)
                {
                    WriteLine(dnsCert.Name);
                }
            }
        }
        
        internal void PushCerts(IEnumerable<X509Certificate2> certs, bool checkForDupes, EntityStatus? status)
        {
            foreach (X509Certificate2 cert in certs)
            {
                string owner = cert.ExtractEmailNameOrName();
                try
                {
                    if (!checkForDupes || !Client.Contains(cert))
                    {
                        Certificate certEntry = new Certificate(owner, cert);
                        if (status != null)
                        {
                            certEntry.Status = status.Value;
                        }
                        Client.AddCertificate(certEntry);                    
                        WriteLine("Added {0}", cert.Subject);
                    }
                    else
                    {
                        WriteLine("Exists {0}", cert.Subject);
                    }
                }
                catch (FaultException<ConfigStoreFault> ex)
                {
                    if (ex.Detail.Error == ConfigStoreError.UniqueConstraint)
                    {
                        WriteLine("Exists {0}", cert.Subject);
                    }
                }
            }
        }

        internal MemoryX509Store LoadCerts(string filePath, string password)
        {
            MemoryX509Store certStore = new MemoryX509Store();
            LoadCerts(certStore, filePath, password, X509KeyStorageFlags.Exportable);
            return certStore;
        }

        internal MemoryX509Store LoadCerts(string filePath, string password, X509KeyStorageFlags flags)
        {
            MemoryX509Store certStore = new MemoryX509Store();
            LoadCerts(certStore, filePath, password, flags);
            return certStore;
        }

        internal void LoadCerts(MemoryX509Store certStore, string filePath, string password, X509KeyStorageFlags flags)
        {
            string ext = Path.GetExtension(filePath) ?? string.Empty;
            switch (ext.ToLower())
            {
                default:
                    certStore.ImportKeyFile(filePath, flags);
                    break;

                case ".pfx":
                    certStore.ImportKeyFile(filePath, password, flags);
                    break;
            }
        }
        
        internal CertificateGetOptions GetOptions(string[] args, int firstArg)
        {
            CertificateGetOptions options = new CertificateGetOptions
            {
                IncludeData = args.GetOptionalValue(firstArg, false),
                IncludePrivateKey = args.GetOptionalValue(firstArg + 1, false)
            };            
            
            if (args.GetValueOrNull(firstArg + 2) != null)
            {
                options.Status = args.GetRequiredEnum<EntityStatus>(firstArg + 2);
            }            

            return options; 
        }        

        internal const string PrintOptionsUsage
            = "\t options:"
              + Constants.CRLF + "\t [certData] [privatekey] [status]"
              + Constants.CRLF + "\t certData: (True/False) Fetch certificate data"
              + Constants.CRLF + "\t privateKey: (True/False) Include private key"
              + Constants.CRLF + "\t status: " + Constants.EntityStatusString;


        void Print(Certificate[] certs)
        {
            if (certs == null || certs.Length == 0)
            {
                WriteLine("No certificates found");
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
            if (cert == null)
            {
                WriteLine("No certificate found");
                return; 
            }

            CommandUI.Print("Owner", cert.Owner);
            CommandUI.Print("Thumbprint", cert.Thumbprint); 
            CommandUI.Print("ID", cert.ID);
            CommandUI.Print("CreateDate", cert.CreateDate);
            CommandUI.Print("ValidStart", cert.ValidStartDate);
            CommandUI.Print("ValidEnd", cert.ValidEndDate);
            CommandUI.Print("Status", cert.Status);
            
            if (cert.HasData)
            {
                using(DisposableX509Certificate2 x509 = cert.ToX509Certificate())
                {
                    Print(x509);
                }
            }
        }
                
        internal void Print(X509Certificate2Collection certs)
        {
            if (certs.IsNullOrEmpty())
            {   
                WriteLine("No certificates found");
                return;
            }
            
            foreach(X509Certificate2 cert in certs)
            {
                Print(cert);
                CommandUI.PrintSectionBreak();
            }
        }        
        
        internal void Print(X509Certificate2 x509)
        {
            CommandUI.Print("Subject", x509.Subject);
            CommandUI.Print("SerialNumber", x509.SerialNumber);
            CommandUI.Print("Issuer", x509.Issuer);
            CommandUI.Print("HasPrivateKey", x509.HasPrivateKey);
        }

        internal bool Match(X509Certificate2 x, X509Certificate2 y)
        {
            if (x.Thumbprint == y.Thumbprint)
            {
                WriteLine("Thumbprint Matched");
                return true;
            }
            if (x.MatchEmailName(y.GetNameInfo(X509NameType.EmailName, false)))
            {
                WriteLine("Email Matched");
                return true;
            }
            if (x.MatchName(y.GetNameInfo(X509NameType.SimpleName, false)))
            {
                WriteLine("Name Matched");
                return true;
            }
            
            return false;
        }

        internal bool Match(X509Certificate2 x, string name)
        {
            if (x.MatchEmailName(name))
            {
                WriteLine("Email Matched");
                return true;
            }
            if (x.MatchName(name))
            {
                WriteLine("Name Matched");
                return true;
            }

            return false;
        }

        internal bool MatchContains(X509Certificate2 x, string name)
        {
            bool match = false;

            string xName = x.GetNameInfo(X509NameType.SimpleName, false);            
            if (!string.IsNullOrEmpty(xName) && xName.IndexOf(name, StringComparison.OrdinalIgnoreCase) >= 0)
            {
                WriteLine("Name Matched");
                match = true;
            }

            xName = x.GetNameInfo(X509NameType.EmailName, false);
            if (!string.IsNullOrEmpty(xName) && xName.IndexOf(name, StringComparison.OrdinalIgnoreCase) >= 0)
            {
                WriteLine("Email Matched");
                match = true;
            }

            return match;
        }

        void Search(Func<X509Certificate2, bool> filter)
        {
            CertificateGetOptions getOptions = new CertificateGetOptions() { IncludeData = true, IncludePrivateKey = false};
            Search(Client.EnumerateCertificates(25, getOptions), filter);
        }

        void Search(IEnumerable<Certificate> query, Func<X509Certificate2, bool> filter)
        {
            int matchCount = 0;
            var matches = from cert in query
                          where filter(cert.ToX509Certificate())
                          select cert;
            
            foreach (Certificate match in matches)
            {
                ++matchCount;
                WriteLine(string.Empty);
                Print(match);
                CommandUI.PrintSectionBreak();
            }

            if (matchCount == 0)
            {
                WriteLine("No matches");
            }
        }
    }
}