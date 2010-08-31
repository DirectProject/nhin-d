/* 
 Copyright (c) 2010, NHIN Direct Project
 All rights reserved.

 Authors:
    Chris Lomonico chris.lomonico@surescripts.com
  
Redistribution and use in source and binary forms, with or without modification, are permitted provided that the following conditions are met:

Redistributions of source code must retain the above copyright notice, this list of conditions and the following disclaimer.
Redistributions in binary form must reproduce the above copyright notice, this list of conditions and the following disclaimer in the documentation and/or other materials provided with the distribution.
Neither the name of the The NHIN Direct Project (nhindirect.org). nor the names of its contributors may be used to endorse or promote products derived from this software without specific prior written permission.
THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 
*/
using System;
using System.Collections.Generic;
using System.Linq;
using System.Net;
using System.Net.Sockets;
using System.Text;
using Microsoft.Win32;

using DnsResolver;
using NHINDirect.Caching;
using NHINDirect.Dns;

using Xunit;
using Xunit.Extensions;
namespace NHINDirect.Tests.Caching
{
    public class DnsClientWithCacheFacts : TestingBase, IDisposable
    {

        private readonly DnsClientWithCache m_client;
        private readonly DnsClient m_clientNoCache;

    	const string PublicDns = "8.8.8.8";
        //const string SubnetDns = "192.168.0.1";
        //const string PublicDns = "4.2.2.1";
        //const string LocalDns = "127.0.0.1";

		#region public static IEnumerable<object[]> CertDomainNames
        //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
        /// <Author>Chris Lomonico (mailto:chris.lomonico@surescripts.com)</Author>
        /// <DateCreated>08.31.2010 11:31:02 AM MST</DateCreated>
        /// <TFSItem></TFSItem>
        /// <ReleaseCandidate></ReleaseCandidate>
        /// <summary>
        /// Gets the CertDomainNames of the DnsClientWithCacheFacts
        /// </summary>
        /// <value></value>
        /// <remarks>we're able to resuse these names in TestCert and ResolveCert</remarks>
        //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
        public static IEnumerable<object[]> CertDomainNames
        {
            get
            {
                yield return new[] { "nhind.hsgincubator.com" };
                yield return new[] { "redmond.hsgincubator.com" };
                yield return new[] { "gm2552.securehealthemail.com.hsgincubator.com" };
                yield return new[] { "ses.testaccount.yahoo.com.hsgincubator.com" };
                yield return new[] { "nhin1.rwmn.org.hsgincubator.com" };
                yield return new[] { "nhin.whinit.org.hsgincubator.com" };
            }
        }
		#endregion


        #region public DnsClientWithCacheFacts()
        //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
        /// <Author>Chris Lomonico (mailto:chris.lomonico@surescripts.com)</Author>
        /// <DateCreated>08.30.2010 1:34:41 PM MST</DateCreated>
        /// <TFSItem></TFSItem>
        /// <ReleaseCandidate></ReleaseCandidate>
        /// <summary>
        /// Initializes a new instance of the <b>DnsClientWithCacheFacts</b> class.
        /// </summary>
        /// <remarks></remarks>
        //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
        public DnsClientWithCacheFacts()
        {
            this.m_client = new DnsClientWithCache(PublicDns) { Timeout = 10000 };
            this.m_clientNoCache = new DnsClient(PublicDns) { Timeout = 10000 };
        }
        #endregion

        #region public void Dispose()
        //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
        /// <Author>Chris Lomonico (mailto:chris.lomonico@surescripts.com)</Author>
        /// <DateCreated>08.30.2010 1:34:46 PM MST</DateCreated>
        /// <TFSItem></TFSItem>
        /// <ReleaseCandidate></ReleaseCandidate>
        /// <summary>
        /// Releases the resources used by the <b>DnsClientWithCacheFacts</b>.
        /// </summary>
        /// <remarks></remarks>
        //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
        public void Dispose()
        {
            m_client.Dispose();
        }
        #endregion

        #region public void ResolveAEnsureInCache(string domain)
        //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
        /// <Author>Chris Lomonico (mailto:chris.lomonico@surescripts.com)</Author>
        /// <DateCreated>08.31.2010 10:19:36 AM MST</DateCreated>
        /// <TFSItem></TFSItem>
        /// <ReleaseCandidate></ReleaseCandidate>
        /// <summary>
        /// confirms ANAME records that are resolved with the client w/cache are stored in cache
        /// </summary>
        /// <param name="domain">domain name to be resolved</param>
        /// <remarks></remarks>
        //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
        [Theory]
        [InlineData("www.microsoft.com")]
        [InlineData("www.yahoo.com")]
        [InlineData("www.google.com")]
        [InlineData("www.apple.com")]
        [InlineData("nhind.hsgincubator.com")]
        [InlineData("hvnhind.hsgincubator.com")]
        [InlineData("dns.hsgincubator.com")]
        public void ResolveAEnsureInCache(string domain)
        {
            //----------------------------------------------------------------------------------------------------
            //---try with ResolveCert
            this.Dump(string.Format("Attempting to resolve CERT records for [{0}]", domain));
            IEnumerable<DnsResolver.AddressRecord> results = this.m_client.ResolveA(domain);
            Assert.True(results != null, domain);
            DnsResponse res = this.m_client.Cache.Get(new DnsQuestion(domain
                , DnsResolver.Dns.RecordType.ANAME));
            this.Dump("ensuring item is stored in cache");
            Assert.NotNull(res);
            
            

        }
        #endregion

