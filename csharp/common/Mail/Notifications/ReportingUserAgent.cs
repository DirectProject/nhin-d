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

namespace Health.Direct.Common.Mail.Notifications
{
    /// <summary>
    /// Represents a Reporting-UA as specified by RFC 3798
    /// </summary>
    /// <remarks>
    /// From RFC 3798, 3.2.2, The Reporting-UA field
    /// reporting-ua-field = "Reporting-UA" ":" ua-name ";" ua-product
    /// 
    /// For Internet Mail user agents, it is recommended that this field contain both: 
    /// the DNS name of the particular instance of the MUA that generated the MDN and the
    /// name of the product
    /// 
    /// </remarks>
    public class ReportingUserAgent
    {
        string m_name;
        string m_product;
                
        /// <summary>
        /// Initializes an instance with the specified user agent name and product name
        /// </summary>
        /// <param name="product">The user agent product</param>
        /// <param name="name">The user agent name</param>
        public ReportingUserAgent(string name, string product)
        {
            this.Name = name;
            this.Product = product;
        }
        
        /// <summary>
        /// Gets the user agent's domain name
        /// </summary>
        public string Name
        {
            get
            {
                return m_name;
            }
            private set
            {
                if (string.IsNullOrEmpty(value))
                {
                    throw new ArgumentException("value was null or empty", "value");
                }
                
                m_name = value;
            }
        }
        
        /// <summary>
        /// Gets user agent's product
        /// </summary>
        public string Product
        {
            get
            {
                return m_product;
            }
            private set
            {
                if (string.IsNullOrEmpty(value))
                {
                    throw new ArgumentException("value was null or empty", "value");
                }
                
                m_product = value;
            }
        }

        /// <summary>
        /// Returns a string representation following the conventions for RFC 3798
        /// </summary>
        /// <returns>A string representation of this reporting user agent</returns>
        public override string ToString()
        {
            return string.Format("{0};{1}", this.Name, this.Product);
        }
    }
}