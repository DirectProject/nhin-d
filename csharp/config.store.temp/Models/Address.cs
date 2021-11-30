using System;
using System.Collections.Generic;

namespace config.store.temp.Models
{
    public partial class Address
    {
        public string EmailAddress { get; set; } = null!;
        public long AddressId { get; set; }
        public long DomainId { get; set; }
        public string DisplayName { get; set; } = null!;
        public DateTime CreateDate { get; set; }
        public DateTime UpdateDate { get; set; }
        public string? Type { get; set; }
        public byte Status { get; set; }

        public virtual Domain Domain { get; set; } = null!;
    }
}
