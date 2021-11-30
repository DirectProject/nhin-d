using System;
using System.Collections.Generic;
using Microsoft.EntityFrameworkCore;
using Microsoft.EntityFrameworkCore.Metadata;

namespace config.store.temp.Models
{
    public partial class DirectConfigContext : DbContext
    {
        public DirectConfigContext()
        {
        }

        public DirectConfigContext(DbContextOptions<DirectConfigContext> options)
            : base(options)
        {
        }

        public virtual DbSet<Address> Addresses { get; set; } = null!;
        public virtual DbSet<Administrator> Administrators { get; set; } = null!;
        public virtual DbSet<Anchor> Anchors { get; set; } = null!;
        public virtual DbSet<Blob> Blobs { get; set; } = null!;
        public virtual DbSet<Bundle> Bundles { get; set; } = null!;
        public virtual DbSet<CertPolicy> CertPolicies { get; set; } = null!;
        public virtual DbSet<CertPolicyGroup> CertPolicyGroups { get; set; } = null!;
        public virtual DbSet<CertPolicyGroupDomainMap> CertPolicyGroupDomainMaps { get; set; } = null!;
        public virtual DbSet<CertPolicyGroupMap> CertPolicyGroupMaps { get; set; } = null!;
        public virtual DbSet<Certificate> Certificates { get; set; } = null!;
        public virtual DbSet<DnsRecord> DnsRecords { get; set; } = null!;
        public virtual DbSet<Domain> Domains { get; set; } = null!;
        public virtual DbSet<Mdn> Mdns { get; set; } = null!;
        public virtual DbSet<Property> Properties { get; set; } = null!;

        protected override void OnConfiguring(DbContextOptionsBuilder optionsBuilder)
        {
            if (!optionsBuilder.IsConfigured)
            {
#warning To protect potentially sensitive information in your connection string, you should move it out of source code. You can avoid scaffolding the connection string by using the Name= syntax to read it from configuration - see https://go.microsoft.com/fwlink/?linkid=2131148. For more guidance on storing connection strings, see http://go.microsoft.com/fwlink/?LinkId=723263.
                optionsBuilder.UseSqlServer("Server=(localdb)\\.\\ProjectsShare;Initial Catalog=DirectConfig;Integrated Security=True");
            }
        }

