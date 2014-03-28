using System;
using System.Configuration;

namespace Health.Direct.Common.Container
{
    ///<summary>
    /// A common class that contains the <see cref="Load"/> method used by multiple classes.
    ///</summary>
    ///<typeparam name="T">The type that must be an instance of <see cref="ConfigurationSection"/></typeparam>
    public class ConfigurationSectionBase<T> : ConfigurationSection
        where T : ConfigurationSection
    {
        ///<summary>
        /// Load a specifically named <paramref name="sectionName"/> from the <see cref="ConfigurationManager"/>
        ///</summary>
        ///<param name="sectionName">The section name to load.</param>
        ///<returns>A reference to self</returns>
        public static T Load(string sectionName)
        {
            return (T) ConfigurationManager.GetSection(sectionName);
        }

        /// <summary>
        /// A convience method to load a type by the name <paramref name="typeName"/>
        /// </summary>
        /// <param name="typeName">The type to load</param>
        /// <returns>The loaded type</returns>
        protected static Type LoadType(string typeName)
        {
            return Type.GetType(typeName, true);
        }
    }
}