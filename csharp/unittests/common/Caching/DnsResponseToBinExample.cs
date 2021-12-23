/* 
 Copyright (c) 2010, Direct Project
 All rights reserved.

 Authors:
    Chris Lomonico chris.lomonico@surescripts.com
    Joe Shook     Joseph.Shook@Surescripts.com

Redistribution and use in source and binary forms, with or without modification, are permitted provided that the following conditions are met:

Redistributions of source code must retain the above copyright notice, this list of conditions and the following disclaimer.
Redistributions in binary form must reproduce the above copyright notice, this list of conditions and the following disclaimer in the documentation and/or other materials provided with the distribution.
Neither the name of The Direct Project (directproject.org) nor the names of its contributors may be used to endorse or promote products derived from this software without specific prior written permission.
THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
*/

using System;
using System.IO;
using System.Security.Cryptography;
using System.Security.Cryptography.X509Certificates;
using Health.Direct.Common.DnsResolver;
using Xunit;
using Xunit.Abstractions;

namespace Health.Direct.Common.Tests.Caching
{
    public class DnsResponseToBinExample
    {
        private readonly ITestOutputHelper _testOutputHelper;
        private readonly DnsClient _client;
        private const string DNSRECORDSEPATH = @"DnsRecords";
        private const string DnsResponsePath = @"dnsresponses";

        const string PublicDns = "8.8.8.8";         // Google

        public class CertData
        {
            public string Key { get; set; }

            public string FriendlyName { get; set; }

            public string DistinguishedName { get; set; }

            public string Password { get; set; }

            public CertData(string key
                , string friendlyName
                , string distinguishedName
                , string password)
            {
                Key = key;
                FriendlyName = friendlyName;
                DistinguishedName = distinguishedName;
                Password = password;
            }
        }

        public DnsResponseToBinExample(ITestOutputHelper testOutputHelper)
        {
            _testOutputHelper = testOutputHelper;

            _client = new DnsClient(PublicDns) { Timeout = TimeSpan.FromSeconds(10) };
        }

        //---these "tests" are purely examples of how to create mock responses, they are skipped out as they alter the file system 
        //---(see the paths above).  Alter and use as needed
        [Theory(Skip = "Alters file system")]
        [InlineData("www.microsoft.com")]
        [InlineData("www.yahoo.com")]
        [InlineData("www.google.com")]
        [InlineData("www.apple.com")]
        [InlineData("www.bing.com")]
        [InlineData("www.nhindirect.org")]
        [InlineData("www.epic.com")]
        [InlineData("www.cerner.com")]
        [InlineData("www.ibm.com")]
        public void CreateAResponseDumps(string domain)
        {
            var buff = new DnsBuffer(DnsStandard.MaxUdpMessageLength * 2);
            _client.Resolve(DnsRequest.CreateA(domain)).Serialize(buff);
            byte[] bytes = buff.CreateReader().ReadBytes();
            string path = Path.Combine(DnsResponsePath, string.Format("aname.{0}.bin", domain)).Replace("www.", "");
            _testOutputHelper.WriteLine("Creating {0}", path);
            using var s = new FileStream(path, FileMode.OpenOrCreate);
            s.Write(bytes, 0, bytes.Length);
            s.Close();
        }

        [Theory(Skip = "Alters file system")]
        [InlineData("www.microsoft.com")]
        [InlineData("www.yahoo.com")]
        [InlineData("www.google.com")]
        [InlineData("www.apple.com")]
        [InlineData("www.bing.com")]
        [InlineData("www.nhindirect.org")]
        [InlineData("www.epic.com")]
        [InlineData("www.cerner.com")]
        [InlineData("www.ibm.com")]
        public void CreateSoaResponseDumps(string domain)
        {
            var buff = new DnsBuffer(DnsStandard.MaxUdpMessageLength * 2);
            _client.Resolve(DnsRequest.CreateSOA(domain)).Serialize(buff);
            byte[] bytes = buff.CreateReader().ReadBytes();
            string path = Path.Combine(DnsResponsePath, string.Format("soa.{0}.bin", domain)).Replace("www.", "");
            _testOutputHelper.WriteLine("Creating {0}", path);

            using var s = new FileStream(path, FileMode.OpenOrCreate);
            s.Write(bytes, 0, bytes.Length);
            s.Close();
        }

