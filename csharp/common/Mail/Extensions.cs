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
using System.Linq;
using System.Collections.Generic;
using System.Text;
using System.IO;
using System.Net.Mail;
using Health.Direct.Common.Extensions;
using Health.Direct.Common.Mail;

namespace Health.Direct.Common.Mail
{
    /// <summary>
    /// Extension methods useful for Mail operations.
    /// </summary>
    public static class Extensions
    {
        /// <summary>
        /// Tests if an address has domain <paramref name="domain"/> by mail string comparison rules
        /// </summary>
        /// <param name="address">The address to test</param>
        /// <param name="domain">The domain name to test the address against</param>
        /// <returns><c>true</c> if the domain portion of <paramref name="address"/> is <paramref name="domain"/>, <c>false</c> otherwise</returns>
        public static bool DomainEquals(this MailAddress address, string domain)
        {
            if (string.IsNullOrEmpty(domain))
            {
                throw new ArgumentException("value was null or empty", "domain");
            }

            return MailStandard.Equals(address.Host, domain);
        }
        

        /// <summary>
        /// Adds an enumeration of addresses to this collection
        /// </summary>
        /// <param name="addresses">This collection</param>
        /// <param name="newAddresses">The enumeration of <see cref="MailAddress"/> instances to add</param>
        public static void Add(this MailAddressCollection addresses, IEnumerable<MailAddress> newAddresses)
        {
            foreach (MailAddress address in newAddresses)
            {
                addresses.Add(address);
            }
        }
        
        /// <summary>
        /// Serializes the address, such that the result is compliant with the line folding recommendations
        /// of the SMTP spec
        /// </summary>
        /// <param name="addresses">Addresses to serialize</param>
        public static string ToStringWithFolding(this MailAddressCollection addresses)
        {
            StringBuilder builder = new StringBuilder();
            foreach(MailAddress address in addresses)
            {
                if (builder.Length > 0)
                {
                    builder.Append(',');
                    builder.Append(MailStandard.CRLF);
                    builder.Append(' ');
                }
                builder.Append(address.ToString());
            }
            
            return builder.ToString();
        }
        
        /// <summary>
        /// Sends this <see cref="MailMessage"/> to the specified path.
        /// </summary>
        /// <param name="message">The message to send</param>
        /// <param name="folderPath">The path to send the message to.</param>
        public static void SendToFolder(this MailMessage message, string folderPath)
        {
            if (string.IsNullOrEmpty(folderPath))
            {
                throw new ArgumentException("value was null or empty", "folderPath");
            }

            SmtpClient smtpClient = new SmtpClient();
            smtpClient.DeliveryMethod = SmtpDeliveryMethod.SpecifiedPickupDirectory;
            smtpClient.PickupDirectoryLocation = folderPath;
            smtpClient.Send(message);
        }

        /// <summary>
        /// Warning: this writes to disk and is NOT efficient. 
        /// This convenience method was created to make it easy for you to Test. 
        /// <see cref="MailMessage"/> does not support writing a messages to a string. It only
        /// writes to a folder. 
        /// 
        /// This method creates a temporary folder in the app's Temp Directory to deposit the message into.
        /// Then it loads the message text, and deletes the folder
        /// </summary>
        /// <param name="message">The message to serialize</param>
        /// <returns>message text</returns>
        public static string Serialize(this MailMessage message)
        {
            string tempFolder = Path.GetTempPath();
            string folderPath = Path.Combine(tempFolder, "Mail_" + StringExtensions.UniqueString());
            Directory.CreateDirectory(folderPath);
            try
            {
                message.SendToFolder(folderPath);
                string fileName = Directory.GetFiles(folderPath).FirstOrDefault();
                if (string.IsNullOrEmpty(fileName))
                {
                    throw new InvalidOperationException("Serialization failed");
                }
                
                return File.ReadAllText(Path.Combine(folderPath, fileName));
            }
            finally
            {
                Directory.Delete(folderPath, true);
            }
        }
        
        /// <summary>
        /// Formats a set of mail addresses in the format expected by SMTP Server's envelope fields
        /// </summary>
        public static string ToSmtpServerEnvelopeAddresses(this MailAddressCollection addresses)
        {            
            StringBuilder builder = new StringBuilder();
            for (int i = 0, count = addresses.Count; i < count; ++i)
            {
                if (i > 0)
                {
                    builder.Append(';');
                }
                builder.Append("SMTP:");
                builder.Append(addresses[i].Address);
            }            
            
            return builder.ToString();
        }
    }
}