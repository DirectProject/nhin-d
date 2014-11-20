/* 
 Copyright (c) 2014, Direct Project
 All rights reserved.

 Authors:
    Joe Shook      Joseph.Shook@Surescripts.com
  
Redistribution and use in source and binary forms, with or without modification, are permitted provided that the following conditions are met:

Redistributions of source code must retain the above copyright notice, this list of conditions and the following disclaimer.
Redistributions in binary form must reproduce the above copyright notice, this list of conditions and the following disclaimer in the documentation and/or other materials provided with the distribution.
Neither the name of The Direct Project (directproject.org) nor the names of its contributors may be used to endorse or promote products derived from this software without specific prior written permission.
THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 
*/


using System;
using System.IO;
using Health.Direct.Common.Diagnostics;
using Health.Direct.SmtpAgent;
using Health.Direct.SmtpAgent.Diagnostics;

namespace Health.Direct.DatabaseAuditor
{
    public class Auditor<T> : IAuditor<IBuildAuditLogMessage> where T : IBuildAuditLogMessage, new ()
    {
        readonly AuditorSettings m_settings;
        
        public Auditor()
        {
            string location = HostLocation();
            m_settings = AuditorSettings.Load(location);
            BuildAuditLogMessage = new T();
        }

        public void Log(string category)
        {
            using (var db = new AuditContext().CreateContext(m_settings))
            {
                AuditEvent auditEvent = new AuditEvent(category);
                db.AuditEvents.Add(auditEvent);
                db.SaveChanges();
            }
        }

        
        public void Log(string category, string message)
        {
            using (var db = new AuditContext().CreateContext(m_settings))
            {
                AuditEvent auditEvent = new AuditEvent(category, message);
                db.AuditEvents.Add(auditEvent);
                db.SaveChanges();
            }
        }

        public IBuildAuditLogMessage BuildAuditLogMessage { get; private set; }

        private static string HostLocation()
        {
            string programData = Environment.GetFolderPath(Environment.SpecialFolder.CommonApplicationData);
            string myDataPath = Path.Combine(programData, Path.Combine(@"DirectProject\auditors", AuditorSettings.ConfigFile));
            EventLogHelper.WriteInformation("Health.Direct.Audit", myDataPath);
            return myDataPath;
        }
    }

    public class SimpleAuditMessageBuilder : IBuildAuditLogMessage
    {
        /// <summary>
        /// Convert <see cref="ISmtpMessage"/> into a audit string.
        /// </summary>
        /// <param name="message"></param>
        /// <returns></returns>
        public string Build(ISmtpMessage message)
        {
            return string.Format("MAILFROM={0};RCPTTO={1}", message.GetMailFrom(), message.GetRcptTo());
        }
    }

    public class HeaderAuditMessageBuilder : IBuildAuditLogMessage
    {
        /// <summary>
        /// Convert <see cref="ISmtpMessage"/> into a audit string.
        /// </summary>
        /// <param name="message"></param>
        /// <returns></returns>
        public string Build(ISmtpMessage message)
        {
            return message.GetEnvelope().Message.Headers.ToString();
        }
    }

    public class FullAuditMessageBuilder : IBuildAuditLogMessage
    {
        /// <summary>
        /// Convert <see cref="ISmtpMessage"/> into a audit string.
        /// </summary>
        /// <param name="message"></param>
        /// <returns></returns>
        public string Build(ISmtpMessage message)
        {
            return message.GetMessageText();
        }
    }
}
