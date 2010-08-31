/* 
 Copyright (c) 2010, NHIN Direct Project
 All rights reserved.

 Authors:
    Umesh Madan     umeshma@microsoft.com
  
Redistribution and use in source and binary forms, with or without modification, are permitted provided that the following conditions are met:

Redistributions of source code must retain the above copyright notice, this list of conditions and the following disclaimer.
Redistributions in binary form must reproduce the above copyright notice, this list of conditions and the following disclaimer in the documentation and/or other materials provided with the distribution.
Neither the name of the The NHIN Direct Project (nhindirect.org). nor the names of its contributors may be used to endorse or promote products derived from this software without specific prior written permission.
THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 
*/
using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.IO;

namespace NHINDirect.Diagnostics
{
    /// <summary>
    /// Provides a writer class specialized for log files.
    /// </summary>
    /// <remarks>
    /// The files created named with a prefix, a timestamp and a sequential numeric identifier.
    /// </remarks>
    public class LogWriter : TextWriter, IDisposable
    {        
        string m_machineName;
        string m_directoryPath;
        string m_namePrefix;
        string m_ext;
        int m_fileChangeFreqHours = 24;
        long m_fileNumber = -1;
        StreamWriter m_writer;
        string m_currentFilePath;
        
        /// <summary>
        /// Initializes an instance using a .log extension and 24hr log rotation.
        /// </summary>
        /// <param name="directoryPath">The directory path to which to write log files</param>
        /// <param name="namePrefix">The prefix for log file name.</param>
        public LogWriter(string directoryPath, string namePrefix)
            : this(directoryPath, namePrefix, "log", 24)
        {
        }

        /// <summary>
        /// Initializes an instance using 24hr log rotation.
        /// </summary>
        /// <param name="directoryPath">The directory path to which to write log files</param>
        /// <param name="namePrefix">The prefix for log file name.</param>
        /// <param name="ext">The file extension to use</param>
        public LogWriter(string directoryPath, string namePrefix, string ext)
            : this(directoryPath, namePrefix, ext, 24)
        {
        }

        /// <summary>
        /// Initializes an instance.
        /// </summary>
        /// <param name="directoryPath">The directory path to which to write log files</param>
        /// <param name="namePrefix">The prefix for log file name.</param>
        /// <param name="ext">The file extension to use</param>
        /// <param name="fileChangeFreqHours">The number of hours between log file rotation.</param>
        public LogWriter(string directoryPath, string namePrefix, string ext, int fileChangeFreqHours)
            : this(directoryPath, namePrefix, ext, fileChangeFreqHours, false)
        {
        }

        /// <summary>
        /// Initializes an instance.
        /// </summary>
        /// <param name="directoryPath">The directory path to which to write log files</param>
        /// <param name="namePrefix">The prefix for log file name.</param>
        /// <param name="ext">The file extension to use</param>
        /// <param name="fileChangeFreqHours">The number of hours between log file rotation.</param>
        /// <param name="useUTC">Specifies if this writer should timestamp in UTC (<c>true</c>) or local time (<c>false</c>)</param>
        public LogWriter(string directoryPath, string namePrefix, string ext, int fileChangeFreqHours, bool useUTC)
        {
            if (string.IsNullOrEmpty(directoryPath) || string.IsNullOrEmpty(namePrefix) || string.IsNullOrEmpty(ext) || fileChangeFreqHours <= 0 || fileChangeFreqHours > 24)
            {
                throw new ArgumentException();
            }

            m_directoryPath = directoryPath;
            m_namePrefix = namePrefix;
            m_ext = ext;
            m_fileChangeFreqHours = fileChangeFreqHours;
            this.UTCTime = useUTC;
            m_machineName = this.GetMachineName();
        }

        /// <summary>
        /// Returns the <see cref="Encoding"/> in which the output is written.
        /// </summary>
        /// <remarks>Always UTF8</remarks>
        public override Encoding Encoding
        {
            get
            {
                return Encoding.UTF8;
            }
        }


        /// <summary>
        /// Defines whether to keep the file handle open (<c>true</c>) or close it (<c>false</c>) in between writes.
        /// </summary>
        public bool KeepWriterOpen = true;

        /// <summary>
        /// Defines if this writer uses UTC for timestamps.
        /// </summary>
        public bool UTCTime = false;
        
        /// <summary>
        /// Gets the current file path.
        /// </summary>
        public string CurrentFilePath
        {
            get
            {
                return m_currentFilePath;
            }
        }
        
        DateTime Now
        {
            get
            {
                return this.UTCTime ? DateTime.UtcNow : DateTime.Now;
            }
        }

        /// <summary>
        /// Logs an exception as an error event.
        /// </summary>
        /// <param name="exception">The exception to log.</param>
        public void WriteError(Exception exception)
        {
            this.WriteLine(LogEventType.Error, exception);
        }

