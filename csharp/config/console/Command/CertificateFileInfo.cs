/* 
 Copyright (c) 2010, Direct Project
 All rights reserved.

 Authors:
    Umesh Madan     umeshma@microsoft.com
  
Redistribution and use in source and binary forms, with or without modification, are permitted provided that the following conditions are met:

Redistributions of source code must retain the above copyright notice, this list of conditions and the following disclaimer.
Redistributions in binary form must reproduce the above copyright notice, this list of conditions and the following disclaimer in the documentation and/or other materials provided with the distribution.
Neither the name of The Direct Project (directproject.org) nor the names of its contributors may be used to endorse or promote products derived from this software without specific prior written permission.
THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 
*/
using System;
using System.IO;
using System.Security.Cryptography.X509Certificates;
using Health.Direct.Config.Store;
using Health.Direct.Common.Certificates;
using Health.Direct.Config.Tools;
using Health.Direct.Config.Tools.Command;

namespace Health.Direct.Config.Console.Command
{
    public class CertificateFileInfo
    {
        internal const string Usage =
            "\t options:"
            + Constants.CRLF + "\t filePath: path fo the certificate file. Can be .DER, .CER or .PFX"
            + Constants.CRLF + "\t password: (optional) file password. Enter 'null' if no password."
            + Constants.CRLF + "\t status: (optional) " + Constants.EntityStatusString;
        
        internal CertificateFileInfo(string filePath, string password, EntityStatus status)
        {
            m_filePath = filePath;
            m_password = password;
            m_status = status;
        }

        private readonly string m_filePath;
        private readonly string m_password;
        private readonly EntityStatus m_status;

        /// <summary>
        /// Path to the certificate file
        /// </summary>
        internal string FilePath
        {
            get { return m_filePath; }
        }

        /// <summary>
        /// (Optional) Password for the cert file
        /// </summary>
        internal string Password
        {
            get { return m_password; }
        }

        /// <summary>
        /// New, Enabled, Disabled. Default is 'New'
        /// </summary>
        internal EntityStatus Status
        {
            get { return m_status; }
        }

        internal MemoryX509Store LoadCerts()
        {
            return LoadCerts(X509KeyStorageFlags.Exportable);
        }

        internal MemoryX509Store LoadCerts(X509KeyStorageFlags flags)
        {
            MemoryX509Store certStore = new MemoryX509Store();
            LoadCerts(certStore, flags);
            return certStore;
        }

        internal void LoadCerts(MemoryX509Store certStore, X509KeyStorageFlags flags)
        {
            if (!File.Exists(this.FilePath))
            {
                throw new FileNotFoundException("File does not exist", this.FilePath);
            }
            
            string ext = Path.GetExtension(this.FilePath) ?? string.Empty;
            switch (ext.ToLower())
            {
                default:
                    certStore.ImportKeyFile(this.FilePath, flags);
                    break;

                case ".pfx":
                    certStore.ImportKeyFile(FilePath, this.Password, flags);
                    break;
            }
        }

        internal static CertificateFileInfo Create(int firstArg, string[] args)
        {
            string filePath = args.GetRequiredValue(firstArg);
            string password = args.GetOptionalValue(firstArg + 1, string.Empty);
            if (!string.IsNullOrEmpty(password) && password.Equals("null", StringComparison.OrdinalIgnoreCase))
            {
                password = string.Empty;
            }

            EntityStatus status = args.GetOptionalEnum(firstArg + 2, EntityStatus.New);
            return new CertificateFileInfo(filePath, password, status);
        }
    }
}
