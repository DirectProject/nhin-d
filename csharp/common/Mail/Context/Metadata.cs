/* 
 Copyright (c) 2010, Direct Project
 All rights reserved.

 Authors:
    Joseph Shook    Joseph.Shook@Surescripts.com
  
Redistribution and use in source and binary forms, with or without modification, are permitted provided that the following conditions are met:

Redistributions of source code must retain the above copyright notice, this list of conditions and the following disclaimer.
Redistributions in binary form must reproduce the above copyright notice, this list of conditions and the following disclaimer in the documentation and/or other materials provided with the distribution.
Neither the name of The Direct Project (directproject.org) nor the names of its contributors may be used to endorse or promote products derived from this software without specific prior written permission.
THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 
*/

using System.Collections.Generic;
using Health.Direct.Common.Mime;

namespace Health.Direct.Common.Mail.Context
{
    /// <summary>
    /// Direct<see cref="Context"/> metadata container.
    /// </summary>
    public class Metadata : HeaderCollection
    {
        /// <summary>
        /// Construct empty Metadata
        /// </summary>
        public Metadata() 
        {
        }
        /// <summary>
        /// Construct Metadata from text that can parse to a <see cref="HeaderCollection"/>
        /// </summary>
        /// <param name="metadata"></param>
        public Metadata(string metadata): base(MimeSerializer.Default.DeserializeHeaders(metadata))
        {
        }

        /// <summary>
        /// Gets and sets the value of <c>version</c> metadata.  Value is specifically called the <c>version-identifier</c> 
        /// </summary>
        public string Version
        {
            get
            {
                return GetValue(ContextStandard.Version);
            }
            set
            {
                SetValue(ContextStandard.Version, value);
            }
        }

        /// <summary>
        /// Gets and sets the value of <c>id</c> metadata.  Value is specifically called the <c>unique-identifier</c> 
        /// </summary>
        public string Id
        {
            get
            {
                return GetValue(ContextStandard.Id);
            }
            set
            {
                SetValue(ContextStandard.Id, value);
            }
        }

        /// <summary>
        /// Gets and sets the value of <c>patient-id</c> metadata. 
        /// </summary>
        public string PatientId
        {
            get
            {
                return GetValue(ContextStandard.PatientId);
            }
            set
            {
                SetValue(ContextStandard.PatientId, value.ToString());
            }
        }

        /// <summary>
        /// Return IEnumerable list <see cref="PatientIdentifier"/>
        /// </summary>
        public IEnumerable<PatientIdentifier> PatientIdentifier
        {
            get
            {
                var patientIdentifier = ContextParser.ParsePatientIdentifier(PatientId);

                return patientIdentifier;
            }
        }

        /// <summary>
        /// Gets and sets the value of <c>type</c> metadata. 
        /// </summary>
        public Type Type
        {
            get
            {
                return ContextParser.ParseType(GetValue(ContextStandard.Type));
            }
            set
            {
                SetValue(ContextStandard.Type, value.ToString());
            }
        }


        /// <summary>
        /// Gets and sets the value of <c>purpose</c> metadata. 
        /// </summary>
        public string Purpose
        {
            get
            {
                return GetValue(ContextStandard.Purpose);
            }
            set
            {
                SetValue(ContextStandard.Purpose, value.ToString());
            }
        }

        /// <summary>
        /// Gets and sets the value of <c>patient</c> metadata. 
        /// </summary>
        public Patient Patient
        {
            get
            {
                return new Patient(GetValue(ContextStandard.Patient));
            }
            set
            {
                SetValue(ContextStandard.Patient, value.ToString());
            }
        }

        /// <summary>
        /// Gets and sets the value of <c>encapsulation</c> metadata. 
        /// </summary>
        public string Encapsulation
        {
            get
            {
                return GetValue(ContextStandard.Encapsulation);
            }
            set
            {
                SetValue(ContextStandard.Encapsulation, value.ToString());
            }
        }
    }
}