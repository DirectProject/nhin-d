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
    public class TestCommands
    {
        public void Command_Test_Certs_Install(string[] args)
        {
            string path = args.GetOptionalValue(0, Directory.GetCurrentDirectory());
            EnsureStandardMachineStores(path);
        }
        public void Usage_Test_Certs_Install()
        {
            System.Console.WriteLine("Install test certs into machine stores");
        }

        public void Command_Test_Certs_InstallInService(string[] args)
        {
            string path = args.GetOptionalValue(0, Directory.GetCurrentDirectory());
            EnsureStandardCertsInService(path);
        }
        public void Usage_Test_Certs_InstallInService()
        {
            System.Console.WriteLine("Install test certs into config service");
        }
        
        static string MakeCertificatesPath(string basePath, string agentFolder)
        {
            return Path.Combine(basePath, Path.Combine("Certificates", agentFolder));
        }
                
        internal static void EnsureStandardMachineStores(string path)
        {
            SystemX509Store.CreateAll();

            string basePath = path;
            string redmondCertsPath = MakeCertificatesPath(basePath, "redmond");
            string nhindCertsPath = MakeCertificatesPath(basePath, "nhind");

            SystemX509Store store;
            System.Console.WriteLine("Installing Private Certs");
            using (store = SystemX509Store.OpenPrivateEdit())
            {
                InstallCerts(store, LoadCerts(redmondCertsPath, "Private"));
                InstallCerts(store, LoadCerts(nhindCertsPath, "Private"));
            }

            System.Console.WriteLine("Installing Public Certs");
            using (store = SystemX509Store.OpenExternalEdit())
            {
                InstallCerts(store, LoadCerts(redmondCertsPath, "Public"));
                InstallCerts(store, LoadCerts(nhindCertsPath, "Public"));
            }

            System.Console.WriteLine("Installing Anchors Certs");
            using (store = SystemX509Store.OpenAnchorEdit())
            {
                InstallCerts(store, LoadCerts(redmondCertsPath, "IncomingAnchors"));
                InstallCerts(store, LoadCerts(redmondCertsPath, "OutgoingAnchors"));

                InstallCerts(store, LoadCerts(nhindCertsPath, "IncomingAnchors"));
                InstallCerts(store, LoadCerts(nhindCertsPath, "OutgoingAnchors"));
            }
        }

        internal static MemoryX509Store LoadCerts(string certsBasePath, string childPath)
        {
            return LoadCertificates(Path.Combine(certsBasePath, childPath));
        }

        static MemoryX509Store LoadCertificates(string folderPath)
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
            foreach(string file in files)
            {
                CertificateCommands.LoadCerts(certStore, file, "passw0rd!", X509KeyStorageFlags.MachineKeySet | X509KeyStorageFlags.Exportable | X509KeyStorageFlags.PersistKeySet);
            }
            return certStore;
        }

        static void InstallCerts(IX509CertificateStore store, IEnumerable<X509Certificate2> certs)
        {
            foreach (X509Certificate2 cert in certs)
            {
                if (!store.Contains(cert))
                {
                    System.Console.WriteLine("Installing {0} HasPrivateKey={1}", cert.ExtractEmailNameOrName(), cert.HasPrivateKey);
                    store.Add(cert);
                }
            }
        }
        
        internal static void EnsureStandardCertsInService(string basePath)
        {
            System.Console.WriteLine("Installing Private Certs in config service");

            string redmondCertsPath = MakeCertificatesPath(basePath, "redmond");
            string nhindCertsPath = MakeCertificatesPath(basePath, "nhind");

            CertificateCommands.PushCerts(LoadCerts(redmondCertsPath, "Private"), true, EntityStatus.Enabled);
            CertificateCommands.PushCerts(LoadCerts(nhindCertsPath, "Private"), true, EntityStatus.Enabled);
            
            System.Console.WriteLine("Installing Anchors in config service");

            AnchorCommands.PushCerts("redmond.hsgincubator.com", LoadCerts(redmondCertsPath, "IncomingAnchors"), true, EntityStatus.Enabled);
            AnchorCommands.PushCerts("redmond.hsgincubator.com", LoadCerts(redmondCertsPath, "OutgoingAnchors"), true, EntityStatus.Enabled);
            AnchorCommands.PushCerts("nhind.hsgincubator.com", LoadCerts(nhindCertsPath, "IncomingAnchors"), true, EntityStatus.Enabled);
            AnchorCommands.PushCerts("nhind.hsgincubator.com", LoadCerts(nhindCertsPath, "OutgoingAnchors"), true, EntityStatus.Enabled);
        }        
    }
}