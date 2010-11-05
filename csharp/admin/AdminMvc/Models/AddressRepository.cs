using System.Linq;

using Health.Direct.Config.Client.DomainManager;
using Health.Direct.Config.Store;

namespace AdminMvc.Models
{
    public class AddressRepository : Repository<Address>
    {
        private readonly AddressManagerClient m_client;

        public AddressRepository()
        {
            m_client = new AddressManagerClient();
        }

        protected AddressManagerClient Client { get { return m_client; } }
        
        public override IQueryable<Address> FindAll()
        {
            return Client.EnumerateAddresses(null, int.MaxValue).AsQueryable();
        }

        public Address Add(AddressModel model)
        {
            return Client.AddAddress(
                new Address
                    {
                        DisplayName = model.DisplayName,
                        DomainID = model.DomainID,
                        EmailAddress = model.EmailAddress,
                        Type = model.Type
                    });
        }

        public override Address Add(Address address)
        {
            return Client.AddAddress(address);
        }

        public override void Update(Address address)
        {
            Client.UpdateAddresses(new[] {address});
        }

        public override void Delete(Address address)
        {
            Client.RemoveAddresses(new[]{address.EmailAddress});
        }

        public override Address Get(long id)
        {
            return Client.GetAddressesByID(new[] {id}, null).FirstOrDefault();
        }
    }
}