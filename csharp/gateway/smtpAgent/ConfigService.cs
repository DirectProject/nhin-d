/* 
 Copyright (c) 2010, Direct Project
 All rights reserved.

 Authors:
    Umesh Madan     umeshma@microsoft.com
    Joe Shook       jshook@kryptiq.com
  
Redistribution and use in source and binary forms, with or without modification, are permitted provided that the following conditions are met:

Redistributions of source code must retain the above copyright notice, this list of conditions and the following disclaimer.
Redistributions in binary form must reproduce the above copyright notice, this list of conditions and the following disclaimer in the documentation and/or other materials provided with the distribution.
Neither the name of The Direct Project (directproject.org) nor the names of its contributors may be used to endorse or promote products derived from this software without specific prior written permission.
THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 
*/

using System;
using System.Collections.Generic;
using System.Diagnostics;
using System.Linq;
using System.Xml;
using System.Xml.Serialization;
using Health.Direct.Agent;
using Health.Direct.Common.Caching;
using Health.Direct.Config.Client;
using Health.Direct.Config.Client.DomainManager;
using Health.Direct.Config.Store;
using Health.Direct.SmtpAgent.Config;

namespace Health.Direct.SmtpAgent
{
    /// <summary>
    /// Facade around the Config System
    /// We'll bake in Caching and other enhancements here..
    /// </summary>
    internal class ConfigService
    {
        readonly SmtpAgentSettings _settings;
        readonly DomainConfigCache _domainCache;
        readonly AddressConfigCache _addressCache;
        readonly bool _enabledDomainSearch;

        internal ConfigService(SmtpAgentSettings settings)
        {
            _settings = settings;

            if (settings.AddressManager != null && settings.AddressManager.HasSettings)
            {
                using (var reader = new XmlNodeReader(settings.AddressManager.Settings))
                {
                    var serializer = new XmlSerializer(typeof(AddressManagerSettings), new XmlRootAttribute(settings.AddressManager.Settings.LocalName));
                    var addressManagerSettings = (AddressManagerSettings)serializer.Deserialize(reader);
                    _enabledDomainSearch = addressManagerSettings.EnableDomainSearch;
                    var cacheSettings = addressManagerSettings.CacheSettings;

                    if (cacheSettings != null && cacheSettings.Cache)
                    {
                        var addressCacheSettings = new CacheSettings(cacheSettings) { Name = "AddressCache" };
                        _addressCache = new AddressConfigCache(addressCacheSettings);
                    }
                }
            }

            if (settings.DomainManagerService != null && settings.DomainManagerService.HasSettings)
            {
                using (var reader = new XmlNodeReader(settings.DomainManagerService.Settings))
                {
                    var serializer = new XmlSerializer(typeof(DomainManagerSettings), new XmlRootAttribute(settings.DomainManagerService.Settings.LocalName));
                    var domainManagerSettings = (DomainManagerSettings)serializer.Deserialize(reader);
                    var cacheSettings = domainManagerSettings.CacheSettings;

                    if (cacheSettings != null && cacheSettings.Cache)
                    {
                        var domainCacheSettings = new CacheSettings(cacheSettings) { Name = "DomainCache" };
                        _domainCache = new DomainConfigCache(domainCacheSettings);
                    }
                }
            }
        }

        internal Domain[] GetDomains(string[] domainNames)
        {
            if (!_settings.HasDomainManagerService)
            {
                return new Domain[0];
            }

            List<Domain> managedDomains;

            if (_domainCache != null)
            {
                managedDomains = _domainCache.Get(domainNames);

                if (managedDomains != null && managedDomains.Count(d => d != null) == domainNames.Count())
                {
                    managedDomains.RemoveAll(d => d == null);
                    return managedDomains.ToArray();
                }
            }
            
            using (var client = _settings.DomainManagerService.CreateDomainManagerClient())
            {
                var domains = client.GetDomains(domainNames, EntityStatus.Enabled);
                managedDomains = new List<Domain>();

                foreach (Domain domain in domains)
                {
                    managedDomains.Add(domain);
                }
            }

            if (_domainCache != null)
            {
                _domainCache.Put(managedDomains);
            }

            return managedDomains.ToArray();
        }

