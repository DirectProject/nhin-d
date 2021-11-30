using System;
using System.Collections.Generic;

namespace config.store.temp.Models
{
    public partial class Certificate
    {
        public string Owner { get; set; } = null!;
        public string Thumbprint { get; set; } = null!;
        public long CertificateId { get; set; }
        public DateTime CreateDate { get; set; }
        public byte[] CertificateData { get; set; } = null!;
        public DateTime ValidStartDate { get; set; }
        public DateTime ValidEndDate { get; set; }
        public byte Status { get; set; }
    }
}
