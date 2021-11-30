/* 
 Copyright (c) 2010, Direct Project
 All rights reserved.

 Authors:
    Joe Shook       Joseph.Shook@Surescripts.com

Redistribution and use in source and binary forms, with or without modification, are permitted provided that the following conditions are met:

Redistributions of source code must retain the above copyright notice, this list of conditions and the following disclaimer.
Redistributions in binary form must reproduce the above copyright notice, this list of conditions and the following disclaimer in the documentation and/or other materials provided with the distribution.
Neither the name of The Direct Project (directproject.org) nor the names of its contributors may be used to endorse or promote products derived from this software without specific prior written permission.
THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 
*/

using Health.Direct.Config.Store.Entity;
using Microsoft.EntityFrameworkCore;
using Microsoft.EntityFrameworkCore.Metadata.Builders;

namespace Health.Direct.Config.Store.EntityMap
{
    /// <inheritdoc />
    public class AddressEntityMap : IEntityTypeConfiguration<Address>
    {
        /// <inheritdoc />
        public void Configure(EntityTypeBuilder<Address> builder)
        {
            builder.HasKey(e => e.EmailAddress);
            
            builder.HasIndex(e => e.ID, "IX_Addresses_AddressID")
                .IsUnique();

            builder.Property(e => e.EmailAddress)
                .HasMaxLength(400)
                .IsUnicode(false);

            builder.Property(e => e.ID)
                .ValueGeneratedOnAdd()
                .HasColumnName("AddressID");

            builder.Property(e => e.CreateDate)
                .HasColumnType("datetime")
                .HasDefaultValueSql("(getdate())");

            builder.Property(e => e.DisplayName)
                .HasMaxLength(64)
                .IsUnicode(false);

            builder.Property(e => e.DomainID)
                .HasColumnName("DomainID");

            builder.Property(e => e.Type).HasMaxLength(64);

            builder.Property(e => e.UpdateDate)
                .HasColumnType("datetime")
                .HasDefaultValueSql("(getdate())");

            builder.HasOne(d => d.Domain)
                .WithMany(p => p.Addresses)
                .HasPrincipalKey(p => p.ID)
                .HasForeignKey(d => d.DomainID)
                .OnDelete(DeleteBehavior.ClientSetNull)
                .HasConstraintName("FK_Addresses_DomainID");
        }
    }

    public class AdministratorEntityMap : IEntityTypeConfiguration<Administrator>
    {
        public void Configure(EntityTypeBuilder<Administrator> builder)
        {
            builder.HasKey(e => e.Username);

            builder.Property(e => e.Username)
                .HasMaxLength(50)
                .IsUnicode(false);

            builder.Property(e => e.ID)
                .ValueGeneratedOnAdd()
                .HasColumnName("AdministratorID");

            builder.Property(e => e.CreateDate)
                .HasColumnType("datetime")
                .HasDefaultValueSql("(getdate())");

            builder.Property(e => e.PasswordHash)
                .HasMaxLength(50)
                .IsUnicode(false);

            builder.Property(e => e.UpdateDate)
                .HasColumnType("datetime")
                .HasDefaultValueSql("(getdate())");
        }
    }

    public class AnchorEntityMap : IEntityTypeConfiguration<Anchor>
    {
        public void Configure(EntityTypeBuilder<Anchor> builder)
        {
            builder.HasKey(e => new { e.Owner, e.Thumbprint });

            builder.Property(e => e.Owner)
                .HasMaxLength(400)
                .IsUnicode(false);

            builder.Property(e => e.Thumbprint).HasMaxLength(64);

            builder.Property(e => e.ID)
                .ValueGeneratedOnAdd()
                .HasColumnName("CertificateID");

            builder.Property(e => e.CreateDate)
                .HasColumnType("datetime")
                .HasDefaultValueSql("(getdate())");

            builder.Property(e => e.ForIncoming)
                .IsRequired()
                .HasDefaultValueSql("((1))");

            builder.Property(e => e.ForOutgoing)
                .IsRequired()
                .HasDefaultValueSql("((1))");

            builder.Property(e => e.ValidEndDate).HasColumnType("datetime");

            builder.Property(e => e.ValidStartDate).HasColumnType("datetime");
        }
    }

    public class NamedBlobEntityMap : IEntityTypeConfiguration<NamedBlob>
    {
        public void Configure(EntityTypeBuilder<NamedBlob> builder)
        {
            builder.HasKey(e => e.Name);

            builder.HasIndex(e => e.BlobId, "IX_Blobs_ID")
                .IsUnique();

            builder.Property(e => e.Name)
                .HasMaxLength(255)
                .IsUnicode(false);

            builder.Property(e => e.BlobId)
                .ValueGeneratedOnAdd()
                .HasColumnName("BlobID");

            builder.Property(e => e.CreateDate)
                .HasColumnType("datetime")
                .HasDefaultValueSql("(getdate())");

            builder.Property(e => e.UpdateDate)
                .HasColumnType("datetime")
                .HasDefaultValueSql("(getdate())");
        }
    }

