/* 
 Copyright (c) 2010, Direct Project
 All rights reserved.

 Authors:
    Umesh Madan     umeshma@microsoft.com
  
Redistribution and use in source and binary forms, with or without modification, are permitted provided that the following conditions are met:

Redistributions of source code must retain the above copyright notice, this list of conditions and the following disclaimer.
Redistributions in binary form must reproduce the above copyright notice, this list of conditions and the following disclaimer in the documentation and/or other materials provided with the distribution.
Neither the name of the The Direct Project (nhindirect.org). nor the names of its contributors may be used to endorse or promote products derived from this software without specific prior written permission.
THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 
*/
using System;

using Health.Direct.Config.ToolLib.Command;

namespace Health.Direct.Config.Console.Command
{
    public class SettingsCommands
    {
        public void Command_Settings_Host_Get(string[] args)
        {
            System.Console.WriteLine("DomainManager {0}", GetHost(ConfigConsole.Settings.DomainManager.Url));
            System.Console.WriteLine("AddressManager {0}", GetHost(ConfigConsole.Settings.AddressManager.Url));
            System.Console.WriteLine("CertificateManager {0}", GetHost(ConfigConsole.Settings.CertificateManager.Url));
            System.Console.WriteLine("AnchorManager {0}", GetHost(ConfigConsole.Settings.AnchorManager.Url));
        }        
        public void Usage_Settings_Host_Get()
        {
            System.Console.WriteLine("Get hosts used by config service clients");
        }
        
        public void Command_Settings_Host_Set(string[] args)
        {
            string host = args.GetRequiredValue(0);
            int port = args.GetOptionalValue<int>(1, -1);            
            ConfigConsole.Current.SetHost(host, port);
            
            System.Console.WriteLine("Host set to {0}", host);
        }        
        public void Usage_Settings_Host_Set()
        {
            System.Console.WriteLine("Specify the host name and (optionally) the port on which the config service is running");
            System.Console.WriteLine("    host [port]");
            System.Console.WriteLine("E.g. foomachine OR foomachine 83");
        }
                
        string GetHost(string url)
        {
            Uri uri = new Uri(url);
            return (uri.Host + ':' + uri.Port);
        }

    }
}