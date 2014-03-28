/* 
 Copyright (c) 2010, Direct Project
 All rights reserved.

 Authors:
    Chris Lomonico      (chris.lomonico@surescripts.com)
    Umesh Madan         umeshma@microsoft.com
  
Redistribution and use in source and binary forms, with or without modification, are permitted provided that the following conditions are met:

Redistributions of source code must retain the above copyright notice, this list of conditions and the following disclaimer.
Redistributions in binary form must reproduce the above copyright notice, this list of conditions and the following disclaimer in the documentation and/or other materials provided with the distribution.
Neither the name of The Direct Project (directproject.org) nor the names of its contributors may be used to endorse or promote products derived from this software without specific prior written permission.
THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 
*/
using System;
using System.IO;
using System.Collections.Generic;
using Health.Direct.Common.Extensions;
using Health.Direct.Config.Client.DomainManager;
using Health.Direct.Config.Store;
using Health.Direct.Config.Tools;
using Health.Direct.Config.Tools.Command;
using Health.Direct.Common.DnsResolver;


namespace Health.Direct.Config.Console.Command
{
    public class DnsRecordCommands : CommandsBase<DnsRecordManagerClient>
    {
        #region usage strings

        private const string ImportMXUsage
            = "Import a new MX dns record from a binary file."
              + Constants.CRLF + "    filepath "
              + Constants.CRLF + "\t filePath: path to the MX record binary file. Can have any (or no extension)";

        private const string ImportSOAUsage
            = "Import a new SOA dns record from a binary file."
              + Constants.CRLF + "    filepath "
              + Constants.CRLF + "\t filePath: path to the SOA record binary file. Can have any (or no extension)";

        private const string ImportAddressUsage
            = "Import a new A dns record from a binary file."
              + Constants.CRLF + "    filepath "
              + Constants.CRLF + "\t filePath: path to the A record binary file. Can have any (or no extension)";

        private const string AddMXUsage
            = "Add a new MX dns record."
            + Constants.CRLF + DnsRecordParser.ParseMXUsage;

        private const string EnsureMXUsage
            = "Adds a new MX dns record if an identical one does't already exist. "
            + Constants.CRLF + DnsRecordParser.ParseMXUsage;

        private const string AddSOAUsage
            = "Add a new SOA dns record."
              + Constants.CRLF + DnsRecordParser.ParseSOAUsage;

        private const string EnsureSOAUsage
            = "Add a new SOA dns record if an identical one does not exist."
              + Constants.CRLF + DnsRecordParser.ParseSOAUsage;
              
        private const string AddANAMEUsage
            = "Add a new ANAME dns record."
              + Constants.CRLF + DnsRecordParser.ParseANAMEUsage;

        private const string EnsureANAMEUsage
            = "Add a new ANAME dns record if an identical one does not exist."
              + Constants.CRLF + DnsRecordParser.ParseANAMEUsage;

        private const string AddNSUsage
            = "Add a new NS dns record."
              + Constants.CRLF + DnsRecordParser.ParseNSUsage;

        private const string EnsureNSUsage
            = "Add a new NS dns record if an identical one does not exist."
              + Constants.CRLF + DnsRecordParser.ParseNSUsage;

        private const string AddCNAMEUsage
            = "Add a new NS dns record."
              + Constants.CRLF + DnsRecordParser.ParseCNAMEUsage;

        private const string EnsureCNAMEUsage
            = "Add a new NS dns record if an identical one does not exist."
              + Constants.CRLF + DnsRecordParser.ParseCNAMEUsage;

        private const string RemoveRecordUsage
             = "Remove an existing record by its ID."
              + Constants.CRLF + "  recordid"
              + Constants.CRLF + "\t recordid: record id to be removed from the database";

        private const string GetMXUsage
             = "Gets an existing MX record by ID."
              + Constants.CRLF + "  recordid"
              + Constants.CRLF + "\t recordid: record id to be retrieved from the database";


        private const string GetSOAUsage
             = "Gets an existing SOA record by ID."
              + Constants.CRLF + "  recordid"
              + Constants.CRLF + "\t recordid: record id to be retrieved from the database";


        private const string GetANAMEUsage
             = "Gets an existing ANAME record by ID."
              + Constants.CRLF + "  recordid"
              + Constants.CRLF + "\t recordid: record id to be retrieved from the database";

