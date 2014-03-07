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
using Health.Direct.Policy.X509.Standard;

namespace Health.Direct.Policy.X509
{
    /// <summary>
    /// Helper methods for Standard namespace
    /// </summary>
    public static class StandardExt
    {
        internal static string GeneralNameOtherName = "otherName";
        internal static string RFC822Name = "rfc822";
        internal static string DNSName = "dns";
        internal static string X400Address = "x400Address";
        internal static string DirectoryName = "directory";
        internal static string EdiPartyName = "ediParty";
        internal static string UniformResourceIdentifier = "uniformResourceIdentifier";
        internal static string IPAddress = "ipaddress";
        internal static string RegisteredId = "registeredId";


        public static string Name(this GeneralNameType type)
        {
            switch (type)
            {
                default:
                    throw new NotSupportedException();

                case GeneralNameType.OtherName:
                    return GeneralNameOtherName;
                case GeneralNameType.RFC822Name:
                    return RFC822Name;
                case GeneralNameType.DNSName:
                    return DNSName;
                case GeneralNameType.X400Address:
                    return X400Address;
                case GeneralNameType.DirectoryName:
                    return DirectoryName;
                case GeneralNameType.EdiPartyName:
                    return EdiPartyName;
                case GeneralNameType.UniformResourceIdentifier:
                    return UniformResourceIdentifier;
                case GeneralNameType.IPAddress:
                    return IPAddress;
                case GeneralNameType.RegisteredId:
                    return RegisteredId;
            }
        }


        internal static string KeyUsageBitDigitalSignature = "digitalSignature";
        internal static string KeyUsageBitNonRepudiation = "nonRepudiation";
        internal static string KeyUsageBitKeyEncipherment = "keyEncipherment";
        internal static string KeyUsageBitDataEncipherment = "dataEncipherment";
        internal static string KeyUsageBitKeyAgreement = "keyAgreement";
        internal static string KeyUsageBitKeyCertSign = "keyCertSign";
        internal static string KeyUsageBitCrlSign = "crlSign";
        internal static string KeyUsageBitDataEncipherOnly = "encipherOnly";
        internal static string KeyUsageBitDecipherOnly = "decipherOnly";

        public static string Name(this KeyUsageBit type)
        {
            switch (type)
            {
                default:
                    throw new NotSupportedException();

                case KeyUsageBit.DigitalSignature:
                    return KeyUsageBitDigitalSignature;

                case KeyUsageBit.NonRepudiation:
                    return KeyUsageBitNonRepudiation;

                case KeyUsageBit.KeyEncipherment:
                    return KeyUsageBitKeyEncipherment;
                    
                case KeyUsageBit.DataEncipherment:
                    return KeyUsageBitDataEncipherment;

                case KeyUsageBit.KeyAgreement:
                    return KeyUsageBitKeyAgreement;

                case KeyUsageBit.KeyCertSign:
                    return KeyUsageBitKeyCertSign;

                case KeyUsageBit.CrlSign:
                    return KeyUsageBitCrlSign;

                case KeyUsageBit.EncipherOnly:
                    return KeyUsageBitDataEncipherOnly;

                case KeyUsageBit.DecipherOnly:
                    return KeyUsageBitDecipherOnly;

            }
        }


        public static TEnum FromTag<TEnum>(int tagNo)
        {
            return (TEnum)Enum.ToObject(typeof(TEnum), tagNo);
        }
    }
}