        [Theory(Skip = "Alters file system")]
        [InlineData("www.microsoft.com")]
        [InlineData("www.yahoo.com")]
        [InlineData("www.google.com")]
        [InlineData("www.apple.com")]
        [InlineData("www.bing.com")]
        [InlineData("www.nhindirect.org")]
        [InlineData("www.epic.com")]
        [InlineData("www.cerner.com")]
        [InlineData("www.ibm.com")]
        public void CreateMxResponseDumps(string domain)
        {
            var buff = new DnsBuffer(DnsStandard.MaxUdpMessageLength * 2);
            _client.Resolve(DnsRequest.CreateMX(domain)).Serialize(buff);
            byte[] bytes = buff.CreateReader().ReadBytes();
            string path = Path.Combine(DnsResponsePath, $"mx.{domain}.bin").Replace("www.", "");
            _testOutputHelper.WriteLine("Creating {0}", path);

            using var s = new FileStream(path, FileMode.OpenOrCreate);
            s.Write(bytes, 0, bytes.Length);
            s.Close();
        }

        [Theory(Skip = "Alters file system")]
        [InlineData("www.microsoft.com")]
        [InlineData("www.yahoo.com")]
        [InlineData("www.google.com")]
        [InlineData("www.apple.com")]
        [InlineData("www.bing.com")]
        [InlineData("www.nhindirect.org")]
        [InlineData("www.epic.com")]
        [InlineData("www.cerner.com")]
        [InlineData("www.ibm.com")]
        public void CreateCertResponseDumps(string domain)
        {
            var buff = new DnsBuffer(DnsStandard.MaxUdpMessageLength * 2);
            _client.Resolve(DnsRequest.CreateCERT(domain)).Serialize(buff);
            byte[] bytes = buff.CreateReader().ReadBytes();
            string path = Path.Combine(DnsResponsePath, $"cert.{domain}.bin").Replace("www.", "");
            _testOutputHelper.WriteLine("Creating {0}", path);

            using (var s = new FileStream(path, FileMode.OpenOrCreate))
            {
                s.Write(bytes
                        , 0
                        , bytes.Length);
                s.Close();
            }
        }

