using System.Linq;
using System.Text;
using Health.Direct.Common.Extensions;

namespace Health.Direct.Common.Mail.Context
{
    public class Patient
    {
        private string originalHeader;
        public Patient(string headerValue)
        {
            originalHeader = headerValue;
            var patientAttributes = ContextParser.GetPatientAttributes(headerValue, ContextError.InvalidPatient);
            GivenName = patientAttributes.SingleOrDefault(p => p.Key.Equals(ContextStandard.PatientGivenName)).Value;
            SurName = patientAttributes.SingleOrDefault(p => p.Key.Equals(ContextStandard.PatientSurName)).Value;
            MiddleName = patientAttributes.SingleOrDefault(p => p.Key.Equals(ContextStandard.PatientMiddleName)).Value;
            DateOfBirth = patientAttributes.SingleOrDefault(p => p.Key.Equals(ContextStandard.PatientDateOfBirth)).Value;
            Gender = patientAttributes.SingleOrDefault(p => p.Key.Equals(ContextStandard.PatientGender)).Value;
            SocialSecurityNumber = patientAttributes.SingleOrDefault(p => p.Key.Equals(ContextStandard.PatientSocialSecurityNumber)).Value;
            TelephoneNumber = patientAttributes.SingleOrDefault(p => p.Key.Equals(ContextStandard.PatientTelephoneNumber)).Value;
            StreetAddress = patientAttributes.SingleOrDefault(p => p.Key.Equals(ContextStandard.PatientStreetAddress)).Value;
            PostalCode = patientAttributes.SingleOrDefault(p => p.Key.Equals(ContextStandard.PatientPostalCode)).Value;
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

        public override string ToString()
        {
            if (!originalHeader.IsNullOrWhiteSpace())
            {
                return originalHeader;
            }
            else
            {
                var sb = new StringBuilder();

                if (!GivenName.IsNullOrWhiteSpace())
                {
                    sb.Append(ContextStandard.PatientGivenName).Append("=").Append(GivenName);
                }

                if (!SurName.IsNullOrWhiteSpace())
                {
                    sb.Append(ContextStandard.PatientSurName).Append("=").Append(SurName);
                }

                if (!MiddleName.IsNullOrWhiteSpace())
                {
                    sb.Append(ContextStandard.PatientMiddleName).Append("=").Append(MiddleName);
                }

                if (!DateOfBirth.IsNullOrWhiteSpace())
                {
                    sb.Append(ContextStandard.PatientDateOfBirth).Append("=").Append(DateOfBirth);
                }

                if (!Gender.IsNullOrWhiteSpace())
                {
                    sb.Append(ContextStandard.PatientGender).Append("=").Append(Gender);
                }

                if (!SocialSecurityNumber.IsNullOrWhiteSpace())
                {
                    sb.Append(ContextStandard.PatientSocialSecurityNumber).Append("=").Append(SocialSecurityNumber);
                }

                if (!TelephoneNumber.IsNullOrWhiteSpace())
                {
                    sb.Append(ContextStandard.PatientTelephoneNumber).Append("=").Append(TelephoneNumber);
                }

                if (!StreetAddress.IsNullOrWhiteSpace())
                {
                    sb.Append(ContextStandard.PatientStreetAddress).Append("=").Append(StreetAddress);
                }

                if (!PostalCode.IsNullOrWhiteSpace())
                {
                    sb.Append(ContextStandard.PatientPostalCode).Append("=").Append(PostalCode);
                }

                return sb.ToString();
            }
        }
    }
}