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
using System.Security.Cryptography.X509Certificates;
using System.Threading.Tasks;
using Health.Direct.Common;
using Health.Direct.Common.Certificates;
using Health.Direct.Common.Extensions;
using Health.Direct.Config.Store.Entity;
using Microsoft.EntityFrameworkCore;
using Org.BouncyCastle.Ocsp;

namespace Health.Direct.Config.Store;

public class CertificateManager : IX509CertificateIndex
{
    internal CertificateManager(ConfigStore store)
    {
        Store = store;
    }

    internal ConfigStore Store { get; }

    public async Task<Certificate> Add(Certificate cert)
    {
        await using var db = Store.CreateContext();
        await Add(db, cert);
        await db.SaveChangesAsync();
        return cert;
    }

    public async Task Add(IEnumerable<Certificate> certs)
    {
        if (certs == null)
        {
            throw new ArgumentNullException(nameof(certs));
        }

        await using var db = Store.CreateContext();
        foreach (var cert in certs)
        {
            await Add(db, cert);
        }
        await db.SaveChangesAsync();
    }

    public async Task Add(ConfigDatabase db, Certificate cert)
    {
        if (db == null)
        {
            throw new ArgumentNullException(nameof(db));
        }

        if (cert == null)
        {
            throw new ConfigStoreException(ConfigStoreError.InvalidCertificate);
        }

        cert.ValidateHasData();
        await db.Certificates.AddAsync(cert);
    }

    public async Task<Certificate> AddHsm(Certificate cert)
    {
        await using var db = Store.CreateContext();
        await AddHsm(db, cert);
        await db.SaveChangesAsync();
        return cert;
    }

    public async Task AddHsm(ConfigDatabase db, Certificate cert)
    {
        if (db == null)
        {
            throw new ArgumentNullException(nameof(db));
        }

        if (cert == null)
        {
            throw new ConfigStoreException(ConfigStoreError.InvalidCertificate);
        }

        cert.ValidateHasData();
        var domain = db.Domains.SingleOrDefault(d => d.Name == cert.Owner);

        if (domain == null)
        {
            throw new ConfigStoreException(ConfigStoreError.MissingDomain);
        }

        domain.SecurityStandard = SecurityStandard.Fips1402;
        await db.Certificates.AddAsync(cert);
    }

    public async Task<Certificate?> Get(long certId)
    {
        await using var db = Store.CreateReadContext();
        return await Get(db, certId);
    }

    public async Task<List<Certificate>> Get(long[] certIDs)
    {
        if (certIDs.IsNullOrEmpty())
        {
            throw new ConfigStoreException(ConfigStoreError.InvalidIDs);
        }

        await using var db = Store.CreateReadContext();

        return await db.Certificates
            .Where(c => certIDs.Contains(c.ID))
            .ToListAsync();
    }

    public async Task<Certificate?> Get(ConfigDatabase db, long certID)
    {
        if (db == null)
        {
            throw new ArgumentNullException(nameof(db));
        }

        return await db.Certificates
            .Where(c => c.ID == certID)
            .SingleOrDefaultAsync();
    }

    public async Task<List<Certificate>> Get(long lastCertID, int maxResults)
    {
        await using var db = Store.CreateReadContext();
        return await Get(db, lastCertID, maxResults);
    }

    public async Task<List<Certificate>> Get(ConfigDatabase db, long lastCertID, int maxResults)
    {
        if (db == null)
        {
            throw new ArgumentNullException(nameof(db));
        }

        return await db.Certificates
            .Where(c => c.ID > lastCertID)
            .OrderBy(c => c.ID)
            .Take(maxResults)
            .ToListAsync();
    }

    public async Task<Certificate?> Get(string owner, string thumbprint)
    {
        await using var db = Store.CreateReadContext();
        return await Get(db, owner, thumbprint);
    }

    public async Task<Certificate?> Get(ConfigDatabase db, string owner, string thumbprint)
    {
        if (db == null)
        {
            throw new ArgumentNullException(nameof(db));
        }

        return await db.Certificates
            .FirstOrDefaultAsync(c =>
                c.Owner == owner
                && c.Thumbprint == thumbprint);
    }

