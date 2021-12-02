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
using Health.Direct.Config.Store.Entity;
using Microsoft.EntityFrameworkCore;

namespace Health.Direct.Config.Store;


/// <summary>
/// Used to manage configured addresses
/// </summary>
public class AddressManager : IEnumerable<Address>
{
    internal AddressManager(ConfigStore store)
    {
        Store = store;
    }
    
    internal ConfigStore Store { get; }

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

        await using var db = Store.CreateContext();
        Add(db, mailAddress, status, addressType);
        await db.SaveChangesAsync();
    }

    /// <summary>
    /// Add a new email address within the given database context
    /// The operation is performed within any transactions in the context
    /// </summary>
    /// <remarks>
    ///  - Gets the domain of the address and ensures that it exists
    ///  - Then tries to create an entry in the Address table
    ///  - The address is created in the given state
    /// </remarks>
    /// <param name="db">db context</param>
    /// <param name="mailAddress">Mail address object</param>
    /// <param name="status">entity status</param>
    /// <param name="addressType"></param>
    public async Task<Address> Add(ConfigDatabase db, MailAddress mailAddress, EntityStatus status, string addressType)
    {
        if (db == null)
        {
            throw new ArgumentNullException(nameof(db));
        }
        if (mailAddress == null)
        {
            throw new ArgumentNullException(nameof(mailAddress));
        }

        var domain = Store.Domains.Get(db, mailAddress.Host);
        if (domain == null)
        {
            throw new ConfigStoreException(ConfigStoreError.InvalidDomain);
        }

        var address = new Address(domain.ID, mailAddress) {Type = addressType, Status = status};

        return await Add(db, address);
    }
    
    /// <summary>
    /// Add an address to the store
    /// </summary>
    /// <param name="address">address object</param>        
    public async Task<Address> Add(Address address)
    {
        await using var db = Store.CreateContext();
        await Add(db, address);
        await db.SaveChangesAsync();

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

        await using var db = Store.CreateContext();

        foreach(var address in addresses)
        {
            await Add(db, address);
        }
            
        await db.SaveChangesAsync();
    }
    
    /// <summary>
    /// Add an address to the database using the given database context
    /// The address will be added within the context's currently active transaction 
    /// </summary>
    /// <param name="db">database context to use</param>
    /// <param name="address">address object</param>
    public async Task<Address> Add(ConfigDatabase db, Address address)
    {
        if (db == null)
        {
            throw new ArgumentNullException(nameof(db));
        }
        
        if (address == null)
        {
            throw new ConfigStoreException(ConfigStoreError.InvalidAddress);
        }
        
        if (!address.IsValidMailAddress())
        {
            throw new ConfigStoreException(ConfigStoreError.InvalidAddress);
        }
        
        var entity = await db.Addresses.AddAsync(address);

        return entity.Entity;
    }
    
    public async Task Update(Address address)
    {
        await using var db = Store.CreateContext();
        Update(db, address);
        await db.SaveChangesAsync();
    }

    public async Task Update(IEnumerable<Address> addresses)
    {
        if (addresses == null)
        {
            throw new ArgumentNullException(nameof(addresses));
        }

        await using var db = Store.CreateContext();
        
        foreach(var address in addresses)
        {
            Update(db, address);
        }

        await db.SaveChangesAsync();
    }
    
    void Update(ConfigDatabase db, Address address)
    {
        if (db == null)
        {
            throw new ArgumentNullException(nameof(db));
        }

        if (address == null)
        {
            throw new ConfigStoreException(ConfigStoreError.InvalidAddress);
        }
        if (!address.IsValidMailAddress())
        {
            throw new ConfigStoreException(ConfigStoreError.InvalidAddress);
        }
        
        var update = new Address();
        update.CopyFixed(address);
        
        db.Addresses.Attach(update);
        update.ApplyChanges(address);             
    }
    
    public async Task<int> Count(long domainID)
    {
        await using var db = Store.CreateReadContext();
        return await db.Addresses.CountAsync(a => a.DomainID == domainID);
    }

    public async Task<List<Address>> GetAllForDomain(string domainName
                    , int maxResults)
    {
        await using var db = Store.CreateReadContext();
        return await GetAllForDomain(db, domainName, maxResults);
    }

    public async Task<List<Address>> GetAllForDomain(ConfigDatabase db
                               , string domainName
                               , int maxResults)
    {
        if (db == null)
        {
            throw new ArgumentNullException(nameof(db));
        }

        var entities = await db.Addresses
            .Include(a => a.Domain)
            .Where(a => String.Compare(a.Domain.Name, domainName) > 0)
            .OrderBy(a => a.EmailAddress)
            .Take(maxResults)
            .ToListAsync();

        return entities;
    }
           
    public async Task<Address> Get(string emailAddress)
    {
        await using var db = Store.CreateReadContext();
        return await Get(db, emailAddress);
    }
    
    public async Task<Address> Get(ConfigDatabase db, string emailAddress)
    {
        if (db == null)
        {
            throw new ArgumentNullException(nameof(db));
        }

        return await db.Addresses
            .Where(a => a.EmailAddress == emailAddress)
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

    public async Task<IEnumerable<Address>> Get(ConfigDatabase db, string[] emailAddresses)
    {
        return await Get(db, emailAddresses, null);
    }

    public async Task<List<Address>> Get(string[] emailAddresses, EntityStatus? status)
    {
        return await Get(emailAddresses, false, status);
    }

    public async Task<List<Address>> Get(string[] emailAddresses, bool domainSearchEnabled, EntityStatus? status)
    {
        using var db = Store.CreateReadContext();
        var addresses = await Get(db, emailAddresses, status);
        
        if (domainSearchEnabled)
        {
            var addressList = new List<Address>();
            foreach (var emailAddress in emailAddresses)
            {
                string enclosureEmailAddress = emailAddress;
                var existingAddress = addresses
                    .SingleOrDefault(a => 
                        a.EmailAddress.Equals(enclosureEmailAddress, StringComparison.OrdinalIgnoreCase));
                if (existingAddress != null)
                {
                    addressList.Add(existingAddress);
                    continue;
                }
                AutoMapDomains(enclosureEmailAddress, addressList, status);
            }
            return addressList;
        }

        return addresses;
    }


    private void AutoMapDomains(string enclosureEmailAddress, List<Address> addressList, EntityStatus? status)
    {
        var mailAddress = new MailAddress(enclosureEmailAddress);
        var domain = Store.Domains.Get(mailAddress.Host);

        if (domain == null || 
            (status.HasValue && domain.Status != status)
            ) return;

        var address = new Address(domain.ID, mailAddress);
        address.Type = "SMTP";
        address.Status = domain.Status;
        addressList.Add(address);
    }


    public async Task<List<Address>> Get(ConfigDatabase db, string[] emailAddresses, EntityStatus? status)
    {
        if (db == null)
        {
            throw new ArgumentNullException(nameof(db));
        }

        VerifyEmailAddresses(emailAddresses);

        if (status == null)
        {
            return await db.Addresses
                .Where(a => emailAddresses.Contains(a.EmailAddress))
                .ToListAsync();
        }

        return await db.Addresses
            .Where(a => emailAddresses.Contains(a.EmailAddress)
                        && a.Status == status)
            .ToListAsync();
    }

    public async Task<List<Address>> Get(string lastAddressID, int maxResults)
    {
        await using var db = Store.CreateReadContext();
        return await Get(db, lastAddressID, maxResults);
    }

    public async Task<List<Address>> Get(ConfigDatabase db, string emailAddress, int maxResults)
    {
        if (db == null)
        {
            throw new ArgumentNullException(nameof(db));
        }

        if (string.IsNullOrEmpty(emailAddress))
        {
            return await db.Addresses
                .OrderBy(a => a.EmailAddress)
                .Take(maxResults)
                .ToListAsync();
        }

        return await db.Addresses
            .Where(a => String.Compare(a.EmailAddress, emailAddress) > 0)
            .OrderBy(a => a.EmailAddress)
            .Take(maxResults)
            .ToListAsync();
    }

    public async Task<List<Address>> Get(long domainID, string lastAddressID, int maxResults)
    {
        await using var db = Store.CreateReadContext();
        return await Get(db, domainID, lastAddressID, maxResults);
    }

    public async Task<List<Address>> Get(ConfigDatabase db, long domainID, string emailAddress, int maxResults)
    {
        if (db == null)
        {
            throw new ArgumentNullException(nameof(db));
        }

        if (string.IsNullOrEmpty(emailAddress))
        {
            return await db.Addresses
                .Where(a => a.DomainID == domainID)
                .OrderBy(a => a.EmailAddress)
                .Take(maxResults)
                .ToListAsync();
        }

        return await db.Addresses
            .Where(a => a.DomainID == domainID
                        && String.Compare(a.EmailAddress, emailAddress) > 0)
            .OrderBy(a => a.EmailAddress)
            .Take(maxResults)
            .ToListAsync();
    }
            
    public async Task<List<Address>> Get(long[] addressIDs)
    {
        return await Get(addressIDs, null);
    }

    public async Task<List<Address>> Get(ConfigDatabase db, long[] addressIDs)
    {
        return await Get(db, addressIDs, null);
    }

    public async Task<List<Address>> Get(long[] addressIDs, EntityStatus? status)
    {
        await using var db = Store.CreateReadContext();
        return await Get(db, addressIDs, status);
    }

    public async Task<List<Address>> Get(ConfigDatabase db, long[] addressIDs, EntityStatus? status)
    {
        if (db == null)
        {
            throw new ArgumentNullException(nameof(db));
        }
        
        if (status == null)
        {
            return await db.Addresses
                .Where(a => addressIDs.Contains(a.ID))
                .ToListAsync();
        }
        
        return await db.Addresses
            .Where(a => addressIDs.Contains(a.ID))
            .ToListAsync();
    }
    
    public async Task<List<Address>> Get(long addressID)
    {
        await using var db = Store.CreateReadContext();
        return await Get(db, addressID);
    }
    
    public async Task<List<Address>> Get(ConfigDatabase db, long addressID)
    {
        if (db == null)
        {
            throw new ArgumentNullException(nameof(db));
        }
        
        return await db.Addresses
            .Where(a => a.ID == addressID)
            .ToListAsync();
    }
            
    public async Task Remove(string emailAddress)
    {
        await using var db = Store.CreateContext();
        await Remove(db, emailAddress);
    }
    
    public async Task Remove(ConfigDatabase db, string emailAddress)
    {
        if (string.IsNullOrEmpty(emailAddress))
        {
            throw new ConfigStoreException(ConfigStoreError.InvalidEmailAddress);
        }

        var addresses = await db.Addresses
            .Where(a => a.EmailAddress == emailAddress)
            .ToListAsync();

        foreach (var address in addresses)
        {
            db.Addresses.Remove(address);
        }
    }

    public void Remove(IEnumerable<string> emailAddresses)
    {
        using var db = Store.CreateContext();
        foreach(string emailAddress in emailAddresses)
        {
            Remove(db, emailAddress);
        }
    }
    
    public async Task RemoveDomain(long domainID)
    {
        await using var db = Store.CreateContext();
        await RemoveDomain(db, domainID);
    }

    public async Task RemoveDomain(ConfigDatabase db, long domainId)
    {
        var addresses = await db.Addresses
            .Where(a => a.DomainID == domainId)
            .ToListAsync();

        foreach (var address in addresses)
        {
            db.Addresses.Remove(address);
        }
    }
    

    public async Task SetStatus(long domainId, EntityStatus status)
    {
        await using var db = Store.CreateContext();

        var addresses = await db.Addresses
            .Where(a => a.DomainID == domainId
                        && a.Status == status)
            .ToListAsync();

        foreach (var address in addresses)
        {
            if (address != null)
            {
                address.Status = status;
            }
        }
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
        using var db = Store.CreateContext();
        foreach (Address address in db.Addresses)
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