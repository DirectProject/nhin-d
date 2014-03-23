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
using System.Net.Mail;

using Health.Direct.Config.Client;
using Health.Direct.Config.Client.DomainManager;
using Health.Direct.Config.Store;
using Health.Direct.Config.Tools;
using Health.Direct.Config.Tools.Command;

namespace Health.Direct.Config.Console.Command
{
    /// <summary>
    /// Commands to manage Addresses
    /// </summary>
    public class AddressCommands : CommandsBase<AddressManagerClient>
    {
        const int DefaultChunkSize = 25;

        //---------------------------------------
        //
        // Commands
        //
        //---------------------------------------

        internal AddressCommands(ConfigConsole console, Func<AddressManagerClient> client) : base(console, client)
        {
        }

        /// <summary>
        /// Add a new email address
        /// </summary>
        [Command(Name = "Address_Add", Usage = AddressAddUsage)]
        public void AddressAdd(string[] args)
        {
            MailAddress address = new MailAddress(args.GetRequiredValue(0));            
            string addressType = args.GetOptionalValue(1, "SMTP");
            string displayName = args.GetOptionalValue(2, string.Empty);
            if (string.IsNullOrEmpty(displayName))
            {
                displayName = address.DisplayName;
            }
            
            if (Client.AddressExists(address))
            {
                WriteLine("Exists {0}", address);
            }
            else
            {
                Domain domain = GetCommand<DomainCommands>().DomainGet(address.Host);
                if (domain == null)
                {
                    WriteLine("Domain does not exist {0}", address.Host);
                }
                else
                {
                    Address newAddress = new Address(domain.ID, address.Address, displayName) { Type = addressType };

                    Client.AddAddress(newAddress);
                    WriteLine("Added {0}", address);
                }
            }
        }

        private const string AddressAddUsage
            = "Add a new email address. The address domain must already exist."
              + Constants.CRLF + "    emailAddress [addressType] [displayName]"
              + Constants.CRLF + "\t emailAddress: valid email address. Verifies that the domain already exists."
              + Constants.CRLF + "\t addressType: (optional) such as XDR. Used for routing. default:SMTP"
              + Constants.CRLF + "\t displayName: (optional)";

        /// <summary>
        /// Set the display name for an address
        /// </summary>
        /// <param name="args"></param>
        [Command(Name = "Address_DisplayName_Set", Usage= AddressDisplayNameSetUsage)]
        public void AddressDisplayNameSet(string[] args)
        {
            string emailAddress = args.GetRequiredValue(0);
            string displayName = args.GetRequiredValue(1);
            
            Address address = Client.GetAddress(emailAddress);
            if (address == null)
            {
                throw new ArgumentException(string.Format("{0} not found", emailAddress));
            }
            
            address.DisplayName = displayName;
            Client.UpdateAddress(address);
        }

        private const string AddressDisplayNameSetUsage
            = "Set the display name for the given address"
              + Constants.CRLF + "    emailAddress displayname"
              + Constants.CRLF + "\t emailAddress: existing email address."
              + Constants.CRLF + "\t displayname: new display name.";
        
        /// <summary>
        /// Retrieve an existing address
        /// </summary>
        [Command(Name = "Address_Get", Usage = AddressGetUsage)]
        public void AddressGet(string[] args)
        {
            MailAddress email = new MailAddress(args.GetRequiredValue(0));
            
            Address address = GetAddress(email.Address);
            Print(address);
        }

        private const string AddressGetUsage
            = "Retrieve an existing address."
              + Constants.CRLF + "    emailAddress";
        
        /// <summary>
        /// Remove an existing email address
        /// </summary>
        [Command(Name = "Address_Remove", Usage = AddressRemoveUsage)]
        public void AddressRemove(string[] args)
        {
            MailAddress address = new MailAddress(args.GetRequiredValue(0));
            Client.RemoveAddress(address);

        }

        private const string AddressRemoveUsage
            = "Remove an existing address."
              + Constants.CRLF + "    emailAddress";
        
        /// <summary>
        /// List all email addresses in a domain
        /// </summary>
        [Command(Name = "Address_List")]
        public void AddressList(string[] args)
        {
            string domainName = args.GetRequiredValue(0);
            int chunkSize = args.GetOptionalValue(1, DefaultChunkSize);
         
            Print(Client.EnumerateDomainAddresses(domainName, chunkSize));
        }

        [Usage(Name = "Address_List")]
        public string AddressListUsage()
        {
            return string.Format(
                "List addresses for a domain."
                + Constants.CRLF + "   domainName [chunkSize]"
                + Constants.CRLF + "\t domainName: list addresses for this domain"
                + Constants.CRLF +
                "\t chunkSize: (optional) Number of addresses to download from service at a time. Default is {0}",
                DefaultChunkSize);
        }

        /// <summary>
        /// List all email addresses
        /// </summary>
        /// <param name="args"></param>
        [Command(Name = "Address_ListAll", Usage = AddressListAllUsage)]
        public void AddressListAll(string[] args)
        {
            int chunkSize = args.GetOptionalValue(0, DefaultChunkSize);
            Print(Client.EnumerateAddresses(chunkSize));
        }

        private const string AddressListAllUsage
            = "List all addresses."
            + Constants.CRLF + "    [chunkSize]"
            + Constants.CRLF + "\tchunkSize: Number of addresses to download from service at a time.";
        
        /// <summary>
        /// Set the status of a specific email address
        /// </summary>
        /// <param name="args"></param>
        [Command(Name = "Address_Status_Set", Usage = AddressStatusSetUsage)]
        public void AddressStatusSet(string[] args)
        {
            MailAddress emailAddress = new MailAddress(args.GetRequiredValue(0));
            EntityStatus status = args.GetRequiredEnum<EntityStatus>(1);

            Address address = Client.GetAddress(emailAddress);
            if (address == null)
            {
                throw new ArgumentException("Address not found");
            }

            address.Status = status;
            Client.UpdateAddress(address);
        }

        private const string AddressStatusSetUsage
            = "Set the status of an address"
              + Constants.CRLF + "    emailAddress status"
              + Constants.CRLF + "\t emailAddress: set the status of this address"
              + Constants.CRLF + "\t status: " + Constants.EntityStatusString;
        
        /// <summary>
        /// Return # of addresses in a domain
        /// </summary>
        [Command(Name = "Address_Count", Usage = AddressCountUsage)]
        public void AddressCount(string[] args)
        {
            string domainName = args.GetRequiredValue(0);
            WriteLine("{0} addresses", Client.GetAddressCount(domainName));
        }

        private const string AddressCountUsage
            = "Retrieve # of addresses in given domain."
            + Constants.CRLF + "  domainName";

        //---------------------------------------
        //
        // Impl
        //
        //---------------------------------------
        
        internal Address GetAddress(string email)
        {
            return GetAddress(Client, email);
        }
        
        internal Address GetAddress(AddressManagerClient client, string email)
        {
            Address address = client.GetAddress(email);
            if (address == null)
            {
                throw new ArgumentException("Address {0} not found", email);
            }
            
            return address;
        }
                
        internal void Print(IEnumerable<Address> addresses)
        {
            foreach(Address address in addresses)
            {
                Print(address);
                CommandUI.PrintSectionBreak();
            }
        }
        
        internal void Print(Address address)
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