using System.ComponentModel.DataAnnotations;

namespace AdminMvc.Models
{
    [MetadataType(typeof(AddressModel_Validation))]
    public class AddressModel
    {
        public long DomainID { get; set; }

        public string EmailAddress { get; set; }
        public string DisplayName { get; set; }
        public string Type { get; set; }
    }

    public class AddressModel_Validation
    {
        [Required(ErrorMessage = "EmailAddress is required")]
        [StringLength(400, ErrorMessage = "EmailAddress may not be longer than 400 characters")]
        [RegularExpression(@"^[A-Za-z0-9](([_\.\-]?[a-zA-Z0-9]+)*)@([A-Za-z0-9]+)(([\.\-]?[a-zA-Z0-9]+)*)\.([A-Za-z]{2,})$", ErrorMessage = "Invalid EmailAddress")]
        public string EmailAddress { get; set; }

        [StringLength(64, ErrorMessage = "Maximum length of DisplayName is 64 characters")]
        public string DisplayName { get; set; }

        [StringLength(64, ErrorMessage = "Maximum length of Type is 64 characters")]
        public string Type { get; set; }
    }
}