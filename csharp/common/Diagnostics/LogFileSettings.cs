/* 
 Copyright (c) 2010, Direct Project
 All rights reserved.

 Authors:
    John Theisen     jtheisen@kryptiq.com
  
Redistribution and use in source and binary forms, with or without modification, are permitted provided that the following conditions are met:

Redistributions of source code must retain the above copyright notice, this list of conditions and the following disclaimer.
Redistributions in binary form must reproduce the above copyright notice, this list of conditions and the following disclaimer in the documentation and/or other materials provided with the distribution.
Neither the name of The Direct Project (directproject.org) nor the names of its contributors may be used to endorse or promote products derived from this software without specific prior written permission.
THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 
*/
using System;
using System.Xml.Serialization;

namespace Health.Direct.Common.Diagnostics
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
            RolloverFrequency = RolloverPeriod.Day;
            Ext = "log";
            Level = LoggingLevel.Error;

            EventLogSource = "Health.Direct";
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
        /// Gets and sets the archive name prefix used for logging.
        /// If null or empty then the <see cref="NamePrefix"/> will be used with
        /// any appropriate suffix.
        /// </summary>
        /// <remarks>Defaults to the process name</remarks>
        [XmlElement]
        public string ArchiveName
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
        /// <value>The enumerated value - minute, hour, day, month, year or none.</value>
        [XmlElement]
        public RolloverPeriod RolloverFrequency
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
            if (this.RolloverFrequency <= 0)
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