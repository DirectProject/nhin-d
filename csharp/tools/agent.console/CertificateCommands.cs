using System;
using System.Collections.Generic;
using System.IO;
using System.Security.Cryptography.X509Certificates;
using Health.Direct.Agent;
using Health.Direct.Common.Certificates;
using Health.Direct.Config.Tools;
using Health.Direct.Config.Tools.Command;
using Health.Direct.Common.DnsResolver;

namespace Health.Direct.Tools.Agent
{
    public class CertificateCommands
    {
        [Command(Name = "Cert_Verify")]
        public void Verify(string[] args)
        {
            string path = args.GetRequiredValue(0);
            X509Certificate2 cert = new X509Certificate2(path);
            X509Certificate2Collection anchors = SystemX509Store.OpenAnchor().GetAllCertificates();
            TrustChainValidator validator = new TrustChainValidator();
            
            validator.IsTrustedCertificate(cert, anchors);
        }

        [Command(Name = "Cert_ExportDns", Usage = ExportDnsUsage)]
        public void Export(string[] args)
        {
            IOFiles ioFiles = new IOFiles(args);
            if (!ioFiles.HasOutputFile)
            {
                Console.WriteLine("Need output file");
            }
            X509Certificate2 cert = new X509Certificate2(ioFiles.InputFile);
            this.ExportCert(cert, ioFiles.OutputFile);
        }

        private const string ExportDnsUsage =
            "Export the given certificate file to Zone file format"
            + Constants.CRLF + "    inputFile"
            + Constants.CRLF + "    outputFile";
        
        //---------------------------------------
        //
        // Implementation...
        //
        //---------------------------------------
        void ExportCert(X509Certificate2 cert, string filepath)
        {
            X509Certificate2Collection certs = new X509Certificate2Collection(cert);
            this.ExportCerts(certs.Enumerate(), filepath);
        }

        void ExportCerts(IEnumerable<X509Certificate2> certs, string filePath)
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

        void ExportCerts(IEnumerable<X509Certificate2> certs, TextWriter writer, bool isOutputFile)
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

        const string CertDumpUsage = "Print out Certificate details"
                                    + Constants.CRLF + "     Path to a .cer file";
        
        [Command(Name="Cert_Dump", Usage=CertDumpUsage)]
        public void Dump(string[] args)
        {
            string path = args.GetRequiredValue(0);
            
            X509Certificate2 cert = new X509Certificate2(path);
            X509ExtensionCollection extensions = cert.Extensions;
            
            Console.WriteLine(cert.ExtractEmailNameOrName());
            Console.WriteLine("Is CA={0}", cert.IsCertificateAuthority());
            foreach(X509Extension extension in extensions)
            {
                this.WriteExtension(extension);
            }
        }
        
        void WriteExtension(X509Extension extension)
        {
            string oid = extension.Oid.Value;
            switch(oid)
            {
                default:
                    Console.WriteLine(oid);                    
                    break;

                case "2.5.29.19":
                    Console.WriteLine("Basic Constraints");
                    break;

                case "2.5.29.35":
                case "2.5.29.1":
                    Console.WriteLine("Authority Key Identifier");
                    break;
                
                case "2.5.29.31":
                    Console.WriteLine("CRL Distribution Points");
                    break;
                    
                case "2.5.29.32":
                    Console.WriteLine("Certificate Policies");
                    break;
                
                case "2.5.29.15":
                    Console.WriteLine("Key usage");
                    break;
                
                case "2.5.29.46":
                    Console.WriteLine("Enhanced Key usage");
                    break;
                
                case "2.5.29.8":
                    Console.WriteLine("Issuer Alternative Name");
                    break;
            }
        }
    }
}
