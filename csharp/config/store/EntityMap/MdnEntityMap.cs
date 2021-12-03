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

namespace Health.Direct.Config.Store.EntityMap;

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