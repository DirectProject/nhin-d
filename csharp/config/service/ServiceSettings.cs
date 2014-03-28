/* 
 Copyright (c) 2010, Direct Project
 All rights reserved.

 Authors:
    Umesh Madan     umeshma@microsoft.com
  
Redistribution and use in source and binary forms, with or without modification, are permitted provided that the following conditions are met:

Redistributions of source code must retain the above copyright notice, this list of conditions and the following disclaimer.
Redistributions in binary form must reproduce the above copyright notice, this list of conditions and the following disclaimer in the documentation and/or other materials provided with the distribution.
Neither the name of The Direct Project (directproject.org) nor the names of its contributors may be used to endorse or promote products derived from this software without specific prior written permission.
THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 
*/
using System;
using System.Configuration;

using Health.Direct.Config.Store;

namespace Health.Direct.Config.Service
{
    public class ServiceSettings
    {
        public const string ConfigConnectStringKey = "configStore";
        public const string QueryTimeoutKey = "queryTimeout";
        
        string m_connectString;
        TimeSpan m_dbTimeout;
        
        public ServiceSettings()
        {
            this.Load(); 
        }
        
        public string StoreConnectString
        {
            get
            {
                return m_connectString;
            }
        }
        
        public TimeSpan QueryTimeout
        {
            get
            {
                return m_dbTimeout;
            }
        }
        
        public string GetSetting(string name)
        {
            string value = ConfigurationManager.AppSettings[name];
            if (string.IsNullOrEmpty(value))
            {
                throw new ConfigurationErrorsException(string.Format("Service Setting {0} not found", name));
            }
            
            return value;
        }

        public T GetSetting<T>(string name)
        {        
            return (T) Convert.ChangeType(this.GetSetting(name), typeof(T)); 
        }         

        public T GetSetting<T>(string name, T defaultValue)
        {        
            try
            {        
                return this.GetSetting<T>(name);        
            }        
            catch 
            {        
            }        
            
            return defaultValue; 
        }         
        
        void Load()
        {
            m_connectString = ConfigurationManager.ConnectionStrings[ConfigConnectStringKey].ConnectionString;

            TimeSpan timeout;
            if (!TimeSpan.TryParse(GetSetting(QueryTimeoutKey), out timeout))
            {
                timeout = ConfigStore.DefaultTimeout;
            }

            m_dbTimeout = timeout;
            if (m_dbTimeout.Ticks <= 0)
            {
                throw new ArgumentException("Invalid query timeout in config");
            }
        }
    }
}