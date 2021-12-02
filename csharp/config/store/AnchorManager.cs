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

/// <summary>
/// Manage anchor data access 
/// </summary>
public class AnchorManager
{
    internal AnchorManager(ConfigStore store)
    {
        Store = store;
    }

    internal ConfigStore Store { get; }


    public async Task<Anchor> Add(Anchor anchor)
    {
        await using ConfigDatabase db = Store.CreateContext();
        await Add(db, anchor);
        await db.SaveChangesAsync();
        return anchor;
    }

    public async Task Add(IEnumerable<Anchor> anchors)
    {
        if (anchors == null)
        {
            throw new ArgumentNullException(nameof(anchors));
        }

        await using ConfigDatabase db = Store.CreateContext();
        
        foreach(var anchor in anchors)
        {
            await Add(db, anchor);
        }

        await db.SaveChangesAsync();
    }
    
    public async Task Add(ConfigDatabase db, Anchor anchor)
    {
        if (db == null)
        {
            throw new ArgumentNullException(nameof(db));
        }
        if (anchor == null)
        {
            throw new ConfigStoreException(ConfigStoreError.InvalidAnchor);
        }
        
        await db.Anchors.AddAsync(anchor);
    }

    public async Task<List<Anchor>> Get(long[] certificateIDs)
    {
        if (certificateIDs.IsNullOrEmpty())
        {
            throw new ConfigStoreException(ConfigStoreError.InvalidIDs);
        }

        await using ConfigDatabase db = Store.CreateReadContext();

        return await db.Anchors
            .Where(a => certificateIDs.Contains(a.ID))
            .ToListAsync();
    }

    public async Task<List<Anchor>> Get(long lastCertID, int maxResults)
    {
        await using ConfigDatabase db = Store.CreateReadContext();
        return await Get(db, lastCertID, maxResults);
    }

    public async Task<List<Anchor>> Get(ConfigDatabase db, long lastCertId, int maxResults)
    {
        if (db == null)
        {
            throw new ArgumentNullException(nameof(db));
        }

        var entities = await db.Anchors
            .Where(a => a.ID > lastCertId)
            .OrderBy(a => a.ID)
            .Take(maxResults)
            .ToListAsync();

        return entities;
    }

    public async Task<List<Anchor>> Get(string owner)
    {
        await using ConfigDatabase db = Store.CreateContext();
        return await Get(db, owner);
    }

    public async Task<List<Anchor>> Get(ConfigDatabase db, string owner)
    {
        if (db == null)
        {
            throw new ArgumentNullException(nameof(db));
        }
        if (string.IsNullOrEmpty(owner))
        {
            throw new ConfigStoreException(ConfigStoreError.InvalidOwnerName);
        }

        return await db.Anchors
            .Where(e => e.Owner == owner)
            .ToListAsync();
    }

    public async Task<List<Anchor>> GetIncoming(string ownerName)
    {
        return await GetIncoming(ownerName, null);
    }

    public async Task<List<Anchor>> GetIncoming(string ownerName, EntityStatus? status)
    {
        if (string.IsNullOrEmpty(ownerName))
        {
            throw new ConfigStoreException(ConfigStoreError.InvalidOwnerName);
        }

        await using ConfigDatabase db = Store.CreateContext();
        List<Anchor> matches;

        if (status == null)
        {
            matches = await db.Anchors
                .Where(a => 
                    a.ForIncoming == true
                    && a.Owner == ownerName)
                .ToListAsync();
        }
        else
        {
            matches = await db.Anchors
                    .Where(a =>
                        a.ForIncoming == true
                        && a.Owner == ownerName
                        && a.Status == status.Value)
                    .ToListAsync();
        }
            
        return matches;
    }

    public async Task<List<Anchor>> GetOutgoing(string ownerName)
    {
        return await GetOutgoing(ownerName, null);
    }

    public async Task<List<Anchor>> GetOutgoing(string ownerName, EntityStatus? status)
    {
        if (string.IsNullOrEmpty(ownerName))
        {
            throw new ConfigStoreException(ConfigStoreError.InvalidOwnerName);
        }

        await using ConfigDatabase db = Store.CreateReadContext();
        List<Anchor> matches;

        if (status == null)
        {
            matches = await db.Anchors
                .Where(a =>
                    a.ForOutgoing == true
                    && a.Owner == ownerName)
                .ToListAsync();
        }
        else
        {
            matches = await db.Anchors
                .Where(a =>
                    a.ForOutgoing == true
                    && a.Owner == ownerName
                    && a.Status == status.Value)
                .ToListAsync();
        }

        return matches;
    }

