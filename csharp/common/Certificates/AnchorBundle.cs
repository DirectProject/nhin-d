/* 
 Copyright (c) 2012, Direct Project
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
using System.Linq;
using System.Text;
using System.Security.Cryptography;
using System.Security.Cryptography.X509Certificates;
using System.Security.Cryptography.Pkcs;
using Health.Direct.Common.Extensions;
using Health.Direct.Common.Web;

namespace Health.Direct.Common.Certificates
{
    /// <summary>
    /// An AnchorBundle is a Cms structure. Parses the contents of that structure. 
    /// Anchor Bundles can be:
    ///     * Raw p7b data - UNSIGNED - containing Certificates and some optional metadata.
    ///     * p7s data - SIGNED - with the ContentInfo containing the p7b data. 
    /// </summary>
    public class AnchorBundle
    {
        /// <summary>
        /// Create a new AnchorBundle by parsing raw bundle data
        /// </summary>
        /// <param name="bundleData">Raw bundle data</param>
        public AnchorBundle(byte[] bundleData)
            : this(bundleData, false)
        {
        }
        
        /// <summary>
        /// Create a new AnchorBundle by parsing raw bundle data
        /// </summary>
        /// <param name="bundleData">Raw bundle data</param>
        /// <param name="checkSignatureOnly">If bundle is signed, check signature but don't verify signature cert chain</param>
        public AnchorBundle(byte[] bundleData, bool checkSignatureOnly)
        {
            SignedCms cms = ParseCms(bundleData, checkSignatureOnly);
            if (cms != null)
            {
                this.Certificates = cms.Certificates;
                if (cms.HasContent())
                {
                    this.Metadata = ParseMetadata(cms.ContentInfo.Content);
                }
            } 
            else
            {
                this.Certificates = new X509Certificate2Collection();
            }
        }
        
        /// <summary>
        /// get/set the certificates in this bundle. 
        /// </summary>
        public X509Certificate2Collection Certificates
        {
            get; set;
        }
        
        /// <summary>
        /// Get/set the metadata in this bundle. Can be null or empty
        /// </summary>
        public string Metadata
        { 
            get; set;
        }
        
        /// <summary>
        /// Parse raw bundle data into a CMS structure
        /// </summary>
        /// <param name="bundleData">Array of bytes</param>
        /// <param name="checkSignatureOnly">If bundle is signed, check signature but don't verify signature cert chain</param>
        /// <returns>The parsed Cms structure</returns>                                                
        public static SignedCms ParseCms(byte[] bundleData, bool checkSignatureOnly)
        {
            if (bundleData.IsNullOrEmpty())
            {
                throw new ArgumentNullException("bundleData");
            }

            SignedCms cms = null;
            try
            {
                cms = DecodeDerBundle(bundleData);
            }
            catch
            {
            }
            if (cms == null)
            {
                cms = DecodePEMBundle(bundleData);
            }

            if (!cms.HasSignatures())
            {
                // No signature. Must assume is already p7b
                return cms;
            }
            
            cms.CheckSignature(checkSignatureOnly);            
            if (!cms.HasContent())
            {
                return null;
            }            
            
            SignedCms p7bCms = new SignedCms();
            p7bCms.Decode(cms.ContentInfo.Content);
            return p7bCms;
        }
        
        /// <summary>
        /// Parse raw metadata bytes into a string
        /// </summary>
        /// <param name="metadata">Raw bytes</param>
        /// <returns></returns>
        public static string ParseMetadata(byte[] metadata)
        {
            try
            {
                return Encoding.UTF8.GetString(metadata);
            }
            catch
            {
            }
            
            return null;
        }
        
        /// <summary>
        /// Export the given certificate collection as an UNSIGNED bundle
        /// </summary>
        /// <param name="certs">Certificates to place in the bundle</param>
        /// <returns>p7b data</returns>
        public static byte[] Create(X509Certificate2Collection certs)
        {
            return certs.Export(X509ContentType.Pkcs7);
        }
        
        /// <summary>
        /// Export the given certificate collection as a SIGNED bundle 
        /// </summary>
        /// <param name="certs">Certificates to place in the bundle</param>
        /// <param name="signingCert">Signing certificate</param>
        /// <returns>p7s data</returns>
        public static byte[] CreateSigned(X509Certificate2Collection certs, X509Certificate2 signingCert)
        {
            if (signingCert == null || !signingCert.HasPrivateKey)
            {
                throw new ArgumentException("signingCert");
            }
            
            byte[] p7bData = certs.Export(X509ContentType.Pkcs7);

            SignedCms cms = new SignedCms(new ContentInfo(p7bData), false);
            CmsSigner signer = new CmsSigner(signingCert);
            signer.IncludeOption = X509IncludeOption.EndCertOnly;
            cms.ComputeSignature(signer, true);

            return cms.Encode();
        }

        static SignedCms DecodeDerBundle(byte[] bytes)
        {
            SignedCms cms = new SignedCms();
            cms.Decode(bytes);
            return cms;
        }

        static SignedCms DecodePEMBundle(byte[] bytes)
        {
            byte[] cmsData = PEMDecoder.Decode(bytes);
            return DecodeDerBundle(cmsData);
        }
    }

    /// <summary>
    /// Simple parser for PEM files - just extracts base64 data
    /// </summary>
    public class PEMDecoder
    {
        const string BoundaryMarker = "-----";

        /// <summary>
        /// Extract data bytes from a PEM file, after removing PEM headers/footers
        /// </summary>
        /// <param name="bytes">PEM encoded bytes</param>
        /// <returns>Data contained</returns>
        public static byte[] Decode(byte[] bytes)
        {
            return Decode(Encoding.UTF8.GetString(bytes));
        }

        /// <summary>
        /// Decode data in PEM encoded text
        /// </summary>
        /// <param name="text">PEM text</param>
        /// <returns>Decoded data</returns>
        public static byte[] Decode(string text)
        {
            if (string.IsNullOrEmpty(text))
            {
                return null;
            }
            string boundaryText;
            int dataStartAt = ParseBoundary(text, 0, out boundaryText);
            int dataEndAt = ParseToBoundaryMarker(text, dataStartAt);
            if (dataStartAt >= dataEndAt)
            {
                ThrowInvalidPEM();
            }
            string data = text.Substring(dataStartAt, dataEndAt - dataStartAt).Trim();
            if (string.IsNullOrEmpty(data))
            {
                ThrowInvalidPEM();
            }
            return Convert.FromBase64String(data);
        }

        static int ParseBoundary(string text, int startAt, out string boundaryText)
        {
            int sectionNameStartAt = ParseToBoundaryMarker(text, startAt) + BoundaryMarker.Length;
            int sectionNameEndAt = ParseToBoundaryMarker(text, sectionNameStartAt);
            boundaryText = text.Substring(sectionNameStartAt, sectionNameEndAt - sectionNameStartAt);
            return sectionNameEndAt + BoundaryMarker.Length;
        }

        static int ParseToBoundaryMarker(string text, int startAt)
        {
            int boundaryPos = text.IndexOf(BoundaryMarker, startAt);
            if (boundaryPos < 0)
            {
                ThrowInvalidPEM();
            }
            return boundaryPos;
        }

        static void ThrowInvalidPEM()
        {
            throw new CryptographicException("Invalid PEM Encoding");
        }
    }
}
