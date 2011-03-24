using Health.Direct.Common.Metadata;

namespace Health.Direct.Xdm
{
    ///<summary>
    /// An interaface to define the packaging and unpackaging of XDM encoded documents.
    ///</summary>
    ///<typeparam name="T"></typeparam>
    public interface IPackager<T>
    {
        /// <summary>
        /// Unpackages an XDM-encoded of type <typeparamref name="T"/>
        /// </summary>
        DocumentPackage Unpackage(T container);

        /// <summary>
        /// Packages a <see cref="DocumentPackage"/> as an XDM of type <typeparamref name="T"/>
        /// </summary>
        T Package(DocumentPackage package);
    }
}