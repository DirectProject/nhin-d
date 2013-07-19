/* 
 Copyright (c) 2013, Direct Project
 All rights reserved.

 Authors:
    Joe Shook     jshook@kryptiq.com
 
Redistribution and use in source and binary forms, with or without modification, are permitted provided that the following conditions are met:

Redistributions of source code must retain the above copyright notice, this list of conditions and the following disclaimer.
Redistributions in binary form must reproduce the above copyright notice, this list of conditions and the following disclaimer in the documentation and/or other materials provided with the distribution.
Neither the name of The Direct Project (directproject.org) nor the names of its contributors may be used to endorse or promote products derived from this software without specific prior written permission.
THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 
*/

using System;
using System.Collections.Generic;
using System.IO;
using System.Linq;
using System.Security;
using System.Xml;
using System.Xml.Schema;
using Health.Direct.Common.Certificates;
using Xunit;
using Xunit.Extensions;

namespace Health.Direct.Trust.Tests
{
    public class BundlerTests : BaseBundlerTests
    {
        
        [Fact]
        public void CreateBundleTest()
        {
            //Arrange
            Bundler bundle = new Bundler();
            const string outputFileName = @"TestBundle.p7m";

            //Act
            IResourceProvider resourceProvider =
                new FileResourceProvider(
                    Path.Combine(Directory.GetCurrentDirectory()
                    , @"Certificates\nhind\IncomingAnchors")
                    , Path.Combine(Directory.GetCurrentDirectory()
                    , outputFileName));
            byte[] cmsdata = bundle.Create(resourceProvider);

            //Assert (Using agent bundler resolver code)
            Assert.DoesNotThrow(() => resourceProvider.StoreBundle(cmsdata));
            byte[] p7BData = File.ReadAllBytes(Path.Combine(Directory.GetCurrentDirectory(), outputFileName));
            AnchorBundle anchorBundle = null;
            Assert.DoesNotThrow(() => anchorBundle = new AnchorBundle(p7BData));
            Assert.True(!anchorBundle.Certificates.IsNullOrEmpty());
            Assert.Equal(4, anchorBundle.Certificates.Count);
            Assert.Null(anchorBundle.Metadata);
            
            
        }


        [Fact]
        public void CreateBundleWithMetadataTest()
        {
            //Arrange
            Bundler bundle = new Bundler();
            const string outputFileName = @"TestBundleWithMetadata.p7b";

            //Act
            IResourceProvider resourceProvider =
                new FileResourceProvider(Path.Combine(Directory.GetCurrentDirectory()
                                                  , @"Certificates\nhind\IncomingAnchors"),
                                     Path.Combine(Directory.GetCurrentDirectory(), outputFileName)
                                     , null
                                     , @"<TrustBundle><Profile>The Good Guys</Profile><DistributionPoint>http://bundler.lab/testComunity/pack.p7b</DistributionPoint></TrustBundle>");
            byte[] cmsdata = bundle.Create(resourceProvider);

            //Assert (Using agent bundler resolver code)
            Assert.DoesNotThrow(() => resourceProvider.StoreBundle(cmsdata));
            byte[] p7BData = File.ReadAllBytes(Path.Combine(Directory.GetCurrentDirectory(), outputFileName));
            AnchorBundle anchorBundle = null;
            Assert.DoesNotThrow(() => anchorBundle = new AnchorBundle(p7BData));
            Assert.True(!anchorBundle.Certificates.IsNullOrEmpty());
            Assert.Equal(4, anchorBundle.Certificates.Count);
            Assert.NotNull(anchorBundle.Metadata);
            Assert.Equal(@"<TrustBundle><Profile>The Good Guys</Profile><DistributionPoint>http://bundler.lab/testComunity/pack.p7b</DistributionPoint></TrustBundle>", anchorBundle.Metadata);

        }

        

