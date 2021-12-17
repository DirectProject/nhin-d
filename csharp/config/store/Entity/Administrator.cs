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

using System;
using System.ComponentModel.DataAnnotations.Schema;
using System.Runtime.Serialization;

namespace Health.Direct.Config.Store.Entity
{
    public class Administrator
    {
        public Administrator()
        {
            this.CreateDate = DateTimeHelper.Now;
            this.UpdateDate = this.CreateDate;
            this.Status = EntityStatus.New;
        }

        public Administrator(string username, string password)
            : this()
        {
            Username = username;
            PasswordHash = new PasswordHash(this, password);
        }

        public Administrator(Administrator that)
        {
            this.CreateDate = that.CreateDate;
            this.ID = that.ID;
            this.PasswordHash = that.PasswordHash;
            this.Status = that.Status;
            this.UpdateDate = that.UpdateDate;
            this.Username = that.Username;
        }

        public void UpdateFrom(Administrator that)
        {
            this.PasswordHash = that.PasswordHash;
            this.Status = that.Status;
            this.UpdateDate = DateTimeHelper.Now;
        }

        public void SetPassword(string password)
        {
            PasswordHash = new PasswordHash(this, password);
        }

        public bool CheckPassword(string password)
        {
            if (string.IsNullOrEmpty(password))
            {
                throw new ConfigStoreException(ConfigStoreError.InvalidPassword);
            }

            return PasswordHash == new PasswordHash(this, password);
        }

        public long ID { get; set; }

        public string Username { get; set; }

        internal string PasswordHashDB { get; set; }

        public PasswordHash PasswordHash
        {
            get
            {
                return new PasswordHash(PasswordHashDB);
            }
            set
            {
                PasswordHashDB = value != null ? value.HashedPassword : null;
            }
        }

        
        public DateTime CreateDate { get; set; }

        
        public DateTime UpdateDate { get; set; }

        
        public EntityStatus Status { get; set; }
    }
}