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

using System.Runtime.CompilerServices;


using System.Net.Mail;

[assembly: InternalsVisibleTo("Health.Direct.Config.Store.Tests, PublicKey=0024000004800000940000000602000000240000525341310004000001000100fb5b9e8fddddc7d2f20a678a850155a6e05eccc2986d5997ef994844b4827febeb053a2c19628bf648365038daa3168d9e12605c33e1164f31acad8526973432c8904d3dd15ecfab461f4f614a03a24bd29358666a6d710c88afdc997c1410a191f4a1733b8cd7193ac366e3e269df27c821b0ba485ce4b96366651be5cd2bcb")]

namespace Health.Direct.Config.Store.Entity
{
    public class Address
    {
        public const int MaxAddressLength = 400;
        public const int MaxDisplayNameLength = 64;
        
        string m_address;
        string m_displayName;
        
        public Address()
        {
            this.CreateDate = DateTimeHelper.Now;
            this.UpdateDate = this.CreateDate;
            this.Status = EntityStatus.New;
        }
        
        public Address(long domainID, string address)
            : this(domainID, address, string.Empty)
        {
        }
        
        public Address(long domainID, string address, string displayName) 
            : this()
        {
            this.DomainID = domainID;
            this.EmailAddress = address;
            this.DisplayName = displayName;
        }
        
        public Address(long domainID, MailAddress address)
            : this(domainID, address.Address, address.DisplayName)
        {
        }

        public string EmailAddress
        {
            get
            {
                return m_address;
            }
            set
            {
                if (string.IsNullOrEmpty(value) || value.Length > MaxAddressLength)
                {
                    throw new ConfigStoreException(ConfigStoreError.AddressLength);
                }

                m_address = value;
            }
        }

        public long ID
        {
            get;
            set;
        }
        
        public long DomainID
        {
            get;
            set;
        }
        
        public string DisplayName
        {
            get
            {
                return m_displayName;
            }
            set
            {
                if (value != null && value.Length > MaxDisplayNameLength)
                {
                    throw new ConfigStoreException(ConfigStoreError.DisplayNameLength);
                }
                
                m_displayName = value ?? string.Empty;
            }
        }

        public DateTime CreateDate
        {
            get;
            set;
        }

        public DateTime UpdateDate
        {
            get;
            set;
        }

        public EntityStatus Status
        {
            get;
            set;
        }
        
        public string? Type
        {
            get;
            set;
        }
        
        public bool HasType
        {
            get
            {
                return (!string.IsNullOrEmpty(this.Type));
            }            
        }
        
        public bool HasDisplayName
        {
            get
            {
                return (!string.IsNullOrEmpty(this.DisplayName));
            }
        }

        public Domain Domain { get; set; }

        public bool IsValidMailAddress()
        {
            try
            {
                return (this.ToMailAddress() != null);
            }
            catch
            {
            }
            
            return false;
        }
        
        public MailAddress ToMailAddress()
        {
            if (this.HasDisplayName)
            {
                return new MailAddress(this.EmailAddress, this.DisplayName);
            }
            
            return new MailAddress(this.EmailAddress);
        }
       
        
        internal void CopyFixed(Address source)
        {
            this.EmailAddress = source.EmailAddress;
            this.ID = source.ID;
            this.DomainID = source.DomainID;
            this.CreateDate = source.CreateDate;
            this.UpdateDate = source.UpdateDate;
        }        
        /// <summary>
        /// Only copy those fields that are allowed to change in updates
        /// </summary>
        internal void ApplyChanges(Address source)
        {
            if (source == null)
            {
                throw new ArgumentNullException("source");
            }
            this.DisplayName = source.DisplayName;
            this.Status = source.Status;
            this.Type = source.Type;
            this.UpdateDate = DateTimeHelper.Now;
        }
    }
}