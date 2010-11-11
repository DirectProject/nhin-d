using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;

namespace Health.Direct.Xd
{
    /// <summary>
    /// Error codes for generating or consuming XD* Metadata
    /// </summary>
    public enum XdError
    {
        /// <summary>
        /// An entity (Association, DocumentEntry, SubmissionSet) is missing the id attribute
        /// </summary>
        MissingId,
        /// <summary>
        /// A document does not have a binary representation
        /// </summary>
        MissingDocumentBytes
    }
}
