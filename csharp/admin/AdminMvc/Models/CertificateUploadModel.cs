using System.ComponentModel.DataAnnotations;

namespace AdminMvc.Models
{
    [MetadataType(typeof(CertificateUploadModel_Validation))]
    public class CertificateUploadModel
    {
        public string Owner { get; set; }
        public string Password { get; set; }
        public string PasswordConfirm { get; set; }
    }

    public class CertificateUploadModel_Validation
    {
    }
}