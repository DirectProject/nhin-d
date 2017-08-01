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
using System.Collections.Generic;
using System.Linq;
using System.Runtime.InteropServices;
using System.Security.Cryptography;
using System.Security.Cryptography.X509Certificates;
using System.Text;
using FILETIME = System.Runtime.InteropServices.ComTypes.FILETIME;

namespace Health.Direct.Agent.Certificates
{
    /// <summary>
    /// This class supports an alternate API to <see cref="X509Chain.Build" />, where the trusted certificate authority roots are specified as
    /// constructor parameters instead of what is installed in the system trusted CA stores.
    /// </summary>
    /// <seealso cref="System.IDisposable" />
    /// <remarks>
    /// The X509ChainEngine implementation is nearly identical to <see cref="X509Chain.Build" />, P/Invoking to the unmanaged Crypto API.
    /// Indeed most of the implementation is a copy of the original .NET source .
    /// The key difference is that it uses CertCreateCertificateChainEngine to create a custom chain engine configured to use its own trusted
    /// roots via the hExclusiveRoot configuration parameter. This chain engine is passed into CertGetCertificateChain, and from then on,
    /// the implementation is essentially identical.
    /// </remarks>
    public sealed class X509ChainEngine : IDisposable
    {
        private readonly SafeCertStoreHandle safeCertStoreHandle;
        private readonly List<SafeCertContextHandle> roots = new List<SafeCertContextHandle>();
        private readonly SafeChainEngineHandle safeChainEngineHandle;

        /// <summary>
        /// Initializes a new instance of the <see cref="X509ChainEngine"/> class.
        /// </summary>
        /// <param name="trustedRoots">Root certificates to trust as certificate authorities for this instance.</param>
        /// <exception cref="System.Security.Cryptography.CryptographicException">
        /// When any of the underlying Windows CryptoAPI calls fail.
        /// </exception>
        public X509ChainEngine(params X509Certificate2[] trustedRoots)
            : this(trustedRoots.AsEnumerable())
        {
        }

        /// <summary>
        /// Initializes a new instance of the <see cref="X509ChainEngine"/> class.
        /// </summary>
        /// <param name="trustedRoots">Root certificates to trust as certificate authorities for this instance.</param>
        /// <exception cref="System.Security.Cryptography.CryptographicException">
        /// When any of the underlying Windows CryptoAPI calls fail.
        /// </exception>
        public unsafe X509ChainEngine(IEnumerable<X509Certificate2> trustedRoots)
        {
            // First, create a memory store
            this.safeCertStoreHandle = NativeMethods.CertOpenStore(
                lpszStoreProvider: new IntPtr(NativeMethods.CERT_STORE_PROV_MEMORY),
                dwMsgAndCertEncodingType: NativeMethods.X509_ASN_ENCODING | NativeMethods.PKCS_7_ASN_ENCODING,
                hCryptProv: IntPtr.Zero,
                dwFlags: NativeMethods.CERT_STORE_ENUM_ARCHIVED_FLAG | NativeMethods.CERT_STORE_CREATE_NEW_FLAG,
                pvPara: null);

            if (this.safeCertStoreHandle == null || this.safeCertStoreHandle.IsInvalid)
            {
                throw new CryptographicException(Marshal.GetLastWin32Error());
            }

            // Add the trusted roots to the store
            foreach (X509Certificate2 trustedRoot in trustedRoots)
            {
                SafeCertContextHandle certContext = NativeMethods.CertDuplicateCertificateContext(trustedRoot.Handle);
                this.roots.Add(certContext);
                AddCertificateLinkToStore(safeCertStoreHandle: this.safeCertStoreHandle, pCertContext: certContext.DangerousGetHandle());
            }

            // Create a chain engine that exclusively trusts these roots.
            var certChainEngineConfig = new NativeMethods.CERT_CHAIN_ENGINE_CONFIG
            {
                cbSize = (uint)sizeof(NativeMethods.CERT_CHAIN_ENGINE_CONFIG),
                dwFlags = NativeMethods.CERT_CHAIN_ENABLE_CACHE_AUTO_UPDATE | NativeMethods.CERT_CHAIN_ENABLE_SHARE_STORE,
                // Need to retrieve underlying IntPtr. The struct cannot contain a Safehandle field as we need to be able to use sizeof()
                hExclusiveRoot = this.safeCertStoreHandle.DangerousGetHandle(),
                dwExclusiveFlags = NativeMethods.CERT_CHAIN_EXCLUSIVE_ENABLE_CA_FLAG
            };

            if (!NativeMethods.CertCreateCertificateChainEngine(new IntPtr(&certChainEngineConfig), out this.safeChainEngineHandle))
            {
                throw new CryptographicException(Marshal.GetLastWin32Error());
            }
        }