        private const string GetNSUsage
             = "Gets an existing NS record by ID."
              + Constants.CRLF + "  recordid"
              + Constants.CRLF + "\t recordid: record id to be retrieved from the database";

        private const string GetCNAMEUsage
             = "Gets an existing CName record by ID."
              + Constants.CRLF + "  recordid"
              + Constants.CRLF + "\t recordid: record id to be retrieved from the database";
              
        #endregion

        DnsRecordPrinter m_printer;
        DnsRecordParser m_parser;
        
        internal DnsRecordCommands(ConfigConsole console, Func<DnsRecordManagerClient> client)
            : base(console, client)
        {
            m_parser = new DnsRecordParser();
            m_printer = new DnsRecordPrinter(System.Console.Out);
        }

        /// <summary>
        /// loads and verifies the dnsrecords from the bin associated bin files, ensuring that the types
        /// match up
        /// </summary>
        /// <typeparam name="T">Type of record that is expected</typeparam>
        /// <param name="path">path to the bin file to be loaded</param>
        /// <returns>bytes from the bin file</returns>
        protected T LoadAndVerifyDnsRecordFromBin<T>(string path) where T : DnsResourceRecord
        {
            T record;
            //----------------------------------------------------------------------------------------------------
            //---read the stream from the bytes
            using (FileStream fs = new FileStream(path, FileMode.Open, FileAccess.Read))
            {
                byte[] bytes = new BinaryReader(fs).ReadBytes((int)new FileInfo(path).Length);
                DnsBufferReader rdr = new DnsBufferReader(bytes, 0, bytes.Length);
                record = DnsResourceRecord.Deserialize(ref rdr) as T;
                if (record == null)
                {
                    throw new TypeLoadException("unexpected type encountered in file");
                }
            }
            return record;
        }

        /// <summary>
        /// simple method to get the bytes from a given DnsResourceRecord
        /// </summary>
        /// <param name="record">DnsResourceRecord instance</param>
        /// <returns>byte[] representation the DnsResourceRecord instance</returns>
        protected byte[] GetBytesFromRecord(DnsResourceRecord record)
        {
            DnsBuffer buff = new DnsBuffer();
            record.Serialize(buff);
            return buff.Buffer;
        }

        /// <summary>
        /// Generic method to add a dns record to the database
        /// </summary>
        /// <typeparam name="T">DnsResourceRecord instance to be added</typeparam>
        /// <param name="path">path to the file to load the binary data from</param>
        /// <param name="typeID">type id used to identify the type of record being added</param>
        protected void ImportRecord<T>(string path
            , int typeID) where T : DnsResourceRecord
        {
            DnsRecord dnsRecord = new DnsRecord {TypeID = typeID};
            T record = this.LoadAndVerifyDnsRecordFromBin<T>(path);
            dnsRecord.RecordData = GetBytesFromRecord(record);
            dnsRecord.DomainName = record.Name;
            Client.AddDnsRecord(dnsRecord);
        }

        /// <summary>
        /// Import a new mx record
        /// </summary>
        [Command(Name = "Dns_MX_Import", Usage = ImportMXUsage)]
        public void MXImport(string[] args)
        {
            string path = args.GetRequiredValue(0);
            this.ImportRecord<MXRecord>(path
                , (int)DnsStandard.RecordType.MX);
        }

        /// <summary>
        /// Import a new SOA record
        /// </summary>
        [Command(Name = "Dns_SOA_Import", Usage = ImportSOAUsage)]
        public void SOAImport(string[] args)
        {
            string path = args.GetRequiredValue(0);
            this.ImportRecord<SOARecord>(path
                , (int)DnsStandard.RecordType.SOA);
        }

        /// <summary>
        /// Import a new A record
        /// </summary>
        [Command(Name = "Dns_ANAME_Import", Usage = ImportAddressUsage)]
        public void ImportAddress(string[] args)
        {
            string path = args.GetRequiredValue(0);
            this.ImportRecord<AddressRecord>(path
                , (int)DnsStandard.RecordType.ANAME);
        }       

