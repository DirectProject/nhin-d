/* 
 Copyright (c) 2013, Direct Project
 All rights reserved.

 Authors:
    Umesh Madan     umeshma@microsoft.com
  
Redistribution and use in source and binary forms, with or without modification, are permitted provided that the following conditions are met:

Redistributions of source code must retain the above copyright notice, this list of conditions and the following disclaimer.
Redistributions in binary form must reproduce the above copyright notice, this list of conditions and the following disclaimer in the documentation and/or other materials provided with the distribution.
Neither the name of The Direct Project (directproject.org) nor the names of its contributors may be used to endorse or promote products derived from this software without specific prior written permission.
THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 
*/
using System;
using System.Collections.Generic;
using System.Collections.Specialized;
using System.Linq;
using System.Text;
using System.Net.Mime;
using System.Net.Mail;
using Health.Direct.Common.Mime;
using Health.Direct.Common.BlueButton;
using Xunit;
using Xunit.Extensions;

namespace Health.Direct.Common.Tests.Mime
{
    public class MimeFieldsFacts
    {
        public static IEnumerable<object[]> ContentTypes
        {
            get
            {
                yield return new object[] { 
                    new ContentType {
                        MediaType = MediaTypeNames.Text.Plain,
                        Name = "FooFile.txt",
                        CharSet = "us-ascii"
                    },
                    3
                };
                
                ContentType c = new ContentType("message/partial");
                c.Name = "Bingo";
                c.Parameters["id"] = "28a3sesd313@xyx.pqr";
                c.Parameters["number"] = "1";
                c.Parameters["quoted"] = "(foo<bar?q";
                c.Parameters["total"] = "3";
                yield return new object[] {c, 6};
            }
        }
        
        public static IEnumerable<object[]> ContentDispositions
        {
            get
            {
                DateTime now = DateTime.Now;
                ContentDisposition cd = new ContentDisposition {
                        DispositionType = "attachment",
                        FileName = "goobar.txt",
                        CreationDate = now,
                        ReadDate = now.AddDays(5),
                        ModificationDate = now.AddDays(3),    
                        Size = 123456789                    
                    };
                cd.Parameters["XYZ"] = "pqr/?.<";
                yield return new object[] { 
                    cd, 7
                };
            }
        }
                
        [Theory]
        [PropertyData("ContentTypes")]
        public void TestContentType(ContentType contentType, int paramCount)
        {
            string fieldText = contentType.ToString();
            
            MimeFieldParameters fieldParams = new MimeFieldParameters();
            Assert.DoesNotThrow(() => fieldParams.Deserialize(fieldText));
            Assert.True(fieldParams.Count == paramCount);
            
            Assert.Equal(contentType.MediaType, fieldParams[0].Value);
            Assert.Equal<string>(contentType.Name, fieldParams["name"]);
            
            Assert.DoesNotThrow(() => Compare(fieldParams, contentType.Parameters));
            
            string fieldTextSerialized = null;
            Assert.DoesNotThrow(() => fieldTextSerialized = fieldParams.Serialize());
            
            fieldParams.Clear();
            Assert.DoesNotThrow(() => fieldParams.Deserialize(fieldTextSerialized));
            Assert.True(fieldParams.Count == paramCount);
            
            Assert.DoesNotThrow(() => new ContentType(fieldTextSerialized));            
        }
        
        [Theory]
        [PropertyData("ContentDispositions")]
        public void TestDisposition(ContentDisposition disposition, int paramCount)
        {
            string fieldText = disposition.ToString();

            MimeFieldParameters fieldParams = new MimeFieldParameters();
            Assert.DoesNotThrow(() => fieldParams.Deserialize(fieldText));
            Assert.True(fieldParams.Count == paramCount);

            Assert.Equal(disposition.DispositionType, fieldParams[0].Value);
            Assert.Equal(disposition.FileName, fieldParams["filename"]);
            
            Assert.DoesNotThrow(() => Compare(fieldParams, disposition.Parameters));
            
            string fieldTextSerialized = null;
            Assert.DoesNotThrow(() => fieldTextSerialized = fieldParams.Serialize());

            fieldParams.Clear();
            Assert.DoesNotThrow(() => fieldParams.Deserialize(fieldTextSerialized));
            Assert.True(fieldParams.Count == paramCount);

            Assert.DoesNotThrow(() => new ContentDisposition(fieldTextSerialized));
        }

