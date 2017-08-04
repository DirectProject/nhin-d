/* 
 Copyright (c) 2017, Direct Project
 All rights reserved.
  
Redistribution and use in source and binary forms, with or without modification, are permitted provided that the following conditions are met:

Redistributions of source code must retain the above copyright notice, this list of conditions and the following disclaimer.
Redistributions in binary form must reproduce the above copyright notice, this list of conditions and the following disclaimer in the documentation and/or other materials provided with the distribution.
Neither the name of The Direct Project (directproject.org) nor the names of its contributors may be used to endorse or promote products derived from this software without specific prior written permission.
THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 
*/

using System;
using System.Runtime.InteropServices;
using System.Runtime.Versioning;
using FILETIME = System.Runtime.InteropServices.ComTypes.FILETIME;

namespace Health.Direct.Agent.Certificates
{
    internal static class NativeMethods
    {
        internal const string CRYPT32 = "crypt32.dll";
        internal const string KERNEL32 = "kernel32.dll";

        internal const uint CERT_STORE_PROV_MEMORY = 2;

        internal const uint X509_ASN_ENCODING = 0x00000001;
        internal const uint X509_NDR_ENCODING = 0x00000002;
        internal const uint PKCS_7_ASN_ENCODING = 0x00010000;
        internal const uint PKCS_7_NDR_ENCODING = 0x00020000;

        internal const uint CERT_STORE_NO_CRYPT_RELEASE_FLAG = 0x00000001;
        internal const uint CERT_STORE_SET_LOCALIZED_NAME_FLAG = 0x00000002;
        internal const uint CERT_STORE_DEFER_CLOSE_UNTIL_LAST_FREE_FLAG = 0x00000004;
        internal const uint CERT_STORE_DELETE_FLAG = 0x00000010;
        internal const uint CERT_STORE_SHARE_STORE_FLAG = 0x00000040;
        internal const uint CERT_STORE_SHARE_CONTEXT_FLAG = 0x00000080;
        internal const uint CERT_STORE_MANIFOLD_FLAG = 0x00000100;
        internal const uint CERT_STORE_ENUM_ARCHIVED_FLAG = 0x00000200;
        internal const uint CERT_STORE_UPDATE_KEYID_FLAG = 0x00000400;
        internal const uint CERT_STORE_BACKUP_RESTORE_FLAG = 0x00000800;
        internal const uint CERT_STORE_READONLY_FLAG = 0x00008000;
        internal const uint CERT_STORE_OPEN_EXISTING_FLAG = 0x00004000;
        internal const uint CERT_STORE_CREATE_NEW_FLAG = 0x00002000;
        internal const uint CERT_STORE_MAXIMUM_ALLOWED_FLAG = 0x00001000;

        // Add certificate/CRL, encoded, context or element disposition values.
        internal const uint CERT_STORE_ADD_NEW = 1;
        internal const uint CERT_STORE_ADD_USE_EXISTING = 2;
        internal const uint CERT_STORE_ADD_REPLACE_EXISTING = 3;
        internal const uint CERT_STORE_ADD_ALWAYS = 4;
        internal const uint CERT_STORE_ADD_REPLACE_EXISTING_INHERIT_PROPERTIES = 5;
        internal const uint CERT_STORE_ADD_NEWER = 6;
        internal const uint CERT_STORE_ADD_NEWER_INHERIT_PROPERTIES = 7;

        internal const uint USAGE_MATCH_TYPE_AND = 0x00000000;

        internal const int LMEM_FIXED = 0x0000;
        internal const int LMEM_ZEROINIT = 0x0040;
        internal const int LPTR = (LMEM_FIXED | LMEM_ZEROINIT);

        // Common chain policy flags.
        internal const uint CERT_CHAIN_REVOCATION_CHECK_END_CERT = 0x10000000;
        internal const uint CERT_CHAIN_REVOCATION_CHECK_CHAIN = 0x20000000;
        internal const uint CERT_CHAIN_REVOCATION_CHECK_CHAIN_EXCLUDE_ROOT = 0x40000000;
        internal const uint CERT_CHAIN_REVOCATION_CHECK_CACHE_ONLY = 0x80000000;
        internal const uint CERT_CHAIN_REVOCATION_ACCUMULATIVE_TIMEOUT = 0x08000000;

