/* 
 Copyright (c) 2010-2017, Direct Project
 All rights reserved.

 Authors:
    Joseph Shook    Joseph.Shook@Surescripts.com
  
Redistribution and use in source and binary forms, with or without modification, are permitted provided that the following conditions are met:

Redistributions of source code must retain the above copyright notice, this list of conditions and the following disclaimer.
Redistributions in binary form must reproduce the above copyright notice, this list of conditions and the following disclaimer in the documentation and/or other materials provided with the distribution.
Neither the name of The Direct Project (directproject.org) nor the names of its contributors may be used to endorse or promote products derived from this software without specific prior written permission.
THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 
*/

using System;
using System.Linq;
using MimeKit;

namespace Health.Direct.Context
{
    /// <summary>
    /// Used for testing only.
    /// 
    /// Transform incoming context to a response context.
    /// </summary>
    public static class EchoContext
    {
        /// <summary>
        /// Used for testing only.
        /// </summary>
        /// <param name="message"></param>
        /// <returns></returns>
        public static MimeMessage Process(MimeMessage message)
        {
            var context = message.DirectContext();
            var contextBuilder = new ContextBuilder();

            contextBuilder
                .WithContentId($"<{Guid.NewGuid():N}@{Environment.MachineName}>")
                .WithDisposition(context.ContentDisposition.FileName)
                .WithTransferEncoding(context.ContentTransferEncoding)
                .WithVersion(context.Metadata.Version)
                //
                // Section 3.2 Implementation Guide for Expressing Context in Direct Messageing.
                // retain the unique and persistent identifier assigned by the originating party
                // to uniquely identify a sequence of related transactions.
                //
                .WithId(context.Metadata.Id)
                .WithPatientId(context.Metadata.PatientId)
                .WithType(context.Metadata.Type?.Category, context.Metadata.Type?.Action)
                .WithPurpose(context.Metadata.Purpose)
                .WithPatient(context.Metadata.Patient)
                .WithEncapsulation(context.Metadata.Encapsulation?.Type);

            var messageBuilt = contextBuilder.Build();

            var echoMessage = new MimeMessage();
            echoMessage.From.Add(message.To.Mailboxes.First());
            echoMessage.To.Add(message.From.Mailboxes.First());
            echoMessage.Subject = message.Subject;
            echoMessage.MessageId = $"<{Guid.NewGuid():N}@{Environment.MachineName}>";
            echoMessage.Headers.Add(MailStandard.Headers.DirectContext, messageBuilt.ContentId);
            
            var body = new TextPart("plain")
            {
                Text = @"Echo context"
            };

            var multipart = new Multipart("mixed");
            multipart.Add(body);
            multipart.Add(contextBuilder.BuildMimePart());

            foreach (var selectEncapulation in message.SelectEncapulations())
            {
                multipart.Add(selectEncapulation);
            }

            echoMessage.Body = multipart;

            return echoMessage;
        } 
    }
}