        /// <summary>
        /// Writes an object string representation to the log.
        /// </summary>
        /// <param name="type">The <see cref="LogEventType"/> severity level.</param>
        /// <param name="message">The object whose string representation will be to logged.</param>
        public void WriteLine(LogEventType type, object message)
        {
            this.WriteLine(type, message.ToString());
        }

        /// <summary>
        /// Writes a message to the log
        /// </summary>
        /// <param name="type">The <see cref="LogEventType"/> severity level.</param>
        /// <param name="message">The message to log.</param>
        public void WriteLine(LogEventType type, string message)
        {
            this.WriteLine(type.ToString(), message);
        }


        /// <summary>
        /// Writes a message of nonstandard severity level to the log.
        /// </summary>
        /// <param name="type">The non-standard message type</param>
        /// <param name="message">The message to log.</param>
        public void WriteLine(string type, string message)
        {
            string log;            
            if (m_machineName != null)
            {
                log = string.Format("{0},{1},{2},{3}", m_machineName, type, this.Now, message);
            }
            else
            {
                log = string.Format("{0},{1},{2}", type, this.Now, message);
            }

            this.WriteText(log);
        }
    
        /// <summary>
        /// Writes a message to the log.
        /// </summary>
        /// <param name="message">The message to write.</param>
        public override void WriteLine(string message)
        {
            string log;
            if (m_machineName != null)
            {
                log = string.Format("{0},{1},{2}", m_machineName, this.Now, message);
            }
            else
            {
                log = string.Format("{0},{1}", this.Now, message);
            }
            
            this.WriteText(message);
        }


        void WriteText(string message)
        {
            this.EnsureWriter();

            m_writer.WriteLine(message);
            m_writer.Flush();

            if (!this.KeepWriterOpen)
            {
                this.Close();
            }
        }

        void EnsureWriter()
        {
            this.EnsureWriter(this.Now);
        }

        // TODO: Why do we let people pass in a custom DateTime here?
        
        /// <summary>
        /// Ensures that the writer is writing to the correct file location (after necessary log rotations, etc.).
        /// </summary>
        /// <param name="now">The time at which to calculate rotation.</param>
        public void EnsureWriter(DateTime now)
        {
            if (m_writer == null || (AbsoluteHour(now) / m_fileChangeFreqHours) != m_fileNumber)
            {
                this.NewFile(now);
            }
        }
        
        const string FileDateFormat = "yyyyMMdd";
        void NewFile(DateTime now)
        {
            this.Close();
            
            string fileName;
            long fileIndex = AbsoluteHour(now) / m_fileChangeFreqHours;
            m_fileNumber = fileIndex;
            
            int id = -1;
            int maxid = 1024;
            do
            {
                id++;
                if (m_fileChangeFreqHours < 24)
                {
                    fileName = this.CreateHourlyName(now, now.Hour, id);
                }
                else
                {
                    fileName = this.CreateDailyName(now, id);
                }
                string filePath = Path.Combine(m_directoryPath, fileName);
                try
                {
                    m_writer = new StreamWriter(File.Open(filePath, FileMode.Append, FileAccess.Write, FileShare.Read));
                    m_writer.AutoFlush = true;                    
                    m_currentFilePath = filePath;
                    return;
                }
                catch
                {
                    if (!File.Exists(filePath))
                    {
                        throw;
                    }
                }
            }
            while (id < maxid);

            throw new InvalidOperationException("Too many open log files");
        }

        /// <summary>
        /// Closes the file handle.
        /// </summary>
        public override void Close()
        {
            if (m_writer != null)
            {
                m_writer.Close();
                m_writer = null;
            }
        }
        
        string CreateDailyName(DateTime now, int id)
        {
            string fileName;
            
            if (id > 0)
            {
                fileName = string.Format("{0}_{1}_{2}.{3}", m_namePrefix, now.ToString(FileDateFormat), id, m_ext);
            }
            else
            {
                fileName = string.Format("{0}_{1}.{2}", m_namePrefix, now.ToString(FileDateFormat), m_ext);
            }
            
            return fileName;
        }

        string CreateHourlyName(DateTime now, long fileIndex, int id)
        {
            string fileName;

            if (id > 0)
            {
                fileName = string.Format("{0}_{1}_{2}_{3}.{4}", m_namePrefix, now.ToString(FileDateFormat), fileIndex, id, m_ext);
            }
            else
            {
                fileName = string.Format("{0}_{1}_{2}.{3}", m_namePrefix, now.ToString(FileDateFormat), fileIndex, m_ext);
            }

            return fileName;
        }
        
        string GetMachineName()
        {
            try
            {
                return Environment.MachineName;
            }
            catch
            {
            }
            
            return null;
        }
        
        internal long AbsoluteHour(DateTime now)
        {
            return (long) (now.Ticks / TimeSpan.TicksPerHour);
        }
    }
}
