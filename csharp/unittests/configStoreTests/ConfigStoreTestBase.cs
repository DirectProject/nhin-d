/* 
 Copyright (c) 2010, Direct Project
 All rights reserved.

 Authors:
    Chris Lomonico  chris.lomonico@surescripts.com
  
Redistribution and use in source and binary forms, with or without modification, are permitted provided that the following conditions are met:

Redistributions of source code must retain the above copyright notice, this list of conditions and the following disclaimer.
Redistributions in binary form must reproduce the above copyright notice, this list of conditions and the following disclaimer in the documentation and/or other materials provided with the distribution.
Neither the name of The Direct Project (directproject.org) nor the names of its contributors may be used to endorse or promote products derived from this software without specific prior written permission.
THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 
*/
using System;
using System.Collections.Generic;
using System.Linq;
using System.IO;

using Xunit;
using Health.Direct.Common.DnsResolver;

namespace Health.Direct.Config.Store.Tests
{
    public class ConfigStoreTestBase 
    {

        protected const string CONNSTR = @"Data Source=.\SQLEXPRESS;Initial Catalog=DirectConfig;Integrated Security=SSPI;";
        //protected const string CONNSTR = "Data Source=localhost;Initial Catalog=NHINDConfig;Integrated Security=SSPI;Persist Security Info=True;User ID=nhindUser;Password=nhindUser!10";
        protected const int MAXDOMAINCOUNT = 10; //---number should be <= .cer file count in metadata folder
        protected const int MAXSMTPCOUNT = 3;
        protected const int MAXADDRESSCOUNT = 3;
        protected const int MAXCERTPEROWNER = 3;  //---number cannot be greater than the certs per domain in metadata\cert folder (see pattern domain[x].test.com.[y] where min(max(y)) = this number)

        protected const int PREFERENCE = 2;
        protected const long STARTID = 1;
        private const string DOMAINNAMEPATTERN = "domain{0}.test.com";
        private const string ADDRESSPATTERN = "test@address{0}.domain{1}.com";
        private const string SMTPDOMAINNAMEPATTERN = "smtp{0}.domain{1}.test.com";
        private const string ADDRESSDISPLAYNAMEPATTERN = "domain[{0}] add[{1}]";
#if DEBUG
        private static string DNSRECORDSEPATH = @"..\..\..\..\bin\debug\metadata\DnsRecords";
#else
        //---assume release...
        private static string DNSRECORDSEPATH = @"..\..\..\..\bin\release\metadata\DnsRecords";
#endif
#if DEBUG
        private static string CERTSRECORDSPATH = @"..\..\..\..\bin\debug\metadata\certs";
#else
        //---assume release...
        private static string CERTSRECORDSPATH = @"..\..\..\..\bin\release\metadata\certs";
#endif
        protected Dictionary<string, DnsResponse> m_DomainResponses = null;


        // if true dump will be sent to the delegate specified by DumpLine
        private readonly bool m_dumpEnabled;

        // the lines used to separate error and success respectively
        private const int PreambleWidth = 50;
        private static readonly string ErrorLinePreamble = new string('!', PreambleWidth);
        private static readonly string SuccessLinePreamble = new string('-', PreambleWidth);

        /// <summary>
        /// Default ctor. Will log to <see cref="Console.Out"/>.
        /// </summary>
        protected ConfigStoreTestBase() :this(true)
        {
        }

        /// <summary>
        /// Logs to <see cref="Console.Out"/> if <paramref name="dumpEnabled"/> is <c>true</c>.
        /// </summary>
        /// <param name="dumpEnabled"><c>true</c> if dump output will be display</param>
        protected ConfigStoreTestBase(bool dumpEnabled)
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
        /// gets clean, workable enumerator for the IEnumerable(object[]) utilized for property data on theory testing
        /// </summary>
        /// <typeparam name="T">Class type to be returned</typeparam>
        /// <param name="dirty">IEnumerable(object[]) to be cleaned and converted</param>
        /// <returns></returns>
        public List<T> GetCleanEnumerable<T>(IEnumerable<object[]> dirty) where T : class
        {
            return dirty.Select(a => a[0] as T).ToList();
        }

