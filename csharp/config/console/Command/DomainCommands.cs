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

using Health.Direct.Config.Client;
using Health.Direct.Config.Client.DomainManager;
using Health.Direct.Config.Store;
using Health.Direct.Config.Tools;
using Health.Direct.Config.Tools.Command;

namespace Health.Direct.Config.Console.Command
{
    /// <summary>
    /// Commands to manage domains
    /// </summary>
    public class DomainCommands : CommandsBase<DomainManagerClient>
    {
        const int DefaultChunkSize = 25;

        private readonly Func<AddressManagerClient> m_addressClientResolver;

        internal DomainCommands(ConfigConsole console, Func<DomainManagerClient> client, Func<AddressManagerClient> addressClient)
            : base(console, client)
        {
            m_addressClientResolver = addressClient;
        }

        private AddressManagerClient AddressClient
        {
            get
            {
                return m_addressClientResolver();
            }
        }

        //---------------------------------------
        //
        // Commands
        //
        //---------------------------------------

        /// <summary>
        /// Add a domain
        /// </summary>
        [Command(Name = "Domain_Add", Usage = DomainAddUsage)]
        public void CommandDomainAdd(string[] args)
        {            
            Domain domain = new Domain(args.GetRequiredValue(0))
                                {
                                    Status = args.GetOptionalEnum(1, EntityStatus.New),
                                    AgentName = args.GetOptionalValue(2, null)
                                };

            if (Client.DomainExists(domain.Name))
            {
                WriteLine("Exists {0}", domain.Name);
            }
            else
            {
                Client.AddDomain(domain);
                WriteLine("Added {0}", domain.Name);
            }
        }                  
        
        private const string DomainAddUsage
            = "Add a new domain."
            + Constants.CRLF + "    domainName [status]"
            + Constants.CRLF + "\t domainName: New domain name"
            + Constants.CRLF + "\t status: " + Constants.EntityStatusString
            + Constants.CRLF + "\t agentName: " + "Domain grouping identifier";
        
        /// <summary>
        /// Retrieve a domain
        /// </summary>
        [Command(Name = "Domain_Get", Usage = DomainGetUsage)]
        public void DomainGet(string[] args)
        {
            string name = args.GetRequiredValue(0);
            Print(this.DomainGet(name));
        }

        private const string DomainGetUsage
            = "Retrieve information for an existing domain."
              + Constants.CRLF + "    domainName";
        
        /// <summary>
        /// How many domains exist? 
        /// </summary>
        [Command(Name = "Domain_Count", Usage = "Retrieve # of domains.")]
        public void DomainCount(string[] args)
        {
            WriteLine("{0} domains", Client.GetDomainCount());
        }
        
        /// <summary>
        /// Set the status for a domain
        /// </summary>                        
        [Command(Name = "Domain_Status_Set", Usage = DomainStatusSetUsage)]
        public void DomainStatusSet(string[] args)
        {
            string name = args.GetRequiredValue(0);
            EntityStatus status = args.GetRequiredEnum<EntityStatus>(1);

            Domain domain = this.DomainGet(name);
            domain.Status = status;            
            Client.UpdateDomain(domain);
        }

        private const string DomainStatusSetUsage
            = "Change a domain's status"
              + Constants.CRLF + "    domainName status"
              + Constants.CRLF + "\t domainName: Set status for this domain"
              + Constants.CRLF + "\t status: " + Constants.EntityStatusString;

        /// <summary>
        /// Set the agent name for a domain
        /// </summary>                        
        [Command(Name = "Domain_Agent_Set", Usage = DomainAgentSetUsage)]
        public void DomainAgentSet(string[] args)
        {
            string name = args.GetRequiredValue(0);
            string agentName = args.GetOptionalValue(1, String.Empty);

            Domain domain = this.DomainGet(name);
            domain.AgentName = agentName;
            Client.UpdateDomain(domain);
        }

