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

/// <summary>
/// Manage certificate policy data access 
/// </summary>
public class CertPolicyGroupManager : IEnumerable<CertPolicyGroup>
{
    internal CertPolicyGroupManager(ConfigStore store)
    {
        Store = store;
    }

    internal ConfigStore Store { get; }

    public async Task<CertPolicyGroup> Add(CertPolicyGroup group)
    {
        await using var db = Store.CreateContext();
        Add(db, group);
        await db.SaveChangesAsync();

        return group;
    }

    public void Add(ConfigDatabase db, CertPolicyGroup group)
    {
        if (db == null)
        {
            throw new ArgumentNullException(nameof(db));
        }
        if (group == null)
        {
            throw new ConfigStoreException(ConfigStoreError.InvalidCertPolicyGroup);
        }

        db.CertPolicyGroups.Add(group);
    }

    
    public async Task<CertPolicyGroup> Get(long id)
    {
        await using var db = Store.CreateContext();
        var certPolicyGroup = await Get(db, id);
            
        return certPolicyGroup;
    }

    /// <summary>
    /// Get PolicyGroup by name
    /// </summary>
    /// <param name="name">Name of the policy</param>
    /// <returns></returns>
    public async Task<CertPolicyGroup> Get(string name)
    {
        await using var db = Store.CreateContext();
        var certPolicyGroup = await Get(db, name);

        return certPolicyGroup;
    }
    
    /// <summary>
    /// Get PolicyGroupOwnerMap by name with owners
    /// </summary>
    /// <param name="name">Name of the policy</param>
    /// <returns></returns>
    public async Task<List<CertPolicyGroupDomainMap>> GetWithOwners(string name)
    {
        await using var db = Store.CreateContext();
        var maps = await GetWithOwners(db, name);

        return maps;
    }



    public async Task<CertPolicyGroup> Get(ConfigDatabase db, string name)
    {
        if (db == null)
        {
            throw new ArgumentNullException(nameof(db));
        }
        if (string.IsNullOrEmpty(name))
        {
            throw new ConfigStoreException(ConfigStoreError.InvalidCertPolicyGroupName);
        }

        return await db.CertPolicyGroups
            .Include(e => e.CertPolicyGroupMaps)
            .ThenInclude(m => m.CertPolicy)
            .Include(e => e.CertPolicyGroupDomainMaps)
            .Where(e => e.Name == name)
            .SingleOrDefaultAsync();
    }

    public async Task<List<CertPolicyGroupDomainMap>> GetWithOwners(ConfigDatabase db, string name)
    {
        if (db == null)
        {
            throw new ArgumentNullException(nameof(db));
        }
        if (string.IsNullOrEmpty(name))
        {
            throw new ConfigStoreException(ConfigStoreError.InvalidCertPolicyGroupName);
        }
        
        var entities = await  db.CertPolicyGroupDomainMaps
            .Include(cpg => cpg.CertPolicyGroup)
            .Where(e => e.CertPolicyGroup.Name == name)
            .ToListAsync();

        return entities;
    }


    public async Task<CertPolicyGroup> Get(ConfigDatabase db, long id)
    {
        if (db == null)
        {
            throw new ArgumentNullException(nameof(db));
        }

        var entity = await db.CertPolicyGroups
            .Where(e => e.ID == id)
            .SingleOrDefaultAsync();

        return entity;
    }


    public async Task<List<CertPolicyGroup>> GetByDomains(string[] owners)
    {
        if (owners.IsNullOrEmpty())
        {
            throw new ConfigStoreException(ConfigStoreError.InvalidOwnerName);
        }

        await using var db = Store.CreateReadContext();
        var entities = await db.CertPolicyGroups
            .Include(g => g.CertPolicyGroupDomainMaps
                .Where(m => owners.Contains(m.Owner)))
            .Include(g => g.CertPolicyGroupMaps)
            .ThenInclude(m => m.CertPolicy)
            .ToListAsync();
        //
        // TODO: Is there a more efficient way to get SQL to do the work without writing SQL and creating a DTO dedicated to this?
        // Have not found simple way to force the inner join to CertPolicyGroupDomainMaps.
        //
         var groups = entities.Where(g => g.CertPolicyGroupDomainMaps.Any()).ToList();

        return groups;
    }

