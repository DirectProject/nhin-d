/* 
 Copyright (c) 2010, Direct Project
 All rights reserved.

 Authors:
    Umesh Madan     umeshma@microsoft.com
    Joe Shook	    jshook@kryptiq.com
  
Redistribution and use in source and binary forms, with or without modification, are permitted provided that the following conditions are met:

Redistributions of source code must retain the above copyright notice, this list of conditions and the following disclaimer.
Redistributions in binary form must reproduce the above copyright notice, this list of conditions and the following disclaimer in the documentation and/or other materials provided with the distribution.
Neither the name of The Direct Project (directproject.org) nor the names of its contributors may be used to endorse or promote products derived from this software without specific prior written permission.
THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 
*/
using System;
using System.Collections.Generic;
using System.IO;
using System.Net.Mail;
using System.Security.Cryptography.X509Certificates;
using System.Text;
using Health.Direct.Agent;
using Health.Direct.Common.Mail;
using Health.Direct.Common.Mail.Notifications;
using Health.Direct.Common.Mime;
using CDO;
using ADODB;
using Health.Direct.Config.Store;

namespace Health.Direct.SmtpAgent
{
    public static class Extensions
    {
        public static CDO.Message LoadCDOMessage(string messageFile)
        {
            return LoadCDOMessageFromText(System.IO.File.ReadAllText(messageFile));
        }

        public static CDO.Message LoadCDOMessageFromText(string text)
        {
            CDO.Message message = new CDO.Message();

            ADODB._Stream stream = message.GetStream();

            stream.Position = 0;
            stream.WriteText(text, StreamWriteEnum.stWriteChar);
            stream.SetEOS();
            stream.Flush();

            return message;
        }
        
        public static void SaveToFile(this CDO.Message message, string filePath)
        {
            ADODB._Stream stream = message.GetStream();
            stream.SaveToFile(filePath, SaveOptionsEnum.adSaveCreateOverWrite);
        }

        /// <summary>
        /// TimelyAndReliable option set and message is not requesting timely and reliable then false.
        /// Always option will return true.
        /// Not other options exist.
        /// </summary>
        /// <param name="message"></param>
        /// <param name="settings"></param>
        /// <returns></returns>
        /// <remarks>
        /// Remember TimelyAndReliable option is the default if not configured.  This means a edge 
        /// client must request timely and reliable delivery.
        /// </remarks>
        public static bool ShouldDeliverFailedStatus(this MessageEnvelope message, NotificationSettings settings)
        {
            if (settings.AutoDsnFailureOption == NotificationSettings.AutoDsnOption.TimelyAndReliable
                   && !message.Message.IsTimelyAndReliable())
            {
                return false;
            }
            return true;
        }


        /// <summary>
        /// Send this outgoing message to a remote smtp server
        /// </summary>
        /// <param name="message">outgoing message</param>
        /// <param name="smtpServer">smtp server to use</param>
        public static void Send(this OutgoingMessage message, string smtpServer)
        {
            if (message == null)
            {
                throw new ArgumentNullException("message");
            }
            
            message.Message.Send(smtpServer);
        }

        public static void Send(this Health.Direct.Common.Mail.Message message, string smtpServer)
        {
            message.Send(smtpServer, -1);
        }
        
        public static void Send(this Health.Direct.Common.Mail.Message message, string smtpServer, int port)
        {
            if (message == null)
            {
                throw new ArgumentNullException("message");
            }
            if (string.IsNullOrEmpty(smtpServer))
            {
                throw new ArgumentException("smtpServer");
            }
            
            CDO.Message cdoMessage = LoadCDOMessageFromText(MimeSerializer.Default.Serialize(message));
            cdoMessage.Send(smtpServer, port);
        }

        /// <summary>
        /// Send this message using the given Smtp Server
        /// </summary>
        /// <param name="message">outgoing message</param>
        /// <param name="smtpServer">smtp server to use</param>
        public static void Send(this CDO.Message message, string smtpServer)
        {
            message.Send(smtpServer, -1);
        }
        
