using System;
using System.Collections.Generic;
using System.Text;

namespace NHINDirect.XDS.Common
{
    public class RegistryErrorList
    {
        private string highestSeverity;

        public string HighestSeverity
        {
            get { return highestSeverity; }
            set { highestSeverity = value; }
        }

        private List<RegistryError> registryErrors;

        public List<RegistryError> RegistryErrors
        {
            get
            {
                if (registryErrors == null)
                    registryErrors = new List<RegistryError>();

                return registryErrors;
            }
            set { registryErrors = value; }
        }


    }
}