    public async Task<List<Certificate>> Get(string owner)
    {
        await using var db = Store.CreateReadContext();
        return await Get(db, owner);
    }

    public async Task<List<Certificate>> Get(ConfigDatabase db, string owner)
    {
        return await GetAsync(db, owner, (EntityStatus?)null);
    }

    public async Task<List<Certificate>> Get(string owner, EntityStatus? status)
    {
        await using var db = Store.CreateReadContext();
        return await GetAsync(db, owner, status);
    }

    public async Task<List<Certificate>> GetAsync(ConfigDatabase db, string owner, EntityStatus? status)
    {
        if (db == null)
        {
            throw new ArgumentNullException(nameof(db));
        }

        if (string.IsNullOrEmpty(owner))
        {
            throw new ConfigStoreException(ConfigStoreError.InvalidOwnerName);
        }

        if (status == null)
        {
            return await db.Certificates
                .Where(c => c.Owner == owner)
                .ToListAsync();
        }

        return await db.Certificates
            .Where(c => c.Owner == owner && c.Status == status)
            .ToListAsync();
    }

    public async Task SetStatus(long[] certificateIDs, EntityStatus status)
    {
        if (certificateIDs.IsNullOrEmpty())
        {
            throw new ConfigStoreException(ConfigStoreError.InvalidIDs);
        }

        await using var db = Store.CreateContext();
        
        foreach (var id in certificateIDs)
        {
            SetStatus(db, id, status);
        }

        await db.SaveChangesAsync();
    }

    public async Task SetStatus(long certificateID, EntityStatus status)
    {
        await using var db = Store.CreateContext();
        await SetStatus(db, certificateID, status);
        await db.SaveChangesAsync(); 
    }

    public async Task SetStatus(ConfigDatabase db, long certificateID, EntityStatus status)
    {
        if (db == null)
        {
            throw new ArgumentNullException(nameof(db));
        }

        var certificate = await db.Certificates.SingleOrDefaultAsync(c => c.ID == certificateID);
        
        if (certificate != null)
        {
            certificate.Status = status;
            db.Entry(certificate).State = EntityState.Modified;
        }
    }

    public async Task SetStatus(string owner, EntityStatus status)
    {
        await using var db = Store.CreateContext();
        await SetStatus(db, owner, status);
        await db.SaveChangesAsync(); 
    }

    public async Task SetStatus(ConfigDatabase db, string owner, EntityStatus status)
    {
        if (db == null)
        {
            throw new ArgumentNullException(nameof(db));
        }

        var entity = await db.Certificates.SingleOrDefaultAsync(c => c.Owner == owner);

        if (entity != null)
        {
            entity.Status = status;
            db.Entry(entity).State = EntityState.Modified;
        }
    }

    public async Task Remove(long certificateID)
    {
        await using var db = Store.CreateContext();
        await Remove(db, certificateID);
    }

    public async Task Remove(ConfigDatabase db, long certificateID)
    {
        if (db == null)
        {
            throw new ArgumentNullException(nameof(db));
        }

        var certificate = await db.Certificates.SingleAsync(c => c.ID == certificateID);
        db.Certificates.Remove(certificate);
    }

    public async Task Remove(long[] certificateIDs)
    {
        await using var db = Store.CreateContext();
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

        var certificates = await db.Certificates
            .Where(c => certificateIDs.Contains(c.ID))
            .ToListAsync();

        db.Certificates.RemoveRange(certificates);
    }

    public void Remove(string ownerName)
    {
        using var db = Store.CreateContext();
        Remove(db, ownerName);
    }

    public async Task<int> Remove(ConfigDatabase db, string ownerName)
    {
        if (db == null)
        {
            throw new ArgumentNullException(nameof(db));
        }

        if (string.IsNullOrEmpty(ownerName))
        {
            throw new ConfigStoreException(ConfigStoreError.InvalidOwnerName);
        }

        return await db.Database.ExecuteSqlRawAsync("DELETE from Certificates where Owner = {0}", ownerName);
    }
    
    public X509Certificate2Collection this[string subjectName]
    {
        get
        {
            return Certificate.ToX509Collection(SyncOverAsyncHelper.RunSync(() => Get(subjectName)).ToArray());
        }
    }
}