        // Predefined verify chain policies
        internal const uint CERT_CHAIN_POLICY_BASE = 1;
        internal const uint CERT_CHAIN_POLICY_AUTHENTICODE = 2;
        internal const uint CERT_CHAIN_POLICY_AUTHENTICODE_TS = 3;
        internal const uint CERT_CHAIN_POLICY_SSL = 4;
        internal const uint CERT_CHAIN_POLICY_BASIC_CONSTRAINTS = 5;
        internal const uint CERT_CHAIN_POLICY_NT_AUTH = 6;
        internal const uint CERT_CHAIN_POLICY_MICROSOFT_ROOT = 7;

        // CERT_CHAIN_ENGINE_CONFIG flags
        public const uint CERT_CHAIN_CACHE_END_CERT = 0x00000001;
        public const uint CERT_CHAIN_CACHE_ONLY_URL_RETRIEVAL = 0x00000004;
        public const uint CERT_CHAIN_USE_LOCAL_MACHINE_STORE = 0x00000008;
        public const uint CERT_CHAIN_ENABLE_CACHE_AUTO_UPDATE = 0x00000010;
        public const uint CERT_CHAIN_ENABLE_SHARE_STORE = 0x00000020;

        // CERT_CHAIN_ENGINE_CONFIG exclusive flags
        public const uint CERT_CHAIN_EXCLUSIVE_ENABLE_CA_FLAG = 0x00000001;

        [StructLayout(LayoutKind.Sequential, CharSet = CharSet.Unicode)]
        internal struct CERT_CHAIN_ENGINE_CONFIG
        {
            internal uint cbSize;
            internal IntPtr hRestrictedRoot;
            internal IntPtr hRestrictedTrust;
            internal IntPtr hRestrictedOther;
            internal uint cAdditionalStore;
            internal IntPtr rghAdditionalStore;
            internal uint dwFlags;
            internal uint dwUrlRetrievalTimeout;
            internal uint MaximumCachedCertificates;
            internal uint CycleDetectionModulus;
            internal IntPtr hExclusiveRoot;
            internal IntPtr hExclusiveTrustedPeople;
            internal uint dwExclusiveFlags;
        }

        [StructLayout(LayoutKind.Sequential, CharSet = CharSet.Unicode)]
        internal struct CERT_CHAIN_PARA
        {
            internal uint cbSize;
            internal CERT_USAGE_MATCH RequestedUsage;
            internal CERT_USAGE_MATCH RequestedIssuancePolicy;
            internal uint dwUrlRetrievalTimeout; // milliseconds
            internal bool fCheckRevocationFreshnessTime;
            internal uint dwRevocationFreshnessTime;
        }

        [StructLayout(LayoutKind.Sequential, CharSet = CharSet.Unicode)]
        internal struct CERT_USAGE_MATCH
        {
            internal uint dwType;
            internal CERT_ENHKEY_USAGE Usage;
        }

        [StructLayout(LayoutKind.Sequential, CharSet = CharSet.Unicode)]
        internal struct CERT_ENHKEY_USAGE
        {
            internal uint cUsageIdentifier;
            internal IntPtr rgpszUsageIdentifier; // LPSTR*
        }

        [StructLayout(LayoutKind.Sequential, CharSet = CharSet.Unicode)]
        internal struct CERT_CHAIN_POLICY_PARA
        {
            internal CERT_CHAIN_POLICY_PARA(int size)
            {
                this.cbSize = (uint)size;
                this.dwFlags = 0;
                this.pvExtraPolicyPara = IntPtr.Zero;
            }

            internal uint cbSize;
            internal uint dwFlags;
            internal IntPtr pvExtraPolicyPara;
        }

        [StructLayout(LayoutKind.Sequential, CharSet = CharSet.Unicode)]
        internal struct CERT_CHAIN_POLICY_STATUS
        {
            internal CERT_CHAIN_POLICY_STATUS(int size)
            {
                this.cbSize = (uint)size;
                this.dwError = 0;
                this.lChainIndex = IntPtr.Zero;
                this.lElementIndex = IntPtr.Zero;
                this.pvExtraPolicyStatus = IntPtr.Zero;
            }

            internal uint cbSize;
            internal uint dwError;
            internal IntPtr lChainIndex;
            internal IntPtr lElementIndex;
            internal IntPtr pvExtraPolicyStatus;
        }