    public async Task SetStatus(ConfigDatabase db, long anchorID, EntityStatus status)
    {
        if (db == null)
        {
            throw new ArgumentNullException(nameof(db));
        }

        var entity = await db.Anchors.SingleOrDefaultAsync(a => a.ID == anchorID);

        if (entity != null)
        {
            entity.Status = status;
        }
    }

    public async Task<Anchor> Get(string owner, string thumbprint)
    {
        await using var db = Store.CreateReadContext();
        return await Get(db, owner, thumbprint);
    }

    public async Task<Anchor> Get(ConfigDatabase db, string owner, string thumbprint)
    {
        if (db == null)
        {
            throw new ArgumentNullException(nameof(db));
        }
        if (string.IsNullOrEmpty(owner))            
        {
            throw new ConfigStoreException(ConfigStoreError.InvalidOwnerName);
        }
        if (string.IsNullOrEmpty(thumbprint))
        {
            throw new ConfigStoreException(ConfigStoreError.InvalidThumbprint);
        }

        return await db.Anchors
            .Where(a => a.Owner == owner
                        && a.Thumbprint == thumbprint)
            .SingleOrDefaultAsync();
    }

    public async Task SetStatus(string owner, EntityStatus status)
    {
        if (string.IsNullOrEmpty(owner))
        {
            throw new ConfigStoreException(ConfigStoreError.InvalidOwnerName);
        }

        await using ConfigDatabase db = Store.CreateContext();
        await SetStatus(db, owner, status);
        await db.SaveChangesAsync();
    }

    public async Task SetStatus(long[] anchorIDs, EntityStatus status)
    {
        if (anchorIDs.IsNullOrEmpty())
        {
            throw new ConfigStoreException(ConfigStoreError.InvalidIDs);
        }

        await using ConfigDatabase db = Store.CreateContext();
        
        foreach (var t in anchorIDs)
        {
            await SetStatus(db, t, status);
        }
        
        await db.SaveChangesAsync();
    }

    public async Task SetStatus(ConfigDatabase db, string owner, EntityStatus status)
    {
        if (db == null)
        {
            throw new ArgumentNullException(nameof(db));
        }

        var entity = await db.Anchors.SingleOrDefaultAsync(a => a.Owner == owner);

        if (entity != null)
        {
            entity.Status = status;
        }
    }

    public async Task Remove(long[] certificateIDs)
    {
        await using ConfigDatabase db = Store.CreateContext();
        await Remove(db, certificateIDs);
        await db.SaveChangesAsync();
    }

    public async Task Remove(ConfigDatabase db, long[] certificateIDs)
    {
        if (db == null)
        {
            throw new ArgumentNullException(nameof(db));
        }
        if (certificateIDs.IsNullOrEmpty())
        {
            throw new ConfigStoreException(ConfigStoreError.InvalidIDs);
        }
        
        for (int i = 0; i < certificateIDs.Length; ++i)
        {
            var anchor = await db.Anchors
                .SingleOrDefaultAsync(m => m.ID == certificateIDs[i]);

            if (anchor != null)
            {
                db.Anchors.Remove(anchor);
            }
        }
    }

    public async Task Remove(string owner, string thumbprint)
    {
        await using var db = Store.CreateContext();
        await Remove(db, owner, thumbprint);
        await db.SaveChangesAsync();
    }

    public async Task Remove(ConfigDatabase db, string owner, string thumbprint)
    {
        if (db == null)
        {
            throw new ArgumentNullException(nameof(db));
        }
        if (string.IsNullOrEmpty(owner))
        {
            throw new ConfigStoreException(ConfigStoreError.InvalidOwnerName);
        }
        if (string.IsNullOrEmpty(thumbprint))
        {
            throw new ConfigStoreException(ConfigStoreError.InvalidThumbprint);
        }

        var anchors = await db.Anchors
            .Where(m => 
                m.Owner == owner
                && m.Thumbprint == thumbprint)
            .ToListAsync();

        foreach (var anchor in anchors)
        {
            db.Anchors.Remove(anchor);
        }
    }

    public async Task Remove(string ownerName)
    {
        if (string.IsNullOrEmpty(ownerName))
        {
            throw new ConfigStoreException(ConfigStoreError.InvalidOwnerName);
        }

        using var db = Store.CreateContext();
        await Remove(db, ownerName);
    }

    public async Task Remove(ConfigDatabase db, string owner)
    {
        if (db == null)
        {
            throw new ArgumentNullException(nameof(db));
        }
        if (string.IsNullOrEmpty(owner))
        {
            throw new ConfigStoreException(ConfigStoreError.InvalidOwnerName);
        }

        var anchors = await db.Anchors
            .Where(m =>
                m.Owner == owner)
            .ToListAsync();

        foreach (var anchor in anchors)
        {
            db.Anchors.Remove(anchor);
        }
    }
}