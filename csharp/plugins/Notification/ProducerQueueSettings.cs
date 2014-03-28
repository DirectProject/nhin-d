/* 
 Copyright (c) 2013, Direct Project
 All rights reserved.

 Authors:
    Joseph Shook    jshook@kryptiq.com
 
Redistribution and use in source and binary forms, with or without modification, are permitted provided that the following conditions are met:

Redistributions of source code must retain the above copyright notice, this list of conditions and the following disclaimer.
Redistributions in binary form must reproduce the above copyright notice, this list of conditions and the following disclaimer in the documentation and/or other materials provided with the distribution.
Neither the name of The Direct Project (directproject.org) nor the names of its contributors may be used to endorse or promote products derived from this software without specific prior written permission.
THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 
*/


using System.Xml.Serialization;
using Health.Direct.Config.Client;

namespace Health.Direct.Plugins.Notification
{
    [XmlType("ProducerQueue")]
    public class ProducerQueueSettings
    {
        /// <summary>
        /// Creates an instance. Normally called through XML deserialization.
        /// </summary>
        public ProducerQueueSettings(){}

        /// <summary>
        /// Using the EnrichManager from config service
        /// </summary>
        /// <remarks>
        /// A custom plugin may choose to talk to a message queue or object database.
        /// </remarks>
        [XmlElement("RetryClientSettings")]
        public ClientSettings RetryClientSettings { get; set; }

        /// <summary>
        /// Consumers use ResourceBase as a root folder or Uri.
        /// </summary>
        /// <remarks>
        /// A custom plugin may use ResourceBase as a base to an object database or connection string.
        /// </remarks>
        public string ResourceBase { get; set; }
    }
}