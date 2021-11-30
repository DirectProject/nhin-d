using System;
using System.Collections.Generic;
using Health.Direct.Config.Store;

namespace config.store.temp.Models
{
    public partial class Domain
    {
        public Domain()
        {
            Addresses = new HashSet<Address>();
        }

        public string DomainName { get; set; } = null!;
        public string? AgentName { get; set; }
        public long DomainId { get; set; }
        public DateTime CreateDate { get; set; }
        public DateTime UpdateDate { get; set; }
        public byte Status { get; set; }
        public SecurityStandard SecurityStandard { get; set; }

        public virtual ICollection<Address> Addresses { get; set; }
    }
}
