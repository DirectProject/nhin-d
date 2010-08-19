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