        /// <summary>
        /// Send this message using the given Smtp Server
        /// </summary>
        /// <param name="message">outgoing message</param>
        /// <param name="smtpServer">smtp server to use</param>
        /// <param name="port">smtp port to use</param>
        public static void Send(this CDO.Message message, string smtpServer, int port)
        {
            if (string.IsNullOrEmpty(smtpServer))
            {
                throw new ArgumentException("smtpServer");
            }
            
            Fields configFields = message.Configuration.Fields;
            configFields.SetValue("http://schemas.microsoft.com/cdo/configuration/smtpserver", smtpServer);
            if (port > 0) // If <= 0, use default
            {
                configFields.SetValue("http://schemas.microsoft.com/cdo/configuration/smtpserverport", port);
            }
            configFields.SetValue("http://schemas.microsoft.com/cdo/configuration/sendusing", CDO.CdoSendUsing.cdoSendUsingPort);
            message.Send();
        }
        
        public static void SetMessageStatus(this CDO.Message message, CdoMessageStat status)
        {
            Fields fields = message.GetEnvelopeFields();
            if (fields == null || fields.Count == 0)
            {
                return;
            }
            fields.SetValue(EnvelopeField_Status, status);
        }

        public static void AbortMessage(this CDO.Message message)
        {
            message.SetMessageStatus(CdoMessageStat.cdoStatAbortDelivery);
        }

        public static void BadMessage(this CDO.Message message)
        {
            message.SetMessageStatus(CdoMessageStat.cdoStatBadMail);
        }
        
        public static string GetMessageText(this CDO.Message message)
        {
            return message.GetMessageText(128 * 1024);
        }

        /// <summary>
        /// 
        /// </summary>
        /// <param name="message"></param>
        /// <param name="blockSize">Avoid increasing this too much. Keep below 128K (optimal). See MSDN notes for ReadText</param>
        /// <returns></returns>
        public static string GetMessageText(this CDO.Message message, int blockSize)
        {
            ADODB._Stream stream = message.GetStream();
            
            int size = (int) stream.Size;
            if (size <= blockSize)
            {
                return stream.ReadText(size);
            }
    
            int countRead = 0;
            string block = null;
            StringBuilder builder = new StringBuilder(size);
            while (countRead < size)
            {
                block = stream.ReadText(blockSize);
                if (string.IsNullOrEmpty(block))
                {
                    break;
                }
                builder.Append(block);
                countRead += block.Length;
            }

            return builder.ToString();
        }

        public static void SetMessageText(this CDO.Message message, string messageText, bool save)
        {
            ADODB._Stream stream = message.GetStream();

            stream.Position = 0;
            stream.WriteText(messageText, StreamWriteEnum.stWriteChar);
            stream.SetEOS();
            stream.Flush();
            if (save)
            {
                try
                {
                    message.DataSource.Save();
                }
                catch
                {
                }
            }
        }

        public static void CopyMessage(this CDO.Message message, string folderPath)
        {
            string fileName = CreateUniqueFileName();
            message.SaveToFile(Path.Combine(folderPath, fileName));
        }

        public static string CreateUniqueFileName()
        {
            return Guid.NewGuid().ToString("D") + ".eml";
        }

        //------------------------------------        
        //
        // Envelopes
        //
        //------------------------------------
        const string EnvelopeSender_LocalPostmaster = "<>";
        const string EnvelopeField_Recipients = @"http://schemas.microsoft.com/cdo/smtpenvelope/recipientlist";
        const string EnvelopeField_Sender = @"http://schemas.microsoft.com/cdo/smtpenvelope/senderemailaddress";
        const string EnvelopeField_Status = "http://schemas.microsoft.com/cdo/smtpenvelope/messagestatus";

