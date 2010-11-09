using System.ComponentModel.DataAnnotations;

namespace AdminMvc.Models
{
    [MetadataType(typeof(DomainModel_Validation))]
    public class DomainModel
    {
        public string Name { get; set; }
    }

    public class DomainModel_Validation
    {
        [Required(ErrorMessage = "Name is required")]
        [StringLength(255, ErrorMessage = "Name may not be longer than 255 characters")]
        [RegularExpression(@"^([A-Za-z0-9]+)(([\.\-]?[a-zA-Z0-9]+)*)\.([A-Za-z]{2,})$", ErrorMessage = "Name must be a domain name")]
        public string Name { get; set; }
    }
}