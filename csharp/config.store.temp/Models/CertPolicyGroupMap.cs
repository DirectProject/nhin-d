using System;
using System.Collections.Generic;

namespace config.store.temp.Models
{
    public partial class CertPolicyGroupMap
    {
        public long MapId { get; set; }
        public long CertPolicyGroupId { get; set; }
        public long CertPolicyId { get; set; }
        public byte PolicyUse { get; set; }
        public bool? ForIncoming { get; set; }
        public bool? ForOutgoing { get; set; }
        public DateTime CreateDate { get; set; }

        public virtual CertPolicy CertPolicy { get; set; } = null!;
        public virtual CertPolicyGroup CertPolicyGroup { get; set; } = null!;
    }
}
