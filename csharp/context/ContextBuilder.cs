using MimeKit;
using System.IO;
using System.Text;
using MimePart = MimeKit.MimePart;

namespace Health.Direct.Context
{
    /// <summary>
    /// 
    /// </summary>
    public class ContextBuilder
    {
        private const string Disposition = "attachment";
        private readonly  Context _directContext;

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
            _directContext = new Context();
        }

        /// <summary>
        /// Build the Content Type.
        /// </summary>
        /// <param name="mediaType">The media type.</param>
        /// <param name="mediaSubtype">The media subtype.</param>
        /// <returns>ContextBuilder</returns>
        public ContextBuilder WithContentType(string mediaType, string mediaSubtype)
        {
            _directContext.ContentType.MediaType = mediaType;
            _directContext.ContentType.MediaSubtype = mediaSubtype;

            return this;
        }

        /// <summary>
        /// Overide contentId
        /// </summary>
        /// <param name="contentId"></param>
        /// <returns>ContextBuilder</returns>
        public ContextBuilder WithContentId(string contentId)
        {
            _directContext.ContentId = contentId;

            return this;
        }

        /// <summary>
        /// Overide Content-Disposition filename
        /// </summary>
        /// <param name="fileName">attachment filename</param>
        /// <returns>ContextBuilder</returns>
        public ContextBuilder WithDisposition(string fileName)
        {
            _directContext.ContentDisposition.Disposition = Disposition;
            _directContext.ContentDisposition.FileName = fileName;
            return this;
        }

        /// <summary>
        /// Set transfer encoding
        /// </summary>
        /// <param name="encoding"></param>
        /// <returns>ContextBuilder</returns>
        public ContextBuilder WithTransferEncoding(ContentEncoding encoding)
        {
            _directContext.ContentTransferEncoding = encoding;

            return this;
        }

        /// <summary>
        /// Set metadata version
        /// </summary>
        /// <param name="version"></param>
        /// <returns>ContextBuilder</returns>
        public ContextBuilder WithVersion(string version)
        {
            _directContext.Metadata.Version = version;

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
            _directContext.Metadata.Id = id;

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
            if (string.IsNullOrEmpty(patientId))
            {
                return this;
            }

            if (_directContext.Metadata.PatientId.IsNullOrWhiteSpace())
            {
                _directContext.Metadata.PatientId = patientId;
            }
            else
            {
                _directContext.Metadata.PatientId += $"; {patientId}";
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
            _directContext.Metadata.Type = new Type
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
            if (string.IsNullOrEmpty(purpose))
            {
                return this;
            }

            _directContext.Metadata.Purpose = purpose;

            return this;
        }

        public ContextBuilder WithPatient(Patient patient)
        {
            if (patient == null)
            {
                return this;
            }

            _directContext.Metadata.Patient = patient;

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

            _directContext.Metadata.Encapsulation = new Encapsulation
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
            return _directContext;
        }

        /// <summary>
        /// Prepare a RFC 5322 message searialized from a Contxt object 
        /// </summary>
        /// <returns></returns>
        public MimePart BuildMimePart()
        {
            var mimePart = new MimePart(_directContext.ContentType)
            {
                ContentDisposition = new ContentDisposition(ContentDisposition.Attachment),
                ContentTransferEncoding = _directContext.ContentTransferEncoding
            };

            var contentDist = _directContext.ContentDisposition;
            mimePart.ContentDisposition.FileName = contentDist.FileName;
            mimePart.ContentId = _directContext.ContentId;

            var contextBodyStream = new MemoryStream(Encoding.UTF8.GetBytes(_directContext.Metadata.Deserialize()));
            mimePart.ContentObject = new ContentObject(contextBodyStream);
            
            return mimePart;
        }
    }
}