        protected override void OnModelCreating(ModelBuilder modelBuilder)
        {
            modelBuilder.Entity<Address>(entity =>
            {
                entity.HasKey(e => e.EmailAddress);

                entity.HasIndex(e => e.AddressId, "IX_Addresses_AddressID")
                    .IsUnique();

                entity.Property(e => e.EmailAddress)
                    .HasMaxLength(400)
                    .IsUnicode(false);

                entity.Property(e => e.AddressId)
                    .ValueGeneratedOnAdd()
                    .HasColumnName("AddressID");

                entity.Property(e => e.CreateDate)
                    .HasColumnType("datetime")
                    .HasDefaultValueSql("(getdate())");

                entity.Property(e => e.DisplayName)
                    .HasMaxLength(64)
                    .IsUnicode(false);

                entity.Property(e => e.DomainId).HasColumnName("DomainID");

                entity.Property(e => e.Type).HasMaxLength(64);

                entity.Property(e => e.UpdateDate)
                    .HasColumnType("datetime")
                    .HasDefaultValueSql("(getdate())");

                entity.HasOne(d => d.Domain)
                    .WithMany(p => p.Addresses)
                    .HasPrincipalKey(p => p.DomainId)
                    .HasForeignKey(d => d.DomainId)
                    .OnDelete(DeleteBehavior.ClientSetNull)
                    .HasConstraintName("FK_Addresses_DomainID");
            });

            modelBuilder.Entity<Administrator>(entity =>
            {
                entity.HasKey(e => e.Username);

                entity.Property(e => e.Username)
                    .HasMaxLength(50)
                    .IsUnicode(false);

                entity.Property(e => e.AdministratorId)
                    .ValueGeneratedOnAdd()
                    .HasColumnName("AdministratorID");

                entity.Property(e => e.CreateDate)
                    .HasColumnType("datetime")
                    .HasDefaultValueSql("(getdate())");

                entity.Property(e => e.PasswordHash)
                    .HasMaxLength(50)
                    .IsUnicode(false);

                entity.Property(e => e.UpdateDate)
                    .HasColumnType("datetime")
                    .HasDefaultValueSql("(getdate())");
            });

            modelBuilder.Entity<Anchor>(entity =>
            {
                entity.HasKey(e => new { e.Owner, e.Thumbprint });

                entity.Property(e => e.Owner)
                    .HasMaxLength(400)
                    .IsUnicode(false);

                entity.Property(e => e.Thumbprint).HasMaxLength(64);

                entity.Property(e => e.CertificateId)
                    .ValueGeneratedOnAdd()
                    .HasColumnName("CertificateID");

                entity.Property(e => e.CreateDate)
                    .HasColumnType("datetime")
                    .HasDefaultValueSql("(getdate())");

                entity.Property(e => e.ForIncoming)
                    .IsRequired()
                    .HasDefaultValueSql("((1))");

                entity.Property(e => e.ForOutgoing)
                    .IsRequired()
                    .HasDefaultValueSql("((1))");

                entity.Property(e => e.ValidEndDate).HasColumnType("datetime");

                entity.Property(e => e.ValidStartDate).HasColumnType("datetime");
            });

            modelBuilder.Entity<Blob>(entity =>
            {
                entity.HasKey(e => e.Name);

                entity.HasIndex(e => e.BlobId, "IX_Blobs_ID")
                    .IsUnique();

                entity.Property(e => e.Name)
                    .HasMaxLength(255)
                    .IsUnicode(false);

                entity.Property(e => e.BlobId)
                    .ValueGeneratedOnAdd()
                    .HasColumnName("BlobID");

                entity.Property(e => e.CreateDate)
                    .HasColumnType("datetime")
                    .HasDefaultValueSql("(getdate())");

                entity.Property(e => e.UpdateDate)
                    .HasColumnType("datetime")
                    .HasDefaultValueSql("(getdate())");
            });

            modelBuilder.Entity<Bundle>(entity =>
            {
                entity.HasIndex(e => e.Owner, "IX_Bundles_Owner");

                entity.Property(e => e.BundleId).HasColumnName("BundleID");

                entity.Property(e => e.CreateDate)
                    .HasColumnType("datetime")
                    .HasDefaultValueSql("(getdate())");

                entity.Property(e => e.ForIncoming)
                    .IsRequired()
                    .HasDefaultValueSql("((1))");

                entity.Property(e => e.ForOutgoing)
                    .IsRequired()
                    .HasDefaultValueSql("((1))");

                entity.Property(e => e.Owner)
                    .HasMaxLength(400)
                    .IsUnicode(false);

                entity.Property(e => e.Url)
                    .HasMaxLength(2048)
                    .IsUnicode(false);
            });

            modelBuilder.Entity<CertPolicy>(entity =>
            {
                entity.HasIndex(e => e.Name, "IX_CertPolicies")
                    .IsUnique();

                entity.Property(e => e.CreateDate)
                    .HasColumnType("datetime")
                    .HasDefaultValueSql("(getdate())");

                entity.Property(e => e.Description)
                    .HasMaxLength(255)
                    .IsUnicode(false);

                entity.Property(e => e.Lexicon)
                    .HasMaxLength(255)
                    .IsUnicode(false);

                entity.Property(e => e.Name)
                    .HasMaxLength(255)
                    .IsUnicode(false);
            });

            modelBuilder.Entity<CertPolicyGroup>(entity =>
            {
                entity.HasIndex(e => e.Name, "IX_CertPolicyGroups_Name")
                    .IsUnique();

                entity.Property(e => e.CreateDate)
                    .HasColumnType("datetime")
                    .HasDefaultValueSql("(getdate())");

                entity.Property(e => e.Description)
                    .HasMaxLength(255)
                    .IsUnicode(false);

                entity.Property(e => e.Name)
                    .HasMaxLength(255)
                    .IsUnicode(false);
            });

            modelBuilder.Entity<CertPolicyGroupDomainMap>(entity =>
            {
                entity.HasKey(e => new { e.Owner, e.CertPolicyGroupId });

                entity.ToTable("CertPolicyGroupDomainMap");

                entity.Property(e => e.Owner)
                    .HasMaxLength(400)
                    .IsUnicode(false);

                entity.Property(e => e.CreateDate)
                    .HasColumnType("datetime")
                    .HasDefaultValueSql("(getdate())");

                entity.Property(e => e.MapId).ValueGeneratedOnAdd();

                entity.HasOne(d => d.CertPolicyGroup)
                    .WithMany(p => p.CertPolicyGroupDomainMaps)
                    .HasForeignKey(d => d.CertPolicyGroupId)
                    .OnDelete(DeleteBehavior.ClientSetNull)
                    .HasConstraintName("FK_CertPolicyGroupDomainMap_CertPolicyGroups");
            });

            modelBuilder.Entity<CertPolicyGroupMap>(entity =>
            {
                entity.HasKey(e => new { e.CertPolicyGroupId, e.ForOutgoing, e.CertPolicyId, e.PolicyUse, e.ForIncoming });

                entity.ToTable("CertPolicyGroupMap");

                entity.Property(e => e.ForOutgoing).HasDefaultValueSql("((1))");

                entity.Property(e => e.ForIncoming).HasDefaultValueSql("((1))");

                entity.Property(e => e.CreateDate)
                    .HasColumnType("datetime")
                    .HasDefaultValueSql("(getdate())");

                entity.Property(e => e.MapId).ValueGeneratedOnAdd();

                entity.HasOne(d => d.CertPolicyGroup)
                    .WithMany(p => p.CertPolicyGroupMaps)
                    .HasForeignKey(d => d.CertPolicyGroupId)
                    .OnDelete(DeleteBehavior.ClientSetNull)
                    .HasConstraintName("FK_CertPolicyGroupMap_CertPolicyGroups");

                entity.HasOne(d => d.CertPolicy)
                    .WithMany(p => p.CertPolicyGroupMaps)
                    .HasForeignKey(d => d.CertPolicyId)
                    .OnDelete(DeleteBehavior.ClientSetNull)
                    .HasConstraintName("FK_CertPolicyGroupMap_CertPolicies");
            });

            modelBuilder.Entity<Certificate>(entity =>
            {
                entity.HasKey(e => new { e.Owner, e.Thumbprint });

                entity.HasIndex(e => e.CertificateId, "IX_Certificates_CertificateID")
                    .IsUnique();

                entity.Property(e => e.Owner)
                    .HasMaxLength(400)
                    .IsUnicode(false);

                entity.Property(e => e.Thumbprint).HasMaxLength(64);

                entity.Property(e => e.CertificateId)
                    .ValueGeneratedOnAdd()
                    .HasColumnName("CertificateID");

                entity.Property(e => e.CreateDate)
                    .HasColumnType("datetime")
                    .HasDefaultValueSql("(getdate())");

                entity.Property(e => e.ValidEndDate).HasColumnType("datetime");

                entity.Property(e => e.ValidStartDate).HasColumnType("datetime");
            });

            modelBuilder.Entity<DnsRecord>(entity =>
            {
                entity.HasKey(e => e.RecordId);

                entity.HasIndex(e => e.DomainName, "IX_DnsRecords_DomainName");

                entity.Property(e => e.RecordId).HasColumnName("RecordID");

                entity.Property(e => e.CreateDate)
                    .HasColumnType("datetime")
                    .HasDefaultValueSql("(getdate())");

                entity.Property(e => e.DomainName)
                    .HasMaxLength(255)
                    .IsUnicode(false);

                entity.Property(e => e.Notes)
                    .HasMaxLength(500)
                    .IsUnicode(false)
                    .HasDefaultValueSql("('')");

                entity.Property(e => e.TypeId).HasColumnName("TypeID");

                entity.Property(e => e.UpdateDate)
                    .HasColumnType("datetime")
                    .HasDefaultValueSql("(getdate())");
            });

            modelBuilder.Entity<Domain>(entity =>
            {
                entity.HasKey(e => e.DomainName);

                entity.HasIndex(e => e.DomainId, "IX_Domains_DomainID")
                    .IsUnique();

                entity.Property(e => e.DomainName)
                    .HasMaxLength(255)
                    .IsUnicode(false);

                entity.Property(e => e.AgentName)
                    .HasMaxLength(25)
                    .IsUnicode(false);

                entity.Property(e => e.CreateDate)
                    .HasColumnType("datetime")
                    .HasDefaultValueSql("(getdate())");

                entity.Property(e => e.DomainId)
                    .ValueGeneratedOnAdd()
                    .HasColumnName("DomainID");

                entity.Property(e => e.UpdateDate)
                    .HasColumnType("datetime")
                    .HasDefaultValueSql("(getdate())");
            });

            modelBuilder.Entity<Mdn>(entity =>
            {
                entity.HasIndex(e => e.CreateDate, "CreateDate");

                entity.HasIndex(e => e.MdnIdentifier, "IX_Mdns_MdnIdentifier")
                    .IsUnique();

                entity.Property(e => e.CreateDate)
                    .HasColumnType("datetime")
                    .HasDefaultValueSql("(getdate())");

                entity.Property(e => e.MdnIdentifier)
                    .HasMaxLength(32)
                    .IsUnicode(false)
                    .IsFixedLength();

                entity.Property(e => e.MessageId).HasMaxLength(255);

                entity.Property(e => e.RecipientAddress).HasMaxLength(400);

                entity.Property(e => e.SenderAddress).HasMaxLength(400);

                entity.Property(e => e.Status).HasMaxLength(15);

                entity.Property(e => e.Subject).HasMaxLength(998);
            });

            modelBuilder.Entity<Property>(entity =>
            {
                entity.HasKey(e => e.Name);

                entity.HasIndex(e => e.PropertyId, "IX_Properties_ID");

                entity.Property(e => e.Name).HasMaxLength(255);

                entity.Property(e => e.PropertyId)
                    .ValueGeneratedOnAdd()
                    .HasColumnName("PropertyID");

                entity.Property(e => e.Value).HasMaxLength(512);
            });

            OnModelCreatingPartial(modelBuilder);
        }

        partial void OnModelCreatingPartial(ModelBuilder modelBuilder);
    }
}
