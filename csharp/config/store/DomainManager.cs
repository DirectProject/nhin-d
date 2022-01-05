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

using Health.Direct.Common.Extensions;

namespace Health.Direct.Config.Store;

public interface IDomainManager
{
    Task<Domain> Add(string name, CancellationToken token = default);
    Task<Domain> Add(Domain domain, CancellationToken token = default);
    Task<int> Count(CancellationToken token = default);
    Task<Domain?> Get(string name, CancellationToken token = default);
    Task<List<Domain>> Get(List<string> names, CancellationToken token = default);
    Task<List<Domain>> Get(List<string> names, EntityStatus? status, CancellationToken token = default);
    Task<List<Domain>> GetByAgentName(string agentName, EntityStatus? status, CancellationToken token = default);
    Task<List<Domain>> Get(string lastDomain, int maxResults, CancellationToken token = default);
    Task<Domain?> Get(long id, CancellationToken token = default);
    Task Update(Domain domain, CancellationToken token = default);
    Task<bool> Remove(long id, CancellationToken token = default);
    Task<List<string>> GetDomainNames(CancellationToken token = default);
    Task<List<string>> GetDomainNames(string agentName, CancellationToken token = default);
    IEnumerator<Domain> GetEnumerator();
}

public class DomainManager : IEnumerable<Domain>, IDomainManager
{
    private readonly DirectDbContext _dbContext;

    public DomainManager(DirectDbContext dbContext)
    {
        _dbContext = dbContext;
    }


    public virtual async Task<Domain> Add(string name, CancellationToken token = default)
    {
        if (!Domain.IsValidEmailDomain(name))
        {
            throw new ConfigStoreException(ConfigStoreError.InvalidDomain);
        }

        var domain = new Domain(name);
        _dbContext.Domains.Add(domain);
        await _dbContext.SaveChangesAsync(token);

        return domain;
    }


    public virtual async Task<Domain> Add(Domain domain, CancellationToken token = default)
    {
        if (domain == null)
        {
            throw new ConfigStoreException(ConfigStoreError.InvalidDomain);
        }

        if (!domain.IsValidEmailDomain())
        {
            throw new ConfigStoreException(ConfigStoreError.InvalidDomain);
        }

        if (!_dbContext.Database.IsRelational())
        {
            var exists = _dbContext.Domains
                .Any(d => d.Name == domain.Name);

            if (exists)
            {
                throw new ConfigStoreException(ConfigStoreError.UniqueConstraint);
            }
        }

        _dbContext.Domains.Add(domain);
        await _dbContext.SaveChangesAsync(token);

        return domain;
    }


    public virtual async Task<int> Count(CancellationToken token = default)
    {
        return await _dbContext.Domains.CountAsync(token);
    }

    public virtual async Task<Domain?> Get(string name, CancellationToken token = default)
    {
        if (string.IsNullOrEmpty(name))
        {
            throw new ConfigStoreException(ConfigStoreError.InvalidDomainName);
        }

        return await _dbContext.Domains
            .Where(d => d.Name.ToUpper() == name.ToUpper())
            .SingleOrDefaultAsync(cancellationToken: token);
    }


    public virtual async Task<List<Domain>> Get(List<string> names, CancellationToken token = default)
    {
        return await Get(names, null, token);
    }

    public virtual async Task<List<Domain>> Get(List<string> names, EntityStatus? status, CancellationToken token = default)
    {

        if (names.IsNullOrEmpty())
        {
            throw new ConfigStoreException(ConfigStoreError.InvalidDomainName);
        }

        if (status == null)
        {
            return await _dbContext.Domains
                .Where(d => names.Contains(d.Name))
                .ToListAsync(cancellationToken: token);
        }

        return await _dbContext.Domains
            .Where(d => names.Contains(d.Name)
                        && d.Status == status)
            .ToListAsync(cancellationToken: token);
    }

    public virtual async Task<List<Domain>> GetByAgentName(string agentName, EntityStatus? status, CancellationToken token = default)
    {
        if (string.IsNullOrEmpty(agentName))
        {
            throw new ConfigStoreException(ConfigStoreError.InvalidAgentName);
        }

        if (status == null)
        {
            return await _dbContext.Domains
                .Where(d => d.AgentName == agentName)
                .ToListAsync(cancellationToken: token);
        }

        
        return await _dbContext.Domains
            .Where(d => d.AgentName == agentName
                        && d.Status == status)
            .ToListAsync(cancellationToken: token);
    }

    public virtual async Task<List<Domain>> Get(string lastDomain, int maxResults, CancellationToken token = default)
    {
        if (string.IsNullOrEmpty(lastDomain))
        {
            return await _dbContext.Domains
                .OrderBy(d => d.Name)
                .Take(maxResults)
                .ToListAsync(cancellationToken: token);
        }

        return await _dbContext.Domains
            .Where(d => String.Compare(d.Name, lastDomain) > 0)
            .OrderBy(d => d.Name)
            .Take(maxResults)
            .ToListAsync(cancellationToken: token);
    }

    public virtual async Task<Domain?> Get(long id, CancellationToken token = default)
    {
        return await _dbContext.Domains
            .Where(d => d.ID == id)
            .SingleOrDefaultAsync(cancellationToken: token);
    }

    public virtual async Task Update(Domain domain, CancellationToken token = default)
    {
        if (domain == null)
        {
            throw new ConfigStoreException(ConfigStoreError.InvalidDomain);
        }

        domain.UpdateDate = DateTimeHelper.Now;
        _dbContext.Domains.Attach(domain);
        _dbContext.Entry(domain).State = EntityState.Modified;

        await _dbContext.SaveChangesAsync(token);
    }

    public virtual async Task<bool> Remove(long id, CancellationToken token = default)
    {
        var domains = await _dbContext.Domains
            .Where(d => d.ID == id)
            .ToListAsync(cancellationToken: token);

        if (!domains.Any())
        {
            return false;
        }

        foreach (var domain in domains)
        {
            _dbContext.Domains.Remove(domain);
        }

        await _dbContext.SaveChangesAsync(token);

        return true;
    }

    public async Task<List<string>> GetDomainNames(CancellationToken token = default)
    {
        return await _dbContext.Domains
            .Where(d => d.Status == EntityStatus.Enabled)
            .Select(d => d.Name)
            .ToListAsync(cancellationToken: token);
    }

    public async Task<List<string>> GetDomainNames(string agentName, CancellationToken token = default)
    {
        return await _dbContext.Domains
            .Where(d => 
                d.Status == EntityStatus.Enabled
                && d.AgentName == agentName)
            .Select(d => d.Name)
            .ToListAsync(cancellationToken: token);
    }


    public IEnumerator<Domain> GetEnumerator()
    {
        foreach (var domain in _dbContext.Domains)
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