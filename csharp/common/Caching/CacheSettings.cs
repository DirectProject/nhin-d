/* 
 Copyright (c) 2011, Direct Project
 All rights reserved.

 Authors:    
    Ali Emami       aliemami@microsoft.com
  
Redistribution and use in source and binary forms, with or without modification, are permitted provided that the following conditions are met:

Redistributions of source code must retain the above copyright notice, this list of conditions and the following disclaimer.
Redistributions in binary form must reproduce the above copyright notice, this list of conditions and the following disclaimer in the documentation and/or other materials provided with the distribution.
Neither the name of The Direct Project (directproject.org) nor the names of its contributors may be used to endorse or promote products derived from this software without specific prior written permission.
THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 
*/

using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Xml.Serialization;

namespace Health.Direct.Common.Caching
{
    /// <summary>
    /// Represents cache settings that can be serialized to XML.
    /// </summary>
    [XmlType]
    public class CacheSettings
    {
        /// <summary>
        /// Initializes a new instance of CacheSettings.
        /// </summary>
        public CacheSettings()
        {
        }

        /// <summary>
        /// Initializes a new instance of CacheSettings from the specified instance. 
        /// </summary>        
        public CacheSettings(CacheSettings settings)
        {
            if (settings != null)
            {
                Name = settings.Name;
                Cache = settings.Cache;
                NegativeCache = settings.NegativeCache;
                CacheTTLSeconds = settings.CacheTTLSeconds;
            }
        }

        /// <summary>
        /// The unique name of the cache.
        /// </summary>
        [XmlIgnore]
        public string Name
        {
            get;
            set;
        }

        /// <summary>
        /// true if caching is enabled. 
        /// </summary>
        [XmlElement]
        public bool Cache
        {
            get;
            set;
        }

        /// <summary>
        /// true if negative caching is enabled. 
        /// This is performed when an item is not found (ie. in a data store), and this
        /// fact should be cached. The TTL property affects this type of cahing as well.
        /// </summary>
        [XmlElement]
        public bool NegativeCache
        {
            get;
            set;
        }

        /// <summary>
        /// The time in seconds to cache results. 
        /// </summary>
        [XmlElement]
        public int? CacheTTLSeconds
        {
            get;
            set;
        }

        /// <summary>
        /// Validates the settings.
        /// </summary>
        public void Validate()
        {
            if (!Cache && NegativeCache)
            {
                throw new InvalidOperationException("NegativeCacheWithCacheDisabled"); 
            }

            if (CacheTTLSeconds.HasValue && CacheTTLSeconds <= 0)
            {
                throw new ArgumentOutOfRangeException("CacheTTLSeconds");
            }
        }
    }
}
