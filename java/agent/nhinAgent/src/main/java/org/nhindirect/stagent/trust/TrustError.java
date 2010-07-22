package org.nhindirect.stagent.trust;

/**
 * Enumeration of errors that can occur during a trust enforcement operation.
 * @author Greg Meyer
 * @author Umesh Madan
 *
 */
public enum TrustError 
{
    Unexpected,   
    UntrustedMessage,
    UntrustedSender,
    UnknownRecipient,
    MissingSenderSignature,
    MissingSenderCertificate,
    MissingRecipientCertificate,
    NoTrustedRecipients,
    SignatureValidation
}
