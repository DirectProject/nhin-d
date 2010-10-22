using System.Collections.Generic;
using System.Net;
using DnsResolver;
using System.IO;

using Xunit;
using Xunit.Extensions;

namespace DnsResolverTests
{
    public class TestSerialization
    {
        public static IEnumerable<object[]> AddressData
        {
            get
            {
                yield return new[] { "foo", "65.55.12.249" };
                yield return new[] { "www.microsoft.com", "65.55.12.249" };
                yield return new[] {"www.bing.com", "67.148.71.19"};
            }
        }

        public static IEnumerable<object[]> MxData
        {
            get
            {
                yield return new[] { "www.microsoft.com", "www.microsoft.com" };
                yield return new[] { "www.hotmail.com", "www.hotmail.com" };
            }
        }

        public static IEnumerable<object[]> CertData
        {
            get
            {
                yield return new[] { "redmond.cer" };
                yield return new[] { "umesh.cer" };
            }
        }
            
        [Theory]
        [PropertyData("AddressData")]
        public void TestA(string name, string ip)
        {
            IPAddress original = IPAddress.Parse(ip);            
            AddressRecord record = new AddressRecord(name, original.ToIPV4());
            Assert.True(record.IPAddress.Equals(original));
            
            AddressRecord parsedRecord = this.Roundtrip<AddressRecord>(record);            
            Assert.True(parsedRecord.Equals(record));
        }

        [Theory]
        [PropertyData("MxData")]
        public void TestMX(string name, string exchange)
        {
            MXRecord record = new MXRecord(name, exchange);
            MXRecord parsedRecord = this.Roundtrip<MXRecord>(record);
            Assert.True(parsedRecord.Equals(record));
        }

        [Theory]
        [PropertyData("CertData")]
        public void TestCert(string name)
        {
            CertRecord record = new CertRecord(this.LoadCert(name));
            CertRecord parsedRecord = this.Roundtrip<CertRecord>(record);
            Assert.True(parsedRecord.Equals(record));
        }
        
        T Roundtrip<T>(T record)
            where T : DnsResourceRecord
        {
            DnsBuffer buffer = new DnsBuffer(1024);
            record.Serialize(buffer);

            DnsBufferReader reader = buffer.CreateReader();
            return (T) DnsResourceRecord.Deserialize(ref reader);        
        }
                        
        DnsX509Cert LoadCert(string name)
        {
            string path = Path.Combine(Directory.GetCurrentDirectory(), Path.Combine("DnsResolver", Path.Combine("DnsTestCerts", name)));
            return new DnsX509Cert(File.ReadAllBytes(path));
        }
    }
}
