package org.nhindirect.common.mail.dsn;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import javax.mail.Address;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;

import org.junit.Test;
import org.nhindirect.common.mail.MailStandard;
import org.nhindirect.common.mail.dsn.DSNStandard.DSNAction;
import org.nhindirect.common.mail.dsn.DSNStandard.DSNStatus;
import org.nhindirect.common.mail.dsn.DSNStandard.MtaNameType;
import org.nhindirect.common.mail.dsn.impl.DefaultDSNFailureTextBodyPartGenerator;
import org.nhindirect.common.mail.dsn.impl.HumanReadableTextAssemblerFactory;



public class DNSGenerator_CreateDSNMessageTest 
{

	
	@Test
	public void testCreateDSNMessage_createGeneralDSNMessage() throws Exception
	{
		final DSNGenerator dsnGenerator = new DSNGenerator("Not Delivered:");
		
    	final DSNRecipientHeaders dsnRecipHeaders = 
    			new DSNRecipientHeaders(DSNAction.FAILED, 
    			DSNStatus.getStatus(DSNStatus.PERMANENT, DSNStatus.UNDEFINED_STATUS), new InternetAddress("ah4626@test.com"));
		
    	final List<DSNRecipientHeaders> dsnHeaders = new ArrayList<DSNRecipientHeaders>();
    	dsnHeaders.add(dsnRecipHeaders);
    	
    	final String originalMessageId = UUID.randomUUID().toString();
    	final DSNMessageHeaders messageDSNHeaders = new DSNMessageHeaders("DirectJUNIT", originalMessageId, MtaNameType.DNS);
    	
    	List<Address> faileRecips = new ArrayList<Address>();
    	faileRecips.add(new InternetAddress("ah4626@test.com"));
    	
    	final DefaultDSNFailureTextBodyPartGenerator textGenerator = new DefaultDSNFailureTextBodyPartGenerator("", "", "",
    		    "", "", HumanReadableTextAssemblerFactory.getInstance());
    	
    	
    	
    	final MimeBodyPart textBodyPart = textGenerator.generate(new InternetAddress("gm2552@test.com"), faileRecips, null);
    	
		MimeMessage dsnMessage = dsnGenerator.createDSNMessage(new InternetAddress("gm2552@test.com"), "test", new InternetAddress("postmaster@test.com"), 
				dsnHeaders, messageDSNHeaders, textBodyPart);
		
		assertNotNull(dsnMessage);
		assertEquals("postmaster@test.com", MailStandard.getHeader(dsnMessage, MailStandard.Headers.From));
		assertEquals("gm2552@test.com", MailStandard.getHeader(dsnMessage, MailStandard.Headers.To));
		assertTrue( MailStandard.getHeader(dsnMessage, MailStandard.Headers.Subject).startsWith("Not Delivered:"));
	}
}
