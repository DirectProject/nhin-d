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
    ICertPolicyValidator m_validator;

    internal CertPolicyManager(ConfigStore store, ICertPolicyValidator validator)
    {
        Store = store;
        m_validator = validator;
    }

    internal CertPolicyManager(ConfigStore store)
    {
        Store = store;
    }

    internal ConfigStore Store { get; }

    
    public async Task<CertPolicy> Add(CertPolicy policy)
    {
        await using var db = Store.CreateContext();
        Add(db, policy);
        await db.SaveChangesAsync();

        return policy;
    }

    public void Add(ConfigDatabase db, CertPolicy policy)
    {
        if (db == null)
        {
            throw new ArgumentNullException(nameof(db));
        }
        if (policy == null)
        {
            throw new ConfigStoreException(ConfigStoreError.InvalidCertPolicy);
        }
        policy.ValidateHasData();

        if (!m_validator.IsValidLexicon(policy))
        {
            throw new ConfigStoreException(ConfigStoreError.InvalidCertPolicy);
        }

        db.CertPolicies.Add(policy);
    }

    public async Task Add(IEnumerable<CertPolicy> policies)
    {
        if (policies == null)
        {
            throw new ArgumentNullException(nameof(policies));
        }

        await using var db = Store.CreateContext();
        foreach (CertPolicy policy in policies)
        {
            Add(db, policy);
        }

        await db.SaveChangesAsync();
    }

    public async Task<int> Count()
    {
        await using var db = Store.CreateReadContext();
        return await db.CertPolicies.CountAsync();
    }


    public async Task<CertPolicy> Get(string name)
    {
        await using var db = Store.CreateReadContext();
        return await Get(db, name);
    }

    public async Task<CertPolicy> Get(ConfigDatabase db, string name)
    {
        if (db == null)
        {
            throw new ArgumentNullException(nameof(db));
        }
        if (string.IsNullOrEmpty(name))
        {
            throw new ConfigStoreException(ConfigStoreError.InvalidCertPolicyName);
        }

        return await db.CertPolicies
            .Where(cp => cp.Name == name)
            .SingleOrDefaultAsync();
    }

    public async Task<CertPolicy> Get(long policyId)
    {
        await using var db = Store.CreateReadContext();

        return await db.CertPolicies
            .Where(cp => cp.ID == policyId)
            .SingleOrDefaultAsync();
    }

    public async Task<List<CertPolicy>> Get(long[] policyIDs)
    {
        if (policyIDs.IsNullOrEmpty())
        {
            throw new ConfigStoreException(ConfigStoreError.InvalidIDs);
        }

        await using var db = Store.CreateReadContext();

        return await db.CertPolicies
            .Where(cp => policyIDs.Contains(cp.ID))
            .ToListAsync();
    }

    public async Task<List<CertPolicy>> Get(long lastId, int maxResults)
    {
        await using var db = Store.CreateReadContext();
        return await Get(db, lastId, maxResults);
    }

    public async Task<List<CertPolicy>> Get(ConfigDatabase db, long lastId, int maxResults)
    {
        if (db == null)
        {
            throw new ArgumentNullException(nameof(db));
        }


        return await db.CertPolicies
            .Where(cp => cp.ID > lastId)
            .OrderBy(cp => cp.ID)
            .Take(maxResults)
            .ToListAsync();
    }


    public async Task<List<CertPolicy>> GetIncomingByOwner(string owner)
    {
        if (string.IsNullOrEmpty(owner))
        {
            throw new ConfigStoreException(ConfigStoreError.InvalidOwnerName);
        }

        await using var db = Store.CreateReadContext();
        
        var certPolicies = await db.CertPolicies
            .Include(cp => cp.CertPolicyGroupMaps
                .Where(e => e.ForIncoming))
            .ThenInclude(cpGroup => cpGroup.CertPolicyGroup)
            .ThenInclude(cpg => cpg.CertPolicyGroupDomainMaps
                .Where(e => e.Owner == owner))
            .ToListAsync();
            
        return certPolicies;
    }

    public async Task<List<CertPolicy>> GetIncomingByOwner(string owner, CertPolicyUse use)
    {
        if (string.IsNullOrEmpty(owner))
        {
            throw new ConfigStoreException(ConfigStoreError.InvalidOwnerName);
        }

        await using var db = Store.CreateReadContext();

        var certPolicies = await db.CertPolicies
            .Include(cp => cp.CertPolicyGroupMaps
                .Where(e => e.ForIncoming
                && e.PolicyUse == use))
            .ThenInclude(cpGroup => cpGroup.CertPolicyGroup)
            .ThenInclude(cpg => cpg.CertPolicyGroupDomainMaps
                .Where(e => e.Owner == owner))
            .ToListAsync();

        return certPolicies;
    }

    public async Task<List<CertPolicy>> GetOutgoingByOwner(string owner)
    {
        if (string.IsNullOrEmpty(owner))
        {
            throw new ConfigStoreException(ConfigStoreError.InvalidOwnerName);
        }

        await using var db = Store.CreateReadContext();

        var certPolicies = await db.CertPolicies
            .Include(cp => cp.CertPolicyGroupMaps
                .Where(e => e.ForOutgoing))
            .ThenInclude(cpGroup => cpGroup.CertPolicyGroup)
            .ThenInclude(cpg => cpg.CertPolicyGroupDomainMaps
                .Where(e => e.Owner == owner))
            .ToListAsync();

        return certPolicies;
    }

    public async Task<List<CertPolicy>> GetOutgoingByOwner(string owner, CertPolicyUse use)
    {
        if (string.IsNullOrEmpty(owner))
        {
            throw new ConfigStoreException(ConfigStoreError.InvalidOwnerName);
        }

        await using var db = Store.CreateReadContext();

        var certPolicies = await db.CertPolicies
            .Include(cp => cp.CertPolicyGroupMaps
                .Where(e => e.ForOutgoing
                            && e.PolicyUse == use))
            .ThenInclude(cpGroup => cpGroup.CertPolicyGroup)
            .ThenInclude(cpg => cpg.CertPolicyGroupDomainMaps
                .Where(e => e.Owner == owner))
            .ToListAsync();

        return certPolicies;
    }


    public async Task Update(CertPolicy policy)
    {
        await using var db = Store.CreateContext();
        Update(db, policy);
        await db.SaveChangesAsync();
    }


    protected void Update(ConfigDatabase db, CertPolicy policy)
    {
        if (db == null)
        {
            throw new ArgumentNullException(nameof(db));
        }
        if (policy == null)
        {
            throw new ConfigStoreException(ConfigStoreError.InvalidDomain);
        }

        var update = new CertPolicy();
        update.CopyFixed(policy);
        db.CertPolicies.Attach(update);
        update.ApplyChanges(policy);
        //foreach (CertPolicyGroupMap certPolicyGroupMap in policy.CertPolicyGroupMap)
        //{
        //    if (certPolicyGroupMap.IsNew)
        //    {
        //        db.CertPolicyGroupMaps.InsertOnSubmit(certPolicyGroupMap);
        //        if (certPolicyGroupMap.CertPolicyGroup.IsNew())
        //        {
        //            db.CertPolicyGroups.InsertOnSubmit(certPolicyGroupMap.CertPolicyGroup);
        //        }
        //    }
        //}

    }

    public async Task Remove(long policyId)
    {
        await using var db = Store.CreateContext();
        await Remove(db, policyId);
    }

    public async Task Remove(ConfigDatabase db, long policyId)
    {
        if (db == null)
        {
            throw new ArgumentNullException(nameof(db));
        }
        
        var entity = await db.CertPolicies
            .SingleOrDefaultAsync(m => m.ID == policyId);

        if (entity != null)
        {
            db.CertPolicies.Remove(entity);
        }
    }

    public async Task Remove(long[] policyIds)
    {
        await using var db = Store.CreateContext();
        await Remove(db, policyIds);
        // We don't commit, because we execute deletes directly
    }

    public async Task Remove(ConfigDatabase db, long[] policyIds)
    {
        if (db == null)
        {
            throw new ArgumentNullException(nameof(db));
        }
        if (policyIds.IsNullOrEmpty())
        {
            throw new ConfigStoreException(ConfigStoreError.InvalidIDs);
        }

        for (int i = 0; i < policyIds.Length; ++i)
        {
            var certPolicy = await db.CertPolicies
                .SingleOrDefaultAsync(m => m.ID == policyIds[i]);

            if (certPolicy != null)
            {
                db.CertPolicies.Remove(certPolicy);
            }
        }
    }

    public async Task Remove(string policyName)
    {
        await using var db = Store.CreateContext();
        await Remove(db, policyName);
        // We don't commit, because we execute deletes directly
    }

    public async Task Remove(ConfigDatabase db, string policyName)
    {
        if (db == null)
        {
            throw new ArgumentNullException(nameof(db));
        }
        if (string.IsNullOrEmpty(policyName))
        {
            throw new ConfigStoreException(ConfigStoreError.InvalidCertPolicyName);
        }

        var certPolicyGroupMap = await db.CertPolicyGroupMaps
            .Include(m => m.CertPolicy)
            .Where(m => m.CertPolicy.Name == policyName)
            .ToListAsync();

        //
        // Todo: check if this deletes CertPolicy also, probably not and I have 
        // to do each table or map EF to delete via cascade.  Save this for testing phase.
        //
        foreach (var policyGroupMap in certPolicyGroupMap)
        {
            db.CertPolicyGroupMaps.Remove(policyGroupMap);
        }
    }
    

    public IEnumerator<CertPolicy> GetEnumerator()
    {
        using var db = Store.CreateContext();
        foreach (CertPolicy policy in db.CertPolicies)
        {
            yield return policy;
        }
    }

    IEnumerator IEnumerable.GetEnumerator()
    {
        return GetEnumerator();
    }
}
