using System;

using Health.Net.Direct.Diagnostics.NLog;

using NHINDirect.Container;
using NHINDirect.Diagnostics;

namespace NHINDirect.Config.Service
{
    public class Global : System.Web.HttpApplication
    {

        protected void Application_Start(object sender, EventArgs e)
        {
            LogFileSettings settings
                = new LogFileSettings
                      {
                          DirectoryPath = Server.MapPath(@"~\..\Logs"),
                          EventLogLevel = LoggingLevel.Fatal,
                          EventLogSource = "nhinConfigService",
                          Ext = ".log",
                          FileChangeFrequency = 24,
                          Level = LoggingLevel.Debug,
                          NamePrefix = "ConfigService"
                      };

            // setup the container here... grrr... we're duplicating this!
            IoC.Initialize(new SimpleDependencyResolver()
                               .Register<ILogFactory>(new NLogFactory(settings))
                );
        }

        protected void Session_Start(object sender, EventArgs e)
        {

        }

        protected void Application_BeginRequest(object sender, EventArgs e)
        {

        }

        protected void Application_AuthenticateRequest(object sender, EventArgs e)
        {

        }

        protected void Application_Error(object sender, EventArgs e)
        {

        }

        protected void Session_End(object sender, EventArgs e)
        {

        }

        protected void Application_End(object sender, EventArgs e)
        {

        }
    }
}