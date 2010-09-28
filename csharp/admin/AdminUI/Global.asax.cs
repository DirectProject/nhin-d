using System;
using System.Web;

using Health.Net.Diagnostics.NLog;

using NHINDirect.Container;
using NHINDirect.Diagnostics;

namespace AdminUI
{
    public class Global : System.Web.HttpApplication
    {

        protected void Application_Start(object sender, EventArgs e)
        {
            IoC.Initialize(new SimpleDependencyResolver())
                .Register<ILogFactory>(
                new NLogFactory(
                    new LogFileSettings
                        {
                            DirectoryPath = HttpContext.Current.Server.MapPath(@"~\Log"),
                            Level = LoggingLevel.Debug,
                            EventLogLevel = LoggingLevel.Fatal,
                            EventLogSource = "nhin AdminUI",
                            Ext = ".log",
                            FileChangeFrequency = 24,
                            NamePrefix = "adminui"
                        }
                    )
                )
                ;
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