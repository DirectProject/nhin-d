using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;

namespace Health.Direct.Common.Metadata
{
    /// <summary>
    /// Represents transport packaging/unpackaging operations operating over <see cref="DocumentPackage"/> instances
    /// </summary>
    /// <typeparam name="T">The type to package/unpackage <see cref="DocumentPackage"/> instances</typeparam>
    public interface IPackager<T>
    {
        /// <summary>
        /// Construct a transport package for the <see cref="DocumentPackage"/> 
        /// </summary>
        public T Package(DocumentPackage p);

        /// <summary>
        /// Construct a <see cref="DocumentPackage"/> from the supplied transport package
        /// </summary>
        DocumentPackage Unpackage(T packagedObj);
    }
}
