/* 
 Copyright (c) 2012, Direct Project
 All rights reserved.

 Authors:
    Sean Nolan      sean.nolan@microsoft.com
  
Redistribution and use in source and binary forms, with or without modification, are permitted provided that the following conditions are met:

Redistributions of source code must retain the above copyright notice, this list of conditions and the following disclaimer.
Redistributions in binary form must reproduce the above copyright notice, this list of conditions and the following disclaimer in the documentation and/or other materials provided with the distribution.
Neither the name of The Direct Project (directproject.org) nor the names of its contributors may be used to endorse or promote products derived from this software without specific prior written permission.
THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 
*/
using System.ServiceModel;

using Health.Direct.Config.Store;

namespace Health.Direct.Config.Service
{
    [ServiceContract(Namespace = Service.Namespace)]
    public interface IBundleStore
    {
        [OperationContract]
        [FaultContract(typeof(ConfigStoreFault))]
        Bundle AddBundle(Bundle bundle);

        [OperationContract]
        [FaultContract(typeof(ConfigStoreFault))]
        void AddBundles(Bundle[] bundles);

        [OperationContract]
        [FaultContract(typeof(ConfigStoreFault))]
        Bundle GetBundle(long bundleId);

        [OperationContract]
        [FaultContract(typeof(ConfigStoreFault))]
        Bundle[] GetBundles(long[] bundleIDs);

        [OperationContract]
        [FaultContract(typeof(ConfigStoreFault))]
        Bundle[] GetBundlesForOwner(string owner);

        [OperationContract]
        [FaultContract(typeof(ConfigStoreFault))]
        Bundle[] GetIncomingBundles(string owner, EntityStatus status);

        [OperationContract]
        [FaultContract(typeof(ConfigStoreFault))]
        Bundle[] GetOutgoingBundles(string owner, EntityStatus status);

        [OperationContract]
        [FaultContract(typeof(ConfigStoreFault))]
        void SetBundleStatus(long[] bundleIDs, EntityStatus status);

        [OperationContract]
        [FaultContract(typeof(ConfigStoreFault))]
        void SetBundleStatusForOwner(string owner, EntityStatus status);

        [OperationContract]
        [FaultContract(typeof(ConfigStoreFault))]
        Bundle[] EnumerateBundles(long lastBundleID, int maxResults);

        [OperationContract]
        [FaultContract(typeof(ConfigStoreFault))]
        void RemoveBundles(long[] bundleIDs);

        [OperationContract]
        [FaultContract(typeof(ConfigStoreFault))]
        void RemoveBundlesForOwner(string owner);
    }
}