    public async Task<List<CertPolicyGroup>> Get(long lastID, int maxResults)
    {
        await using var db = Store.CreateReadContext();
        return await Get(db, lastID, maxResults);
    }

    public async Task<List<CertPolicyGroup>> Get(ConfigDatabase db, long lastID, int maxResults)
    {
        if (db == null)
        {
            throw new ArgumentNullException(nameof(db));
        }

        var entities = await db.CertPolicyGroups
            .Where(cpg => cpg.ID > lastID)
            .OrderBy(cpg => cpg.ID)
            .Take(maxResults)
            .ToListAsync();

        return entities;
    }

    public async Task<bool> PolicyGroupMapExists(string policyName, string groupName, CertPolicyUse policyUse, bool incoming, bool outgoing)
    {
        await using var db = Store.CreateReadContext();
        bool exists = await db.CertPolicyGroupMaps
            .Include(cpgm => cpgm.CertPolicy)
            .Include(cpgm => cpgm.CertPolicyGroup)
            .AnyAsync(cpgm => 
                cpgm.CertPolicy.Name == policyName
                && cpgm.CertPolicyGroup.Name == groupName
                && cpgm.PolicyUse == policyUse
                && cpgm.ForIncoming == incoming
                && cpgm.ForOutgoing == outgoing
            );

        return exists;
    }

    public async Task<bool> PolicyGroupMapExists(string groupName, string owner)
    {
        await using var db = Store.CreateReadContext();
        bool exists = await db.CertPolicyGroupDomainMaps
            .Include(cpg => cpg.CertPolicyGroup)
            .AnyAsync(cpg =>
                cpg.CertPolicyGroup.Name == groupName
                && cpg.Owner == owner
            );

        return exists;
    }

    public async Task Update(CertPolicyGroup policyGroup)
    {
        await using var db = Store.CreateContext();
        Update(db, policyGroup);

        await db.SaveChangesAsync();
    }


    protected void Update(ConfigDatabase db, CertPolicyGroup policyGroup)
    {
        if (db == null)
        {
            throw new ArgumentNullException(nameof(db));
        }
        if (policyGroup == null)
        {
            throw new ConfigStoreException(ConfigStoreError.InvalidDomain);
        }

        var update = new CertPolicyGroup();
        update.CopyFixed(policyGroup);
        db.CertPolicyGroups.Attach(update);
        update.ApplyChanges(policyGroup);
    }



    public async Task AddPolicyUse(string policyName, string groupName, CertPolicyUse policyUse, bool incoming, bool outgoing)
    {
        await using var db = Store.CreateContext();
        await AddPolicyUse(db, policyName, groupName, policyUse, incoming, outgoing);
        await db.SaveChangesAsync();
    }


    protected async Task AddPolicyUse(ConfigDatabase db, string policyName, string groupName, CertPolicyUse policyUse, bool incoming, bool outgoing)
    {
        if (db == null)
        {
            throw new ArgumentNullException(nameof(db));
        }
        if (string.IsNullOrEmpty(policyName))
        {
            throw new ConfigStoreException(ConfigStoreError.InvalidCertPolicyName);
        }
        if (string.IsNullOrEmpty(groupName))
        {
            throw new ConfigStoreException(ConfigStoreError.InvalidCertPolicyGroupName);
        }

        var group = await db.CertPolicyGroups
            .Include(g => g.CertPolicyGroupMaps)
            .Where(cpg => cpg.Name == groupName)
            .SingleOrDefaultAsync();

        var policy = await db.CertPolicies
            .Where(g => g.Name == policyName)
            .SingleOrDefaultAsync();

        if (@group != null)
        {
            // @group.CertPolicies.Add(policy);
            @group.CertPolicyGroupMaps.Add(new CertPolicyGroupMap(policyUse, incoming, outgoing));
            var map = @group.CertPolicyGroupMaps.First(m => m.IsNew);
            map.CertPolicy = policy;
            
        }
    }


    public async Task AssociateToOwner(string groupName, string owner)
    {
        await using var db = Store.CreateContext();
        await AssociateToOwner(db, groupName, owner);
        await db.SaveChangesAsync();
    }

