using System;
using System.Collections.Generic;
using System.Linq;
using System.IO;
using Health.Direct.Common.Certificates;
using Health.Direct.Common.DnsResolver;
using Health.Direct.Config.Store;

using Xunit;

namespace Health.Direct.DnsResponder.Tests
{
    class TestBase
    {

        ///TODO: incorporate  [xUnit.Extensions.AutoRollback] for autorollback
        ///TODO: incorporate sample for executing database population one time...
        

        protected const string ConnectionString = @"Data Source=.\SQLEXPRESS;Initial Catalog=DirectConfig;Integrated Security=SSPI;";
        //protected const string ConnectionString = "Data Source=localhost;Initial Catalog=DirectConfig;Integrated Security=SSPI;Persist Security Info=True;User ID=nhindUser;Password=nhindUser!10";

        private readonly static string DnsRecordsPath = Environment.CurrentDirectory + "\\metadata\\DnsRecords";
        private readonly static string CertRecordsPath = Environment.CurrentDirectory + "\\metadata\\certs";

        // if true dump will be sent to the delegate specified by DumpLine
        private readonly bool m_dumpEnabled;

        // the lines used to separate error and success respectively
        private const int PreambleWidth = 50;
        private static readonly string ErrorLinePreamble = new string('!', PreambleWidth);
        private static readonly string SuccessLinePreamble = new string('-', PreambleWidth);


        /// <summary>
        /// Default ctor. Will log to <see cref="Console.Out"/>.
        /// </summary>
        protected TestBase() :this(true)
        {
        }

        /// <summary>
        /// Logs to <see cref="Console.Out"/> if <paramref name="dumpEnabled"/> is <c>true</c>.
        /// </summary>
        /// <param name="dumpEnabled"><c>true</c> if dump output will be display</param>
        protected TestBase(bool dumpEnabled)
        {
            m_dumpEnabled = dumpEnabled;
        }

        /// <summary>
        /// Dump the <paramref name="msg"/> to the output with a preamble line of '!'s
        /// </summary>
        /// <remarks>
        /// Will not dump to output if the dump was disable with the ctor set false.
        /// </remarks>
        /// <param name="msg">The message to dump</param>
        protected void DumpError(string msg)
        {
            if (!m_dumpEnabled) return;

            DumpLine(ErrorLinePreamble);
            Dump(msg);
        }

        /// <summary>
        /// Dump the <paramref name="msg"/> to the output with a preamble line of '-'s
        /// </summary>
        /// <remarks>
        /// Will not dump to output if the dump was disable with the ctor set false.
        /// </remarks>
        /// <param name="msg">The message to dump</param>
        protected void DumpSuccess(string msg)
        {
            if (!m_dumpEnabled) return;

            DumpLine(SuccessLinePreamble);
            Dump(msg);
        }

        /// <summary>
        /// Dump the message created by calling <see cref="string.Format(string,object[])"/>
        /// with <paramref name="format"/> and <paramref name="args"/>. A preamble of '-'s will
        /// begin the message.
        /// </summary>
        /// <param name="format">The format of the message</param>
        /// <param name="args">The values to format</param>
        protected void DumpSuccess(string format, params object[] args)
        {
            DumpSuccess(string.Format(format, args));
        }

        /// <summary>
        /// Dump the <paramref name="msg"/> to the output. The output will include the timestamp in UTC.
        /// </summary>
        /// <remarks>
        /// Will not dump to output if the dump was disable with the ctor set false.
        /// </remarks>
        /// <param name="msg">The message to dump</param>
        protected void Dump(string msg)
        {
            if (!m_dumpEnabled) return;

            DumpLine(string.Format("{0:mm:ss.ff} - {1}", DateTime.UtcNow, msg));
        }

        /// <summary>
        /// Dump the message created by calling <see cref="string.Format(string,object[])"/> 
        /// with <paramref name="format"/> and <paramref name="args"/>.
        /// </summary>
        /// <param name="format">The format of the message</param>
        /// <param name="args">The values to format</param>
        protected void Dump(string format, params object[] args)
        {
            Dump(string.Format(format, args));
        }
        
        /// <summary>
        /// dumps out message to the console
        /// </summary>
        /// <param name="msg"></param>
        private static void DumpLine(string msg)
        {
            Console.Out.WriteLine(msg);
        }

        /// <summary>
        /// Gets test dns record domain names that sync up with 
        /// generated metadata dns response binary files in the 
        /// related dns responses folder        
        /// </summary>
        /// <remarks>
        /// Note that for each value listed below, there should be 
        /// a CERT, MX, SOA and A record bin file
        /// </remarks>
        protected static IEnumerable<string> DnsRecordDomainNames
        {
            get
            {
                yield return "microsoft.com";
                yield return "yahoo.com";
                yield return "google.com";
                yield return "apple.com";
                yield return "bing.com";
                yield return "epic.com";
                yield return "cerner.com";
                yield return "nhindirect.org";
                yield return "ibm.com";
            }
        }

        /// <summary>
        /// Gets test cert file names that sync up with 
        /// generated metadata cert binary files in the 
        /// related cert folder        
        /// </summary>
        protected static IEnumerable<string> CertFiles
        {
            get
            {
                //----------------------------------------------------------------------------------------------------
                //---get the file names in the certs folder path
                foreach (string name in System.IO.Directory.GetFiles(CertRecordsPath))
                {
                    yield return name.ToLower();
                }
            }
        }


