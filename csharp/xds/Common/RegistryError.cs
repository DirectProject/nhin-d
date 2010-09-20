using System;
using System.Collections.Generic;
using System.Text;

namespace NHINDirect.XDS.Common
{
    public class RegistryError
    {

        private string codeContext;
        public string CodeContext
        {
            get { return codeContext; }
            set { codeContext = value; }
        }

        private string errorCode;
        public string ErrorCode
        {
            get { return errorCode; }
            set { errorCode = value; }
        }

        private string severity;
        public string Severity
        {
            get { return severity; }
            set { severity = value; }
        }

        private string location;
        public string Location
        {
            get { return location; }
            set { location = value; }
        }

    }
}
