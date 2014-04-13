/* 
 Copyright (c) 2010, Direct Project
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
using Health.Direct.Common.Extensions;
using Health.Direct.Config.Client.CertificateService;
using Health.Direct.Config.Store;

namespace Health.Direct.Config.Client
{
    public static class BundleManagerExtensions
    {
        public static Bundle GetExistingBundle(this BundleStoreClient client, string owner, string url)
        {
            if (string.IsNullOrEmpty(owner))
            {
                throw new ArgumentException("owner");
            }
            if (string.IsNullOrEmpty(url))
            { 
                throw new ArgumentException("url");
            }
            Bundle[] bundles = client.GetBundlesForOwner(owner);
            if (bundles.IsNullOrEmpty())
            {
                return null;
            }
            
            return bundles.FirstOrDefault((b) => (!string.IsNullOrEmpty(b.Url) && b.Url.Equals(url, StringComparison.OrdinalIgnoreCase)));
        }
        
        public static IEnumerable<Bundle> EnumerateBundles(this BundleStoreClient client, int chunkSize)
        {
            if (chunkSize < 1)
            {
                throw new ArgumentException("value was less than 1", "chunkSize");
            }

            long lastID = -1;

            Bundle[] bundles;
            while (true)
            {
                bundles = client.EnumerateBundles(lastID, chunkSize);
                if (bundles == null || bundles.Length == 0)
                {
                    yield break;
                }
                for (int i = 0; i < bundles.Length; ++i)
                {
                    yield return bundles[i];
                }
                lastID = bundles[bundles.Length - 1].ID;
            }
        }
    }
}