        //
        // In SMTP Server, the sender address can be empty if the message is from the postmaster
        //
        public static bool IsSenderLocalPostmaster(string sender)
        {
            return (sender == EnvelopeSender_LocalPostmaster);
        }
        
        public static bool HasEnvelopeFields(this CDO.Message message)
        {
            return (message.GetEnvelopeFields() != null);
        }
        
        public static Fields GetEnvelopeFields(this CDO.Message message)
        {
            Fields fields = null;
            try
            {
                fields = message.EnvelopeFields;
            }
            catch
            {
            }
            
            return fields;
        }

        public static string GetEnvelopeRecipients(this CDO.Message message)
        {
            return message.GetEnvelopeField(EnvelopeField_Recipients);
        }
        
        public static void SetEnvelopeRecipients(this CDO.Message message, MailAddressCollection recipients)
        {
            message.SetEnvelopeField(EnvelopeField_Recipients, recipients.ToSmtpServerEnvelopeAddresses());
        }
        
        public static void SetEnvelopeRecipients(this CDO.Message message, string recipients)
        {
            message.SetEnvelopeField(EnvelopeField_Recipients, recipients);
        }
        
        public static string GetEnvelopeSender(this CDO.Message message)
        {
            return message.GetEnvelopeField(EnvelopeField_Sender);
        }

        public static string GetEnvelopeField(this CDO.Message message, string name)
        {
            Fields fields = message.GetEnvelopeFields();
            if (fields == null || fields.Count == 0)
            {
                return null;
            }

            return fields.GetStringValue(name);
        }

        public static void SetEnvelopeField(this CDO.Message message, string name, string value)
        {
            Fields fields = message.GetEnvelopeFields();
            if (fields != null && fields.Count > 0)
            {
                fields.SetValue(name, value);                
            }
        }

        //------------------------------------        
        //
        // Headers
        //
        //------------------------------------
        const string Header_ContentType = "urn:schemas:mailheader:content-type";
        
        public static string GetContentType(this CDO.Message message)
        {
            return message.GetHeader(Header_ContentType);
        }
        
        public static string GetHeader(this CDO.Message message, string name)
        {
            Fields fields = message.Fields;
            if (fields == null || fields.Count == 0)
            {
                return  null;
            }
            
            return fields.GetStringValue(name);
        }
        //------------------------------------        
        //
        // Fields
        //
        //------------------------------------
        public static string GetStringValue(this Fields fields, string name)
        {
            return (string)fields.GetValue(name);
        }

        public static object GetValue(this Fields fields, string name)
        {
            Field field = fields[name];
            if (field == null)
            {
                return null;
            }

            return field.Value;
        }

        public static void SetValue(this Fields fields, string name, object value)
        {
            Field field = fields[name];
            if (field != null)
            {
                field.Value = value;
                fields.Update();
            }
        }

        
        /// <summary>
        /// Find <c>DirectAddress</c>s with Certificates
        /// </summary>
        /// <param name="addresses"></param>
        /// <returns></returns>
        public static DirectAddressCollection ResolvedCertificates(this DirectAddressCollection addresses)
        {
            DirectAddressCollection untrusted = new DirectAddressCollection();
            foreach (DirectAddress addr in addresses)
            {
                if (addr.ResolvedCertificates)
                {
                    untrusted.Add(addr);
                }
            }
            return untrusted;
        }

        /// <summary>
        /// Find <c>DirectAddress</c>s without Certificates
        /// </summary>
        /// <param name="addresses"></param>
        /// <returns></returns>
        public static DirectAddressCollection UnResolvedCertificates(this DirectAddressCollection addresses)
        {
            DirectAddressCollection unsecured = new DirectAddressCollection();
            foreach (DirectAddress addr in addresses)
            {
                if ( ! addr.ResolvedCertificates)
                {
                    unsecured.Add(addr);
                }
            }
            return unsecured;
        }

    }
}