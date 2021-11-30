using System;
using System.Collections.Generic;

namespace config.store.temp.Models
{
    public partial class CertPolicyGroupDomainMap
    {
        public long MapId { get; set; }
        public string Owner { get; set; } = null!;
        public long CertPolicyGroupId { get; set; }
        public DateTime CreateDate { get; set; }

        public virtual CertPolicyGroup CertPolicyGroup { get; set; } = null!;
    }
}