        public static IEnumerable<KeyValuePair<string, string>> DocumentSourcesText
        {
            get
            {
                yield return new KeyValuePair<string, string>("mixedDocument", DocumentSource.StandardSources.Mixed);
                yield return new KeyValuePair<string, string>("unknownDoc", DocumentSource.StandardSources.Unknown);
                yield return new KeyValuePair<string, string>("patientDoc", DocumentSource.StandardSources.Patient);
            }
        }

        public static IEnumerable<KeyValuePair<string, MailAddress>> DocumentSourcesMailAddress
        {
            get
            {
                yield return new KeyValuePair<string, MailAddress>("emailDoc", new MailAddress("Toby McDuff <toby@direct.healthvault.com>"));
                yield return new KeyValuePair<string, MailAddress>("emailDoc", new MailAddress("Toby McDuff <toby@direct.healthvault.com>"));
                yield return new KeyValuePair<string, MailAddress>("emailDoc2", new MailAddress("<biff.hooper@direct.healthvault.com>"));
                yield return new KeyValuePair<string, MailAddress>("emailDoc3", new MailAddress("<frank.hardy@direct.healthvault.com>"));
            }
        }
                
        [Fact]
        public void TestBlueBluttonSerialization()
        {            
            RequestContext context = this.CreateContext();
            
            MimeEntity entity = null;
            Assert.DoesNotThrow(() => entity = context.ToMimeEntity());
            
            string entityText = null;            
            Assert.DoesNotThrow(() => entityText = entity.ToString());
            
            MimeEntity parsedEntity = null;
            Assert.DoesNotThrow(() => parsedEntity = MimeSerializer.Default.Deserialize<MimeEntity>(entityText));

            Attachment attachment = null;
            Assert.DoesNotThrow(() => attachment = context.ToAttachment());
            Assert.True(attachment.ContentType.MediaType == MediaTypeNames.Text.Plain);
            Assert.True(attachment.ContentDisposition.FileName == RequestContext.AttachmentName);
            
            string attachmentBody = attachment.StringContent();
            Assert.True(attachmentBody.Contains(RequestContext.FieldNames.DocumentSource));
            
            RequestContext contextParsed = new RequestContext(parsedEntity.Body.Text);
            this.Compare(context.GetDocumentSources(), contextParsed.GetDocumentSources());
        }
        
        [Fact]        
        public void TestBlueButtonSources()
        {
            RequestContext context = this.CreateContext();
            List<DocumentSource> ds = null;
            
            Assert.DoesNotThrow(() => ds = context.GetDocumentSources().ToList());

            List<DocumentSource> matches = null;            
            Assert.DoesNotThrow(() => {
                matches = (
                from doc in ds
                where doc.IsSourceForDocument("emailDoc")
                select doc
                ).ToList();
            });    
            Assert.True(matches.Count == 2);
            
            int matchCount = matches.Count;                    
            Assert.DoesNotThrow(() => context.RemoveDocumentSources());
            Assert.True(context.Count == (matchCount -2));
        }
        
        RequestContext CreateContext()
        {
            RequestContext context = new RequestContext();
            this.AddTextSources(context);
            this.AddMailSources(context);
            return context;
        }
        
        void AddTextSources(RequestContext context)
        {        
            foreach(KeyValuePair<string, string> kv in DocumentSourcesText)
            {
                Assert.DoesNotThrow(() => context.Add(new DocumentSource(kv.Key, kv.Value)));
            }
        } 

        void AddMailSources(RequestContext context)
        {    
            foreach (KeyValuePair<string, MailAddress> kv in DocumentSourcesMailAddress)
            {
                Assert.DoesNotThrow(() => context.Add(new DocumentSource(kv.Key, kv.Value)));
            }
        }
        
        void Compare(MimeFieldParameters mfp, StringDictionary pd)
        {   
            foreach(string key in pd.Keys)
            {
                Assert.Equal(pd[key], mfp[key]);
            }
        } 
        
        void Compare(IEnumerable<DocumentSource> x, IEnumerable<DocumentSource> y)
        {
            List<DocumentSource> xl = null;
            List<DocumentSource> yl = null;
            
            Assert.DoesNotThrow(() => xl = x.ToList());
            Assert.DoesNotThrow(() => yl = y.ToList());
            Assert.True(xl.Count == yl.Count);
            
            for (int i = 0, count = xl.Count; i < count; ++i)
            {
                Assert.True(xl[i].CompareTo(yl[i]) == 0);
            }
        }
    }
}
