/* 
 Copyright (c) 2010, Direct Project
 All rights reserved.

 Authors:
    Arien Malec
  
Redistribution and use in source and binary forms, with or without modification, are permitted provided that the following conditions are met:

Redistributions of source code must retain the above copyright notice, this list of conditions and the following disclaimer.
Redistributions in binary form must reproduce the above copyright notice, this list of conditions and the following disclaimer in the documentation and/or other materials provided with the distribution.
Neither the name of The Direct Project (directproject.org) nor the names of its contributors may be used to endorse or promote products derived from this software without specific prior written permission.
THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 
*/
using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Xml.Linq;

using Health.Direct.Common.Metadata;
using Health.Direct.Xd;
using Ionic.Zip;
using System.IO;

namespace Health.Direct.Xdm
{
    public class XDMZipPackager : IPackager<ZipFile>
    {
        // Use Default
        private XDMZipPackager() { }


        private static readonly XDMZipPackager m_Instance = new XDMZipPackager();

        /// <summary>
        /// The default instance
        /// </summary>
        public static XDMZipPackager Default
        {
            get
            {
                return m_Instance;
            }
        }

        /// <summary>
        /// Unpackages an XDM-encoded zip file
        /// </summary>
        public DocumentPackage Unpackage(ZipFile z)
        {
            DocumentPackage package;
            package = ReadMetadata(z);
            return package;
        }

        private DocumentPackage ReadMetadata(ZipFile z)
        {
            ZipEntry metadataEntry = LocateMetadataFile(z);
            XDocument metadataDoc = ExtractMetadataFile(metadataEntry);
            DocumentPackage package = XDMetadataConsumer.Consume(metadataDoc.Root);
            string[] dirParts = metadataEntry.FileName.Split('/');
            string submissionSetDir = String.Format("{0}/{1}", dirParts[0], dirParts[1]);
            foreach (DocumentMetadata doc in package.Documents)
            {
                string docPath = String.Format("{0}/{1}", submissionSetDir, doc.Uri);
                byte[] bytes = ExtractDocumentBytes(z, docPath);
                doc.SetDocument(bytes);
            }

            return package;
        }

        private byte[] ExtractDocumentBytes(ZipFile z, string path)
        {
            ZipEntry docEntry = z[path];
            if (docEntry == null) throw new XdmException(XdmError.FileNotFound, String.Format("File {0} was not located in the archive", path));
            using (MemoryStream stream = new MemoryStream())
            {
                docEntry.Extract(stream);
                stream.Seek(0, SeekOrigin.Begin);
                return stream.ReadAllBytes();
            }
        }

        private XDocument ExtractMetadataFile(ZipEntry e)
        {
            XDocument metadataDoc;
            using (MemoryStream docStream = new MemoryStream())
            {
                e.Extract(docStream);
                docStream.Seek(0, SeekOrigin.Begin);
                using (TextReader reader = new StreamReader(docStream))
                {
                    metadataDoc = XDocument.Load(reader);
                }
            }
            return metadataDoc;
        }


        private ZipEntry LocateMetadataFile(ZipFile z)
        {
            IEnumerable<ZipEntry> subFiles = z.Entries.Where(e => e.FileName.StartsWith(XDMStandard.MainDirectory));
            IEnumerable<ZipEntry> metadataFiles = subFiles
                .Where(e => e.FileName.EndsWith(XDMStandard.MetadataFilename) &&
                    e.FileName.Split('/').Count() == 3);
            if (metadataFiles.Count() == 0) throw new XdmException(XdmError.NoMetadataFile);
            if (metadataFiles.Count() > 1) throw new NotImplementedException("Multiple submission sets not supported");
            return metadataFiles.First();
        }



        /// <summary>
        /// Packages a <see cref="DocumentPackage"/> as an XDM zip file
        /// </summary>
        public ZipFile Package(DocumentPackage package)
        {
            ZipFile z = new ZipFile();

            AddDocuments(package, z); // Alters URI by side effect
            AddManifests(package, z);
            AddMetadata(package, z);

            return z;
        }

        private void AddManifests(DocumentPackage package, ZipFile z)
        {
            AddIndex(package, z);
            AddReadme(z);
        }

        private void AddReadme(ZipFile z)
        {
            z.AddEntry(XDMStandard.ReadmeFilename, XDMStandard.ReadmeFileString);
        }

        private void AddIndex(DocumentPackage package, ZipFile z)
        {
            z.AddEntry(XDMStandard.IndexHtmFile, GenerateIndexFile(package));
        }

        private void AddMetadata(DocumentPackage package, ZipFile z)
        {
            
            StringBuilder sb = new StringBuilder();
            using (StringWriter w = new StringWriter(sb))
            {
                package.Generate().Save(w);
            }

            z.AddEntry(XDMStandard.DefaultMetadataFilePath, sb.ToString());
        }

        private void AddDocuments(DocumentPackage package, ZipFile z)
        {
            int i = 1;
            foreach (DocumentMetadata doc in package.Documents)
            {
                if (doc.DocumentBytes == null) throw new XdMetadataException(XdError.MissingDocumentBytes);
                string suffix = i.ToString("000");
                string name = XDMStandard.DocPrefix + suffix;
                string path = String.Format("{0}/{1}/{2}", XDMStandard.MainDirectory, XDMStandard.DefaultSubmissionSet, name);
                doc.Uri = name;
                z.AddEntry(path, doc.DocumentBytes);
            }
        }

        private string GenerateIndexFile(DocumentPackage package)
        {
            var liElts = from d in package.Documents
                         select new XElement("li",
                             new XElement("a", d.Title,
                                 new XAttribute("href", String.Format("{0}/{1}/{2}", XDMStandard.MainDirectory, XDMStandard.DefaultSubmissionSet, d.Uri))));
            XDocument index = new XDocument(
                new XDocumentType("html", "-//W3C//DTD XHTML Basic 1.1//EN", "http://www.w3.org/TR/xhtml-basic/xhtml-basic11.dtd", null),
                new XElement("html",
                    new XElement("head",
                        new XElement("title", "Content index")),
                    new XElement("body",
                        new XElement("h2", "Content index"),
                        new XElement("ul", liElts))));

            return index.ToString();
        }



    }
}
