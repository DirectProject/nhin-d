/* 
 Copyright (c) 2010, NHIN Direct Project
 All rights reserved.

 Authors:
    Umesh Madan     umeshma@microsoft.com
    Sean Nolan      seannol@microsoft.com
 
Redistribution and use in source and binary forms, with or without modification, are permitted provided that the following conditions are met:

Redistributions of source code must retain the above copyright notice, this list of conditions and the following disclaimer.
Redistributions in binary form must reproduce the above copyright notice, this list of conditions and the following disclaimer in the documentation and/or other materials provided with the distribution.
Neither the name of the The NHIN Direct Project (nhindirect.org). nor the names of its contributors may be used to endorse or promote products derived from this software without specific prior written permission.
THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 
*/
using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.IO;

namespace DnsResolver
{
    public class DnsRecordPrinter
    {
        TextWriter m_writer;
        
        public DnsRecordPrinter(TextWriter writer)
        {
            if (writer == null)
            {
                throw new ArgumentNullException();
            }
            
            this.m_writer = writer;
        }
        
        public void Print(DnsResponse response)
        {
            if (response == null)
            {
                return;
            }
            
            if (response.IsNameError)
            {
				this.Print("Is Name Error");
                return;
            }
            
            if (!response.IsSuccess)
            {
				this.Print("Failed");
                return;
            }
            
            if (response.HasAnswerRecords)
            {
				this.Print("***ANSWERS***");
                this.Print(response.AnswerRecords);
            }
            else
            {
				this.Print("No answers");
            }              
            
            if (response.HasNameServerRecords)
            {
				this.Print("***NAME SERVERS***");
                this.Print(response.NameServerRecords);
            }
            
            if (response.HasAdditionalRecords)
            {
				this.Print("***Additional***");
                this.Print(response.AdditionalRecords);
            }
        }
        
        public void Print(DnsResourceRecordCollection records)
        {
            if (records == null || records.Count == 0)
            {
                m_writer.WriteLine("Empty record list");
                return;
            }

            for (int i = 0; i < records.Count; ++i)
            {
                this.Print(records[i]);
            }
        }
        
        public void Print(DnsResourceRecord[] records)
        {
            if (records == null || records.Length == 0)
            {
                m_writer.WriteLine("Empty record list");
                return;
            }

            for (int i = 0; i < records.Length; ++i)
            {
                this.Print(records[i]);
            }
        }

        public void Print(DnsResourceRecord record)
        {
            if (record == null)
            {
                m_writer.WriteLine("Null Resource Record");
                return;
            }
            
            m_writer.WriteLine("-----------");
            this.Print("Type", record.Type.ToString());
            this.Print("Name", record.Name);
            this.Print<int>("TTL", record.TTL);
            switch(record.Type)
            {
                default:
                    break;
                
                case Dns.RecordType.ANAME:
                    this.Print((AddressRecord) record);
                    break;

                case Dns.RecordType.NS:
                    this.Print((NSRecord)record);
                    break;
                
                case Dns.RecordType.CNAME:
                    this.Print((CNameRecord) record);
                    break;
                    
                case Dns.RecordType.SOA:
                    this.Print((SOARecord) record);
                    break;
                        
                case Dns.RecordType.MX:
                    this.Print((MXRecord) record);
                    break;               
                
                case Dns.RecordType.PTR:
                    this.Print((PtrRecord) record);
                    break;
                    
                case Dns.RecordType.TXT:
                    this.Print((TextRecord) record);
                    break;      
                
                case Dns.RecordType.CERT:
                    this.Print((CertRecord) record);                                       
                    break;
            }
        }
        
        public void Print(AddressRecord body)
        {
            if (body == null)
            {
                this.Print("Null A Record Body");
                return;
            }

            this.Print("IPAddress", body.IPAddress.ToString());
        }
        
        public void Print(MXRecord body)
        {
            if (body == null)
            {
                this.Print("Null MX Record Body");
                return;
            }
            
            this.Print("Exchange", body.Exchange);
            this.Print("Preferrence", body.Preference);
        }
        
        public void Print(TextRecord body)
        {
            if (body == null)
            {
                m_writer.WriteLine("Null TXT Record Body");
                return;
            }
            
            if (!body.HasStrings)
            {
                m_writer.WriteLine("Empty TXT Record Body");
                return;
            }
            
            foreach(string sz in body.Strings)
            {
                if (sz == null)
                {
                    m_writer.WriteLine("Null string");
                }
                
                m_writer.WriteLine(sz);
            }
        }
        
        public void Print(CNameRecord cname)
        {
			this.Print(cname.CName);
        }
        
        public void Print(SOARecord soa)
        {
			this.Print(soa.DomainName);
        }
        
        public void Print(CertRecord cert)
        {
            if (cert.Cert != null)
            {
				this.Print(cert.Cert.Certificate.Subject);
            }
        }
        
        public void Print(NSRecord ns)
        {
			this.Print(ns.NameServer);
        }
        
        void Print(PtrRecord ptr)
        {
            this.Print(ptr.Domain);
        }
        
        void Print<T>(string name, T value)
        {
            this.Print(name, value.ToString());
        }

        void Print(string name, short value)
        {
            this.Print(name, value.ToString());
        }

        void Print(string name, int value)
        {
            this.Print(name, value.ToString());
        }
        
        void Print(string name, string value)
        {
            m_writer.WriteLine("{0}={1}", name, value);
        }

		void Print(string message)
		{
			m_writer.WriteLine(message);
		}
    }
}
