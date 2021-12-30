/* 
 Copyright (c) 2012, Direct Project
 All rights reserved.

 Authors:
    Sean Nolan      sean.nolan@microsoft.com
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
using Microsoft.EntityFrameworkCore;

namespace Health.Direct.Config.Store;

public interface IBundleManager
{
    Task<Bundle> Add(Bundle bundle);
    Task Add(IEnumerable<Bundle> bundles);
    Task<List<Bundle>> Get(long[] bundleIDs);
    Task<List<Bundle>> Get(long lastBundleId, int maxResults);
    Task<List<Bundle>> Get(string owner);
    Task<List<Bundle>> GetIncoming(string ownerName);
    Task<List<Bundle>> GetIncoming(string ownerName, EntityStatus? status);
    Task<List<Bundle>> GetOutgoing(string ownerName);
    Task<List<Bundle>> GetOutgoing(string ownerName, EntityStatus? status);
    Task SetStatus(string owner, EntityStatus status);
    Task SetStatus(long[] bundleIDs, EntityStatus status);
    Task Remove(long[] bundleIDs);
    Task Remove(string ownerName);
}

public class BundleManager : IBundleManager
{
    private readonly DirectDbContext _dbContext;

    internal BundleManager(DirectDbContext dbContext)
    {
        _dbContext = dbContext;
    }
    
    public async Task<Bundle> Add(Bundle bundle)
    {
        if (bundle == null)
        {
            throw new ConfigStoreException(ConfigStoreError.InvalidBundle);
        }

        _dbContext.Bundles.Add(bundle);
        await _dbContext.SaveChangesAsync();

        return bundle;
    }

    public async Task Add(IEnumerable<Bundle> bundles)
    {
        if (bundles == null)
        {
            throw new ArgumentNullException(nameof(bundles));
        }
        
        foreach (var bundle in bundles)
        {
            await Add(bundle);
        }

        await _dbContext.SaveChangesAsync();
    }

   
    public async Task<List<Bundle>> Get(long[] bundleIDs)
    {
        if (bundleIDs.IsNullOrEmpty())
        {
            throw new ConfigStoreException(ConfigStoreError.InvalidIDs);
        }

        return await _dbContext.Bundles
            .Where(b => bundleIDs.Contains(b.ID))
            .ToListAsync();
    }

    public async Task<List<Bundle>> Get(long lastBundleId, int maxResults)
    {
        return await _dbContext.Bundles
            .Where(b => b.ID > lastBundleId)
            .OrderBy(b => b.ID)
            .Take(maxResults)
            .ToListAsync();
    }

    public async Task<List<Bundle>> Get(string owner)
    {
        if (string.IsNullOrEmpty(owner))
        {
            throw new ConfigStoreException(ConfigStoreError.InvalidOwnerName);
        }

        return await _dbContext.Bundles
            .Where(b => b.Owner.ToUpper() == owner.ToUpper())
            .ToListAsync();
    }
    
    public async Task<List<Bundle>> GetIncoming(string ownerName)
    {
        return await GetIncoming(ownerName, null);
    }

    public async Task<List<Bundle>> GetIncoming(string ownerName, EntityStatus? status)
    {
        if (string.IsNullOrEmpty(ownerName))
        {
            throw new ConfigStoreException(ConfigStoreError.InvalidOwnerName);
        }

        
        List<Bundle> matches;
        
        if (status == null)
        {
            matches = await _dbContext.Bundles
                .Where(b =>
                    b.ForIncoming == true
                    && b.Owner == ownerName)
                .ToListAsync();
        }
        else
        {
            matches = await _dbContext.Bundles
                .Where(b =>
                    b.ForIncoming == true
                    && b.Owner == ownerName
                    && b.Status == status.Value)
                .ToListAsync();
        }

        return matches;
    }

    public async Task<List<Bundle>> GetOutgoing(string ownerName)
    {
        return await GetOutgoing(ownerName, null);
    }

    public async Task<List<Bundle>> GetOutgoing(string ownerName, EntityStatus? status)
    {
        if (string.IsNullOrEmpty(ownerName))
        {
            throw new ConfigStoreException(ConfigStoreError.InvalidOwnerName);
        }

        
        List<Bundle> matches;

        if (status == null)
        {
            matches = await _dbContext.Bundles
                .Where(b =>
                    b.ForOutgoing == true
                    && b.Owner == ownerName)
                .ToListAsync();
        }
        else
        {
            matches = await _dbContext.Bundles
                .Where(b =>
                    b.ForOutgoing == true
                    && b.Owner == ownerName
                    && b.Status == status.Value)
                .ToListAsync();
        }

        return matches;
    }

   

    public async Task SetStatus(string owner, EntityStatus status)
    {
        if (string.IsNullOrEmpty(owner))
        {
            throw new ConfigStoreException(ConfigStoreError.InvalidOwnerName);
        }

        var entity = await _dbContext.Bundles.SingleOrDefaultAsync(a => a.Owner == owner);

        if (entity != null)
        {
            entity.Status = status;
            _dbContext.Entry(entity).State = EntityState.Modified;
        }

        await _dbContext.SaveChangesAsync();
    }

    public async Task SetStatus(long[] bundleIDs, EntityStatus status)
    {
        if (bundleIDs.IsNullOrEmpty())
        {
            throw new ConfigStoreException(ConfigStoreError.InvalidIDs);
        }
        
        
        foreach (var bundleId in bundleIDs)
        {
            var entity = await _dbContext.Bundles.SingleOrDefaultAsync(a => a.ID == bundleId);

            if (entity != null)
            {
                entity.Status = status;
            }
        }

        await _dbContext.SaveChangesAsync();
    }
    

    public async Task Remove(long[] bundleIDs)
    {
        if (bundleIDs.IsNullOrEmpty())
        {
            throw new ConfigStoreException(ConfigStoreError.InvalidIDs);
        }

        for (var i = 0; i < bundleIDs.Length; ++i)
        {
            var bundle = await _dbContext.Bundles
                .SingleOrDefaultAsync(m => m.ID == bundleIDs[i]);

            if (bundle != null)
            {
                _dbContext.Bundles.Remove(bundle);
            }
        }

        await _dbContext.SaveChangesAsync();
    }
    
    public async Task Remove(string ownerName)
    {
        if (string.IsNullOrEmpty(ownerName))
        {
            throw new ConfigStoreException(ConfigStoreError.InvalidOwnerName);
        }

        if (string.IsNullOrEmpty(ownerName))
        {
            throw new ConfigStoreException(ConfigStoreError.InvalidOwnerName);
        }

        var bundles = await _dbContext.Bundles
            .Where(m =>
                m.Owner == ownerName)
            .ToListAsync();

        foreach (var bundle in bundles)
        {
            _dbContext.Bundles.Remove(bundle);
        }

        await _dbContext.SaveChangesAsync();
    }
}
