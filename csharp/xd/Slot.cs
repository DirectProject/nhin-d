/* 
 Copyright (c) 2010, Direct Project
 All rights reserved.

 Authors:
    Arien Malec     arien.malec@nhindirect.org
  
Redistribution and use in source and binary forms, with or without modification, are permitted provided that the following conditions are met:

Redistributions of source code must retain the above copyright notice, this list of conditions and the following disclaimer.
Redistributions in binary form must reproduce the above copyright notice, this list of conditions and the following disclaimer in the documentation and/or other materials provided with the distribution.
Neither the name of The Direct Project (directproject.org) nor the names of its contributors may be used to endorse or promote products derived from this software without specific prior written permission.
THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 
*/
using System.Collections.Generic;
using System.Linq;
using System.Xml.Linq;

namespace Health.Direct.Xd
{
    /// <summary>
    /// Represents an ebXML Slot.
    /// </summary>
    /// <remarks>
    /// A slot has a name and a list of values. Slots may be single or multiple valued; use the appropriate constructor.
    /// 
    /// The resulting XML will be:
    /// <code>
    /// &lt;Slot name="slotname"&gt;
    ///   &lt;ValueList&gt;
    ///       &lt;Value&gt;value1&lt;/Value&gt;
    ///       &lt;Value&gt;value2&lt;/Value&gt;
    ///       &lt;Value&gt;...&lt;/Value&gt;
    ///   &lt;/ValueList&gt;
    /// &lt;/Slot&gt;      
    /// </code>
    /// </remarks>
    public class Slot : XElement
    {

        /// <summary>
        /// Initializes a Slot with the specified name and a single value in the value list.
        /// </summary>
        /// <param name="name">The slot name</param>
        /// <param name="value">The single value in the value list</param>
        public Slot(string name, string value)
            : this(name, new string[] { value })
        {
        }

        /// <summary>
        /// Intializes a multivalued slot with the specified values.
        /// </summary>
        /// <param name="name">The slot name</param>
        /// <param name="values">The slot values</param>
        public Slot(string name, IEnumerable<string> values)
            : base("Slot")
        {
            var valueXElts = from v in values
                             select new XElement("Value", v);

            this.Add(new XAttribute("name", name));
            XElement valueList = new XElement("ValueList",
                                              valueXElts);
            this.Add(valueList);
        }
    }
}