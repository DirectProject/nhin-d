/* 
 Copyright (c) 2010, Direct Project
 All rights reserved.

 Authors:
    Umesh Madan     umeshma@microsoft.com
    Joe Shook     Joseph.Shook@Surescripts.com

Redistribution and use in source and binary forms, with or without modification, are permitted provided that the following conditions are met:

Redistributions of source code must retain the above copyright notice, this list of conditions and the following disclaimer.
Redistributions in binary form must reproduce the above copyright notice, this list of conditions and the following disclaimer in the documentation and/or other materials provided with the distribution.
Neither the name of The Direct Project (directproject.org) nor the names of its contributors may be used to endorse or promote products derived from this software without specific prior written permission.
THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 
*/
using System;
using System.Collections.Generic;
using System.Linq;
using System.Net.Mail;
using System.Threading.Tasks;
using Health.Direct.Common.Extensions;
using Microsoft.EntityFrameworkCore;

namespace Health.Direct.Config.Store;


/// <summary>
/// Used to manage configured addresses
/// </summary>
public class AddressManager : IEnumerable<Address>
{
    private readonly DirectDbContext _dbContext;

    internal AddressManager(DirectDbContext dbContext)
    {
        _dbContext = dbContext;
    }
    

    /// <summary>
    /// Add a new email address
    /// </summary>
    /// <remarks>
    ///  - Gets the domain of the address and ensures that it exists
    ///  - Then tries to create an entry in the Address table
    ///  - The address is created with EntityStatus.New
    ///  - To use the address, you must enable it
    /// </remarks>
    /// <param name="mailAddress">Mail address object</param>
    public async Task Add(MailAddress mailAddress)
    {
        await Add(mailAddress, EntityStatus.New, "SMTP");
    }
    
    /// <summary>
    /// Add a new email address
    /// </summary>
    /// <remarks>
    ///  - Gets the domain of the address and ensures that it exists
    ///  - Then tries to create an entry in the Address table
    ///  - The address is created in the given state
    /// </remarks>
    /// <param name="mailAddress">Mail address object</param>
    /// <param name="status">entity status</param>
    /// <param name="addressType"></param>
    public async Task Add(MailAddress mailAddress, EntityStatus status, string addressType)
    {
        if (mailAddress == null)
        {
            throw new ArgumentNullException(nameof(mailAddress));
        }

        if (mailAddress == null)
        {
            throw new ArgumentNullException(nameof(mailAddress));
        }

        var domain = await _dbContext.Domains
            .Where(d => d.Name.ToUpper() == mailAddress.Host.ToUpper())
            .SingleOrDefaultAsync();
        
        if (domain == null)
        {
            throw new ConfigStoreException(ConfigStoreError.InvalidDomain);
        }

        var address = new Address(domain.ID, mailAddress) { Type = addressType, Status = status };

        await Add(address);

        await _dbContext.SaveChangesAsync();
    }

    /// <summary>
    /// Add an address to the store
    /// </summary>
    /// <param name="address">address object</param>        
    public async Task<Address?> Add(Address address)
    {
        if (address == null)
        {
            throw new ConfigStoreException(ConfigStoreError.InvalidAddress);
        }

        if (!address.IsValidMailAddress())
        {
            throw new ConfigStoreException(ConfigStoreError.InvalidAddress);
        }

        _dbContext.Addresses.Add(address);
        await _dbContext.SaveChangesAsync();

        return address;
    }
    
    /// <summary>
    /// Add a set of addresses to the store in a single transaction
    /// </summary>
    /// <param name="addresses">address set</param>
    public async Task Add(IEnumerable<Address> addresses)
    {
        if (addresses == null)
        {
            throw new ArgumentNullException(nameof(addresses));
        }

        foreach(var address in addresses)
        {
            _dbContext.Addresses.Add(address);
        }
            
        await _dbContext.SaveChangesAsync();
    }
    
    
    public async Task Update(Address address)
    {
        if (address == null)
        {
            throw new ConfigStoreException(ConfigStoreError.InvalidAddress);
        }
        if (!address.IsValidMailAddress())
        {
            throw new ConfigStoreException(ConfigStoreError.InvalidAddress);
        }
        
        _dbContext.Addresses.Attach(address);
        
        await _dbContext.SaveChangesAsync();
    }

