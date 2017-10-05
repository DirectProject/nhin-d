using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;

namespace Health.Direct.Context
{
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
        
        public string GivenName{get; set; }
        public string SurName { get; set; }
        public string MiddleName { get; set; }
        public string DateOfBirth { get; set; }
        public string Gender { get; set; }
        public string SocialSecurityNumber { get; set; }
        public string TelephoneNumber { get; set; }
        public string StreetAddress { get; set; }
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