    protected async Task AssociateToOwner(ConfigDatabase db, string groupName, string owner)
    {
        if (db == null)
        {
            throw new ArgumentNullException(nameof(db));
        }
        if (string.IsNullOrEmpty(owner))
        {
            throw new ConfigStoreException(ConfigStoreError.InvalidOwnerName);
        }

        var group = await db.CertPolicyGroups
            .Where(cpg => cpg.Name == groupName)
            .SingleOrDefaultAsync();

        var map = new CertPolicyGroupDomainMap(true)
        {
            CertPolicyGroup = group,
            Owner = owner
        };
        group.CertPolicyGroupDomainMaps.Add(map);

    }

    public async Task DisassociateFromDomain(string owner, long policyGroupID)
    {
        await using var db = Store.CreateContext();
        var policyGroup = await db.CertPolicyGroups
            .Include(cpg => cpg.CertPolicyGroupDomainMaps)
            .Where(cpg => cpg.ID == policyGroupID)
            .SingleOrDefaultAsync();

        if (policyGroup != null && policyGroup.CertPolicyGroupDomainMaps.Any(map => map.Owner == owner))
        {
            var maps = policyGroup.CertPolicyGroupDomainMaps.Where(map => map.Owner == owner).ToArray();
            await RemoveDomain(db, maps);
            await db.SaveChangesAsync();
        }
    }

    /// <summary>
    /// Disassociate all Policy Groups associated to an owner
    /// </summary>
    /// <param name="owner"></param>
    public async Task DisassociateFromDomain(string owner)
    {
        await using var db = Store.CreateContext();
        var certPolicyGroupDomainMap = await db.CertPolicyGroupDomainMaps
            .Where(m => m.Owner == owner)
            .ToListAsync();

        foreach (var policyGroupDomainMap in certPolicyGroupDomainMap)
        {
            db.CertPolicyGroupDomainMaps.Remove(policyGroupDomainMap);
            await db.SaveChangesAsync();
        }
    }

    /// <summary>
    /// Remove all 
    /// </summary>
    /// <param name="policyGroupId"></param>
    public async Task DisassociateFromDomains(long policyGroupId)
    {
        await using var db = Store.CreateContext();
        var certPolicyGroupDomainMap = await db.CertPolicyGroupDomainMaps
            .Where(m => m.CertPolicyGroupId == policyGroupId)
            .ToListAsync();

        foreach (var policyGroupDomainMap in certPolicyGroupDomainMap)
        {
            db.CertPolicyGroupDomainMaps.Remove(policyGroupDomainMap);
            await db.SaveChangesAsync();
        }
    }


    public async Task Remove(long policyGroupId)
    {
        await using var db = Store.CreateContext();
        await Remove(db, policyGroupId);
        await db.SaveChangesAsync();
    }

    public async Task Remove(ConfigDatabase db, long policyGroupId)
    {
        if (db == null)
        {
            throw new ArgumentNullException(nameof(db));
        }

        var certPolicyGroupDomainMap = await db.CertPolicyGroups
            .SingleOrDefaultAsync(m => m.ID == policyGroupId);

        if (certPolicyGroupDomainMap != null)
        {
            db.CertPolicyGroups.Remove(certPolicyGroupDomainMap);
        }
    }

    public async Task Remove(long[] policyGroupIds)
    {
        await using ConfigDatabase db = Store.CreateContext();
        await Remove(db, policyGroupIds);
        await db.SaveChangesAsync();
    }

    public async Task Remove(ConfigDatabase db, long[] policyGroupIds)
    {
        if (db == null)
        {
            throw new ArgumentNullException(nameof(db));
        }
        if (policyGroupIds.IsNullOrEmpty())
        {
            throw new ConfigStoreException(ConfigStoreError.InvalidIDs);
        }
        
        for (int i = 0; i < policyGroupIds.Length; ++i)
        {
            await Remove(db, policyGroupIds[i]);
        }
    }

    public async Task Remove(string groupName)
    {
        await using ConfigDatabase db = Store.CreateContext();
        await Remove(db, groupName);
        await db.SaveChangesAsync();
    }

    public async Task Remove(ConfigDatabase db, string groupName)
    {
        if (db == null)
        {
            throw new ArgumentNullException(nameof(db));
        }
        if (string.IsNullOrEmpty(groupName))
        {
            throw new ConfigStoreException(ConfigStoreError.InvalidCertPolicyName);
        }

        var certPolicyGroupDomainMap = await db.CertPolicyGroups
            .SingleOrDefaultAsync(m => m.Name == groupName);

        if (certPolicyGroupDomainMap != null)
        {
            db.CertPolicyGroups.Remove(certPolicyGroupDomainMap);
        }
    }

