/* 
 Copyright (c) 2014, Direct Project
 All rights reserved.

 Authors:
    Joe Shook     Joseph.Shook@Surescripts.com
  
Redistribution and use in source and binary forms, with or without modification, are permitted provided that the following conditions are met:

Redistributions of source code must retain the above copyright notice, this list of conditions and the following disclaimer.
Redistributions in binary form must reproduce the above copyright notice, this list of conditions and the following disclaimer in the documentation and/or other materials provided with the distribution.
Neither the name of The Direct Project (directproject.org) nor the names of its contributors may be used to endorse or promote products derived from this software without specific prior written permission.
THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 
*/


using System;
using System.Collections.Generic;
using System.Collections.ObjectModel;
using System.Collections.Specialized;
using System.Linq;
using Health.Direct.Common.Extensions;
using Health.Direct.Policy.Impl;

namespace Health.Direct.Config.Store.Entity
{
    public class CertPolicy
    {
        public const int MaxNameLength = 255;
        public const int MaxDescriptionLength = 255;

        string m_Name;
        string m_Description = String.Empty;
        
        public virtual ICollection<CertPolicyGroupMap> CertPolicyGroupMaps { get; set; }

        public CertPolicy()
        {
            CertPolicyGroupMaps = new HashSet<CertPolicyGroupMap>();
            CreateDate = DateTimeHelper.Now;
        }
        
        public CertPolicy(string name) : this()
        {
            Name = name;
            Lexicon = "SimpleText";
        }

        public CertPolicy(string name, byte[] data)
            : this()
        {
            Name = name;
            Lexicon = "SimpleText"; 
            Data = data;
        }

        public CertPolicy(string name, string description, byte[] data)
            : this()
        {
            Name = name;
            Description = description;
            Lexicon = "SimpleText";
            Data = data;
        }

        public CertPolicy(CertPolicy policy) : this()
        {
            ID = policy.ID;
            Name = policy.Name;
            Description = policy.Description;
            Lexicon = policy.Lexicon;
            Data = policy.Data;
        }
        
        public long ID 
        { 
            get; 
            set; 
        }

        public ICollection<CertPolicyGroupMap> CertPolicyGroupMap
        {
            get;
            set;
        }

        
        public string Name
        {
            get
            {
                return m_Name;
            }
            set
            {
                if (string.IsNullOrEmpty(value))
                {
                    throw new ConfigStoreException(ConfigStoreError.InvalidCertPolicyName);
                }

                if (value.Length > MaxNameLength)
                {
                    throw new ConfigStoreException(ConfigStoreError.CertPolicyNameLength);
                }

                m_Name = value;
            }
        }

        public String Description
        {
            get
            {
                return m_Description;
            }
            set
            {
                if (value.Length > MaxDescriptionLength)
                {
                    throw new ConfigStoreException(ConfigStoreError.CertPolicyGroupDescriptionLength);
                }

                m_Description = value;
            }
        }

        /// <summary>
        /// For now this will always be the <see cref="SimpleTextV1LexiconPolicyParser"/>.
        /// 
        /// </summary>
        public string Lexicon { 
            get;
            set;
        }

        public byte[] Data
        {
            get; 
            set;
        }


        public DateTime CreateDate
        {
            get;
            set;
        }
        
        
        public bool HasData
        {
            get
            {
                return (Data != null && Data.Length > 0);
            }
        }

        public void ValidateHasData()
        {
            if (Data.IsNullOrEmpty())
            {
                throw new ConfigStoreException(ConfigStoreError.MissingCertPolicyData);
            }
        }

        internal void CopyFixed(CertPolicy source)
        {
            this.ID = source.ID;
            this.CreateDate = source.CreateDate;
            this.Name = source.Name;
        }

        internal void ApplyChanges(CertPolicy source)
        {
            this.Description = source.Description;
            this.Data = source.Data;
            this.Lexicon = source.Lexicon;
        }

        public bool IsNew()
        {
            return ID <= 0;
        }
    }
}
