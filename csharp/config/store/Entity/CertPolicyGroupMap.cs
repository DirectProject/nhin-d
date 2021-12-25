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

namespace Health.Direct.Config.Store.Entity
{

    public class CertPolicyGroupMap
    {
        bool m_new;

        public CertPolicyGroupMap()
        {
            CreateDate = DateTimeHelper.Now;
            PolicyUse = CertPolicyUse.NONE;
        }

        public CertPolicyGroupMap(bool isNew) : this()
        {
            m_new = isNew;
        }

        public CertPolicyGroupMap(CertPolicyUse use, bool forIncoming, bool forOutgoing): this()
        {
            m_new = true;
            PolicyUse = use;
            ForIncoming = forIncoming;
            ForOutgoing = forOutgoing;
        }

        public long ID
        {
            get;
            set;
        }

        public long CertPolicyGroupId { get; set; }
        public long CertPolicyId { get; set; }

        public CertPolicyGroup CertPolicyGroup { get; set; }


        public CertPolicy CertPolicy { get; set; }


        public CertPolicyUse PolicyUse
        {
            get;
            set;
        }

        
        public bool ForIncoming
        {
            get;
            set;
        }

        
        public bool ForOutgoing
        {
            get;
            set;
        }

        
        public DateTime CreateDate
        {
            get;
            set;
        }

        public bool IsNew
        {
            get { return m_new; }
        }
        public void Remove( ) {
            //DirectDbContext.RemoveAssociativeRecord(this);

            CertPolicy originalCertPolicy = CertPolicy;
            originalCertPolicy.CertPolicyGroupMaps.Remove(this);

            CertPolicyGroup originalCertPolicyGroup = CertPolicyGroup;
            originalCertPolicyGroup.CertPolicyGroupMaps.Remove(this);
        }
    }
}