        /// <summary>
        /// Add a new MX DnsRecord entry to the db
        /// </summary>
        /// <param name="args"></param>
        [Command(Name = "Dns_MX_Add", Usage = AddMXUsage)]
        public void AddMX(string[] args)
        {
            DnsRecord record = m_parser.ParseMX(args);
            Client.AddDnsRecord(record);
        }

        /// <summary>
        /// Add this MX DnsRecord entry to the db if it doesn't already exist
        /// </summary>
        /// <param name="args"></param>
        [Command(Name = "Dns_MX_Ensure", Usage = EnsureMXUsage)]
        public void EnsureMX(string[] args)
        {
            DnsRecord record = m_parser.ParseMX(args);
            if (!this.VerifyIsUnique(record, false))
            {
                return;
            }
            
            Client.AddDnsRecord(record);
        }

        /// <summary>
        /// Add a new SOA DnsRecord entry to the db
        /// </summary>
        /// <param name="args"></param>
        [Command(Name = "Dns_SOA_Add", Usage = AddSOAUsage)]
        public void AddSOA(string[] args)
        {
            DnsRecord record = m_parser.ParseSOA(args);
            Client.AddDnsRecord(record);
        }

        [Command(Name = "Dns_SOA_Ensure", Usage = EnsureSOAUsage)]
        public void EnsureSOA(string[] args)
        {
            DnsRecord record = m_parser.ParseSOA(args);
            if (!this.VerifyIsUnique(record, false))
            {
                return;
            }
            
            Client.AddDnsRecord(record);
        }

        /// <summary>
        /// Add a new A DnsRecord entry to the db
        /// </summary>
        /// <param name="args"></param>
        [Command(Name = "Dns_ANAME_Add", Usage = AddANAMEUsage)]
        public void AddANAME(string[] args)
        {
            DnsRecord record = m_parser.ParseANAME(args);
            Client.AddDnsRecord(record);
        }

        [Command(Name = "Dns_ANAME_Ensure", Usage = EnsureANAMEUsage)]
        public void EnsureANAME(string[] args)
        {
            DnsRecord record = m_parser.ParseANAME(args);
            if (!this.VerifyIsUnique(record, false))
            {
                return;
            }
            
            Client.AddDnsRecord(record);
        }

        /// <summary>
        /// Add a new NS DnsRecord entry to the db
        /// </summary>
        /// <param name="args"></param>
        [Command(Name = "Dns_NS_Add", Usage = AddNSUsage)]
        public void AddNS(string[] args)
        {
            DnsRecord record = m_parser.ParseNS(args);
            Client.AddDnsRecord(record);
        }

        [Command(Name = "Dns_NS_Ensure", Usage = EnsureNSUsage)]
        public void EnsureNS(string[] args)
        {
            DnsRecord record = m_parser.ParseNS(args);
            if (!this.VerifyIsUnique(record, false))
            {
                return;
            }

            Client.AddDnsRecord(record);
        }

        /// <summary>
        /// Add a new NS DnsRecord entry to the db
        /// </summary>
        /// <param name="args"></param>
        [Command(Name = "Dns_CNAME_Add", Usage = AddCNAMEUsage)]
        public void AddCNAME(string[] args)
        {
            DnsRecord record = m_parser.ParseCNAME(args);
            Client.AddDnsRecord(record);
        }

        [Command(Name = "Dns_CNAME_Ensure", Usage = EnsureCNAMEUsage)]
        public void EnsureCNAME(string[] args)
        {
            DnsRecord record = m_parser.ParseCNAME(args);
            if (!this.VerifyIsUnique(record, false))
            {
                return;
            }

            Client.AddDnsRecord(record);
        }

        /// <summary>
        /// Removes an existing MX DnsRecord entry to the db
        /// </summary>
        /// <param name="args"></param>
        [Command(Name = "Dns_Record_Remove", Usage = RemoveRecordUsage)]
        public void RemoveRecord(string[] args)
        {
            long recordID = args.GetRequiredValue<long>(0);
            Client.RemoveDnsRecordByID(recordID);
        }

        /// <summary>
        /// Gets an existing MX DnsRecord entry from the db
        /// </summary>
        /// <param name="args"></param>
        [Command(Name = "Dns_MX_Get", Usage = GetMXUsage)]
        public void GetMX(string[] args)
        {
            this.Get<MXRecord>(args.GetRequiredValue<long>(0));
        }

