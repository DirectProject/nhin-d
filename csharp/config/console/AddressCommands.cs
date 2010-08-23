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
using NHINDirect.Tools.Command;
using NHINDirect.Config.Store;
using NHINDirect.Config.Client.DomainManager;

namespace NHINDirect.Config.Command
{
    public class AddressCommands
    {
        DomainManagerClient m_domainClient;
        AddressManagerClient m_addressClient;
        
        public AddressCommands()
        {            
            m_domainClient = new DomainManagerClient(ConfigConsole.Settings.DomainManager.Binding, ConfigConsole.Settings.DomainManager.Endpoint);
            m_addressClient = new AddressManagerClient(ConfigConsole.Settings.AddressManager.Binding, ConfigConsole.Settings.AddressManager.Endpoint);
        }
        
        public void Command_AddressAdd(string[] args)
        {
            MailAddress address = new MailAddress(args.GetRequiredValue(0));
            string displayName = address.DisplayName;
            if (string.IsNullOrEmpty(displayName))
            {
                displayName = args.GetOptionalValue(1, string.Empty);
            }
            
            Domain domain = DomainCommands.DomainGet(m_domainClient, address.Host);
            m_addressClient.AddAddress(new Address(domain.ID, address.Address, displayName));
        }                
        public void Usage_AddressAdd()
        {
            Console.WriteLine("Add a new email address. The address domain must already exist.");
            Console.WriteLine("    emailAddress [displayName]");
        }
        
        public void Command_AddressGet(string[] args)
        {
            MailAddress email = new MailAddress(args.GetRequiredValue(0));
            
            Address address = GetAddress(email.Address);
            Print(address);
        }
        public void Usage_AddressGet()
        {
            Console.WriteLine("Retrieve an existing address.");
            Console.WriteLine("    addressget emailAddress");
        }
                
        public void Command_AddressRemove(string[] args)
        {
            MailAddress address = new MailAddress(args.GetRequiredValue(0));
            m_addressClient.RemoveAddress(address);
        }
        public void Usage_AddressRemove()
        {
            Console.WriteLine("Remove an existing address.");
            Console.WriteLine("    addressremove emailAddress.");
        }
        
        public void Command_AddressList(string[] args)
        {
            string domainName = args.GetRequiredValue(0);            
            int chunkSize = args.GetOptionalValue<int>(1, 25);
         
            Domain domain = DomainCommands.DomainGet(m_domainClient, domainName);
            Print(m_addressClient.EnumerateDomainAddresses(domain.ID, chunkSize));
        }        
        public void Usage_AddressList()
        {
            Console.WriteLine("List addresses for a domain.");
            Console.WriteLine("    addresslist domainName [chunkSize] [displayChunkSize]");
            Console.WriteLine("\tchunkSize: Number of addresses to download from service at a time.");
        }
        
        internal Address GetAddress(string email)
        {
            return GetAddress(m_addressClient, email);
        }
        
        internal static Address GetAddress(AddressManagerClient client, string email)
        {
            Address address = client.GetAddress(email);
            if (address == null)
            {
                throw new ArgumentException("Address {0} not found", email);
            }
            
            return address;
        }
                
        internal static void Print(IEnumerable<Address> addresses)
        {
            foreach(Address address in addresses)
            {
                Print(address);
                CommandUI.PrintSectionBreak();
            }
        }
        
        internal static void Print(Address address)
        {
            CommandUI.Print("Email", address.EmailAddress);
            CommandUI.Print("ID", address.ID);
            CommandUI.Print("DisplayName", address.DisplayName);
            CommandUI.Print("DomainID", address.DomainID);
            CommandUI.Print("Type", address.Type);
            CommandUI.Print("CreateDate", address.CreateDate);
            CommandUI.Print("UpdateDate", address.UpdateDate);
        }
    }
}
