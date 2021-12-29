/* 
 Copyright (c) 2014, Direct Project
 All rights reserved.

 Authors:
    Joe Shook     Joseph.Shook@Surescripts.com
  
Redistribution and use in source and binary forms, with or without modification, are permitted provided that the following conditions are met:

Redistributions of source code must retain the above copyright notice, this list of conditions and the following disclaimer.
Redistributions in binary form must reproduce the above copyright notice, this list of conditions and the following disclaimer in the documentation and/or other materials provided with the distribution.
Neither the name of The Direct Project (directproject.org) nor the names of its contributors may be used to endorse or promote products derived from this software without specific prior written permission.
THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 
*/

using System;
using System.Collections;
using System.Collections.Generic;
using System.Linq;
using System.Threading.Tasks;
using Health.Direct.Common.Extensions;
using Health.Direct.Config.Store.Entity;
using Microsoft.EntityFrameworkCore;

namespace Health.Direct.Config.Store;

public class CertPolicyManager : IEnumerable<CertPolicy>
{
    private readonly DirectDbContext _dbContext;
    private readonly ICertPolicyValidator? _validator;

    internal CertPolicyManager(DirectDbContext dbContext, ICertPolicyValidator validator)
    {
        _dbContext = dbContext;
        _validator = validator;
    }

    internal CertPolicyManager(DirectDbContext dbContext)
    {
        _dbContext = dbContext;
    }
    
    
    public async Task<CertPolicy> Add(CertPolicy policy)
    {
        if (policy == null)
        {
            throw new ConfigStoreException(ConfigStoreError.InvalidCertPolicy);
        }
        policy.ValidateHasData();

        if (_validator != null && !_validator.IsValidLexicon(policy))
        {
            throw new ConfigStoreException(ConfigStoreError.InvalidCertPolicy);
        }

        _dbContext.CertPolicies.Add(policy);
        await _dbContext.SaveChangesAsync();

        return policy;
    }
    

    public async Task Add(IEnumerable<CertPolicy> policies)
    {
        if (policies == null)
        {
            throw new ArgumentNullException(nameof(policies));
        }
        
        foreach (CertPolicy policy in policies)
        {
            await Add(policy);
        }
    }

    public async Task<int> Count()
    {
        return await _dbContext.CertPolicies.CountAsync();
    }


    public async Task<CertPolicy?> Get(string name)
    {
        if (string.IsNullOrEmpty(name))
        {
            throw new ConfigStoreException(ConfigStoreError.InvalidCertPolicyName);
        }

        return await _dbContext.CertPolicies
            .Where(cp => cp.Name == name)
            .SingleOrDefaultAsync();
    }
    
    public async Task<CertPolicy?> Get(long policyId)
    {
        return await _dbContext.CertPolicies
            .Where(cp => cp.CertPolicyId == policyId)
            .SingleOrDefaultAsync();
    }

    public async Task<List<CertPolicy>> Get(long[] policyIDs)
    {
        if (policyIDs.IsNullOrEmpty())
        {
            throw new ConfigStoreException(ConfigStoreError.InvalidIDs);
        }
        
        return await _dbContext.CertPolicies
            .Where(cp => policyIDs.Contains(cp.CertPolicyId))
            .ToListAsync();
    }

    public async Task<List<CertPolicy>> Get(long lastId, int maxResults)
    {
        return await _dbContext.CertPolicies
            .Where(cp => cp.CertPolicyId > lastId)
            .OrderBy(cp => cp.CertPolicyId)
            .Take(maxResults)
            .ToListAsync();
    }

    public async Task<List<CertPolicy>> GetIncomingByOwner(string owner)
    {
        if (string.IsNullOrEmpty(owner))
        {
            throw new ConfigStoreException(ConfigStoreError.InvalidOwnerName);
        }
        
        var certPolicies = await _dbContext.CertPolicies
            .Include(cp => cp.CertPolicyGroupMaps
                .Where(e => e.ForIncoming))
            .ThenInclude(cpGroup => cpGroup.CertPolicyGroup)
            .ThenInclude(cpg => cpg.CertPolicyGroupDomainMaps
                .Where(e => e.Owner == owner))
            .ToListAsync();

        return certPolicies
            .Where(p => p.CertPolicyGroupMaps
                .Any(m => m.CertPolicyGroup.CertPolicyGroupDomainMaps.Any()))
            .ToList();
    }

