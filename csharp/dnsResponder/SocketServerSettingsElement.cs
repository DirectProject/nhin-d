/* 
 Copyright (c) 2010, Direct Project
 All rights reserved.

 Authors:
    Chris Lomonico  chris.lomonico@surescripts.com
 
Redistribution and use in source and binary forms, with or without modification, are permitted provided that the following conditions are met:

Redistributions of source code must retain the above copyright notice, this list of conditions and the following disclaimer.
Redistributions in binary form must reproduce the above copyright notice, this list of conditions and the following disclaimer in the documentation and/or other materials provided with the distribution.
Neither the name of The Direct Project (directproject.org) nor the names of its contributors may be used to endorse or promote products derived from this software without specific prior written permission.
THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 
*/
using System;
using System.Net.Sockets;
using System.Configuration;

namespace Health.Direct.DnsResponder
{
    public class SocketServerSettingsElement : ConfigurationElement
    {

        public SocketServerSettingsElement()
        {
        }

        public SocketServerSettings AsSocketServerSettings()
        {
            SocketServerSettings settings = new SocketServerSettings();
            settings.MaxOutstandingAccepts = this.MaxOutstandingAccepts;
            settings.MaxConnectionBacklog = this.MaxConnectionBacklog;
            settings.MaxActiveRequests = this.MaxActiveRequests;
            settings.ReadBufferSize = this.ReadBufferSize;
            settings.SendTimeout = this.SendTimeout;
            settings.ReceiveTimeout = this.ReceiveTimeout;
            settings.SocketCloseTimeout = this.SocketCloseTimeout;
            return settings;
        }

        /// <summary>
        /// Have these many asynchronous "Accept" or Receive calls already in place, so that actual request/connect acceptance 
        /// does not become a bottleneck
        /// </summary>
        [ConfigurationProperty("MaxOutstandingAccepts", DefaultValue = SocketServerSettings.DefaultMaxOutstandingAccepts, IsRequired = false)]
        public short MaxOutstandingAccepts
        {
            get
            {
                return (short)this["MaxOutstandingAccepts"];
            }
            set
            {
                if (value <= 0)
                {
                    throw new ArgumentException();
                }

                this["MaxOutstandingAccepts"] = value;
            }
        }

        /// <summary>
        /// Connections not already accepted are Queued automatically by Winsock until they can be accepted by Winsock
        /// Set this to some reasonable limit between 20-200. Note: if you specify too high a number, .NET will automatically
        /// constrain the # based on OS restrictions. 
        /// </summary>
        [ConfigurationProperty("MaxConnectionBacklog", DefaultValue = SocketServerSettings.DefaultMaxConnectionBacklog, IsRequired = false)]
        public short MaxConnectionBacklog
        {
            get
            {
                return (short)this["MaxConnectionBacklog"];
            }
            set
            {
                if (value <= 0)
                {
                    throw new ArgumentException();
                }
                this["MaxConnectionBacklog"] = value;
            }
        }

        /// <summary>
        /// Max requests you simultaneously want to handle. The socket server will automatically impose this limit
        /// </summary>
        [ConfigurationProperty("MaxActiveRequests", DefaultValue = SocketServerSettings.DefaultMaxActiveRequests, IsRequired = false)]
        public short MaxActiveRequests
        {
            get
            {
                return (short)this["MaxActiveRequests"];
            }
            set
            {
                if (value <= 0)
                {
                    throw new ArgumentException();
                }

                this["MaxActiveRequests"] = value;
            }
        }

        [ConfigurationProperty("ReadBufferSize", DefaultValue = SocketServerSettings.DefaultReadBufferSize, IsRequired = false)]
        public short ReadBufferSize
        {
            get
            {
                return (short)this["ReadBufferSize"];
            }
            set
            {
                if (value <= 0)
                {
                    throw new ArgumentException();
                }

                this["ReadBufferSize"] = value;
            }
        }

        //--------------------------------------
        //
        // Self explanatory Socket Timeouts
        //
        //--------------------------------------
        [ConfigurationProperty("SendTimeout",  IsRequired = false)]
        public int SendTimeout
        {
            get
            {
                return (int)this["SendTimeout"];
            }
            set
            {
                this["SendTimeout"] = value;
            }
        }

        [ConfigurationProperty("ReceiveTimeout",  IsRequired = false)]
        public int ReceiveTimeout
        {
            get
            {
                return (int)this["ReceiveTimeout"];
            }
            set
            {
                this["ReceiveTimeout"] = value;
            }
        }

        [ConfigurationProperty("SocketCloseTimeout", IsRequired = false)]
        public int SocketCloseTimeout
        {
            get
            {
                return (int)this["SocketCloseTimeout"];
            }
            set
            {
                this["SocketCloseTimeout"] = value;
            }
        }

    }
}