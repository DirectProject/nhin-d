/* 
 Copyright (c) 2010, NHIN Direct Project
 All rights reserved.

 Authors:
    Umesh Madan     umeshma@microsoft.com
  
Redistribution and use in source and binary forms, with or without modification, are permitted provided that the following conditions are met:

Redistributions of source code must retain the above copyright notice, this list of conditions and the following disclaimer.
Redistributions in binary form must reproduce the above copyright notice, this list of conditions and the following disclaimer in the documentation and/or other materials provided with the distribution.
Neither the name of the The NHIN Direct Project (nhindirect.org). nor the names of its contributors may be used to endorse or promote products derived from this software without specific prior written permission.
THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 
*/
using System;
using System.Collections.Generic;
using System.Linq;
using System.Web;
using System.Configuration;
using NHINDirect.Diagnostics;

namespace NHINDirect.Config.Service
{
    public class ServiceSettings
    {
        public const string ConfigConnectStringKey = "configStoreConnectString";
        public const string LogDirectoryKey = "logDirectory";
        public const string QueryTimeoutKey = "queryTimeout";
        
        string m_connectString;
        int m_dbTimeout;
        LogFileSettings m_logSettings;        
        
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
        
        public int QueryTimeout
        {
            get
            {
                return m_dbTimeout;
            }
        }
                
        public LogFileSettings LogSettings
        {
            get
            {
                return m_logSettings;
            }
        }
        
        public string GetSetting(string name)
        {
            string value = ConfigurationSettings.AppSettings[name];
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
            m_connectString = this.GetSetting(ServiceSettings.ConfigConnectStringKey);
            
            m_logSettings = new LogFileSettings();
            m_logSettings.SetDefaults();
            m_logSettings.DirectoryPath = this.GetSetting<string>(LogDirectoryKey, m_logSettings.DirectoryPath);
            
            m_dbTimeout = this.GetSetting<int>(ServiceSettings.QueryTimeoutKey, Config.Store.ConfigStore.DefaultTimeoutSeconds);
            if (m_dbTimeout <= 0)
            {
                throw new ArgumentException("Invalid query timeout in config");
            }
        }
    }
}