        internal Address GetAddress(DirectAddress address)
        {
            Debug.Assert(_settings.HasAddressManager);

            Address managedAddress;

            if (_addressCache != null)
            {
                managedAddress = _addressCache.Get(address.Address);
                if (managedAddress != null)
                {
                    return managedAddress;
                }
            }

            IAddressManager client = CreateAddressManagerClient();
            using (client as IDisposable)
            {
                if (_enabledDomainSearch)
                {
                    managedAddress = client.GetAddressesOrDomain(address, EntityStatus.Enabled);
                }
                else
                {
                    managedAddress = client.GetAddress(address, EntityStatus.Enabled);
                }
            }


            if (_addressCache != null)
            {
                _addressCache.Put(managedAddress);
            }

            return managedAddress;
        }

        internal Address[] GetAddresses(DirectAddressCollection addresses)
        {
            Debug.Assert(_settings.HasAddressManager);

            List<Address> managedAddresses;

            string[] emailAddresses = addresses
                .Select(address => address.Address)
                .ToArray();

            if (_addressCache != null)
            {
                managedAddresses = _addressCache.Get(emailAddresses);

                if (managedAddresses != null && managedAddresses.Count(d => d != null) == addresses.Count())
                {
                    managedAddresses.RemoveAll(d => d == null);
                    return managedAddresses.ToArray();
                }
            }

            IAddressManager client = CreateAddressManagerClient();
            using (client as IDisposable)
            {
                Address[] addressesFound;

                if (_enabledDomainSearch)
                {
                    addressesFound = client.GetAddressesOrDomain(emailAddresses, EntityStatus.Enabled);
                }
                else
                {
                    addressesFound = client.GetAddresses(emailAddresses, EntityStatus.Enabled);
                }

                managedAddresses = addressesFound.ToList();
            }

            if (_addressCache != null)
            {
                _addressCache.Put(managedAddresses);
            }

            return managedAddresses.ToArray();
        }

        private IAddressManager CreateAddressManagerClient()
        {
            return _settings.AddressManager.CreateAddressManagerClient();
        }

    }

    /// <summary>
    /// Handles caching of domains.
    /// </summary>
    public class DomainConfigCache
    {
        const int DefaultCacheTtlSeconds = 60; /* 1 minute */

        Cache<Domain> _cache;
        CacheSettings m_settings;

        /// <summary>
        /// Construct a DomainConfigCache
        /// </summary>
        /// <param name="settings"></param>
        public DomainConfigCache(CacheSettings settings)
        {
            if (settings == null)
            {
                throw new ArgumentNullException(nameof(settings));
            }
            m_settings = new CacheSettings(settings);

            InitializeCache();
        }

        private void InitializeCache()
        {
            if (m_settings.Cache)
            {
                if (!m_settings.CacheTTLSeconds.HasValue)
                {
                    m_settings.CacheTTLSeconds = DefaultCacheTtlSeconds;
                }

                if (m_settings.CacheTTLSeconds <= 0)
                {
                    throw new ArgumentOutOfRangeException(nameof(m_settings.CacheTTLSeconds));
                }

                _cache = new Cache<Domain>(m_settings.Name);
            }
            else
            {
                if (m_settings.NegativeCache)
                {
                    throw new InvalidOperationException("NegativeCacheWithCacheDisabled");
                }
            }
        }

        /// <summary>
        /// Get domain from cache or store
        /// </summary>
        /// <param name="domainNames"></param>
        /// <returns></returns>
        public List<Domain> Get(string[] domainNames)
        {
            var domains = new List<Domain>();

            if (_cache != null)
            {
                foreach (var name in domainNames)
                {
                    var key = name.ToLower();
                    var value = _cache.Get(key);
                    domains.Add(value);
                }
                return domains;
            }
            return null;
        }

