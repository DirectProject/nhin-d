using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Xml.Serialization;

namespace NHINDirect.Diagnostics
{
    [XmlType("LogSettings")]
    public class LogFileSettings
    {
        public LogFileSettings()
        {
            this.FileChangeFrequency = 24;
            this.UseUTC = false;
            this.Ext = "log";
        }
        
        [XmlElement]
        public string DirectoryPath
        {
            get;set;
        }
        [XmlElement]
        public string NamePrefix
        {
            get;
            set;
        }
        [XmlElement]
        public string Ext
        {
            get;
            set;
        }
        [XmlElement]
        public int FileChangeFrequency
        {
            get;
            set;
        }
        [XmlElement]
        public bool UseUTC
        {
            get;
            set;
        }
        
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
        
        public LogWriter CreateWriter()
        {
            this.Validate();
            return new LogWriter(this.DirectoryPath, this.NamePrefix, this.Ext, this.FileChangeFrequency, this.UseUTC);
        }
    }
}
