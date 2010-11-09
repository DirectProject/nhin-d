using System;
using System.ComponentModel.DataAnnotations;

using Health.Direct.Config.Store;

namespace AdminMvc.Models
{
    [MetadataType(typeof(AnchorModel_Validation))]
    public class AnchorModel
    {
        public string Owner { get; set; }

        public string Thumbprint { get; set; }
        public string ValidStartDate { get; set; }
        public string ValidEndDate { get; set; }
        public bool ForIncoming { get; set; }
        public bool ForOutgoing { get; set; }
        public bool HasData { get; set; }

        public string Status { get; set; }
        public DateTime CreateDate { get; set; }
        public DateTime UpdateDate { get; set; }

        public bool IsEnabled
        {
            get
            {
                return Status == EntityStatus.Enabled.ToString();
            }
        }
    }

    public class AnchorModel_Validation
    {
    }

}