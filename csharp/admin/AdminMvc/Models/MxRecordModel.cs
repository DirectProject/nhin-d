using System.ComponentModel.DataAnnotations;

using Health.Direct.Admin.Console.Common;
using Health.Direct.Common.DnsResolver;

namespace Health.Direct.Admin.Console.Models
{
    [MetadataType(typeof(Metadata))]
    public class MxRecordModel : DnsRecordModel
    {
        [Required(ErrorMessage = "Exchange is required")]
        [StringLength(255, ErrorMessage = "Exchange may not be longer than 255 characters")]
        [DomainName(ErrorMessage = "Invalid exchange")]
        public string Exchange { get; set; }

        [Required, Range(0, int.MaxValue)]
        public int TTL { get; set; }

        [Required, Range(0, short.MaxValue)]
        public short Preference { get; set; }

        public override int TypeID
        {
            get { return (int)DnsStandard.RecordType.MX; }
            set { }
        }

        public override byte[] RecordData
        {
            get
            {
                DnsBuffer buffer = new DnsBuffer();
                new MXRecord(DomainName, Exchange, Preference) {TTL = TTL}.Serialize(buffer);
                return buffer.Buffer;
            }
            set
            {
                base.RecordData = value;
                DnsBufferReader rdr = new DnsBufferReader(value, 0, value.Length);
                var record = DnsResourceRecord.Deserialize(ref rdr) as MXRecord;
                if (record != null)
                {
                    Exchange = record.Exchange;
                    Preference = record.Preference;
                    TTL = record.TTL;
                }
            }
        }

        public new class Metadata : DnsRecordModel.Metadata
        {
            public string Exchange { get; set; }
            public int TTL { get; set; }
            public int Preference { get; set; }
        }
    }
}