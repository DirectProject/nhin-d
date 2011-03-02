using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using Health.Direct.Agent;
using Health.Direct.Config.Store;
using Health.Direct.Common.Extensions;
using System.Xml.Serialization;

namespace Health.Direct.SmtpAgent
{
    public abstract class Route
    {
        string m_addressType;

        public Route()
        {
        }

        [XmlElement]
        public string AddressType
        {
            get
            {
                return m_addressType;
            }
            set
            {
                if (string.IsNullOrEmpty(value))
                {
                    throw new ArgumentException("AddressType");
                }
                
                m_addressType = value;
            }
        }

        public event Action<Route, Exception> Error;

        public abstract void Init();        
        public abstract bool Process(ISmtpMessage message);
        public virtual void Validate()
        {
            if (string.IsNullOrEmpty(this.AddressType))
            {
                throw new SmtpAgentException(SmtpAgentError.MissingAddressTypeInRoute);
            }
        }
                
        protected void NotifyError(Exception ex)
        {
            if (this.Error != null)
            {
                try
                {
                    this.Error(this, ex);
                }
                catch
                {
                }
            }
        }
    }
}
