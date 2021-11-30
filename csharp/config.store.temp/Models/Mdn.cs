using System;
using System.Collections.Generic;

namespace config.store.temp.Models
{
    public partial class Mdn
    {
        public string MdnIdentifier { get; set; } = null!;
        public long MdnId { get; set; }
        public string MessageId { get; set; } = null!;
        public string RecipientAddress { get; set; } = null!;
        public string SenderAddress { get; set; } = null!;
        public string? Subject { get; set; }
        public string Status { get; set; } = null!;
        public bool NotifyDispatched { get; set; }
        public DateTime CreateDate { get; set; }
    }
}
