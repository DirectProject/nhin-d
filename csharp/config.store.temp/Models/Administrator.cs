using System;
using System.Collections.Generic;

namespace config.store.temp.Models
{
    public partial class Administrator
    {
        public long AdministratorId { get; set; }
        public string Username { get; set; } = null!;
        public string PasswordHash { get; set; } = null!;
        public DateTime CreateDate { get; set; }
        public DateTime UpdateDate { get; set; }
        public byte Status { get; set; }
    }
}
