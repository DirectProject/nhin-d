/* 
 Copyright (c) 2010, Direct Project
 All rights reserved.

 Authors:
    John Theisen     jtheisen@kryptiq.com
    Umesh Madan      umeshma@microsoft.com
 
Redistribution and use in source and binary forms, with or without modification, are permitted provided that the following conditions are met:

Redistributions of source code must retain the above copyright notice, this list of conditions and the following disclaimer.
Redistributions in binary form must reproduce the above copyright notice, this list of conditions and the following disclaimer in the documentation and/or other materials provided with the distribution.
Neither the name of The Direct Project (directproject.org) nor the names of its contributors may be used to endorse or promote products derived from this software without specific prior written permission.
THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 
*/
using System;

using Health.Direct.Common.Container;
using Health.Direct.Common.Diagnostics;
using Health.Direct.Diagnostics.NLog;

namespace Health.Direct.SmtpAgent
{
    public static class SmtpAgentFactory
    {
        private static readonly object m_initSync = new object();
        private static bool m_initialized;

        public static SmtpAgent Create(string configFilePath)
        {
            SmtpAgentSettings settings = null;
            try
            {
                // move this to some package initializer...
                settings = SmtpAgentSettings.LoadSettings(configFilePath);
                InitializeContainer(settings);
                Log.For<MessageArrivalEventHandler>().Debug(settings);
                return new SmtpAgent(settings);
            }
            catch (Exception ex)
            {
                LogError(settings, ex);
                throw;
            }
        }

        private static void InitializeContainer(SmtpAgentSettings settings)
        {
            lock (m_initSync)
            {
                if (!m_initialized)
                {
                    SimpleDependencyResolver dependencyResolver = new SimpleDependencyResolver();
                    if (settings.HasContainer && settings.Container.HasComponents)
                    {
                        try
                        {
                            dependencyResolver.Register(settings.Container);
                        }
                        catch(Exception ex)
                        {
                            LogError(settings, ex);
                        }
                    }
                    EnsureDefaults(dependencyResolver, settings);
                    IoC.Initialize(dependencyResolver);
                    m_initialized = true;
                }
            }
        }
        
        static void LogError(SmtpAgentSettings settings, Exception ex)
        {
            // if we blow up here we should write out to the EventLog w/o any logger
            // dependencies, etc...
            string source = settings == null
                            || settings.LogSettings == null
                            || string.IsNullOrEmpty(settings.LogSettings.EventLogSource)
                                ? null
                                : settings.LogSettings.EventLogSource;

            EventLogHelper.WriteError(source, "While loading SmtpAgent settings - " + ex);
        }
                
        static void EnsureDefaults(SimpleDependencyResolver dependencyResolver, SmtpAgentSettings settings)
        {
            if (!dependencyResolver.IsRegistered<ILogFactory>())
            {
                dependencyResolver.Register<ILogFactory>(new NLogFactory(settings.LogSettings));
            }
            if (!dependencyResolver.IsRegistered<IAuditor>())
            {
                dependencyResolver.Register<IAuditor>(new EventLogAuditor());
            }
        }
    }
}
