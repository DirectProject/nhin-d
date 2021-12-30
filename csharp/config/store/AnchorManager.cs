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

#nullable enable

using System;
using System.Collections.Generic;
using System.Linq;
using System.Threading.Tasks;
using Health.Direct.Common.Extensions;
using Microsoft.EntityFrameworkCore;

namespace Health.Direct.Config.Store;

public interface IAnchorManager
{
    Task<Anchor> Add(Anchor anchor);
    Task Add(IEnumerable<Anchor> anchors);
    Task<List<Anchor>> Get(long[] certificateIDs);
    Task<List<Anchor>> Get(long lastCertId, int maxResults);
    Task<List<Anchor>> Get(string owner);
    Task<Anchor?> Get(string owner, string thumbprint);
    Task<List<Anchor>> GetIncoming(string ownerName);
    Task<List<Anchor>> GetIncoming(string ownerName, EntityStatus? status);
    Task<List<Anchor>> GetOutgoing(string ownerName);
    Task<List<Anchor>> GetOutgoing(string ownerName, EntityStatus? status);
    Task SetStatus(string owner, EntityStatus status);
    Task Remove(long[] certificateIDs);
    Task Remove(string owner, string thumbprint);
    Task Remove(string ownerName);
}

/// <summary>
/// Manage anchor data access 
/// </summary>
public class AnchorManager : IAnchorManager
{
    private readonly DirectDbContext _dbContext;

    internal AnchorManager(DirectDbContext dbContext)
    {
        _dbContext = dbContext;
    }
    
    public async Task<Anchor> Add(Anchor anchor)
    {
        _dbContext.Anchors.Add(anchor);
        await _dbContext.SaveChangesAsync();
        return anchor;
    }

    public async Task Add(IEnumerable<Anchor> anchors)
    {
        if (anchors == null)
        {
            throw new ArgumentNullException(nameof(anchors));
        }
        
        foreach(var anchor in anchors)
        {
            _dbContext.Anchors.Add(anchor);
        }

        await _dbContext.SaveChangesAsync();
    }
    
    public async Task<List<Anchor>> Get(long[] certificateIDs)
    {
        if (certificateIDs.IsNullOrEmpty())
        {
            throw new ConfigStoreException(ConfigStoreError.InvalidIDs);
        }

        return await _dbContext.Anchors
            .Where(a => certificateIDs.Contains(a.ID))
            .ToListAsync();
    }

    public async Task<List<Anchor>> Get(long lastCertId, int maxResults)
    {
        var entities = await _dbContext.Anchors
            .Where(a => a.ID > lastCertId)
            .OrderBy(a => a.ID)
            .Take(maxResults)
            .ToListAsync();

        return entities;
    }
    

    public async Task<List<Anchor>> Get(string owner)
    {
        if (string.IsNullOrEmpty(owner))
        {
            throw new ConfigStoreException(ConfigStoreError.MissingDomain);
        }

        return await _dbContext.Anchors
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
        
        List<Anchor> matches;

        if (status == null)
        {
            matches = await _dbContext.Anchors
                .Where(a => 
                    a.ForIncoming == true
                    && a.Owner == ownerName)
                .ToListAsync();
        }
        else
        {
            matches = await _dbContext.Anchors
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
        
        List<Anchor> matches;

        if (status == null)
        {
            matches = await _dbContext.Anchors
                .Where(a =>
                    a.ForOutgoing == true
                    && a.Owner == ownerName)
                .ToListAsync();
        }
        else
        {
            matches = await _dbContext.Anchors
                .Where(a =>
                    a.ForOutgoing == true
                    && a.Owner == ownerName
                    && a.Status == status.Value)
                .ToListAsync();
        }

        return matches;
    }

    
    public async Task<Anchor?> Get(string owner, string thumbprint)
    {
        if (string.IsNullOrEmpty(owner))
        {
            throw new ConfigStoreException(ConfigStoreError.InvalidOwnerName);
        }
        if (string.IsNullOrEmpty(thumbprint))
        {
            throw new ConfigStoreException(ConfigStoreError.InvalidThumbprint);
        }

        return await _dbContext.Anchors
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
        
        var entities = await _dbContext.Anchors
            .Where(a => a.Owner == owner)
            .ToListAsync();

        foreach (var entity in entities)
        {
            entity.Status = status;
        }

        await _dbContext.SaveChangesAsync();
    }

    public async Task Remove(long[] certificateIDs)
    {
        if (certificateIDs.IsNullOrEmpty())
        {
            throw new ConfigStoreException(ConfigStoreError.InvalidIDs);
        }

        for (var i = 0; i < certificateIDs.Length; ++i)
        {
            var anchor = await _dbContext.Anchors
                .SingleOrDefaultAsync(m => m.ID == certificateIDs[i]);

            if (anchor != null)
            {
                _dbContext.Anchors.Remove(anchor);
            }
        }
        await _dbContext.SaveChangesAsync();
    }
    

    public async Task Remove(string owner, string thumbprint)
    {
        if (string.IsNullOrEmpty(owner))
        {
            throw new ConfigStoreException(ConfigStoreError.InvalidOwnerName);
        }
        if (string.IsNullOrEmpty(thumbprint))
        {
            throw new ConfigStoreException(ConfigStoreError.InvalidThumbprint);
        }

        var anchors = await _dbContext.Anchors
            .Where(m =>
                m.Owner == owner
                && m.Thumbprint == thumbprint)
            .ToListAsync();

        foreach (var anchor in anchors)
        {
            _dbContext.Anchors.Remove(anchor);
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

        var anchors = await _dbContext.Anchors
            .Where(m =>
                m.Owner == ownerName)
            .ToListAsync();

        foreach (var anchor in anchors)
        {
            _dbContext.Anchors.Remove(anchor);
        }

        await _dbContext.SaveChangesAsync();
    }
}