﻿/* 
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
using System.Threading.Tasks;
using Health.Direct.Common.Extensions;
using Health.Direct.Config.Store.Entity;
using Microsoft.EntityFrameworkCore;

namespace Health.Direct.Config.Store;

public interface IDomainManager
{
    Task<Domain> Add(string name);
    Task<Domain> Add(Domain domain);
    Task<int> Count();
    Task<Domain?> Get(string name);
    Task<List<Domain>> Get(string[] names);
    Task<List<Domain>> Get(string[] names, EntityStatus? status);
    Task<List<Domain>> GetByAgentName(string agentName, EntityStatus? status);
    Task<List<Domain>> Get(string lastDomain, int maxResults);
    Task<Domain?> Get(long id);
    Task Update(Domain domain);
    Task Remove(string name);
    IEnumerator<Domain> GetEnumerator();
}

public class DomainManager : IEnumerable<Domain>, IDomainManager
{
    private readonly DirectDbContext _dbContext;

    public  DomainManager(DirectDbContext dbContext)
    {
        _dbContext = dbContext;
    }


    public async Task<Domain> Add(string name)
    {
        if (!Domain.IsValidEmailDomain(name))
        {
            throw new ConfigStoreException(ConfigStoreError.InvalidDomain);
        }

        var domain = new Domain(name);
        _dbContext.Domains.Add(domain);
        await _dbContext.SaveChangesAsync();
        return domain;
    }
   
   
    public async Task<Domain> Add(Domain domain)
    {
        if (domain == null)
        {
            throw new ConfigStoreException(ConfigStoreError.InvalidDomain);
        }

        if (!domain.IsValidEmailDomain())
        {
            throw new ConfigStoreException(ConfigStoreError.InvalidDomain);
        }

        _dbContext.Domains.Add(domain); ;
        await _dbContext.SaveChangesAsync();

        return domain;
    }
    
    
    public async Task<int> Count()
    {
        return await _dbContext.Domains.CountAsync();
    }
            
    public async Task<Domain?> Get(string name)
    {
        if (string.IsNullOrEmpty(name))
        {
            throw new ConfigStoreException(ConfigStoreError.InvalidDomainName);
        }

        return await _dbContext.Domains
            .Where(d => d.Name.ToUpper() == name.ToUpper())
            .SingleOrDefaultAsync();
    }
    
    
    public async Task<List<Domain>> Get(string[] names)
    {
        return await Get(names, null);
    }
        
    public async Task<List<Domain>> Get(string[] names, EntityStatus? status)
    {
        
        if (names.IsNullOrEmpty())
        {
            throw new ConfigStoreException(ConfigStoreError.InvalidDomainName);
        }
        
        if (status == null)
        {
            return await _dbContext.Domains
                .Where(d => names.Contains(d.Name))
                .ToListAsync();
        }

        return await _dbContext.Domains
            .Where(d => names.Contains(d.Name)
                        && d.Status == status)
            .ToListAsync();
    }

    public async Task<List<Domain>> GetByAgentName(string agentName, EntityStatus? status)
    {
        if (string.IsNullOrEmpty(agentName))
        {
            throw new ConfigStoreException(ConfigStoreError.InvalidAgentName);
        }

        if (status == null)
        {
            return await _dbContext.Domains
                .Where(d => d.AgentName == agentName)
                .ToListAsync();
        }

        return await _dbContext.Domains
            .Where(d => d.AgentName == agentName
                        && d.Status == status)
            .ToListAsync();
    }
    
    public async Task<List<Domain>> Get(string lastDomain, int maxResults)
    {
        if (string.IsNullOrEmpty(lastDomain))
        {
            return await _dbContext.Domains
                .OrderBy(d => d.Name)
                .Take(maxResults)
                .ToListAsync();
        }

        return await _dbContext.Domains
            .Where(d => String.Compare(d.Name, lastDomain) > 0)
            .OrderBy(d => d.Name)
            .Take(maxResults)
            .ToListAsync();
    }

    public async Task<Domain?> Get(long id)
    {
        return await _dbContext.Domains
            .Where(d => d.ID == id)
            .SingleOrDefaultAsync();
    }

    public async Task Update(Domain domain)
    {
        if (domain == null)
        {
            throw new ConfigStoreException(ConfigStoreError.InvalidDomain);
        }

        var update = new Domain();
        update.CopyFixed(domain);

        _dbContext.Domains.Attach(update);
        update.ApplyChanges(domain);
        await _dbContext.SaveChangesAsync();
    }
    
    public async Task Remove(string name)
    {
        if (string.IsNullOrEmpty(name))
        {
            throw new ConfigStoreException(ConfigStoreError.InvalidDomainName);
        }

        var domains = await _dbContext.Domains
            .Where(d => d.Name == name)
            .ToListAsync();

        foreach (var domain in domains)
        {
            _dbContext.Domains.Remove(domain);
        }
    }
    
    public IEnumerator<Domain> GetEnumerator()
    {
        foreach(var domain in _dbContext.Domains)
        {
            yield return domain;
        }
    }

    #region IEnumerable Members

    System.Collections.IEnumerator System.Collections.IEnumerable.GetEnumerator()
    {
        return GetEnumerator();
    }

    #endregion

}