    public async Task Update(IEnumerable<Address> addresses)
    {
        if (addresses == null)
        {
            throw new ArgumentNullException(nameof(addresses));
        }
        
        foreach(var address in addresses)
        {
            if (!address.IsValidMailAddress())
            {
                throw new ConfigStoreException(ConfigStoreError.InvalidAddress);
            }

            _dbContext.Addresses.Attach(address);
        }

        await _dbContext.SaveChangesAsync();
    }

    public async Task<int> Count()
    {
        return await _dbContext.Addresses.CountAsync();
    }

    public async Task<int> Count(long domainId)
    {
        return await _dbContext.Addresses.CountAsync(a => a.DomainID == domainId);
    }

    public async Task<List<Address>> GetAllForDomain(string domainName
                    , int maxResults)
    {

        var entities = await _dbContext.Addresses
            .Include(a => a.Domain)
            .Where(a => a.Domain.Name.ToUpper().Contains(domainName.ToUpper()))
            .OrderBy(a => a.EmailAddress)
            .Take(maxResults)
            .ToListAsync();

        return entities;
    }
    
    public async Task<Address?> Get(string emailAddress)
    {
        return await _dbContext.Addresses
            .Where(a => a.EmailAddress.ToUpper() == emailAddress.ToUpper())
            .SingleOrDefaultAsync();
    }
    
    public async Task<List<Address>> Get(string[] emailAddresses)
    {
        return await Get(emailAddresses, false, null);
    }

    public async Task<List<Address>> Get(string[] emailAddresses, bool domainSearchEnabled)
    {
        return await Get(emailAddresses, domainSearchEnabled, null);
    }

    
    public async Task<List<Address>> Get(string[] emailAddresses, EntityStatus? status)
    {
        VerifyEmailAddresses(emailAddresses);

        if (status == null)
        {
            return await _dbContext.Addresses
                .Where(a => emailAddresses.Select(e => e.ToUpper()).Contains(a.EmailAddress.ToUpper()))
                .ToListAsync();
        }

        return await _dbContext.Addresses
            .Where(a => emailAddresses.Select(e => e.ToUpper()).Contains(a.EmailAddress.ToUpper())
                        && a.Status == status)
            .ToListAsync();
    }

    public async Task<List<Address>> Get(string[] emailAddresses, bool domainSearchEnabled, EntityStatus? status)
    {
        var addresses = await Get(emailAddresses, status);
        
        if (domainSearchEnabled)
        {
            var addressList = new List<Address>();
            foreach (var emailAddress in emailAddresses)
            {
                string enclosureEmailAddress = emailAddress;
                var existingAddress = addresses
                    .SingleOrDefault(a => 
                        a.EmailAddress.ToUpper() == enclosureEmailAddress.ToUpper());
                if (existingAddress != null)
                {
                    addressList.Add(existingAddress);
                    continue;
                }
                await AutoMapDomains(enclosureEmailAddress, addressList, status);
            }

            return addressList;
        }

        return addresses;
    }


    private async Task AutoMapDomains(string enclosureEmailAddress, List<Address> addressList, EntityStatus? status)
    {
        var mailAddress = new MailAddress(enclosureEmailAddress);
        var domain = await _dbContext.Domains
            .Where(d => d.Name.ToUpper() == mailAddress.Host.ToUpper())
            .SingleOrDefaultAsync();

        if (domain == null || 
            (status.HasValue && domain.Status != status)
            ) return;

        var address = new Address(domain.ID, mailAddress);
        address.Type = "SMTP";
        address.Status = domain.Status;
        addressList.Add(address);
    }
    
