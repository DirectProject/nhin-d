/* 
 Copyright (c) 2010, NHIN Direct Project
 All rights reserved.

 Authors:
    Umesh Madan     umeshma@microsoft.com
  
Redistribution and use in source and binary forms, with or without modification, are permitted provided that the following conditions are met:

Redistributions of source code must retain the above copyright notice, this list of conditions and the following disclaimer.
Redistributions in binary form must reproduce the above copyright notice, this list of conditions and the following disclaimer in the documentation and/or other materials provided with the distribution.
Neither the name of the The NHIN Direct Project (nhindirect.org). nor the names of its contributors may be used to endorse or promote products derived from this software without specific prior written permission.
THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 
*/
using System;
using System.Collections.Generic;
using System.Linq;
using System.Runtime.Serialization;
using System.ServiceModel;
using System.Text;
using System.Data.Linq;
using System.Data.Linq.Mapping;
using NHINDirect.Config.Store;

namespace NHINDirect.Config.Service
{
    // NOTE: If you change the class name "AccountService" here, you must also update the reference to "AccountService" in Web.config.
    public class DomainManagerService : IDomainManager, IAddressManager
    {
        #region IDomainManager Members

        public void AddDomain(Domain domain)
        {
            try
            {
                Service.Current.Store.Domains.Add(domain);
            }
            catch (Exception ex)
            {
                throw Service.CreateFault(ex);
            }
        }
        
        public void UpdateDomain(Domain domain)
        {
            try
            {
                Service.Current.Store.Domains.Update(domain);
            }
            catch (Exception ex)
            {
                throw Service.CreateFault(ex);
            }
        }

        public int GetDomainCount()
        {
            try
            {
                return Service.Current.Store.Domains.Count();
            }
            catch (Exception ex)
            {
                throw Service.CreateFault(ex);
            }
        }
        
        public Domain[] GetDomains(string[] domainNames, EntityStatus? status)
        {
            try
            {
                Domain[] matches = Service.Current.Store.Domains.Get(domainNames, status);
                return matches;
            }
            catch (Exception ex)
            {
                throw Service.CreateFault(ex);
            }
        }

        public void RemoveDomain(string domainName)
        {
            try
            {
                Service.Current.Store.Domains.Remove(domainName);
            }
            catch (Exception ex)
            {
                throw Service.CreateFault(ex);
            }
        }

        public Domain[] EnumerateDomains(string lastDomainName, int maxResults)
        {
            try
            {
                return Service.Current.Store.Domains.Get(lastDomainName, maxResults);
            }
            catch (Exception ex)
            {
                throw Service.CreateFault(ex);
            }
        }

        #endregion

        #region IAddressManager Members

        public void AddAddresses(Address[] addresses)
        {
            try
            {
                Service.Current.Store.Addresses.Add(addresses);
            }
            catch (Exception ex)
            {
                throw Service.CreateFault(ex);
            }
        }
        
        public void UpdateAddresses(Address[] addresses)
        {
            try
            {
                Service.Current.Store.Addresses.Update(addresses);
            }
            catch (Exception ex)
            {
                throw Service.CreateFault(ex);
            }
        }

        public int GetAddressCount(string domainName)
        {
            try
            {
                Domain domain = Service.Current.Store.Domains.Get(domainName);
                if (domain == null)
                {
                    return 0;
                }

                return Service.Current.Store.Addresses.Count(domain.ID);
            }
            catch (Exception ex)
            {
                throw Service.CreateFault(ex);
            }
        }
        
        public Address[] GetAddresses(string[] emailAddresses, EntityStatus? status)
        {
            try
            {
                Address[] matches = Service.Current.Store.Addresses.Get(emailAddresses, status);
                return matches;
            }
            catch (Exception ex)
            {
                throw Service.CreateFault(ex);
            }
        }

        public Address[] GetAddressesByID(long[] addressIDs, EntityStatus? status)
        {
            try
            {
                return Service.Current.Store.Addresses.Get(addressIDs, status);
            }
            catch (Exception ex)
            {
                throw Service.CreateFault(ex);
            }
        }

        public void RemoveAddresses(string[] emailAddresses)
        {
            try
            {
                Service.Current.Store.Addresses.Remove(emailAddresses);
            }
            catch (Exception ex)
            {
                throw Service.CreateFault(ex);
            }
        }

        public void RemoveDomainAddresses(long domainID)
        {
            try
            {
                Service.Current.Store.Addresses.RemoveDomain(domainID);
            }
            catch (Exception ex)
            {
                throw Service.CreateFault(ex);
            }
        }
        
        public void SetDomainAddressesStatus(long domainID, EntityStatus status)
        {
            try
            {
                Service.Current.Store.Addresses.SetStatus(domainID, status);
            }
            catch (Exception ex)
            {
                throw Service.CreateFault(ex);
            }
        }
        
        public Address[] EnumerateDomainAddresses(string domainName, string lastAddress, int maxResults)
        {
            try
            {
                Domain domain = Service.Current.Store.Domains.Get(domainName);
                if (domain == null)
                {
                    return null;
                }
                
                return Service.Current.Store.Addresses.Get(domain.ID, lastAddress, maxResults);
            }
            catch (Exception ex)
            {
                throw Service.CreateFault(ex);
            }
        }

        public Address[] EnumerateAddresses(string lastAddress, int maxResults)
        {
            try
            {
                return Service.Current.Store.Addresses.Get(lastAddress, maxResults);
            }
            catch (Exception ex)
            {
                throw Service.CreateFault(ex);
            }
        }

        #endregion
    }
}
