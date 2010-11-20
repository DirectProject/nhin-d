using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;

namespace Health.Direct.Xdm
{
    /// <summary>
    /// Errors in XDM processing
    /// </summary>
    public enum XdmError
    {
        /// <summary>
        /// The main IHE_XDM directory was no found
        /// </summary>
        NoRootDirectory,
        /// <summary>
        /// An XDM metadata file could not be found
        /// </summary>
        NoMetadataFile,
        /// <summary>
        /// The file pointed to by the metadata URI attribute was not found in the XDM Zip archive
        /// </summary>
        FileNotFound
    }
}
