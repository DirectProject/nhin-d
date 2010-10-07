using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using NHINDirect.XDS.Common;

namespace NHINDirect.XDS
{
    // the response from a Provide&Register Document Set-b [ITI-41] transaction
    public class ProvideAndRegisterResponse
    {
        #region properties
        // ??? make enum
        private string m_status = "";

        public string Status
        {
            get { return m_status; }
            set { m_status = value; }
        }
        private RegistryErrorList m_registryErrorList;

        public RegistryErrorList RegistryErrorList
        {
            get { return m_registryErrorList; }
            set { m_registryErrorList = value; }
        }

        #endregion
    }
}
