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