    public async Task RemovePolicy(CertPolicyGroupMap[] map)
    {
        await using var db = Store.CreateContext();
        await RemovePolicy(db, map);
        await db.SaveChangesAsync();
    }


    public async Task RemovePolicy(ConfigDatabase db, CertPolicyGroupMap[] map)
    {
        if (db == null)
        {
            throw new ArgumentNullException(nameof(db));
        }
        if (map.IsNullOrEmpty())
        {
            throw new ConfigStoreException(ConfigStoreError.InvalidIDs);
        }

        for (int i = 0; i < map.Length; ++i)
        {
            var certPolicyGroupMaps = await db.CertPolicyGroupMaps
                .SingleOrDefaultAsync(m =>
                    m.CertPolicyId == map[i].CertPolicyId
                    && m.CertPolicyGroupId == map[i].CertPolicyGroupId);

            db.CertPolicyGroupMaps.Remove(certPolicyGroupMaps);
        }
    }

    /// <summary>
    /// Delete record from CertPolicyGroupDomainMap by CertPolicyGroupId and Owner fields
    ///
    /// User is responsible for saving.
    /// </summary>
    /// <param name="map"></param>
    /// <returns></returns>
    public async Task RemoveDomain(CertPolicyGroupDomainMap[] map)
    {
        await using var db = Store.CreateContext();
        await RemoveDomain(db, map);
        await db.SaveChangesAsync();
    }

    /// <summary>Delete record from CertPolicyGroupDomainMap by CertPolicyGroupId and Owner fields
    ///
    /// User is responsible for saving. 
    /// </summary>
    /// <param name="db"></param>
    /// <param name="map"></param>
    /// <returns></returns>
    /// <exception cref="ArgumentNullException"></exception>
    /// <exception cref="ConfigStoreException"></exception>
    public async Task RemoveDomain(ConfigDatabase db, CertPolicyGroupDomainMap[] map)
    {
        if (db == null)
        {
            throw new ArgumentNullException(nameof(db));
        }
        if (map.IsNullOrEmpty())
        {
            throw new ConfigStoreException(ConfigStoreError.InvalidIDs);
        }

        for (int i = 0; i < map.Length; ++i)
        {
            var certPolicyGroupDomainMap = await db.CertPolicyGroupDomainMaps
                .SingleOrDefaultAsync(m => 
                    m.CertPolicyGroupId == map[i].CertPolicyGroupId
                    && m.Owner == map[i].Owner);

            if (certPolicyGroupDomainMap != null)
            {
                db.CertPolicyGroupDomainMaps.Remove(certPolicyGroupDomainMap);
            }
        }
    }

    /// <summary>
    /// Remove a policy from a group by MapId in CertPolicyGroupMap table.
    /// </summary>
    /// <param name="mapId"></param>
    /// <returns></returns>
    public async Task RemovePolicyUseFromGroup(long mapId)
    {
        await using var db = Store.CreateContext();
        await RemovePolicyUseFromGroup(db, mapId);
        await db.SaveChangesAsync();
    }

    /// <summary>
    /// Remove a policy from a group by MapId in CertPolicyGroupMap table.
    ///
    /// User is responsible for saving.
    /// </summary>
    /// <param name="db"></param>
    /// <param name="mapId"></param>
    /// <exception cref="ArgumentNullException"></exception>
    public async Task RemovePolicyUseFromGroup(ConfigDatabase db, long mapId)
    {
        if (db == null)
        {
            throw new ArgumentNullException(nameof(db));
        }

        var certPolicyGroupMap = await db.CertPolicyGroupMaps
            .SingleOrDefaultAsync(m => m.ID == mapId);

        if (certPolicyGroupMap != null)
        {
            db.CertPolicyGroupMaps.Remove(certPolicyGroupMap);
        }
    }


    /// <inheritdoc />
    public IEnumerator<CertPolicyGroup> GetEnumerator()
    {
        using ConfigDatabase db = Store.CreateContext();
        foreach (CertPolicyGroup policy in db.CertPolicyGroups)
        {
            yield return policy;
        }
    }

    IEnumerator IEnumerable.GetEnumerator()
    {
        return GetEnumerator();
    }



}

