using System;

using log4net;
using log4net.Core;

using ILogger=Health.Direct.Diagnostics.ILogger;

namespace Log4NetDiagnosticsSample
{
    class Log4NetLogger : ILogger
    {
        private readonly ILog m_logger;

        public Log4NetLogger(ILog logger)
        {
            m_logger = logger;
        }

        private ILog Logger
        {
            get
            {
                return m_logger;
            }
        }

        public bool IsDebugEnabled
        {
            get { return Logger.IsDebugEnabled; }
        }

        public void Debug(string message)
        {
            Logger.Debug(message);
        }

        public void Debug(object obj)
        {
            Logger.Debug(obj);
        }

        public void Debug(string message, params object[] args)
        {
            Logger.DebugFormat(message, args);
        }

        public void Debug(string message, Exception exception)
        {
            Logger.Debug(message, exception);
        }

        public bool IsErrorEnabled
        {
            get { return Logger.IsErrorEnabled; }
        }

        public void Error(string message)
        {
            Logger.Error(message);
        }

        public void Error(object obj)
        {
            Logger.Error(obj);
        }

        public void Error(string message, params object[] args)
        {
            Logger.ErrorFormat(message, args);
        }

        public void Error(string message, Exception exception)
        {
            Logger.Error(message, exception);
        }

        public bool IsFatalEnabled
        {
            get { return Logger.IsFatalEnabled; }
        }

        public void Fatal(string message)
        {
            Logger.Fatal(message);
        }

        public void Fatal(object obj)
        {
            Logger.Fatal(obj);
        }

        public void Fatal(string message, params object[] args)
        {
            Logger.FatalFormat(message, args);
        }

        public void Fatal(string message, Exception exception)
        {
            Logger.Fatal(message, exception);
        }

        public bool IsInfoEnabled
        {
            get { return Logger.IsInfoEnabled; }
        }

        public void Info(string message)
        {
            Logger.Info(message);
        }

        public void Info(object obj)
        {
            Logger.Info(obj);
        }

        public void Info(string message, params object[] args)
        {
            Logger.InfoFormat(message, args);
        }

        public void Info(string message, Exception exception)
        {
            Logger.Info(message, exception);
        }

        public bool IsTraceEnabled
        {
            get { return Logger.Logger.IsEnabledFor(Level.Trace); }
        }

        public void Trace(string message)
        {
            if (IsTraceEnabled) Logger.Debug(message);
        }

        public void Trace(object obj)
        {
            if (IsTraceEnabled) Logger.Debug(obj);
        }

        public void Trace(string message, params object[] args)
        {
            if (IsTraceEnabled) Logger.DebugFormat(message, args);
        }

        public void Trace(string message, Exception exception)
        {
            if (IsTraceEnabled) Logger.Debug(message, exception);
        }

        public bool IsWarnEnabled
        {
            get { return Logger.IsWarnEnabled; }
        }

        public void Warn(string message)
        {
            Logger.Warn(message);
        }

        public void Warn(object obj)
        {
            Logger.Warn(obj);
        }

        public void Warn(string message, params object[] args)
        {
            Logger.WarnFormat(message, args);
        }

        public void Warn(string message, Exception exception)
        {
            Logger.Warn(message, exception);
        }
    }
}