        /// <summary>
        /// property to expose enumerable testing Certificate instances
        /// </summary>
        /// <remarks>
        /// Relates to .cer files in the metadata/certs folder; all files should be copied to output dir if newer
        /// </remarks>
        public static IEnumerable<object[]> TestCertificates
        {
            get
            {
                for (int i = 1; i <= MAXDOMAINCOUNT; i++)
                {
                    for (int t = 1; t <= MAXCERTPEROWNER; t++)
                    {
                        yield return new[] { GetCertificateFromTestCertPfx(i, t) };
                    }
                }

            }
        }

        /// <summary>
        /// property to expose enumerable testing Anchor Certificate instances
        /// </summary>
        /// <remarks>
        /// Relates to .cer files in the metadata/certs folder; all files should be copied to output dir if newer
        /// </remarks>
        public static IEnumerable<object[]> TestAnchors
        {
            get
            {
                for (int i = 1; i <= MAXDOMAINCOUNT; i++)
                {
                    for (int t = 1; t <= MAXCERTPEROWNER; t++)
                    {
                        yield return new[] { GetAnchorFromTestCertPfx(i, t) };
                    }
                }

            }
        }

        /// <summary>
        /// property to expose enumerable test certs extracted from pfx files in metadata\certs folder
        /// </summary>
        /// <remarks>
        /// Relates to .cer files in the metadata/certs folder; all files should be copied to output dir if newer
        /// </remarks>
        public static IEnumerable<object[]> TestCerts
        {
            get
            {
                for (int i = 1; i <= MAXDOMAINCOUNT; i++)
                {
                    for (int t = 1; t <= MAXCERTPEROWNER; t++)
                    {
                        yield return new[] { GetTestCertFromPfx(i, t) };
                    }
                    
                }

            }
        }

        /// <summary>
        /// property to expose enumerable test cert bytpes extracted from pfx files in metadata\certs folder
        /// </summary>
        /// <remarks>
        /// Relates to .cer files in the metadata/certs folder; all files should be copied to output dir if newer
        /// </remarks>
        public static IEnumerable<object[]> TestCertsBytes
        {
            get
            {
                for (int i = 1; i <= MAXDOMAINCOUNT; i++)
                {
                    for (int t = 1; t <= MAXCERTPEROWNER; t++)
                    {
                        yield return new[] { GetCertBytesTestCertPfx(i, t) };
                    }
                }

            }
        }