        /// <summary>
        /// Builds and validates a certificate chain, similar to <see cref="X509Chain.Build" />.
        /// </summary>
        /// <param name="certificate">The certificate to validate.</param>
        /// <param name="chainPolicy">The chain policy to apply.</param>
        /// <param name="chain">The resulting chain, whose <see cref="X509Chain.ChainStatus"/> and <see cref="X509Chain.ChainElements"/> properties are populated.</param>
        /// <returns>Whether the certificate is valid accoring to the given <paramref name="chainPolicy"/></returns>
        /// <exception cref="System.Security.Cryptography.CryptographicException">
        /// When any of the underlying Windows CryptoAPI calls fail.
        /// </exception>
        public unsafe bool BuildChain(X509Certificate2 certificate, X509ChainPolicy chainPolicy, out X509Chain chain)
        {
            SafeX509ChainHandle ppChainContext = SafeX509ChainHandle.InvalidHandle;

            SafeCertStoreHandle hCertStore = SafeCertStoreHandle.InvalidHandle;
            if (chainPolicy.ExtraStore != null && chainPolicy.ExtraStore.Count > 0)
            {
                hCertStore = ExportToMemoryStore(chainPolicy.ExtraStore);
            }

            NativeMethods.CERT_CHAIN_PARA chainPara = new NativeMethods.CERT_CHAIN_PARA();

            // Initialize the structure size.
            chainPara.cbSize = (uint)Marshal.SizeOf(chainPara);

            SafeLocalAllocHandle applicationPolicyHandle = SafeLocalAllocHandle.InvalidHandle;
            SafeLocalAllocHandle certificatePolicyHandle = SafeLocalAllocHandle.InvalidHandle;

            try
            {
                // Application policy
                if (chainPolicy.ApplicationPolicy != null && chainPolicy.ApplicationPolicy.Count > 0)
                {
                    chainPara.RequestedUsage.dwType = NativeMethods.USAGE_MATCH_TYPE_AND;
                    chainPara.RequestedUsage.Usage.cUsageIdentifier = (uint)chainPolicy.ApplicationPolicy.Count;
                    applicationPolicyHandle = CopyOidsToUnmanagedMemory(chainPolicy.ApplicationPolicy);
                    chainPara.RequestedUsage.Usage.rgpszUsageIdentifier = applicationPolicyHandle.DangerousGetHandle();
                }

                // Certificate policy
                if (chainPolicy.CertificatePolicy != null && chainPolicy.CertificatePolicy.Count > 0)
                {
                    chainPara.RequestedIssuancePolicy.dwType = NativeMethods.USAGE_MATCH_TYPE_AND;
                    chainPara.RequestedIssuancePolicy.Usage.cUsageIdentifier = (uint)chainPolicy.CertificatePolicy.Count;
                    certificatePolicyHandle = CopyOidsToUnmanagedMemory(chainPolicy.CertificatePolicy);
                    chainPara.RequestedIssuancePolicy.Usage.rgpszUsageIdentifier = certificatePolicyHandle.DangerousGetHandle();
                }

                chainPara.dwUrlRetrievalTimeout = (uint)Math.Floor(chainPolicy.UrlRetrievalTimeout.TotalMilliseconds);

                FILETIME ft = new FILETIME();
                *(long*)&ft = chainPolicy.VerificationTime.ToFileTime();

                uint flags = MapRevocationFlags(chainPolicy.RevocationMode, chainPolicy.RevocationFlag);

                using (SafeCertContextHandle certContextHandle = NativeMethods.CertDuplicateCertificateContext(certificate.Handle))
                {
                    // Build the chain.
                    if (!NativeMethods.CertGetCertificateChain(hChainEngine: this.safeChainEngineHandle,
                        pCertContext: certContextHandle,
                        pTime: ref ft,
                        hAdditionalStore: hCertStore,
                        pChainPara: ref chainPara,
                        dwFlags: flags,
                        pvReserved: IntPtr.Zero,
                        ppChainContext: ref ppChainContext))
                    {
                        throw new CryptographicException(Marshal.GetHRForLastWin32Error());
                    }

                    chain = new X509Chain(ppChainContext.DangerousGetHandle()) { ChainPolicy = chainPolicy };

                    // Verify the chain using the specified policy.
                    NativeMethods.CERT_CHAIN_POLICY_PARA policyPara = new NativeMethods.CERT_CHAIN_POLICY_PARA(Marshal.SizeOf(typeof(NativeMethods.CERT_CHAIN_POLICY_PARA)));
                    NativeMethods.CERT_CHAIN_POLICY_STATUS policyStatus = new NativeMethods.CERT_CHAIN_POLICY_STATUS(Marshal.SizeOf(typeof(NativeMethods.CERT_CHAIN_POLICY_STATUS)));

                    policyPara.dwFlags = (uint)chainPolicy.VerificationFlags;

                    if (!NativeMethods.CertVerifyCertificateChainPolicy(
                        pszPolicyOID: new IntPtr(NativeMethods.CERT_CHAIN_POLICY_BASE),
                        pChainContext: ppChainContext,
                        pPolicyPara: ref policyPara,
                        pPolicyStatus: ref policyStatus))
                    {
                        throw new CryptographicException(Marshal.GetLastWin32Error());
                    }

                    NativeMethods.SetLastError(policyStatus.dwError);
                    return policyStatus.dwError == 0;
                }
            }
            finally
            {
                applicationPolicyHandle.Dispose();
                certificatePolicyHandle.Dispose();
            }
        }