        #region public void ResolveCertEnsureInCache(string domain)
        //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
        /// <Author>Chris Lomonico (mailto:chris.lomonico@surescripts.com)</Author>
        /// <DateCreated>08.30.2010 1:35:11 PM MST</DateCreated>
        /// <TFSItem></TFSItem>
        /// <ReleaseCandidate></ReleaseCandidate>
        /// <summary>
        /// confirms ability of code to resolve certs using the dns caching client method ResolveCERT, ensures items are in cache
        /// </summary>
        /// <param name="domain">domain name to be resolved</param>
        /// <remarks></remarks>
        //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
        [Theory]
        [PropertyData("CertDomainNames")]
        public void ResolveCertEnsureInCache(string domain)
        {
            //----------------------------------------------------------------------------------------------------
            //---try with ResolveCert
            this.Dump(string.Format("Attempting to resolve CERT records for [{0}]", domain));
            IEnumerable<DnsResolver.CertRecord> results = this.m_client.ResolveCERT(domain);
            Assert.True(results != null, domain);
            this.Dump("ensuring item is stored in cache");
            DnsResponse res = this.m_client.Cache.Get(new DnsQuestion(domain
                , DnsResolver.Dns.RecordType.CERT));
            Assert.NotNull(res);



        }
        #endregion

        #region public void ResolveCERTFromNameServerEnsureInCache(string domain)
        //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
        /// <Author>Chris Lomonico (mailto:chris.lomonico@surescripts.com)</Author>
        /// <DateCreated>08.30.2010 1:36:00 PM MST</DateCreated>
        /// <TFSItem></TFSItem>
        /// <ReleaseCandidate></ReleaseCandidate>
        /// <summary>
        /// confirms ability of code to resolve certs using the dns caching client method ResolveCERT
        /// </summary>
        /// <param name="domain">domain name to be resolved</param>
        /// <remarks></remarks>
        //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
        [Theory]
        [PropertyData("CertDomainNames")]
        public void ResolveCERTFromNameServerEnsureInCache(string domain)
        {
            //----------------------------------------------------------------------------------------------------
            //---try with ResolveCert
            this.Dump(string.Format("Attempting to resolve CERT records for [{0}]", domain));
            IEnumerable<DnsResolver.CertRecord> results = this.m_client.ResolveCERTFromNameServer(domain);
            Assert.True(results != null, domain);
            this.Dump("ensuring item is stored in cache");
            DnsResponse res = this.m_client.Cache.Get(new DnsQuestion(domain
                , DnsResolver.Dns.RecordType.CERT));
            Assert.NotNull(res);
        }
        #endregion

        #region public void ResolveMXEnsureInCache(string domain)
        //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
        /// <Author>Chris Lomonico (mailto:chris.lomonico@surescripts.com)</Author>
        /// <DateCreated>08.31.2010 10:59:29 AM MST</DateCreated>
        /// <TFSItem></TFSItem>
        /// <ReleaseCandidate></ReleaseCandidate>
        /// <summary>
        /// confirms ability to resolve and cache mx records
        /// </summary>
        /// <param name="domain">domain name to be resolved</param>
        /// <remarks></remarks>
        //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
        [Theory]
        [InlineData("nhind.hsgincubator.com")]
        [InlineData("redmond.hsgincubator.com")]
        [InlineData("www.microsoft.com")]
        public void ResolveMXEnsureInCache(string domain)
        {
            //----------------------------------------------------------------------------------------------------
            //---try with ResolveCert
            this.Dump(string.Format("Attempting to resolve MX records for [{0}]", domain));
            IEnumerable<DnsResolver.MXRecord> results = this.m_client.ResolveMX(domain);
            this.Dump("ensuring that results were returned");
            Assert.True(results != null, domain);
            DnsResponse res = this.m_client.Cache.Get(new DnsQuestion(domain
                , DnsResolver.Dns.RecordType.MX));
            this.Dump("ensuring item is stored in cache");
            Assert.NotNull(res);

        }
        #endregion
        
