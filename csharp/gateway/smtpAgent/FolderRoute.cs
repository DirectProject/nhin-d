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
using System.IO;
using System.Threading;
using Health.Direct.Agent;
using Health.Direct.Common.Diagnostics;
using Health.Direct.Config.Store;
using Health.Direct.Common.Routing;
using Health.Direct.Common.Extensions;
using System.Xml.Serialization;

namespace Health.Direct.SmtpAgent
{        
    /// <summary>
    /// Default Message Route.
    /// - Copies messages to folders
    /// - Uses Round Robin over folders as default
    /// - If a write fails, switches (for that particular episode) to random writes over other folders
    /// 
    /// </summary>
    public class FolderRoute : Route
    {
        Func<ISmtpMessage, string, bool> m_copyHandler;
        FolderBalancer<ISmtpMessage> m_loadBalancer;
                
        public FolderRoute()
        {
            m_copyHandler = this.CopyToFolder;
            m_loadBalancer = new FolderBalancer<ISmtpMessage>(m_copyHandler);
        }

        /// <summary>
        /// Route the messages to one of these folders
        /// If there are multiple, round robin
        /// DO NOT alter this once the system is running
        /// </summary>
        [XmlElement("CopyFolder")]
        public string[] CopyFolders
        {
            get
            {
                return m_loadBalancer.Receivers;
            }
            set
            {
                m_loadBalancer.Receivers = value;
            }
        }

        [XmlIgnore]
        internal bool HasCopyFolders
        {
            get
            {
                return !(this.CopyFolders.IsNullOrEmpty());
            }
        }
        
        [XmlIgnore]
        public Func<ISmtpMessage, string, bool> CopyMessageHandler
        {
            get
            {
                return m_loadBalancer.DataCopier;
            }
            set
            {
                m_loadBalancer.DataCopier = value ?? this.CopyToFolder;
            }
        }
        
        [XmlIgnore]
        public FolderBalancer<ISmtpMessage> LoadBalancer
        {
            get
            {
                return m_loadBalancer;
            }
        }
        
        public override void Validate()
        {
            base.Validate();
            if (!this.HasCopyFolders)
            {
                throw new SmtpAgentException(SmtpAgentError.NoFoldersInRoute);
            }
        }

        public override void Init()
        {
            this.Validate();            
        }

        public override bool Process(ISmtpMessage message)
        {
            return m_loadBalancer.Process(message);
        }   
             
        bool CopyToFolder(ISmtpMessage message, string folderPath)
        {
            try
            {
                string uniqueFileName = Extensions.CreateUniqueFileName();
                message.SaveToFile(Path.Combine(folderPath, uniqueFileName));
                return true;
            }
            catch
            {
            }
            
            return false;
        }

        
    }
}
