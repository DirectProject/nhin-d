using System;
using System.IO;

using log4net;
using log4net.Appender;
using log4net.Core;
using log4net.Repository.Hierarchy;

using NHINDirect.Diagnostics;

using ILogger=NHINDirect.Diagnostics.ILogger;

namespace Log4NetDiagnosticsSample
{
    public class Log4NetLogFactory : ILogFactory
    {
        public Log4NetLogFactory(LogFileSettings settings)
        {
            var patternLayout = new log4net.Layout.PatternLayout(
                "%date [%thread] %level %logger - %message%newline");
            patternLayout.ActivateOptions();

            RollingFileAppender appender
                = new RollingFileAppender
                      {
                          Name = "rolling-appender",
                          File = Path.Combine(settings.DirectoryPath, settings.NamePrefix + ".log"),
                          AppendToFile = true,
                          MaxSizeRollBackups = 10,
                          RollingStyle = RollingFileAppender.RollingMode.Date,
                          Layout = patternLayout,
                          LockingModel = new FileAppender.MinimalLock()
                      };
            appender.ActivateOptions();


            log4net.Config.BasicConfigurator.Configure(appender);

            Hierarchy h = (Hierarchy)LogManager.GetRepository();
            Logger rootLogger = h.Root;
            rootLogger.Level = h.LevelMap[ConvertLogLevel(settings.Level).ToString().ToUpper()];
        }

        public ILogger GetLogger(string name)
        {
            return new Log4NetLogger(LogManager.GetLogger(name));
        }

        public ILogger GetLogger(Type loggerType)
        {
            return new Log4NetLogger(LogManager.GetLogger(loggerType));
        }

        private static Level ConvertLogLevel(LoggingLevel level)
        {
            switch (level)
            {
                case LoggingLevel.Debug:
                    return Level.Debug;
                case LoggingLevel.Error:
                    return Level.Error;
                case LoggingLevel.Fatal:
                    return Level.Fatal;
                case LoggingLevel.Info:
                    return Level.Info;
                case LoggingLevel.Off:
                    return Level.Off;
                case LoggingLevel.Trace:
                    return Level.Trace;
                case LoggingLevel.Warn:
                    return Level.Warn;
            }

            throw new ArgumentException("Unknown logging level - " + level);
        }
    }
}