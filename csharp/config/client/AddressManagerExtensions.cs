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

using Health.Direct.Common.Extensions;
using Health.Direct.Config.Client.DomainManager;
using Health.Direct.Config.Store;

namespace Health.Direct.Config.Client
{
    public static class AddressManagerExtensions
    {
        public static bool AddressExists(this AddressManagerClient client, MailAddress emailAddress)
        {
            Address address = client.GetAddress(emailAddress);
            return (address != null);
        }
        
        public static void AddAddress(this AddressManagerClient client, Address address)
        {
            if (address == null)
            {
                throw new ArgumentNullException("address");
            }
            
            client.AddAddresses(new Address[] {address});
        }
        
        public static void UpdateAddress(this AddressManagerClient client, Address address)
        {
            if (address == null)
            {
                throw new ArgumentNullException("address");
            }

            client.UpdateAddresses(new Address[] { address });
        }

        public static Address GetAddressesOrDomain(this AddressManagerClient client, MailAddress address, EntityStatus? status)
        {
            if (address == null)
            {
                throw new ArgumentNullException("address");
            }

            return client.GetAddressesOrDomain(address.Address, status);
        }


        public static Address GetAddressesOrDomain(this AddressManagerClient client, string emailAddress, EntityStatus? status)
        {
            if (string.IsNullOrEmpty(emailAddress))
            {
                throw new ArgumentException("value was null or empty", "emailAddress");
            }

            Address[] addresses = client.GetAddressesOrDomain(new string[] { emailAddress }, status);
            if (addresses.IsNullOrEmpty())
            {
                return null;
            }

            return addresses[0];
        }
        


        public static Address GetAddress(this AddressManagerClient client, MailAddress address)
        {
            return client.GetAddress(address.Address, null);
        }

        public static Address GetAddress(this AddressManagerClient client, MailAddress address, EntityStatus? status)
        {
            if (address == null)
            {
                throw new ArgumentNullException("address");
            }

            return client.GetAddress(address.Address, status);
        }
        
        public static Address GetAddress(this AddressManagerClient client, string emailAddress)
        {
            return client.GetAddress(emailAddress, null);
        }
        
        public static Address GetAddress(this AddressManagerClient client, string emailAddress, EntityStatus? status)
        {
            if (string.IsNullOrEmpty(emailAddress))
            {
                throw new ArgumentException("value was null or empty", "emailAddress");
            }
            
            Address[] addresses = client.GetAddresses(new string[] {emailAddress}, status);
            if (addresses.IsNullOrEmpty())
            {
                return null;
            }
            
            return addresses[0];
        }
        
        public static Address GetAddress(this AddressManagerClient client, long addressID)
        {
            return client.GetAddress(addressID, null);
        }
        
        public static Address GetAddress(this AddressManagerClient client, long addressID, EntityStatus? status)
        {
            // use ids..
            Address[] addresses = client.GetAddressesByID(new long[] { addressID }, status);
            if (addresses.IsNullOrEmpty())
            {
                return null;
            }
            
            return addresses[0];
        }
        
        public static Address[] GetAddresses(this AddressManagerClient client, string[] emailAddresses)
        {
            return client.GetAddresses(emailAddresses, null);
        }

        public static Address[] GetAddressesByID(this AddressManagerClient client, long[] addressIDs)
        {
            return client.GetAddressesByID(addressIDs, null);
        }
        
        public static void RemoveAddress(this AddressManagerClient client, MailAddress emailAddress)
        {
            client.RemoveAddress(emailAddress.Address);
        }
        
        public static void RemoveAddress(this AddressManagerClient client, string emailAddress)
        {
            if (string.IsNullOrEmpty(emailAddress))
            {
                throw new ArgumentException("value was null or empty", "emailAddress");
            }
            
            client.RemoveAddresses(new string[] {emailAddress});
        }
        
        public static IEnumerable<Address> EnumerateDomainAddresses(this AddressManagerClient client, string domainName, int chunkSize)
        {
            if (chunkSize < 1)
            {
                throw new ArgumentException("value was less than 1", "chunkSize");
            }

            string lastAddress = null;
            Address[] addresses;
            while (true)
            {
                addresses = client.EnumerateDomainAddresses(domainName, lastAddress, chunkSize);
                if (addresses.IsNullOrEmpty())
                {
                    yield break;
                }
                for (int i = 0; i < addresses.Length; ++i)
                {
                    yield return addresses[i];
                }
                lastAddress = addresses[addresses.Length - 1].EmailAddress;
            }
        }

        public static IEnumerable<Address> EnumerateAddresses(this AddressManagerClient client, int chunkSize)
        {
            if (chunkSize < 1)
            {
                throw new ArgumentException("value was less than 1", "chunkSize");
            }

            string lastAddress = null;
            Address[] addresses;
            while (true)
            {
                addresses = client.EnumerateAddresses(lastAddress, chunkSize);
                if (addresses.IsNullOrEmpty())
                {
                    yield break;
                }
                for (int i = 0; i < addresses.Length; ++i)
                {
                    yield return addresses[i];
                }
                lastAddress = addresses[addresses.Length - 1].EmailAddress;
            }
        }
    }
}