/* 
 Copyright (c) 2010, NHIN Direct Project
 All rights reserved.

 Authors:
    Chris Lomonico  chris.lomonico@surescripts.com
 
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
using System.Text;

using DnsResolver;
using NHINDirect.Caching;

namespace NHINDirect.Dns
{
    public class DnsClientWithCache : DnsClient
    {

        protected DnsResponseCache m_cache = null;

        #region public DnsResponseCache Cache
        //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
        /// <Author>Chris Lomonico (mailto:chris.lomonico@surescripts.com)</Author>
        /// <DateCreated>08.31.2010 10:11:17 AM MST</DateCreated>
        /// <TFSItem></TFSItem>
        /// <ReleaseCandidate></ReleaseCandidate>
        /// <summary>
        /// Gets the Cache of the DnsClientWithCache
        /// </summary>
        /// <value></value>
        /// <remarks></remarks>
        //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
        public DnsResponseCache Cache
        {
            get
            {
                return this.m_cache;
            }
        }
        #endregion

        #region public DnsClientWithCache(string server)
        //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
        /// <Author>???</Author>
        /// <AdpatedBy>Chris Lomonico (mailto:chris.lomonico@surescripts.com)</AdpatedBy>
        /// <DateCreated>08.27.2010 9:16:47 AM MST</DateCreated>
        /// <TFSItem></TFSItem>
        /// <ReleaseCandidate></ReleaseCandidate>
        /// <summary>
        ///   Creates a DnsClient instance specifying a string representation of the IP address and using the default DNS port.
        /// </summary>
        /// <param name="server">
        /// A string representation of the DNS server IP address.
        /// </param>
        /// <example><c>var client = new DnsClientWithCache("8.8.8.8");</c></example>
        /// <remarks></remarks>
        //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
        public DnsClientWithCache(string server)
            : base(server)
        {
            this.Initialize();
        }
        #endregion

        #region public DnsClientWithCache(string server, int port)
        //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
        /// <Author>???</Author>
        /// <AdpatedBy>Chris Lomonico (mailto:chris.lomonico@surescripts.com)</AdpatedBy>
        /// <DateCreated>08.27.2010 9:17:02 AM MST</DateCreated>
        /// <TFSItem></TFSItem>
        /// <ReleaseCandidate></ReleaseCandidate>
        /// <summary>
        ///   Creates a DnsClient instance specifying the DNS port.
        /// </summary>
        /// <param name="server">
        /// A string representation of the DNS server IP address.
        /// </param>
        /// <param name="port">
        /// The port to use for the DNS requests.
        /// </param>
        /// <example><c>var client = new DnsClientWithCache("8.8.8.8", 8888);</c></example>
        /// <remarks></remarks>
        //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
        public DnsClientWithCache(string server, int port)
            : base(server, port)
        {
            this.Initialize();
        }
        #endregion

        #region public DnsClientWithCache(IPAddress server)
        //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
        /// <Author>???</Author>
        /// <AdpatedBy>Chris Lomonico (mailto:chris.lomonico@surescripts.com)</AdpatedBy>
        /// <DateCreated>08.27.2010 9:17:06 AM MST</DateCreated>
        /// <TFSItem></TFSItem>
        /// <ReleaseCandidate></ReleaseCandidate>
        /// <summary>
        /// Creates a DnsClient instance specifying an IPAddress representation of the IP address.
        /// </summary>
        /// <param name="server">
        /// The IPAddress of the DNS server. A <see cref="IPAddress"/>
        /// </param>
        /// <remarks></remarks>
        //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
        public DnsClientWithCache(IPAddress server)
            : base(server)
        {
            this.Initialize();
        }
        #endregion

        #region public DnsClientWithCache(IPEndPoint server)
        //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
        /// <Author>???</Author>
        /// <AdpatedBy>Chris Lomonico (mailto:chris.lomonico@surescripts.com)</AdpatedBy>
        /// <DateCreated>08.27.2010 9:17:09 AM MST</DateCreated>
        /// <TFSItem></TFSItem>
        /// <ReleaseCandidate></ReleaseCandidate>
        /// <summary>
        /// Creates a DnsClient instance specifying an IPEndPoint representation of the IP address and port.
        /// </summary>
        /// <param name="server">
        /// A <see cref="IPEndPoint"/>
        /// </param>
        /// <remarks></remarks>
        //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
        public DnsClientWithCache(IPEndPoint server)
            : base(server)
        {
            this.Initialize();
        }
        #endregion

        #region public DnsClientWithCache(IPEndPoint server , int timeoutMillis , int maxBufferSize)
        //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
        /// <Author>???</Author>
        /// <AdpatedBy>Chris Lomonico (mailto:chris.lomonico@surescripts.com)</AdpatedBy>
        /// <DateCreated>08.27.2010 9:17:12 AM MST</DateCreated>
        /// <TFSItem></TFSItem>
        /// <ReleaseCandidate></ReleaseCandidate>
        /// <summary>
        /// Creates a DnsClient instance specifying an IPEndPoint representation of the IP address and port and 
        /// specifying the timeout and buffer size to use.
        /// </summary>
        /// <param name="server">
        /// A <see cref="IPEndPoint"/>
        /// </param>
        /// <param name="timeoutMillis">
        /// Timeout in milliseconds.
        /// </param>
        /// <param name="maxBufferSize">
        /// Maximum buffer size.
        /// </param>
        /// <remarks></remarks>
        //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
        public DnsClientWithCache(IPEndPoint server
            , int timeoutMillis
            , int maxBufferSize)
            : base(server, timeoutMillis, maxBufferSize)
        {
            this.Initialize();
        }
        #endregion

        #region protected virtual void Initialize()
        //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
        /// <Author>Chris Lomonico (mailto:chris.lomonico@surescripts.com)</Author>
        /// <DateCreated>08.30.2010 12:49:10 PM MST</DateCreated>
        /// <TFSItem></TFSItem>
        /// <ReleaseCandidate></ReleaseCandidate>
        /// <summary>
        /// initializes any object(s) utilized by an instance of this class
        /// </summary>
        /// <remarks>if more is needed here, saves having to update each item in the constructors</remarks>
        //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
        protected virtual void Initialize()
        {
            m_cache = new DnsResponseCache();

        }
        #endregion

        #region public override DnsResponse Resolve(DnsRequest request)
        //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
        /// <Author>Chris Lomonico (mailto:chris.lomonico@surescripts.com)</Author>
        /// <DateCreated>08.30.2010 4:23:04 PM MST</DateCreated>
        /// <TFSItem></TFSItem>
        /// <ReleaseCandidate></ReleaseCandidate>
        /// <summary>
        /// overrides base resolve method to provide cache funcationality at time of resolution
        /// </summary>
        /// <param name="request"></param>
        /// <returns>DnsResponse containing the results either pulled from cache or manually resolved</returns>
        /// <remarks></remarks>
        //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
        public override DnsResponse Resolve(DnsRequest request)
        {
            //----------------------------------------------------------------------------------------------------
            //---check to see if the item is in the cache
            DnsResponse dr = this.m_cache.Get(request);
            if (dr != null)
            {
#if DEBUG
                Console.WriteLine("{0} - [{1}] found in cache"
                    , DateTime.UtcNow.ToString("mm:ss:ff")
                    , this.m_cache.BuildKey(request.Question));
#endif
                return dr;
            }
            //----------------------------------------------------------------------------------------------------
            //---item as not found in cache, try to get it from the base class
            dr = base.Resolve(request);
            if (dr != null)
            {
                //----------------------------------------------------------------------------------------------------
                //---if found store in the cache for future use
                this.m_cache.Put(dr);
            }
            return dr;
        }
        #endregion

        #region public override IEnumerable<CertRecord> ResolveCERTFromNameServer(string domain, IPAddress nameserver)
        //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
        /// <Author>Chris Lomonico (mailto:chris.lomonico@surescripts.com)</Author>
        /// <DateCreated>08.31.2010 10:27:43 AM MST</DateCreated>
        /// <TFSItem></TFSItem>
        /// <ReleaseCandidate></ReleaseCandidate>
        /// <summary>
        /// override base method to use DnsClientWithCache passing in specific name server
        /// </summary>
        /// <param name="domain">The domain name to resolve.</param>
        /// <param name="nameserver">Nameserver to use during resolution</param>
        /// <returns></returns>
        /// <remarks></remarks>
        //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
        public override IEnumerable<CertRecord> ResolveCERTFromNameServer(string domain, IPAddress nameserver)
        {
            if (string.IsNullOrEmpty(domain) || nameserver == null)
            {
                throw new ArgumentException();
            }

            using (DnsClientWithCache client = new DnsClientWithCache(nameserver))
            {
                client.UseUDPFirst = false;
                return client.ResolveCERT(domain);
            }
        }
        #endregion

    }
}