        /// <summary>
        /// Gets an existing SOA DnsRecord entry from the db
        /// </summary>
        /// <param name="args"></param>
        [Command(Name = "Dns_SOA_Get", Usage = GetSOAUsage)]
        public void GetSOA(string[] args)
        {
            this.Get<SOARecord>(args.GetRequiredValue<long>(0));
        }

        /// <summary>
        /// Gets an existing ANAME DnsRecord entry from the db
        /// </summary>
        /// <param name="args"></param>
        [Command(Name = "Dns_ANAME_Get", Usage = GetANAMEUsage)]
        public void GetANAME(string[] args)
        {
            this.Get<AddressRecord>(args.GetRequiredValue<long>(0));
        }

        /// <summary>
        /// Gets an existing ANAME DnsRecord entry from the db
        /// </summary>
        /// <param name="args"></param>
        [Command(Name = "Dns_NS_Get", Usage = GetNSUsage)]
        public void GetNS(string[] args)
        {
            this.Get<NSRecord>(args.GetRequiredValue<long>(0));
        }

        /// <summary>
        /// Gets an existing ANAME DnsRecord entry from the db
        /// </summary>
        /// <param name="args"></param>
        [Command(Name = "Dns_CNAME_Get", Usage = GetCNAMEUsage)]
        public void GetCNAME(string[] args)
        {
            this.Get<CNameRecord>(args.GetRequiredValue<long>(0));
        }
        
        void Get<T>(long recordID)
            where T : DnsResourceRecord
        {
            T record = this.GetRecord(recordID).Deserialize<T>();
            m_printer.Print(record);
        }
        
        /// <summary>
        /// Resolves all records that match the given domain
        /// </summary>
        /// <param name="args"></param>
        [Command(Name = "Dns_Match", Usage = "Resolve all records for the given domain")]
        public void Match(string[] args)
        {
            string domain = args.GetRequiredValue(0);
            DnsRecord[] records = Client.GetMatchingDnsRecords(domain);
            if (records.IsNullOrEmpty())
            {
                throw new ArgumentException("Domain not found");
            }
            Print(records);
        }

        /// <summary>
        /// Resolves records that match the given domain
        /// </summary>
        /// <param name="args"></param>
        [Command(Name = "Dns_SOA_Match", Usage = "Resolve SOA records for the given domain")]
        public void MatchSOA(string[] args)
        {
            this.Match(args.GetRequiredValue(0), DnsStandard.RecordType.SOA);
        }
        
        [Command(Name = "Dns_ANAME_Match", Usage = "Resolve Address records for the given domain")]
        public void MatchAName(string[] args)
        {
            this.Match(args.GetRequiredValue(0), DnsStandard.RecordType.ANAME);
        }

        [Command(Name = "Dns_MX_Match", Usage = "Resolve MX records for the given domain")]
        public void MatchMX(string[] args)
        {
            this.Match(args.GetRequiredValue(0), DnsStandard.RecordType.MX);
        }

        [Command(Name = "Dns_NS_Match", Usage = "Resolve NS records for the given domain")]
        public void MatchNS(string[] args)
        {
            this.Match(args.GetRequiredValue(0), DnsStandard.RecordType.NS);
        }

        [Command(Name = "Dns_CNAME_Match", Usage = "Resolve CNAME records for the given domain")]
        public void MatchCNAME(string[] args)
        {
            this.Match(args.GetRequiredValue(0), DnsStandard.RecordType.CNAME);
        }
        
        void Match(string domain, DnsStandard.RecordType type)
        {
            DnsRecord[] records = this.GetRecords(domain, type);
            if (records.IsNullOrEmpty())
            {
                throw new ArgumentException("No matches");
            }
            Print(records);
        }
        
        DnsRecord GetRecord(long recordID)
        {
            DnsRecord dr = Client.GetDnsRecord(recordID);
            if (dr == null)
            {
                throw new ArgumentException("No record found matching id");
            }
            if (dr.RecordData.IsNullOrEmpty())
            {
                throw new ArgumentException("Empty record data found for matchng record");
            }
            
            return dr;
        }
        
