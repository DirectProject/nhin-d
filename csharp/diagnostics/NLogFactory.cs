/* 
 Copyright (c) 2010, Direct Project
 All rights reserved.

 Authors:
    John Theisen     john.theisen@kryptiq.com
  
Redistribution and use in source and binary forms, with or without modification, are permitted provided that the following conditions are met:

Redistributions of source code must retain the above copyright notice, this list of conditions and the following disclaimer.
Redistributions in binary form must reproduce the above copyright notice, this list of conditions and the following disclaimer in the documentation and/or other materials provided with the distribution.
Neither the name of The Direct Project (directproject.org) nor the names of its contributors may be used to endorse or promote products derived from this software without specific prior written permission.
THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 
*/

using System;
using System.IO;

using Health.Direct.Common.Diagnostics;

using NLog;
using NLog.Config;
using NLog.Targets;
using NLog.Win32.Targets;

// these have been renamed in the NLog 2.0 version
using ArchiveNumberingMode = NLog.Targets.FileTarget.ArchiveNumberingMode;
using FileArchivePeriod = NLog.Targets.FileTarget.ArchiveEveryMode;

namespace Health.Direct.Diagnostics.NLog
{
    public class NLogFactory : ILogFactory
    {
        public NLogFactory()
            : this(LogFileSection.GetAsSettings())
        {
        }

        // TODO: not sure if this is the way we want to configure the logger, however, this
        // honors the principle of being a code based configuration vs XML/file based.
        public NLogFactory(LogFileSettings settings)
        {
            string fileName = settings.NamePrefix;
            string archiveFileName = settings.NamePrefix + ".{###}";
            if (!string.IsNullOrEmpty(settings.ArchiveName))
            {
                archiveFileName = settings.ArchiveName;
            }

            FileTarget target
                = new FileTarget
                      {
                          Layout = "${longdate} [${threadid}] ${level} ${logger} - ${message}",
                          FileName = CreatePathFromSettings(settings.DirectoryPath, fileName, settings.Ext),
                          ArchiveFileName = CreatePathFromSettings(settings.DirectoryPath, archiveFileName, settings.Ext),
                          ArchiveEvery = ConvertRollingPeriod(settings.RolloverFrequency),
                          ArchiveNumbering = ArchiveNumberingMode.Rolling,
                          MaxArchiveFiles = 100,
                          DeleteOldFileOnStartup = false,
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
            LogManager.ReconfigExistingLoggers();
        }

        private static FileArchivePeriod ConvertRollingPeriod(RolloverPeriod period)
        {
            switch (period)
            {
                case RolloverPeriod.Day:
                    return FileArchivePeriod.Day;
                case RolloverPeriod.Hour:
                    return FileArchivePeriod.Hour;
                case RolloverPeriod.Minute:
                    return FileArchivePeriod.Minute;
                case RolloverPeriod.Month:
                    return FileArchivePeriod.Month;
                case RolloverPeriod.None:
                    return FileArchivePeriod.None;
                case RolloverPeriod.Year:
                    return FileArchivePeriod.Year;
            }

            throw new ArgumentException("Unexpected value - " + period);
        }

        public ILogger GetLogger(string name)
        {
            return new NLogLogger(LogManager.GetLogger(name));
        }

        public ILogger GetLogger(Type loggerType)
        {
            return GetLogger(loggerType.FullName);
        }

        private static string CreatePathFromSettings(string directoryPath, string filename, string extension)
        {
            return Path.Combine(directoryPath, filename) + NormalizeExtension(extension);
        }

        private static string NormalizeExtension(string extension)
        {
            return "." + extension.TrimStart('.');
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