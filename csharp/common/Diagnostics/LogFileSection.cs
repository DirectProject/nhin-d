/* 
 Copyright (c) 2010, Direct Project
 All rights reserved.

 Authors:
    John Theisen
  
Redistribution and use in source and binary forms, with or without modification, are permitted provided that the following conditions are met:

Redistributions of source code must retain the above copyright notice, this list of conditions and the following disclaimer.
Redistributions in binary form must reproduce the above copyright notice, this list of conditions and the following disclaimer in the documentation and/or other materials provided with the distribution.
Neither the name of The Direct Project (directproject.org) nor the names of its contributors may be used to endorse or promote products derived from this software without specific prior written permission.
THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 
*/
using System;
using System.Configuration;
using System.Web;
using System.Web.Hosting;

namespace Health.Direct.Common.Diagnostics
{
    ///<summary>
    /// The LogFileSection class allows us to specify LogFileSettings in an
    /// app.config file.
    ///</summary>
    /// <example>
    ///   &lt;!-- place this in the configSections --&gt;
    ///   &lt;section name="logging" type="Health.Direct.Common.Diagnostics.LogFileSection"/&gt;
    ///   &lt;!-- insert into your app.config file --&gt;
    ///   &lt;logging&gt;
    ///     &lt;file directory="~\Log" name="ConfigService" extension="log" /&gt;
    ///     &lt;behavior rolloverFrequency="Day" loggingLevel="Debug" /&gt;
    ///     &lt;eventLog threshold="Fatal" source="Health.Direct.Config.Service" /&gt;
    ///   &lt;/logging&gt;
    /// </example>
    public class LogFileSection : ConfigurationSection
    {
        private const string EventLogTag = "eventLog";
        private const string BehaviorTag = "behavior";
        private const string FileTag = "file";

        /// <summary>
        /// This is a utility method to convert the values found in the LogFileSection
        /// to <see cref="LogFileSettings"/> and to substitute the appearance of a '~'
        /// for a call to <see cref="HttpServerUtility.MapPath"/> if this is run inside
        /// of a web context, or use the <see cref="Environment.CurrentDirectory"/>.
        /// </summary>
        /// <remarks>This assumes that there is &lt;logging /&gt; section in the app.config </remarks>
        /// <returns>A <see cref="LogFileSettings"/> object.</returns>
        public static LogFileSettings GetAsSettings()
        {
            LogFileSection section = (LogFileSection)ConfigurationManager.GetSection("logging");
            if (section == null)
            {
                return null;
            }
            
            LogFileSettings settings = section.ToSettings();
            if (settings.DirectoryPath.Contains("~"))
            {
                string appDirectory = HostingEnvironment.ApplicationPhysicalPath ?? Environment.CurrentDirectory;
                settings.DirectoryPath = settings.DirectoryPath.Replace("~", appDirectory);
            }

            EventLogHelper.WriteInformation(settings.EventLogSource,
                                "Writing log file to " + settings.DirectoryPath);

            return settings;
        }

        /// <summary>
        /// The file information used to define the path to the log file.
        /// </summary>
        /// <example>
        ///     &lt;file directory="~\Log" name="ConfigService" extension="log" /&gt;
        /// </example>
        [ConfigurationProperty(FileTag, IsRequired = true)]
        public LogFileElement LogFile
        {
            get { return (LogFileElement)this[FileTag]; }
            set { this[FileTag] = value; }
        }

        /// <summary>
        /// The behavior of the logging framework.
        /// </summary>
        /// <example>
        ///     &lt;behavior rolloverFrequency="Day" loggingLevel="Debug" /&gt;
        /// </example>
        [ConfigurationProperty(BehaviorTag, IsRequired = true)]
        public LogBehaviorElement Behavior
        {
            get { return (LogBehaviorElement)this[BehaviorTag]; }
            set { this[BehaviorTag] = value; }
        }

        /// <summary>
        /// The <see cref="LoggingLevel"/> threshold when the logging framework
        /// will write to the system's EventLog. This configuration 
        /// </summary>
        /// <example>
        ///     &lt;eventLog threshold="Fatal" source="Health.Direct.Config.Service" /&gt;
        /// </example>
        [ConfigurationProperty(EventLogTag)]
        public EventLogElement EventLogInfo
        {
            get { return (EventLogElement)this[EventLogTag]; }
            set { this[EventLogTag] = value; }
        }

