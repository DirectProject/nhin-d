/* 
 Copyright (c) 2010, Direct Project
 All rights reserved.

 Authors:
    Umesh Madan     umeshma@microsoft.com
    John Theisen    john.theisen@kryptiq.com
  
Redistribution and use in source and binary forms, with or without modification, are permitted provided that the following conditions are met:

Redistributions of source code must retain the above copyright notice, this list of conditions and the following disclaimer.
Redistributions in binary form must reproduce the above copyright notice, this list of conditions and the following disclaimer in the documentation and/or other materials provided with the distribution.
Neither the name of The Direct Project (directproject.org) nor the names of its contributors may be used to endorse or promote products derived from this software without specific prior written permission.
THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 
*/
using System.Security.Cryptography.X509Certificates;

namespace Health.Direct.Common.Certificates
{
    /// <summary>
    /// Some useful cryptography utility/factory methods.
    /// </summary>
    public static class CryptoUtility
    {
        /// <summary>
        /// Opens an existing <see cref="X509Store"/> in read-only mode.
        /// </summary>
        /// <param name="location">The <see cref="StoreLocation"/> of the store to open.</param>
        /// <returns>The newly opened <see cref="X509Store"/></returns>
        public static X509Store OpenStoreRead(StoreLocation location)
        {
            X509Store store = new X509Store(location);
            store.Open(OpenFlags.OpenExistingOnly | OpenFlags.ReadOnly);
            return store;
        }

        /// <summary>
        /// Opens a <see cref="X509Store"/> in read-write-only mode,
        /// creating it if necessary
        /// </summary>
        /// <param name="location">The <see cref="StoreLocation"/> of the store to open.</param>
        /// <returns>The newly opened <see cref="X509Store"/></returns>
        public static X509Store OpenStoreReadWrite(StoreLocation location)
        {
            X509Store store = new X509Store(location);
            store.Open(OpenFlags.ReadWrite);
            return store;
        }

        /// <summary>
        /// Opens a named existing <see cref="X509Store"/> in read-only mode.
        /// </summary>
        /// <param name="location">The <see cref="StoreLocation"/> of the store to open.</param>
        /// <param name="storeName">The name of the store to open</param>
        /// <returns>The newly opened <see cref="X509Store"/></returns>
        public static X509Store OpenStoreRead(string storeName, StoreLocation location)
        {
            X509Store store = new X509Store(storeName, location);
            store.Open(OpenFlags.OpenExistingOnly | OpenFlags.ReadOnly);
            return store;
        }

        /// <summary>
        /// Opens a named <see cref="X509Store"/> in read-write mode, creating it if necessary.
        /// </summary>
        /// <param name="location">The <see cref="StoreLocation"/> of the store to open.</param>
        /// <param name="storeName">The name of the store to open</param>
        /// <returns>The newly opened <see cref="X509Store"/></returns>
        public static X509Store OpenStoreReadWrite(string storeName, StoreLocation location)
        {
            X509Store store = new X509Store(storeName, location);
            store.Open(OpenFlags.ReadWrite);
            return store;
        }
    }
}