        /// <summary>
        /// Gets test cert file names that sync up with 
        /// generated metadata cert binary files in the 
        /// related cert folder        
        /// </summary>
        protected static IEnumerable<string> CertOwners
        {
            get
            {
                //----------------------------------------------------------------------------------------------------
                //---get the file names in the certs folder path
                foreach (string name in System.IO.Directory.GetFiles(CertRecordsPath))
                {
                    yield return name.ToLower().Substring(0,name.LastIndexOf(".com") + 4).Replace(CertRecordsPath.ToLower(),"").Replace(@"\","");
                }
            }
        }


        /// <summary>
        /// Gets test dns record types that will be stored in the config store db      
        /// </summary>
        /// <remarks>
        /// Note that only types that are currently supported should be included here
        /// </remarks>
        protected static IEnumerable<DnsStandard.RecordType> DnsRecordTypes
        {
            get
            {
                yield return DnsStandard.RecordType.MX;
                yield return DnsStandard.RecordType.SOA;
                yield return DnsStandard.RecordType.ANAME;
            }
        }


        /// <summary>
        /// this method populates the DnsRecords table with viable DnsRecords stored in metadata
        /// </summary>
        /// <remarks>
        /// Related domains are pulled from the DnsRecodDomainNames per each possible DnsRecordType.
        /// These can be generated using the common.tests.DnsResponseToBinExample.cs test class, however
        /// they must be copied to the metadata\dns responses folder in this project and marked as copy to output
        /// directory, if newer
        /// </remarks>
        protected void InitDnsRecords()
        {
            List<string> domains = DnsRecordDomainNames.ToList<string>();
            List<DnsStandard.RecordType> recTypes = DnsRecordTypes.ToList<DnsStandard.RecordType>();

            DnsRecordManager mgr = new DnsRecordManager(new ConfigStore(ConnectionString));
            mgr.RemoveAll();

            //----------------------------------------------------------------------------------------------------
            //---go through all domains and load up the corresponding record types
            foreach (string domainName in domains)
            {
                mgr.Add(new DnsRecord(domainName
                    , (int)DnsStandard.RecordType.MX
                    , LoadAndVerifyDnsRecordFromBin<MXRecord>(Path.Combine(DnsRecordsPath
                        , string.Format("mx.{0}.bin", domainName)))
                    , string.Format("some test notes for mx domain{0}", domainName)));

                mgr.Add(new DnsRecord(domainName
                    , (int)DnsStandard.RecordType.SOA
                    , LoadAndVerifyDnsRecordFromBin<SOARecord>(Path.Combine(DnsRecordsPath
                        , string.Format("soa.{0}.bin", domainName)))
                    , string.Format("some test notes for soa domain{0}", domainName)));

                mgr.Add(new DnsRecord(domainName
                    , (int)DnsStandard.RecordType.ANAME
                    , LoadAndVerifyDnsRecordFromBin<AddressRecord>(Path.Combine(DnsRecordsPath
                        , string.Format("aname.{0}.bin", domainName)))
                    , string.Format("some test notes for aname domain{0}", domainName)));

            }
        }

        /// <summary>
        /// loads and verifies the dnsrecords from the bin associated bin files, ensuring that the types
        /// match up
        /// </summary>
        /// <typeparam name="T">Type of record that is expected</typeparam>
        /// <param name="path">path to the bin file to be loaded</param>
        /// <returns>bytes from the bin file</returns>
        protected byte[] LoadAndVerifyDnsRecordFromBin<T>(string path)
        {
            byte[] bytes = null;

            //----------------------------------------------------------------------------------------------------
            //---read the stream from the bytes
            using (FileStream fs = new FileStream(path, FileMode.Open, FileAccess.Read))
            {
                //Console.WriteLine("checking [{0}]", path);
                bytes = new BinaryReader(fs).ReadBytes((int)new FileInfo(path).Length);
                DnsBufferReader rdr = new DnsBufferReader(bytes, 0, bytes.Length);
                DnsResourceRecord rec = DnsResourceRecord.Deserialize(ref rdr);
                Assert.Equal(rec.GetType(), typeof(T));

            }
            return bytes;
        }


        /// <summary>
        /// will populate database with cert records from metadata project; cleans db prior to ensure fresh results
        /// </summary>
        protected void InitCertRecords()
        {
            List<string> certs = CertFiles.ToList<string>();
            CertificateManager mgr = new CertificateManager(new ConfigStore(ConnectionString));
            mgr.RemoveAll();
            foreach (string s in certs)
            {
                mgr.Add(LoadAndVerifyCertFromFile(s));
            }
        }

        /// <summary>
        /// loads and verifies the cert records from the files, ensuring that the types
        /// match up
        /// </summary>
        /// <typeparam name="T">Type of record that is expected</typeparam>
        /// <param name="path">path to the bin file to be loaded</param>
        /// <returns>bytes from the bin file</returns>
        protected Certificate LoadAndVerifyCertFromFile(string path)
        {
            byte[] bytes = null;

            //----------------------------------------------------------------------------------------------------
            //---read the stream from the bytes
            using (FileStream fs = new FileStream(path, FileMode.Open, FileAccess.Read))
            {
                //Console.WriteLine("checking [{0}]", path);
                bytes = new BinaryReader(fs).ReadBytes((int)new FileInfo(path).Length);
                
                using(DisposableX509Certificate2 x509 = new DisposableX509Certificate2(bytes))
                {
                    Certificate cert = new Certificate(x509.FriendlyName, x509, false);
                    return cert;
                }
            }
        }
    }
}
