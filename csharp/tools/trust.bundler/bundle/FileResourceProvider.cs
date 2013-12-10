/* 
 Copyright (c) 2013, Direct Project
 All rights reserved.

 Authors:
    Joe Shook     jshook@kryptiq.com
 
Redistribution and use in source and binary forms, with or without modification, are permitted provided that the following conditions are met:

Redistributions of source code must retain the above copyright notice, this list of conditions and the following disclaimer.
Redistributions in binary form must reproduce the above copyright notice, this list of conditions and the following disclaimer in the documentation and/or other materials provided with the distribution.
Neither the name of The Direct Project (directproject.org) nor the names of its contributors may be used to endorse or promote products derived from this software without specific prior written permission.
THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 
*/

using System;
using System.Collections;
using System.Collections.Generic;
using System.IO;
using System.Linq;
using Org.BouncyCastle.X509;

namespace Health.Direct.Trust
{
    /// <summary>
    /// File system implementation of IResourceProvider 
    /// </summary>
    public class FileResourceProvider : IResourceProvider
    {
        private readonly string _source;
        private readonly string _destination;
        private readonly string[] _ignore;
        private readonly string _metadata;

        /// <summary>
        /// Create a new FileResourceProvider by supplying required resources
        /// </summary>
        /// <param name="source">Folder loacation of the anchor certificates</param>
        /// <param name="destination">Trust bundle filename</param>
        /// <param name="ignore">Optional array of sub-strings to ignore.</param>
        /// <param name="metadata">Optional metadata</param>
        public FileResourceProvider(string source, string destination, string[] ignore = null, string metadata = null)
        {
            _source = source;
            _destination = destination;
            _ignore = ignore;
            _metadata = metadata;
        }

        public string Source
        {
            get { return _source; }
        }

        public string Destination
        {
            get { return _destination; }
        }

        public string[] Ignore
        {
            get { return _ignore; }
        }

        public string Metadata
        {
            get { return _metadata; }
        }

        public IList LoadCertificates()
        {
            if (!Directory.Exists(_source))
            {
                throw new DirectoryNotFoundException("Directory not found: " + _source);
            }

            List<X509Certificate> certList = new List<X509Certificate>();

            string[] files = Directory.GetFiles(_source);
            List<string> failures = new List<string>();
            foreach (string file in files)
            {
                try
                {
                    if (SkipResource(_ignore, file)) continue;
                    X509CertificateParser certParser = new X509CertificateParser();
                    using (Stream stream = new FileStream(file, FileMode.Open))
                    {
                        var certs = certParser.ReadCertificates(stream);
                        stream.Close();
                        foreach (var cert in certs)
                        {
                            certList.Add(cert as X509Certificate);
                        }
                    }
                }
                catch (Exception e)
                {
                    failures.Add(string.Format("Failed loading file {0}\r\n{1}", file, e.Message));
                }
            }
            if (failures.Count > 0)
            {
                string result = failures.Aggregate((current, f) => current + f + "\r\n");
                throw new Exception(result);
            }
            return certList;
        }

        public void StoreBundle(byte[] cmsData)
        {
            File.WriteAllBytes(Destination, cmsData);
        }


        private static bool SkipResource(IEnumerable<string> ignore, string file)
        {
            if (ignore == null)
            {
                return false;
            }

            bool flag = false;
            foreach (string ignoreString in ignore)
            {
                if (file.IndexOf(ignoreString, StringComparison.OrdinalIgnoreCase) > 0)
                {
                    flag = true;
                }
            }
            return flag;
        }
    }
}