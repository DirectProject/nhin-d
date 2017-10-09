/* 
 Copyright (c) 2010-2017, Direct Project
 All rights reserved.

 Authors:
    Joseph Shook    Joseph.Shook@Surescripts.com
  
Redistribution and use in source and binary forms, with or without modification, are permitted provided that the following conditions are met:

Redistributions of source code must retain the above copyright notice, this list of conditions and the following disclaimer.
Redistributions in binary form must reproduce the above copyright notice, this list of conditions and the following disclaimer in the documentation and/or other materials provided with the distribution.
Neither the name of The Direct Project (directproject.org) nor the names of its contributors may be used to endorse or promote products derived from this software without specific prior written permission.
THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 
*/

using System.Linq;
using System.Text;

namespace Health.Direct.Context
{
    /// <summary>
    /// Patient class holds patient matching parameters
    /// </summary>
    public class Patient
    {
        /// <summary>
        /// Construct and empty Patient.
        /// </summary>
        public Patient(){}

        /// <summary>
        /// Parse Patient from patient-attributes.
        /// </summary>
        /// <param name="headerValue"></param>
        public Patient(string headerValue)
        {
            var patientAttributes = ContextParser.GetPatientAttributes(headerValue, ContextError.InvalidPatient);
            GivenName = patientAttributes.SingleOrDefault(p => p.Key.Equals(ContextStandard.Patient.PatientGivenName)).Value;
            SurName = patientAttributes.SingleOrDefault(p => p.Key.Equals(ContextStandard.Patient.PatientSurName)).Value;
            MiddleName = patientAttributes.SingleOrDefault(p => p.Key.Equals(ContextStandard.Patient.PatientMiddleName)).Value;
            DateOfBirth = patientAttributes.SingleOrDefault(p => p.Key.Equals(ContextStandard.Patient.PatientDateOfBirth)).Value;
            Gender = patientAttributes.SingleOrDefault(p => p.Key.Equals(ContextStandard.Patient.PatientGender)).Value;
            SocialSecurityNumber = patientAttributes.SingleOrDefault(p => p.Key.Equals(ContextStandard.Patient.PatientSocialSecurityNumber)).Value;
            TelephoneNumber = patientAttributes.SingleOrDefault(p => p.Key.Equals(ContextStandard.Patient.PatientTelephoneNumber)).Value;
            StreetAddress = patientAttributes.SingleOrDefault(p => p.Key.Equals(ContextStandard.Patient.PatientStreetAddress)).Value;
            PostalCode = patientAttributes.SingleOrDefault(p => p.Key.Equals(ContextStandard.Patient.PatientPostalCode)).Value;
        }
        
        /// <summary>
        /// Patient matching parameter
        /// </summary>
        public string GivenName{get; set; }
        /// <summary>
        /// Patient matching parameter
        /// </summary>
        public string SurName { get; set; }
        /// <summary>
        /// Patient matching parameter
        /// </summary>
        public string MiddleName { get; set; }
        /// <summary>
        /// Patient matching parameter
        /// </summary>
        public string DateOfBirth { get; set; }
        /// <summary>
        /// Patient matching parameter
        /// </summary>
        public string Gender { get; set; }
        /// <summary>
        /// Patient matching parameter
        /// </summary>
        public string SocialSecurityNumber { get; set; }
        /// <summary>
        /// Patient matching parameter
        /// </summary>
        public string TelephoneNumber { get; set; }
        /// <summary>
        /// Patient matching parameter
        /// </summary>
        public string StreetAddress { get; set; }
        /// <summary>
        /// Patient matching parameter
        /// </summary>
        public string PostalCode { get; set; }

        /// <summary>
        /// Format patient-attributes as a header value 
        /// </summary>
        /// <returns></returns>
        public override string ToString()
        {
            var sb = new StringBuilder();
            var seperator = "; ";

            if (!GivenName.IsNullOrWhiteSpace())
            {
                sb.Append(ContextStandard.Patient.PatientGivenName).Append("=").Append(GivenName).Append(seperator);
            }

            if (!SurName.IsNullOrWhiteSpace())
            {
                sb.Append(ContextStandard.Patient.PatientSurName).Append("=").Append(SurName).Append(seperator);
            }

            if (!MiddleName.IsNullOrWhiteSpace())
            {
                sb.Append(ContextStandard.Patient.PatientMiddleName).Append("=").Append(MiddleName).Append(seperator);
            }

            if (!DateOfBirth.IsNullOrWhiteSpace())
            {
                sb.Append(ContextStandard.Patient.PatientDateOfBirth).Append("=").Append(DateOfBirth).Append(seperator);
            }

            if (!Gender.IsNullOrWhiteSpace())
            {
                sb.Append(ContextStandard.Patient.PatientGender).Append("=").Append(Gender).Append(seperator);
            }

            if (!SocialSecurityNumber.IsNullOrWhiteSpace())
            {
                sb.Append(ContextStandard.Patient.PatientSocialSecurityNumber).Append("=").Append(SocialSecurityNumber).Append(seperator);
            }

            if (!TelephoneNumber.IsNullOrWhiteSpace())
            {
                sb.Append(ContextStandard.Patient.PatientTelephoneNumber).Append("=").Append(TelephoneNumber).Append(seperator);
            }

            if (!StreetAddress.IsNullOrWhiteSpace())
            {
                sb.Append(ContextStandard.Patient.PatientStreetAddress).Append("=").Append(StreetAddress).Append(seperator);
            }

            if (!PostalCode.IsNullOrWhiteSpace())
            {
                sb.Append(ContextStandard.Patient.PatientPostalCode).Append("=").Append(PostalCode).Append(seperator);
            }
            
            return sb.ToString().TrimEnd(';', ' ');
        }
    }
}