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
        /// Initializes an instance with a default log rotation interval, local time, and
        /// an extension of .log
        /// </summary>
        public LogFileSettings()
        {
            this.FileChangeFrequency = 24;
            this.UseUTC = false;
            this.Ext = "log";
        }
        
        /// <summary>
        /// Gets and sets the directory where log files are created.
        /// </summary>
        /// <remarks>Defaults to the system Temp file path.</remarks>
        /// <value>A <see cref="string"/> representation of the directory path</value>
        [XmlElement]
        public string DirectoryPath
        {
            get;set;
        }

        /// <summary>
        /// Gets and sets the name prefix used for logging
        /// </summary>
        /// <remarks>Defaults to the process name</remarks>
        [XmlElement]
        public string NamePrefix
        {
            get;
            set;
        }

        /// <summary>
        /// Gets and sets the log file extension.
        /// </summary>
        /// <remarks>Defaults to <c>.log</c></remarks>
        [XmlElement]
        public string Ext
        {
            get;
            set;
        }

        /// <summary>
        /// Gets and sets the log file rotation period.
        /// </summary>
        /// <value>The number of hours between log rotation.</value>
        [XmlElement]
        public int FileChangeFrequency
        {
            get;
            set;
        }

        /// <summary>
        /// Gets and sets if these settings define UTC for logging
        /// </summary>
        [XmlElement]
        public bool UseUTC
        {
            get;
            set;
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
        }

        /// <summary>
        /// Sets default values for properties.
        /// </summary>
        public void SetDefaults()
        {           
            if (string.IsNullOrEmpty(this.NamePrefix))
            {
                this.NamePrefix = this.GetProcessName();
            }
            
            if (string.IsNullOrEmpty(this.DirectoryPath))
            {
                this.DirectoryPath = System.IO.Path.Combine(System.IO.Path.GetTempPath(), "LogFiles");
                System.IO.Directory.CreateDirectory(this.DirectoryPath);
            }
        }
        
        string GetProcessName()
        {
            try
            {
                using (System.Diagnostics.Process process = System.Diagnostics.Process.GetCurrentProcess())
                {
                    return System.IO.Path.GetFileNameWithoutExtension(process.MainModule.ModuleName);
                }
            }
            catch
            {
                return "Unknown";
            }
            
        }
        
        // <summary>
        // Creates a writer instance using these settings.
        // </summary>
        // <returns></returns>
		//public LogWriter CreateWriter()
		//{
		//    this.Validate();
		//    return new LogWriter(this.DirectoryPath, this.NamePrefix, this.Ext, this.FileChangeFrequency, this.UseUTC);
		//}
    }
}
