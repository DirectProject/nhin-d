using System;
using System.Configuration;
using System.Web;

namespace NHINDirect.Diagnostics
{
    ///<summary>
    /// The LogFileSection class allows us to specify LogFileSettings in an
    /// app.config file.
    ///</summary>
    /// <example>
    ///   &lt;!-- place this in the configSections --&gt;
    ///   &lt;section name="logging" type="NHINDirect.Diagnostics.LogFileSection"/&gt;
    ///   &lt;!-- insert into your app.config file --&gt;
    ///   &lt;logging&gt;
    ///     &lt;location directory="~\Log" name="ConfigService" extension="log" /&gt;
    ///     &lt;behavior rolloverFrequency="24" loggingLevel="Debug" /&gt;
    ///     &lt;eventLog threshold="Fatal" source="nhinConfigService" /&gt;
    ///   &lt;/logging&gt;
    /// </example>
    public class LogFileSection : ConfigurationSection
    {
        private const string EventLogTag = "eventLog";
        private const string BehaviorTag = "behavior";
        private const string LocationTag = "location";

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

            LogFileSettings settings = section.ToSettings();
            if (settings.DirectoryPath.Contains("~"))
            {
                settings.DirectoryPath = HttpContext.Current != null 
                    ? HttpContext.Current.Server.MapPath(settings.DirectoryPath)
                    : settings.DirectoryPath.Replace("~", Environment.CurrentDirectory);
            }
            return settings;
        }

        /// <summary>
        /// The location information used to define the path to the log file.
        /// </summary>
        /// <example>
        ///     &lt;location directory="~\Log" name="ConfigService" extension="log" /&gt;
        /// </example>
        [ConfigurationProperty(LocationTag, IsRequired = true)]
        public LogLocationElement Location
        {
            get { return (LogLocationElement)this[LocationTag]; }
            set { this[LocationTag] = value; }
        }

        /// <summary>
        /// The behavior of the logging framework.
        /// </summary>
        /// <example>
        ///     &lt;behavior rolloverFrequency="24" loggingLevel="Debug" /&gt;
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
        ///     &lt;eventLog threshold="Fatal" source="nhinConfigService" /&gt;
        /// </example>
        [ConfigurationProperty(EventLogTag)]
        public EventLogElement EventLog
        {
            get { return (EventLogElement)this[EventLogTag]; }
            set { this[EventLogTag] = value; }
        }

        private LogFileSettings ToSettings()
        {
            return new LogFileSettings
                       {
                           DirectoryPath = Location.Directory,
                           NamePrefix = Location.Name,
                           Ext = Location.Extension,
                           FileChangeFrequency = Behavior.RolloverFrequency,
                           Level = Behavior.Level,
                           EventLogLevel = EventLog.Threshold,
                           EventLogSource = EventLog.Source
                       };
        }
    }

    /// <summary>
    /// The location information used to define the path to the log file.
    /// </summary>
    /// <example>
    ///     &lt;location directory="~\Log" name="ConfigService" extension="log" /&gt;
    /// </example>
    public class LogLocationElement : ConfigurationElement
    {
        private const string DirectoryTag = "directory";
        private const string NameTag = "name";
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
        /// The log file name's. 
        ///</summary>
        [ConfigurationProperty(NameTag, IsRequired = true)]
        public string Name
        {
            get { return (string)this[NameTag]; }
            set { this[NameTag] = value; }
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
    ///     &lt;behavior rolloverFrequency="24" loggingLevel="Debug" /&gt;
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
        public int RolloverFrequency
        {
            get { return (int)this[RolloverFrequencyTag]; }
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