        /// <summary>
        /// Put domains in cache
        /// </summary>
        /// <param name="domains"></param>
        public void Put(List<Domain> domains)
        {
            if (_cache != null)
            {
                foreach (var domain in domains)
                {
                    var key = domain.Name.ToLower();
                    _cache.Put(key, domain,
                        m_settings.CacheTTLSeconds != null
                            ? new TimeSpan(0, 0, m_settings.CacheTTLSeconds.Value)
                            : new TimeSpan(0, 0, DefaultCacheTtlSeconds));
                }
            }
        }
    }

    /// <summary>
    /// Handles caching of addresses.
    /// </summary>
    public class AddressConfigCache
    {
        const int DefaultCacheTtlSeconds = 60; /* 1 minute */

        Cache<Address> _cache;
        CacheSettings m_settings;

        /// <summary>
        /// Construct a AddressConfigCache
        /// </summary>
        /// <param name="settings"></param>
        public AddressConfigCache(CacheSettings settings)
        {
            if (settings == null)
            {
                throw new ArgumentNullException(nameof(settings));
            }
            m_settings = new CacheSettings(settings);

            InitializeCache();
        }

        private void InitializeCache()
        {
            if (m_settings.Cache)
            {
                if (!m_settings.CacheTTLSeconds.HasValue)
                {
                    m_settings.CacheTTLSeconds = DefaultCacheTtlSeconds;
                }

                if (m_settings.CacheTTLSeconds <= 0)
                {
                    throw new ArgumentOutOfRangeException(nameof(m_settings.CacheTTLSeconds));
                }

                _cache = new Cache<Address>(m_settings.Name);
            }
            else
            {
                if (m_settings.NegativeCache)
                {
                    throw new InvalidOperationException("NegativeCacheWithCacheDisabled");
                }
            }
        }

        /// <summary>
        /// Get <see cref="Address"/> from cache or store
        /// </summary>
        /// <param name="addressName"></param>
        /// <returns></returns>
        public Address Get(string addressName)
        {
            if (_cache != null)
            {
                var key = addressName.ToLower();
                var value = _cache.Get(key);

                return value;
            }

            return null;
        }

        /// <summary>
        /// Get addresses from cache or store.
        /// </summary>
        /// <param name="addressNames"></param>
        /// <returns></returns>
        public List<Address> Get(string[] addressNames)
        {
            var addresses = new List<Address>();

            if (_cache != null)
            {
                foreach (var name in addressNames)
                {
                    var key = name.ToLower();
                    var value = _cache.Get(key);

                    addresses.Add(value);
                }
                return addresses;
            }

            return null;
        }

        /// <summary>
        /// Put addresses in cache
        /// </summary>
        /// <param name="addresses"></param>
        public void Put(List<Address> addresses)
        {
            if (_cache != null)
            {
                foreach (var address in addresses)
                {
                    var key = address.EmailAddress.ToLower();
                    _cache.Put(key, address,
                        m_settings.CacheTTLSeconds != null
                            ? new TimeSpan(0, 0, m_settings.CacheTTLSeconds.Value)
                            : new TimeSpan(0, 0, DefaultCacheTtlSeconds));
                }
            }
        }
        
        /// 
        /// <summary>
        /// Put address in cache
        /// </summary>
        /// <param name="address"></param>
        public void Put(Address address)
        {
            if (_cache != null)
            {
                var key = address.EmailAddress.ToLower();
                _cache.Put(key, address,
                    m_settings.CacheTTLSeconds != null
                        ? new TimeSpan(0, 0, m_settings.CacheTTLSeconds.Value)
                        : new TimeSpan(0, 0, DefaultCacheTtlSeconds));
            }
        }
    }
}