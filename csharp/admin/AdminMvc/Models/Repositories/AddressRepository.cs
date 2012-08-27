/* 
 Copyright (c) 2010, Direct Project
 All rights reserved.

 Authors:
    John Theisen
  
Redistribution and use in source and binary forms, with or without modification, are permitted provided that the following conditions are met:

Redistributions of source code must retain the above copyright notice, this list of conditions and the following disclaimer.
Redistributions in binary form must reproduce the above copyright notice, this list of conditions and the following disclaimer in the documentation and/or other materials provided with the distribution.
Neither the name of The Direct Project (directproject.org) nor the names of its contributors may be used to endorse or promote products derived from this software without specific prior written permission.
THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 
*/
using System;
using System.Linq;

using Health.Direct.Config.Client.DomainManager;
using Health.Direct.Config.Store;

namespace Health.Direct.Admin.Console.Models.Repositories
{
    public class AddressRepository : IAddressRepository
    {
        private const string DefaultType = "SMTP";

        private readonly IAddressManager m_client;

        public AddressRepository(IAddressManager client)
        {
            m_client = client;
        }

        protected IAddressManager Client { get { return m_client; } }
        
        public IQueryable<Address> Query()
        {
            return Client.EnumerateAddresses(null, int.MaxValue).AsQueryable();
        }

        public Address Add(Address address)
        {
            if (address == null)
            {
                throw new ArgumentNullException("address");
            }

            address.Type = address.Type ?? DefaultType;

            return Client.AddAddress(address);
        }

        public Address Update(Address address)
        {
            Client.UpdateAddresses(new[] {address});
            address.UpdateDate = DateTimeHelper.Now;
            return address;
        }

        public void Delete(Address address)
        {
            Client.RemoveAddresses(new[]{address.EmailAddress});
        }

        public Address Get(long id)
        {
            return Client.GetAddressesByID(new[] {id}, null).FirstOrDefault();
        }
    }
}