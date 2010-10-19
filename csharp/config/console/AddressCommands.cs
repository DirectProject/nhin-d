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
using System.Net.Mail;

using NHINDirect.Tools.Command;
using NHINDirect.Config.Store;
using NHINDirect.Config.Client.DomainManager;

namespace NHINDirect.Config.Command
{
    /// <summary>
    /// Commands to manage Addresses
    /// </summary>
    public class AddressCommands : CommandsBase
    {
        const int DefaultChunkSize = 25;
                
        //---------------------------------------
        //
        // Commands
        //
        //---------------------------------------
        /// <summary>
        /// Add a new email address
        /// </summary>
        public void Command_Address_Add(string[] args)
        {
            MailAddress address = new MailAddress(args.GetRequiredValue(0));            
            string addressType = args.GetOptionalValue(1, "SMTP");
            string displayName = args.GetOptionalValue(2, string.Empty);
            if (string.IsNullOrEmpty(displayName))
            {
                displayName = address.DisplayName;
            }
            
            if (ConfigConsole.Current.AddressClient.AddressExists(address))
            {
                Console.WriteLine("Exists {0}", address);
            }
            else
            {
                Domain domain = DomainCommands.DomainGet(ConfigConsole.Current.DomainClient, address.Host);
                Address newAddress = new Address(domain.ID, address.Address, displayName);
                newAddress.Type = addressType;
                
                ConfigConsole.Current.AddressClient.AddAddress(newAddress);
                Console.WriteLine("Added {0}", address);
            }
        }                
        public void Usage_Address_Add()
        {
            Console.WriteLine("Add a new email address. The address domain must already exist.");
            Console.WriteLine("    emailAddress [addressType] [displayName]");
            Console.WriteLine("\t emailAddress: valid email address. Verifies that the domain already exists.");
            Console.WriteLine("\t addressType: (optional) such as XDR. Used for routing. default:SMTP");
            Console.WriteLine("\t displayName: (optional)");
        }
        
        /// <summary>
        /// Set the display name for an address
        /// </summary>
        /// <param name="args"></param>
        public void Command_Address_DisplayName_Set(string[] args)
        {
            string emailAddress = args.GetRequiredValue(0);
            string displayName = args.GetRequiredValue(1);
            
            Address address = ConfigConsole.Current.AddressClient.GetAddress(emailAddress);
            if (address == null)
            {
                throw new ArgumentException(string.Format("{0} not found", emailAddress));
            }
            
            address.DisplayName = displayName;
            ConfigConsole.Current.AddressClient.UpdateAddress(address);
        }        
        public void Usage_Address_DisplayName_Set()
        {
            Console.WriteLine("Set the display name for the given address");
            Console.WriteLine("    emailAddress displayname");
            Console.WriteLine("\t emailAddress: existing email address.");
            Console.WriteLine("\t displayname: new display name.");
        }
        
        /// <summary>
        /// Retrieve an existing address
        /// </summary>
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
        
        /// <summary>
        /// Remove an existing email address
        /// </summary>
        public void Command_Address_Remove(string[] args)
        {
            MailAddress address = new MailAddress(args.GetRequiredValue(0));
            ConfigConsole.Current.AddressClient.RemoveAddress(address);
        }
        public void Usage_Address_Remove()
        {
            Console.WriteLine("Remove an existing address.");
            Console.WriteLine("    emailAddress");
        }
        
        /// <summary>
        /// List all email addresses in a domain
        /// </summary>
        public void Command_Address_List(string[] args)
        {
            string domainName = args.GetRequiredValue(0);
            int chunkSize = args.GetOptionalValue<int>(1, DefaultChunkSize);
         
            Print(ConfigConsole.Current.AddressClient.EnumerateDomainAddresses(domainName, chunkSize));
        }        
        public void Usage_Address_List()
        {
            Console.WriteLine("List addresses for a domain.");
            Console.WriteLine("   domainName [chunkSize]");
            Console.WriteLine("\t domainName: list addresses for this domain");
            Console.WriteLine("\tchunkSize: (optional) Number of addresses to download from service at a time. Default is {0}", DefaultChunkSize);
        }
        
        /// <summary>
        /// List all email addresses
        /// </summary>
        /// <param name="args"></param>
        public void Command_Address_ListAll(string[] args)
        {
            int chunkSize = args.GetOptionalValue<int>(0, DefaultChunkSize);
            Print(ConfigConsole.Current.AddressClient.EnumerateAddresses(chunkSize));
        }
        public void Usage_Address_ListAll()
        {
            Console.WriteLine("List all addresses.");
            Console.WriteLine("    [chunkSize]");
            Console.WriteLine("\tchunkSize: Number of addresses to download from service at a time.");
        }
        
        /// <summary>
        /// Set the status of a specific email address
        /// </summary>
        /// <param name="args"></param>
        public void Command_Address_Status_Set(string[] args)
        {
            MailAddress emailAddress = new MailAddress(args.GetRequiredValue(0));
            EntityStatus status = args.GetRequiredEnum<EntityStatus>(1);
            
            Address address = ConfigConsole.Current.AddressClient.GetAddress(emailAddress);
            if (address == null)
            {
                throw new ArgumentException("Address not found");
            }
            
            address.Status = status;
            ConfigConsole.Current.AddressClient.UpdateAddress(address);
        }        
        public void Usage_Address_Status_Set()
        {
            Console.WriteLine("Set the status of an address");
            Console.WriteLine("    emailAddress status");
            Console.WriteLine("\t emailAddress: set the status of this address");
            Console.WriteLine("\t status: {0}", EntityStatusString);
        }
        
        /// <summary>
        /// Return # of addresses in a domain
        /// </summary>
        public void Command_Address_Count(string[] args)
        {
            string domainName = args.GetRequiredValue(0);
            Console.WriteLine("{0} addresses", ConfigConsole.Current.AddressClient.GetAddressCount(domainName));
        }
        public void Usage_Address_Count()
        {
            Console.WriteLine("Retrieve # of addresses in given domain.");
            Console.WriteLine("  domainName");
        }

        //---------------------------------------
        //
        // Impl
        //
        //---------------------------------------
        
        internal Address GetAddress(string email)
        {
            return GetAddress(ConfigConsole.Current.AddressClient, email);
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
