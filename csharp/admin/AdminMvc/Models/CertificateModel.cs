using System;

using Health.Direct.Config.Store;

namespace AdminMvc.Models
{
    public class CertificateModel
    {
        public long ID { get; set; }

        public string Owner { get; set; }
        public string Thumbprint { get; set; }

        public DateTime ValidStartDate { get; set; }
        public DateTime ValidEndDate { get; set; }
        public bool HasData { get; set; }

        public string Status { get; set; }
        public DateTime CreateDate { get; set; }

        public bool IsEnabled
        {
            get
            {
                return Status == EntityStatus.Enabled.ToString();
            }
        }
    }
}