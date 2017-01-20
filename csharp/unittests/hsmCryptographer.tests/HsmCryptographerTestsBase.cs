using System.Linq;
using Health.Direct.Common.Extensions;
using Health.Direct.Config.Client;
using Health.Direct.Config.Client.SettingsManager;
using Health.Direct.Config.Store;
using Health.Direct.Hsm;
using Moq;

namespace hsmCryptographer.tests
{
    public class HsmCryptographerTestsBase
    {
        protected static TokenResolverSettings MockTokenResolverSettings(TokenSettings tokensettings)
        {
            var resolverSettings = new TokenResolverSettings();
            var clientSettingsMock = new Mock<ClientSettings>();
            var settingManagerClient = new Mock<IPropertyManager>();
            resolverSettings.ClientSettings = clientSettingsMock.Object;

            Property[] properties = new Property[1];
            properties[0] = new Property("TokenSettings", tokensettings.ToXml());

            settingManagerClient.Setup(c => c.GetProperties(It.Is<string[]>(s => s.Single() == "TokenSettings")))
                .Returns(properties);

            clientSettingsMock.Setup(s => s.CreatePropertyManagerClient())
                .Returns(settingManagerClient.Object);

            return resolverSettings;
        }
    }
}
