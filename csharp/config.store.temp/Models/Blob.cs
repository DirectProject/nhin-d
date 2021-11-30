using System;
using System.Collections.Generic;

namespace config.store.temp.Models
{
    public partial class Blob
    {
        public long BlobId { get; set; }
        public string Name { get; set; } = null!;
        public byte[] Data { get; set; } = null!;
        public DateTime CreateDate { get; set; }
        public DateTime UpdateDate { get; set; }
    }
}
