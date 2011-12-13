using System.Net.Mail;
using System.Security.Cryptography.X509Certificates;
using Health.Direct.Common.Certificates;
using Health.Direct.Common.Container;

namespace Health.Direct.ResolverPlugins.Tests.Fakes
{
    public class DnsFakeResolver : ICertificateResolver , IPlugin
    {
        public X509Certificate2Collection GetCertificates(MailAddress address)
        {
            return null;
        }

        public X509Certificate2Collection GetCertificatesForDomain(string domain)
        {
            return null;
        }

        public void Init(PluginDefinition pluginDef)
        {
            //noop
        }
    }
}
