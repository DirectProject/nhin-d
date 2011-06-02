/* 
 Copyright (c) 2010, Direct Project
 All rights reserved.

 Authors:
    Vassil Peytchev     vassil@epic.com
  
Redistribution and use in source and binary forms, with or without modification, are permitted provided that the following conditions are met:

Redistributions of source code must retain the above copyright notice, this list of conditions and the following disclaimer.
Redistributions in binary form must reproduce the above copyright notice, this list of conditions and the following disclaimer in the documentation and/or other materials provided with the distribution.
Neither the name of the The Direct Project (nhindirect.org). nor the names of its contributors may be used to endorse or promote products derived from this software without specific prior written permission.
THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 
*/
using System.Xml;
using System.IO;

namespace Health.Direct.Xd.Common
{
    public class Content
    {
        public enum TypeOfContent
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