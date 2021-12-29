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
using System.Security.Cryptography.X509Certificates;
using System.Threading.Tasks;
using Health.Direct.Common;
using Health.Direct.Common.Certificates;
using Health.Direct.Common.Extensions;
using Health.Direct.Config.Store.Entity;
using Microsoft.EntityFrameworkCore;

namespace Health.Direct.Config.Store;

public class CertificateManager : IX509CertificateIndex
{
    private readonly DirectDbContext _dbContext;

    internal CertificateManager(DirectDbContext dbContext)
    {
        _dbContext = dbContext;
    }

    public async Task<Certificate> Add(Certificate cert)
    {
        if (cert == null)
        {
            throw new ConfigStoreException(ConfigStoreError.InvalidCertificate);
        }

        cert.ValidateHasData();
        _dbContext.Certificates.Add(cert);
        await _dbContext.SaveChangesAsync();
        return cert;
    }

    public async Task Add(IEnumerable<Certificate> certs)
    {
        if (certs == null)
        {
            throw new ArgumentNullException(nameof(certs));
        }
        
        foreach (var cert in certs)
        {
            cert.ValidateHasData();
            _dbContext.Certificates.Add(cert);
        }
        
        await _dbContext.SaveChangesAsync();
    }

    
    public async Task<Certificate> AddHsm(Certificate cert)
    {
        if (cert == null)
        {
            throw new ConfigStoreException(ConfigStoreError.InvalidCertificate);
        }

        cert.ValidateHasData();
        var domain = _dbContext.Domains.SingleOrDefault(d => d.Name == cert.Owner);

        if (domain == null)
        {
            throw new ConfigStoreException(ConfigStoreError.MissingDomain);
        }

        domain.SecurityStandard = SecurityStandard.Fips1402;
        _dbContext.Certificates.Add(cert);
        await _dbContext.SaveChangesAsync();

        return cert;
    }

    public async Task<Certificate?> Get(long certId)
    {
        return await _dbContext.Certificates
            .Where(c => c.ID == certId)
            .SingleOrDefaultAsync(); 
    }

    public async Task<List<Certificate>> Get(long[] certIDs)
    {
        if (certIDs.IsNullOrEmpty())
        {
            throw new ConfigStoreException(ConfigStoreError.InvalidIDs);
        }
        
        return await _dbContext.Certificates
            .Where(c => certIDs.Contains(c.ID))
            .ToListAsync();
    }

   
    public async Task<List<Certificate>> Get(long lastCertId, int maxResults)
    {
        return await _dbContext.Certificates
            .Where(c => c.ID > lastCertId)
            .OrderBy(c => c.ID)
            .Take(maxResults)
            .ToListAsync();
    }
    
    public async Task<Certificate?> Get(string owner, string thumbprint)
    {
        return await _dbContext.Certificates
            .FirstOrDefaultAsync(c =>
                c.Owner == owner
                && c.Thumbprint == thumbprint);
    }
    
    public async Task<List<Certificate>> Get(string owner)
    {
        return await Get(owner, (EntityStatus?)null);
    }

    public async Task<List<Certificate>> Get(string owner, EntityStatus? status)
    {
        if (string.IsNullOrEmpty(owner))
        {
            throw new ConfigStoreException(ConfigStoreError.InvalidOwnerName);
        }

        if (status == null)
        {
            return await _dbContext.Certificates
                .Where(c => c.Owner == owner)
                .ToListAsync();
        }

        return await _dbContext.Certificates
            .Where(c => c.Owner == owner && c.Status == status)
            .ToListAsync();
    }
    
    public async Task SetStatus(long[] certificateIDs, EntityStatus status)
    {
        if (certificateIDs.IsNullOrEmpty())
        {
            throw new ConfigStoreException(ConfigStoreError.InvalidIDs);
        }

        foreach (var id in certificateIDs)
        {
            var certificate = await _dbContext.Certificates.SingleOrDefaultAsync(c => c.ID == id);

            if (certificate != null)
            {
                certificate.Status = status;
            }
        }

        await _dbContext.SaveChangesAsync();
    }

    
    public async Task SetStatus(long certificateId, EntityStatus status)
    {
        var certificate = await _dbContext.Certificates.SingleOrDefaultAsync(c => c.ID == certificateId);
        
        if (certificate != null)
        {
            certificate.Status = status;
        }

        await _dbContext.SaveChangesAsync();
    }

    public async Task SetStatus(string owner, EntityStatus status)
    {
        var certificates = await _dbContext.Certificates
            .Where(c => c.Owner == owner)
            .ToListAsync();

        foreach (var certificate in certificates)
        {
            certificate.Status = status;
        }

        await _dbContext.SaveChangesAsync(); 
    }
    
    public async Task Remove(long certificateId)
    {
        var certificate = await _dbContext.Certificates.SingleAsync(c => c.ID == certificateId);
        _dbContext.Certificates.Remove(certificate);

        await _dbContext.SaveChangesAsync();
    }
    
    public async Task Remove(long[] certificateIDs)
    {
        if (certificateIDs.IsNullOrEmpty())
        {
            throw new ConfigStoreException(ConfigStoreError.InvalidIDs);
        }

        var certificates = await _dbContext.Certificates
            .Where(c => certificateIDs.Contains(c.ID))
            .ToListAsync();

        _dbContext.Certificates.RemoveRange(certificates);

        await _dbContext.SaveChangesAsync();
    }
    
    public async Task Remove(string ownerName)
    {
        if (string.IsNullOrEmpty(ownerName))
        {
            throw new ConfigStoreException(ConfigStoreError.InvalidOwnerName);
        }

        // await _dbContext.Database.ExecuteSqlRawAsync("DELETE from Certificates where Owner = {0}", ownerName);

        var certificates = _dbContext.Certificates
            .Where(c => c.Owner == ownerName)
            .Select(c => new Certificate() { ID = c.ID });

        _dbContext.ChangeTracker.Clear();
        _dbContext.RemoveRange(certificates);
        
        await _dbContext.SaveChangesAsync();
    }
    
    public X509Certificate2Collection this[string subjectName]
    {
        get
        {
            return Certificate.ToX509Collection(SyncOverAsyncHelper.RunSync(() => Get(subjectName)).ToArray());
        }
    }
}