        private const string DomainAgentSetUsage
            = "Change a domain's agent name"
              + Constants.CRLF + "    domain agentName"
              + Constants.CRLF + "\t domainName: Set agent name for this domain"
              + Constants.CRLF + "\t agentName: " + "Optional domain grouping identifier";

        /// <summary>
        /// Set the status for all addresses in a domain
        /// </summary>
        [Command(Name = "Domain_Address_Status_Set", Usage = DomainAddressStatusSetUsage)]
        public void DomainAddressStatusSet(string[] args)
        {
            string name = args.GetRequiredValue(0);
            EntityStatus status = args.GetRequiredEnum<EntityStatus>(1);

            Domain domain = this.DomainGet(name);
            AddressClient.SetDomainAddressesStatus(domain.ID, status);
        }

        private const string DomainAddressStatusSetUsage
            = "Set the status of all addresses in this domain"
              + Constants.CRLF + "    domainName status"
              + Constants.CRLF + "\t domainName: Set status for this domain"
              + Constants.CRLF + "\t status: " + Constants.EntityStatusString;
        


        // We think this is no longer needed. Remove when confirmed
        /*
        [Command(Name = "Domain_Postmaster_Get", Usage = DomainPostmasterGetUsage)]
        public void DomainPostmasterGet(string[] args)
        {
            string name = args.GetRequiredValue(0);
            Domain domain = DomainGet(name);
            Address address = CurrentConsole.AddressClient.GetAddress(domain.ID);
            AddressCommands.Print(address);
        }

        private const string DomainPostmasterGetUsage
            = "Display a domain's postmaster, if set explicitly."
                + CRLF + "    domainName";
        }

        [Command(Name = "Domain_Postmaster_Set", Usage = DomainPostmasterSetUsage)]
        public void DomainPostmasterSet(string[] args)
        {
            MailAddress email = new MailAddress(args.GetRequiredValue(0));
            
            Address postmaster = CurrentConsole.AddressClient.GetAddress(email);
            if (postmaster == null)
            {
                throw new ArgumentException(string.Format("Postmaster address {0} not found", email));
            }
            
            Domain domain = this.DomainGet(email.Host);
            domain.PostmasterID = postmaster.ID;
            Client.UpdateDomain(domain);
        }        

        private const string DomainPostmasterSetUsage
            = "Set the postmaster address for a domain. The address must have been already created."
                + CRLF + "    postmasterEmail";
        */

        /// <summary>
        /// Remove domain
        /// </summary>
        [Command(Name = "Domain_Remove", Usage = DomainRemoveUsage)]
        public void DomainRemove(string[] args)
        {
            Client.RemoveDomain(args.GetRequiredValue(0));
        }

        private const string DomainRemoveUsage
            = "Remove a domain."
              + Constants.CRLF + "    domainName"
              + Constants.CRLF + "\t domainName: remove this domain";

        [Command(Name = "Domain_List", Usage = "List all domains")]
        public void DomainList(string[] args)
        {
            int chunkSize = args.GetOptionalValue(0, DefaultChunkSize);            
            Print(Client.EnumerateDomains(chunkSize));
        }

        //---------------------------------------
        //
        // Implementation
        //
        //---------------------------------------

        public Domain DomainGet(string name)
        {
            Domain domain = Client.GetDomain(name);
            if (domain == null)
            {
                throw new ArgumentException(string.Format("Domain {0} not found", name));
            }

            return domain;
        }
        
        public void Print(IEnumerable<Domain> domains)
        {
            foreach(Domain domain in domains)
            {
                Print(domain);
                CommandUI.PrintSectionBreak();
            }
        }
        
        public void Print(Domain domain)
        {
            CommandUI.Print("Name", domain.Name);
            CommandUI.Print("AgentName", domain.AgentName);
            CommandUI.Print("ID", domain.ID);
            CommandUI.Print("CreateDate", domain.CreateDate);
            CommandUI.Print("UpdateDate", domain.UpdateDate);
            CommandUI.Print("Status", domain.Status);
        }
    }
}