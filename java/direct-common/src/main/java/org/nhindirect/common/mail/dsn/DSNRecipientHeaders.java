package org.nhindirect.common.mail.dsn;

import javax.mail.Address;

import org.nhindirect.common.mail.dsn.DSNStandard.DSNAction;


/**
 * All of the possible values for the recipient delivery status notification
 * headers
 * 
 * @author Bret Fulcher <BF3174@cerner.com>
 * @author Greg Meyer
 * @since 1.1
 */
public class DSNRecipientHeaders 
{
	  private final DSNAction action;
	  private final String status;
	  private final Address finalRecipient;
	  
	  /**
	   * Constructor
	   * @param action The DSN action
	   * @param status The DSN status
	   * @param finalRecipient The final recipient of this set of DSN headers
	   */
	  public DSNRecipientHeaders(DSNAction action, String status, Address finalRecipient) 
	  {
		  this.action = action;
		  this.status = status;
		  this.finalRecipient = finalRecipient;
	  }	  
	  
	  /**
	   * @return action
	   * 
	   *         The Action field indicates the action performed by the
	   *         Reporting-MTA as a result of its attempt to deliver the message to
	   *         this recipient address. This field MUST be present for each
	   *         recipient named in the DSN.
	   */
	  public DSNAction getAction() 
	  {
	    return action;
	  }


	  /**
	   * @return status
	   * 
	   *         <pre>
	   * 
	   * The per-recipient Status field contains a transport-independent
	   *    status code that indicates the delivery status of the message to that
	   *    recipient.  This field MUST be present for each delivery attempt
	   *    which is described by a DSN.
	   * 
	   *    The syntax of the status field is:
	   * 
	   *    status-field = "Status" ":" status-code
	   * 
	   *    status-code = DIGIT "." 1*3DIGIT "." 1*3DIGIT
	   * 
	   *       ; White-space characters and comments are NOT allowed within
	   *       ; a status-code, though a comment enclosed in parentheses
	   *       ; MAY follow the last numeric sub-field of the status-code.
	   *       ; Each numeric sub-field within the status-code MUST be
	   *       ; expressed without leading zero digits.
	   * 
	   *    Status codes thus consist of three numerical fields separated by ".".
	   *    The first sub-field indicates whether the delivery attempt was
	   *    successful (2= success, 4 = persistent temporary failure, 5 =
	   *    permanent failure).  The second sub-field indicates the probable
	   *    source of any delivery anomalies, and the third sub-field denotes a
	   *    precise error condition, if known.
	   * </pre>
	   */
	  public String getStatus() 
	  {
	    return status;
	  }

	  /**
	   * @return finalrecipient
	   * 
	   *         <pre>
	   * 
	   * RFC 3464             Delivery Status Notifications          January 2003
	   *  The Final-Recipient field indicates the recipient for which this set
	   *    of per-recipient fields applies.  This field MUST be present in each
	   *    set of per-recipient data.
	   * 
	   *    The syntax of the field is as follows:
	   * 
	   *          final-recipient-field =
	   *              "Final-Recipient" ":" address-type ";" generic-address
	   * 
	   *    The generic-address sub-field of the Final-Recipient field MUST
	   *    contain the mailbox address of the recipient (from the transport
	   *    envelope), as it was when the Reporting MTA accepted the message for
	   *    delivery.
	   * 
	   *    The Final-Recipient address may differ from the address originally
	   *    provided by the sender, because it may have been transformed during
	   *    forwarding and gatewaying into a totally unrecognizable mess.
	   *    However, in the absence of the optional Original-Recipient field, the
	   *    Final-Recipient field and any returned content may be the only
	   *    information available with which to correlate the DSN with a
	   *    particular message submission.
	   * 
	   *    The address-type sub-field indicates the type of address expected by
	   *    the reporting MTA in that context.  Recipient addresses obtained via
	   *    SMTP will normally be of address-type "rfc822".
	   * 
	   *    NOTE: The Reporting MTA is not expected to ensure that the address
	   *    actually conforms to the syntax conventions of the address-type.
	   *    Instead, it MUST report exactly the address received in the envelope,
	   *    unless that address contains characters such as CR or LF which are
	   *    not allowed in a DSN field.
	   * 
	   *    Since mailbox addresses (including those used in the Internet) may be
	   *    case sensitive, the case of alphabetic characters in the address MUST
	   *    be preserved.
	   * 
	   * </pre>
	   */
	  public Address getFinalRecipient() 
	  {
	    return finalRecipient;
	  }

}