    public async Task<List<CertPolicy>> GetIncomingByOwner(string owner, CertPolicyUse use)
    {
        if (string.IsNullOrEmpty(owner))
        {
            throw new ConfigStoreException(ConfigStoreError.InvalidOwnerName);
        }
        
        var certPolicies = await _dbContext.CertPolicies
            .Include(cp => cp.CertPolicyGroupMaps
                .Where(e => e.ForIncoming
                         && e.PolicyUse == use))
            .ThenInclude(cpGroup => cpGroup.CertPolicyGroup)
            .ThenInclude(cpg => cpg.CertPolicyGroupDomainMaps
                .Where(e => e.Owner == owner))
            .ToListAsync();

        return certPolicies
            .Where(p => p.CertPolicyGroupMaps
                .Any(m => m.CertPolicyGroup.CertPolicyGroupDomainMaps.Any()))
            .ToList();
    }

    public async Task<List<CertPolicy>> GetOutgoingByOwner(string owner)
    {
        if (string.IsNullOrEmpty(owner))
        {
            throw new ConfigStoreException(ConfigStoreError.InvalidOwnerName);
        }
        
        var certPolicies = await _dbContext.CertPolicies
            .Include(cp => cp.CertPolicyGroupMaps
                .Where(e => e.ForOutgoing))
            .ThenInclude(cpGroup => cpGroup.CertPolicyGroup)
            .ThenInclude(cpg => cpg.CertPolicyGroupDomainMaps
                .Where(e => e.Owner == owner))
            .ToListAsync();

        return certPolicies
            .Where(p => p.CertPolicyGroupMaps
                .Any(m => m.CertPolicyGroup.CertPolicyGroupDomainMaps.Any()))
            .ToList();
    }

    public async Task<List<CertPolicy>> GetOutgoingByOwner(string owner, CertPolicyUse use)
    {
        if (string.IsNullOrEmpty(owner))
        {
            throw new ConfigStoreException(ConfigStoreError.InvalidOwnerName);
        }

        var certPolicies = await _dbContext.CertPolicies
            .Include(cp => cp.CertPolicyGroupMaps
                .Where(e => e.ForOutgoing
                            && e.PolicyUse == use))
            .ThenInclude(cpGroup => cpGroup.CertPolicyGroup)
            .ThenInclude(cpg => cpg.CertPolicyGroupDomainMaps
                .Where(e => e.Owner == owner))
            .ToListAsync();

        return certPolicies
            .Where(p => p.CertPolicyGroupMaps
                .Any(m => m.CertPolicyGroup.CertPolicyGroupDomainMaps.Any()))
            .ToList();
    }


    public async Task Update(CertPolicy policy)
    {
        if (policy == null)
        {
            throw new ConfigStoreException(ConfigStoreError.InvalidDomain);
        }

        _dbContext.CertPolicies.Attach(policy);

        await _dbContext.SaveChangesAsync();
    }
    

    public async Task Remove(long policyId)
    {
        var certPolicy = await _dbContext.CertPolicies
            .SingleOrDefaultAsync(m => m.CertPolicyId == policyId);

        if (certPolicy != null)
        {
            _dbContext.CertPolicies.Remove(certPolicy);

            var groupMaps = await _dbContext.CertPolicyGroupMaps
                .Where(e => e.CertPolicyId == policyId)
                .ToListAsync();

            foreach (var certPolicyGroupMap in groupMaps)
            {
                _dbContext.CertPolicyGroupMaps.Remove(certPolicyGroupMap);
            }
        }

        await _dbContext.SaveChangesAsync();
    }
    
    public async Task Remove(long[] policyIds)
    {
        if (policyIds.IsNullOrEmpty())
        {
            throw new ConfigStoreException(ConfigStoreError.InvalidIDs);
        }

        for (int i = 0; i < policyIds.Length; ++i)
        {
            var certPolicy = await _dbContext.CertPolicies
                .SingleOrDefaultAsync(m => m.CertPolicyId == policyIds[i]);

            if (certPolicy != null)
            {
                _dbContext.CertPolicies.Remove(certPolicy);
            }
        }

        await _dbContext.SaveChangesAsync();
    }
    
    public async Task Remove(string policyName)
    {
        if (string.IsNullOrEmpty(policyName))
        {
            throw new ConfigStoreException(ConfigStoreError.InvalidCertPolicyName);
        }

        var certPolicyGroupMap = await _dbContext.CertPolicyGroupMaps
            .Include(m => m.CertPolicy)
            .Where(m => m.CertPolicy.Name == policyName)
            .ToListAsync();

        //
        // Todo: check if this deletes CertPolicy also, probably not and I have 
        // to do each table or map EF to delete via cascade.  Save this for testing phase.
        //
        foreach (var policyGroupMap in certPolicyGroupMap)
        {
            _dbContext.CertPolicyGroupMaps.Remove(policyGroupMap);
        }
    }
    

    public IEnumerator<CertPolicy> GetEnumerator()
    {
        
        foreach (CertPolicy policy in _dbContext.CertPolicies)
        {
            yield return policy;
        }
    }

    IEnumerator IEnumerable.GetEnumerator()
    {
        return GetEnumerator();
    }
}
