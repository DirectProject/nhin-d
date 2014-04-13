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

namespace Health.Direct.Xd
{
    /// <summary>
    /// Represents a classification that contains a set of slots
    /// </summary>
    public class MultiSlotClassification : Classification
    {
        /// <summary>
        /// Initializes the classification
        /// </summary>
        /// <param name="scheme">The classification scheme UUID</param>
        /// <param name="nodeRepresentation">The node representation </param>
        /// <param name="classifiedObject">The classified content</param>
        /// <param name="slots">The slots for the classification</param>
        public MultiSlotClassification(string scheme, string nodeRepresentation, string classifiedObject, params Slot[] slots)
            : base(scheme, nodeRepresentation, classifiedObject, slots)
        { }

        /// <summary>
        /// Initializes the classification
        /// </summary>
        /// <param name="scheme">The classification scheme UUID</param>
        /// <param name="nodeRepresentation">The node representation </param>
        /// <param name="classifiedObject">The classified content</param>
        /// <param name="slots">The slots for the classification</param>
        public MultiSlotClassification(string scheme, string nodeRepresentation, string classifiedObject, IEnumerable<Slot> slots)
            : base(scheme, nodeRepresentation, classifiedObject, slots)
        { }
    }
}