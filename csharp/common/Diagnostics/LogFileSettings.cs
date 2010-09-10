using System;
using System.Xml.Serialization;

namespace NHINDirect.Diagnostics
{
    /// <summary>
    /// Defines settings for logging.
    /// </summary>
    [XmlType("LogSettings")]
    public class LogFileSettings
    {
        /// <summary>
        /// Initializes an instance with a default log rotation interval, and
        /// an extension of .log
        /// </summary>
        public LogFileSettings()
        {
            FileChangeFrequency = 24;
            Ext = "log";
            Level = LoggingLevel.Error;

            EventLogSource = "nhin";
            EventLogLevel = LoggingLevel.Fatal;
        }
        
        /// <summary>
        /// Gets and sets the directory where log files are created.
        /// </summary>
        /// <remarks>Defaults to the system Temp file path.</remarks>
        /// <value>A <see cref="string"/> representation of the directory path</value>
        [XmlElement]
        public string DirectoryPath
        {
            get; set;
        }

        /// <summary>
        /// Gets and sets the name prefix used for logging
        /// </summary>
        /// <remarks>Defaults to the process name</remarks>
        [XmlElement]
        public string NamePrefix
        {
            get; set;
        }

        /// <summary>
        /// Gets and sets the log file extension.
        /// </summary>
        /// <remarks>Defaults to <c>.log</c></remarks>
        [XmlElement]
        public string Ext
        {
            get; set;
        }

        /// <summary>
        /// Gets and sets the log file rotation period.
        /// </summary>
        /// <value>The number of hours between log rotation.</value>
        [XmlElement]
        public int FileChangeFrequency
        {
            get; set;
        }

        ///<summary>
        /// Defines the minimum level of logging for the log files.
        ///</summary>
        [XmlElement]
        public LoggingLevel Level
        {
            get; set;
        }

        ///<summary>
        /// The minimum level for logging to the event log.
        ///</summary>
        [XmlElement]
        public LoggingLevel EventLogLevel
        {
            get; set;
        }

        ///<summary>
        /// The source name used when writing to the event log.
        ///</summary>
        [XmlElement]
        public string EventLogSource
        {
            get; set;
        }

        /// <summary>
        /// Validates settings, throwing an exception if settings are invalid.
        /// </summary>
        /// <exception cref="ArgumentException">Thrown if settings are invalid.</exception>
        public virtual void Validate()
        {
            if (string.IsNullOrEmpty(this.DirectoryPath))
            {
                throw new ArgumentException("DirectoryPath not specified");
            }
            if (string.IsNullOrEmpty(this.NamePrefix))
            {
                throw new ArgumentException("Name prefix not specified");
            }
            if (string.IsNullOrEmpty(this.Ext))
            {
                throw new ArgumentException("Extension not specified");
            }
            if (this.FileChangeFrequency <= 0)
            {
                throw new ArgumentException("FileIntervalHours not specified");
            }
            if (this.Level == 0)
            {
                throw new ArgumentException("Level not specified");
            }
        }
    }
}
