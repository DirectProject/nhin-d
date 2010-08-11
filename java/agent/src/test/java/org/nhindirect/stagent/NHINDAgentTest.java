package org.nhindirect.stagent;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Map.Entry;

import javax.mail.internet.MimeMessage;

import org.nhindirect.stagent.NHINDAgent;
import org.nhindirect.stagent.cert.impl.KeyStoreCertificateStore;
import org.nhindirect.stagent.cert.impl.UniformCertificateStore;
import org.nhindirect.stagent.trust.TrustAnchorResolver;
import org.nhindirect.stagent.utils.TestUtils;

import junit.framework.TestCase;

public class NHINDAgentTest extends TestCase 
{
	private static final String internalStorePassword = "h3||0 wor|d";
	private static final String pkPassword = "pKpa$$wd";	
	
	static
	{
		// Override the logging system to turn on trace level logging
		// Need to make sure debug logging is tested to check for possible null reference errors
		System.setProperty("org.apache.commons.logging.Log", "org.apache.commons.logging.impl.SimpleLog");
		System.setProperty("org.apache.commons.logging.simplelog.defaultlog", "trace");
		
		for (Entry<Object, Object> entry : System.getProperties().entrySet())
		{
			System.out.println("Name: " + entry.getKey() + " Value: " + entry.getValue());
		}
	}
	
