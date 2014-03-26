using System.ServiceModel;

using Health.Direct.Config.Store;

namespace Health.Direct.Config.Service
{
    [ServiceContract(Namespace = Service.Namespace)]
    public interface IAuthManager
    {
        [OperationContract]
        [FaultContract(typeof(ConfigStoreFault))]
        Administrator GetUser(string username);

        [OperationContract]
        [FaultContract(typeof(ConfigStoreFault))]
        bool ValidateUser(string username, PasswordHash passwordHash);
    }
}