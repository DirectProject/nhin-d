using System.ComponentModel.DataAnnotations;

using Health.Direct.Common.DnsResolver;

namespace Health.Direct.Admin.Console.Models
{
    [MetadataType(typeof(Metadata))]
    public class SoaRecordModel : DnsRecordModel
    {
        public override int TypeID
        {
            get { return (int)DnsStandard.RecordType.SOA; }
            set { }
        }

        public string PrimarySourceDomain { get; set; }
        public string ResponsibleName { get; set; }
        public int SerialNumber { get; set; }
        public int Refresh { get; set; }
        public int Retry { get; set; }
        public int Expire { get; set; }
        public int Minimum { get; set; }

        public override byte[] RecordData
        {
            get
            {
                DnsBuffer buffer = new DnsBuffer();
                new SOARecord(DomainName, PrimarySourceDomain, ResponsibleName, SerialNumber, Refresh, Retry, Expire, Minimum).Serialize(buffer);
                return buffer.Buffer;
            }
            set
            {
                base.RecordData = value;
                DnsBufferReader rdr = new DnsBufferReader(value, 0, value.Length);
                var record = DnsResourceRecord.Deserialize(ref rdr) as SOARecord;
                if (record != null)
                {
                    PrimarySourceDomain = record.DomainName;
                    ResponsibleName = record.ResponsibleName;
                    SerialNumber = record.SerialNumber;
                    Refresh = record.SerialNumber;
                    Retry = record.Retry;
                    Expire = record.Expire;
                    Minimum = record.Minimum;
                }
            }
        }

        public new class Metadata : DnsRecordModel.Metadata
        {
            [Required]
            public string PrimarySourceDomain { get; set; }
            [Required]
            public string ResponsibleName { get; set; }
            [Required]
            public int SerialNumber { get; set; }

            public int Refresh { get; set; }
            public int Retry { get; set; }
            public int Expire { get; set; }
            public int Minimum { get; set; }
        }
    }
}