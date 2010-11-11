namespace AdminMvc.Models
{
    public class AnchorModel : CertificateModel
    {
        public bool ForIncoming { get; set; }
        public bool ForOutgoing { get; set; }

        public string Purpose
        {
            get
            {
                if (ForIncoming)
                {
                    return ForOutgoing ? "In & Out" : "In";
                }

                return ForOutgoing ? "Out" : "None";
            }
        }
    }
}