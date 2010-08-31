/* 
 Copyright (c) 2010, NHIN Direct Project
 All rights reserved.

 Authors:
    Umesh Madan     umeshma@microsoft.com
  
Redistribution and use in source and binary forms, with or without modification, are permitted provided that the following conditions are met:

Redistributions of source code must retain the above copyright notice, this list of conditions and the following disclaimer.
Redistributions in binary form must reproduce the above copyright notice, this list of conditions and the following disclaimer in the documentation and/or other materials provided with the distribution.
Neither the name of the The NHIN Direct Project (nhindirect.org). nor the names of its contributors may be used to endorse or promote products derived from this software without specific prior written permission.
THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 
*/
using System;
using System.Collections;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.IO;
using System.Net.Mail;
using System.Net.Mime;
using System.Security.Cryptography;
using System.Security.Cryptography.Pkcs;
using System.Security.Cryptography.X509Certificates;
using NHINDirect.Certificates;

namespace NHINDirect.Cryptography
{
    public static class Extensions
    {
        //---------------------------------------
        //
        // SignerInfoCollection extensions (PKS7)
        //
        //---------------------------------------        
        public static int IndexOf(this SignerInfoCollection signers, Predicate<SignerInfo> matcher)
        {
            if (matcher == null)
            {
                throw new ArgumentNullException();
            }
            
            for (int i = 0, count = signers.Count; i < count; ++i)
            {
                if (matcher(signers[i]))
                {
                    return i;
                }
            }

            return -1;
        }

        public static SignerInfo Find(this SignerInfoCollection signers, Predicate<SignerInfo> matcher)
        {
            int index = signers.IndexOf(matcher);
            if (index >= 0)
            {
                return signers[index];
            }

            return null;
        }

        public static SignerInfo FindByName(this SignerInfoCollection signers, string name)
        {
            if (string.IsNullOrEmpty(name))
            {
                throw new ArgumentException();
            }

            return signers.Find(x => (x.Certificate.MatchName(name) || x.Certificate.MatchEmailName(name)));
        }

        public static SignerInfo FindByThumbprint(this SignerInfoCollection signers, string thumbprint)
        {
            if (string.IsNullOrEmpty(thumbprint))
            {
                throw new ArgumentException();
            }

            return signers.Find(x => x.Certificate.Thumbprint == thumbprint);
        }

        //---------------------------------------
        //
        // ContentType
        //
        //---------------------------------------        
        public static bool IsCms(this ContentType contentType)
        {
            return SMIMEStandard.IsContentCms(contentType);
        }
        
        public static bool IsEncrypted(this ContentType contentType)
        {
            return SMIMEStandard.IsContentEncrypted(contentType);
        }
        
        public static bool IsEnvelopedSignature(this ContentType contentType)
        {
            return SMIMEStandard.IsContentEnvelopedSignature(contentType);
        }
        
        public static bool IsMultipartSignature(this ContentType contentType)
        {
            return SMIMEStandard.IsContentMultipartSignature(contentType);
        }
        
        public static bool IsDetachedSignature(this ContentType contentType)
        {
            return SMIMEStandard.IsContentDetachedSignature(contentType);
        }
        
        public static string AsString(this DigestAlgorithm algorithm)
        {
            return SMIMEStandard.AsString(algorithm);
        }
        
        public static AlgorithmIdentifier AsAlgorithmIdentifier(this EncryptionAlgorithm algorithm)
        {
            return SMIMECryptographer.ToAlgorithmID(algorithm);
        }
    }
 }