        /// <summary>
        /// Releases managed resources.
        /// </summary>
        public void Dispose()
        {
            this.safeCertStoreHandle?.Dispose();
            this.safeChainEngineHandle?.Dispose();
            foreach (SafeCertContextHandle certContextHandle in this.roots)
            {
                certContextHandle.Dispose();
            }
        }

        /// <summary>
        /// Adapted from .NET source <see cref="System.Security.Cryptography.X509Certificates.X509Utils.MapRevocationFlags"/>
        /// </summary>
        private static uint MapRevocationFlags(X509RevocationMode revocationMode, X509RevocationFlag revocationFlag)
        {
            uint dwFlags = 0;
            if (revocationMode == X509RevocationMode.NoCheck)
            {
                return dwFlags;
            }

            if (revocationMode == X509RevocationMode.Offline)
            {
                dwFlags |= NativeMethods.CERT_CHAIN_REVOCATION_CHECK_CACHE_ONLY;
            }

            if (revocationFlag == X509RevocationFlag.EndCertificateOnly)
            {
                dwFlags |= NativeMethods.CERT_CHAIN_REVOCATION_CHECK_END_CERT;
            }
            else if (revocationFlag == X509RevocationFlag.EntireChain)
            {
                dwFlags |= NativeMethods.CERT_CHAIN_REVOCATION_CHECK_CHAIN;
            }
            else
            {
                dwFlags |= NativeMethods.CERT_CHAIN_REVOCATION_CHECK_CHAIN_EXCLUDE_ROOT;
            }

            return dwFlags;
        }

