using System;
using System.Collections.Generic;

namespace config.store.temp.Models
{
    public partial class Property
    {
        public long PropertyId { get; set; }
        public string Name { get; set; } = null!;
        public string Value { get; set; } = null!;
    }
}
