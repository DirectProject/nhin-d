/* 
 Copyright (c) 2010, Direct Project
 All rights reserved.

 Authors:
    John Theisen     john.theisen@kryptiq.com
  
Redistribution and use in source and binary forms, with or without modification, are permitted provided that the following conditions are met:

Redistributions of source code must retain the above copyright notice, this list of conditions and the following disclaimer.
Redistributions in binary form must reproduce the above copyright notice, this list of conditions and the following disclaimer in the documentation and/or other materials provided with the distribution.
Neither the name of the The Direct Project (nhindirect.org). nor the names of its contributors may be used to endorse or promote products derived from this software without specific prior written permission.
THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 
*/

using System;
using System.IO;

using NHINDirect.Diagnostics;

using NLog;
using NLog.Config;
using NLog.Targets;
using NLog.Win32.Targets;

namespace Health.Direct.Diagnostics.NLog
{
    public class NLogFactory : ILogFactory
    {
        // TODO: not sure if this is the way we want to configure the logger, however, this
        // honors the principle of being a code based configuration vs XML/file based.
        public NLogFactory(LogFileSettings settings)
        {
            Target target
                = new FileTarget
                      {
                          Layout = "${longdate} [${threadid}] ${level} ${logger} - ${message}",
                          FileName = CreatePathFromSettings(settings, ""),
                          ArchiveFileName = CreatePathFromSettings(settings, ".{###}"),

                          // TODO: expose this up to the LogFileSettings
                          ArchiveEvery = settings.FileChangeFrequency < 24
                                             ? FileTarget.ArchiveEveryMode.Hour
                                             : FileTarget.ArchiveEveryMode.Day,
                          ArchiveNumbering = FileTarget.ArchiveNumberingMode.Rolling
                      };

            LoggingConfiguration config = new LoggingConfiguration();

            LogLevel level = ConvertLogLevel(settings.Level);
            config.LoggingRules.Add(new LoggingRule("*", level, target));

            // if the event log level is specified, then we will log the the Application EventLog.
            // Default is to only log Fatal messages.
            if (settings.EventLogLevel != LoggingLevel.Off)
            {
                EventLogTarget eventLogTarget
                    = new EventLogTarget
                          {
                              Layout = "${logger}: ${message}${newline}${exception:format=ToString}",
                              Source = settings.EventLogSource,
                              Log = "Application",
                          };

                LogLevel eventLogLevel = ConvertLogLevel(settings.EventLogLevel);
                config.LoggingRules.Add(new LoggingRule("*", eventLogLevel, eventLogTarget));
            }

            LogManager.Configuration = config;
        }

        public ILogger GetLogger(string name)
        {
            return new NLogLogger(LogManager.GetLogger(name));
        }

        public ILogger GetLogger(Type loggerType)
        {
            return GetLogger(loggerType.FullName);
        }

        private static string CreatePathFromSettings(LogFileSettings settings, string suffix)
        {
            return Path.ChangeExtension(
                Path.Combine(settings.DirectoryPath, settings.NamePrefix + suffix),
                NormalizeExtension(settings));
        }

        private static string NormalizeExtension(LogFileSettings settings)
        {
            return "." + settings.Ext.TrimStart('.');
        }

        private static LogLevel ConvertLogLevel(LoggingLevel level)
        {
            switch (level)
            {
                case LoggingLevel.Off:
                    return LogLevel.Off;
                case LoggingLevel.Debug:
                    return LogLevel.Debug;
                case LoggingLevel.Error:
                    return LogLevel.Error;
                case LoggingLevel.Fatal:
                    return LogLevel.Fatal;
                case LoggingLevel.Info:
                    return LogLevel.Info;
                case LoggingLevel.Trace:
                    return LogLevel.Trace;
                case LoggingLevel.Warn:
                    return LogLevel.Warn;
            }

            throw new ArgumentOutOfRangeException("level", "Unknown value for LoggingLevel - " + level);
        }
    }
}