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
    public class LogWriter : TextWriter, IDisposable
    {
        public enum EventType
        {
            Critical,
            Error,
            Warning,
            Information
        }
        
        string m_machineName;
        string m_directoryPath;
        string m_namePrefix;
        string m_ext;
        int m_newFileIntervalHours = 24;
        int m_fileIndex = -1;
        StreamWriter m_writer;
        
        public LogWriter(string directoryPath, string namePrefix, string ext)
            : this(directoryPath, namePrefix, ext, 24)
        {
        }
                
        public LogWriter(string directoryPath, string namePrefix, string ext, int newFileIntervalHours)
        {
            if (string.IsNullOrEmpty(directoryPath) || string.IsNullOrEmpty(namePrefix) || string.IsNullOrEmpty(ext) || newFileIntervalHours <= 0 || newFileIntervalHours > 24)
            {
                throw new ArgumentException();
            }

            m_directoryPath = directoryPath;
            m_namePrefix = namePrefix;
            m_ext = ext;
            m_newFileIntervalHours = newFileIntervalHours;
            
            m_machineName = this.GetMachineName();
        }

        public override Encoding Encoding
        {
            get
            {
                return Encoding.UTF8;
            }
        }

        public bool KeepWriterOpen = true;
        public bool UTCTime = false;

        DateTime Now
        {
            get
            {
                return this.UTCTime ? DateTime.UtcNow : DateTime.Now;
            }
        }

        public void WriteError(Exception exception)
        {
            this.WriteLine(EventType.Error, exception);
        }

        public void WriteLine(EventType type, object message)
        {
            this.WriteLine(type, message.ToString());
        }

        public void WriteLine(EventType type, string message)
        {
            this.WriteLine(type.ToString(), message);
        }

        public void WriteLine(string type, string message)
        {
            string log;            
            if (m_machineName != null)
            {
                log = string.Format("{0},{1},{2},{3}", m_machineName, type, DateTime.UtcNow, message);
            }
            else
            {
                log = string.Format("{0},{1},{2}", type, DateTime.UtcNow, message);
            }
            
            this.WriteLine(log);
        }
    
        public override void WriteLine(string message)
        {
            string log;
            if (m_machineName != null)
            {
                log = string.Format("{0},{1},{2}", m_machineName, DateTime.UtcNow, message);
            }
            else
            {
                log = string.Format("{0},{1}", DateTime.UtcNow, message);
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
            DateTime now = this.Now;
            if (m_writer == null || (now.Hour / m_newFileIntervalHours) != m_fileIndex)
            {
                this.NewFile(now);
            }
        }

        const string FileDateFormat = "yyyyMMdd";
        void NewFile(DateTime now)
        {
            this.Close();
            
            string fileName;
            int fileIndex = now.Hour / m_newFileIntervalHours;
            m_fileIndex = fileIndex;

            int id = -1;
            int maxid = 1024;
            do
            {
                id++;
                if (m_newFileIntervalHours < 24)
                {
                    fileName = this.CreateHourlyName(now, fileIndex, id);
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
                    return;
                }
                catch
                {
                }
            }
            while (id < maxid);

            throw new InvalidOperationException("Too many open log files");
        }

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

        string CreateHourlyName(DateTime now, int fileIndex, int id)
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
    }
}
