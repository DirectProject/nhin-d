/* 
 Copyright (c) 2010, NHIN Direct Project
 All rights reserved.

 Authors:
    Chris Lomonico chris.lomonico@surescripts.com
  
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
using System.Configuration;
using DnsResolver;

namespace NHINDirect.Caching
{
    public class DnsResponseCache : CachingBase<DnsResponse>
    {

        protected override TimeSpan Ttl
        {
            get
            {
                //----------------------------------------------------------------------------------------------------
                //---default to 20 seconds for global expiration
                return new TimeSpan(0, 0, 20);
            }
        }

        public DnsResponseCache()
        {

        }        

        public virtual string BuildKey(DnsQuestion question)
        {
            if (question == null)
            {
                throw new Exception("Empty DnsQuestion used as key");
            }
            return string.Format("{0}.{1}"
                    , question.QType.ToString()
                    , question.QName ?? "unknown").ToLower();
        }
        
        public void Put(DnsResponse dr
            , TimeSpan ttl)
        {
            base.Put(this.BuildKey(dr.Question)
                , dr
                , ttl);
        }

        public void Put(DnsResponse dr)
        {
            //----------------------------------------------------------------------------------------------------
            //---no sense in storing nothing
            if (dr == null || dr.AnswerRecords.Count() == 0)
            {
                return;
            }

            //----------------------------------------------------------------------------------------------------
            //---get the minimum ttl from the records (Int32 for the total seconds)
            var val = dr.AnswerRecords.Select(r => r.TTL).Min();
            TimeSpan ts = new TimeSpan(0, 0, val);

            //----------------------------------------------------------------------------------------------------
            //---store the record in the cache
            this.Put(dr
                , ts);
        }

        public DnsResponse Get(DnsRequest drq)
        {
            return base.Get(this.BuildKey(drq.Question));
        }

        public DnsResponse Get(DnsResponse drs)
        {
            return base.Get(this.BuildKey(drs.Question));
        }

        public DnsResponse Get(DnsQuestion dq)
        {
            return base.Get(this.BuildKey(dq));
        }


        public void Remove(DnsRequest drq)
        {
            this.Remove(drq.Question);
        }

        public void Remove(DnsResponse drs)
        {
            this.Remove(drs.Question);
        }

        public void Remove(DnsQuestion drq)
        {
            base.Remove(this.BuildKey(drq));
        }

    }
}