        #region public void ResolveTXTEnsureInCache(string domain)
        //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
        /// <Author>Chris Lomonico (mailto:chris.lomonico@surescripts.com)</Author>
        /// <DateCreated>08.31.2010 10:59:29 AM MST</DateCreated>
        /// <TFSItem></TFSItem>
        /// <ReleaseCandidate></ReleaseCandidate>
        /// <summary>
        /// confirms ability to resolve and cache TXT records
        /// </summary>
        /// <param name="domain">domain name to be resolved</param>
        /// <remarks></remarks>
        //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
        [Theory]
        [InlineData("www.microsoft.com")]
        [InlineData("www.yahoo.com")]
        [InlineData("www.google.com")]
        [InlineData("www.apple.com")]
        public void ResolveTXTEnsureInCache(string domain)
        {
            //----------------------------------------------------------------------------------------------------
            //---try with ResolveCert
            this.Dump(string.Format("Attempting to resolve MX records for [{0}]", domain));
            IEnumerable<DnsResolver.TextRecord> results = this.m_client.ResolveTXT(domain);
            this.Dump("ensuring that results were returned");
            Assert.True(results != null, domain);
            DnsResponse res = this.m_client.Cache.Get(new DnsQuestion(domain
                , DnsResolver.Dns.RecordType.TXT));
            this.Dump("ensuring item is stored in cache");
            Assert.NotNull(res);

        }
        #endregion


        #region public void ResolvePTREnsureInCache(string domain)
        //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
        /// <Author>Chris Lomonico (mailto:chris.lomonico@surescripts.com)</Author>
        /// <DateCreated>08.31.2010 10:59:29 AM MST</DateCreated>
        /// <TFSItem></TFSItem>
        /// <ReleaseCandidate></ReleaseCandidate>
        /// <summary>
        /// confirms ability to resolve and cache PTR records
        /// </summary>
        /// <param name="domain">domain name to be resolved</param>
        /// <remarks></remarks>
        //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
        [Theory]
        [InlineData("www.microsoft.com")]
        [InlineData("www.yahoo.com")]
        [InlineData("www.google.com")]
        [InlineData("www.apple.com")]
        public void ResolvePTREnsureInCache(string domain)
        {
            //----------------------------------------------------------------------------------------------------
            //---try with ResolveCert
            this.Dump(string.Format("Attempting to resolve MX records for [{0}]", domain));
            IEnumerable<DnsResolver.PtrRecord> results = this.m_client.ResolvePTR(domain);
            this.Dump("ensuring that results were returned");
            Assert.True(results != null, domain);
            DnsResponse res = this.m_client.Cache.Get(new DnsQuestion(domain
                , DnsResolver.Dns.RecordType.PTR));
            this.Dump("ensuring item is stored in cache");
            Assert.NotNull(res);

        }
        #endregion

        #region public void ResolveNSEnsureInCache(string domain)
        //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
        /// <Author>Chris Lomonico (mailto:chris.lomonico@surescripts.com)</Author>
        /// <DateCreated>08.31.2010 10:59:29 AM MST</DateCreated>
        /// <TFSItem></TFSItem>
        /// <ReleaseCandidate></ReleaseCandidate>
        /// <summary>
        /// confirms ability to resolve and cache NS records
        /// </summary>
        /// <param name="domain">domain name to be resolved</param>
        /// <remarks></remarks>
        //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
        [Theory]
        [InlineData("www.microsoft.com")]
        [InlineData("www.yahoo.com")]
        [InlineData("www.google.com")]
        [InlineData("www.apple.com")]
        public void ResolveNSEnsureInCache(string domain)
        {
            //----------------------------------------------------------------------------------------------------
            //---try with ResolveCert
            this.Dump(string.Format("Attempting to resolve MX records for [{0}]", domain));
            IEnumerable<DnsResolver.NSRecord> results = this.m_client.ResolveNS(domain);
            this.Dump("ensuring that results were returned");
            Assert.True(results != null, domain);
            DnsResponse res = this.m_client.Cache.Get(new DnsQuestion(domain
                , DnsResolver.Dns.RecordType.NS));
            this.Dump("ensuring item is stored in cache");
            Assert.NotNull(res);

        }
        #endregion


        #region public void ResolveSOAEnsureInCache(string domain)
        //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
        /// <Author>Chris Lomonico (mailto:chris.lomonico@surescripts.com)</Author>
        /// <DateCreated>08.31.2010 10:59:29 AM MST</DateCreated>
        /// <TFSItem></TFSItem>
        /// <ReleaseCandidate></ReleaseCandidate>
        /// <summary>
        /// confirms ability to resolve and cache SOA records
        /// </summary>
        /// <param name="domain">domain name to be resolved</param>
        /// <remarks></remarks>
        //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
        [Theory]
        [InlineData("www.microsoft.com")]
        [InlineData("www.yahoo.com")]
        [InlineData("www.google.com")]
        [InlineData("www.apple.com")]
        public void ResolveSOAEnsureInCache(string domain)
        {
            //----------------------------------------------------------------------------------------------------
            //---try with ResolveCert
            this.Dump(string.Format("Attempting to resolve MX records for [{0}]", domain));
            IEnumerable<DnsResolver.SOARecord> results = this.m_client.ResolveSOA(domain);
            this.Dump("ensuring that results were returned");
            Assert.True(results != null, domain);
            DnsResponse res = this.m_client.Cache.Get(new DnsQuestion(domain
                , DnsResolver.Dns.RecordType.SOA));
            this.Dump("ensuring item is stored in cache");
            Assert.NotNull(res);

        }
        #endregion

    }
}
