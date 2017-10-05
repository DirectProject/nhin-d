using System;
using System.Linq;
using MimeKit;

namespace Health.Direct.Context
{
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
                .WithContentType(context.ContentType.MediaType, context.ContentType.MediaSubtype)
                .WithContentId($"<{Guid.NewGuid():N}@{Environment.MachineName}>")
                .WithDisposition(context.ContentDisposition.FileName)
                .WithTransferEncoding(context.ContentTransferEncoding)
                .WithVersion(context.Metadata.Version)
                //
                // Section 3.2 Implementation Guide for Expressing Context in Direct Messageing.
                // retain the unique and persisten identifier assigned by the originating party
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
