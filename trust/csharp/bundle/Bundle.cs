using System;
using System.Collections.Generic;
using System.IO;
using System.Linq;
using System.Security.Cryptography.Pkcs;
using System.Security.Cryptography.X509Certificates;
using System.Xml;
using System.Xml.Linq;
using System.Xml.Schema;
using Health.Direct.Common.Certificates;

namespace Health.Direct.Trust
{
    public class Bundle
    {


        /// <summary>
        /// Create a trust bundle.
        /// </summary>
        /// <param name="sourcePath">Folder loacation of the anchor certificates</param>
        /// <param name="destination">Trust bundle filename</param>
        /// <param name="ignore">Array of sub-strings to ignore.</param>
        /// <param name="metadata">Optional metadata</param>
        public void Create(string sourcePath, string destination, string[] ignore = null, string metadata = null)
        {
            var validMetadata = GetValidMetaData(metadata);
            const X509KeyStorageFlags flags = X509KeyStorageFlags.MachineKeySet | X509KeyStorageFlags.Exportable;
            X509Certificate2Collection certs = LoadCertificates(sourcePath, ignore, flags).GetAllCertificates();
            var p7bData = Create(certs, validMetadata);
            StoreBundle(destination, p7bData);
        }


        /// <summary>
        /// Create a trust bundle.
        /// </summary>
        /// <param name="sourcePath">Folder loacation of the anchor certificates</param>
        /// <param name="destination">Trust bundle filename</param>
        /// <param name="signingCertPath">Signing Certificate path</param>
        /// /// <param name="ignore">Array of sub-strings to ignore.</param>
        /// <param name="metadata">Optional metadata</param>
        public void Create(string sourcePath, string destination, string signingCertPath, string metadata)
        {
            var validMetadata = GetValidMetaData(metadata);
            const X509KeyStorageFlags flags = X509KeyStorageFlags.MachineKeySet | X509KeyStorageFlags.Exportable;
            X509Certificate2Collection certs = LoadCertificates(sourcePath, flags).GetAllCertificates();
            X509Certificate2 signingCert = LoadCertificate(signingCertPath, flags).First();
            var p7mData = CreateSigned(certs, signingCert, validMetadata);
            StoreBundle(destination, p7mData);
        }


        /// <summary>
        /// Create a trust bundle.
        /// </summary>
        /// <param name="sourcePath">Folder loacation of the anchor certificates</param>
        /// <param name="ignore">Array of sub-strings to ignore.</param>
        /// <param name="metadata">Optional metadata</param>
        /// <returns>raw bytes</returns>
        public byte[] Create(string sourcePath, string[] ignore = null, string metadata = null)
        {
            var validMetadata = GetValidMetaData(metadata);
            const X509KeyStorageFlags flags = X509KeyStorageFlags.MachineKeySet | X509KeyStorageFlags.Exportable;
            X509Certificate2Collection certs = LoadCertificates(sourcePath, ignore, flags).GetAllCertificates();
            var p7bData = Create(certs, validMetadata);
            return p7bData;
        }

        
        /// <summary>
        /// Create a trust bundle.
        /// </summary>
        /// <param name="sourcePath">Folder loacation of the anchor certificates</param>
        /// <param name="signingCert">Signing Certificate</param>
        /// <param name="metadata">Optional metadata</param>
        /// /// <returns>raw bytes</returns>
        public byte[] Create(string sourcePath, X509Certificate2 signingCert, string metadata)
        {
            var validMetadata = GetValidMetaData(metadata);
            const X509KeyStorageFlags flags = X509KeyStorageFlags.MachineKeySet | X509KeyStorageFlags.Exportable;
            X509Certificate2Collection certs = LoadCertificates(sourcePath, flags).GetAllCertificates();
            var p7mData = CreateSigned(certs, signingCert, validMetadata);
            return p7mData;
        }

        private static MemoryX509Store LoadCertificates(string folderPath, X509KeyStorageFlags flags)
        {
            return LoadCertificates(folderPath, null, flags);
        }

