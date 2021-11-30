using System;
using System.Collections.Generic;

namespace config.store.temp.Models
{
    public partial class DnsRecord
    {
        public long RecordId { get; set; }
        public string DomainName { get; set; } = null!;
        public int TypeId { get; set; }
        public byte[]? RecordData { get; set; }
        public DateTime CreateDate { get; set; }
        public DateTime UpdateDate { get; set; }
        public string Notes { get; set; } = null!;
    }
}
