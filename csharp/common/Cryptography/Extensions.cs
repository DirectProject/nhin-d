﻿/* 
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
using System.Security.Cryptography;
using System.Security.Cryptography.Pkcs;
using System.Security.Cryptography.X509Certificates;
using NHINDirect.Certificates;

namespace NHINDirect.Cryptography
{
    /// <summary>
    /// Extension methods for cryto
    /// </summary>
    public static class Extensions
    {
        //---------------------------------------
        //
        // SignerInfoCollection extensions (PKS7)
        //
        //---------------------------------------   
        /// <summary>
        /// Returns the first index position of this colleciton of signers matching <paramref name="matcher"/>
        /// </summary>
        /// <param name="signers">This collection to search</param>
        /// <param name="matcher">The predicate to match elements against.</param>
        /// <returns>The zero-based index of the first matching <see cref="SignerInfo"/> or -1 if no elements match</returns>
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

        /// <summary>
        /// Returns the first element of this colleciton of signers matching <paramref name="matcher"/>
        /// </summary>
        /// <param name="signers">This collection to search</param>
        /// <param name="matcher">The predicate to match elements against.</param>
        /// <returns>The first matching <see cref="SignerInfo"/> or null if no elements match</returns>
        public static SignerInfo Find(this SignerInfoCollection signers, Predicate<SignerInfo> matcher)
        {
            int index = signers.IndexOf(matcher);
            if (index >= 0)
            {
                return signers[index];
            }

            return null;
        }

        //TODO: doesn't match or use the logic for X509Certifiate2.MatchEmailNameOrName. Why?
        /// <summary>
        /// Searches this collection for the first signature whose certificate has subject name or email <paramref name="name"/>
        /// </summary>
        /// <param name="signers">This collection to search</param>
        /// <param name="name">The subject name or email to match against.</param>
        /// <returns>The first matching <see cref="SignerInfo"/> or null if no elements match</returns>
        public static SignerInfo FindByName(this SignerInfoCollection signers, string name)
        {
            if (string.IsNullOrEmpty(name))
            {
                throw new ArgumentException();
            }

            return signers.Find(x => (x.Certificate.MatchName(name) || x.Certificate.MatchEmailName(name)));
        }

        /// <summary>
        /// Searches this collection for the first signature whose certificate has <paramref name="thumprint"/>
        /// </summary>
        /// <param name="signers">This collection to search</param>
        /// <param name="thumbprint">The certificate thumbprint to match against.</param>
        /// <returns>The first matching <see cref="SignerInfo"/> or null if no elements match</returns>
        public static SignerInfo FindByThumbprint(this SignerInfoCollection signers, string thumbprint)
        {
            if (string.IsNullOrEmpty(thumbprint))
            {
                throw new ArgumentException();
            }

            return signers.Find(x => x.Certificate.Thumbprint == thumbprint);
        }
    }
 }
