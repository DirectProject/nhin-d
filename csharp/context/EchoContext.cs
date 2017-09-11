using System;
using MimeKit;

namespace Health.Direct.Context
{
    public static class EchoContext
    {
        public static MimeMessage Process(MimeMessage message)
        {
            var context = message.DirectContext();

            var contextBuilder = new ContextBuilder();

            contextBuilder
                .WithContentType(context.ContentType.MediaType, context.ContentType.MediaSubtype)
                .WithContentId(context.ContentId)
                .WithDisposition(context.ContentDisposition.FileName)
                .WithTransferEncoding(context.ContentTransferEncoding)
                .WithVersion(context.Metadata.Version)
                .WithId(context.Metadata.Id)
                .WithEncapsulation(context.Metadata.Encapsulation?.Type);

            var messageBuilt = contextBuilder.Build();

            var echo = new MimeMessage();
            echo.From.Add(new MailboxAddress("Jean", "Jean@opsstation.lab"));
            echo.To.Add(new MailboxAddress("Joe", "Joe@hobo.lab"));
            echo.Subject = "Need more memory";
            echo.MessageId = $"<{Guid.NewGuid():N}@{Environment.MachineName}>";
            echo.Headers.Add("X-Direct-Context", messageBuilt.ContentId);
            
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

            message.Body = multipart;

            return message;
        } 
    }
}