        private LogFileSettings ToSettings()
        {
            return new LogFileSettings
                       {
                           DirectoryPath = LogFile.Directory,
                           NamePrefix = LogFile.Name,
                           ArchiveName = LogFile.ArchiveName,
                           Ext = LogFile.Extension,
                           RolloverFrequency = Behavior.RolloverFrequency,
                           Level = Behavior.Level,
                           EventLogLevel = EventLogInfo.Threshold,
                           EventLogSource = EventLogInfo.Source
                       };
        }
    }

    /// <summary>
    /// The file information used to define the path to the log file.
    /// </summary>
    /// <example>
    ///     &lt;file directory="~\Log" name="ConfigService" extension="log" /&gt;
    /// </example>
    public class LogFileElement : ConfigurationElement
    {
        private const string DirectoryTag = "directory";
        private const string NameTag = "name";
        private const string ArchiveNameTag = "archiveName";
        private const string ExtensionTag = "extension";

        ///<summary>
        /// The directory where the log file will be written.
        ///</summary>
        [ConfigurationProperty(DirectoryTag, IsRequired = true)]
        public string Directory
        {
            get { return (string)this[DirectoryTag]; }
            set { this[DirectoryTag] = value; }
        }

        ///<summary>
        /// The log file's name. 
        ///</summary>
        [ConfigurationProperty(NameTag, IsRequired = true)]
        public string Name
        {
            get { return (string)this[NameTag]; }
            set { this[NameTag] = value; }
        }

        ///<summary>
        /// The log file's archived name. 
        ///</summary>
        [ConfigurationProperty(ArchiveNameTag, IsRequired = false)]
        public string ArchiveName
        {
            get { return (string)this[ArchiveNameTag]; }
            set { this[ArchiveNameTag] = value; }
        }

        ///<summary>
        /// The extension to use on the log file.
        ///</summary>
        [ConfigurationProperty(ExtensionTag, IsRequired = true)]
        public string Extension
        {
            get { return (string)this[ExtensionTag]; }
            set { this[ExtensionTag] = value; }
        }
    }

    /// <summary>
    /// The behavior of the logging framework.
    /// </summary>
    /// <example>
    ///     &lt;behavior rolloverFrequency="Day" loggingLevel="Debug" /&gt;
    /// </example>
    public class LogBehaviorElement : ConfigurationElement
    {
        private const string RolloverFrequencyTag = "rolloverFrequency";
        private const string LoggingLevelTag = "loggingLevel";

        ///<summary>
        /// The log file rollover frequency in hours. All logging frameworks
        /// don't support this, so we'll likely turn this into an enumerated
        /// value.
        ///</summary>
        [ConfigurationProperty(RolloverFrequencyTag, IsRequired = true)]
        public RolloverPeriod RolloverFrequency
        {
            get { return (RolloverPeriod)this[RolloverFrequencyTag]; }
            set { this[RolloverFrequencyTag] = value; }
        }

        ///<summary>
        /// The <see cref="LoggingLevel"/> that sets the level of logging
        /// we want to see.
        ///</summary>
        [ConfigurationProperty(LoggingLevelTag, IsRequired = true)]
        public LoggingLevel Level
        {
            get { return (LoggingLevel)this[LoggingLevelTag]; }
            set { this[LoggingLevelTag] = value; }
        }
    }

    /// <summary>
    /// The <see cref="LoggingLevel"/> threshold when the logging framework
    /// will write to the system's EventLog. If this is not specified in the
    /// configuration file it will default to <see cref="LoggingLevel.Fatal"/>
    /// for the <see cref="Threshold"/> and 'nhin' for <see cref="Source"/>.
    /// </summary>
    /// <example>
    ///     &lt;eventLog threshold="Fatal" source="nhin" /&gt;
    /// </example>
    public class EventLogElement : ConfigurationElement
    {
        private const string ThresholdTag = "threshold";
        private const string SourceTag = "source";

        ///<summary>
        /// The default ctor for 
        ///</summary>
        public EventLogElement()
        {
            Threshold = LoggingLevel.Fatal;
            Source = "nhin";
        }

        ///<summary>
        /// The <see cref="LoggingLevel"/> threshold at which we'll write to the EventLog.
        ///</summary>
        [ConfigurationProperty(ThresholdTag)]
        public LoggingLevel Threshold
        {
            get { return (LoggingLevel)this[ThresholdTag]; }
            set { this[ThresholdTag] = value; }
        }

        /// <summary>
        /// The name of the event source to use when writing to the EventLog.
        /// </summary>
        [ConfigurationProperty(SourceTag)]
        public string Source
        {
            get { return (string)this[SourceTag]; }
            set { this[SourceTag] = value; }
        }
    }
}