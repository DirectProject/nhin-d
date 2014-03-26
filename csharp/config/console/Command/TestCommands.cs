/* 
 Copyright (c) 2010, Direct Project
 All rights reserved.

 Authors:
    Umesh Madan     umeshma@microsoft.com
  
Redistribution and use in source and binary forms, with or without modification, are permitted provided that the following conditions are met:

Redistributions of source code must retain the above copyright notice, this list of conditions and the following disclaimer.
Redistributions in binary form must reproduce te above copyright notice, this list of conditions and the following disclaimer in the documentation and/or other materials provided with the distribution.
Neither the name of The Direct Project (directproject.org) nor the names of its contributors may be used to endorse or promote products derived from this software without specific prior written permission.
THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 
*/
using System;
using System.Collections.Generic;
using System.IO;
using System.Security.Cryptography.X509Certificates;

using Health.Direct.Common.Certificates;
using Health.Direct.Common.Extensions;
using Health.Direct.Config.Store;
using Health.Direct.Config.Tools.Command;

namespace Health.Direct.Config.Console.Command
{
    /// <summary>
    /// Commands to install unit test certificates. 
    /// These are stop-gap and make LOTS of assumptions about paths, names, etc...
    /// </summary>
    public class TestCommands : CommandsBase
    {
        internal TestCommands(ConfigConsole console) : base(console)
        {
        }

        [Command(Name ="Test_Certs_Install",
            Usage = "Install test certs into machine stores")]
        public void TestCertsInstall(string[] args)
        {
            string path = args.GetOptionalValue(0, Directory.GetCurrentDirectory());
            EnsureStandardMachineStores(path);
        }

        [Command(Name = "Test_Certs_InstallInService",
            Usage = "Install test certs into config service")]
        public void TestCertsInstallInService(string[] args)
        {
            string path = args.GetOptionalValue(0, Directory.GetCurrentDirectory());
            EnsureStandardCertsInService(path);
        }
        
        [Command(Name = "Test_Stores_Ensure", Usage="Ensures all standard machine stores are created")]
        public void EnsureMachineStores(string[] args)
        {
            SystemX509Store store = null;

            using (store = SystemX509Store.OpenPrivateEdit())
            {
                WriteLine("Created Private Store");
            }
            using (store = SystemX509Store.OpenExternalEdit())
            {
                WriteLine("Created Public Store");
            }
            using (store = SystemX509Store.OpenAnchorEdit())
            {
                WriteLine("Created Anchor Store");
            }
        }
        
        static string MakeCertificatesPath(string basePath, string agentFolder)
        {
            return Path.Combine(basePath, Path.Combine("Certificates", agentFolder));
        }
                
        void EnsureStandardMachineStores(string path)
        {
            SystemX509Store.CreateAll();

            string basePath = path;
            string redmondCertsPath = MakeCertificatesPath(basePath, "redmond");
            string nhindCertsPath = MakeCertificatesPath(basePath, "nhind");
            string noAnchorCertsPath = MakeCertificatesPath(basePath, "noAnchor");

            SystemX509Store store;
            WriteLine("Installing Private Certs");
            using (store = SystemX509Store.OpenPrivateEdit())
            {
                InstallCerts(store, LoadCerts(redmondCertsPath, "Private"));
                InstallCerts(store, LoadCerts(nhindCertsPath, "Private"));
            }

            WriteLine("Installing Public Certs");
            using (store = SystemX509Store.OpenExternalEdit())
            {
                InstallCerts(store, LoadCerts(redmondCertsPath, "Public"));
                InstallCerts(store, LoadCerts(nhindCertsPath, "Public"));
                InstallCerts(store, LoadCerts(noAnchorCertsPath, "Public"));
            }

            WriteLine("Installing Anchors Certs");
            using (store = SystemX509Store.OpenAnchorEdit())
            {
                InstallCerts(store, LoadCerts(redmondCertsPath, "IncomingAnchors"));
                InstallCerts(store, LoadCerts(redmondCertsPath, "OutgoingAnchors"));

                InstallCerts(store, LoadCerts(nhindCertsPath, "IncomingAnchors"));
                InstallCerts(store, LoadCerts(nhindCertsPath, "OutgoingAnchors"));
            }
        }

        MemoryX509Store LoadCerts(string certsBasePath, string childPath)
        {
            return LoadCertificates(Path.Combine(certsBasePath, childPath));
        }

        MemoryX509Store LoadCertificates(string folderPath)
        {
            if (!Directory.Exists(folderPath))
            {
                throw new DirectoryNotFoundException("Directory not found: " + folderPath);
            }

            MemoryX509Store certStore = new MemoryX509Store();
            string[] files = Directory.GetFiles(folderPath);
            if (ArrayExtensions.IsNullOrEmpty(files))
            {
                throw new ArgumentException("Empty directory");
            }

            CertificateCommands certcmd = GetCommand<CertificateCommands>();
            foreach(string file in files)
            {
                certcmd.LoadCerts(certStore, file, "passw0rd!", X509KeyStorageFlags.MachineKeySet | X509KeyStorageFlags.Exportable | X509KeyStorageFlags.PersistKeySet);
            }

            return certStore;
        }

        void InstallCerts(IX509CertificateStore store, IEnumerable<X509Certificate2> certs)
        {
            foreach (X509Certificate2 cert in certs)
            {
                if (!store.Contains(cert))
                {
                    WriteLine("Installing {0} HasPrivateKey={1}", cert.ExtractEmailNameOrName(), cert.HasPrivateKey);
                    store.Add(cert);
                }
            }
        }
        
        void EnsureStandardCertsInService(string basePath)
        {
            WriteLine("Installing Private Certs in config service");

            string redmondCertsPath = MakeCertificatesPath(basePath, "redmond");
            string nhindCertsPath = MakeCertificatesPath(basePath, "nhind");

            CertificateCommands certcmds = GetCommand<CertificateCommands>();
            certcmds.PushCerts(LoadCerts(redmondCertsPath, "Private"), true, EntityStatus.Enabled);
            certcmds.PushCerts(LoadCerts(nhindCertsPath, "Private"), true, EntityStatus.Enabled);
            
            WriteLine("Installing Anchors in config service");

            AnchorCommands anchorcmds = GetCommand<AnchorCommands>();
            anchorcmds.PushCerts("redmond.hsgincubator.com", LoadCerts(redmondCertsPath, "IncomingAnchors"), true, EntityStatus.Enabled);
            anchorcmds.PushCerts("redmond.hsgincubator.com", LoadCerts(redmondCertsPath, "OutgoingAnchors"), true, EntityStatus.Enabled);
            anchorcmds.PushCerts("nhind.hsgincubator.com", LoadCerts(nhindCertsPath, "IncomingAnchors"), true, EntityStatus.Enabled);
            anchorcmds.PushCerts("nhind.hsgincubator.com", LoadCerts(nhindCertsPath, "OutgoingAnchors"), true, EntityStatus.Enabled);
        }        
    }
}