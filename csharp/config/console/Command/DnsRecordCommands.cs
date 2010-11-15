/* 
 Copyright (c) 2010, Direct Project
 All rights reserved.

 Authors:
    Chris Lomonico      (chris.lomonico@surescripts.com)
  
Redistribution and use in source and binary forms, with or without modification, are permitted provided that the following conditions are met:

Redistributions of source code must retain the above copyright notice, this list of conditions and the following disclaimer.
Redistributions in binary form must reproduce the above copyright notice, this list of conditions and the following disclaimer in the documentation and/or other materials provided with the distribution.
Neither the name of The Direct Project (directproject.org) nor the names of its contributors may be used to endorse or promote products derived from this software without specific prior written permission.
THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 
*/
using System;
using System.IO;

using Health.Direct.Config.Client.DomainManager;
using Health.Direct.Config.Store;
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
              + Constants.CRLF + "\t filePath: path fo the MX record binary file. Can have any (or no extension)";

        private const string ImportSOAUsage
            = "Import a new SOA dns record from a binary file."
              + Constants.CRLF + "    filepath "
              + Constants.CRLF + "\t filePath: path fo the SOA record binary file. Can have any (or no extension)";

        private const string ImportAddressUsage
            = "Import a new A dns record from a binary file."
              + Constants.CRLF + "    filepath "
              + Constants.CRLF + "\t filePath: path fo the A record binary file. Can have any (or no extension)";


        private const string AddMXUsage
            = "Add a new MX dns record."
              + Constants.CRLF + "  domainname exchange ttl [preference] [notes]"
              + Constants.CRLF + "\t domainname: domain name for the record"
              + Constants.CRLF + "\t exchange: smtp domain name for the record"
              + Constants.CRLF + "\t ttl: time to live, 32bit int"
              + Constants.CRLF + "\t [preference]: short value indicating preference of the record"
              + Constants.CRLF + "\t [notes]: description for the record";

        private const string AddSOAUsage
            = "Add a new SOA dns record."
              + Constants.CRLF + "  domainname primarysourcedomain responsibleemail serialnumber ttl [refresh] [retry] [expire] [minimum] [notes]"
              + Constants.CRLF + "\t domainname: The domain name of the name server that was the primary source for this zone"
              + Constants.CRLF + "\t responsibleemail: Email mailbox of the hostmaster"
              + Constants.CRLF + "\t serialnumber: Version number of the original copy of the zone."
              + Constants.CRLF + "\t ttl: time to live, 32bit int"
              + Constants.CRLF + "\t [refresh]: Number of seconds before the zone should be refreshed."
              + Constants.CRLF + "\t [retry]: Number of seconds before failed refresh should be retried."
              + Constants.CRLF + "\t [expire]: Number of seconds before records should be expired if not refreshed"
              + Constants.CRLF + "\t [minimum]: Minimum TTL for this zone."
              + Constants.CRLF + "\t [notes]: description for the record";

        private const string AddANAMEUsage
            = "Add a new ANAME dns record."
              + Constants.CRLF + "  domainname ipaddress ttl [notes]"
              + Constants.CRLF + "\t domainname: domain name for the record"
              + Constants.CRLF + "\t ipaddress: IP address in dot notation"
              + Constants.CRLF + "\t ttl: time to live, 32bit int"
              + Constants.CRLF + "\t [notes]: description for the record";

        private const string RemoveMXUsage
             = "Remove an existing MX record by ID."
              + Constants.CRLF + "  recordid"
              + Constants.CRLF + "\t recordid: record id to be removed from the database";


        private const string RemoveSOAUsage
             = "Remove an existing SOA record by ID."
              + Constants.CRLF + "  recordid"
              + Constants.CRLF + "\t recordid: record id to be removed from the database";


        private const string RemoveANAMEUsage
             = "Remove an existing ANAME record by ID."
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

        private const string UpdateMXUsage
            = "Update an existing MX dns record."
              + Constants.CRLF + "  recordid domainname exchange ttl [preference] [notes]"
              + Constants.CRLF + "\t recordid: id of the record to be update"
              + Constants.CRLF + "\t domainname: new domain name for the record"
              + Constants.CRLF + "\t exchange: new smtp domain name for the record"
              + Constants.CRLF + "\t ttl: new time to live, 32bit int"
              + Constants.CRLF + "\t [preference]: new short value indicating preference of the record"
              + Constants.CRLF + "\t [notes]: new description for the record";

        #endregion


        internal DnsRecordCommands(ConfigConsole console, Func<DnsRecordManagerClient> client)
            : base(console, client)
        {
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
            T record = null;
            //----------------------------------------------------------------------------------------------------
            //---read the stream from the bytes
            using (FileStream fs = new FileStream(path, FileMode.Open, FileAccess.Read))
            {
                byte[] bytes = new BinaryReader(fs).ReadBytes((int)new FileInfo(path).Length);
                DnsBufferReader rdr = new DnsBufferReader(bytes, 0, bytes.Length);
                record = DnsResourceRecord.Deserialize(ref rdr) as T;
                if (record.GetType() != typeof(T))
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
            DnsRecord dnsRecord = new DnsRecord();
            dnsRecord.TypeID = typeID;
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
            DnsRecord rec = new DnsRecord();

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
            DnsRecord rec = new DnsRecord();

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
            DnsRecord rec = new DnsRecord();

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
            string domainName = args.GetRequiredValue(0);
            string exchange = args.GetRequiredValue(1);
            int ttl = args.GetRequiredValue<int>(2);
            short pref = args.GetOptionalValue<short>(3,0);
            string notes = args.GetOptionalValue(4,string.Empty);
 
            MXRecord record = new MXRecord(domainName
                , exchange
                , pref);
            record.TTL = ttl;
            DnsRecord dns = new DnsRecord(domainName
                , (int)DnsStandard.RecordType.MX
                , this.GetBytesFromRecord(record)
                , notes);
            Client.AddDnsRecord(dns);
        }

        /// <summary>
        /// Add a new SOA DnsRecord entry to the db
        /// </summary>
        /// <param name="args"></param>
        [Command(Name = "Dns_SOA_Add", Usage = AddSOAUsage)]
        public void AddSOA(string[] args)
        {
            string domainName = args.GetRequiredValue(0);
            string primarySourceDomain = args.GetRequiredValue(1);
            string responsibleEmail = args.GetRequiredValue(2);
            int serialNumber = args.GetRequiredValue<int>(3);
            int ttl = args.GetRequiredValue<int>(4);

            int refresh = args.GetOptionalValue<int>(5,0);
            int retry = args.GetOptionalValue<int>(6,0);
            int expire = args.GetOptionalValue<int>(7,0);
            int minimum = args.GetOptionalValue<int>(8,0);
            string notes = args.GetOptionalValue(9,string.Empty);

            SOARecord record = new SOARecord(domainName
               , primarySourceDomain
               , responsibleEmail
               , serialNumber
               , refresh
               , retry
               , expire
               , minimum);
            record.TTL = ttl;
            DnsRecord dns = new DnsRecord(domainName
                , (int)DnsStandard.RecordType.SOA
                , this.GetBytesFromRecord(record)
                , notes);
            Client.AddDnsRecord(dns);
        }

        /// <summary>
        /// Add a new A DnsRecord entry to the db
        /// </summary>
        /// <param name="args"></param>
        [Command(Name = "Dns_ANAME_Add", Usage = AddANAMEUsage)]
        public void AddANAME(string[] args)
        {
            string domainName = args.GetRequiredValue(0);
            string ipAddress = args.GetRequiredValue(1);
            int ttl = args.GetRequiredValue<int>(2);
            string notes = args.GetOptionalValue(3, string.Empty);

            AddressRecord record = new AddressRecord(domainName
               , ipAddress);
            record.TTL = ttl;
            DnsRecord dns = new DnsRecord(domainName
                , (int)DnsStandard.RecordType.ANAME
                , this.GetBytesFromRecord(record)
                , notes);
            Client.AddDnsRecord(dns);
        }

        /// <summary>
        /// Removes an existing MX DnsRecord entry to the db
        /// </summary>
        /// <param name="args"></param>
        [Command(Name = "Dns_MX_Remove", Usage = RemoveMXUsage)]
        public void RemoveMX(string[] args)
        {
            long recordID = args.GetRequiredValue<long>(0);
            Client.RemoveDnsRecordByID(recordID);
        }

        /// <summary>
        /// Removes an existing SOA DnsRecord entry to the db
        /// </summary>
        /// <param name="args"></param>
        [Command(Name = "Dns_SOA_Remove", Usage = RemoveSOAUsage)]
        public void RemoveSOA(string[] args)
        {
            long recordID = args.GetRequiredValue<long>(0);
            Client.RemoveDnsRecordByID(recordID);
        }

        /// <summary>
        /// Removes an existing ANAME DnsRecord entry to the db
        /// </summary>
        /// <param name="args"></param>
        [Command(Name = "Dns_ANAME_Remove", Usage = RemoveANAMEUsage)]
        public void RemoveANAME(string[] args)
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
            long recordID = args.GetRequiredValue<long>(0);
            DnsRecord dr = Client.GetDnsRecord(recordID);
            if (dr == null)
            {
                CommandUI.Print(new Exception("no record found matching id"));
                return;
            }

            if (dr.RecordData == null || dr.RecordData.Length.Equals(0))
            {
                CommandUI.Print(new Exception("empty record data found for matchng record, please update or delete data"));
                return;
            }

            DnsBufferReader dbr = new DnsBufferReader(dr.RecordData
                , 0
                , dr.RecordData.Length);
            MXRecord ar = DnsResourceRecord.Deserialize(ref dbr) as MXRecord;
            if (ar == null)
            {
                CommandUI.Print(new Exception(string.Format(
                    "returned record type does not match expected type, found [{0}], expected [MX]"
                    , ((DnsStandard.RecordType)dr.TypeID))));
                return;
            }
            CommandUI.Print("DomainName", ar.Name);
            CommandUI.Print("Exchange", ar.Exchange);
            CommandUI.Print("Preference", ar.Preference);
            CommandUI.Print("TTL", ar.TTL);
            CommandUI.Print("CreateDate", dr.CreateDate);
            CommandUI.Print("UpdateDate", dr.UpdateDate);
            CommandUI.Print("Notes", dr.Notes);
                
        }

        /// <summary>
        /// Gets an existing SOA DnsRecord entry from the db
        /// </summary>
        /// <param name="args"></param>
        [Command(Name = "Dns_SOA_Get", Usage = GetSOAUsage)]
        public void GetSOA(string[] args)
        {
            long recordID = args.GetRequiredValue<long>(0);
            DnsRecord dr = Client.GetDnsRecord(recordID);
            if (dr == null)
            {
                CommandUI.Print(new Exception("no record found matching id"));
                return;
            }

            if (dr.RecordData == null || dr.RecordData.Length.Equals(0))
            {
                CommandUI.Print(new Exception("empty record data found for matchng record, please update or delete data"));
                return;
            }

            DnsBufferReader dbr = new DnsBufferReader(dr.RecordData
                , 0
                , dr.RecordData.Length);
            SOARecord ar = DnsResourceRecord.Deserialize(ref dbr) as SOARecord;
            if (ar == null)
            {
                CommandUI.Print(new Exception(string.Format(
                    "returned record type does not match expected type, found [{0}], expected [SOA]"
                    , ((DnsStandard.RecordType)dr.TypeID))));
                return;
            }
            CommandUI.Print("DomainName", ar.Name);
            CommandUI.Print("primarySourceDomain", ar.DomainName);
            CommandUI.Print("TTL", ar.TTL);
            CommandUI.Print("Refresh", ar.Refresh);
            CommandUI.Print("Retry", ar.Retry);
            CommandUI.Print("Expire", ar.Expire);
            CommandUI.Print("Minimum", ar.Minimum);
            CommandUI.Print("CreateDate", dr.CreateDate);
            CommandUI.Print("UpdateDate", dr.UpdateDate);
            CommandUI.Print("Notes", dr.Notes);
        }

        /// <summary>
        /// Gets an existing ANAME DnsRecord entry from the db
        /// </summary>
        /// <param name="args"></param>
        [Command(Name = "Dns_ANAME_Get", Usage = GetANAMEUsage)]
        public void GetANAME(string[] args)
        {
            long recordID = args.GetRequiredValue<long>(0);
            DnsRecord dr = Client.GetDnsRecord(recordID);
            if(dr == null){
                CommandUI.Print(new Exception("no record found matching id"));
                return;
            }

            if(dr.RecordData == null || dr.RecordData.Length.Equals(0)){
                CommandUI.Print(new Exception("empty record data found for matchng record, please update or delete data"));
                return;
            }

            DnsBufferReader dbr = new DnsBufferReader(dr.RecordData
                ,0
                ,dr.RecordData.Length);
            AddressRecord ar = DnsResourceRecord.Deserialize(ref dbr) as AddressRecord;
            if (ar == null)
            {
                CommandUI.Print(new Exception(string.Format(
                    "returned record type does not match expected type, found [{0}], expected [ANAME]"
                    ,((DnsStandard.RecordType)dr.TypeID))));
                return;
            }
            CommandUI.Print("DomainName", ar.Name);
            CommandUI.Print("IP Address", ar.IPAddress);
            CommandUI.Print("TTL", ar.TTL);
            CommandUI.Print("CreateDate", dr.CreateDate);
            CommandUI.Print("UpdateDate", dr.UpdateDate);
            CommandUI.Print("Notes", dr.Notes);
                

        }

        /*
        /// <summary>
        /// Update an existing MX DnsRecord entry to the db
        /// </summary>
        /// <param name="args"></param>
        [Command(Name = "Dns_MX_Updte", Usage = UpdateMXUsage)]
        public void UpdateMX(string[] args)
        {
            long recordID = args.GetRequiredValue<long>(0);
            string domainName = args.GetRequiredValue(1);
            string exchange = args.GetRequiredValue(2);
            int ttl = args.GetRequiredValue<int>(3);
            short pref = args.GetOptionalValue<short>(4, 0);
            string notes = args.GetOptionalValue(5, string.Empty);

            MXRecord record = new MXRecord(domainName
                , exchange
                , pref);
            record.TTL = ttl;
            DnsRecord dns = new DnsRecord(domainName
                , (int)DnsStandard.RecordType.MX
                , this.GetBytesFromRecord(record)
                , notes);
            Client.AddDnsRecord(dns);
        }

        /// <summary>
        /// Update an existing SOA DnsRecord entry to the db
        /// </summary>
        /// <param name="args"></param>
        [Command(Name = "Dns_SOA_Add", Usage = AddSOAUsage)]
        public void AddSOA(string[] args)
        {
            string domainName = args.GetRequiredValue(0);
            string primarySourceDomain = args.GetRequiredValue(1);
            string responsibleEmail = args.GetRequiredValue(2);
            int serialNumber = args.GetRequiredValue<int>(3);
            int ttl = args.GetRequiredValue<int>(4);

            int refresh = args.GetOptionalValue<int>(5, 0);
            int retry = args.GetOptionalValue<int>(6, 0);
            int expire = args.GetOptionalValue<int>(7, 0);
            int minimum = args.GetOptionalValue<int>(8, 0);
            string notes = args.GetOptionalValue(9, string.Empty);

            SOARecord record = new SOARecord(domainName
               , primarySourceDomain
               , responsibleEmail
               , serialNumber
               , refresh
               , retry
               , expire
               , minimum);
            record.TTL = ttl;
            DnsRecord dns = new DnsRecord(domainName
                , (int)DnsStandard.RecordType.SOA
                , this.GetBytesFromRecord(record)
                , notes);
            Client.AddDnsRecord(dns);
        }


        /// <summary>
        /// Update an existing A DnsRecord entry to the db
        /// </summary>
        /// <param name="args"></param>
        [Command(Name = "Dns_ANAME_Add", Usage = AddANAMEUsage)]
        public void AddANAME(string[] args)
        {
            string domainName = args.GetRequiredValue(0);
            string ipAddress = args.GetRequiredValue(1);
            int ttl = args.GetRequiredValue<int>(4);
            string notes = args.GetOptionalValue(9, string.Empty);

            AddressRecord record = new AddressRecord(domainName
               , ipAddress);
            record.TTL = ttl;
            DnsRecord dns = new DnsRecord(domainName
                , (int)DnsStandard.RecordType.ANAME
                , this.GetBytesFromRecord(record)
                , notes);
            Client.AddDnsRecord(dns);
        }
        */
    }
}