        [Fact]
        public void CreateSignedBundleTest()
        {
            //Arrange
            Bundler bundle = new Bundler();
            const string outputFileName = @"TestBundleWithMetadata.p7m";

            var secString = new SecureString();
            foreach (var secchar in "passw0rd!".ToCharArray())
            {
                secString.AppendChar(secchar);
            }


            //Act
            IResourceProvider resourceProvider =
                new FileResourceProvider(
                    Path.Combine(Directory.GetCurrentDirectory(), @"Certificates\nhind\IncomingAnchors")
                    , Path.Combine(Directory.GetCurrentDirectory(), outputFileName));
            ISignProvider signProvider =
                new FileSignerProvider(
                    Path.Combine(Directory.GetCurrentDirectory(), @"Certificates\redmond\Private\redmond.pfx"),
                    secString);
            byte[] cmsdata = bundle.Create(resourceProvider, signProvider);

            //Assert (Using agent bundler resolver code)
            Assert.DoesNotThrow(() => resourceProvider.StoreBundle(cmsdata));
            byte[] p7BData = File.ReadAllBytes(Path.Combine(Directory.GetCurrentDirectory(), outputFileName));
            AnchorBundle anchorBundle = null;
            Assert.DoesNotThrow(() => anchorBundle = new AnchorBundle(p7BData, true));
            Assert.True(!anchorBundle.Certificates.IsNullOrEmpty());
            Assert.Equal(4, anchorBundle.Certificates.Count);
            Assert.Null(anchorBundle.Metadata);


        }

        /// <summary>
        /// This is not actually implemented.
        /// If there are multiple signatures in the X509Certificate2 only the first will be used.
        /// If there are multiple files in the default Signatures folder only the first is used.
        /// 
        /// Need to look into Countersignature for multiple signing.
        /// Look into CmsSignedDataGenerator.GenerateCounterSigners()
        /// See TestSha1WithRsaCounterSignature in BouncyCastle source
        /// </summary>
        [Fact]
        public void CreateMultiSignedBundleTest()
        {
            //Arrange
            Bundler bundle = new Bundler();
            const string outputFileName = @"TestBundleWithMetadata.p7m";

            var secString = new SecureString();
            foreach (var secchar in "passw0rd!".ToCharArray())
            {
                secString.AppendChar(secchar);
            }


            //Act
            IResourceProvider resourceProvider =
                new FileResourceProvider(
                    Path.Combine(Directory.GetCurrentDirectory(), @"Certificates\nhind\IncomingAnchors")
                    , Path.Combine(Directory.GetCurrentDirectory(), outputFileName));
            ISignProvider signProvider =
                new FileSignerProvider(
                    Path.Combine(Directory.GetCurrentDirectory(), @"Certificates\nhind\Private"),
                    secString);
            byte[] cmsdata = bundle.Create(resourceProvider, signProvider);

            //Assert (Using agent bundler resolver code)
            Assert.DoesNotThrow(() => resourceProvider.StoreBundle(cmsdata));
            byte[] p7BData = File.ReadAllBytes(Path.Combine(Directory.GetCurrentDirectory(), outputFileName));
            AnchorBundle anchorBundle = null;
            Assert.DoesNotThrow(() => anchorBundle = new AnchorBundle(p7BData, true));
            Assert.True(!anchorBundle.Certificates.IsNullOrEmpty());
            Assert.Equal(4, anchorBundle.Certificates.Count);
            Assert.Null(anchorBundle.Metadata);


        }