    public async Task<List<Address>> Get(string emailAddress, int maxResults)
    {
        if (string.IsNullOrEmpty(emailAddress))
        {
            return await _dbContext.Addresses
                .OrderBy(a => a.EmailAddress)
                .Take(maxResults)
                .ToListAsync();
        }

        return await _dbContext.Addresses
            .Where(a => String.Compare(a.EmailAddress.ToUpper(), emailAddress.ToUpper()) > 0)
            .OrderBy(a => a.EmailAddress)
            .Take(maxResults)
            .ToListAsync();
    }

    public async Task<List<Address>> Get(long domainId, string emailAddress, int maxResults)
    {
        if (string.IsNullOrEmpty(emailAddress))
        {
            return await _dbContext.Addresses
                .Where(a => a.DomainID == domainId)
                .OrderBy(a => a.EmailAddress)
                .Take(maxResults)
                .ToListAsync();
        }

        return await _dbContext.Addresses
            .Where(a => a.DomainID == domainId
                        && String.Compare(a.EmailAddress.ToUpper(), emailAddress.ToUpper()) > 0)
            .OrderBy(a => a.EmailAddress)
            .Take(maxResults)
            .ToListAsync();
    }
            
    public async Task<List<Address>> Get(long[] addressIDs)
    {
        return await Get(addressIDs, null);
    }
    
    public async Task<List<Address>> Get(long[] addressIDs, EntityStatus? status)
    {
        if (status == null)
        {
            return await _dbContext.Addresses
                .Where(a => addressIDs.Contains(a.ID))
                .ToListAsync();
        }
        
        return await _dbContext.Addresses
            .Where(a => addressIDs.Contains(a.ID))
            .ToListAsync();
    }
    
    public async Task<List<Address>> Get(long addressID)
    {
        return await _dbContext.Addresses
            .Where(a => a.ID == addressID)
            .ToListAsync();
    }
    
    public async Task Remove(string emailAddress)
    {
        if (string.IsNullOrEmpty(emailAddress))
        {
            throw new ConfigStoreException(ConfigStoreError.InvalidEmailAddress);
        }

        var addresses = await _dbContext.Addresses
            .Where(a => a.EmailAddress.ToUpper() == emailAddress.ToUpper())
            .ToListAsync();

        foreach (var address in addresses)
        {
            _dbContext.Addresses.Remove(address);
        }

        await _dbContext.SaveChangesAsync();
    }
    
    public async Task Remove(IEnumerable<string> emailAddresses)
    {
        foreach(string emailAddress in emailAddresses)
        {
            await Remove(emailAddress);
        }

        await _dbContext.SaveChangesAsync();
    }
    
    public async Task RemoveDomain(long domainId)
    {
        var addresses = await _dbContext.Addresses
            .Where(a => a.DomainID == domainId)
            .ToListAsync();

        foreach (var address in addresses)
        {
            _dbContext.Addresses.Remove(address);
        }
        await _dbContext.SaveChangesAsync();
    }
    
    public async Task SetStatus(long domainId, EntityStatus status)
    {
        var addresses = await _dbContext.Addresses
            .Where(a => a.DomainID == domainId)
            .ToListAsync();

        foreach (var address in addresses)
        {
            if (address != null)
            {
                address.Status = status;
            }
        }

        await _dbContext.SaveChangesAsync();
    }
    
    void VerifyEmailAddresses(string[] emailAddresses)
    {
        if (emailAddresses.IsNullOrEmpty())
        {
            throw new ConfigStoreException(ConfigStoreError.InvalidEmailAddress);
        }

        foreach (var emailAddress in emailAddresses)
        {
            if (string.IsNullOrEmpty(emailAddress))
            {
                throw new ConfigStoreException(ConfigStoreError.InvalidEmailAddress);
            }
        }
    }

    public IEnumerator<Address> GetEnumerator()
    {
        foreach (Address address in _dbContext.Addresses)
        {
            yield return address;
        }
    }


    #region IEnumerable Members

    System.Collections.IEnumerator System.Collections.IEnumerable.GetEnumerator()
    {
        return GetEnumerator();
    }

    #endregion

}