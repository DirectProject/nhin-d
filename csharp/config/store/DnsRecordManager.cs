/* 
 Copyright (c) 2010, Direct Project
 All rights reserved.

 Authors:
    Chris Lomonico      chris.lomonico@surescripts.com
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
using System.Threading.Tasks;
using Health.Direct.Common.Extensions;
using Health.Direct.Config.Store.Entity;
using Microsoft.EntityFrameworkCore;

namespace Health.Direct.Config.Store;


public class DnsRecordManager
{
    internal DnsRecordManager(ConfigStore store)
    {
        Store = store;
    }

    internal ConfigStore Store { get; }

    public async Task Add(DnsRecord record)
    {
        await using ConfigDatabase db = Store.CreateContext();
        Add(db, record);
        await db.SaveChangesAsync();
    }

    public void Add(ConfigDatabase db, DnsRecord record)
    {
        if (db == null)
        {
            throw new ArgumentNullException(nameof(db));
        }

        if (record == null)
        {
            throw new ConfigStoreException(ConfigStoreError.InvalidDnsRecord);
        }

        db.DnsRecords.Add(record);
    }

    public async Task Add(DnsRecord[] dnsRecords)
    {
        await using var db = Store.CreateContext();
        Add(db, dnsRecords);
        await db.SaveChangesAsync();
    }

    public void Add(ConfigDatabase db
        , DnsRecord[] dnsRecords)
    {
        if (db == null)
        {
            throw new ArgumentNullException(nameof(db));
        }

        if (dnsRecords == null || dnsRecords.Length.Equals(0))
        {
            return;
        }
        foreach (DnsRecord dnsRecord in dnsRecords)
        {
            db.DnsRecords.Add(dnsRecord);
        }
    }

    public async Task<DnsRecord> Get(long recordID)
    {
        await using var db = Store.CreateReadContext();
        return await Get(db, recordID);
    }

    public async Task<DnsRecord> Get(ConfigDatabase db, long recordID)
    {
        if (db == null)
        {
            throw new ArgumentNullException(nameof(db));
        }

        return await db.DnsRecords
            .SingleOrDefaultAsync(d => d.ID == recordID);
    }

    public async Task<List<DnsRecord>> Get(long[] recordIDs)
    {
        if (recordIDs.IsNullOrEmpty())
        {
            throw new ConfigStoreException(ConfigStoreError.InvalidIDs);
        }

        await using var db = Store.CreateReadContext();

        return await db.DnsRecords
            .Where(d => recordIDs.Contains(d.ID))
            .ToListAsync();
    }


    public async Task<List<DnsRecord>> Get(string domainName)
    {
        if (string.IsNullOrEmpty(domainName))
        {
            throw new ConfigStoreException(ConfigStoreError.InvalidDomainName);
        }

        await using var db = Store.CreateReadContext();
        return await Get(db, domainName);
    }


    public async Task<List<DnsRecord>> Get(ConfigDatabase db
        , string domainName)
    {
        return await db.DnsRecords
            .Where(d => d.DomainName == domainName)
            .ToListAsync();
    }

    public async Task<List<DnsRecord>> Get(string domainName
        , Common.DnsResolver.DnsStandard.RecordType typeId)
    {
        if (string.IsNullOrEmpty(domainName))
        {
            throw new ConfigStoreException(ConfigStoreError.InvalidDomainName);
        }

        await using var db = Store.CreateReadContext();
        return await Get(db, domainName, typeId);
    }


    public async Task<List<DnsRecord>> Get(ConfigDatabase db
        , string domainName
        , Common.DnsResolver.DnsStandard.RecordType typeId)
    {
        return await db.DnsRecords
                .Where(d => d.DomainName == domainName
                    && d.TypeID == (int)typeId)
                .ToListAsync();
    }

    public async Task<List<DnsRecord>> Get(long lastRecordId
        , int maxResults
        , Common.DnsResolver.DnsStandard.RecordType typeId)
    {
        await using var db = Store.CreateReadContext();

        return await Get(db
            , lastRecordId
            , maxResults
            , typeId);
    }

    public async Task<List<DnsRecord>> Get(ConfigDatabase db
        , long lastRecordId
        , int maxResults
        , Common.DnsResolver.DnsStandard.RecordType typeId)
    {
        var entities = await db.DnsRecords
            .Where(a => a.ID > lastRecordId && a.TypeID == (int)typeId)
            .OrderBy(a => a.ID)
            .Take(maxResults)
            .ToListAsync();

        return entities;
    }

    public async Task<List<DnsRecord>> Get(long lastRecordId, int maxResults)
    {
        await using var db = Store.CreateReadContext();
        return await Get(db, lastRecordId, maxResults);
    }

    public async Task<List<DnsRecord>> Get(ConfigDatabase db, long lastRecordId, int maxResults)
    {
        var entities = await db.DnsRecords
            .Where(a => a.ID > lastRecordId)
            .OrderBy(a => a.ID)
            .Take(maxResults)
            .ToListAsync();

        return entities;
    }

    /// <summary>
    /// simple method to remove an dns record by CertPolicyId 
    /// </summary>
    /// <param name="dnsRecord">DnsRecord instance to be removed</param>
    public async Task Remove(DnsRecord dnsRecord)
    {
        await using var db = Store.CreateContext();
        db.DnsRecords.Attach(dnsRecord);
        Remove(db, dnsRecord);
        await db.SaveChangesAsync();
    }

    /// <summary>
    /// simple method to remove an dns record by CertPolicyId 
    /// </summary>
    ///  <param name="db">database context to use</param>
    /// <param name="dnsRecord">DnsRecord instance to be removed</param>
    public void Remove(ConfigDatabase db, DnsRecord dnsRecord)
    {
        db.DnsRecords.Remove(dnsRecord);
    }

    /// <summary>
    /// simple method to remove an dns record by CertPolicyId 
    /// </summary>
    /// <param name="recordId">long holding the id of the record to be deleted</param>
    public async Task Remove(long recordId)
    {
        await using var db = Store.CreateContext();
        await Remove(db, recordId);
    }

    /// <summary>
    /// simple method to remove an dns record by CertPolicyId 
    /// </summary>
    /// <param name="db">database context to use</param>
    /// <param name="recordId">long holding the id of the record to be deleted</param>
    public async Task Remove(ConfigDatabase db, long recordId)
    {
        var dnsRecord = await db.DnsRecords
            .Where(d => d.ID == recordId)
            .SingleOrDefaultAsync();

        if (dnsRecord != null)
        {
            db.DnsRecords.Remove(dnsRecord);
        }
    }

    public async Task Update(DnsRecord dnsRecord)
    {
        await using ConfigDatabase db = Store.CreateContext();
        Update(db, dnsRecord);
        await db.SaveChangesAsync();
    }

    public async Task Update(IEnumerable<DnsRecord> dnsRecords)
    {
        if (dnsRecords == null)
        {
            throw new ArgumentNullException(nameof(dnsRecords));
        }

        await using var db = Store.CreateContext();
        
        foreach (var dnsRecord in dnsRecords)
        {
            Update(db, dnsRecord);
        }

        await db.SaveChangesAsync();
    }

    public void Update(ConfigDatabase db, DnsRecord dnsRecord)
    {
        if (db == null)
        {
            throw new ArgumentNullException(nameof(db));
        }

        if (dnsRecord == null)
        {
            throw new ConfigStoreException(ConfigStoreError.InvalidDnsRecord);
        }

        //
        // TODO: Is this still the right way to do this?
        //
        var update = new DnsRecord();
        update.CopyFixed(update);

        db.DnsRecords.Attach(update);
        update.ApplyChanges(dnsRecord);
    }

    public async Task<int> Count(Common.DnsResolver.DnsStandard.RecordType? recordType)
    {
        await using var db = Store.CreateReadContext();

        return await db.DnsRecords.CountAsync(
            d => d.RecordType == recordType.Value);
    }
}

