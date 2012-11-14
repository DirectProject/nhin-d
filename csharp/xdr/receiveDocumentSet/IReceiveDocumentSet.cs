using System.ServiceModel;
using System.ServiceModel.Channels;

namespace Health.Direct.Xdr
{
    [ServiceContract(Namespace = "urn:ihe:iti:xds-b:2007", Name = "XDSRepository")]
    public interface IReceiveDocumentSet
    {
        [OperationContract(Action = "urn:ihe:iti:2007:ProvideAndRegisterDocumentSet-b",
            ReplyAction = "urn:ihe:iti:2007:ProvideAndRegisterDocumentSet-bResponse")]
        Message ReceiveDocumentSet(Message input);

    }
}