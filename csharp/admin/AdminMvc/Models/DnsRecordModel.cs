using System;

namespace Health.Direct.Admin.Console.Models
{
    public class DnsRecordModel
    {
        public long ID { get; set; }
        public string DomainName { get; set; }
        public string Notes { get; set; }
        public int TypeID { get; set; }
        public DateTime CreateDate { get; set; }
        public DateTime UpdateDate { get; set; }
    }
}