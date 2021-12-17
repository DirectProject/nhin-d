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
using Health.Direct.Config.Store.Entity;
using Microsoft.EntityFrameworkCore;

namespace Health.Direct.Config.Store;


public class BundleManager
{
    internal BundleManager(ConfigStore store)
    {
        Store = store;
    }

    internal ConfigStore Store { get; }


    public async Task<Bundle> Add(Bundle bundle)
    {
        await using var db = Store.CreateContext();
        Add(db, bundle);
        await db.SaveChangesAsync();

        return bundle;
    }

    public async Task Add(IEnumerable<Bundle> bundles)
    {
        if (bundles == null)
        {
            throw new ArgumentNullException(nameof(bundles));
        }

        await using var db = Store.CreateContext();
        foreach (var bundle in bundles)
        {
            Add(db, bundle);
        }
        await db.SaveChangesAsync();
    }

    public void Add(ConfigDatabase db, Bundle bundle)
    {
        if (db == null)
        {
            throw new ArgumentNullException(nameof(db));
        }
        if (bundle == null)
        {
            throw new ConfigStoreException(ConfigStoreError.InvalidBundle);
        }

        db.Bundles.Add(bundle);
    }

    public async Task<List<Bundle>> Get(long[] bundleIDs)
    {
        if (bundleIDs.IsNullOrEmpty())
        {
            throw new ConfigStoreException(ConfigStoreError.InvalidIDs);
        }

        await using var db = Store.CreateReadContext();
        
        return await db.Bundles
            .Where(b => bundleIDs.Contains(b.ID))
            .ToListAsync();
    }

    public async Task<List<Bundle>> Get(long lastBundleId, int maxResults)
    {
        await using var db = Store.CreateReadContext();
        return await Get(db, lastBundleId, maxResults);
    }

    public async Task<List<Bundle>> Get(ConfigDatabase db, long lastBundleId, int maxResults)
    {
        if (db == null)
        {
            throw new ArgumentNullException(nameof(db));
        }

        return await db.Bundles
            .Where(b => b.ID > lastBundleId)
            .OrderBy(b => b.ID)
            .Take(maxResults)
            .ToListAsync();
    }

    public async Task<List<Bundle>> Get(string owner)
    {
        await using var db = Store.CreateContext();
        return await Get(db, owner);
    }

    public async Task<List<Bundle>> Get(ConfigDatabase db, string owner)
    {
        if (db == null)
        {
            throw new ArgumentNullException(nameof(db));
        }
        if (string.IsNullOrEmpty(owner))
        {
            throw new ConfigStoreException(ConfigStoreError.InvalidOwnerName);
        }

        return await db.Bundles
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

        await using var db = Store.CreateContext();
        List<Bundle> matches;
        
        if (status == null)
        {
            matches = await db.Bundles
                .Where(b =>
                    b.ForIncoming == true
                    && b.Owner == ownerName)
                .ToListAsync();
        }
        else
        {
            matches = await db.Bundles
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

        await using var db = Store.CreateReadContext();
        List<Bundle> matches;

        if (status == null)
        {
            matches = await db.Bundles
                .Where(b =>
                    b.ForOutgoing == true
                    && b.Owner == ownerName)
                .ToListAsync();
        }
        else
        {
            matches = await db.Bundles
                .Where(b =>
                    b.ForOutgoing == true
                    && b.Owner == ownerName
                    && b.Status == status.Value)
                .ToListAsync();
        }

        return matches;
    }

    public async Task SetStatus(ConfigDatabase db, long BundleID, EntityStatus status)
    {
        if (db == null)
        {
            throw new ArgumentNullException(nameof(db));
        }

        var entity = await db.Bundles.SingleOrDefaultAsync(a => a.ID == BundleID);

        if (entity != null)
        {
            entity.Status = status;
        }
    }

    public async Task SetStatus(string owner, EntityStatus status)
    {
        if (string.IsNullOrEmpty(owner))
        {
            throw new ConfigStoreException(ConfigStoreError.InvalidOwnerName);
        }

        await using var db = Store.CreateContext();
        await SetStatus(db, owner, status);
    }

    public async Task SetStatus(long[] bundleIDs, EntityStatus status)
    {
        if (bundleIDs.IsNullOrEmpty())
        {
            throw new ConfigStoreException(ConfigStoreError.InvalidIDs);
        }

        await using var db = Store.CreateContext();
        
        foreach (var bundleId in bundleIDs)
        {
            await SetStatus(db, bundleId, status);
        }

        await db.SaveChangesAsync();
    }

    public async Task SetStatus(ConfigDatabase db, string owner, EntityStatus status)
    {
        if (db == null)
        {
            throw new ArgumentNullException(nameof(db));
        }

        var entity = await db.Bundles.SingleOrDefaultAsync(a => a.Owner == owner);

        if (entity != null)
        {
            entity.Status = status;
            db.Entry(entity).State = EntityState.Modified;
        }
    }

    public void Remove(long[] bundleIDs)
    {
        using var db = Store.CreateContext();
        Remove(db, bundleIDs);

        // We don't commit, because we execute deletes directly
    }

    public async Task Remove(ConfigDatabase db, long[] bundleIDs)
    {
        if (db == null)
        {
            throw new ArgumentNullException(nameof(db));
        }
        if (bundleIDs.IsNullOrEmpty())
        {
            throw new ConfigStoreException(ConfigStoreError.InvalidIDs);
        }

        for (var i = 0; i < bundleIDs.Length; ++i)
        {
            var bundle = await db.Bundles
                .SingleOrDefaultAsync(m => m.ID == bundleIDs[i]);

            if (bundle != null)
            {
                db.Bundles.Remove(bundle);
            }
        }
    }

    public async Task Remove(string ownerName)
    {
        if (string.IsNullOrEmpty(ownerName))
        {
            throw new ConfigStoreException(ConfigStoreError.InvalidOwnerName);
        }

        await using var db = Store.CreateContext();
        await Remove(db, ownerName);
        await db.SaveChangesAsync();
    }

    public async Task Remove(ConfigDatabase db, string ownerName)
    {
        if (db == null)
        {
            throw new ArgumentNullException(nameof(db));
        }
        if (string.IsNullOrEmpty(ownerName))
        {
            throw new ConfigStoreException(ConfigStoreError.InvalidOwnerName);
        }

        var bundles = await db.Bundles
            .Where(m =>
                m.Owner == ownerName)
            .ToListAsync();

        foreach (var bundle in bundles)
        {
            db.Bundles.Remove(bundle);
        }
    }
}
