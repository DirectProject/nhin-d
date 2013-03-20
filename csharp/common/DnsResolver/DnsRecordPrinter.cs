/* 
 Copyright (c) 2010, Direct Project
 All rights reserved.

 Authors:
    Umesh Madan     umeshma@microsoft.com
    Sean Nolan      seannol@microsoft.com
 
Redistribution and use in source and binary forms, with or without modification, are permitted provided that the following conditions are met:

Redistributions of source code must retain the above copyright notice, this list of conditions and the following disclaimer.
Redistributions in binary form must reproduce the above copyright notice, this list of conditions and the following disclaimer in the documentation and/or other materials provided with the distribution.
Neither the name of The Direct Project (directproject.org) nor the names of its contributors may be used to endorse or promote products derived from this software without specific prior written permission.
THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 
*/
using System;
using System.IO;

namespace Health.Direct.Common.DnsResolver
{
    /// <summary>
    /// Prints a textual representation of DNS transactions (request, response, records, etc.)
    /// </summary>
    public class DnsRecordPrinter
    {
        TextWriter m_writer;
        
        /// <summary>
        /// Initializes the printer with <paramref name="writer"/>
        /// </summary>
        /// <param name="writer">The <see cref="TextWriter"/> used for output</param>
        public DnsRecordPrinter(TextWriter writer)
        {
            if (writer == null)
            {
                throw new ArgumentNullException("writer");
            }
            
            this.m_writer = writer;
        }
        
        /// <summary>
        /// Prints a Dns Request
        /// </summary>
        /// <param name="request">the request to print</param>
        public void Print(DnsRequest request)
        {
            if (request == null)
            {
                return;
            }
            
            this.Print("RequestID", request.RequestID);
            this.Print(request.Question);
        }
        
        /// <summary>
        /// Prints a DNS Question.
        /// </summary>
        /// <param name="question">The question to print</param>
        public void Print(DnsQuestion question)
        {
            if (question == null)
            {
                return;
            }
            
            this.Print("***QUESTION***");
            this.Print("Domain", question.Domain);
            this.Print("Type", question.Type);
            this.Print("Class", question.Class);
        }
                
        /// <summary>
        /// Prints a DNS response.
        /// </summary>
        /// <param name="response">The response to print</param>
        public void Print(DnsResponse response)
        {
            if (response == null)
            {
                return;
            }

            this.Print("RequestID", response.RequestID);
            this.Print("Response Code", response.Header.ResponseCode);
            if (!response.IsSuccess)
            {
                return;
            }
            
            this.Print(response.Question);
            
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
        
        /// <summary>
        /// Prints a collection of DNS RRs
        /// </summary>
        /// <param name="records">The RRs to print</param>
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
        
        /// <summary>
        /// Prints an array of RRs.
        /// </summary>
        /// <param name="records">The RRs to print</param>
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

        /// <summary>
        /// Prints an RR
        /// </summary>
        /// <param name="record">The RR to print</param>
        public void Print(DnsResourceRecord record)
        {
            if (record == null)
            {
                m_writer.WriteLine("Null Resource Record");
                return;
            }
            
            m_writer.WriteLine("-----------");
            this.Print("Type", record.Type.ToString());
            this.Print("DomainName", record.Name);
            this.Print<int>("TTL", record.TTL);
            switch(record.Type)
            {
                default:
                    break;
                
                case DnsStandard.RecordType.ANAME:
                    this.Print((AddressRecord) record);
                    break;

                case DnsStandard.RecordType.NS:
                    this.Print((NSRecord)record);
                    break;
                
                case DnsStandard.RecordType.CNAME:
                    this.Print((CNameRecord) record);
                    break;
                    
                case DnsStandard.RecordType.SOA:
                    this.Print((SOARecord) record);
                    break;
                        
                case DnsStandard.RecordType.MX:
                    this.Print((MXRecord) record);
                    break;               
                
                case DnsStandard.RecordType.PTR:
                    this.Print((PtrRecord) record);
                    break;
                    
                case DnsStandard.RecordType.TXT:
                    this.Print((TextRecord) record);
                    break;      
                
                case DnsStandard.RecordType.CERT:
                    this.Print((CertRecord) record);                                       
                    break;
                
                case DnsStandard.RecordType.SRV:
                    this.Print((SRVRecord) record);
                    break;
            }
        }
        
        /// <summary>
        /// Prints an A RR
        /// </summary>
        /// <param name="body">The RR to print</param>
        public void Print(AddressRecord body)
        {
            if (body == null)
            {
                this.Print("Null A Record Body");
                return;
            }

            this.Print("IPAddress", body.IPAddress.ToString());
        }

        /// <summary>
        /// Prints an MX RR
        /// </summary>
        /// <param name="body">The RR to print</param>
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

        /// <summary>
        /// Prints a TXT RR
        /// </summary>
        /// <param name="body">The RR to print</param>
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
        
        /// <summary>
        /// Prints a CNAME RR
        /// </summary>
        /// <param name="cname">The RR to print</param>
        public void Print(CNameRecord cname)
        {
            this.Print("CName", cname.CName);
        }
        
        /// <summary>
        /// Prints a SOA RR
        /// </summary>
        /// <param name="soa">The RR to print</param>
        public void Print(SOARecord soa)
        {
            this.Print("DomainName", soa.Name);
            this.Print("PrimarySourceDomain", soa.DomainName);
            this.Print("Refresh", soa.Refresh);
            this.Print("Retry", soa.Retry);
            this.Print("Expire", soa.Expire);
            this.Print("Minimum", soa.Minimum);
        }
        
        /// <summary>
        /// Prints a CERT RR
        /// </summary>
        /// <param name="cert">The RR to print</param>
        public void Print(CertRecord cert)
        {
            if (cert.Cert != null)
            {
                this.Print(cert.Cert.Certificate.Subject);
            }
        }
        
        /// <summary>
        /// Prints an NS RR
        /// </summary>
        /// <param name="ns">The RR to print</param>
        public void Print(NSRecord ns)
        {
            this.Print(ns.NameServer);
        }
        
        /// <summary>
        /// Prints a PTR RR
        /// </summary>
        /// <param name="ptr">The RR to print</param>
        void Print(PtrRecord ptr)
        {
            this.Print(ptr.Domain);
        }

        /// <summary>
        /// Prints an SRV RR
        /// </summary>
        /// <param name="srv">the RR to print</param>
        public void Print(SRVRecord srv)
        {
            this.Print("Priority", srv.Priority);
            this.Print("Weight", srv.Weight);
            this.Print("Port", srv.Port);
            this.Print("Target", srv.Target);
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
            m_writer.WriteLine("{0} = {1}", name, value);
        }

        void Print(string name, ushort value)
        {
            this.Print(name, value.ToString());
        }

        void Print(string message)
        {
            m_writer.WriteLine(message);
        }
    }
}