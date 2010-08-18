using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.ServiceModel;
using NHINDirect.Config.Store;

namespace NHINDirect.Config.Service
{
    [ServiceContract(Namespace = Service.Namespace)]
    public interface IDomainManager
    {
        [OperationContract]
        [FaultContract(typeof(ConfigStoreFault))]
        void AddDomain(Domain domain);

        [OperationContract]
        [FaultContract(typeof(ConfigStoreFault))]
        void UpdateDomain(Domain domain);

        [OperationContract]
        [FaultContract(typeof(ConfigStoreFault))]
        Domain GetDomain(string domainName);
        
        [OperationContract]
        [FaultContract(typeof(ConfigStoreFault))]
        void RemoveDomain(string domainName);

        [OperationContract]
        [FaultContract(typeof(ConfigStoreFault))]
        Domain[] EnumerateDomains(long lastDomainID, int maxResults);
    }
}
