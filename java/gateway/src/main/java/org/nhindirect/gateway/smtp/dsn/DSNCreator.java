package org.nhindirect.gateway.smtp.dsn;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

import org.nhindirect.common.tx.model.Tx;
import org.nhindirect.stagent.NHINDAddressCollection;

public interface DSNCreator 
{
	public MimeMessage createDSNFailure(Tx tx, NHINDAddressCollection failedRecipeints) throws MessagingException;
}
