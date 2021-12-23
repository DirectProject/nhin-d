// using System.Linq;
// using Health.Direct.Common.Extensions;
// using Health.Direct.Config.Client;
// using Health.Direct.Config.Model;
// using Health.Direct.Hsm;
// using Moq;
//
// namespace hsmCryptographer.tests
// {
//     public class HsmCryptographerTestsBase
//     {
//         protected static TokenResolverSettings MockTokenResolverSettings(TokenSettings tokensettings)
//         {
//             var resolverSettings = new TokenResolverSettings();
//             var clientSettingsMock = new Mock<ClientSettings>();
//             var settingManagerClient = new Mock<IPropertyService>();
//             resolverSettings.ClientSettings = clientSettingsMock.Object;
//
//             var property = new Property(){
//                 Name = "TokenSettings", 
//                 Value = tokensettings.ToXml()
//
//             };
//
//             settingManagerClient.Setup(c => c.GetPropertyByName(It.Is<string>(s => s == "TokenSettings")))
//                 .ReturnsAsync(property);
//
//             // clientSettingsMock.Setup(s => s.CreatePropertyManagerClient())
//             //     .Returns(settingManagerClient.Object);
//
//             return resolverSettings;
//         }
//     }
// }
