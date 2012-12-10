/* 
 Copyright (c) 2010, Direct Project
 All rights reserved.

 Authors:
    Umesh Madan     umeshma@microsoft.com
  
Redistribution and use in source and binary forms, with or without modification, are permitted provided that the following conditions are met:

Redistributions of source code must retain the above copyright notice, this list of conditions and the following disclaimer.
Redistributions in binary form must reproduce the above copyright notice, this list of conditions and the following disclaimer in the documentation and/or other materials provided with the distribution.
Neither the name of The Direct Project (directproject.org) nor the names of its contributors may be used to endorse or promote products derived from this software without specific prior written permission.
THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 
*/
using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Data.Linq.Mapping;
using System.Runtime.Serialization;
using System.Xml.Serialization;
using Health.Direct.Common.Extensions;

namespace Health.Direct.Config.Store
{
    [Table(Name = "Blobs")]
    [DataContract(Namespace = ConfigStore.Namespace)]
    public class NamedBlob
    {
        public const int MaxNameLength = 255;
        public const string DefaultContentType = "application/octet-stream";
        
        string m_name;
        byte[] m_data;
        
        public NamedBlob()
        {
            this.CreateDate = DateTimeHelper.Now;
            this.UpdateDate = this.CreateDate;
        }

        public NamedBlob(string name, byte[] data)
            : this()
        {
            this.Name = name;
            this.Data = data;
        }

        public NamedBlob(string name, object data)
            : this()
        {
            this.Name = name;
            this.SetObject(data);
        }

        [Column(Name = "Name", IsPrimaryKey=true, CanBeNull = false, UpdateCheck = UpdateCheck.Never)]
        [DataMember(IsRequired = true)]
        public string Name
        {
            get
            {
                return m_name;
            }
            set
            {
                if (string.IsNullOrEmpty(value))
                {
                    throw new ConfigStoreException(ConfigStoreError.InvalidTextBlobName);
                }
                if (value.Length > MaxNameLength)
                {
                    throw new ConfigStoreException(ConfigStoreError.InvalidTextBlobNameLength);
                }

                m_name = value;
            }
        }

        [Column(Name = "Data", DbType = "varbinary(MAX)", CanBeNull = false, UpdateCheck = UpdateCheck.Never)]
        [DataMember(IsRequired = true)]
        public byte[] Data
        {
            get
            {
                return m_data;
            }
            set
            {
                m_data = value;
            }
        }

        [Column(Name = "CreateDate", CanBeNull = false, UpdateCheck = UpdateCheck.Never)]
        [DataMember(IsRequired = true)]
        public DateTime CreateDate
        {
            get;
            set;
        }

        [Column(Name = "UpdateDate", CanBeNull = false, UpdateCheck = UpdateCheck.Never)]
        [DataMember(IsRequired = true)]
        public DateTime UpdateDate
        {
            get;
            set;
        }
        
        public void SetObject(object item)
        {
            XmlSerializer serializer = new XmlSerializer(item.GetType());
            this.Data = serializer.ToBytes(item);            
        }
        
        public T GetObject<T>()
        {
            XmlSerializer serializer = new XmlSerializer(typeof(T));
            return (T) serializer.FromBytes(this.Data);
        }
        
        internal void CopyFixed(NamedBlob source)
        {
            this.Name = source.Name;
            this.CreateDate = source.CreateDate;
            this.UpdateDate = source.UpdateDate;
            this.Data = null;
        }
        
        /// <summary>
        /// Only copy those fields that are allowed to change in updates
        /// </summary>
        internal void ApplyChanges(NamedBlob source)
        {
            this.Data = source.Data;
            this.UpdateDate = DateTimeHelper.Now;
        }
    }
}
