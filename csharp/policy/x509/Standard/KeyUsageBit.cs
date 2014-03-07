/* 
 Copyright (c) 2013, Direct Project
 All rights reserved.

 Authors:
    Joe Shook      jshook@kryptiq.com
  
Redistribution and use in source and binary forms, with or without modification, are permitted provided that the following conditions are met:

Redistributions of source code must retain the above copyright notice, this list of conditions and the following disclaimer.
Redistributions in binary form must reproduce the above copyright notice, this list of conditions and the following disclaimer in the documentation and/or other materials provided with the distribution.
Neither the name of The Direct Project (directproject.org) nor the names of its contributors may be used to endorse or promote products derived from this software without specific prior written permission.
THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 
*/

using System;

namespace Health.Direct.Policy.X509.Standard
{
    /// <summary>
    /// Bit strings defined for KeyUsage in RFC 5280 section 4.2.1.3
    /// </summary>
    [Flags]
    public enum KeyUsageBit
    {
        /// <summary>
        /// Digital signature
        /// Binary: 1000 0000
        /// Hex: 0x80
        /// Dec: 128
        /// </summary>
        DigitalSignature = (1 << 7),



        /// <summary>
        /// Non repudiation
        /// Binary: 1000000
        /// Hex: 0x40
        /// Dec: 64
        /// </summary>
        NonRepudiation = (1 << 6),

        /// <summary>
        /// Key encipherment
        /// Binary: 100000
        /// Hex: 0x20
        /// Dec: 32
        /// </summary>
        KeyEncipherment = (1 << 5),

        /// <summary>
        /// Data encipherment
        /// Binary: 10000
        /// Hex: 0x10
        /// Dec: 16
        /// </summary>
        DataEncipherment = (1 << 4),

        /// <summary>
        /// Key agreement
        /// Binary: 100
        /// Hex: 0x08
        /// Dec: 8
        /// </summary>
        KeyAgreement = (1 << 3),

        /// <summary>
        /// Certificate signing
        /// Binary: 100
        /// Hex: 0x04
        /// Dec: 4
        /// </summary>
        KeyCertSign = (1 << 2),

        /// <summary>
        /// CRL signing
        /// Binary: 10
        /// Hex: 0x02
        /// Dec: 2
        /// </summary>
        CrlSign = (1 << 1),

        /// <summary>
        /// Encipherment only
        /// Binary: 1
        /// Hex: 0x01
        /// Dec: 1
        /// </summary>
        EncipherOnly = (1 << 0),

        /// <summary>
        /// Dicipherment only
        /// Binary: 1000000000000000
        /// Hex: 0x8000
        /// Dec: 32768
        /// </summary>
        DecipherOnly = (1 << 15),



    }


}
