/* 
 Copyright (c) 2010, NHIN Direct Project
 All rights reserved.

 Authors:
    Arien Malec     arien.malec@nhindirect.org
  
Redistribution and use in source and binary forms, with or without modification, are permitted provided that the following conditions are met:

Redistributions of source code must retain the above copyright notice, this list of conditions and the following disclaimer.
Redistributions in binary form must reproduce the above copyright notice, this list of conditions and the following disclaimer in the documentation and/or other materials provided with the distribution.
Neither the name of the The NHIN Direct Project (nhindirect.org). nor the names of its contributors may be used to endorse or promote products derived from this software without specific prior written permission.
THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 
*/
using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;

namespace NHINDirect.Metadata
{
    /// <summary>
    /// Represents metadata for a document
    /// </summary>
    public class DocumentMetadata
    {

        /// <summary>
        /// Represents the humans and/or machines that authored the document
        /// </summary>
        public Author Author { get; set; }

        /// <summary>
        /// The code specifying the particular kind of document.
        /// </summary>
        public ClassCode Class { get; set; }

        /// <summary>
        /// Comments associated with the Document.
        /// </summary>
        public string Comments { get; set; }

        /// <summary>
        /// Confidentialty codes applied to this document
        /// </summary>
        public ConfidentialtyCode Confidentiality { get; set; }

        /// <summary>
        /// Document creation datetime. Must either be defined as UTC or Local time.
        /// </summary>
        public DateTime? CreatedOn { get; set; }


        /// <summary>
        /// This list of codes represents the main clinical acts,
        /// such as a colonoscopy or an appendectomy, being documented. 
        /// </summary>
        public IEnumerable<CodedValue> EventCodes { get; set; }

        /// <summary>
        /// Format code for the document
        /// </summary>
        public FormatCode FormatCode { get; set; }

        //TODO: include the Document and only make this get, not set
        /// <summary>
        /// Hash key of the Document itself.
        /// </summary>
        public string Hash { get; set; }

        /// <summary>
        /// The facility code for this document.
        /// </summary>
        public FacilityCode FaciltyCode { get; set; }

        /// <summary>
        /// ISO language code (e.g., en-us)
        /// </summary>
        public string LanguageCode { get; set; }

        /// <summary>
        /// Represents a participant who has legally authenticated or attested the document within
        /// the authorInstitution.
        /// </summary>
        public Person LegalAuthenticator { get; set; }

        /// <summary>
        /// The MIME media type (e.g., "text/plain") of this document
        /// </summary>
        public string MediaType { get; set; }

        /// <summary>
        /// Patient ID of the receiving system.
        /// </summary>
        public PatientID PatientID { get; set; }

        /// <summary>
        /// The code specifying the clinical specialty where the act that resulted in the document was performed
        /// </summary>
        public SpecialtyCode PracticeSetting { get; set; }

        /// <summary>
        /// Represents the start time the service being documented took place (clinically significant,
        /// but not necessarily when the document was produced or approved).
        /// </summary>
        public DateTime? ServiceStart { get; set; }


        /// <summary>
        /// Represents the stop time the service being documented took place (clinically significant,
        /// but not necessarily when the document was produced or approved).
        /// </summary>
        public DateTime? ServiceStop { get; set; }


        //TODO: should only get, not set, based on the size of the underlying document.
        /// <summary>
        /// Represents the size of the document in bytes.
        /// </summary>
        public int? Size { get; set; }

        /// <summary>
        /// The sourcePatientId represents the subject of care medical record Identifier (e.g., Patient Id)
        /// in the local patient Identifier Domain of the Document Source.
        /// </summary>
        public PatientID SourcePtId { get; set; }

        /// <summary>
        /// Represents person information about the patient this document refers to (if any)
        /// </summary>
        public Person Patient { get; set; }

        /// <summary>
        /// Represents the title of the document.
        /// </summary>
        public string Title { get; set; }

        /// <summary>
        /// The globally unique identifier assigned by the document creator to this document.
        /// </summary>
        public string UniqueId { get; set; }

        /// <summary>
        /// URI for this document
        /// </summary>
        public string Uri { get; set; }
    }
}