    public class BundleEntityMap : IEntityTypeConfiguration<Bundle>
    {
        public void Configure(EntityTypeBuilder<Bundle> builder)
        {
            builder.HasIndex(e => e.Owner, "IX_Bundles_Owner");

            builder.Property(e => e.ID).HasColumnName("BundleID");

            builder.Property(e => e.CreateDate)
                .HasColumnType("datetime")
                .HasDefaultValueSql("(getdate())");

            builder.Property(e => e.ForIncoming)
                .IsRequired()
                .HasDefaultValueSql("((1))");

            builder.Property(e => e.ForOutgoing)
                .IsRequired()
                .HasDefaultValueSql("((1))");

            builder.Property(e => e.Owner)
                .HasMaxLength(400)
                .IsUnicode(false);

            builder.Property(e => e.Url)
                .HasMaxLength(2048)
                .IsUnicode(false);
        }
    }

    public class CertPolicyEntityMap : IEntityTypeConfiguration<CertPolicy>
    {
        public void Configure(EntityTypeBuilder<CertPolicy> builder)
        {
            builder.HasIndex(e => e.Name, "IX_CertPolicies")
                .IsUnique();

            builder.Property(e => e.CreateDate)
                .HasColumnType("datetime")
                .HasDefaultValueSql("(getdate())");

            builder.Property(e => e.Description)
                .HasMaxLength(255)
                .IsUnicode(false);

            builder.Property(e => e.Lexicon)
                .HasMaxLength(255)
                .IsUnicode(false);

            builder.Property(e => e.Name)
                .HasMaxLength(255)
                .IsUnicode(false);
        }
    }

    public class CertPolicyGroupEntityMap : IEntityTypeConfiguration<CertPolicyGroup>
    {
        public void Configure(EntityTypeBuilder<CertPolicyGroup> builder)
        {
            builder.HasIndex(e => e.Name, "IX_CertPolicyGroups_Name")
                .IsUnique();

            builder.Property(e => e.CreateDate)
                .HasColumnType("datetime")
                .HasDefaultValueSql("(getdate())");

            builder.Property(e => e.Description)
                .HasMaxLength(255)
                .IsUnicode(false);

            builder.Property(e => e.Name)
                .HasMaxLength(255)
                .IsUnicode(false);
        }
    }

    public class CertPolicyGroupDomainMapEntityMap : IEntityTypeConfiguration<CertPolicyGroupDomainMap>
    {
        public void Configure(EntityTypeBuilder<CertPolicyGroupDomainMap> builder)
        {
            builder.HasKey(e => new { e.Owner, e.CertPolicyGroupId });

            builder.ToTable("CertPolicyGroupDomainMap");

            builder.Property(e => e.Owner)
                .HasMaxLength(400)
                .IsUnicode(false);

            builder.Property(e => e.CreateDate)
                .HasColumnType("datetime")
                .HasDefaultValueSql("(getdate())");

            builder.Property(e => e.ID)
                .HasColumnName("MapId")
                .ValueGeneratedOnAdd();

            builder.HasOne(d => d.CertPolicyGroup)
                .WithMany(p => p.CertPolicyGroupDomainMaps)
                .HasForeignKey(d => d.CertPolicyGroupId)
                .OnDelete(DeleteBehavior.ClientSetNull)
                .HasConstraintName("FK_CertPolicyGroupDomainMap_CertPolicyGroups");
        }
    }

    public class CertPolicyGroupMapEntityMap : IEntityTypeConfiguration<CertPolicyGroupMap>
    {
        public void Configure(EntityTypeBuilder<CertPolicyGroupMap> builder)
        {
            builder.HasKey(e => new { e.CertPolicyGroupId, e.ForOutgoing, e.CertPolicyId, e.PolicyUse, e.ForIncoming });

            builder.ToTable("CertPolicyGroupMap");

            builder.Property(e => e.ForOutgoing).HasDefaultValueSql("((1))");

            builder.Property(e => e.ForIncoming).HasDefaultValueSql("((1))");

            builder.Property(e => e.CreateDate)
                .HasColumnType("datetime")
                .HasDefaultValueSql("(getdate())");

            builder.Property(e => e.ID)
                .HasColumnName("MapId")
                .ValueGeneratedOnAdd();

            builder.HasOne(d => d.CertPolicyGroup)
                .WithMany(p => p.CertPolicyGroupMaps)
                .HasForeignKey(d => d.CertPolicyGroupId)
                .OnDelete(DeleteBehavior.ClientSetNull)
                .HasConstraintName("FK_CertPolicyGroupMap_CertPolicyGroups");

            builder.HasOne(d => d.CertPolicy)
                .WithMany(p => p.CertPolicyGroupMaps)
                .HasForeignKey(d => d.CertPolicyId)
                .OnDelete(DeleteBehavior.ClientSetNull)
                .HasConstraintName("FK_CertPolicyGroupMap_CertPolicies");
        }
    }

