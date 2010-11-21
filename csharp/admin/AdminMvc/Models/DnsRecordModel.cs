using System;
using System.ComponentModel.DataAnnotations;

using Health.Direct.Admin.Console.Common;
using Health.Direct.Common.DnsResolver;
using Health.Direct.Config.Store;

namespace Health.Direct.Admin.Console.Models
{
    [MetadataType(typeof(Metadata))]
    public class DnsRecordModel
    {
        protected DnsRecordModel()
        {
            CreateDate = DateTimeHelper.Now;
            UpdateDate = CreateDate;
        }

        public long ID { get; set; }
        public string DomainName { get; set; }
        public string Notes { get; set; }
        public virtual int TypeID { get; set; }
        public virtual byte[] RecordData { get; set; }
        public DateTime CreateDate { get; set; }
        public DateTime UpdateDate { get; set; }

        public string TypeString
        {
            get
            {
                return ((DnsStandard.RecordType)TypeID).ToString();
            }
        }

        public class Metadata
        {
            [Required(ErrorMessage = "Domain name is required")]
            [StringLength(255)]
            [DomainName]
            public string DomainName { get; set; }

            [StringLength(500, ErrorMessage = "Notes name may not be longer than 500 characters")]
            public string Notes { get; set; }
        }
    }
}