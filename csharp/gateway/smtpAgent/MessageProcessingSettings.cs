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
using System.Xml.Serialization;
using System.IO;

namespace Health.Direct.SmtpAgent
{
    public class MessageProcessingSettings
    {
        /// <summary>
        /// For debugging/diagnostics etc - vital especially as we develop and try to debug
        // If a CopyFolder is defined, a copy of the message is saved in the folder
        /// </summary>
        [XmlElement("CopyFolder")]
        public string CopyFolder
        {
            get;
            set;
        }

        [XmlIgnore]
        internal bool HasCopyFolder
        {
            get
            {
                return !(string.IsNullOrEmpty(this.CopyFolder));
            }
        }
        
        public virtual void Validate()
        {
        }
        
        internal virtual void EnsureFolders()
        {
            if (this.HasCopyFolder && !Directory.Exists(this.CopyFolder))
            {
                Directory.CreateDirectory(this.CopyFolder);
            }
        }

        public override string ToString()
        {
            return "CopyFolder=" + CopyFolder;
        }
    }

    [XmlType("RawMessage")]
    public class RawMessageSettings : MessageProcessingSettings
    {
        public RawMessageSettings()
            : base()
        {
        }
    }

    [XmlType("ProcessIncoming")]
    public class ProcessIncomingSettings : MessageProcessingSettings
    {
        bool m_enableRelay = true;

        public ProcessIncomingSettings()
            : base()
        {
        }

        /// <summary>
        /// Diagnostic/development tool. When false, the processed incoming message is never put on the
        /// wire. If a CopyFolder is available, the message is copied into it. 
        /// </summary>        
        [XmlElement("EnableRelay")]
        public bool EnableRelay
        {
            get
            {
                return m_enableRelay;
            }
            set
            {
                m_enableRelay = value;
            }
        }

        public override string ToString()
        {
            return base.ToString() + " EnableRelay=" + EnableRelay;
        }
    }

    [XmlType("ProcessOutgoing")]
    public class ProcessOutgoingSettings : MessageProcessingSettings
    {
        bool m_enableRelay = true;
        
        public ProcessOutgoingSettings()
            : base()
        {
        }
        
        /// <summary>
        /// Diagnostic/development tool. When false, the outgoing message is never put on the
        /// wire. If a CopyFolder is available, the message is copied into it. 
        /// </summary>        
        [XmlElement("EnableRelay")]
        public bool EnableRelay
        {
            get
            {
                return m_enableRelay;
            }
            set
            {
                m_enableRelay = value;
            }
        }

        public override string ToString()
        {
            return base.ToString() + " EnableRelay=" + EnableRelay;
        }
    }

    [XmlType("ProcessBadMessage")]
    public class ProcessBadMessageSettings : MessageProcessingSettings
    {
        public ProcessBadMessageSettings()
            : base()
        {
        }
    }
}