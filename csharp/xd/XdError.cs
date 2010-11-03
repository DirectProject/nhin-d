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
    }
}
