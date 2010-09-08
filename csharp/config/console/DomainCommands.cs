/* 
 Copyright (c) 2010, NHIN Direct Project
 All rights reserved.

 Authors:
    Umesh Madan     umeshma@microsoft.com
  
Redistribution and use in source and binary forms, with or without modification, are permitted provided that the following conditions are met:

Redistributions of source code must retain the above copyright notice, this list of conditions and the following disclaimer.
Redistributions in binary form must reproduce the above copyright notice, this list of conditions and the following disclaimer in the documentation and/or other materials provided with the distribution.
Neither the name of the The NHIN Direct Project (nhindirect.org). nor the names of its contributors may be used to endorse or promote products derived from this software without specific prior written permission.
THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 
*/
using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Net.Mail;
using System.ServiceModel;
using NHINDirect.Tools.Command;
using NHINDirect.Config.Client;
using NHINDirect.Config.Store;
using NHINDirect.Config.Client.DomainManager;

namespace NHINDirect.Config.Command
{
    /// <summary>
    /// Commands to manage domains
    /// </summary>
    public class DomainCommands
    {
        const int DefaultChunkSize = 25;
        
        public DomainCommands()
        {
        }     
        
        public void Command_Domain_Add(string[] args)
        {            
            Domain domain = new Domain(args.GetRequiredValue(0));
            domain.Status = args.GetOptionalEnum<EntityStatus>(1, EntityStatus.New);
            
            if (ConfigConsole.Current.DomainClient.DomainExists(domain.Name))
            {
                Console.WriteLine("Exists {0}", domain);
            }
            else
            {
                ConfigConsole.Current.DomainClient.AddDomain(domain);
                Console.WriteLine("Added {0}", domain);
            }
        }                   
        public void Usage_Domain_Add()
        {
            Console.WriteLine("Add a new domain.");
            Console.WriteLine("    domainName [status]");            
        }
        
        public void Command_Domain_Get(string[] args)
        {
            string name = args.GetRequiredValue(0);
            Print(this.DomainGet(name));
        }
        public void Usage_Domain_Get()
        {
            Console.WriteLine("Retrieve information for an existing domain.");
            Console.WriteLine("    domainName");
        }
        
        public void Command_Domain_Count(string[] args)
        {
            Console.WriteLine("{0} domains", ConfigConsole.Current.DomainClient.GetDomainCount());
        }
        public void Usage_Domain_Count()
        {
            Console.WriteLine("Retrieve # of domains.");
        }
                        
        public void Command_Domain_Status_Set(string[] args)
        {
            string name = args.GetRequiredValue(0);
            EntityStatus status = args.GetRequiredEnum<EntityStatus>(1);

            Domain domain = this.DomainGet(name);
            domain.Status = status;            
            ConfigConsole.Current.DomainClient.UpdateDomain(domain);
        }        
        public void Usage_Domain_Status_Set()
        {
            Console.WriteLine("Change a domain's status");
            Console.WriteLine("    domainName Status({0})", Extensions.EntityStatusString);
        }

        public void Command_Domain_Address_Status_Set(string[] args)
        {
            string name = args.GetRequiredValue(0);
            EntityStatus status = args.GetRequiredEnum<EntityStatus>(1);

            Domain domain = this.DomainGet(name);
            ConfigConsole.Current.AddressClient.SetDomainAddressesStatus(domain.ID, status);
        }
        public void Usage_Domain_Address_Status_Set()
        {
            Console.WriteLine("Set the status of all addresses in this domain");
            Console.WriteLine("    domainaddressstatusset Status ({0})", Extensions.EntityStatusString);
        }
        
        public void Command_Domain_Postmaster_Get(string[] args)
        {
            string name = args.GetRequiredValue(0);
            Domain domain = DomainGet(name);
            Address address = ConfigConsole.Current.AddressClient.GetAddress(domain.ID);
            AddressCommands.Print(address);
        }
        public void Usage_Domain_Postmaster_Get()
        {
            Console.WriteLine("Display a domain's postmaster, if set explicitly.");
            Console.WriteLine("    domainName");
        }
        public void Command_Domain_Postmaster_Set(string[] args)
        {
            MailAddress email = new MailAddress(args.GetRequiredValue(0));
            
            Address postmaster = ConfigConsole.Current.AddressClient.GetAddress(email);
            if (postmaster == null)
            {
                throw new ArgumentException(string.Format("Postmaster address {0} not found", email));
            }
            
            Domain domain = this.DomainGet(email.Host);
            domain.PostmasterID = postmaster.ID;
            ConfigConsole.Current.DomainClient.UpdateDomain(domain);
        }        
        public void Usage_Domain_Postmaster_Set()
        {
            Console.WriteLine("Set the postmaster address for a domain. The address must have been already created.");
            Console.WriteLine("    postmasterEmail");
        }
        
        public void Command_Domain_Remove(string[] args)
        {
            ConfigConsole.Current.DomainClient.RemoveDomain(args.GetRequiredValue(0));
        }
        public void Usage_Domain_Remove()
        {
            Console.WriteLine("Remove a domain.");
            Console.WriteLine("    domainName");
        }
        
        public void Command_Domain_List(string[] args)
        {
            int chunkSize = args.GetOptionalValue<int>(0, DefaultChunkSize);            
            Print(ConfigConsole.Current.DomainClient.EnumerateDomains(chunkSize));
        }
        public void Usage_Domain_List()
        {
            Console.WriteLine("List all domains");
            Console.WriteLine("    domainlist");
        }
        
        Domain DomainGet(string name)
        {
            return DomainGet(ConfigConsole.Current.DomainClient, name);
        }

        internal static Domain DomainGet(DomainManagerClient client, string name)
        {
            Domain domain = client.GetDomain(name);
            if (domain == null)
            {
                throw new ArgumentException(string.Format("Domain {0} not found", name));
            }

            return domain;
        }
        
        public static void Print(IEnumerable<Domain> domains)
        {
            foreach(Domain domain in domains)
            {
                Print(domain);
                CommandUI.PrintSectionBreak();
            }
        }
        
        public static void Print(Domain domain)
        {
            CommandUI.Print("Name", domain.Name);
            CommandUI.Print("ID", domain.ID);
            CommandUI.Print("CreateDate", domain.CreateDate);
            CommandUI.Print("UpdateDate", domain.UpdateDate);
            CommandUI.Print("PostmasterID", domain.PostmasterID);
            CommandUI.Print("Status", domain.Status);
        }
    }
}
