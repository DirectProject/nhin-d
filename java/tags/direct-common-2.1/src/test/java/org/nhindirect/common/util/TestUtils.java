package org.nhindirect.common.util;

import java.io.File;
import java.io.InputStream;
import java.security.Security;
import java.util.HashMap;
import java.util.Map;

import javax.mail.internet.MimeMessage;
import javax.security.auth.callback.Callback;
import javax.security.auth.callback.CallbackHandler;
import javax.security.auth.callback.PasswordCallback;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.junit.Test;
import org.nhindirect.common.tx.model.Tx;
import org.nhindirect.common.tx.model.TxDetail;
import org.nhindirect.common.tx.model.TxDetailType;
import org.nhindirect.common.tx.model.TxMessageType;

public class TestUtils 
{
	@Test
	public void dummy()
	{
		
	}
	
    /**
     * used for testing with a pkcs11 token
     * @return The Security provider name if the token is loaded successfully... an empty string other wise 
     * @throws Exception
     */
	public static String setupSafeNetToken() throws Exception
	{	
		final CallbackHandler handler = new CallbackHandler()
		{
			public void	handle(Callback[] callbacks)
			{
				for (Callback callback : callbacks)
				{
					if (callback instanceof PasswordCallback)
					{		
						
						 ((PasswordCallback)callback).setPassword("1Kingpuff".toCharArray());
					
					}
				}
			}
		};
		
		sun.security.pkcs11.SunPKCS11 p = null;
		try
		{
			final String configName = "./src/test/resources/pkcs11Config/pkcs11.cfg";
			p = new sun.security.pkcs11.SunPKCS11(configName);
			Security.addProvider(p);
			p.login(null, handler);

		}
		catch (Exception e)
		{
			return "";
		}

		return p.getName();
	}
	
	public static String readMessageFromFile(String fileName) throws Exception
	{
		return FileUtils.readFileToString(new File("./src/test/resources/messages/" + fileName));
	
	}
	
	public static MimeMessage readMimeMessageFromFile(String fileName) throws Exception
	{
		InputStream str = IOUtils.toInputStream(readMessageFromFile(fileName));
		
		try
		{
			return new MimeMessage(null, str);
		}
		finally
		{
			IOUtils.closeQuietly(str);
		}
	}

	
	public static Tx makeMessage(TxMessageType type, String msgId, String parentId, String from, String recip,
			String finalRecip, String action, String disposition, String dispostionOption)
	{
		Map<String, TxDetail> details = new HashMap<String, TxDetail>();
		if(msgId != null && !msgId.isEmpty())
			details.put(TxDetailType.MSG_ID.getType(), new TxDetail(TxDetailType.MSG_ID, msgId));
		
		if(parentId != null && !parentId.isEmpty())
			details.put(TxDetailType.PARENT_MSG_ID.getType(), new TxDetail(TxDetailType.PARENT_MSG_ID, parentId));
		
		if(from != null && !from.isEmpty())
			details.put(TxDetailType.FROM.getType(), new TxDetail(TxDetailType.FROM, from));
		
		if(recip != null && !recip.isEmpty())
			details.put(TxDetailType.RECIPIENTS.getType(), new TxDetail(TxDetailType.RECIPIENTS, recip));
		
		if(finalRecip != null && !finalRecip.isEmpty())
			details.put(TxDetailType.FINAL_RECIPIENTS.getType(), new TxDetail(TxDetailType.FINAL_RECIPIENTS, finalRecip));

		if(action != null && !action.isEmpty())
			details.put(TxDetailType.DSN_ACTION.getType(), new TxDetail(TxDetailType.DSN_ACTION, action));

		if(disposition != null && !disposition.isEmpty())
			details.put(TxDetailType.DISPOSITION.getType(), new TxDetail(TxDetailType.DISPOSITION, disposition));
		
		if(dispostionOption != null && !dispostionOption.isEmpty())
			details.put(TxDetailType.DISPOSITION_OPTIONS.getType(), new TxDetail(TxDetailType.DISPOSITION_OPTIONS, disposition));
		
		return new Tx(type, details);
	}
	
