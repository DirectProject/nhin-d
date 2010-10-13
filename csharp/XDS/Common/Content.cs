using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Xml;
using System.IO;

namespace NHINDirect.XDS.Common
{
    public class Content
    {
        public static enum TypeOfContent
        {
            CDA = 1,
            CCD,
            HL7v2,
            X12,
            PDF,
            Unstructured
        }

        private byte[] _contentBytes;
        private XmlDocument _contentXml = null;
        private TypeOfContent _contentType;
        private string _encoding;

        public Content(byte[] contentBytes, TypeOfContent contentType)
        {
            _contentBytes = contentBytes;
            _encoding = "application/octet-stream";
            switch (contentType)
            {
                case TypeOfContent.CDA:
                case TypeOfContent.CCD:
                    _contentXml = new XmlDocument();
                    MemoryStream contentStream = new MemoryStream();
                    contentStream.Write(contentBytes, 0, contentBytes.Length);
                    contentStream.Position = 0; //reset memory stream
                    _contentXml.Load(contentStream);
                    _encoding = "text/xml";
                    break;
                case TypeOfContent.PDF:
                    _encoding = "application/pdf";
                    break;
                //TODO and HL7 V2.x, and X12 formatting
                default:
                    break;
                
            }
        }
        
        #region Properties
        public byte[] ContentBytes
        {
            get { return _contentBytes; }
			set { _contentBytes = value; }
		}

        public XmlDocument ContentXml
        {
            get { return _contentXml; }
            set { _contentXml = value; }
        }

        public TypeOfContent ContentType
        {
            get { return _contentType; }
            set { _contentType = value; }
        }

        public string Encoding
        {
            get { return _encoding; }
            set { _encoding = value; }
        }

		#endregion Properties
    }
}
