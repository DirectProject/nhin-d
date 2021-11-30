using System;
using System.Collections.Generic;

namespace config.store.temp.Models
{
    public partial class CertPolicy
    {
        public CertPolicy()
        {
            CertPolicyGroupMaps = new HashSet<CertPolicyGroupMap>();
        }

        public long CertPolicyId { get; set; }
        public string Name { get; set; } = null!;
        public string Description { get; set; } = null!;
        public string Lexicon { get; set; } = null!;
        public byte[] Data { get; set; } = null!;
        public DateTime CreateDate { get; set; }

        public virtual ICollection<CertPolicyGroupMap> CertPolicyGroupMaps { get; set; }
    }
}