	public static Tx makeMessage(TxMessageType type, String msgId, String parentId, String from, String recip,
			String finalRecip, String action, String disposition)
	{
		Map<String, TxDetail> details = new HashMap<String, TxDetail>();
		if(msgId != null && !msgId.isEmpty())
			details.put(TxDetailType.MSG_ID.getType(), new TxDetail(TxDetailType.MSG_ID, msgId));
		
		if(parentId != null && !parentId.isEmpty())
			details.put(TxDetailType.PARENT_MSG_ID.getType(), new TxDetail(TxDetailType.PARENT_MSG_ID, parentId));
		
		if(from != null && !from.isEmpty())
			details.put(TxDetailType.FROM.getType(), new TxDetail(TxDetailType.FROM, from));
		
		if(recip != null && !recip.isEmpty())
			details.put(TxDetailType.RECIPIENTS.getType(), new TxDetail(TxDetailType.RECIPIENTS, recip));
		
		if(finalRecip != null && !finalRecip.isEmpty())
			details.put(TxDetailType.FINAL_RECIPIENTS.getType(), new TxDetail(TxDetailType.FINAL_RECIPIENTS, finalRecip));

		if(action != null && !action.isEmpty())
			details.put(TxDetailType.DSN_ACTION.getType(), new TxDetail(TxDetailType.DSN_ACTION, action));

		if(disposition != null && !disposition.isEmpty())
			details.put(TxDetailType.DISPOSITION.getType(), new TxDetail(TxDetailType.DISPOSITION, disposition));
		
		return new Tx(type, details);
	}
	
	public static Tx makeReliableMessage(TxMessageType type, String msgId, String parentId, String from, String recip,
			String finalRecip, String action, String disposition)
	{
		Map<String, TxDetail> details = new HashMap<String, TxDetail>();
		if(msgId != null && !msgId.isEmpty())
			details.put(TxDetailType.MSG_ID.getType(), new TxDetail(TxDetailType.MSG_ID, msgId));
		
		if(parentId != null && !parentId.isEmpty())
			details.put(TxDetailType.PARENT_MSG_ID.getType(), new TxDetail(TxDetailType.PARENT_MSG_ID, parentId));
		
		if(from != null && !from.isEmpty())
			details.put(TxDetailType.FROM.getType(), new TxDetail(TxDetailType.FROM, from));
		
		if(recip != null && !recip.isEmpty())
			details.put(TxDetailType.RECIPIENTS.getType(), new TxDetail(TxDetailType.RECIPIENTS, recip));
		
		if(finalRecip != null && !finalRecip.isEmpty())
			details.put(TxDetailType.FINAL_RECIPIENTS.getType(), new TxDetail(TxDetailType.FINAL_RECIPIENTS, finalRecip));

		if(action != null && !action.isEmpty())
			details.put(TxDetailType.DSN_ACTION.getType(), new TxDetail(TxDetailType.DSN_ACTION, action));

		if(disposition != null && !disposition.isEmpty())
			details.put(TxDetailType.DISPOSITION.getType(), new TxDetail(TxDetailType.DISPOSITION, disposition));
		
		details.put(TxDetailType.DISPOSITION_OPTIONS.getType(), new TxDetail(TxDetailType.DISPOSITION_OPTIONS, "X-DIRECT-FINAL-DESTINATION-DELIVERY"));
		
		return new Tx(type, details);
	}
	
	public static Tx makeMessage(TxMessageType type, String msgId, String parentId, String from, String recip,
			String finalRecip)
	{
		Map<String, TxDetail> details = new HashMap<String, TxDetail>();
		if(msgId != null && !msgId.isEmpty())
			details.put(TxDetailType.MSG_ID.getType(), new TxDetail(TxDetailType.MSG_ID, msgId));
		
		if(parentId != null && !parentId.isEmpty())
			details.put(TxDetailType.PARENT_MSG_ID.getType(), new TxDetail(TxDetailType.PARENT_MSG_ID, parentId));
		
		if(from != null && !from.isEmpty())
			details.put(TxDetailType.FROM.getType(), new TxDetail(TxDetailType.FROM, from));
		
		if(recip != null && !recip.isEmpty())
			details.put(TxDetailType.RECIPIENTS.getType(), new TxDetail(TxDetailType.RECIPIENTS, recip));
		
		if(finalRecip != null && !finalRecip.isEmpty())
			details.put(TxDetailType.FINAL_RECIPIENTS.getType(), new TxDetail(TxDetailType.FINAL_RECIPIENTS, finalRecip));
		
		return new Tx(type, details);
	}
}
