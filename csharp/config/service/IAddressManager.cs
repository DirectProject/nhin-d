using System;
using System.Collections.Generic;
using System.Linq;
using System.ServiceModel;
using System.Text;
using NHINDirect.Config.Store;

namespace NHINDirect.Config.Service
{
    [ServiceContract(Namespace = Service.Namespace)]
    public interface IAddressManager
    {
        [OperationContract]
        [FaultContract(typeof(ConfigStoreFault))]
        void AddAddresses(Address[] addresses);

        [OperationContract]
        [FaultContract(typeof(ConfigStoreFault))]
        void UpdateAddresses(Address[] address);

        [OperationContract]
        [FaultContract(typeof(ConfigStoreFault))]
        Address[] GetAddresses(string[] emailAddresses);

        [OperationContract]
        [FaultContract(typeof(ConfigStoreFault))]
        void RemoveAddresses(string[] emailAddresses);

        [OperationContract]
        [FaultContract(typeof(ConfigStoreFault))]
        void RemoveDomainAddresses(long domainID);

        [OperationContract]
        [FaultContract(typeof(ConfigStoreFault))]
        Address[] EnumerateDomainAddresses(long domainID, long lastAddressID, int maxResults);

        [OperationContract]
        [FaultContract(typeof(ConfigStoreFault))]
        Address[] EnumerateAddresses(long lastAddressID, int maxResults);
    }
}
