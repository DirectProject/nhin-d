/* 
 Copyright (c) 2010, Direct Project
 All rights reserved.

 Authors:
    Umesh Madan     umeshma@microsoft.com
 
Redistribution and use in source and binary forms, with or without modification, are permitted provided that the following conditions are met:

Redistributions of source code must retain the above copyright notice, this list of conditions and the following disclaimer.
Redistributions in binary form must reproduce the above copyright notice, this list of conditions and the following disclaimer in the documentation and/or other materials provided with the distribution.
Neither the name of The Direct Project (directproject.org) nor the names of its contributors may be used to endorse or promote products derived from this software without specific prior written permission.
THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 
*/
using System;

namespace Health.Direct.DnsResponder
{
    public class DnsServer
    {
        public event Action<Exception> Error;

        readonly DnsServerSettings m_settings;
        readonly IDnsStore m_store;

        readonly DnsResponderTCP m_tcpResponder;
        readonly DnsResponderUDP m_udpResponder;
        
        public DnsServer(IDnsStore store, DnsServerSettings settings)
        {
            if (store == null || settings == null)
            {
                throw new ArgumentNullException();
            }

            m_settings = settings;
            m_store = store;

            m_tcpResponder = new DnsResponderTCP(this.Store, this.Settings);
            m_udpResponder = new DnsResponderUDP(this.Store, this.Settings);

            m_tcpResponder.Server.Error += InvokeError;
            m_udpResponder.Server.Error += InvokeError;
        }

        private void InvokeError(Exception ex)
        {
            this.Error.SafeInvoke(ex);
        }

        public DnsServerSettings Settings
        {
            get
            {
                return m_settings;
            }
        }
                
        public IDnsStore Store
        {
            get
            {
                return m_store;
            }
        }
        
        public DnsResponderTCP TCPResponder
        {
            get
            {
                return m_tcpResponder;
            }
        }
        
        public DnsResponderUDP UDPResponder
        {
            get
            {
                return m_udpResponder;
            }
        }
        
        public void Start()
        {
            m_udpResponder.Start();
            m_tcpResponder.Start();
        }
        
        public void Stop()
        {
            try
            {
                m_udpResponder.Stop();
            }
            catch (Exception ex)
            {
                InvokeError(ex);
            }

            try
            {
                m_tcpResponder.Stop();
            }
            catch (Exception ex)
            {
                InvokeError(ex);
            }
        }
    }
}