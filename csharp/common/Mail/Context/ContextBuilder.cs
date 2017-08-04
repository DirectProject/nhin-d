using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace Health.Direct.Common.Mail.Context
{
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
        /// <param name="encoding"></param>
        /// <returns>ContextBuilder</returns>
        public ContextBuilder WithVersion(string version)
        {
            context.Metadata.Version = version;

            return this;
        }

        /// <summary>
        /// Set metadata id
        /// </summary>
        /// <param name="id"></param>
        /// <returns>ContextBuilder</returns>
        public ContextBuilder WithId(string id)
        {
            context.Metadata.Id = id;

            return this;
        }

        /// <summary>
        /// Set metadata patientId/s
        /// </summary>
        /// <param name="patientId"></param>
        /// <returns>ContextBuilder</returns>
        public ContextBuilder WithPatientId(string patientId)
        {
            context.Metadata.PatientId = patientId;

            return this;
        }

        /// <summary>
        /// Add patientId to  metadata patientIds
        /// </summary>
        /// <param name="patientId"></param>
        /// <returns>ContextBuilder</returns>
        public ContextBuilder AddPatientId(string patientId)
        {
            context.Metadata.PatientId += $"; {patientId}";

            return this;
        }

        /// <summary>
        /// Set metadata type
        /// </summary>
        /// <param name="type"></param>
        /// <returns>ContextBuilder</returns>
        public ContextBuilder WithType(Context.Type type)
        {
            context.Metadata.Type = type;

            return this;
        }
    }
}
