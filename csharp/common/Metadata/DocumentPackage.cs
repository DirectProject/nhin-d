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
using System;
using System.Collections.Generic;

namespace Health.Direct.Common.Metadata
{
    /// <summary>
    /// Represents a package of documents
    /// </summary>
    public class DocumentPackage
    {

        private List<DocumentMetadata> m_docs = new List<DocumentMetadata>();

        /// <summary>
        /// The list of package documents
        /// </summary>
        public List<DocumentMetadata> Documents
        {
            get { return m_docs; }
        }


        /// <summary>
        /// Represents the humans and/or machines that authored the package (may be different from the document authors
        /// </summary>
        public Author Author { get; set; }

        /// <summary>
        /// Comments associated with the Submission Set.
        /// </summary>
        public string Comments { get; set; }

        /// <summary>
        /// The code specifying the type of clinical activity that resulted in placing these
        /// Documents in this Package.
        /// </summary>
        public CodedValue ContentTypeCode { get; set; }

        private List<Recipient> m_recipients = new List<Recipient>();

        /// <summary>
        /// The list of recipients to which this package is being sent.
        /// </summary>
        public IList<Recipient> IntendedRecipients { get { return m_recipients; } }

        /// <summary>
        /// The patient ID for whom this package pertains.
        /// </summary>
        public PatientID PatientId { get; set; }

        /// <summary>
        /// OID identifying the instance of the Document Source that contributed the Submission Set.
        /// </summary>
        public string SourceId { get; set; }

        /// <summary>
        /// Point in Time at the Document Source when the Submission Set was created and sent
        /// </summary>
        public DateTime? SubmissionTime { get; set; }

        /// <summary>
        /// The title of the package
        /// </summary>
        public string Title { get; set; }

        /// <summary>
        /// Globally unique identifier for the submission-set instance assigned by the Document Source in OID format
        /// </summary>
        public string UniqueId { get; set; }

    }
}