        [Theory]
        [InlineData("microsoft.com")]
        [InlineData("yahoo.com")]
        [InlineData("google.com")]
        [InlineData("apple.com")]
        [InlineData("bing.com")]
        [InlineData("nhindirect.org")]
        [InlineData("epic.com")]
        [InlineData("cerner.com")]
        [InlineData("ibm.com")]
        public void CreateDnsResourceRecords(string domain)
        {
            var buff = new DnsBuffer();
            byte[] bytes;
            var arec = new AddressRecord(domain
                , "127.0.0.1")
            { TTL = 1000 };
            arec.Serialize(buff);

            string path = Path.Combine(DNSRECORDSEPATH, $"aname.{domain}.bin");
            _testOutputHelper.WriteLine("Creating {0}", path);

            using (var s = new FileStream(path, FileMode.OpenOrCreate))
            {
                s.Write(buff.Buffer
                        , 0
                        , buff.Buffer.Length);
                s.Close();
            }


            //----------------------------------------------------------------------------------------------------
            //---read the stream from the bytes
            using (var fs = new FileStream(path, FileMode.Open, FileAccess.Read))
            {
                _testOutputHelper.WriteLine("checking [{0}]", path);
                bytes = new BinaryReader(fs).ReadBytes((int)new FileInfo(path).Length);
                DnsBufferReader rdr = new DnsBufferReader(bytes, 0, bytes.Length);
                arec = (AddressRecord)DnsResourceRecord.Deserialize(ref rdr);
            }
            _testOutputHelper.WriteLine(arec.IPAddress.ToString());
            _testOutputHelper.WriteLine(arec.TTL.ToString());
            _testOutputHelper.WriteLine(arec.Name);
            //----------------------------------------------------------------------------------------------------------------
            var soa = new SOARecord(domain
                , domain + ".dom"
                , "somebody"
                , 1
                , 2
                , 3
                , 4
                , 5)
            { TTL = 2000 };
            buff = new DnsBuffer();
            soa.Serialize(buff);

            path = Path.Combine(DNSRECORDSEPATH, $"soa.{domain}.bin");
            _testOutputHelper.WriteLine("Creating {0}", path);

            using (FileStream s = new FileStream(path, FileMode.OpenOrCreate))
            {
                s.Write(buff.Buffer
                        , 0
                        , buff.Buffer.Length);
                s.Close();
            }

            //----------------------------------------------------------------------------------------------------
            //---read the stream from the bytes
            using (FileStream fs = new FileStream(path, FileMode.Open, FileAccess.Read))
            {
                _testOutputHelper.WriteLine("checking [{0}]", path);
                bytes = new BinaryReader(fs).ReadBytes((int)new FileInfo(path).Length);
                var rdr = new DnsBufferReader(bytes, 0, bytes.Length);
                soa = (SOARecord)DnsResourceRecord.Deserialize(ref rdr);
            }
            _testOutputHelper.WriteLine(soa.ResponsibleName);
            _testOutputHelper.WriteLine(soa.SerialNumber.ToString());
            _testOutputHelper.WriteLine(soa.Retry.ToString());
            _testOutputHelper.WriteLine(soa.Refresh.ToString());
            _testOutputHelper.WriteLine(soa.Expire.ToString());
            _testOutputHelper.WriteLine(soa.Minimum.ToString());
            _testOutputHelper.WriteLine(soa.TTL.ToString());
            _testOutputHelper.WriteLine(soa.Name);
            //----------------------------------------------------------------------------------------------------------------
            var mx = new MXRecord(domain, $"mx.{domain}", 1)
            { TTL = 2000 };

            buff = new DnsBuffer();
            mx.Serialize(buff);

            path = Path.Combine(DNSRECORDSEPATH, $"mx.{domain}.bin");
            _testOutputHelper.WriteLine("Creating {0}", path);

            using (var s = new FileStream(path, FileMode.OpenOrCreate))
            {
                s.Write(buff.Buffer
                        , 0
                        , buff.Buffer.Length);
                s.Close();
            }

            //----------------------------------------------------------------------------------------------------
            //---read the stream from the bytes
            using (var fs = new FileStream(path, FileMode.Open, FileAccess.Read))
            {
                _testOutputHelper.WriteLine("checking [{0}]", path);
                bytes = new BinaryReader(fs).ReadBytes((int)new FileInfo(path).Length);
                DnsBufferReader rdr = new DnsBufferReader(bytes, 0, bytes.Length);
                mx = (MXRecord)DnsResourceRecord.Deserialize(ref rdr);
            }
            _testOutputHelper.WriteLine(mx.Exchange);
            _testOutputHelper.WriteLine(mx.Name);
            _testOutputHelper.WriteLine(mx.Preference.ToString());

            //----------------------------------------------------------------------------------------------------------------
            //---create the cert on the fly
            var cert = new CertRecord(new DnsX509Cert(CreateNamedKeyCertificate(new CertData(domain
                , domain
                , $"CN={domain}"
                , ""))))
            { TTL = 2000 };

            buff = new DnsBuffer();
            cert.Serialize(buff);

            path = Path.Combine(DNSRECORDSEPATH, $"cert.{domain}.bin");
            _testOutputHelper.WriteLine("Creating {0}", path);

            using (FileStream s = new FileStream(path, FileMode.OpenOrCreate))
            {
                s.Write(buff.Buffer
                        , 0
                        , buff.Buffer.Length);
                s.Close();
            }

            //----------------------------------------------------------------------------------------------------
            //---read the stream from the bytes
            using (var fs = new FileStream(path, FileMode.Open, FileAccess.Read))
            {
                _testOutputHelper.WriteLine("checking [{0}]", path);
                bytes = new BinaryReader(fs).ReadBytes((int)new FileInfo(path).Length);
                DnsBufferReader rdr = new DnsBufferReader(bytes, 0, bytes.Length);
                cert = (CertRecord)DnsResourceRecord.Deserialize(ref rdr);
            }
            _testOutputHelper.WriteLine(cert.Name);
            _testOutputHelper.WriteLine(cert.Cert.Certificate.NotBefore.ToString());
            _testOutputHelper.WriteLine(cert.Cert.Certificate.NotAfter.ToString());
        }

        public X509Certificate2  CreateNamedKeyCertificate(CertData data)
        {
            var sanBuilder = new SubjectAlternativeNameBuilder();
            sanBuilder.AddDnsName(data.Key);

            using (var rsa = RSA.Create(2048))
            {

                var certRequest = new CertificateRequest(
                    data.DistinguishedName,
                    rsa,
                    HashAlgorithmName.SHA256,
                    RSASignaturePadding.Pkcs1);

                // No CA
                certRequest.CertificateExtensions.Add(
                    new X509BasicConstraintsExtension(
                        false,
                        false, 
                        0, 
                        false));
                
                certRequest.CertificateExtensions.Add(
                    new X509KeyUsageExtension(
                        X509KeyUsageFlags.DigitalSignature | X509KeyUsageFlags.KeyEncipherment,
                        true));

                // Add the SubjectAlternativeName extension. New in .net 4.7.2 and core
                certRequest.CertificateExtensions.Add(sanBuilder.Build());

                var now = DateTimeOffset.UtcNow;
                var cert = certRequest.CreateSelfSigned(now, now.AddDays(365));

                Assert.True(cert.HasPrivateKey);

                using (var certKey = cert.GetRSAPrivateKey())
                {
                    Assert.Equal("RSA", certKey?.SignatureAlgorithm);
                }

                return cert;
            }
        }
    }
}