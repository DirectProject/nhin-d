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
    private readonly DirectDbContext _dbContext;

    internal DnsRecordManager(DirectDbContext dbContext)
    {
        _dbContext = dbContext;
    }


    public async Task Add(DnsRecord record)
    {
        if (record == null)
        {
            throw new ConfigStoreException(ConfigStoreError.InvalidDnsRecord);
        }

        _dbContext.DnsRecords.Add(record);

        await _dbContext.SaveChangesAsync();
    }
    
    public async Task Add(List<DnsRecord> dnsRecords)
    {
        if (!dnsRecords.Any())
        {
            return;
        }
        foreach (var dnsRecord in dnsRecords)
        {
            _dbContext.DnsRecords.Add(dnsRecord);
        }

        await _dbContext.SaveChangesAsync();
    }
    
    public async Task<DnsRecord?> Get(long recordId)
    {
        return await _dbContext.DnsRecords
            .SingleOrDefaultAsync(d => d.ID == recordId);
    }

    public async Task<List<DnsRecord>> Get(long[] recordIDs)
    {
        if (recordIDs.IsNullOrEmpty())
        {
            throw new ConfigStoreException(ConfigStoreError.InvalidIDs);
        }
        
        return await _dbContext.DnsRecords
            .Where(d => recordIDs.Contains(d.ID))
            .ToListAsync();
    }
    
    public async Task<List<DnsRecord>> Get(string domainName)
    {
        if (string.IsNullOrEmpty(domainName))
        {
            throw new ConfigStoreException(ConfigStoreError.InvalidDomainName);
        }

        return await _dbContext.DnsRecords
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

        return await _dbContext.DnsRecords
            .Where(d => d.DomainName == domainName
                        && d.TypeID == (int)typeId)
            .ToListAsync();
    }
    
    public async Task<List<DnsRecord>> Get(long lastRecordId
        , int maxResults
        , Common.DnsResolver.DnsStandard.RecordType typeId)
    {

        return await _dbContext.DnsRecords
            .Where(a => a.ID > lastRecordId && a.TypeID == (int)typeId)
            .OrderBy(a => a.ID)
            .Take(maxResults)
            .ToListAsync();
    }
    

    public async Task<List<DnsRecord>> Get(long lastRecordId, int maxResults)
    {
        return await _dbContext.DnsRecords
            .Where(a => a.ID > lastRecordId)
            .OrderBy(a => a.ID)
            .Take(maxResults)
            .ToListAsync();
    }
    
    /// <summary>
    /// simple method to remove an dns record by CertPolicyId 
    /// </summary>
    /// <param name="dnsRecord">DnsRecord instance to be removed</param>
    public async Task Remove(DnsRecord dnsRecord)
    {
        _dbContext.DnsRecords.Attach(dnsRecord);
        _dbContext.DnsRecords.Remove(dnsRecord);
        await _dbContext.SaveChangesAsync();
    }
    
    /// <summary>
    /// simple method to remove an dns record by CertPolicyId 
    /// </summary>
    /// <param name="recordId">long holding the id of the record to be deleted</param>
    public async Task Remove(long recordId)
    {
        var dnsRecord = await _dbContext.DnsRecords
            .Where(d => d.ID == recordId)
            .SingleOrDefaultAsync();

        if (dnsRecord != null)
        {
            _dbContext.DnsRecords.Remove(dnsRecord);
        }

        await _dbContext.SaveChangesAsync();
    }
    
    public async Task Update(DnsRecord dnsRecord)
    {
        if (dnsRecord == null)
        {
            throw new ConfigStoreException(ConfigStoreError.InvalidDnsRecord);
        }

        //
        // TODO: Is this still the right way to do this?
        //
        var update = new DnsRecord();
        update.CopyFixed(update);

        _dbContext.DnsRecords.Attach(update);
        update.ApplyChanges(dnsRecord);

        await _dbContext.SaveChangesAsync();
    }

    public async Task Update(IEnumerable<DnsRecord> dnsRecords)
    {
        if (dnsRecords == null)
        {
            throw new ArgumentNullException(nameof(dnsRecords));
        }
        
        foreach (var dnsRecord in dnsRecords)
        {
            var update = new DnsRecord();
            update.CopyFixed(update);

            _dbContext.DnsRecords.Attach(update);
            update.ApplyChanges(dnsRecord);
        }

        await _dbContext.SaveChangesAsync();
    }


    public async Task<int> Count(Common.DnsResolver.DnsStandard.RecordType? recordType)
    {
        if (recordType == null)
        {
            return await _dbContext.DnsRecords.CountAsync();
        }

        return await _dbContext.DnsRecords.CountAsync(
            d => d.RecordType == recordType.Value);
    }
}

