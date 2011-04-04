/* 
 Copyright (c) 2010, Direct Project
 All rights reserved.

 Authors:
    John Theisen     jtheisen@kryptiq.com
  
Redistribution and use in source and binary forms, with or without modification, are permitted provided that the following conditions are met:

Redistributions of source code must retain the above copyright notice, this list of conditions and the following disclaimer.
Redistributions in binary form must reproduce the above copyright notice, this list of conditions and the following disclaimer in the documentation and/or other materials provided with the distribution.
Neither the name of The Direct Project (directproject.org) nor the names of its contributors may be used to endorse or promote products derived from this software without specific prior written permission.
THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 
*/
using System;
using System.ServiceModel;

using Health.Direct.Common.Diagnostics;
using Health.Direct.Config.Store;

namespace Health.Direct.Config.Service
{
    public class ConfigServiceBase
    {
        private readonly ConfigStore m_store;
        private readonly ILogger m_logger;

        public ConfigServiceBase()
        {
            try
            {
                m_store = Service.Current.Store;
                m_logger = Log.For(this);
            }
            catch (Exception ex)
            {
                WriteToEventLog(ex);
                throw;
            }
        }

        private static void WriteToEventLog(Exception ex)
        {
            const string source = "Health.Direct.Config.Service";

            EventLogHelper.WriteError(source, ex.Message);
            EventLogHelper.WriteError(source, ex.GetBaseException().ToString());
        }

        private ILogger Logger
        {
            get { return m_logger; }
        }

        protected ConfigStore Store
        {
            get { return m_store; }
        }

        protected FaultException<ConfigStoreFault> CreateFault(string methodName, Exception ex)
        {
            Logger.Error("While performing {0}(); {1}", methodName, ex);

            ConfigStoreFault fault = ConfigStoreFault.ToFault(ex);
            return new FaultException<ConfigStoreFault>(fault, new FaultReason(fault.ToString()));
        }
    }
}