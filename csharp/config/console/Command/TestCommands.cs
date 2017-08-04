/* 
 Copyright (c) 2010, Direct Project
 All rights reserved.

 Authors:
    Umesh Madan         umeshma@microsoft.com
    Dávid Koronthály    koronthaly@hotmail.com

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
using Org.BouncyCastle.Asn1.X509;

namespace Health.Direct.Config.Console.Command
{
    /// <summary>
    /// Commands to install unit test certificates. 
    /// These are stop-gap and make LOTS of assumptions about paths, names, etc...
    /// </summary>
    public class TestCommands : CommandsBase
    {
        public TestCommands(ConfigConsole console)
            : base(console)
        {
        }

        /// <summary>
        /// The old test certificates generated using 'makecert' command do not meet current Direct standard.
        /// This is an example how to generate a full chain of certificates, including all required extensions.
        /// </summary>
        /// <param name="args">Directory to save certificate files to. Optional, defaults to current directory.</param>
        [Command(Name = "Test_Certs_Create", Usage = "Creates test certificates.")]
        public void TestCertsCreate(string[] args)
        {
            // Initialize the names.
            var rootDomain = "hsgincubator.com";
            var redmondDomain = $"redmond.{rootDomain}";
            var nhindDomain = $"nhind.{rootDomain}";
            var testEmailAddress = $"test@{nhindDomain}";
            var testDomain = testEmailAddress.Replace('@', '.');

            string path = args.GetOptionalValue(0, Path.Combine(Directory.GetCurrentDirectory(), "Certificates"));

            if (!Directory.Exists(path))
            {
                Directory.CreateDirectory(path);
            }

            // Create a self-signed certificate authority.
            var rootCaBuilder = new CertificateBuilder(1)
            {
                SubjectDN = new X509Name($"CN={rootDomain}")
            };
            rootCaBuilder.SetSubjectAlternativeNameToDomain(rootDomain);
            var rootCa = rootCaBuilder.Generate();
            File.WriteAllBytes(Path.Combine(path, $"{rootDomain}.pfx"), rootCa.Export(X509ContentType.Pfx));
            File.WriteAllBytes(Path.Combine(path, $"{rootDomain}.cer"), rootCa.Export(X509ContentType.Cert));

            // Create valid organizational certificate.
            var redmondValidCertBuilder = new CertificateBuilder(rootCa)
            {
                AuthorityInformationAccessUri = new Uri($"http://{rootDomain}/pki/{rootDomain}.cer"),
                CrlDistributionPointUri = new Uri($"http://{rootDomain}/pki/{rootDomain}.crl"),
                SubjectDN = new X509Name($"CN={redmondDomain}")
            };
            redmondValidCertBuilder.SetSubjectAlternativeNameToDomain(redmondDomain);
            var redmondValidCert = redmondValidCertBuilder.Generate();
            File.WriteAllBytes(Path.Combine(path, $"{redmondDomain}-valid.pfx"), redmondValidCert.Export(X509ContentType.Pfx));
            File.WriteAllBytes(Path.Combine(path, $"{redmondDomain}-valid.cer"), redmondValidCert.Export(X509ContentType.Cert));

            // Create revoked organizational certificate.
            var redmondRevokedCertBuilder = new CertificateBuilder(rootCa)
            {
                AuthorityInformationAccessUri = new Uri($"http://{rootDomain}/pki/{rootDomain}.cer"),
                CrlDistributionPointUri = new Uri($"http://{rootDomain}/pki/{rootDomain}.crl"),
                SubjectDN = new X509Name($"CN={redmondDomain}")
            };
            redmondRevokedCertBuilder.SetSubjectAlternativeNameToDomain(redmondDomain);
            var redmondRevokedCert = redmondRevokedCertBuilder.Generate();
            File.WriteAllBytes(Path.Combine(path, $"{redmondDomain}-revoked.pfx"), redmondRevokedCert.Export(X509ContentType.Pfx));
            File.WriteAllBytes(Path.Combine(path, $"{redmondDomain}-revoked.cer"), redmondRevokedCert.Export(X509ContentType.Cert));

            // Create a certificate revocation list.
            var rootCrlBuilder = new CertificateRevocationListBuilder(rootCa, 1)
            {
                AuthorityInformationAccessUri = new Uri($"http://{rootDomain}/pki/{rootDomain}.cer")
            };
            rootCrlBuilder.AddRevokedCertificate(redmondRevokedCert);
            var rootCrl = rootCrlBuilder.Generate();
            var rootCrlBytes = rootCrl.GetEncoded();
            File.WriteAllBytes(Path.Combine(path, $"{rootDomain}.crl"), rootCrlBytes);

            // Intermediate certificate authority.
            var nhindCaBuilder = new CertificateBuilder(rootCa, 0)
            {
                AuthorityInformationAccessUri = new Uri($"http://{rootDomain}/pki/{rootDomain}.cer"),
                CrlDistributionPointUri = new Uri($"http://{rootDomain}/pki/{rootDomain}.crl"),
                SubjectDN = new X509Name($"CN={nhindDomain}")
            };
            nhindCaBuilder.SetSubjectAlternativeNameToDomain(nhindDomain);
            var nhindCa = nhindCaBuilder.Generate();
            File.WriteAllBytes(Path.Combine(path, $"{nhindDomain}.pfx"), nhindCa.Export(X509ContentType.Pfx));
            File.WriteAllBytes(Path.Combine(path, $"{nhindDomain}.cer"), nhindCa.Export(X509ContentType.Cert));

            // Create valid address-bound certificate.
            var testValidCertBuilder = new CertificateBuilder(nhindCa)
            {
                AuthorityInformationAccessUri = new Uri($"http://{rootDomain}/pki/{rootDomain}.cer"),
                CrlDistributionPointUri = new Uri($"http://{rootDomain}/pki/{rootDomain}.crl"),
                SubjectDN = new X509Name($"CN={testEmailAddress}")
            };
            testValidCertBuilder.SetSubjectAlternativeNameToEmail(testEmailAddress);
            var testValidCert = testValidCertBuilder.Generate();
            File.WriteAllBytes(Path.Combine(path, $"{testDomain}-valid.pfx"), testValidCert.Export(X509ContentType.Pfx));
            File.WriteAllBytes(Path.Combine(path, $"{testDomain}-valid.cer"), testValidCert.Export(X509ContentType.Cert));

            // Create revoked address-bound certificate.
            var testRevokedCertBuilder = new CertificateBuilder(nhindCa)
            {
                AuthorityInformationAccessUri = new Uri($"http://{rootDomain}/pki/{rootDomain}.cer"),
                CrlDistributionPointUri = new Uri($"http://{rootDomain}/pki/{rootDomain}.crl"),
                SubjectDN = new X509Name($"CN={testEmailAddress}")
            };
            testRevokedCertBuilder.SetSubjectAlternativeNameToEmail(testEmailAddress);
            var testRevokedCert = testRevokedCertBuilder.Generate();
            File.WriteAllBytes(Path.Combine(path, $"{testDomain}-revoked.pfx"), testRevokedCert.Export(X509ContentType.Pfx));
            File.WriteAllBytes(Path.Combine(path, $"{testDomain}-revoked.cer"), testRevokedCert.Export(X509ContentType.Cert));

            // Create a certificate revocation list.
            var nhindCrlBuilder = new CertificateRevocationListBuilder(nhindCa, 1)
            {
                AuthorityInformationAccessUri = new Uri($"http://{rootDomain}/pki/{nhindDomain}.cer")
            };
            nhindCrlBuilder.AddRevokedCertificate(redmondRevokedCert);
            var nhindCrl = nhindCrlBuilder.Generate();
            var nhindCrlBytes = nhindCrl.GetEncoded();
            File.WriteAllBytes(Path.Combine(path, $"{nhindDomain}.crl"), nhindCrlBytes);
        }

        [Command(Name = "Test_Certs_Install", Usage = "Install test certs into machine stores")]
        public void TestCertsInstall(string[] args)
        {
            string path = args.GetOptionalValue(0, Directory.GetCurrentDirectory());
            EnsureStandardMachineStores(path);
        }

        [Command(Name = "Reset_Stores", Usage = "Remove certs in all Direct Project machine stores")]
        public void ResetStores(string[] args)
        {
            SystemX509Store store;
            WriteLine("Removing all Machine Private Certs");
            using (store = SystemX509Store.OpenPrivateEdit())
            {
                foreach (var certificate in store.GetAllCertificates())
                {
                    store.Remove(certificate);
                }
            }

            WriteLine("Removing all Machine Public Certs");
            using (store = SystemX509Store.OpenExternalEdit())
            {
                foreach (var certificate in store.GetAllCertificates())
                {
                    store.Remove(certificate);
                }
            }

            WriteLine("Removing all Machine Anchors Certs");
            using (store = SystemX509Store.OpenAnchorEdit())
            {
                foreach (var certificate in store.GetAllCertificates())
                {
                    store.Remove(certificate);
                }
            }
        }

        [Command(Name = "Test_Certs_InstallInService",
            Usage = "Install test certs into config service")]
        public void TestCertsInstallInService(string[] args)
        {
            string path = args.GetOptionalValue(0, Directory.GetCurrentDirectory());
            EnsureStandardCertsInService(path);
        }

        [Command(Name = "Test_Stores_Ensure", Usage = "Ensures all standard machine stores are created")]
        public void EnsureMachineStores(string[] args)
        {
            using (SystemX509Store.OpenPrivateEdit())
            {
                WriteLine("Created Private Store");
            }

            using (SystemX509Store.OpenExternalEdit())
            {
                WriteLine("Created Public Store");
            }

            using (SystemX509Store.OpenAnchorEdit())
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

            try
            {
                string[] files = Directory.GetFiles(folderPath);

                if (files.IsNullOrEmpty())
                {
                    throw new ArgumentException("Empty directory");
                }

                CertificateCommands certcmd = GetCommand<CertificateCommands>();

                foreach (string file in files)
                {
                    certcmd.LoadCerts(certStore, file, "passw0rd!", X509KeyStorageFlags.MachineKeySet | X509KeyStorageFlags.Exportable | X509KeyStorageFlags.PersistKeySet);
                }

                return certStore;
            }
            catch
            {
                certStore.Dispose();
                throw;
            }
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