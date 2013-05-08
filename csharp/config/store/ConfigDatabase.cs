/* 
 Copyright (c) 2010, Direct Project
 All rights reserved.

 Authors:
    Umesh Madan     umeshma@microsoft.com
    Joe Shook       jshook@kryptiq.com
  
Redistribution and use in source and binary forms, with or without modification, are permitted provided that the following conditions are met:

Redistributions of source code must retain the above copyright notice, this list of conditions and the following disclaimer.
Redistributions in binary form must reproduce the above copyright notice, this list of conditions and the following disclaimer in the documentation and/or other materials provided with the distribution.
Neither the name of The Direct Project (directproject.org) nor the names of its contributors may be used to endorse or promote products derived from this software without specific prior written permission.
THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 
*/
using System;
using System.Data;
using System.Data.Common;
using System.Data.Linq;
using System.Data.Linq.Mapping;
using Health.Direct.Common.Mail.Notifications;

namespace Health.Direct.Config.Store
{
    [Database(Name="DirectConfig")]
    public class ConfigDatabase : DataContext
    {
        static MappingSource s_mappingSource = new AttributeMappingSource();
        
        Table<Certificate> m_certs;
        Table<Administrator> m_administrators;
        Table<Anchor> m_anchors;
        Table<Domain> m_domains;
        Table<Address> m_addresses;
        Table<DnsRecord> m_dnsRecords;
        Table<Property> m_properties;
        Table<NamedBlob> m_blobs;
        Table<Mdn> m_mdns;
        Table<Bundle> m_bundles;
        
        DbTransaction m_transaction;
                          
        public ConfigDatabase(string connectString)
            : base(connectString, s_mappingSource)
        {
        }

        public Table<Address> Addresses
        {
            get
            {
                if (m_addresses == null)
                {
                    m_addresses = this.GetTable<Address>();
                }

                return m_addresses;
            }
        }

        public Table<Administrator> Administrators
        {
            get
            {
                if (m_administrators == null)
                {
                    m_administrators = this.GetTable<Administrator>();
                }

                return m_administrators;
            }
        }

        public Table<Anchor> Anchors
        {
            get
            {
                if (m_anchors == null)
                {
                    m_anchors = this.GetTable<Anchor>();
                }

                return m_anchors;
            }
        }
        public Table<Certificate> Certificates
        {
            get
            {
                if (m_certs == null)
                {
                    m_certs = this.GetTable<Certificate>();
                }

                return m_certs;
            }
        }

        public Table<DnsRecord> DnsRecords
        {
            get
            {
                if (m_dnsRecords == null)
                {
                    m_dnsRecords = this.GetTable<DnsRecord>();
                }

                return m_dnsRecords;
            }
        }

        public Table<Domain> Domains
        {
            get
            {
                if (m_domains == null)
                {
                    m_domains = this.GetTable<Domain>();
                }

                return m_domains;
            }
        }
        
        public Table<Property> Properties
        {
            get
            {
                if (m_properties == null)
                {
                    m_properties = this.GetTable<Property>();
                }

                return m_properties;
            }
        }

        public Table<NamedBlob> Blobs
        {
            get
            {
                if (m_blobs == null)
                {
                    m_blobs = this.GetTable<NamedBlob>();
                }

                return m_blobs;
            }
        }

        public Table<Bundle> Bundles
        {
            get
            {
                if (m_bundles == null)
                {
                    m_bundles = this.GetTable<Bundle>();
                }

                return m_bundles;
            }
        }

        public Table<Mdn> Mdns
        {
            get
            {
                if (m_mdns == null)
                {
                    m_mdns = this.GetTable<Mdn>();
                }

                return m_mdns;
            }
        }

        public void BeginTransaction()
        {
            if (this.Connection == null || this.Connection.State == ConnectionState.Closed)
            {
                this.Connection.Open();
            }

            m_transaction = this.Connection.BeginTransaction();
            this.Transaction = m_transaction;
        }
        
        public void Commit()
        {
            if (m_transaction != null)
            {
                m_transaction.Commit();
                m_transaction = null;
                this.Transaction = null;
            }
        }
        
        public void Rollback()
        {
            this.Rollback(false);
        }
        
        public void Rollback(bool disposing)
        {
            if (m_transaction != null)
            {
                try
                {
                    m_transaction.Rollback();
                }
                catch
                {
                }
                m_transaction = null;
                if (!disposing)
                {
                    this.Transaction = null;
                }
            }
        }
        
        protected override void Dispose(bool disposing)
        {
            this.Rollback(disposing);         
            base.Dispose(disposing);
        }
    }
}