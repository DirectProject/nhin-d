/* 
 Copyright (c) 2010, Direct Project
 All rights reserved.

 Authors:
    Arien Malec     arien.malec@nhindirect.org
  
Redistribution and use in source and binary forms, with or without modification, are permitted provided that the following conditions are met:

Redistributions of source code must retain the above copyright notice, this list of conditions and the following disclaimer.
Redistributions in binary form must reproduce the above copyright notice, this list of conditions and the following disclaimer in the documentation and/or other materials provided with the distribution.
Neither the name of The Direct Project (directproject.org). nor the names of its contributors may be used to endorse or promote products derived from this software without specific prior written permission.
THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 
*/
using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Xml.Linq;

namespace Health.Direct.Xd
{
    /// <summary>
    /// Represents an association between two objects.
    /// </summary>
    public class Association : XElement
    {
        /// <summary>
        /// Initializes an instance of the specified type, with target and source.
        /// </summary>
        /// <param name="type">Association type</param>
        /// <param name="source">The source object for the association</param>
        /// <param name="target">The target object for the association</param>
        /// <param name="content">Parameters representing association content, handled like the <see cref="XElement"/> parameter</param>
        public Association(string type, string source, string target, params object[] content)
            : base(XDMetadataStandard.Elts.Association)
        {
            if (source == null || target == null) throw new XdMetadataException(XdError.MissingId);

            Add(new XAttribute(XDMetadataStandard.Attrs.AssociationType, type),
                new XAttribute(XDMetadataStandard.Attrs.SourceObject, source),
                new XAttribute(XDMetadataStandard.Attrs.TargetObject, target),
                content);
        }

        /// <summary>
        /// Initializes an original object association between a SubmissionSet and a DocumentEntry
        /// </summary>
        public static Association OriginalDocumentAssociation(string submissionSetId, string documentEntryId)
        {
            return new Association("HasMember", submissionSetId, documentEntryId,
                new Slot(XDMetadataStandard.Slots.SubmissionSetStatus, "Original"));
        }
    }
}
