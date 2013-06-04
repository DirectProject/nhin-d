using System.Collections;
using System.Management.Automation;
using System.Security;
using Health.Direct.Common.Certificates;
using Health.Direct.Trust.Commandlet;
using Xunit;

namespace Health.Direct.Trust.Tests
{
    public class Trustbundle
    {
        [Fact]
        public void CreateCmdLetTest()
        {
            BundleAnchorsCommand cmd = new BundleAnchorsCommand();
            Assert.IsAssignableFrom<Cmdlet>(cmd);

            SignBundleCommand signCmd = new SignBundleCommand();
            Assert.IsAssignableFrom<Cmdlet>(signCmd);
        }

        [Fact]
        public void CreateBundleTest()
        {
            BundleAnchorsCommand cmd = new BundleAnchorsCommand();
            string[] ignoreArray = new string[] {"Direct.Drhisp.Com Root CAKey.der"};
            cmd.Name = @".\Certificates\nhind\IncomingAnchors";
            cmd.Ignore = ignoreArray;

            IEnumerator result = cmd.Invoke().GetEnumerator();
            result.MoveNext();
            byte[] cmsdata = (byte [])result.Current;


            //Assert (Using agent bundler resolver code)
            AnchorBundle anchorBundle = null;
            Assert.DoesNotThrow(() => anchorBundle = new AnchorBundle(cmsdata));
            Assert.True(!anchorBundle.Certificates.IsNullOrEmpty());
            Assert.Equal(4, anchorBundle.Certificates.Count);
            Assert.Null(anchorBundle.Metadata);
        }


        [Fact]
        public void CreateBundleWithMetadataTest()
        {
            BundleAnchorsCommand cmd = new BundleAnchorsCommand();
            string[] ignoreArray = new string[] { "Direct.Drhisp.Com Root CAKey.der" };
            cmd.Name = @".\Certificates\nhind\IncomingAnchors";
            cmd.Ignore = ignoreArray;
            cmd.Metadata = @"<TrustBundle><Profile>The Good Guys</Profile><DistributionPoint>http://bundler.lab/testComunity/pack.p7b</DistributionPoint></TrustBundle>";

            IEnumerator result = cmd.Invoke().GetEnumerator();
            result.MoveNext();
            byte[] cmsdata = (byte[])result.Current;


            //Assert (Using agent bundler resolver code)
            AnchorBundle anchorBundle = null;
            Assert.DoesNotThrow(() => anchorBundle = new AnchorBundle(cmsdata));
            Assert.True(!anchorBundle.Certificates.IsNullOrEmpty());
            Assert.Equal(4, anchorBundle.Certificates.Count);
            Assert.Equal(@"<TrustBundle><Profile>The Good Guys</Profile><DistributionPoint>http://bundler.lab/testComunity/pack.p7b</DistributionPoint></TrustBundle>", anchorBundle.Metadata);

        }


        [Fact]
        public void CreateSigneBundleTest()
        {
            BundleAnchorsCommand cmd = new BundleAnchorsCommand();
            string[] ignoreArray = new string[] { "Direct.Drhisp.Com Root CAKey.der" };
            cmd.Name = @".\Certificates\nhind\IncomingAnchors";
            cmd.Ignore = ignoreArray;

            IEnumerator result = cmd.Invoke().GetEnumerator();
            result.MoveNext();
           

            SignBundleCommand signCmd = new SignBundleCommand();
            
            var secString = new SecureString();
            foreach (var secchar in "passw0rd!".ToCharArray())
            {
                secString.AppendChar(secchar);
            }
            signCmd.Name = @".\Certificates\redmond\Private\redmond.pfx";
            signCmd.PassKey = secString;
            signCmd.Bundle = (byte[])result.Current;

            result = signCmd.Invoke().GetEnumerator();
            result.MoveNext();
            byte[] signedCmsdata = (byte[])result.Current;

            //Assert (Using agent bundler resolver code)
            AnchorBundle anchorBundle = null;
            Assert.DoesNotThrow(() => anchorBundle = new AnchorBundle(signedCmsdata, true));
            Assert.True(!anchorBundle.Certificates.IsNullOrEmpty());
            Assert.Equal(4, anchorBundle.Certificates.Count);
            Assert.Null(anchorBundle.Metadata);
        }


        [Fact]
        public void CreateSignedBundleWithMetadataTest()
        {
           
            BundleAnchorsCommand cmd = new BundleAnchorsCommand();
            string[] ignoreArray = new string[] { "Direct.Drhisp.Com Root CAKey.der" };
            cmd.Name = @".\Certificates\nhind\IncomingAnchors";
            cmd.Ignore = ignoreArray;
            cmd.Metadata = @"<TrustBundle><Profile>The Good Guys</Profile><DistributionPoint>http://bundler.lab/testComunity/pack.p7b</DistributionPoint></TrustBundle>";

            IEnumerator result = cmd.Invoke().GetEnumerator();
            result.MoveNext();


            SignBundleCommand signCmd = new SignBundleCommand();

            var secString = new SecureString();
            foreach (var secchar in "passw0rd!".ToCharArray())
            {
                secString.AppendChar(secchar);
            }
            signCmd.Name = @".\Certificates\redmond\Private\redmond.pfx";
            signCmd.PassKey = secString;
            signCmd.Bundle = (byte[])result.Current;

            result = signCmd.Invoke().GetEnumerator();
            result.MoveNext();
            byte[] signedCmsdata = (byte[])result.Current;

            

            //Assert (Using agent bundler resolver code)
            AnchorBundle anchorBundle = null;
            Assert.DoesNotThrow(() => anchorBundle = new AnchorBundle(signedCmsdata, true));
            Assert.True(!anchorBundle.Certificates.IsNullOrEmpty());
            Assert.Equal(4, anchorBundle.Certificates.Count);
            Assert.Equal(@"<TrustBundle><Profile>The Good Guys</Profile><DistributionPoint>http://bundler.lab/testComunity/pack.p7b</DistributionPoint></TrustBundle>", anchorBundle.Metadata);

        }
    }
}