	private static String readResource(String _rec) throws Exception
	{
		
		int BUF_SIZE = 2048;		
		int count = 0;
	
		BufferedInputStream imgStream = new BufferedInputStream(NHINDAgentTest.class.getResourceAsStream(_rec));
				
		ByteArrayOutputStream ouStream = new ByteArrayOutputStream();
		if (imgStream != null) 
		{
			byte buf[] = new byte[BUF_SIZE];
			
			while ((count = imgStream.read(buf)) > -1)
			{
				ouStream.write(buf, 0, count);
			}
			
			try 
			{
				imgStream.close();
			} 
			catch (IOException ieo) 
			{
				throw ieo;
			}
			catch (Exception e)
			{
				throw e;
			}					
		} 
		else
			throw new IOException("Failed to open resource " + _rec);

		return new String(ouStream.toByteArray());		
	}
	
	
	public void testEndToEndMessageWithCertKeyStore() throws Exception
	{
		// get the keystore file
		File fl = new File("testfile");
		int idx = fl.getAbsolutePath().lastIndexOf("testfile");
		
		String path = fl.getAbsolutePath().substring(0, idx);
		
		File internalKeystoreFile = new File(path + "src/test/resources/keystores/internalKeystore");		
		
		KeyStoreCertificateStore service = new KeyStoreCertificateStore(internalKeystoreFile, 
				internalStorePassword, pkPassword);
		
		X509Certificate caCert = TestUtils.getExternalCert("cacert");
		X509Certificate externCaCert = TestUtils.getExternalCert("externCaCert");
		X509Certificate secureHealthEmailCACert = TestUtils.getExternalCert("secureHealthEmailCACert");
		
		// anchors cert validation
		Collection<X509Certificate> anchors = new ArrayList<X509Certificate>();
		anchors.add(caCert);
		anchors.add(externCaCert);
		anchors.add(secureHealthEmailCACert);
		
		NHINDAgent agent = new NHINDAgent("cerner.com", service, 
				service, new TrustAnchorResolver(anchors));
		
		String testMessage = readResource("MultipartMimeMessage.txt");
		MimeMessage originalMsg = new MimeMessage(null, new ByteArrayInputStream(testMessage.getBytes("ASCII")));

		
		OutgoingMessage SMIMEenvMessage = agent.processOutgoing(testMessage);
		
		assertNotNull(SMIMEenvMessage);
		assertTrue(SMIMEenvMessage.getMessage().toString().length() > 0);

		
		// verify the message
		// need a new agent because this is a different domain
		agent = new NHINDAgent("starugh-stateline.com", service, 
				service, new TrustAnchorResolver(anchors));
		IncomingMessage strippedAndVerifiesMessage = agent.processIncoming(SMIMEenvMessage.getMessage().toString());
		
		
		assertNotNull(strippedAndVerifiesMessage);
		assertTrue(strippedAndVerifiesMessage.getMessage().toString().length() > 0);
		
		MimeMessage processedMsg = new MimeMessage(null, 
				new ByteArrayInputStream(strippedAndVerifiesMessage.getMessage().toString().getBytes("ASCII")));
		
		assertNotNull(processedMsg);
		
		// can't do a direct compare on headers because the processing may strip some of the recipients
		assertTrue(processedMsg.getContentType().compareTo(originalMsg.getContentType()) == 0);
		assertTrue(originalMsg.getSubject().compareTo(processedMsg.getSubject()) == 0);
				
		// get the message data and compare
		ByteArrayOutputStream oStream = new ByteArrayOutputStream();
		byte[] buffer = new byte[1024];
		int count = 0;
		InputStream inStream = originalMsg.getInputStream();
		while ((count = inStream.read(buffer)) > -1)
			oStream.write(buffer, 0, count);
		
		String originalPart = new String(oStream.toByteArray(), "ASCII");
		
		oStream = new ByteArrayOutputStream();
		count = 0;
		inStream = processedMsg.getInputStream();
		while ((count = inStream.read(buffer)) > -1)
			oStream.write(buffer, 0, count);
		
		String processedPart = new String(oStream.toByteArray(), "ASCII");
		
		
		//assertTrue(processedPart.compareTo(originalPart) == 0);
		
		
		// now do a large message with some attachments
		agent = new NHINDAgent("securehealthemail.com", service, 
				service, new TrustAnchorResolver(anchors));
		
		testMessage = readResource("LargeMsgWithAttachments.txt");
		originalMsg = new MimeMessage(null, new ByteArrayInputStream(testMessage.getBytes("ASCII")));

		
		SMIMEenvMessage = agent.processOutgoing(testMessage);
		
		//FileOutputStream ouStream = new FileOutputStream(new File("large.eml"));
		//ouStream.write(SMIMEenvMessage.getBytes());
		
		assertNotNull(SMIMEenvMessage);
		assertTrue(SMIMEenvMessage.getMessage().toString().length() > 0);

		
		// verify the message
		agent = new NHINDAgent("securehealthemail.com", service, 
				service, new TrustAnchorResolver(anchors));
		strippedAndVerifiesMessage = agent.processIncoming(SMIMEenvMessage);
		
		
		assertNotNull(strippedAndVerifiesMessage);
		assertTrue(strippedAndVerifiesMessage.getMessage().toString().length() > 0);
		
		processedMsg = new MimeMessage(null, new ByteArrayInputStream(strippedAndVerifiesMessage.
				getMessage().toString().getBytes("ASCII")));
		
		assertNotNull(processedMsg);
		
		// can't do a direct compare on headers because the processing may strip some of the recipients
		assertTrue(processedMsg.getContentType().compareTo(originalMsg.getContentType()) == 0);
		assertTrue(originalMsg.getSubject().compareTo(processedMsg.getSubject()) == 0);
				
		// get the message data and compare
		oStream = new ByteArrayOutputStream();
		buffer = new byte[1024];
		 count = 0;
		inStream = originalMsg.getInputStream();
		while ((count = inStream.read(buffer)) > -1)
			oStream.write(buffer, 0, count);
		
		originalPart = new String(oStream.toByteArray(), "ASCII");
		
		oStream = new ByteArrayOutputStream();
		count = 0;
		inStream = processedMsg.getInputStream();
		while ((count = inStream.read(buffer)) > -1)
			oStream.write(buffer, 0, count);
		
		processedPart = new String(oStream.toByteArray(), "ASCII");
		
		
		//assertTrue(processedPart.compareTo(originalPart) == 0);		
	}
	
	
	public void testDecryptProvidedMessage() throws Exception
	{
		/*
		 * EncryptedMessage2
		 */
		
		
		// get the keystore file
		File fl = new File("testfile");
		int idx = fl.getAbsolutePath().lastIndexOf("testfile");
		
		String path = fl.getAbsolutePath().substring(0, idx);
		
		File internalKeystoreFile = new File(path + "src/test/resources/keystores/internalKeystore");		
		
		KeyStoreCertificateStore service = new KeyStoreCertificateStore(internalKeystoreFile, 
				internalStorePassword, pkPassword);
		
		X509Certificate caCert = TestUtils.getExternalCert("cacert");
		X509Certificate externCaCert = TestUtils.getExternalCert("externCaCert");
		X509Certificate secureHealthEmailCACert = TestUtils.getExternalCert("secureHealthEmailCACert");
		X509Certificate msCACert = TestUtils.getExternalCert("msanchor");
		
		// anchors cert validation
		Collection<X509Certificate> anchors = new ArrayList<X509Certificate>();
		anchors.add(caCert);
		anchors.add(externCaCert);
		anchors.add(secureHealthEmailCACert);
		anchors.add(msCACert);
		
		NHINDAgent agent = new NHINDAgent("securehealthemail.com", service, 
				service, new TrustAnchorResolver(anchors));
		
		String testMessage = readResource("EncryptedMessage2.txt");
		MimeMessage originalMsg = new MimeMessage(null, new ByteArrayInputStream(testMessage.getBytes("ASCII")));
		
		
		// verify the message
		// need a new agent because this is a different domain);
		IncomingMessage strippedAndVerifiesMessage = agent.processIncoming(originalMsg);
		
		
		assertNotNull(strippedAndVerifiesMessage);
		assertTrue(strippedAndVerifiesMessage.getMessage().toString().length() > 0);
		
		/*
		 * EncryptedMessage3
		 */				
		
		testMessage = readResource("EncryptedMessage3.txt");
		originalMsg = new MimeMessage(null, new ByteArrayInputStream(testMessage.getBytes("ASCII")));
		
		
		// verify the message
		// need a new agent because this is a different domain);
		strippedAndVerifiesMessage = agent.processIncoming(originalMsg);
		
		
		assertNotNull(strippedAndVerifiesMessage);
		assertTrue(strippedAndVerifiesMessage.getMessage().toString().length() > 0);
	}
	
	
	public void testDecryptAttachmentMessage() throws Exception
	{
		
	
		// get the keystore file
		File fl = new File("testfile");
		int idx = fl.getAbsolutePath().lastIndexOf("testfile");
		
		String path = fl.getAbsolutePath().substring(0, idx);
		
		File internalKeystoreFile = new File(path + "src/test/resources/keystores/internalKeystore");		
		
		KeyStoreCertificateStore service = new KeyStoreCertificateStore(internalKeystoreFile, 
				internalStorePassword, pkPassword);
		
		X509Certificate caCert = TestUtils.getExternalCert("cacert");
		X509Certificate externCaCert = TestUtils.getExternalCert("externCaCert");
		X509Certificate secureHealthEmailCACert = TestUtils.getExternalCert("secureHealthEmailCACert");
		X509Certificate msCACert = TestUtils.getExternalCert("msanchor");
		
		// anchors cert validation
		Collection<X509Certificate> anchors = new ArrayList<X509Certificate>();
		anchors.add(caCert);
		anchors.add(externCaCert);
		anchors.add(secureHealthEmailCACert);
		anchors.add(msCACert);
		
		NHINDAgent agent = new NHINDAgent("securehealthemail.com", service, 
				service, new TrustAnchorResolver(anchors));
		
		String testMessage = readResource("EncAttachment.txt");
		MimeMessage originalMsg = new MimeMessage(null, new ByteArrayInputStream(testMessage.getBytes("ASCII")));
			
		
		// verify the message
		// need a new agent because this is a different domain);
		IncomingMessage strippedAndVerifiesMessage = agent.processIncoming(originalMsg);
		
		
		assertNotNull(strippedAndVerifiesMessage);
		assertTrue(strippedAndVerifiesMessage.getMessage().toString().length() > 0);	
				
		/*
		 * EncAttachment2.txt
		 */		
		
		agent = new NHINDAgent("securehealthemail.com", service, 
				service, new TrustAnchorResolver(anchors));
		
		testMessage = readResource("EncAttachment2.txt");
		originalMsg = new MimeMessage(null, new ByteArrayInputStream(testMessage.getBytes("ASCII")));
		
		
		// verify the message
		strippedAndVerifiesMessage = agent.processIncoming(originalMsg);
		
		
		assertNotNull(strippedAndVerifiesMessage);
		assertTrue(strippedAndVerifiesMessage.getMessage().toString().length() > 0);	

		/*
		 * LargeEncAttachment.txt
		 */		
		agent = new NHINDAgent("securehealthemail.com", service, 
				service, new TrustAnchorResolver(anchors));
		
		testMessage = readResource("LargeEncAttachment.txt");
		originalMsg = new MimeMessage(null, new ByteArrayInputStream(testMessage.getBytes("ASCII")));
		

		// verify the message
		strippedAndVerifiesMessage = agent.processIncoming(originalMsg);
		
		
		assertNotNull(strippedAndVerifiesMessage);
		assertTrue(strippedAndVerifiesMessage.getMessage().toString().length() > 0);	
	}	
	
	
	public void testEndToEndMessageBase64AttachmentOnly() throws Exception
	{
		// get the keystore file
		File fl = new File("testfile");
		int idx = fl.getAbsolutePath().lastIndexOf("testfile");
		
		String path = fl.getAbsolutePath().substring(0, idx);
		
		File internalKeystoreFile = new File(path + "src/test/resources/keystores/internalKeystore");		
		
		KeyStoreCertificateStore service = new KeyStoreCertificateStore(internalKeystoreFile, 
				internalStorePassword, pkPassword);
		
		X509Certificate caCert = TestUtils.getExternalCert("cacert");
		X509Certificate externCaCert = TestUtils.getExternalCert("externCaCert");
		X509Certificate secureHealthEmailCACert = TestUtils.getExternalCert("secureHealthEmailCACert");
		X509Certificate cernerDemos = TestUtils.getExternalCert("cernerDemosCaCert");		
		
		// anchors cert validation
		Collection<X509Certificate> anchors = new ArrayList<X509Certificate>();
		anchors.add(caCert);
		anchors.add(externCaCert);
		anchors.add(secureHealthEmailCACert);		
		anchors.add(cernerDemos);

		NHINDAgent agent = new NHINDAgent("messaging.cernerdemos.com", service, 
				service, new TrustAnchorResolver(anchors));
		
		String testMessage = readResource("raw2.txt");
		MimeMessage originalMsg = new MimeMessage(null, new ByteArrayInputStream(testMessage.getBytes("ASCII")));		
		
		OutgoingMessage SMIMEenvMessage = agent.processOutgoing(testMessage);
		
		assertNotNull(SMIMEenvMessage);
		assertTrue(SMIMEenvMessage.getMessage().toString().length() > 0);

		
		// verify the message
		// need a new agent because this is a different domain
		agent = new NHINDAgent("securehealthemail.com", service, 
				service, new TrustAnchorResolver(anchors));
		IncomingMessage strippedAndVerifiesMessage = agent.processIncoming(SMIMEenvMessage);
		
		
		assertNotNull(strippedAndVerifiesMessage);
		assertTrue(strippedAndVerifiesMessage.getMessage().toString().length() > 0);
		
		MimeMessage processedMsg = new MimeMessage(null, 
				new ByteArrayInputStream(strippedAndVerifiesMessage.getMessage().toString().getBytes("ASCII")));
		
		assertNotNull(processedMsg);
		
		// can't do a direct compare on headers because the processing may strip some of the recipients
		assertTrue(processedMsg.getContentType().compareTo(originalMsg.getContentType()) == 0);
		assertTrue(originalMsg.getSubject().compareTo(processedMsg.getSubject()) == 0);
				
		// get the message data and compare
		ByteArrayOutputStream oStream = new ByteArrayOutputStream();
		byte[] buffer = new byte[1024];
		int count = 0;
		InputStream inStream = originalMsg.getInputStream();
		while ((count = inStream.read(buffer)) > -1)
			oStream.write(buffer, 0, count);
		
		String originalPart = new String(oStream.toByteArray(), "ASCII");
		
		oStream = new ByteArrayOutputStream();
		count = 0;
		inStream = processedMsg.getInputStream();
		while ((count = inStream.read(buffer)) > -1)
			oStream.write(buffer, 0, count);
		
		String processedPart = new String(oStream.toByteArray(), "ASCII");
		
	}
}

