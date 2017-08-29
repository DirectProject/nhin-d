using System;
using System.IO;
using System.Text;
using Health.Direct.Common.Extensions;
using Health.Direct.Common.Mime;
using MimeKit;
using MimePart = MimeKit.MimePart;

namespace Health.Direct.Common.Mail.Context
{
    /// <summary>
    /// 
    /// </summary>
    public class ContextBuilder
    {
        private readonly  Context directContext;

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
            directContext = new Context();
        }
        
        /// <summary>
        /// Overide contentType
        /// </summary>
        /// <param name="contentType"></param>
        /// <returns>ContextBuilder</returns>
        public ContextBuilder WithContentType(string contentType)
        {
            directContext.ContentType = contentType;

            return this;
        }

        /// <summary>
        /// Overide contentId
        /// </summary>
        /// <param name="contentId"></param>
        /// <returns>ContextBuilder</returns>
        public ContextBuilder WithContentId(string contentId)
        {
            directContext.ContentID = contentId;

            return this;
        }

        /// <summary>
        /// Overide Content-Disposition filename
        /// </summary>
        /// <param name="fileName"></param>
        /// <returns>ContextBuilder</returns>
        public ContextBuilder WithDisposition(string disposition)
        {
            directContext.ContentDisposition = disposition;

            return this;
        }

        /// <summary>
        /// Set transfer encoding
        /// </summary>
        /// <param name="encoding"></param>
        /// <returns>ContextBuilder</returns>
        public ContextBuilder WithTransferEncoding(string encoding)
        {
            directContext.ContentTransferEncoding = encoding;

            return this;
        }

        /// <summary>
        /// Set metadata version
        /// </summary>
        /// <param name="version"></param>
        /// <returns>ContextBuilder</returns>
        public ContextBuilder WithVersion(string version)
        {
            directContext.Metadata.Version = version;

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
            directContext.Metadata.Id = id;

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
            if (directContext.Metadata.PatientId.IsNullOrWhiteSpace())
            {
                directContext.Metadata.PatientId = patientId;
            }
            else
            {
                directContext.Metadata.PatientId += $"; {patientId}";
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
            directContext.Metadata.Type = new Type
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
            directContext.Metadata.Purpose = purpose;

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
            directContext.Metadata.Patient = new Patient
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

        /// <summary>
        /// Set metadata Encapsulation attribute.
        /// </summary>
        /// <param name="encapsulation">“http” / “hl7v2”</param>
        /// <returns></returns>
        public ContextBuilder WithEncapsulation(string encapsulation)
        {
            if (encapsulation.IsNullOrWhiteSpace())
            {
                return this;
            }

            directContext.Metadata.Encapsulation = new Encapsulation
            {
                Type = encapsulation
            };

            return this;
        }

        /// <summary>
        /// Return a Context object
        /// </summary>
        /// <returns></returns>
        public Context Build()
        {
            return directContext;
        }

        /// <summary>
        /// Prepare a RFC 5322 message searialized from a Contxt object 
        /// </summary>
        /// <returns></returns>
        public MimePart BuildMimePart()
        {
            var mimePart = new MimePart(directContext.ContentType)
            {
                ContentDisposition = new ContentDisposition(ContentDisposition.Attachment),
                ContentTransferEncoding = ContextMapper.MapContentEncoding(directContext.ContentTransferEncoding)
            };
            var contentDist = new System.Net.Mime.ContentDisposition(directContext.ContentDisposition);
            mimePart.ContentDisposition.FileName = contentDist.FileName;
            mimePart.Headers.Add(MimeStandard.ContentIDHeader, directContext.ContentID);

            var contextBodyStream = new MemoryStream(Encoding.UTF8.GetBytes(directContext.Metadata.Deserialize()));
            mimePart.ContentObject = new ContentObject(contextBodyStream);
            
            return mimePart;
        }
    }
}
