using System;
using System.Collections.Generic;
using System.IO;
using System.Linq;
using System.Net.Mail;
using Health.Direct.Common.Container;
using Health.Direct.Common.Diagnostics;
using Health.Direct.Common.Mail.DSN;
using Health.Direct.Common.Mail.Notifications;
using Health.Direct.Common.Routing;
using Health.Direct.SmtpAgent;
using MimeKit;

namespace Health.Direct.Context.Loopback.Receiver
{
    /// <summary>
    /// Test only receiver.
    /// The LoopBack receiver receives messages to a specific email address and parses context if it exists.  Then is rebuilds the context and sends a reply to the sender with the same context.
    /// The value of such a receiver is to allow testing partners to test a context implementation with with the .net RI context implemenation.
    /// </summary>
    /*
       <IncomingRoutes>
           <PluginRoute>
             <AddressType>LoopBackContext</AddressType>
             <Receiver>
                <TypeName>Health.Direct.Context.Loopback.Receiver.LoopBackContext, Health.Direct.Context.Loopback.Receiver</TypeName>
                <Settings>                
                    <PickupFolder>c:\inetpub\mailroot\pickup</PickupFolder>
                </Settings>
              </Receiver>
            </PluginRoute>

            ...

        </IncomingRoutes>
     */
    public class LoopBackContext : IReceiver<ISmtpMessage>, IPlugin
    {
        PongContextSettings m_settings;

        public PongContextSettings Settings
        {
            get
            {
                return m_settings;
            }
            set
            {
                if (value == null)
                {
                    throw new SmtpAgentException(SmtpAgentError.InvalidPluginRoute);
                }

                value.Validate();
                m_settings = value;
            }
        }

        public void Init(PluginDefinition pluginDef)
        {
            Settings = pluginDef.DeserializeSettings<PongContextSettings>();
        }

        public bool Receive(ISmtpMessage data)
        {
            var message = data.GetEnvelope().Message;

            if (message.IsDSN())
            {
                Log.For<LoopBackContext>().Debug("Ignore DSN");

                return true;
            }

            if (message.IsMDN())
            {
                Log.For<LoopBackContext>().Debug("Ignore MDN");

                return true;
            }

            var directMessage = MimeMessage.Load(message.ToString().ToStream());
            
            try
            {
                if (! directMessage.ContainsDirectContext())
                {
                    Log.For<LoopBackContext>()
                        .Warn($"Message does not contain context.  MessageId={directMessage.MessageId}");

                    var dsnMessage = ReturnNoContextMessage(directMessage, @"No Context found");
                    DropMessage(dsnMessage);

                    return true;
                }

                var pongMessage = EchoContext.Process(directMessage);
                DropMessage(pongMessage);

                Log.For<LoopBackContext>()
                    .Info($"Message context pong response.  MessageId={pongMessage.MessageId} RelatesToMessageId={message.ID}");
            }
            catch (Exception ex)
            {
                Log.For<LoopBackContext>()
                    .Error($"Message exception.  MessageId={message.ID} :: Exception: {ex}");

                var dsnMessage = ReturnNoContextMessage(directMessage, ex.Message);
                DropMessage(dsnMessage);
                
            }
            
            return true;
        }

        private void DropMessage(MimeMessage pongMessage)
        {
            using (Stream stream = File.OpenWrite(
                Path.Combine(
                    Settings.PickupFolder,
                    TestFilename)))
            {
                pongMessage.WriteTo(stream);
            }
        }

        private void DropMessage(DSNMessage dsnMessage)
        {
            using (Stream stream = File.OpenWrite(
                Path.Combine(
                    Settings.PickupFolder,
                    TestFilename)))
            {
                dsnMessage.Save(stream);
            }
        }

        private DSNMessage ReturnNoContextMessage(MimeMessage directMessage, string bodyMessage)
        {
            var to = new MailAddress(directMessage.From.Mailboxes.Single().ToString());
           
            var perMessage = new DSNPerMessage(
                to.Host, 
                directMessage.MessageId);

            var dsnPerRecipients = new List<DSNPerRecipient>();

            foreach (var mailboxAddress in directMessage.To.Mailboxes)
            {
                var dsnPerRecipient = new DSNPerRecipient(
                    DSNStandard.DSNAction.Failed,
                    DSNStandard.DSNStatus.Permanent,
                    "3.3",
                    new MailAddress(mailboxAddress.ToString())
                );

                dsnPerRecipients.Add(dsnPerRecipient);
            }

            var dsn = new DSN(perMessage, dsnPerRecipients);
            dsn.Explanation = bodyMessage;
            var postMaster = new MailAddress("Postmaster@" + to.Host);

            var statusMessage = new DSNMessage(to.Address, postMaster.Address, dsn );

            return statusMessage;
        } 
        
        private string m_fileName;

        /// <summary>
        /// Set a known filename per test
        /// </summary>
        public string TestFilename
        {
            get
            {
                var fileName = m_fileName ?? SmtpAgent.Extensions.CreateUniqueFileName();
                m_fileName = null;

                return fileName;
            }
            set { m_fileName = value; }
        }
    }
}
