using System.Security;

namespace Health.Direct.Trust
{
    /// <summary>
    /// Implementations will provide signing cert location, optional pass key
    /// and routines for loading and storing resources. 
    /// </summary>
    public interface ISignProvider
    {
        /// <summary>
        /// Resource location
        /// </summary>
        string Signature { get; }
        /// <summary>
        /// Pass key to open Signature
        /// </summary>
        SecureString Key { get; }

        byte[] Sign(byte[] cmsData);
    }
}