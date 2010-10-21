/* 
 Copyright (c) 2010, Direct Project
 All rights reserved.

 Authors:
    Umesh Madan     umeshma@microsoft.com
    Chris Lomonico  chris.lomonico@surescripts.com
  
Redistribution and use in source and binary forms, with or without modification, are permitted provided that the following conditions are met:

Redistributions of source code must retain the above copyright notice, this list of conditions and the following disclaimer.
Redistributions in binary form must reproduce the above copyright notice, this list of conditions and the following disclaimer in the documentation and/or other materials provided with the distribution.
Neither the name of the The Direct Project (nhindirect.org). nor the names of its contributors may be used to endorse or promote products derived from this software without specific prior written permission.
THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 
*/
using System;

using NHINDirect.Config.Store;

namespace Health.Direct.Config.Service
{
    public class DomainManagerService : ConfigServiceBase, IDomainManager, IAddressManager, IMXManager
    {
        #region IDomainManager Members

        public void AddDomain(Domain domain)
        {
            try
            {
                Store.Domains.Add(domain);
            }
            catch (Exception ex)
            {
                throw CreateFault("AddDomain", ex);
            }
        }

        public void UpdateDomain(Domain domain)
        {
            try
            {
                Store.Domains.Update(domain);
            }
            catch (Exception ex)
            {
                throw CreateFault("UpdateDomain", ex);
            }
        }

        public int GetDomainCount()
        {
            try
            {
                return Store.Domains.Count();
            }
            catch (Exception ex)
            {
                throw CreateFault("GetDomainCount", ex);
            }
        }

        public Domain[] GetDomains(string[] domainNames, EntityStatus? status)
        {
            try
            {
                return Store.Domains.Get(domainNames, status);
            }
            catch (Exception ex)
            {
                throw CreateFault("GetDomains", ex);
            }
        }

        public void RemoveDomain(string domainName)
        {
            try
            {
                Store.Domains.Remove(domainName);
            }
            catch (Exception ex)
            {
                throw CreateFault("RemoveDomain", ex);
            }
        }

        public Domain[] EnumerateDomains(string lastDomainName, int maxResults)
        {
            try
            {
                return Store.Domains.Get(lastDomainName, maxResults);
            }
            catch (Exception ex)
            {
                throw CreateFault("EnumerateDomains", ex);
            }
        }

        #endregion

        #region IAddressManager Members

        public void AddAddresses(Address[] addresses)
        {
            try
            {
                Store.Addresses.Add(addresses);
            }
            catch (Exception ex)
            {
                throw CreateFault("AddAddresses", ex);
            }
        }

        public void UpdateAddresses(Address[] addresses)
        {
            try
            {
                Store.Addresses.Update(addresses);
            }
            catch (Exception ex)
            {
                throw CreateFault("UpdateAddresses", ex);
            }
        }

        public int GetAddressCount(string domainName)
        {
            try
            {
                Domain domain = Store.Domains.Get(domainName);
                if (domain == null)
                {
                    return 0;
                }

                return Store.Addresses.Count(domain.ID);
            }
            catch (Exception ex)
            {
                throw CreateFault("GetAddressCount", ex);
            }
        }

        public Address[] GetAddresses(string[] emailAddresses, EntityStatus? status)
        {
            try
            {
                return Store.Addresses.Get(emailAddresses, status);
            }
            catch (Exception ex)
            {
                throw CreateFault("GetAddresses", ex);
            }
        }

        public Address[] GetAddressesByID(long[] addressIDs, EntityStatus? status)
        {
            try
            {
                return Store.Addresses.Get(addressIDs, status);
            }
            catch (Exception ex)
            {
                throw CreateFault("GetAddressesByID", ex);
            }
        }

        public void RemoveAddresses(string[] emailAddresses)
        {
            try
            {
                Store.Addresses.Remove(emailAddresses);
            }
            catch (Exception ex)
            {
                throw CreateFault("RemoveAddresses", ex);
            }
        }

        public void RemoveDomainAddresses(long domainID)
        {
            try
            {
                Store.Addresses.RemoveDomain(domainID);
            }
            catch (Exception ex)
            {
                throw CreateFault("RemoveDomainAddresses", ex);
            }
        }

        public void SetDomainAddressesStatus(long domainID, EntityStatus status)
        {
            try
            {
                Store.Addresses.SetStatus(domainID, status);
            }
            catch (Exception ex)
            {
                throw CreateFault("SetDomainAddressesStatus", ex);
            }
        }

        public Address[] EnumerateDomainAddresses(string domainName, string lastAddress, int maxResults)
        {
            try
            {
                Domain domain = Store.Domains.Get(domainName);
                if (domain == null)
                {
                    return null;
                }

                return Store.Addresses.Get(domain.ID, lastAddress, maxResults);
            }
            catch (Exception ex)
            {
                throw CreateFault("EnumerateDomainAddresses", ex);
            }
        }

        public Address[] EnumerateAddresses(string lastAddress, int maxResults)
        {
            try
            {
                return Store.Addresses.Get(lastAddress, maxResults);
            }
            catch (Exception ex)
            {
                throw CreateFault("EnumerateAddresses", ex);
            }
        }

        #endregion

        #region IMXManager Members

        public void AddMX(MX mx)
        {
            try
            {
                Store.MXs.Add(mx);
            }
            catch (Exception ex)
            {
                throw CreateFault("AddMX", ex);
            }
        }

        public int GetMXCount(long domainID)
        {
            try
            {
                return Store.MXs.Count(domainID);
            }
            catch (Exception ex)
            {
                throw CreateFault("GetMXCount", ex);
            }
        }

        public void UpdateMX(MX mx)
        {
            try
            {
                Store.MXs.Update(mx);
            }
            catch (Exception ex)
            {
                throw CreateFault("UpdateMX", ex);
            }
        }


        public MX[] GetMXs(string[] mxNames, Int16? preference)
        {
            try
            {
                return Store.MXs.Get(mxNames, preference);
            }
            catch (Exception ex)
            {
                throw CreateFault("GetMXs", ex);
            }
        }

        public void RemoveMX(string name)
        {
            try
            {
                Store.MXs.Remove(name);
            }
            catch (Exception ex)
            {
                throw CreateFault("RemoveMX", ex);
            }
        }

        public MX[] EnumerateMXs(string lastMXName, int maxResults)
        {
            try
            {
                return Store.MXs.Get(lastMXName, maxResults);
            }
            catch (Exception ex)
            {
                throw CreateFault("EnumerateMXs", ex);
            }
        }

        #endregion
    }
}