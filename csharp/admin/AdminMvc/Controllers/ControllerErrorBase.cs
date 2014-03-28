using System.Web.Mvc;

using Health.Direct.Config.Store;

namespace Health.Direct.Admin.Console.Controllers
{
    [HandleError(View = "Error")]
    [HandleError(ExceptionType = typeof(System.ServiceModel.EndpointNotFoundException), View = "ServiceDownError")]
    [HandleError(ExceptionType = typeof(System.ServiceModel.FaultException<ConfigStoreFault>), View = "ConfigStoreError")]
    public class ControllerErrorBase : Controller
    {
    }
}