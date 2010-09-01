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
            string path = args.GetOptionalValue(0, null);
            EnsureStandardMachineStores(path ?? Directory.GetCurrentDirectory());
        }
        public void Usage_Test_Certs_Install()
        {
            Console.WriteLine("Install test certs into machine stores");
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
            using (store = SystemX509Store.OpenPrivateEdit())
            {
                InstallCerts(store, LoadCerts(redmondCertsPath, "Private"));
                InstallCerts(store, LoadCerts(nhindCertsPath, "Private"));
            }

            using (store = SystemX509Store.OpenExternalEdit())
            {
                InstallCerts(store, LoadCerts(redmondCertsPath, "Public"));
                InstallCerts(store, LoadCerts(nhindCertsPath, "Public"));
            }

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
            for (int i = 0; i < files.Length; ++i)
            {
                string file = files[i];
                string ext = Path.GetExtension(file) ?? string.Empty;
                switch (ext.ToLower())
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

        static void InstallCerts(IX509CertificateStore store, IEnumerable<X509Certificate2> certs)
        {
            foreach (X509Certificate2 cert in certs)
            {
                if (!store.Contains(cert))
                {
                    store.Add(cert);
                }
            }
        }
    }
}