    public class CertificateEntityMap : IEntityTypeConfiguration<Certificate>
    {
        public void Configure(EntityTypeBuilder<Certificate> builder)
        {
            builder.HasKey(e => new { e.Owner, e.Thumbprint });

            builder.HasIndex(e => e.ID, "IX_Certificates_CertificateID")
                .IsUnique();

            builder.Property(e => e.Owner)
                .HasMaxLength(400)
                .IsUnicode(false);

            builder.Property(e => e.Thumbprint).HasMaxLength(64);

            builder.Property(e => e.ID)
                .ValueGeneratedOnAdd()
                .HasColumnName("CertificateID");

            builder.Property(e => e.CreateDate)
                .HasColumnType("datetime")
                .HasDefaultValueSql("(getdate())");

            builder.Property(e => e.ValidEndDate).HasColumnType("datetime");

            builder.Property(e => e.ValidStartDate).HasColumnType("datetime");
        }
    }

    public class DnsRecordEntityMap : IEntityTypeConfiguration<DnsRecord>
    {
        public void Configure(EntityTypeBuilder<DnsRecord> builder)
        {
            builder.HasKey(e => e.ID);

            builder.HasIndex(e => e.DomainName, "IX_DnsRecords_DomainName");

            builder.Property(e => e.ID)
                .HasColumnName("RecordID");

            builder.Property(e => e.CreateDate)
                .HasColumnType("datetime")
                .HasDefaultValueSql("(getdate())");

            builder.Property(e => e.DomainName)
                .HasMaxLength(255)
                .IsUnicode(false);

            builder.Property(e => e.Notes)
                .HasMaxLength(500)
                .IsUnicode(false)
                .HasDefaultValueSql("('')");

            builder.Property(e => e.TypeID).HasColumnName("TypeID");

            builder.Property(e => e.UpdateDate)
                .HasColumnType("datetime")
                .HasDefaultValueSql("(getdate())");
        }
    }

    public class DomainEntityMap : IEntityTypeConfiguration<Domain>
    {
        public void Configure(EntityTypeBuilder<Domain> builder)
        {
            builder.HasKey(e => e.Name);

            builder.HasIndex(e => e.ID, "IX_Domains_DomainID")
                .IsUnique();
            
            builder.Property(e => e.Name)
                .HasColumnName("DomainName")
                .HasMaxLength(255)
                .IsUnicode(false);

            builder.Property(e => e.AgentName)
                .HasMaxLength(25)
                .IsUnicode(false);

            builder.Property(e => e.CreateDate)
                .HasColumnType("datetime")
                .HasDefaultValueSql("(getdate())");

            builder.Property(e => e.ID)
                .ValueGeneratedOnAdd()
                .HasColumnName("DomainID");

            builder.Property(e => e.UpdateDate)
                .HasColumnType("datetime")
                .HasDefaultValueSql("(getdate())");
        }
    }

    public class MdnEntityMap : IEntityTypeConfiguration<Mdn>
    {
        public void Configure(EntityTypeBuilder<Mdn> builder)
        {
            builder.HasIndex(e => e.CreateDate, "CreateDate");

            builder.HasIndex(e => e.MdnIdentifier, "IX_Mdns_MdnIdentifier")
                .IsUnique();

            builder.Property(e => e.Id)
                .HasColumnName("MdnId");

            builder.Property(e => e.CreateDate)
                .HasColumnType("datetime")
                .HasDefaultValueSql("(getdate())");

            builder.Property(e => e.MdnIdentifier)
                .HasMaxLength(32)
                .IsUnicode(false)
                .IsFixedLength();

            builder.Property(e => e.MessageId)
                .HasMaxLength(255);

            builder.Property(e => e.Recipient)
                .HasColumnName("RecipientAddress")
                .HasMaxLength(400);

            builder.Property(e => e.Sender)
                .HasColumnName("SenderAddress")
                .HasMaxLength(400);

            builder.Property(e => e.Status).HasMaxLength(15);

            builder.Property(e => e.SubjectValue)
                .HasColumnName("Subject")
                .HasMaxLength(998);
        }
    }

    public class PropertyEntityMap : IEntityTypeConfiguration<Property>
    {
        public void Configure(EntityTypeBuilder<Property> builder)
        {
            builder.HasKey(e => e.Name);

            builder.HasIndex(e => e.PropertyId, "IX_Properties_ID");

            builder.Property(e => e.Name).HasMaxLength(255);

            builder.Property(e => e.PropertyId)
                .ValueGeneratedOnAdd()
                .HasColumnName("PropertyID");

            builder.Property(e => e.Value).HasMaxLength(512);
        }
    }
}
