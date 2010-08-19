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
        
        public Domain GetDomain(string domainName)
        {
            try
            {
                return Service.Current.Store.Domains.Get(domainName);
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
        
        public Domain[] EnumerateDomains(long lastDomainID, int maxResults)
        {
            try
            {
                return Service.Current.Store.Domains.Get(lastDomainID, maxResults);
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

        public Address[] GetAddresses(string[] emailAddresses)
        {
            try
            {
                return Service.Current.Store.Addresses.Get(emailAddresses);
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

        public Address[] EnumerateDomainAddresses(long domainID, long lastAddressID, int maxResults)
        {
            try
            {
                return Service.Current.Store.Addresses.Get(domainID, lastAddressID, maxResults);
            }
            catch (Exception ex)
            {
                throw Service.CreateFault(ex);
            }
        }

        public Address[] EnumerateAddresses(long lastAddressID, int maxResults)
        {
            try
            {
                return Service.Current.Store.Addresses.Get(lastAddressID, maxResults);
            }
            catch (Exception ex)
            {
                throw Service.CreateFault(ex);
            }
        }

        #endregion
    }
}
