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
using System.Security;
using Microsoft.Win32.SafeHandles;

namespace Health.Direct.Agent.Certificates
{
    internal sealed class SafeCertContextHandle : SafeHandleZeroOrMinusOneIsInvalid
    {
        private SafeCertContextHandle()
            : base(true)
        {
        }

        internal SafeCertContextHandle(IntPtr handle)
            : base(true)
        {
            this.SetHandle(handle);
        }

        internal static SafeCertContextHandle InvalidHandle => new SafeCertContextHandle(IntPtr.Zero);

        [SecurityCritical]
        protected override bool ReleaseHandle() => NativeMethods.CertFreeCertificateContext(this.handle);
    }

    [SecurityCritical] // auto-generated
    internal sealed class SafeCertStoreHandle : SafeHandleZeroOrMinusOneIsInvalid
    {
        private SafeCertStoreHandle()
            : base(true)
        {
        }

        internal SafeCertStoreHandle(IntPtr handle)
            : base(true)
        {
            this.SetHandle(handle);
        }

        internal static SafeCertStoreHandle InvalidHandle => new SafeCertStoreHandle(IntPtr.Zero);

        [SecurityCritical]
        protected override bool ReleaseHandle() => NativeMethods.CertCloseStore(this.handle, 0);
    }

    public sealed class SafeX509ChainHandle : SafeHandleZeroOrMinusOneIsInvalid
    {
        private SafeX509ChainHandle()
            : base(true)
        {
        }

        internal SafeX509ChainHandle(IntPtr handle)
            : base(true)
        {
            this.SetHandle(handle);
        }

        internal static SafeX509ChainHandle InvalidHandle => new SafeX509ChainHandle(IntPtr.Zero);

        [SecurityCritical]
        protected override bool ReleaseHandle()
        {
            NativeMethods.CertFreeCertificateChain(this.handle);
            return true;
        }
    }

    [SecurityCritical]
    internal sealed class SafeChainEngineHandle : SafeHandleZeroOrMinusOneIsInvalid
    {
        private SafeChainEngineHandle()
            : base(true)
        {
        }

        internal SafeChainEngineHandle(IntPtr handle)
            : base(true)
        {
            this.SetHandle(handle);
        }

        internal static SafeChainEngineHandle InvalidHandle => new SafeChainEngineHandle(IntPtr.Zero);

        [SecurityCritical]
        protected override bool ReleaseHandle() => NativeMethods.CertFreeCertificateChainEngine(this.handle);
    }

    internal sealed class SafeLocalAllocHandle : SafeBuffer
    {
        private SafeLocalAllocHandle()
            : base(true)
        {
        }

        internal SafeLocalAllocHandle(IntPtr handle)
            : base(true)
        {
            this.SetHandle(handle);
        }

        internal static SafeLocalAllocHandle InvalidHandle => new SafeLocalAllocHandle(IntPtr.Zero);

        [SecurityCritical]
        protected override bool ReleaseHandle()
        {
            return NativeMethods.LocalFree(this.handle) == IntPtr.Zero;
        }
    }
}