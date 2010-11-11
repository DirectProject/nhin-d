using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;

using Health.Direct.Common;

namespace Health.Direct.Xd
{
    /// <summary>
    /// Represents an exception in generating or consuming XD* Metadata
    /// </summary>
    public class XdMetadataException : DirectException<XdError>
    {
        /// <summary>
        /// Creates an exception with the specified error
        /// </summary>
        /// <param name="error">The <see cref="XdError"/> that triggered this exception.</param>
        public XdMetadataException(XdError error)
            : base(error)
        {
        }

        /// <summary>
        /// Creates an exception with the specified error and a custom message
        /// </summary>
        /// <param name="error">The <see cref="XdError"/> that triggered this exception.</param>
        /// <param name="message">The custom error message</param>
        public XdMetadataException(XdError error, string message)
            : base(error, message)
        {
        }

        /// <summary>
        /// Creates an exception with the specified error, and a lower level exception
        /// </summary>
        /// <param name="error">The <see cref="XdError"/> that triggered this exception.</param>
        /// <param name="innerException">The lower level exception that triggered this exception</param>
        public XdMetadataException(XdError error, Exception innerException)
            : base(error, innerException)
        {
        }

        /// <summary>
        /// Creates an exception with the specified error, a lower level exception, and a custom message
        /// </summary>
        /// <param name="error">The <see cref="XdError"/> that triggered this exception.</param>
        /// <param name="innerException">The lower level exception that triggered this exception</param>
        /// <param name="message">The custom error message</param>
        public XdMetadataException(XdError error, string message, Exception innerException)
            : base(error, message, innerException)
        {
        }

    }
}
