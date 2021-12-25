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
    private readonly DirectDbContext _dbContext;

    internal CertPolicyGroupManager(DirectDbContext dbContext)
    {
        _dbContext = dbContext;
    }

    public async Task<CertPolicyGroup> Add(CertPolicyGroup group)
    {
        if (group == null)
        {
            throw new ConfigStoreException(ConfigStoreError.InvalidCertPolicyGroup);
        }

        _dbContext.CertPolicyGroups.Add(group);
        await _dbContext.SaveChangesAsync();

        return group;
    }
    
    
    public async Task<CertPolicyGroup?> Get(long id)
    {
        var certPolicyGroup = await _dbContext.CertPolicyGroups
            .Where(e => e.ID == id)
            .SingleOrDefaultAsync();

        return certPolicyGroup;
    }

    /// <summary>
    /// Get PolicyGroup by name
    /// </summary>
    /// <param name="name">Name of the policy</param>
    /// <returns></returns>
    public async Task<CertPolicyGroup?> Get(string name)
    {
        
        if (string.IsNullOrEmpty(name))
        {
            throw new ConfigStoreException(ConfigStoreError.InvalidCertPolicyGroupName);
        }

        return (await _dbContext.CertPolicyGroups
            .Include(e => e.CertPolicyGroupMaps)
            .ThenInclude(m => m.CertPolicy)
            .Include(e => e.CertPolicyGroupDomainMaps)
            .Where(e => e.Name == name)
            .SingleOrDefaultAsync());
    }
    
    /// <summary>
    /// Get PolicyGroupOwnerMap by name with owners
    /// </summary>
    /// <param name="name">Name of the policy</param>
    /// <returns></returns>
    public async Task<List<CertPolicyGroupDomainMap>> GetWithOwners(string name)
    {
        if (string.IsNullOrEmpty(name))
        {
            throw new ConfigStoreException(ConfigStoreError.InvalidCertPolicyGroupName);
        }

        var maps = await _dbContext.CertPolicyGroupDomainMaps
            .Include(cpg => cpg.CertPolicyGroup)
            .Where(e => e.CertPolicyGroup.Name == name)
            .ToListAsync();

        return maps;
    }
    

    public async Task<List<CertPolicyGroup>> GetByDomains(string[] owners)
    {
        if (owners.IsNullOrEmpty())
        {
            throw new ConfigStoreException(ConfigStoreError.InvalidOwnerName);
        }

        var entities = await _dbContext.CertPolicyGroups
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

    public async Task<List<CertPolicyGroup>> Get(long lastId, int maxResults)
    {
        return await _dbContext.CertPolicyGroups
            .Where(cpg => cpg.ID > lastId)
            .OrderBy(cpg => cpg.ID)
            .Take(maxResults)
            .ToListAsync();
    }

    public async Task<bool> PolicyGroupMapExists(string policyName, string groupName, CertPolicyUse policyUse, bool incoming, bool outgoing)
    {
        
        bool exists = await _dbContext.CertPolicyGroupMaps
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
        
        bool exists = await _dbContext.CertPolicyGroupDomainMaps
            .Include(cpg => cpg.CertPolicyGroup)
            .AnyAsync(cpg =>
                cpg.CertPolicyGroup.Name == groupName
                && cpg.Owner == owner
            );

        return exists;
    }

    public async Task Update(CertPolicyGroup policyGroup)
    {
        if (policyGroup == null)
        {
            throw new ConfigStoreException(ConfigStoreError.InvalidDomain);
        }

        var update = new CertPolicyGroup();
        update.CopyFixed(policyGroup);
        _dbContext.CertPolicyGroups.Attach(update);
        update.ApplyChanges(policyGroup);

        await _dbContext.SaveChangesAsync();
    }

    protected async Task AddPolicyUse(string policyName, string groupName, CertPolicyUse policyUse, bool incoming, bool outgoing)
    {
        if (string.IsNullOrEmpty(policyName))
        {
            throw new ConfigStoreException(ConfigStoreError.InvalidCertPolicyName);
        }
        if (string.IsNullOrEmpty(groupName))
        {
            throw new ConfigStoreException(ConfigStoreError.InvalidCertPolicyGroupName);
        }

        var group = await _dbContext.CertPolicyGroups
            .Include(g => g.CertPolicyGroupMaps)
            .Where(cpg => cpg.Name == groupName)
            .SingleOrDefaultAsync();

        var policy = await _dbContext.CertPolicies
            .Where(g => g.Name == policyName)
            .SingleOrDefaultAsync();

        if (group != null && policy != null)
        {
            // group.CertPolicies.Add(policy);
            group.CertPolicyGroupMaps.Add(new CertPolicyGroupMap(policyUse, incoming, outgoing));
            var map = group.CertPolicyGroupMaps.First(m => m.IsNew);
            map.CertPolicy = policy;
            
        }

        await _dbContext.SaveChangesAsync();
    }
    
    protected async Task AssociateToOwner(string groupName, string owner)
    {
        if (string.IsNullOrEmpty(owner))
        {
            throw new ConfigStoreException(ConfigStoreError.InvalidOwnerName);
        }

        var group = await _dbContext.CertPolicyGroups
            .Where(cpg => cpg.Name == groupName)
            .SingleOrDefaultAsync();

        if (group != null)
        {
            var map = new CertPolicyGroupDomainMap(true)
            {
                CertPolicyGroup = group,
                Owner = owner
            };

            group.CertPolicyGroupDomainMaps.Add(map);
        }

        await _dbContext.SaveChangesAsync();
    }

    public async Task DisassociateFromDomain(string owner, long policyGroupId)
    {
        var policyGroup = await _dbContext.CertPolicyGroups
            .Include(cpg => cpg.CertPolicyGroupDomainMaps)
            .Where(cpg => cpg.ID == policyGroupId)
            .SingleOrDefaultAsync();

        if (policyGroup != null && policyGroup.CertPolicyGroupDomainMaps.Any(map => map.Owner == owner))
        {
            var maps = policyGroup.CertPolicyGroupDomainMaps.Where(map => map.Owner == owner).ToArray();
            await RemoveDomain(maps);
        }
    }

    /// <summary>
    /// Disassociate all Policy Groups associated to an owner
    /// </summary>
    /// <param name="owner"></param>
    public async Task DisassociateFromDomain(string owner)
    {
        var certPolicyGroupDomainMap = await _dbContext.CertPolicyGroupDomainMaps
            .Where(m => m.Owner == owner)
            .ToListAsync();

        foreach (var policyGroupDomainMap in certPolicyGroupDomainMap)
        {
            _dbContext.CertPolicyGroupDomainMaps.Remove(policyGroupDomainMap);
            await _dbContext.SaveChangesAsync();
        }
    }

    /// <summary>
    /// Remove all 
    /// </summary>
    /// <param name="policyGroupId"></param>
    public async Task DisassociateFromDomains(long policyGroupId)
    {
        var certPolicyGroupDomainMap = await _dbContext.CertPolicyGroupDomainMaps
            .Where(m => m.CertPolicyGroupId == policyGroupId)
            .ToListAsync();

        foreach (var policyGroupDomainMap in certPolicyGroupDomainMap)
        {
            _dbContext.CertPolicyGroupDomainMaps.Remove(policyGroupDomainMap);
            await _dbContext.SaveChangesAsync();
        }
    }

    public async Task Remove(long policyGroupId)
    {
        var certPolicyGroupDomainMap = await _dbContext.CertPolicyGroups
            .SingleOrDefaultAsync(m => m.ID == policyGroupId);

        if (certPolicyGroupDomainMap != null)
        {
            _dbContext.CertPolicyGroups.Remove(certPolicyGroupDomainMap);
        }

        await _dbContext.SaveChangesAsync();
    }
    
    public async Task Remove(long[] policyGroupIds)
    {
        if (policyGroupIds.IsNullOrEmpty())
        {
            throw new ConfigStoreException(ConfigStoreError.InvalidIDs);
        }

        var certPolicyGroups = await _dbContext.CertPolicyGroups
            .Where(m => policyGroupIds.Contains(m.ID))
            .ToListAsync();

        _dbContext.CertPolicyGroups.RemoveRange(certPolicyGroups);

        await _dbContext.SaveChangesAsync();
    }

    public async Task Remove(string groupName)
    {
        if (string.IsNullOrEmpty(groupName))
        {
            throw new ConfigStoreException(ConfigStoreError.InvalidCertPolicyName);
        }

        var certPolicyGroupDomainMap = await _dbContext.CertPolicyGroups
            .SingleOrDefaultAsync(m => m.Name == groupName);

        if (certPolicyGroupDomainMap != null)
        {
            _dbContext.CertPolicyGroups.Remove(certPolicyGroupDomainMap);
        }

        await _dbContext.SaveChangesAsync();
    }

    public async Task RemovePolicy(DirectDbContext db, CertPolicyGroupMap[] map)
    {
        if (map.IsNullOrEmpty())
        {
            throw new ConfigStoreException(ConfigStoreError.InvalidIDs);
        }

        for (int i = 0; i < map.Length; ++i)
        {
            var certPolicyGroupMaps = await _dbContext.CertPolicyGroupMaps
                .SingleOrDefaultAsync(m =>
                    m.CertPolicyId == map[i].CertPolicyId
                    && m.CertPolicyGroupId == map[i].CertPolicyGroupId);

            if (certPolicyGroupMaps != null) _dbContext.CertPolicyGroupMaps.Remove(certPolicyGroupMaps);
        }

        await _dbContext.SaveChangesAsync();
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
        if (map.IsNullOrEmpty())
        {
            throw new ConfigStoreException(ConfigStoreError.InvalidIDs);
        }

        for (int i = 0; i < map.Length; ++i)
        {
            var certPolicyGroupDomainMap = await _dbContext.CertPolicyGroupDomainMaps
                .SingleOrDefaultAsync(m =>
                    m.CertPolicyGroupId == map[i].CertPolicyGroupId
                    && m.Owner == map[i].Owner);

            if (certPolicyGroupDomainMap != null)
            {
                _dbContext.CertPolicyGroupDomainMaps.Remove(certPolicyGroupDomainMap);
            }
        }

        await _dbContext.SaveChangesAsync();
    }
    
    /// <summary>
    /// Remove a policy from a group by MapId in CertPolicyGroupMap table.
    /// </summary>
    /// <param name="mapId"></param>
    /// <returns></returns>
    public async Task RemovePolicyUseFromGroup(long mapId)
    {
        var certPolicyGroupMap = await _dbContext.CertPolicyGroupMaps
            .SingleOrDefaultAsync(m => m.ID == mapId);

        if (certPolicyGroupMap != null)
        {
            _dbContext.CertPolicyGroupMaps.Remove(certPolicyGroupMap);
        }

        await _dbContext.SaveChangesAsync();
    }
    

    /// <inheritdoc />
    public IEnumerator<CertPolicyGroup> GetEnumerator()
    {
        foreach (CertPolicyGroup policy in _dbContext.CertPolicyGroups)
        {
            yield return policy;
        }
    }

    IEnumerator IEnumerable.GetEnumerator()
    {
        return GetEnumerator();
    }



}

