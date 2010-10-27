using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.IO;
using System.Security.Cryptography.X509Certificates;
using Health.Direct.Agent;
using Health.Direct.Common.Certificates;
using Health.Direct.Config.Tools.Command;

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
        
        [Command(Name="Cert_Dump")]
        public void Dump(string[] args)
        {
            string path = args.GetRequiredValue(0);
            
            X509Certificate2 cert = new X509Certificate2(path);
            X509ExtensionCollection extensions = cert.Extensions;
            
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
