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
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.ServiceModel;
using System.Configuration;
using System.Xml.Serialization;
using NHINDirect.Tools.Command;
using NHINDirect.Config.Store;
using NHINDirect.Config.Client;
using NHINDirect.Config.Client.DomainManager;
using NHINDirect.Config.Client.CertificateService;

namespace NHINDirect.Config.Command
{
    internal class ConfigConsole
    {
        internal static ConsoleSettings Settings;
        internal static ConfigConsole Current;
        
        Commands m_commands;
        
        internal ConfigConsole()
        {
            this.CreateClients();

            m_commands = new Commands("ConfigConsole");
            m_commands.Error += PrintError;
            
            m_commands.Register(new DomainCommands());
            m_commands.Register(new AddressCommands());
            m_commands.Register(new CertificateCommands());
            m_commands.Register(new AnchorCommands());
            m_commands.Register(new TestCommands());
            m_commands.Register(new SettingsCommands());            
        }

        internal DomainManagerClient DomainClient;
        internal AddressManagerClient AddressClient;
        internal CertificateStoreClient CertificateClient;
        internal AnchorStoreClient AnchorClient;
        
        internal void SetHost(string host, int port)
        {        
            Settings.SetHost(host, port);     
            this.CreateClients();        
        }
        
        void CreateClients()
        {
            DomainClient = Settings.DomainManager.CreateDomainManagerClient();
            AddressClient = Settings.AddressManager.CreateAddressManagerClient();
            CertificateClient = Settings.CertificateManager.CreateCertificateStoreClient();
            AnchorClient = Settings.AnchorManager.CreateAnchorStoreClient();
        }
        
        internal bool Run(string[] args)
        {
            if (args != null && args.Length > 0)
            {
                return m_commands.Run(args);
            }
            
            m_commands.RunInteractive();
            return true;
        }
        
        static void Main(string[] args)
        {
            ConfigConsole.Settings = ConsoleSettings.Load();
            ConfigConsole.Current = new ConfigConsole();
            bool result = ConfigConsole.Current.Run(args);
            if (!result && Environment.ExitCode == 0)
            {
                Environment.ExitCode = -1;
            }
        }
        
        void PrintError(Exception ex)
        {
            FaultException<ConfigStoreFault> fault = ex as FaultException<ConfigStoreFault>;
            if (fault != null)
            {
                CommandUI.PrintBold("CONFIGSTOREERROR={0}", fault.Detail.Error);
                Console.WriteLine(fault.Detail.Message);
                CommandUI.PrintSectionBreak();
                Console.WriteLine(fault.ToString());
            }
            else
            {
                Console.WriteLine(ex.Message);
            }
        }        
    }
}
