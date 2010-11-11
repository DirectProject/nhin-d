using System;
using System.Collections.Generic;
using System.ComponentModel.DataAnnotations;
using System.Linq;
using System.Web.Mvc;

namespace AdminMvc.Models
{
    [MetadataType(typeof(CertificateUploadModel_Validation))]
    public class AnchorUploadModel : CertificateUploadModel
    {
        public PurposeType Purpose { get; set; }

        public static SelectList PurposeTypeList
        {
            get
            {
                var enumValues = Enum.GetValues(typeof(PurposeType)).Cast<PurposeType>()
                                                             .Select(e => new KeyValuePair<int, string>((byte)e, e.ToString()));
                return new SelectList(enumValues, "Key", "Value");
            }
        }
    }

    [Flags]
    public enum PurposeType
    {
        Incoming = 1,
        Outgoing = 2,
        Both = 3
    }
}