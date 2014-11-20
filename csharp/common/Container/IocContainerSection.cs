using System;
using System.Configuration;

namespace Health.Direct.Common.Container
{
    ///<summary>
    /// This section can be used to move the configuration of the container to the configuration
    /// file. This section can be passed to the <see cref="IDependencyContainer.RegisterFromConfig"/> method
    /// to register all of the types with the container.
    ///</summary>
    ///<example>
    ///  &lt;configSections&gt;
    ///    &lt;section name="ioc" type="Health.Direct.Common.Container.IocContainerSection, Health.Direct.Common" /&gt;
    ///  &lt;/configSections&gt;
    ///  &lt;ioc service="Health.Direct.Common.Tests.Container.SimpleDependencyResolver, Health.Direct.Common" /&gt;
    ///</example>
    public class IocContainerSection : ConfigurationSectionBase<IocContainerSection>
    {
        ///<summary>
        /// The type that implements <see cref="IDependencyContainer"/>
        ///</summary>
        [ConfigurationProperty("type")]
        public string ResolverType
        {
            get { return (string)this["type"]; }
        }

        ///<summary>
        /// A convience method that activates an instance of <see cref="IDependencyContainer"/>
        ///</summary>
        ///<returns>A new instance of the resolver</returns>
        public IDependencyContainer CreateContainer()
        {
            return (IDependencyContainer)Activator.CreateInstance(LoadType(ResolverType));
        }
    }
}