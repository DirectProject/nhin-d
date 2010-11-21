using System.ComponentModel.DataAnnotations;
using System.Net;

using Health.Direct.Common.DnsResolver;

namespace Health.Direct.Admin.Console.Models
{
    [MetadataType(typeof(Metadata))]
    public class AddressRecordModel : DnsRecordModel
    {
        public string IPAddress { get; set; }
        public int TTL { get; set; }

        public override int TypeID
        {
            get { return (int)DnsStandard.RecordType.ANAME; }
            set { }
        }

        public override byte[] RecordData
        {
            get
            {
                DnsBuffer buffer = new DnsBuffer();
                new AddressRecord(DomainName, IPAddress) {TTL = TTL}.Serialize(buffer);
                return buffer.Buffer;
            }
            set
            {
                base.RecordData = value;
                DnsBufferReader rdr = new DnsBufferReader(value, 0, value.Length);
                var record = DnsResourceRecord.Deserialize(ref rdr) as AddressRecord;
                if (record != null)
                {
                    IPAddress = record.IPAddress.ToString();
                    TTL = record.TTL;
                }
            }
        }

        public new class Metadata : DnsRecordModel.Metadata
        {
            [Required]
            public IPAddress IPAddress { get; set; }

            [Required, Range(0, int.MaxValue)]
            public int TTL { get; set; }
        }
    }
}