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
using System.ServiceModel;
using Health.Direct.Config.Client.CertificateService;
using Health.Direct.Config.Client.DomainManager;
using Health.Direct.Config.Client.SettingsManager;
using Health.Direct.Config.Console.Command;
using Health.Direct.Config.Store;
using Health.Direct.Config.Tools.Command;

namespace Health.Direct.Config.Console
{
    public class ConfigConsole
    {
        readonly ConsoleSettings m_settings;
        readonly Commands m_commands;

        private DomainManagerClient m_domainClient;
        private AddressManagerClient m_addressClient;
        private CertificateStoreClient m_certificateClient;
        private AnchorStoreClient m_anchorClient;
        private DnsRecordManagerClient m_dnsRecordClient;
        private PropertyManagerClient m_propertyClient;
        private BlobManagerClient m_blobClient;
        private BundleStoreClient m_bundleClient;
        
        internal ConfigConsole(ConsoleSettings settings)
        {
            m_settings = settings;

            this.CreateClients();

            m_commands = new Commands("ConfigConsole");
            m_commands.Error += PrintError;
            
            m_commands.Register(new AddressCommands(this, () => m_addressClient));
            m_commands.Register(new AnchorCommands(this, () => m_anchorClient));
            m_commands.Register(new CertificateCommands(this, () => m_certificateClient));
            m_commands.Register(new DomainCommands(this, () => m_domainClient, () => m_addressClient));
            m_commands.Register(new DnsRecordCommands(this, () => m_dnsRecordClient));
            m_commands.Register(new PropertyCommands(this, () => m_propertyClient));
            m_commands.Register(new BlobCommands(this, () => m_blobClient));
            m_commands.Register(new SettingsCommands(this));
            m_commands.Register(new TestCommands(this));
            m_commands.Register(new BundleCommands(this, () => m_bundleClient));

            m_settings.HostAndPortChanged += HostAndPortChanged;
        }

        public ConsoleSettings Settings
        {
            get { return m_settings; }
        }

        private void HostAndPortChanged(object sender, EventArgs e)
        {
            this.CreateClients();
        }
        
        void CreateClients()
        {
            m_domainClient = m_settings.DomainManager.CreateDomainManagerClient();
            m_addressClient = m_settings.AddressManager.CreateAddressManagerClient();
            m_certificateClient = m_settings.CertificateManager.CreateCertificateStoreClient();
            m_dnsRecordClient = m_settings.DnsRecordManager.CreateDnsRecordManagerClient();
            m_anchorClient = m_settings.AnchorManager.CreateAnchorStoreClient();
            m_bundleClient = m_settings.BundleManager.CreateBundleStoreClient();

            if (m_settings.PropertyManager != null)
            {
                m_propertyClient = m_settings.PropertyManager.CreatePropertyManagerClient();
            }
            if (m_settings.BlobManager != null)
            {
                m_blobClient = m_settings.BlobManager.CreateBlobManagerClient();
            }
        }
        
        bool Run(string[] args)
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
            ConfigConsole console = new ConfigConsole(ConsoleSettings.Load());

            bool result = console.Run(args);
            if (!result && Environment.ExitCode == 0)
            {
                Environment.ExitCode = -1;
            }
        }

        static void PrintError(Exception ex)
        {
            FaultException<ConfigStoreFault> fault = ex as FaultException<ConfigStoreFault>;
            if (fault != null)
            {
                CommandUI.PrintBold("CONFIGSTOREERROR={0}", fault.Detail.Error);
                System.Console.WriteLine(fault.Detail.Message);
                CommandUI.PrintSectionBreak();
                System.Console.WriteLine(fault.ToString());
            }
            else
            {
                System.Console.WriteLine(ex.Message);
            }
        }

        public T GetCommand<T>()
            where T : class
        {
            return m_commands.GetCommand<T>();
        }
        
        //
        // Evaluate the given command line. Lets you run one command from another command
        //
        public void Eval(params string[] args)
        {
            m_commands.Eval(args);
        }
    }
}