        DnsRecord[] GetRecords(string domain, DnsStandard.RecordType type)
        {
            DnsRecord[] records = Client.GetMatchingDnsRecordsByType(domain, type);
            if (records.IsNullOrEmpty())
            {
                throw new ArgumentException("No matches");
            }
            return records;
        }

        bool VerifyIsUnique(DnsRecord record, bool details)
        {
            DnsRecord existing = this.Find(record);
            if (existing != null)
            {
                CommandUI.PrintBold("Record already exists");
                if (details)
                {
                    Print(existing);
                }
                else
                {
                    CommandUI.Print("RecordID", existing.ID);
                }
                return false;
            }
            
            return true;
        }
                
        DnsRecord Find(DnsRecord record)
        {
            DnsRecord[] existingRecords = Client.GetMatchingDnsRecordsByType(record.DomainName, record.RecordType);
            if (existingRecords.IsNullOrEmpty())
            {
                return null;
            }
            
            DnsResourceRecord testRecord = record.Deserialize();
            foreach(DnsRecord existingRecord in existingRecords)
            {
                DnsResourceRecord rr = existingRecord.Deserialize();
                if (rr.Equals(testRecord))
                {
                    return existingRecord;
                }
            }
            
            return null;
        }
        
        void Print(DnsRecord[] records)
        {
            foreach(DnsRecord record in records)
            {
                Print(record);
                CommandUI.PrintDivider();
            }
        }
        
        void Print(DnsRecord dnsRecord)
        {
            CommandUI.Print("RecordID", dnsRecord.ID);                        
            DnsResourceRecord resourceRecord = dnsRecord.Deserialize();

            m_printer.Print(resourceRecord);

            CommandUI.Print("CreateDate", dnsRecord.CreateDate);
            CommandUI.Print("UpdateDate", dnsRecord.UpdateDate);
            CommandUI.Print("Notes", dnsRecord.Notes);
        }
    }
    
    public class DnsRecordParser
    {
        #region usage strings

        public const string ParseANAMEUsage = 
              "  domainname ipaddress [ttl] [notes]"
              + Constants.CRLF + "\t domainname: domain name for the record"
              + Constants.CRLF + "\t ipaddress: IP address in dot notation"
              + Constants.CRLF + "\t [ttl]: time to live in seconds, 32bit int"
              + Constants.CRLF + "\t [notes]: description for the record";

        public const string ParseSOAUsage =
              "  domainname primarysourcedomain responsibleemail serialnumber [ttl] [refresh] [retry] [expire] [minimum] [notes]"
              + Constants.CRLF + "\t domainname: The domain name of the name server that was the primary source for this zone"
              + Constants.CRLF + "\t responsibleemail: Email mailbox of the hostmaster"
              + Constants.CRLF + "\t serialnumber: Version number of the original copy of the zone."
              + Constants.CRLF + "\t [ttl]: time to live in seconds, 32bit int"
              + Constants.CRLF + "\t [refresh]: Number of seconds before the zone should be refreshed. Default is 10800 seconds"
              + Constants.CRLF + "\t [retry]: Number of seconds before failed refresh should be retried. Default is 3600 seconds"
              + Constants.CRLF + "\t [expire]: Number of seconds before records should be expired if not refreshed. Default is 86400 seconds"
              + Constants.CRLF + "\t [minimum]: Minimum TTL for this zone. Default is 10800 seconds"
              + Constants.CRLF + "\t [notes]: description for the record";

        public const string ParseMXUsage =
              "  domainname exchange [ttl] [preference] [notes]"
              + Constants.CRLF + "\t domainname: domain name for the record"
              + Constants.CRLF + "\t exchange: smtp domain name for the record"
              + Constants.CRLF + "\t [ttl]: time to live in seconds"
              + Constants.CRLF + "\t [preference]: short value indicating preference of the record. Default 10"
              + Constants.CRLF + "\t [notes]: description for the record";

        public const string ParseNSUsage =
              "  domainname nameserver [ttl] [notes]"
              + Constants.CRLF + "\t domainname: domain name for the record"
              + Constants.CRLF + "\t nameserver: nameserver"
              + Constants.CRLF + "\t [ttl]: time to live in seconds"
              + Constants.CRLF + "\t [notes]: description for the record";

