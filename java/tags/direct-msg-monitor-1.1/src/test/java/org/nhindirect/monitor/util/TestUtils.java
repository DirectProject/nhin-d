package org.nhindirect.monitor.util;

import java.io.File;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import javax.mail.internet.MimeMessage;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.junit.Test;
import org.nhindirect.common.tx.model.Tx;
import org.nhindirect.common.tx.model.TxDetail;
import org.nhindirect.common.tx.model.TxDetailType;
import org.nhindirect.common.tx.model.TxMessageType;
import org.nhindirect.monitor.providers.TxJSONProvider;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.api.client.config.ClientConfig;
import com.sun.jersey.api.client.config.DefaultClientConfig;

///CLOVER:OFF
public class TestUtils 
{
	private static final Integer CONNECTION_TIMEOUT = 10000; // 10 seconds
	private static final Integer READ_TIMEOUT = 10000000; // 10 seconds
	
	@Test
	public void testDummy()
	{
		
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
	
	public static WebResource getResource(String serviceURL)
	{
		final ClientConfig config = new DefaultClientConfig();

		config.getSingletons().add(new TxJSONProvider());

		// need to set timeouts so we don't block forever in the event of a bad URL or hung web server
		config.getProperties().put(ClientConfig.PROPERTY_CONNECT_TIMEOUT, CONNECTION_TIMEOUT);
		config.getProperties().put(ClientConfig.PROPERTY_READ_TIMEOUT, READ_TIMEOUT);
		
		
		final Client client = Client.create(config);
		WebResource resource = client.resource(serviceURL);
		
		return resource;
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
///CLOVER:ON