        private static MemoryX509Store LoadCertificates(string folderPath, string[] ignore, X509KeyStorageFlags flags)
        {
            if (string.IsNullOrEmpty(folderPath))
            {
                throw new ArgumentException("value was null or empty", "folderPath");
            }

            if (!Directory.Exists(folderPath))
            {
                throw new DirectoryNotFoundException("Directory not found: " + folderPath);
            }

            MemoryX509Store certStore = new MemoryX509Store();

            string[] files = Directory.GetFiles(folderPath);
            List<string> failures = new List<string>();
            foreach (string file in files)
            {
                try
                {
                    if (SkipResource(ignore, file)) continue;
                    certStore.ImportKeyFile(file, flags);
                }
                catch(Exception e)
                {
                    failures.Add(string.Format("Failed loading file {0}\r\n{1}", file, e.Message));
                }
            }
            if (failures.Count > 0)
            {
                string result = failures.Aggregate((current, f) => current + f + "\r\n");
                throw new Exception(result);
            }
            return certStore;
        }

        


        private static MemoryX509Store LoadCertificate(string path, X509KeyStorageFlags flags)
        {
            if (string.IsNullOrEmpty(path))
            {
                throw new ArgumentException("value was null or empty", "path");
            }

            if (!Directory.Exists(path))
            {
                throw new DirectoryNotFoundException("File not found: " + path);
            }

            MemoryX509Store certStore = new MemoryX509Store();
            certStore.ImportKeyFile(path, flags);
            
            return certStore;
        }

        /// <summary>
        /// Export the given certificate collection as an UNSIGNED bundle
        /// </summary>
        /// <param name="certs">Certificates to place in the bundle</param>
        /// <param name="metadata">Optional metadata</param>
        /// <returns>p7b data</returns>
        private static byte[] Create(X509Certificate2Collection certs, string metadata = null)
        {
            SignedCms cms = new SignedCms();
            return certs.Export(X509ContentType.Pkcs7);
        }

        /// <summary>
        /// Export the given certificate collection as a SIGNED bundle 
        /// </summary>
        /// <param name="certs">Certificates to place in the bundle</param>
        /// <param name="signingCert">Signing certificate</param>
        /// <param name="metadata">Optional metadata</param>
        /// <returns>p7s data</returns>
        private static byte[] CreateSigned(X509Certificate2Collection certs, X509Certificate2 signingCert, string metadata = null)
        {
            if (signingCert == null || !signingCert.HasPrivateKey)
            {
                throw new ArgumentException("signingCert");
            }

            byte[] p7bData = certs.Export(X509ContentType.Pkcs7);

            SignedCms cms = new SignedCms(new ContentInfo(p7bData), false);
            CmsSigner signer = new CmsSigner(signingCert);
            signer.IncludeOption = X509IncludeOption.EndCertOnly;
            cms.ComputeSignature(signer, true);

            return cms.Encode();
        }


        private string GetValidMetaData(string metadata)
        {
            if (string.IsNullOrEmpty(metadata))
            {
                return null;
            }
            XmlSchemaSet schemas = new XmlSchemaSet();
            schemas.Add("", "TrustBundleMetadata.xsd");

            XDocument metaDoc = XDocument.Parse(metadata);
            bool errors = false;
            metaDoc.Validate(schemas, (o, e) =>
            {
                throw new XmlSchemaValidationException("Invalid metadata", e.Exception, e.Exception.LineNumber,
                                                       e.Exception.LinePosition);
            });

            return metadata;
        }


        private static bool SkipResource(IEnumerable<string> ignore, string file)
        {
            if (ignore == null)
            {
                return false;
            }

            bool flag = false;
            foreach (string ignoreString in ignore)
            {
                if (file.IndexOf(ignoreString, StringComparison.OrdinalIgnoreCase) > 0)
                {
                    flag = true;
                }
            }
            return flag;
        }

        private void StoreBundle(string path, byte[] data)
        {
            File.WriteAllBytes(path, data);
        }
    }
}
