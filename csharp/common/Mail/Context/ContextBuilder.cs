using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;
using Health.Direct.Common.Extensions;

namespace Health.Direct.Common.Mail.Context
{
    /// <summary>
    /// 
    /// </summary>
    public class ContextBuilder
    {
        private readonly  Context context;

        /// <summary>
        /// Construct Context.
        /// <remarks>
        /// Defaults:
        ///   ContentType = text/plain
        ///   ContentId = new guid.
        ///   Content-Dispostion = attachment; filename=metadata.txt
        /// </remarks>
        /// </summary>
        public ContextBuilder()
        {
            context = new Context();
        }
        
        public Context Build()
        {
            return context;
        }

        /// <summary>
        /// Overide contentType
        /// </summary>
        /// <param name="contentType"></param>
        /// <returns>ContextBuilder</returns>
        public ContextBuilder WithContentType(string contentType)
        {
            context.ContentType = contentType;

            return this;
        }

        /// <summary>
        /// Overide contentId
        /// </summary>
        /// <param name="contentId"></param>
        /// <returns>ContextBuilder</returns>
        public ContextBuilder WithContentId(string contentId)
        {
            context.ContentType = contentId;

            return this;
        }

        /// <summary>
        /// Overide Content-Disposition filename
        /// </summary>
        /// <param name="fileName"></param>
        /// <returns>ContextBuilder</returns>
        public ContextBuilder WithFileName(string fileName)
        {
            var disposition = Context.BuildContentDisposition(fileName);
            context.ContentDisposition = disposition;

            return this;
        }

        /// <summary>
        /// Set transfer encoding
        /// </summary>
        /// <param name="encoding"></param>
        /// <returns>ContextBuilder</returns>
        public ContextBuilder WithTransferEncoding(string encoding)
        {
            context.ContentTransferEncoding = encoding;

            return this;
        }

        /// <summary>
        /// Set metadata version
        /// </summary>
        /// <param name="version"></param>
        /// <returns>ContextBuilder</returns>
        public ContextBuilder WithVersion(string version)
        {
            context.Metadata.Version = version;

            return this;
        }

        /// <summary>
        /// Set metadata id
        /// </summary>
        /// <remarks>
        /// See 3.2 Transaction ID
        /// </remarks>
        /// <param name="id"></param>
        /// <returns>ContextBuilder</returns>
        public ContextBuilder WithId(string id)
        {
            context.Metadata.Id = id;

            return this;
        }

        /// <summary>
        /// Set metadata patientId/s.
        /// Call multiple times if adding more than one Id.
        /// </summary>
        /// <param name="patientId"></param>
        /// <returns>ContextBuilder</returns>
        public ContextBuilder WithPatientId(string patientId)
        {
            if (context.Metadata.PatientId.IsNullOrWhiteSpace())
            {
                context.Metadata.PatientId = patientId;
            }
            else
            {
                context.Metadata.PatientId += $"; {patientId}";
            }

            return this;
        }

        /// <summary>
        /// Set metadata type
        /// </summary>
        /// <param name="category">Valid <see cref="ContextStandard.Type.Category"/></param>
        /// <param name="action">Valid <see cref="ContextStandard.Type.Action"/></param>
        /// <returns>ContextBuilder</returns>
        public ContextBuilder WithType(string category, string action)
        {
            context.Metadata.Type = new Type
            {
                Category = category,
                Action = action
            };

            return this;
        }

        /// <summary>
        /// Set metadata type
        /// </summary>
        /// <remarks>
        /// See 3.5 Purpose of Use
        /// </remarks>
        /// <param name="purpose">Valid <see cref="ContextStandard.Purpose"/></param>
        /// <returns>ContextBuilder</returns>
        public ContextBuilder WithPurpose(string purpose)
        {
            context.Metadata.Purpose = purpose;

            return this;
        }

        /// <summary>
        /// Set metadata patient attributes
        /// Paramters must be a valid <see cref="ContextStandard.Patient"/>
        /// </summary>
        /// <param name="givenName"></param>
        /// <param name="surname"></param>
        /// <param name="middleName"></param>
        /// <param name="dateOfBirth"></param>
        /// <param name="gender"></param>
        /// <param name="socialSecurityNumber"></param>
        /// <param name="telephoneNumber"></param>
        /// <param name="streetAddress"></param>
        /// <param name="postalCode"></param>
        /// <returns>ContextBuilder</returns>
        public ContextBuilder WithPatient(
            string givenName, 
            string surname,
            string middleName,
            string dateOfBirth,
            string gender,
            string socialSecurityNumber,
            string telephoneNumber,
            string streetAddress,
            string postalCode)
        {
            context.Metadata.Patient = new Patient
            {
                GivenName = givenName,
                SurName = surname,
                MiddleName = middleName,
                DateOfBirth = dateOfBirth,
                Gender = gender,
                SocialSecurityNumber = socialSecurityNumber,
                TelephoneNumber = telephoneNumber,
                StreetAddress = streetAddress,
                PostalCode = postalCode
            };

            return this;
        }
    }
}
