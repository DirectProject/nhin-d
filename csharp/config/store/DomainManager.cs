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
using System.Threading.Tasks;
using Health.Direct.Common.Extensions;
using Health.Direct.Config.Store.Entity;
using Microsoft.EntityFrameworkCore;

namespace Health.Direct.Config.Store;

public class DomainManager : IEnumerable<Domain>
{
    internal DomainManager(ConfigStore store)
    {
        Store = store;
    }

    internal ConfigStore Store { get; }

    public async Task<Domain> Add(string name)
    {
        await using var db = Store.CreateContext();
        var domain = await Add(db, name);
        await db.SaveChangesAsync();
        return domain;
    }
    
    public async Task<Domain> Add(ConfigDatabase db, string name)
    {
        var domain = new Domain(name);
        Add(db, domain);
        return domain;
    }

    public async Task<Domain> Add(Domain domain)
    {
        await using var db = Store.CreateContext();
        Add(db, domain);
        await db.SaveChangesAsync();

        return domain;
    }

    public void Add(ConfigDatabase db, Domain domain)
    {
        if (db == null)
        {
            throw new ArgumentNullException(nameof(db));
        }
        if (domain == null)
        {
            throw new ConfigStoreException(ConfigStoreError.InvalidDomain);
        }
        
        if (!domain.IsValidEmailDomain())
        {
            throw new ConfigStoreException(ConfigStoreError.InvalidDomain);
        }
        
        db.Domains.Add(domain);
    }
    
    public async Task<int> Count()
    {
        await using var db = Store.CreateReadContext();
        return await db.Domains.CountAsync();
    }
            
    public async Task<Domain> Get(string name)
    {
        await using var db = Store.CreateReadContext();
        return await Get(db, name);
    }

    public async Task<Domain> Get(ConfigDatabase db, string name)
    {
        if (db == null)
        {
            throw new ArgumentNullException(nameof(db));
        }
        if (string.IsNullOrEmpty(name))
        {
            throw new ConfigStoreException(ConfigStoreError.InvalidDomainName);
        }

        return await db.Domains
            .Where(d => d.Name.ToUpper() == name.ToUpper())
            .SingleOrDefaultAsync();
    }
    
    public async Task<List<Domain>> Get(string[] names)
    {
        return await Get(names, null);
    }
            
    public async Task<List<Domain>> Get(ConfigDatabase db, string[] names)
    {
        return await Get(db, names, null);
    }

    public async Task<List<Domain>> Get(string[] names, EntityStatus? status)
    {
        await using var db = Store.CreateReadContext();
        return await Get(db, names, status);
    }

    public async Task<List<Domain>> Get(string groupName, EntityStatus? status)
    {
        await using var db = Store.CreateReadContext();
        return await Get(db, groupName, status);
    }
    
    public async Task<List<Domain>> Get(ConfigDatabase db, string[] names, EntityStatus? status)
    {
        if (db == null)
        {
            throw new ArgumentNullException(nameof(db));
        }
        if (names.IsNullOrEmpty())
        {
            throw new ConfigStoreException(ConfigStoreError.InvalidDomainName);
        }
        
        if (status == null)
        {
            return await db.Domains
                .Where(d => names.Contains(d.Name))
                .ToListAsync();
        }

        return await db.Domains
            .Where(d => names.Contains(d.Name)
                        && d.Status == status)
            .ToListAsync();
    }

    public async Task<List<Domain>> Get(ConfigDatabase db, string agentName, EntityStatus? status)
    {
        if (db == null)
        {
            throw new ArgumentNullException(nameof(db));
        }
        if (string.IsNullOrEmpty(agentName))
        {
            throw new ConfigStoreException(ConfigStoreError.InvalidAgentName);
        }

        if (status == null)
        {
            return await db.Domains
                .Where(d => d.AgentName == agentName)
                .ToListAsync();
        }

        return await db.Domains
            .Where(d => d.AgentName == agentName
                        && d.Status == status)
            .ToListAsync();
    }

    public async Task<List<Domain>> Get(string lastDomain, int maxResults)
    {
        await using var db = Store.CreateReadContext();
        return await Get(db, lastDomain, maxResults);
    }

    public async Task<List<Domain>> Get(ConfigDatabase db, string lastDomain, int maxResults)
    {
        if (db == null)
        {
            throw new ArgumentNullException(nameof(db));
        }

        if (string.IsNullOrEmpty(lastDomain))
        {
            return await db.Domains
                .OrderBy(d => d.Name)
                .Take(maxResults)
                .ToListAsync();
        }

        return await db.Domains
            .Where(d => String.Compare(d.Name, lastDomain) > 0)
            .OrderBy(d => d.Name)
            .Take(maxResults)
            .ToListAsync();
    }

    public async Task<Domain> Get(long id)
    {
        await using var db = Store.CreateContext();
        return await Get(db, id);
    }

    public async Task<Domain> Get(ConfigDatabase db, long id)
    {
        if (db == null)
        {
            throw new ArgumentNullException(nameof(db));
        }

        return await db.Domains
            .Where(d => d.ID == id)
            .SingleOrDefaultAsync();
    }

    public async Task Update(Domain domain)
    {
        await using var db = Store.CreateContext();
        Update(db, domain);
        await db.SaveChangesAsync();
    }
    
    protected void Update(ConfigDatabase db, Domain domain)
    {
        if (db == null)
        {
            throw new ArgumentNullException(nameof(db));
        }
        if (domain == null)
        {
            throw new ConfigStoreException(ConfigStoreError.InvalidDomain);
        }

        var update = new Domain(); 
        update.CopyFixed(domain);

        db.Domains.Attach(update);
        update.ApplyChanges(domain);           
    }
            
    public async Task Remove(string name)
    {
        await using ConfigDatabase db = Store.CreateContext();
        await Remove(db, name);
        await db.SaveChangesAsync();
    }

    public async Task Remove(ConfigDatabase db, string name)
    {
        if (db == null)
        {
            throw new ArgumentNullException(nameof(db));
        }
        
        if (string.IsNullOrEmpty(name))
        {
            throw new ConfigStoreException(ConfigStoreError.InvalidDomainName);
        }

        var domains = await db.Domains
            .Where(d => d.Name == name)
            .ToListAsync();

        foreach (var domain in domains)
        {
            db.Domains.Remove(domain);
        }
    }
    
    public IEnumerator<Domain> GetEnumerator()
    {
        using var db = Store.CreateContext();
        foreach(var domain in db.Domains)
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