        /// <summary>
        /// Adapted from .NET source <see cref="System.Security.Cryptography.X509Certificates.X509Utils.ExportToMemoryStore"/>
        /// </summary>
        private static SafeCertStoreHandle ExportToMemoryStore(X509Certificate2Collection collection)
        {
            SafeCertStoreHandle safeCertStoreHandle = SafeCertStoreHandle.InvalidHandle;

            // we always want to use CERT_STORE_ENUM_ARCHIVED_FLAG since we want to preserve the collection in this operation.
            // By default, Archived certificates will not be included.

            safeCertStoreHandle = NativeMethods.CertOpenStore(
                new IntPtr(NativeMethods.CERT_STORE_PROV_MEMORY),
                NativeMethods.X509_ASN_ENCODING | NativeMethods.PKCS_7_ASN_ENCODING,
                IntPtr.Zero,
                NativeMethods.CERT_STORE_ENUM_ARCHIVED_FLAG | NativeMethods.CERT_STORE_CREATE_NEW_FLAG,
                null);

            if (safeCertStoreHandle == null || safeCertStoreHandle.IsInvalid)
            {
                throw new CryptographicException(Marshal.GetLastWin32Error());
            }

            //
            // We use CertAddCertificateLinkToStore to keep a link to the original store, so any property changes get
            // applied to the original store. This has a limit of 99 links per cert context however.
            //

            foreach (X509Certificate2 x509 in collection)
            {
                AddCertificateLinkToStore(safeCertStoreHandle, x509.Handle);
            }

            return safeCertStoreHandle;
        }

        /// <summary>
        /// Adds a link in a certificate store to a certificate context in a different store.
        /// </summary>
        /// <param name="safeCertStoreHandle">The certificate store.</param>
        /// <param name="pCertContext">A pointer to the certificate context.</param>
        private static void AddCertificateLinkToStore(SafeCertStoreHandle safeCertStoreHandle, IntPtr pCertContext)
        {
            if (!NativeMethods.CertAddCertificateLinkToStore(
                hCertStore: safeCertStoreHandle,
                pCertContext: pCertContext,
                dwAddDisposition: NativeMethods.CERT_STORE_ADD_ALWAYS,
                ppStoreContext: SafeCertContextHandle.InvalidHandle))
            {
                throw new CryptographicException(Marshal.GetLastWin32Error());
            }
        }

        /// <summary>
        /// Adapted from .NET source <see cref="System.Security.Cryptography.X509Certificates.X509Utils.CopyOidsToUnmanagedMemory"/>
        /// </summary>
        private static SafeLocalAllocHandle CopyOidsToUnmanagedMemory(OidCollection oids)
        {
            SafeLocalAllocHandle safeLocalAllocHandle = SafeLocalAllocHandle.InvalidHandle;

            if (oids == null || oids.Count == 0)
            {
                return safeLocalAllocHandle;
            }

            // Copy the oid strings to a local list to prevent a security race condition where
            // the OidCollection or individual oids can be modified by another thread and
            // potentially cause a buffer overflow
            List<string> oidStrs = oids.Cast<Oid>().Select(oid => oid.Value).ToList();

            IntPtr pOid = IntPtr.Zero;

            // Needs to be checked to avoid having large sets of oids overflow the sizes and allow
            // a potential buffer overflow
            checked
            {
                int ptrSize = oidStrs.Count * Marshal.SizeOf(typeof(IntPtr));
                int oidSize = oidStrs.Sum(oidStr => oidStr.Length + 1);
                safeLocalAllocHandle = NativeMethods.LocalAlloc(NativeMethods.LPTR, new IntPtr((uint)ptrSize + (uint)oidSize));
                pOid = new IntPtr((long)safeLocalAllocHandle.DangerousGetHandle() + ptrSize);
            }

            for (int index = 0; index < oidStrs.Count; index++)
            {
                Marshal.WriteIntPtr(new IntPtr((long)safeLocalAllocHandle.DangerousGetHandle() + index * Marshal.SizeOf(typeof(IntPtr))), pOid);
                byte[] ansiOid = Encoding.ASCII.GetBytes(oidStrs[index]);
                Marshal.Copy(ansiOid, 0, pOid, ansiOid.Length);
                pOid = new IntPtr((long)pOid + oidStrs[index].Length + 1);
            }

            return safeLocalAllocHandle;
        }
    }
}