using System;
using System.Collections.Generic;

namespace config.store.temp.Models
{
    public partial class Bundle
    {
        public long BundleId { get; set; }
        public string Owner { get; set; } = null!;
        public string Url { get; set; } = null!;
        public DateTime CreateDate { get; set; }
        public bool? ForIncoming { get; set; }
        public bool? ForOutgoing { get; set; }
        public byte Status { get; set; }
    }
}