        [DllImport(CRYPT32, CharSet = CharSet.Unicode, SetLastError = true)]
        [return: MarshalAs(UnmanagedType.Bool)]
        internal static extern bool CertCreateCertificateChainEngine(
            [In] IntPtr pConfig, // PCERT_CHAIN_ENGINE_CONFI
            [Out] out SafeChainEngineHandle phChainEngine); // HCERTCHAINENGINE          

        [DllImport(CRYPT32, CharSet = CharSet.Unicode, SetLastError = true)]
        [return: MarshalAs(UnmanagedType.Bool)]
        internal static extern bool CertFreeCertificateChainEngine(
            [In] IntPtr hChainEngine); // HCERTCHAINENGINE          

        [DllImport(CRYPT32, CharSet = CharSet.Unicode, SetLastError = true)]
        internal static extern SafeCertStoreHandle CertOpenStore(
            [In] IntPtr lpszStoreProvider,
            [In] uint dwMsgAndCertEncodingType,
            [In] IntPtr hCryptProv,
            [In] uint dwFlags,
            [In] string pvPara); // we want this always as a Unicode string.

        [DllImport(CRYPT32, CharSet = CharSet.Unicode, SetLastError = true)]
        [return: MarshalAs(UnmanagedType.Bool)]
        internal static extern bool CertCloseStore(
            [In] IntPtr hCertStore,
            [In] uint dwFlags);

        [DllImport(CRYPT32, CharSet = CharSet.Auto, SetLastError = true)]
        [return: MarshalAs(UnmanagedType.Bool)]
        internal static extern bool CertAddCertificateContextToStore(
            [In] SafeCertStoreHandle hCertStore,
            [In] SafeCertContextHandle pCertContext,
            [In] uint dwAddDisposition,
            [In, Out] SafeCertContextHandle ppStoreContext);

        [DllImport(CRYPT32, CharSet = CharSet.Auto, SetLastError = true)]
        [ResourceExposure(ResourceScope.None)]
        internal static extern SafeCertContextHandle CertDuplicateCertificateContext(
            [In] IntPtr pCertContext);

        [DllImport(CRYPT32, CharSet = CharSet.Auto, SetLastError = true)]
        [return: MarshalAs(UnmanagedType.Bool)]
        internal static extern bool CertFreeCertificateContext(
            [In] IntPtr pCertContext);

        [DllImport(CRYPT32, CharSet = CharSet.Auto, SetLastError = true)]
        [return: MarshalAs(UnmanagedType.Bool)]
        internal static extern bool CertGetCertificateChain(
            [In] SafeChainEngineHandle hChainEngine,
            [In] SafeCertContextHandle pCertContext,
            [In] ref FILETIME pTime,
            [In] SafeCertStoreHandle hAdditionalStore,
            [In] ref CERT_CHAIN_PARA pChainPara,
            [In] uint dwFlags,
            [In] IntPtr pvReserved,
            [In, Out] ref SafeX509ChainHandle ppChainContext);

        [DllImport(CRYPT32, CharSet = CharSet.Auto, SetLastError = true)]
        [return: MarshalAs(UnmanagedType.Bool)]
        internal extern static bool CertAddCertificateLinkToStore(
            [In] SafeCertStoreHandle hCertStore,
            [In] IntPtr pCertContext,
            [In] uint dwAddDisposition,
            [In, Out] SafeCertContextHandle ppStoreContext);

        [DllImport(KERNEL32, SetLastError = true)]
        internal static extern IntPtr LocalFree(IntPtr handle);

        [DllImport(KERNEL32, CharSet = CharSet.Auto, SetLastError = true)]
        [ResourceExposure(ResourceScope.None)]
        internal static extern SafeLocalAllocHandle LocalAlloc(
            [In] uint uFlags,
            [In] IntPtr sizetdwBytes);

        [DllImport(CRYPT32, ExactSpelling = true, SetLastError = true)]
        internal static extern void CertFreeCertificateChain(
            [In] IntPtr pChainContext);

        [DllImport(CRYPT32, CharSet = CharSet.Auto, SetLastError = true)]
        [return: MarshalAs(UnmanagedType.Bool)]
        internal static extern bool CertVerifyCertificateChainPolicy(
            [In] IntPtr pszPolicyOID,
            [In] SafeX509ChainHandle pChainContext,
            [In] ref CERT_CHAIN_POLICY_PARA pPolicyPara,
            [In, Out] ref CERT_CHAIN_POLICY_STATUS pPolicyStatus);

        [DllImport(KERNEL32, SetLastError = true)]
        internal static extern void SetLastError(uint dwErrorCode);
    }
}