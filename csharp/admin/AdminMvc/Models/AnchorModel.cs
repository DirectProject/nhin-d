using System.ComponentModel.DataAnnotations;

namespace AdminMvc.Models
{
    [MetadataType(typeof(AnchorModel_Validation))]
    public class AnchorModel : CertificateModel
    {
        public bool ForIncoming { get; set; }
        public bool ForOutgoing { get; set; }
    }

    public class AnchorModel_Validation : CertificateModel_Validation
    {
    }
}