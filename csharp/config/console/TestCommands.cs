using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.IO;
using System.Security.Cryptography.X509Certificates;
using NHINDirect;
using NHINDirect.Certificates;
using NHINDirect.Tools.Command;

namespace NHINDirect.Config.Command
{
    public class TestCommands
    {
        public TestCommands()
        {
        }
        
        public void Command_Test_Certs_Install(string[] args)
        {
            string path = args.GetOptionalValue(0, Directory.GetCurrentDirectory());
            string storetype = args.GetOptionalValue(1, "local").ToLower();
            EnsureStandardMachineStores(path);
        }
        public void Usage_Test_Certs_Install()
        {
            Console.WriteLine("Install test certs into machine stores");
            Console.WriteLine("   [local (default) | service]");
        }

        public void Command_Test_Certs_InstallInService(string[] args)
        {
            string path = args.GetOptionalValue(0, Directory.GetCurrentDirectory());
            EnsureStandardCertsInService(path);
        }
        public void Usage_Test_Certs_InstallInService()
        {
            Console.WriteLine("Install test certs into config service");
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
            Console.WriteLine("Installing Private Certs");
            using (store = SystemX509Store.OpenPrivateEdit())
            {
                InstallCerts(store, LoadCerts(redmondCertsPath, "Private"));
                InstallCerts(store, LoadCerts(nhindCertsPath, "Private"));
            }

            Console.WriteLine("Installing Public Certs");
            using (store = SystemX509Store.OpenExternalEdit())
            {
                InstallCerts(store, LoadCerts(redmondCertsPath, "Public"));
                InstallCerts(store, LoadCerts(nhindCertsPath, "Public"));
            }

            Console.WriteLine("Installing Anchors Certs");
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
            if (files.IsNullOrEmpty())
            {
                throw new ArgumentException("Empty directory");
            }
            foreach(string file in files)
            {
                CertificateCommands.LoadCerts(certStore, file, "passw0rd!");
            }
            return certStore;
        }

        static void InstallCerts(IX509CertificateStore store, IEnumerable<X509Certificate2> certs)
        {
            foreach (X509Certificate2 cert in certs)
            {
                if (!store.Contains(cert))
                {
                    Console.WriteLine("Installing {0} HasPrivateKey={1}", cert.ExtractEmailNameOrName(), cert.HasPrivateKey);
                    store.Add(cert);
                }
            }
        }
        
        internal static void EnsureStandardCertsInService(string basePath)
        {
            Console.WriteLine("Installing Private Certs in config service");

            string redmondCertsPath = MakeCertificatesPath(basePath, "redmond");
            string nhindCertsPath = MakeCertificatesPath(basePath, "nhind");
            
            CertificateCommands.PushCerts(LoadCerts(redmondCertsPath, "Private"), true);
            CertificateCommands.PushCerts(LoadCerts(nhindCertsPath, "Private"), true);
            
            Console.WriteLine("Installing Anchors in config service");

            AnchorCommands.PushCerts("redmond.hsgincubator.com", LoadCerts(redmondCertsPath, "IncomingAnchors"), true);
            AnchorCommands.PushCerts("redmond.hsgincubator.com", LoadCerts(redmondCertsPath, "OutgoingAnchors"), true);
            AnchorCommands.PushCerts("nhind.hsgincubator.com", LoadCerts(nhindCertsPath, "IncomingAnchors"), true);
            AnchorCommands.PushCerts("nhind.hsgincubator.com", LoadCerts(nhindCertsPath, "OutgoingAnchors"), true);
        }
    }
}
