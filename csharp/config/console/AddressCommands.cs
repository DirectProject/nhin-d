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
    /// <summary>
    /// Commands to manage Addresses
    /// </summary>
    public class AddressCommands
    {
        const int DefaultChunkSize = 25;
        
        DomainManagerClient m_domainClient;
        AddressManagerClient m_addressClient;
        
        public AddressCommands()
        {            
            m_domainClient = ConfigConsole.Settings.DomainManager.CreateDomainManagerClient();
            m_addressClient = ConfigConsole.Settings.AddressManager.CreateAddressManagerClient();
        }
        
        public void Command_Address_Add(string[] args)
        {
            MailAddress address = new MailAddress(args.GetRequiredValue(0));            
            string addressType = args.GetOptionalValue(1, "SMTP");
            string displayName = args.GetOptionalValue(2, string.Empty);
            if (string.IsNullOrEmpty(displayName))
            {
                displayName = address.DisplayName;
            }
            
            Domain domain = DomainCommands.DomainGet(m_domainClient, address.Host);
            Address newAddress = new Address(domain.ID, address.Address, displayName);
            newAddress.Type = addressType;
            
            m_addressClient.AddAddress(newAddress);
        }                
        public void Usage_Address_Add()
        {
            Console.WriteLine("Add a new email address. The address domain must already exist.");
            Console.WriteLine("    emailAddress [addressType (default:SMTP)] [displayName]");
        }
        
        public void Command_Address_DisplayName_Set(string[] args)
        {
            string emailAddress = args.GetRequiredValue(0);
            string displayName = args.GetRequiredValue(1);
            
            Address address = m_addressClient.GetAddress(emailAddress);
            if (address == null)
            {
                throw new ArgumentException(string.Format("{0} not found", emailAddress));
            }
            
            address.DisplayName = displayName;
            m_addressClient.UpdateAddress(address);
        }
        
        public void Usage_Address_DisplayName_Set()
        {
            Console.WriteLine("Set the display name for the given address");
            Console.WriteLine("    emailAddress displayname");
        }
        
        public void Command_Address_Get(string[] args)
        {
            MailAddress email = new MailAddress(args.GetRequiredValue(0));
            
            Address address = GetAddress(email.Address);
            Print(address);
        }
        public void Usage_Address_Get()
        {
            Console.WriteLine("Retrieve an existing address.");
            Console.WriteLine("    emailAddress");
        }
                
        public void Command_Address_Remove(string[] args)
        {
            MailAddress address = new MailAddress(args.GetRequiredValue(0));
            m_addressClient.RemoveAddress(address);
        }
        public void Usage_Address_Remove()
        {
            Console.WriteLine("Remove an existing address.");
            Console.WriteLine("    emailAddress");
        }
        
        public void Command_Address_List(string[] args)
        {
            string domainName = args.GetRequiredValue(0);
            int chunkSize = args.GetOptionalValue<int>(1, DefaultChunkSize);
         
            Print(m_addressClient.EnumerateDomainAddresses(domainName, chunkSize));
        }        
        public void Usage_Address_List()
        {
            Console.WriteLine("List addresses for a domain.");
            Console.WriteLine("   domainName [chunkSize]");
            Console.WriteLine("\tchunkSize: Number of addresses to download from service at a time.");
        }

        public void Command_Address_ListAll(string[] args)
        {
            int chunkSize = args.GetOptionalValue<int>(0, DefaultChunkSize);
            Print(m_addressClient.EnumerateAddresses(chunkSize));
        }
        public void Usage_Address_ListAll()
        {
            Console.WriteLine("List all addresses.");
            Console.WriteLine("    [chunkSize]");
            Console.WriteLine("\tchunkSize: Number of addresses to download from service at a time.");
        }
        
        public void Command_Address_Status_Set(string[] args)
        {
            MailAddress emailAddress = new MailAddress(args.GetRequiredValue(0));
            EntityStatus status = args.GetRequiredEnum<EntityStatus>(1);
            
            Address address = m_addressClient.GetAddress(emailAddress);
            if (address == null)
            {
                throw new ArgumentException("Address not found");
            }
            
            address.Status = status;
            m_addressClient.UpdateAddress(address);
        }        
        public void Usage_Address_Status_Set()
        {
            Console.WriteLine("Set the status of an address");
            Console.WriteLine("    emailAddress status");
        }

        public void Command_Address_Count(string[] args)
        {
            string domainName = args.GetRequiredValue(0);
            Console.WriteLine("{0} addresses", m_addressClient.GetAddressCount(domainName));
        }
        public void Usage_Address_Count()
        {
            Console.WriteLine("Retrieve # of addresses in given domain.");
            Console.WriteLine("  domainName");
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
            CommandUI.Print("Status", address.Status);
            CommandUI.Print("CreateDate", address.CreateDate);
            CommandUI.Print("UpdateDate", address.UpdateDate);
        }
    }
}
