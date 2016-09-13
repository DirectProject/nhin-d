/* 
 Copyright (c) 2016, Direct Project
 All rights reserved.

 Authors:
    Joseph Shook    Joseph.Shook@Surescripts.com
  
Redistribution and use in source and binary forms, with or without modification, are permitted provided that the following conditions are met:

Redistributions of source code must retain the above copyright notice, this list of conditions and the following disclaimer.
Redistributions in binary form must reproduce the above copyright notice, this list of conditions and the following disclaimer in the documentation and/or other materials provided with the distribution.
Neither the name of The Direct Project (directproject.org) nor the names of its contributors may be used to endorse or promote products derived from this software without specific prior written permission.
THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 
*/

using System;
using Org.BouncyCastle.Crypto.Parameters;

namespace Health.Direct.Config.Console
{
    /// <summary>
    /// Pkcs#11 public key and certificate container
    /// </summary>
    public class Pkcs11PublicKey
    {
        public string Id { get; }

        public string Label { get; }

        public byte[] Data { get; }

        public RsaKeyParameters PublicKey { get; }


        /// <summary>
        /// 
        /// </summary>
        /// <param name="id">Value of CKA_ID attribute</param>
        /// <param name="label">Value of CKA_LABEL attribute.</param>
        /// <param name="publicKey"><see cref="RsaKeyParameters"/> of public Modulus and public Exponent represent the Public part of the key or null</param>
        internal Pkcs11PublicKey(string id, string label, RsaKeyParameters publicKey)
        {
            Id = id;
            Label = label;
            PublicKey = publicKey;
        }


        internal Pkcs11PublicKey(string id, string label, byte[] data)
        {
            if (data == null)
                throw new ArgumentNullException(nameof(data));
            
            Id = id;
            Label = label;
            Data = data;
            
        }
    }
}