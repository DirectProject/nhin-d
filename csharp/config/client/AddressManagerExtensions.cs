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
using NHINDirect.Config.Store;

namespace NHINDirect.Config.Client.DomainManager
{
    public static class AddressManagerExtensions
    {
        public static void AddAddress(this AddressManagerClient client, Address address)
        {
            if (address == null)
            {
                throw new ArgumentNullException();
            }
            
            client.AddAddresses(new Address[] {address});
        }
        
        public static void UpdateAddress(this AddressManagerClient client, Address address)
        {
            if (address == null)
            {
                throw new ArgumentNullException();
            }

            client.UpdateAddresses(new Address[] { address });
        }
        
        public static Address GetAddress(this AddressManagerClient client, MailAddress address)
        {
            if (address == null)
            {
                throw new ArgumentNullException();
            }
            
            return client.GetAddress(address.Address);
        }
        
        public static Address GetAddress(this AddressManagerClient client, string emailAddress)
        {
            if (string.IsNullOrEmpty(emailAddress))
            {
                throw new ArgumentException();
            }
            
            Address[] addresses = client.GetAddresses(new string[] {emailAddress});
            if (addresses.IsNullOrEmpty())
            {
                return null;
            }
            
            return addresses[0];
        }
        
        public static Address GetAddress(this AddressManagerClient client, long addressID)
        {
            // use ids..
            Address[] addresses = client.GetAddressesByID(new long[] { addressID });
            if (addresses.IsNullOrEmpty())
            {
                return null;
            }
            
            return addresses[0];
        }
        
        public static void RemoveAddress(this AddressManagerClient client, MailAddress emailAddress)
        {
            client.RemoveAddress(emailAddress.Address);
        }
        
        public static void RemoveAddress(this AddressManagerClient client, string emailAddress)
        {
            if (string.IsNullOrEmpty(emailAddress))
            {
                throw new ArgumentException();
            }
            
            client.RemoveAddresses(new string[] {emailAddress});
        }
        
        public static IEnumerable<Address> EnumerateDomainAddresses(this AddressManagerClient client, long domainID, int chunkSize)
        {
            if (chunkSize <= 0)
            {
                throw new ArgumentException();
            }

            long lastID = -1;
            Address[] addresses;
            while (true)
            {
                addresses = client.EnumerateDomainAddresses(domainID, lastID, chunkSize);
                if (addresses.IsNullOrEmpty())
                {
                    yield break;
                }
                for (int i = 0; i < addresses.Length; ++i)
                {
                    yield return addresses[i];
                }
                lastID = addresses[addresses.Length - 1].ID;
            }
        }

        public static IEnumerable<Address> EnumerateAddresses(this AddressManagerClient client, int chunkSize)
        {
            if (chunkSize <= 0)
            {
                throw new ArgumentException();
            }

            long lastID = -1;
            Address[] addresses;
            while (true)
            {
                addresses = client.EnumerateAddresses(lastID, chunkSize);
                if (addresses.IsNullOrEmpty())
                {
                    yield break;
                }
                for (int i = 0; i < addresses.Length; ++i)
                {
                    yield return addresses[i];
                }
                lastID = addresses[addresses.Length - 1].ID;
            }
        }
    }
}
