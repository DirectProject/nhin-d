using System;
using System.Collections.Generic;
using System.Configuration;

namespace NHINDirect.Container
{
    public class SimpleContainerSection : ConfigurationSection
    {
        public SimpleContainerSection()
        {
            Components = new List<SimpleComponentElement>();
        }

        [ConfigurationProperty("components")]
        [ConfigurationCollection(typeof(List<SimpleComponentElement>), AddItemName = "component")]
        public IList<SimpleComponentElement> Components
        {
            get; set;
        }
    }

    public class SimpleComponentElement : ConfigurationElement
    {
        [ConfigurationProperty("type", IsRequired = true)]
        public Type ConcreteType
        {
            get; set;
        }

        [ConfigurationProperty("service", IsRequired = true)]
        public Type ServiceType
        {
            get; set;
        }
    }
}