        [Fact]
        public void CreateSignedBundleIndependentTest()
        {
            //Arrange
            Bundler bundle = new Bundler();
            const string outputFileName = @"TestBundleSignedIndependent.p7m";

            var secString = new SecureString();
            foreach (var secchar in "passw0rd!".ToCharArray())
            {
                secString.AppendChar(secchar);
            }


            //Act
            IResourceProvider resourceProvider =
                new FileResourceProvider(
                    Path.Combine(Directory.GetCurrentDirectory(), @"Certificates\nhind\IncomingAnchors")
                    , Path.Combine(Directory.GetCurrentDirectory(), outputFileName));
            ISignProvider signProvider =
                new FileSignerProvider(
                    Path.Combine(Directory.GetCurrentDirectory(), @"Certificates\redmond\Private\redmond.pfx"),
                    secString);
            byte[] cmsdata = bundle.Create(resourceProvider);
            cmsdata = bundle.Sign(cmsdata, signProvider);

            //Assert (Using agent bundler resolver code)
            Assert.DoesNotThrow(() => resourceProvider.StoreBundle(cmsdata));
            byte[] p7BData = File.ReadAllBytes(Path.Combine(Directory.GetCurrentDirectory(), outputFileName));
            AnchorBundle anchorBundle = null;
            Assert.DoesNotThrow(() => anchorBundle = new AnchorBundle(p7BData, true));
            Assert.True(!anchorBundle.Certificates.IsNullOrEmpty());
            Assert.Equal(4, anchorBundle.Certificates.Count);
            Assert.Null(anchorBundle.Metadata);


        }


        [Fact]
        public void CreateSignedBundleWithMetadataTest()
        {
            //Arrange
            Bundler bundle = new Bundler();
            const string outputFileName = @"TestBundleSignedWithMetadata.p7m";

            var secString = new SecureString();
            foreach (var secchar in "passw0rd!".ToCharArray())
            {
                secString.AppendChar(secchar);
            }

            
            //Act
            IResourceProvider resourceProvider =
                new FileResourceProvider(Path.Combine(Directory.GetCurrentDirectory()
                                                  , @"Certificates\nhind\IncomingAnchors"),
                                     Path.Combine(Directory.GetCurrentDirectory(), outputFileName)
                                     , null
                                     , @"<TrustBundle><Profile>The Good Guys</Profile><DistributionPoint>http://bundler.lab/testComunity/pack.p7b</DistributionPoint></TrustBundle>");
            ISignProvider signProvider =
                new FileSignerProvider(
                    Path.Combine(Directory.GetCurrentDirectory(), @"Certificates\redmond\Private\redmond.pfx"),
                    secString);

            byte[] cmsdata = bundle.Create(resourceProvider, signProvider);

            //Assert (Using agent bundler resolver code)
            Assert.DoesNotThrow(() => resourceProvider.StoreBundle(cmsdata));
            byte[] p7BData = File.ReadAllBytes(Path.Combine(Directory.GetCurrentDirectory(), outputFileName));
            AnchorBundle anchorBundle = null;
            Assert.DoesNotThrow(() => anchorBundle = new AnchorBundle(p7BData, true));
            Assert.True(!anchorBundle.Certificates.IsNullOrEmpty());
            Assert.Equal(4, anchorBundle.Certificates.Count);
            Assert.NotNull(anchorBundle.Metadata);
            Assert.Equal(@"<TrustBundle><Profile>The Good Guys</Profile><DistributionPoint>http://bundler.lab/testComunity/pack.p7b</DistributionPoint></TrustBundle>", anchorBundle.Metadata);

            
        }



        [Theory, PropertyData("BadMetadata")]
        public void CreateWithBadMetaDataTest(string metatdata)
        {
            Bundler bundle = new Bundler();
            Assert.Throws<XmlException>(() =>
                                        bundle.Create(
                                        new FileResourceProvider(Path.Combine(Directory.GetCurrentDirectory()
                                            ,@"Certificates\nhind\IncomingAnchors")
                                            , Path.Combine(Directory.GetCurrentDirectory(), @"TestBundle.p7b")
                                            , null
                                            , metatdata))
                );


        }

        [Theory, PropertyData("InvalidMetadata")]
        public void CreateWithInvalidMetaDataTest(string metatdata)
        {
            Bundler bundle = new Bundler();
            
            Assert.Throws<XmlSchemaValidationException>(() =>
                                        bundle.Create(
                                            new FileResourceProvider(Path.Combine(Directory.GetCurrentDirectory(), @"Certificates\nhind\IncomingAnchors")
                                            , Path.Combine(Directory.GetCurrentDirectory(), @"TestBundle.p7b")
                                            , null
                                            , metatdata))
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
