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

using Health.Direct.Common.Metadata;

namespace Health.Direct.Xd
{
    /// <summary>
    /// Represents an IHE-compliant ebXML classification node for coded values.
    /// </summary>
    public class CodedValueClassification : Classification
    {

        /// <summary>
        /// Initializes an instance with the supplied classification scheme, node representation, classified object and
        /// a name element for the label and a slot for the code.
        /// </summary>
        /// <param name="attr">The XD metadata attribute type</param>
        /// <param name="classifiedObject">The document that is being classified</param>
        /// <param name="code">The code</param>
        /// <param name="label">The code label</param>
        /// <param name="codingScheme">The coding scheme for this code</param>
        public CodedValueClassification(XDAttribute attr, string classifiedObject, string code, string label, string codingScheme)
            : base(attr, codingScheme, classifiedObject)
        {
            Initialize(code, label);
        }

        /// <summary>
        /// Initializes an instance with the supplied classification scheme, node representation, classified object and
        /// a name element for the label and a slot for the code.
        /// </summary>
        /// <param name="attr">The XD metadata attribute type</param>
        /// <param name="classifiedObject">The document that is being classified</param>
        /// <param name="value">The <see cref="CodedValue"/> for this classification</param>
        public CodedValueClassification(XDAttribute attr, string classifiedObject, CodedValue value)
            : base(attr, value.Code, classifiedObject)
        {
            Initialize(value);
        }

        /// <summary>
        /// Initializes an instance with the supplied classification scheme, node representation, classified object and
        /// a name element for the label and a slot for the code.
        /// </summary>
        /// <param name="scheme">The classification scheme UUID</param>
        /// <param name="codingScheme">The node representation</param>
        /// <param name="classifiedObject">The document that is being classified</param>
        /// <param name="code">The code</param>
        /// <param name="label">The code label</param>
        public CodedValueClassification(string scheme, string classifiedObject, string code, string label, string codingScheme)
            : base(scheme, code, classifiedObject)
        {
            Initialize(codingScheme, label);
        }

        /// <summary>
        /// Initializes an instance with the supplied classification scheme, node representation, classified object and
        /// a name element for the label and a slot for the code.
        /// </summary>
        /// <param name="scheme">The classification scheme UUID</param>
        /// <param name="nodeRepresentation">The node representation</param>
        /// <param name="classifiedObject">The document that is being classified</param>
        /// <param name="value">The coded value</param>
        public CodedValueClassification(string scheme, string nodeRepresentation, string classifiedObject, CodedValue value)
            : base(scheme, nodeRepresentation, classifiedObject)
            
        {
            Initialize(value);
        }
             

        private void Initialize(CodedValue value)
        {
            if (value == null)
                Initialize("", "");
            else
                Initialize(value.Scheme, value.Label);
        }


        private void Initialize(string codingScheme, string label)
        {
            this.Add(new Name(label),
                     new Slot(XDMetadataStandard.Slots.CodingScheme, codingScheme));
        }

    }
}