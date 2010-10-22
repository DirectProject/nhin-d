using Health.Direct.Common;

namespace Health.Direct.SmtpAgent
{
    public enum SmtpAgentError
    {
        Unknown = 0,
        NotInitialized,
        MissingPostmaster,
        MissingLogSettings,
        InvalidEnvelopeFromAgent,
        EmptyResultFromAgent,
        MissingMailPickupFolder,
        MailPickupFolderDoesNotExist,
        MissingBounceTemplateOutgoing,
        MissingBounceTemplateIncoming,
        InvalidBounceMessageTemplate,
        MissingCertResolverClientSettings,
        MissingAnchorResolverClientSettings,
        NoAddressManager,
        ConfiguredDomainsMismatch,   // Domains in Xml file not found in config
        NoSenderInEnvelope,
        NoRecipientsInEnvelope
    }

    public class SmtpAgentException : DirectException<SmtpAgentError>
    {
        public SmtpAgentException(SmtpAgentError error)
            : base(error)
        {
        }
    }
}