using System.Xml.Serialization;
using Health.Direct.Common.Container;
using Health.Direct.Common.Cryptography;

namespace Health.Direct.Agent.Config
{
    /// <summary>
    /// Settings for plugin HsmCryptographer resolver.
    /// 
    /// </summary>
    public class PluginCryptographerSettings
    {
        /// <summary>
        /// A plugin cryptographer is a custom cryptographer.  It is an arbitrary Type (Type + Assembly) that implements <see cref="ISmimeCryptographer"/>.
        /// There is an included HsmCryptographer tested against the Luna SA HSM.  
        /// 
        /// You use this object to define the plugin which will be loaded and plugged into the Agent's cryptographer.
        /// 
        /// The plugin definition uses <see cref="PluginDefinition"/>.  If your plugin also implements IPlugin, then you will be called to initialize yourself with settings (see below).
        /// <![CDATA[
        /// <PluginResolver>
        ///  <Definition>
        ///    <TypeName>Type Name, Assembly Name</TypeName>
        ///    <Settings> 
        ///        <!-- Your OPTIONAL Xml settings....
        ///             If your plugin ALSO implements IPlugin:
        ///             When your plugin is loaded, we will invoke IPlugin:Init(pluginDefinition)
        ///             The pluginDefinition object has an XmlNode on it containing your settings
        ///        -->
        ///    </Settings>
        ///  </Definition>
        ///</PluginResolver>
        ///
        ///*  Example:
        ///<Cryptographer>
        ///    <DefaultEncryption>AES128</DefaultEncryption>
        ///    <DefaultDigest>SHA1</DefaultDigest>
        ///    <PluginCryptographer>
        ///        <Definition>
        ///            <TypeName>Surescripts.Health.Direct.Hsm.HsmCryptographerProxy, Surescripts.Health.Direct.Hsm</TypeName>
        ///            <Settings>
        ///                 <Library>C:\Program Files\LunaSA\cryptoki.dll</Library>
        ///                 <TokenSerial>445500001</TokenSerial>
        ///                 <TokenLabel>directproject</TokenLabel>
        ///                 <UserPin>password</UserPin>
        ///                 <DefaultEncryption>AES256</DefaultEncryption>
        ///                 <DefaultDigest>SHA256</DefaultDigest>
        ///            </Settings>
        ///        </Definition>
        ///    </PluginCryptographer>
        ///</Cryptographer>
        ///
        /// ]]>
        /// </summary>
        public PluginCryptographerSettings()
        {
        }

        /// <summary>
        /// Resolver Type information
        /// <see cref="PluginDefinition"/>
        /// </summary>
        [XmlElement("Definition")]
        public PluginDefinition CryptographerDefinition
        {
            get;
            set;
        }

        public ISmimeCryptographer Create()
        {
            return CryptographerDefinition.Create<ISmimeCryptographer>();
        }
    }
}