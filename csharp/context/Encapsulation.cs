namespace Health.Direct.Context
{
    /// <summary>
    /// 3.7 Encapsulation of Other Data Types
    /// This metadata element MUST be included when Direct is used as a transport to encapsulate other message types such as an HTTP request or response, or HL7 v2 message or acknowledgement. 
    /// The encapsulated-message-type "http" MUST be used for encapsulated HTTP RESTful transactions such as with HL7 Fast Healthcare Interoperability Resources(FHIR) or encapsulated HTTP SOAP transactions such as transactions defined by the Integrating the Healthcare Enterprise(IHE) IT Infrastructure Technical Framework.The encapsulated-message-type "hl7v2" MUST be used for encapsulated HL7 V2 transactions.
    /// To encapsulate an HTTP transaction or its response, the HTTP headers and, if applicable, any POST data or other content SHALL be included in one or more separate attachments with the MIME type of application/x-direct-encapsulated+http.To encapsulate an HL7 V2 message or its response, the HL7 V2 message data SHALL be included in one or more separate attachments with MIME type of application/x-direct-encapsulated+hl7v2.Encapsulated HL7 V2 messages SHALL NOT include any control codes required by the HL7 V2 low level protocol (LLP).
    /// When more than one encapsulated message is included by the sender, the recipient MUST process the attachments in the order that the corresponding MIME attachments were included by the sender.A recipient MUST process each encapsulated message even if an earlier message results in an error, and MUST include the encapsulated responses in the same order in the response message.Thus, the sender MUST NOT assemble a sequence of attachments where the message encapsulated in one attachment depends upon or assumes the successful processing of an earlier attachment in the sequence. If the assembled responses would exceed a sender’s outgoing message size limits, the sender MAY send a failure message instead.
    /// This guide does not provide a mechanism to insert the results of one encapsulated transaction into the next encapsulated transaction when multiple transactions are included in a single message.
    /// </summary>
    public class Encapsulation
    {
        /// <summary>
        /// http or hl7v2
        /// </summary>
        public string Type { get; set; }

    }
}
