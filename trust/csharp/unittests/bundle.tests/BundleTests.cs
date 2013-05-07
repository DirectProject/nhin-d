using System.Collections.Generic;
using System.IO;
using System.Xml;
using System.Xml.Schema;
using Xunit;
using Xunit.Extensions;

namespace Health.Direct.Trust
{
    public class BundleTests
    {

        [Fact]
        public void CreateTest()
        {
            Bundle bundle = new Bundle();
            bundle.Create(
                Path.Combine(Directory.GetCurrentDirectory(), @"Certificates\nhind\IncomingAnchors")
                , Path.Combine(Directory.GetCurrentDirectory(), @"TestBundle.p7b")
                );


        }

        [Fact]
        public void CreateTestFull()
        {
            Bundle bundle = new Bundle();
            string[] ignore = new string[] { "Direct.Drhisp.Com Root CAKey.der", "test.p7b" };

            bundle.Create(
                    Path.Combine(Directory.GetCurrentDirectory(), @"C:\nhin-d35\certs\anchors")
                    , Path.Combine(Directory.GetCurrentDirectory(), @"TestBundle.p7b")
                    , ignore
                    );


        }


        [Theory, PropertyData("BadMetadata")]
        public void CreateWithBadMetaDataTest(string metatdata)
        {
            Bundle bundle = new Bundle();
            string[] ignore = null;
            Assert.Throws<XmlException>(() =>
                                        bundle.Create(
                                            Path.Combine(Directory.GetCurrentDirectory(),
                                                         @"Certificates\nhind\IncomingAnchors")
                                            , Path.Combine(Directory.GetCurrentDirectory(), @"TestBundle.p7b")
                                            , ignore
                                            , metatdata
                                            )
                );


        }

        [Theory, PropertyData("InvalidMetadata")]
        public void CreateWithInvalidMetaDataTest(string metatdata)
        {
            Bundle bundle = new Bundle();
            string[] ignore = null;
            Assert.Throws<XmlSchemaValidationException>(() =>
                                        bundle.Create(
                                            Path.Combine(Directory.GetCurrentDirectory(),
                                                         @"Certificates\nhind\IncomingAnchors")
                                            , Path.Combine(Directory.GetCurrentDirectory(), @"TestBundle.p7b")
                                            , ignore
                                            , metatdata
                                            )
                );


        }


        [Fact]
        public void CreateValidMetadataTest()
        {
            Bundle bundle = new Bundle();
            string[] ignore = null;
            bundle.Create(
                Path.Combine(Directory.GetCurrentDirectory(), @"Certificates\nhind\IncomingAnchors")
                , Path.Combine(Directory.GetCurrentDirectory(), @"TestBundle.p7b")
                , ignore
                , @"<TrustBundle><Profile>The Good Guys</Profile><DistributionPoint>http://bundler.lab/testComunity/pack.p7b</DistributionPoint></TrustBundle>"
                );


        }

        public static IEnumerable<object[]> BadMetadata
        {
            get
            {
                // Or this could read from a file. :)
                return new[]
                {
                    new object[] {"s"},
                    new object[] {"</stuff>"},
                    new object[] {"</TrustBundle/>"},
                    new object[] {"<TrustBundle></TrustBundleX>"}
                };
            }
        }


        public static IEnumerable<object[]> InvalidMetadata
        {
            get
            {
                // Or this could read from a file. :)
                return new[]
                {
                    new object[] {"<TrustBundle></TrustBundle>"},
                    new object[] {"<TrustBundle><BakersDozen count='13' /></TrustBundle>"},
                    new object[] {"<TrustBundle><DistributionPoint>http://bundler.lab/testComunity/pack.p7b</DistributionPoint></TrustBundle>"},
                    new object[] {"<TrustBundle><Profile>The Good Guys</Profile></TrustBundle>"}
                };
            }
        }

    }
}