        /// <summary>
        /// Gets the TestMXDomainNames of the MXManagerTests
        /// </summary>
        protected static IEnumerable<string> TestDomainNames
        {
            get
            {
                for (int t = 1; t <= MAXDOMAINCOUNT; t++)
                {
                    yield return  BuildDomainName(t);
                       

                }
            }
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
        /// Gets the TestMXDomainNames of the MXManagerTests
        /// </summary>
        protected static IEnumerable<KeyValuePair<long, KeyValuePair<int, string>>> TestMXDomainNames
        {
            get
            {
                for (int i = 1; i <= MAXDOMAINCOUNT; i++)
                {
                    for (int t = 1; t <= MAXSMTPCOUNT; t++)
                    {
                        //----------------------------------------------------------------------------------------------------
                        //---use i as the domain id, t as preference
                        yield return new KeyValuePair<long, KeyValuePair<int,string>>(i
                                                                                      , new KeyValuePair<int,string>(t, BuildSMTPDomainName(i,t)));
                    }
                }
                
            }
        }

        /// <summary>
        /// Gets the TestAddressNames of the MXManagerTests
        /// </summary>
        protected static IEnumerable<KeyValuePair<long, KeyValuePair<int, string>>> TestAddressNames
        {
            get
            {
                for (int i = 1; i <= MAXDOMAINCOUNT; i++)
                {
                    for (int t = 1; t <= MAXADDRESSCOUNT; t++)
                    {
                        //----------------------------------------------------------------------------------------------------
                        //---use i as the domain id, t as preference
                        yield return new KeyValuePair<long, KeyValuePair<int,string>>(i
                                                                                      , new KeyValuePair<int,string>(t, BuildEmailAddress(i,t)));
                    }
                }
                
            }
        }
        
        /// <summary>
        /// Simple method to return a list containing all MX domain names
        /// </summary>
        /// <returns>List of type string containing all domain names</returns>
        protected static List<string> AllMXDomainNames()
        {
            List<string> names = new List<string>(MAXDOMAINCOUNT * MAXSMTPCOUNT);
            for (int i = 1; i <= MAXDOMAINCOUNT; i++)
            {
                for (int t = 1; t <= MAXSMTPCOUNT; t++)
                {
                    names.Add(BuildSMTPDomainName(i, t));
                }
            }
            return names;
        }

        
        /// <summary>
        /// this method populates the DnsRecords table with viable DnsRecords stored in metadata
        /// </summary>
        /// <remarks>
        /// Note that the related domains are pulled from the DnsRecodDomainNames per each possible DnsRecordType
        /// These can be generated using the common.tests.DnsResponseToBinExample.cs test class, however
        /// they must be copied to the metadata\dns responses folder in this project and marked as copy to output
        /// directory, if newer
        /// </remarks>
        protected void InitDnsRecords()
        {
            List<string> domains = DnsRecordDomainNames.ToList<string>();
            List<DnsStandard.RecordType> recTypes = DnsRecordTypes.ToList<DnsStandard.RecordType>();

            DnsRecordManager mgr = new DnsRecordManager(new ConfigStore(CONNSTR));
            mgr.RemoveAll();

            //----------------------------------------------------------------------------------------------------
            //---go through all domains and load up the corresponding record types
            foreach (string domainName in domains)
            {
                mgr.Add(new DnsRecord(domainName
                    , (int)DnsStandard.RecordType.MX
                    , LoadAndVerifyDnsRecordFromBin<MXRecord>(Path.Combine(DNSRECORDSEPATH
                        , string.Format("mx.{0}.bin", domainName)))
                    , string.Format("some test notes for mx domain{0}", domainName)));

                mgr.Add(new DnsRecord(domainName
                    , (int)DnsStandard.RecordType.SOA
                    , LoadAndVerifyDnsRecordFromBin<SOARecord>(Path.Combine(DNSRECORDSEPATH
                        , string.Format("soa.{0}.bin", domainName)))
                    , string.Format("some test notes for soa domain{0}", domainName)));

                mgr.Add(new DnsRecord(domainName
                    , (int)DnsStandard.RecordType.ANAME
                    , LoadAndVerifyDnsRecordFromBin<AddressRecord>(Path.Combine(DNSRECORDSEPATH
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
        /// This method will clean, load and verify Domain records in the DB for testing purposes
        /// </summary>
        protected void InitDomainRecords()
        {
            this.InitDomainRecords(new DomainManager(new ConfigStore(CONNSTR))
                                   , new ConfigDatabase(CONNSTR));
        }

        /// <summary>
        /// This method will clean, load and verify Domain records in the DB for testing purposes
        /// </summary>
        /// <param name="mgr">DomainManager instance used for controlling the Domain records</param>
        /// <param name="db">ConfigDatabase instance used as the target storage mechanism for the records</param>
        /// <remarks>
        /// this approach goes out to db each time it is called, however it ensures that clean records
        /// are present for every test that is execute, if it is taking too long, simply cut down on the
        /// number of items using the consts above
        /// </remarks>
        protected void InitDomainRecords(DomainManager mgr
                                         , ConfigDatabase db)
        {
            //----------------------------------------------------------------------------------------------------
            //---clean all existing records
            mgr.RemoveAll();
            foreach (string val in TestDomainNames)
            {
                mgr.Add(db, new Domain(val));
            }

            //----------------------------------------------------------------------------------------------------
            //---submit changes to db and verify existence of records
            db.SubmitChanges();
            foreach (string val in TestDomainNames)
            {
                Assert.NotNull(mgr.Get(val));
            }

        }

        /// <summary>
        /// This method will clean, load and verify Certificate records based on the certs stored in the
        /// metadata\certs folder into the db for testing purposes
        /// </summary>
        protected void InitCertRecords()
        {
            this.InitCertRecords(new CertificateManager(new ConfigStore(CONNSTR))
                                 , new ConfigDatabase(CONNSTR));
        }

        /// <summary>
        /// This method will clean, load and verify Certificate records based on the certs stored in the
        /// metadata\certs folder into the db for testing purposes
        /// </summary>
        /// <param name="mxmgr">CertificateManager instance used for controlling the Certificate records</param>
        /// <param name="db">ConfigDatabase instance used as the target storage mechanism for the records</param>
        /// <remarks>
        /// this approach goes out to db each time it is called, however it ensures that clean records
        /// are present for every test that is execute, if it is taking too long, simply cut down on the
        /// number of items using the consts above
        /// </remarks>
        protected void InitCertRecords(CertificateManager mgr
                                       , ConfigDatabase db)
        {
            mgr.RemoveAll(db);
            for (int i = 1; i <= MAXDOMAINCOUNT; i++)
            {
                //----------------------------------------------------------------------------------------------------
                //---cheezy but will add MAXCERTPEROWNER certs per each relative domain
                for (int t = 1; t <= MAXCERTPEROWNER; t++)
                {
                    mgr.Add(GetCertificateFromTestCertPfx(i,t));
                }
            }
        }


        /// <summary>
        /// This method will clean, load and verify Anchor records based on the certs stored in the
        /// metadata\certs folder into the db for testing purposes
        /// </summary>
        protected void InitAnchorRecords()
        {
            this.InitAnchorRecords(new AnchorManager(new ConfigStore(CONNSTR))
                                   , new ConfigDatabase(CONNSTR));
        }

        /// <summary>
        /// This method will clean, load and verify Anchor records based on the certs stored in the
        /// metadata\certs folder into the db for testing purposes
        /// </summary>
        /// <param name="mxmgr">CertificateManager instance used for controlling the Certificate records</param>
        /// <param name="db">ConfigDatabase instance used as the target storage mechanism for the records</param>
        /// <remarks>
        /// this approach goes out to db each time it is called, however it ensures that clean records
        /// are present for every test that is execute, if it is taking too long, simply cut down on the
        /// number of items using the consts above
        /// </remarks>
        protected void InitAnchorRecords(AnchorManager mgr
                                         , ConfigDatabase db)
        {
            mgr.RemoveAll(db);
            for (int i = 1; i <= MAXDOMAINCOUNT; i++)
            {
                //----------------------------------------------------------------------------------------------------
                //---cheezy but will add MAXCERTPEROWNER certs per each relative domain
                for (int t = 1; t <= MAXCERTPEROWNER; t++)
                {
                    Anchor anc = GetAnchorFromTestCertPfx(i, t);
                    //----------------------------------------------------------------------------------------------------
                    //---cheezey but flags all as incoming and outgoing

                    anc.ForIncoming = true;
                    anc.ForOutgoing = true;
                    mgr.Add(GetAnchorFromTestCertPfx(i, t));
                
                }
            }
        }

        /// <summary>
        /// This method will clean, load and verify address records in the DB for testing purposes
        /// </summary>
        protected void InitAddressRecords()
        {
            this.InitAddressRecords(new AddressManager(new ConfigStore(CONNSTR))
                                    , new ConfigDatabase(CONNSTR));
        }

        /// <summary>
        /// This method will clean, load and verify address records in the DB for testing purposes
        /// </summary>
        /// <param name="mgr">AddressManager instance used for controlling the Address records</param>
        /// <param name="db">ConfigDatabase instance used as the target storage mechanism for the records</param>
        /// <remarks>
        /// this approach goes out to db each time it is called, however it ensures that clean records
        /// are present for every test that is execute, if it is taking too long, simply cut down on the
        /// number of items using the consts above
        /// </remarks>
        protected void InitAddressRecords(AddressManager mgr
                                          , ConfigDatabase db)
        {
            //----------------------------------------------------------------------------------------------------
            //---init domain records as well we want them fresh too
            InitDomainRecords(new DomainManager(new ConfigStore(CONNSTR)), db);
            foreach (KeyValuePair<long, KeyValuePair<int, string>> kp in TestAddressNames)
            {
                //----------------------------------------------------------------------------------------------------
                //---create new address entry with the domain id (kp.key) and the address 
                mgr.Add(db, new Address(kp.Key, kp.Value.Value, BuildEmailAddressDisplayName(kp.Key, kp.Value.Key)));
            }

            //----------------------------------------------------------------------------------------------------
            //---submit changes to db and verify existence of records
            db.SubmitChanges();
            foreach (KeyValuePair<long, KeyValuePair<int, string>> kp in TestAddressNames)
            {
                Assert.NotNull(mgr.Get(kp.Value.Value));
            }
        }


        /// <summary>
        ///  Simple method that yeilds a uniform means for setting up an address name
        /// </summary>
        /// <param name="domainID">domain id to be used to build the string</param>
        /// <param name="addressID">address id number used to build the string</param>
        /// <returns>a commonly formatted string based on the domainID and address domain id</returns>
        protected static String BuildEmailAddress(long domainID, int addressID)
        {
            return string.Format(ADDRESSPATTERN, addressID, domainID);
        }

        /// <summary>
        /// provides uniform manner for creating email address display names
        /// </summary>
        /// <param name="domainID">domain id used in address string</param>
        /// <param name="addressID">address id used in address string</param>
        /// <returns></returns>
        protected static String BuildEmailAddressDisplayName(long domainID, int addressID)
        {
            return string.Format(ADDRESSDISPLAYNAMEPATTERN, domainID, addressID);
        }

        /// <summary>
        ///  Simple method that yeilds a uniform means for setting up a specific SMTP domain name
        /// </summary>
        /// <param name="domainID">domain id to be used to build the string</param>
        /// <param name="SMTPDomainID">smtp id number used to build the string</param>
        /// <returns>a commonly formatted string based on the domainID and smtp domain id</returns>
        protected static String BuildSMTPDomainName(long domainID, int SMTPDomainID)
        {
            return string.Format(SMTPDOMAINNAMEPATTERN, SMTPDomainID, domainID);
        }

        /// <summary>
        /// Simple method that yeilds a uniform means for setting up a specific domain name
        /// </summary>
        /// <param name="domainID">domain id to be used to build the string</param>
        /// <returns>a commonly formatted string based on the domainID</returns>
        protected static String BuildDomainName(long domainID)
        {
            return string.Format(DOMAINNAMEPATTERN, domainID);
        }

        /// <summary>
        /// attempts to load up a Certificate instance from the metadata, testing pfx file associated with the domain
        /// </summary>
        /// <param name="domainID">long containing the id of the relative domain for which the pfx file is to be loaded</param>
        /// <returns>Certificate instance populated with data from the related pfx file</returns>
        protected static Anchor GetAnchorFromTestCertPfx(long domainID, int subId)
        {
            if (domainID > MAXDOMAINCOUNT || domainID <= 0)
            {
                throw new Exception(string.Format("Domain ID is out of range (1-{0}", MAXDOMAINCOUNT));
            }
            if (subId > MAXCERTPEROWNER || subId <= 0)
            {
                throw new Exception(string.Format("Cert sub ID is out of range (1-{0}", MAXDOMAINCOUNT));
            }
            string path = string.Format(@"{0}\domain{1}.test.com.{2}.pfx"
                , CERTSRECORDSPATH
                , domainID
                , subId);
            Anchor cert = null;
            using (FileStream fs = new FileStream(path, FileMode.Open, FileAccess.Read))
            {
                cert = new Anchor(string.Format("CN=domain{0}.test.com", domainID)
                                  , new BinaryReader(fs).ReadBytes((int)new FileInfo(path).Length)
                                  , String.Empty);
                //cert.Owner = string.Format("domain{0}.test.com", domainID);
                //cert.Data = new BinaryReader(fs).ReadBytes((int)new FileInfo(path).Length);
            }
            return cert;
        }

        /// <summary>
        /// attempts to load up a Certificate instance from the metadata, testing pfx file associated with the domain
        /// </summary>
        /// <param name="domainID">long containing the id of the relative domain for which the pfx file is to be loaded</param>
        /// <returns>Certificate instance populated with data from the related pfx file</returns>
        protected static Certificate GetCertificateFromTestCertPfx(long domainID, int subId)
        {
            if (domainID > MAXDOMAINCOUNT || domainID <= 0)
            {
                throw new Exception(string.Format("Domain ID is out of range (1-{0}", MAXDOMAINCOUNT));
            }
            if (subId > MAXCERTPEROWNER || subId <= 0)
            {
                throw new Exception(string.Format("Cert sub ID is out of range (1-{0}", MAXDOMAINCOUNT));
            }
            string path = string.Format(@"{0}\domain{1}.test.com.{2}.pfx"
                , CERTSRECORDSPATH
                , domainID
                ,subId);
            Certificate cert = null;
            using (FileStream fs = new FileStream(path, FileMode.Open, FileAccess.Read))
            {
                cert = new Certificate(string.Format("CN=domain{0}.test.com", domainID)
                                       , new BinaryReader(fs).ReadBytes((int)new FileInfo(path).Length)
                                       , String.Empty);
                //cert.Owner = string.Format("domain{0}.test.com", domainID);
                //cert.Data = new BinaryReader(fs).ReadBytes((int)new FileInfo(path).Length);
            }
            return cert;
        }

        /// <summary>
        /// attempts to load up a Certificate instance from the metadata, testing pfx file associated with the domain
        /// </summary>
        /// <param name="domainID">long containing the id of the relative domain for which the pfx file is to be loaded</param>
        /// <returns>Certificate instance populated with data from the related pfx file</returns>
        protected static System.Security.Cryptography.X509Certificates.X509Certificate2 GetTestCertFromPfx(long domainID, int subId)
        {
            if (domainID > MAXDOMAINCOUNT || domainID <= 0)
            {
                throw new Exception(string.Format("Domain ID is out of range (1-{0}", MAXDOMAINCOUNT));
            }
            if (subId > MAXCERTPEROWNER || subId <= 0)
            {
                throw new Exception(string.Format("Cert sub ID is out of range (1-{0}", MAXDOMAINCOUNT));
            }
            string path = string.Format(@"{0}\domain{1}.test.com.{2}.pfx"
                , CERTSRECORDSPATH
                , domainID
                , subId);
            return  new System.Security.Cryptography.X509Certificates.X509Certificate2(path, String.Empty);
        }

        /// <summary>
        /// attempts to load up a Certificate instance from the metadata, testing pfx file associated with the domain
        /// </summary>
        /// <param name="domainID">long containing the id of the relative domain for which the pfx file is to be loaded</param>
        /// <returns>Certificate instance populated with data from the related pfx file</returns>
        protected static byte[] GetCertBytesTestCertPfx(long domainID, int subId)
        {
            if (domainID > MAXDOMAINCOUNT || domainID <= 0)
            {
                throw new Exception(string.Format("Domain ID is out of range (1-{0}", MAXDOMAINCOUNT));
            }
            if (subId > MAXCERTPEROWNER || subId <= 0)
            {
                throw new Exception(string.Format("Cert sub ID is out of range (1-{0}", MAXDOMAINCOUNT));
            }
            string path = string.Format(@"{0}\domain{1}.test.com.{2}.pfx"
                , CERTSRECORDSPATH
                , domainID
                , subId);
            using (FileStream fs = new FileStream(path, FileMode.Open, FileAccess.Read))
            {
                return  new BinaryReader(fs).ReadBytes((int)new FileInfo(path).Length);
            }
        }
        
        /// <summary>
        /// generates a random cert id within the allotted range
        /// </summary>
        /// <returns>long random cert id</returns>
        protected int GetRndCertID()
        {
            return (new Random().Next(1, MAXCERTPEROWNER * MAXDOMAINCOUNT));
        }

        /// <summary>
        /// generates a random cert id within the allotted range
        /// </summary>
        /// <returns>long random cert id</returns>
        protected int GetRndDomainID()
        {
            return (new Random().Next(1, MAXDOMAINCOUNT));
        }

    }
}