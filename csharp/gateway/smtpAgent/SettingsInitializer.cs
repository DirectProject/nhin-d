/* 
 Copyright (c) 2016, Direct Project
 All rights reserved.

 Authors:
    Joseph Shook    Joseph.Shook@Surescripts.com
  
Redistribution and use in source and binary forms, with or without modification, are permitted provided that the following conditions are met:

Redistributions of source code must retain the above copyright notice, this list of conditions and the following disclaimer.
Redistributions in binary form must reproduce the above copyright notice, this list of conditions and the following disclaimer in the documentation and/or other materials provided with the distribution.
Neither the name of The Direct Project (directproject.org) nor the names of its contributors may be used to endorse or promote products derived from this software without specific prior written permission.
THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 
*/

using System;
using Health.Direct.Common.Diagnostics;
using Health.Direct.SmtpAgent.Config;

namespace Health.Direct.SmtpAgent
{
    public static class SettingsInitializer
    {
        private static bool m_initialized;
        public const string BadmailFolder = @"C:\inetpub\mailroot\Gateway\badMail";
        public const string LogPath = @"C:\inetpub\logs";

        public static SmtpAgentSettings Init(string configFilePath)
        {
            SmtpAgentSettings settings;

            try
            {
                settings = SmtpAgentSettings.LoadSettings(configFilePath);
            }
            catch (Exception ex)
            {
                settings = LoadFailedInit();
            }

            return settings;
        }

        public static SmtpAgentSettings LoadFailedInit()
        {

            var badMessage = new ProcessBadMessageSettings
            {
                CopyFolder = BadmailFolder
            };

            badMessage.EnsureFolders();

            var logSettings = new LogFileSettings();
            logSettings.DirectoryPath = LogPath;
            logSettings.NamePrefix = "gateway";

            return new SmtpAgentSettings
            {
                FailedInit = true,
                BadMessage = badMessage,
                LogSettings = logSettings
            };
        }

        public static SmtpAgent DisabledAgent()
        {
            var smtpAgent = new SmtpAgent(LoadFailedInit());

            return smtpAgent;
        }
    }
}
