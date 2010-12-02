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

using Health.Direct.Config.Tools;
using Health.Direct.Config.Tools.Command;

namespace Health.Direct.Config.Console.Command
{
    public class SettingsCommands : CommandsBase
    {
        internal SettingsCommands(ConfigConsole console) : base(console)
        {
        }

        [Command(Name = "Settings_Host_Get",
            Usage = "Get hosts used by config service clients")]
        public void SettingsHostGet(string[] args)
        {
            WriteLine("DomainManager {0}", GetHost(CurrentConsole.Settings.DomainManager.Url));
            WriteLine("AddressManager {0}", GetHost(CurrentConsole.Settings.AddressManager.Url));
            WriteLine("CertificateManager {0}", GetHost(CurrentConsole.Settings.CertificateManager.Url));
            WriteLine("DnsRecordManager {0}", GetHost(CurrentConsole.Settings.DnsRecordManager.Url));
            WriteLine("AnchorManager {0}", GetHost(CurrentConsole.Settings.AnchorManager.Url));
        }

        [Command(Name = "Settings_Host_Set", Usage = SettingsHostSetUsage)]
        public void SettingsHostSet(string[] args)
        {
            string host = args.GetRequiredValue(0);
            int port = args.GetOptionalValue(1, -1);
            CurrentConsole.Settings.SetHost(host, port);
            
            WriteLine("Host set to {0}", host);
        }

        private const string SettingsHostSetUsage
            = "Specify the host name and (optionally) the port on which the config service is running"
              + Constants.CRLF + "    host [port]"
              + Constants.CRLF + "E.g. foomachine OR foomachine 83";

        static string GetHost(string url)
        {
            Uri uri = new Uri(url);
            return (uri.Host + ':' + uri.Port);
        }
    }
}