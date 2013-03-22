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
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Net.Mail;
using System.IO;
using Health.Direct.Common.Mail;
using Health.Direct.Common.Mime;
using Health.Direct.Config.Tools;
using Health.Direct.Config.Tools.Command;

namespace Health.Direct.Tools.Agent
{   
    /// <summary>
    /// Command line Mail builder
    /// </summary>
    public class MailCommands
    {
        MailMessage m_message;
        
        public MailCommands()
        {
        }
        
        [Command(Name = "Mail_New", Usage = StartMailUsage)]
        public void StartMail(string[] args)
        {
            m_message = new MailMessage();
        }
        const string StartMailUsage = "Start building a mail message.";
        
        [Command(Name = "Mail_Save", Usage=SaveMailUsage)]
        public void SaveMail(string[] args)
        {
            this.VerifyStarted();            
            this.SaveMessageToFile(m_message, args.GetRequiredValue(0));                        
        }
        const string SaveMailUsage = "Save the mail you constructed to the given filePath.";

        [Command(Name = "Mail_SaveWrapped", Usage = SaveWrappedUsage)]
        public void SaveWrappedMail(string[] args)
        {
            this.VerifyStarted();
            string path = args.GetRequiredValue(0);
            
            MailMessage wrapped = this.WrapMessage();
            this.SaveMessageToFile(wrapped, args.GetRequiredValue(0));
        }
        const string SaveWrappedUsage = "Saves the mail you constructed, but embedded as the body of a wrapper email.";
        
        [Command(Name = "Mail_SaveWrapped64", Usage = SaveWrappedUsage)]
        public void SaveWrappedMail64(string[] args)
        {
            this.VerifyStarted();
            string path = args.GetRequiredValue(0);

            MailMessage wrapped = this.WrapMessage();
            wrapped.BodyEncoding = Encoding.UTF8; // This will force Transfer Encoding to be base64 instead of QuotedPrintable
            
            this.SaveMessageToFile(wrapped, args.GetRequiredValue(0));
        }

        [Command(Name = "Mail_Send", Usage=SendMailUsage)]
        public void SendMail(string[] args)
        {
            this.VerifyStarted();
            
            string server = args.GetRequiredValue(0);
            this.SendMessage(server, m_message);
        }
        const string SendMailUsage = "Send mail using the given Smtp Server";

        [Command(Name = "Mail_SendWrapped", Usage = SendMailUsage)]
        public void SendWrappedMail(string[] args)
        {
            this.VerifyStarted();

            string server = args.GetRequiredValue(0);

            MailMessage wrapped = this.WrapMessage();
            this.SendMessage(server, wrapped);
        }

        [Command(Name = "Mail_From")]
        public void SetFrom(string[] args)
        {
            this.VerifyStarted();
            m_message.From = new MailAddress(args.GetRequiredValue(0));
        }
        
        [Command(Name = "Mail_To")]
        public void SetTo(string[] args)
        {
            this.VerifyStarted();
            
            string to = string.Join(",", args);
            m_message.To.Add(to);
        }


        [Command(Name = "Mail_CC")]
        public void SetCc(string[] args)
        {
            this.VerifyStarted();
            string to = string.Join(",", args);
            m_message.CC.Add(to);
        }

        [Command(Name = "Mail_Subject")]
        public void SetSubject(string[] args)
        {
            this.VerifyStarted();
            m_message.Subject = string.Join(" ", args);
        }

        [Command(Name = "Mail_Body", Usage = "Set Text Body")]
        public void SetBody(string[] args)
        {
            this.VerifyStarted();
            m_message.Body = string.Join(" ", args);
        }

        [Command(Name = "Mail_Body_HtmlFile", Usage = "File to make Html Body")]
        public void SetHtmlBody(string[] args)
        {
            this.VerifyStarted();

            m_message.Body = File.ReadAllText(args.GetRequiredValue(0));
            m_message.IsBodyHtml = true;
        }

        [Command(Name = "Mail_Body_File", Usage = "File to make Text Body")]
        public void SetFileBody(string[] args)
        {
            this.VerifyStarted();
            m_message.Body = File.ReadAllText(args.GetRequiredValue(0));
        }

        [Command(Name = "Mail_AltView", Usage="File representing an alternate view for the body")]
        public void AddAltView(string[] args)
        {
            this.VerifyStarted();
            m_message.AlternateViews.Add(new AlternateView(args.GetRequiredValue(0)));
        }

        [Command(Name = "Mail_Attach")]
        public void AddAttachment(string[] args)
        {
            this.VerifyStarted();
            
            Attachment attachment = new Attachment(args.GetRequiredValue(0));
            m_message.Attachments.Add(attachment);
        }

        [Command(Name = "Mail_Stop", Usage = StopMailUsage)]
        public void StopMail(string[] args)
        {
            m_message = null;
        }
        const string StopMailUsage = "Stop building current message.";
        
        void VerifyStarted()
        {
            if (m_message == null)
            {
                m_message = new MailMessage();
            }
        }
        
        void SaveMessageToFile(MailMessage message, string filePath)
        {
            string messageText = message.Serialize();
            File.WriteAllText(filePath, messageText);
        }
        
        void SendMessage(string server, MailMessage message)
        {
            SmtpClient smtp = new SmtpClient(server);
            smtp.Send(message);
        }
                
        MailMessage WrapMessage()
        {
            MailMessage wrapped = new MailMessage();
            wrapped.From = m_message.From;
            if (m_message.To.Count > 0)
            {
                wrapped.To.Add(m_message.To.ToString());
            }
            if (m_message.CC.Count > 0)
            {
                wrapped.CC.Add(m_message.CC.ToString());
            }
            wrapped.Headers.Add(MailStandard.ContentTypeHeader, MailStandard.MediaType.WrappedMessage);

            string innerText = m_message.Serialize();
            wrapped.Body = innerText;
            
            return wrapped;
        }
    }
}
