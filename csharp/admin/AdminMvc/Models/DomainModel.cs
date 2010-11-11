using System;
using System.ComponentModel.DataAnnotations;

using Health.Direct.Config.Store;

namespace AdminMvc.Models
{
    [MetadataType(typeof(DomainModel_Validation))]
    public class DomainModel
    {
        public long ID { get; set; }
        public string Name { get; set; }
        public DateTime CreateDate { get; set; }
        public DateTime UpdateDate { get; set; }
        public string Status { get; set; }

        public bool IsEnabled
        {
            get { return Status == EntityStatus.Enabled.ToString(); }
        }
    }

    public class DomainModel_Validation
    {
        [Required(ErrorMessage = "Name is required")]
        [StringLength(255, ErrorMessage = "Name may not be longer than 255 characters")]
        [RegularExpression(@"^([A-Za-z0-9]+)(([\.\-]?[a-zA-Z0-9]+)*)\.([A-Za-z]{2,})$", ErrorMessage = "Name must be a domain name")]
        public string Name { get; set; }
    }
}