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
using System.Net.Mime;
using System.Text;
using System.Net.Mail;
using Health.Direct.Common.Mail;
using Health.Direct.Common.Mime;
using Xunit;
using Xunit.Extensions;

namespace Health.Direct.Common.Tests.Mail
{
    /// <summary>
    /// Wrappers for generating test .NET MailMessages
    /// </summary>
    public class MailMessageGenerator
    {
        public static MailMessage GenerateRandomMail(int toCount, int ccCount, int bodyLength)
        {
            MailMessage mail = new MailMessage();
            AddRandomAddresses(mail, toCount, ccCount);
            mail.Subject = string.Format("Random long subject text with a timestamp in it  {0} and some ====", DateTime.Now);
            mail.Body = GenerateRandomBody(bodyLength);
            return mail;
        }

        public static void AddRandomAddresses(MailMessage mail, int toCount, int ccCount)
        {
            mail.From = new MailAddress(GenerateEmailAddress(50, 35, 40));
            for (int i = 0; i < toCount; ++i)
            {
                string addr = GenerateEmailAddress(50, 35, 30);
                mail.To.Add(new MailAddress(addr));
            }

            for (int i = 0; i < ccCount; ++i)
            {
                string addr = GenerateEmailAddress(40, 40, 30);
                mail.CC.Add(new MailAddress(addr));
            }
        }

        public static string GenerateEmailAddress(int nameLength, int userNameLength, int hostLength)
        {
            StringBuilder builder = new StringBuilder(nameLength + userNameLength + hostLength + 10);
            builder.Append('"');
            builder.Append('N', nameLength);
            builder.Append('"');
            builder.Append(" <");
            builder.Append('u', userNameLength);
            builder.Append('@');
            builder.Append('H', hostLength);
            builder.Append(".foo");
            builder.Append('>');

            return builder.ToString();
        }

        public static string GenerateRandomBody(int length)
        {
            Random rand = new Random();
            StringBuilder builder = new StringBuilder(length);

            while (builder.Length < length)
            {
                char ch = (char)rand.Next(1, 127);

                if (ch == '.')
                {
                    // Prevent "Dot stuffing". 
                    continue;
                }

                if (ch == MimeStandard.CR || ch == MimeStandard.LF)
                {
                    builder.Append(MimeStandard.CRLF);
                }
                else
                {
                    builder.Append(ch);
                }
            }

            string sz = builder.ToString();
            return sz.TrimEnd();
        }

        public static MailMessage WrappedMailMessage(MailMessage inner)
        {
            MailMessage wrapped = new MailMessage();
            wrapped.From = inner.From;
            if (inner.To.Count > 0)
            {
                wrapped.To.Add(inner.To.ToString());
            }
            if (inner.CC.Count > 0)
            {
                wrapped.CC.Add(inner.CC.ToString());
            }

            string innerText = inner.Serialize();
            ContentType mimeType = new ContentType(MailStandard.MediaType.WrappedMessage);
            AlternateView alternate = AlternateView.CreateAlternateViewFromString(innerText, mimeType);
            wrapped.AlternateViews.Add(alternate);

            return wrapped;
        }


    }
}
