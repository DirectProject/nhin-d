using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Net.Mail;
using NHINDirect.Mail;

namespace NHINDirect.Agent
{
    public class OutgoingMessage2
    {
        //
        // The processed message
        //   - Recipient list was rewritten
        //   - Envelope.Message is now signed+encrypted
        //
        public MessageEnvelope MessageEnvelope {get;set;}
        //
        // List of rejected/untrusted recipients
        //
        public MailAddressCollection UntrustedRecipients {get; set;}
    }
    
    public class IncomingMessage2
    {
        //
        // The processed message
        //   - Recipient list was rewritten
        //   - Envelope.Message is decrypted, signatures verified
        //
        public MessageEnvelope MessageEnvelope { get; set; }
        //
        // Recipients in the local domain - since some of the recipients may have
        // the message relayed to them
        //
        public MailAddressCollection DomainRecipients { get; set; }
        //
        // Non-domain recipients
        //
        public MailAddressCollection OtherRecipients { get; set; }
        //
        // Recipients who were removed as Untrusted
        //
        public MailAddressCollection UntrustedRecipients { get; set; }
    }
}