        public const string ParseCNAMEUsage =
              "  domainname cname [ttl] [notes]"
              + Constants.CRLF + "\t domainname: domain name for the record"
              + Constants.CRLF + "\t cname: alias for this domain"
              + Constants.CRLF + "\t [ttl]: time to live in seconds"
              + Constants.CRLF + "\t [notes]: description for the record";
        
        #endregion
                
        public DnsRecordParser()
        {
        }
        
        public DnsRecord ParseANAME(string[] args)
        {
            string domainName = args.GetRequiredValue(0);
            string ipAddress = args.GetRequiredValue(1);
            int ttl = this.ValidateTTL(args.GetOptionalValue<int>(2, 0));
            string notes = args.GetOptionalValue(3, string.Empty);

            AddressRecord record = new AddressRecord(domainName
               , ipAddress) { TTL = ttl };

            return new DnsRecord(domainName, DnsStandard.RecordType.ANAME, record.Serialize(), notes);
        }
                
        public DnsRecord ParseSOA(string[] args)
        {
            string domainName = args.GetRequiredValue(0);
            string primarySourceDomain = args.GetRequiredValue(1);
            string responsibleEmail = args.GetRequiredValue(2);
            int serialNumber = args.GetRequiredValue<int>(3);
            int ttl = this.ValidateTTL(args.GetOptionalValue<int>(4, 0));

            int refresh = args.GetOptionalValue(5, 10800);
            int retry = args.GetOptionalValue(6, 3600);
            int expire = args.GetOptionalValue(7, 86400);
            int minimum = args.GetOptionalValue(8, 10800);
            string notes = args.GetOptionalValue(9, string.Empty);

            SOARecord record = new SOARecord(domainName
               , primarySourceDomain
               , responsibleEmail
               , serialNumber
               , refresh
               , retry
               , expire
               , minimum) { TTL = ttl };

            return new DnsRecord(domainName, DnsStandard.RecordType.SOA, record.Serialize(), notes);
        }
                
        public DnsRecord ParseMX(string[] args)
        {        
            string domainName = args.GetRequiredValue(0);
            string exchange = args.GetRequiredValue(1);
            int ttl = this.ValidateTTL(args.GetOptionalValue<int>(2, 0));
            short pref = args.GetOptionalValue<short>(3, 10);
            string notes = args.GetOptionalValue(4, string.Empty);

            MXRecord record = new MXRecord(domainName
                , exchange
                , pref) { TTL = ttl };

            DnsRecord dnsRecord = new DnsRecord(domainName, DnsStandard.RecordType.MX, record.Serialize(), notes);
            return dnsRecord;
        }
        
        /// <summary>
        /// Arguments:
        /// 0: domainName
        /// 1: nameServer
        /// 2: ttl (optional)
        /// 3: notes (optional)
        /// </summary>
        /// <param name="args"></param>
        /// <returns></returns>
        public DnsRecord ParseNS(string[] args)
        {
            string domainName = args.GetRequiredValue(0);
            string nameServer = args.GetRequiredValue(1);
            int ttl = this.ValidateTTL(args.GetOptionalValue<int>(2, 0));
            string notes = args.GetOptionalValue(3, string.Empty);
            
            NSRecord nsRecord = new NSRecord(domainName, nameServer) {TTL = ttl};
            DnsRecord dnsRecord = new DnsRecord(domainName, DnsStandard.RecordType.NS, nsRecord.Serialize(), notes);
            return dnsRecord;
        }
        
        public DnsRecord ParseCNAME(string[] args)
        {
            string domainName = args.GetRequiredValue(0);
            string cname = args.GetRequiredValue(1);
            int ttl = this.ValidateTTL(args.GetOptionalValue<int>(2, 0));
            string notes = args.GetOptionalValue(3, string.Empty);

            CNameRecord cnameRecord = new CNameRecord(domainName, cname) {TTL = ttl};
            DnsRecord dnsRecord = new DnsRecord(domainName, DnsStandard.RecordType.CNAME, cnameRecord.Serialize(), notes);
            return dnsRecord;
        }
                
        int ValidateTTL(int ttl)
        {
            if (ttl < 0)
            {
                throw new ArgumentException(string.Format("Invalid TTL {0}", ttl));
            }
            
            return ttl;
        }
    }
}
