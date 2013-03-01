

namespace Health.Direct.ModSpec3.ResolverPlugins
{
    ///<summary>
    /// LDAP resolution errors that may trigger <see cref="LdapCertResolverException"/> exceptions.
    ///</summary>
    public enum LDAPError
    {
        /// <summary>
        /// Unable to make an anonymous connection
        /// </summary>
        BindFailure = 0, 
        /// <summary>
        /// Unexpected missing userCertificate attribute.  Possibly no attributes at ldap end point.
        /// </summary>
        NoUserCertificateAttribute,
        
    }
    
}
