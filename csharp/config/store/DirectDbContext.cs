/* 
 Copyright (c) 2010, Direct Project
 All rights reserved.

 Authors:
    Umesh Madan     umeshma@microsoft.com
    Joe Shook       Joseph.Shook@Surescripts.com
  
Redistribution and use in source and binary forms, with or without modification, are permitted provided that the following conditions are met:

Redistributions of source code must retain the above copyright notice, this list of conditions and the following disclaimer.
Redistributions in binary form must reproduce the above copyright notice, this list of conditions and the following disclaimer in the documentation and/or other materials provided with the distribution.
Neither the name of The Direct Project (directproject.org) nor the names of its contributors may be used to endorse or promote products derived from this software without specific prior written permission.
THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 
*/

using Health.Direct.Config.Store.Entity;
using Microsoft.EntityFrameworkCore;
using Microsoft.Extensions.Logging;
using Microsoft.Extensions.Logging.Debug;

namespace Health.Direct.Config.Store;

/// <inheritdoc />
public class DirectDbContext : DbContext
{
    public DbSet<Address> Addresses { get; set; } = null!;
    public DbSet<Administrator> Administrators { get; set; } = null!;
    public DbSet<Anchor> Anchors { get; set; } = null!;
    // public DbSet<NamedBlob> Blobs { get; set; } = null!;
    public DbSet<Bundle> Bundles { get; set; } = null!;
    public DbSet<CertPolicy> CertPolicies { get; set; } = null!;
    public DbSet<CertPolicyGroup> CertPolicyGroups { get; set; } = null!;
    public DbSet<CertPolicyGroupDomainMap> CertPolicyGroupDomainMaps { get; set; } = null!;
    public DbSet<CertPolicyGroupMap> CertPolicyGroupMaps { get; set; } = null!;
    public DbSet<Certificate> Certificates { get; set; } = null!;
    public DbSet<DnsRecord> DnsRecords { get; set; } = null!;
    public DbSet<Domain> Domains { get; set; } = null!;
    public DbSet<Mdn> Mdns { get; set; } = null!;
    // public DbSet<Property> Properties { get; set; } = null!;

    private static string _connectionString;
    private readonly int _timeout = 10;

    private static readonly LoggerFactory MyLoggerFactory =
        new LoggerFactory(new[]
        {
                new DebugLoggerProvider()
        });

    public DirectDbContext(DbContextOptions dbContextOptions) : base(dbContextOptions)
    {
    }

    public DirectDbContext(string connectionString)
    {
        _connectionString = connectionString;
    }

    public DirectDbContext(string connectionString, int commandTimeout)
        : this(connectionString)
    {
        _timeout = commandTimeout;
    }

    public DirectDbContext(string connectionString, int commandTimeout, bool objectTracking)
        : this(connectionString, commandTimeout)
    {
        if (objectTracking) return;

        Domains.AsNoTracking();
        Addresses.AsNoTracking();
        Anchors.AsNoTracking();
        Certificates.AsNoTracking();
        DnsRecords.AsNoTracking();
        Bundles.AsNoTracking();
        Mdns.AsNoTracking();
    }

    /// <inheritdoc />
    protected override void OnConfiguring(DbContextOptionsBuilder optionsBuilder)
    {
        if (!optionsBuilder.IsConfigured)
        {
            optionsBuilder
                .EnableSensitiveDataLogging()
                .UseLoggerFactory(MyLoggerFactory)
                .UseSqlServer(
                    _connectionString,
                    providerOptions => providerOptions.CommandTimeout(_timeout));
        }
    }

    protected override void OnModelCreating(ModelBuilder modelBuilder)
    {
        var assemblyWithConfigurations = GetType().Assembly;
        modelBuilder.ApplyConfigurationsFromAssembly(assemblyWithConfigurations);
    }
}