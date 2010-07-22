package org.nhindirect.stagent;

import java.io.ByteArrayOutputStream;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Arrays;

import javax.mail.internet.InternetHeaders;
import javax.mail.internet.MimeMultipart;


import junit.framework.TestCase;

import org.nhindirect.stagent.Cryptographer;
import org.nhindirect.stagent.MimeEntity;
import org.nhindirect.stagent.cert.X509CertificateEx;
import org.nhindirect.stagent.parser.EntitySerializer;
import org.nhindirect.stagent.parser.Protocol;
import org.nhindirect.stagent.utils.TestUtils;

public class CryptographerTest extends TestCase
{	
	
	public void testEncryptAndDecryptMimeEntity() throws Exception
	{
		X509Certificate cert = TestUtils.getExternalCert("user1");
		
		Cryptographer cryptographer = new Cryptographer();
		
		MimeEntity entity = new MimeEntity();
		entity.setText("Hello world.");
		entity.setHeader(Protocol.ContentTypeHeader, "text/plain");
		entity.setHeader(Protocol.ContentTransferEncodingHeader, "7bit");
		
		
		MimeEntity encEntity = cryptographer.encrypt(entity, cert);
		
		assertNotNull(encEntity);
		
		X509CertificateEx certex = TestUtils.getInternalCert("user1");
		
		MimeEntity decryEntity = cryptographer.decrypt(encEntity, certex);
		
		assertNotNull(decryEntity);
		
		byte[] decryEntityBytes = EntitySerializer.Default.serializeToBytes(decryEntity);
		byte[] entityBytes = EntitySerializer.Default.serializeToBytes(entity);
		
		assertTrue(Arrays.equals(decryEntityBytes, entityBytes));
		
	}
	
	public void testEncryptAndDecryptMultipartEntity() throws Exception
	{
		X509Certificate cert = TestUtils.getExternalCert("user1");
		
		Cryptographer cryptographer = new Cryptographer();
		
		MimeEntity entityText = new MimeEntity();
		entityText.setText("Hello world.");
		entityText.setHeader(Protocol.ContentTypeHeader, "text/plain");
		entityText.setHeader(Protocol.ContentTransferEncodingHeader, "7bit");
		
		MimeEntity entityXML = new MimeEntity();
		entityXML.setText("<Test></Test>");
		entityXML.setHeader(Protocol.ContentTypeHeader, "text/xml");		
		
		MimeMultipart mpEntity = new MimeMultipart();
		
		mpEntity.addBodyPart(entityText);
		mpEntity.addBodyPart(entityXML);
		
		MimeEntity encEntity = cryptographer.encrypt(mpEntity, cert);
		
		assertNotNull(encEntity);
		
		X509CertificateEx certex = TestUtils.getInternalCert("user1");
		
		MimeEntity decryEntity = cryptographer.decrypt(encEntity, certex);
		
		assertNotNull(decryEntity);
		
		ByteArrayOutputStream oStream = new ByteArrayOutputStream();
		mpEntity.writeTo(oStream);
		InternetHeaders hdrs = new InternetHeaders();
		hdrs.addHeader(Protocol.ContentTypeHeader, mpEntity.getContentType());
		MimeEntity orgEntity = new MimeEntity(hdrs, oStream.toByteArray());
		
		byte[] decryEntityBytes = EntitySerializer.Default.serializeToBytes(decryEntity);
		byte[] entityBytes = EntitySerializer.Default.serializeToBytes(orgEntity);

		System.out.println("Original:\r\n" + new String(entityBytes));
		System.out.println("\r\n\r\n\r\nNew:\r\n" + new String(decryEntityBytes));		
		
		
		assertTrue(Arrays.equals(decryEntityBytes, entityBytes));		
		
	
	}	
	
	public void testSignMimeEntity() throws Exception
	{	
		X509CertificateEx certex = TestUtils.getInternalCert("user1");
		
		Cryptographer cryptographer = new Cryptographer();
		
		MimeEntity entity = new MimeEntity();
		entity.setText("Hello world.");
		entity.setHeader(Protocol.ContentTypeHeader, "text/plain");
		entity.setHeader(Protocol.ContentTransferEncodingHeader, "7bit");
		
		SignedEntity signedEnt = cryptographer.sign(entity, certex);
		
		assertNotNull(signedEnt);
		
		byte[] signedEntityBytes = EntitySerializer.Default.serializeToBytes(signedEnt.getContent());
		byte[] entityBytes = EntitySerializer.Default.serializeToBytes(entity);		
		
		assertTrue(Arrays.equals(signedEntityBytes, entityBytes));
		assertNotNull(signedEnt.getSignature());
		
		X509Certificate cert = TestUtils.getExternalCert("user1");
			
		
		cryptographer.checkSignature(signedEnt, cert, new ArrayList<X509Certificate>());
	}

	public void testEncryptAndSignMimeEntity() throws Exception
	{	
		X509Certificate cert = TestUtils.getExternalCert("user1");
		
		Cryptographer cryptographer = new Cryptographer();
		
		MimeEntity entity = new MimeEntity();
		entity.setText("Hello world.");
		entity.setHeader(Protocol.ContentTypeHeader, "text/plain");
		entity.setHeader(Protocol.ContentTransferEncodingHeader, "7bit");

		MimeEntity encEntity = cryptographer.encrypt(entity, cert);
		
		assertNotNull(encEntity);
		
		X509CertificateEx certex = TestUtils.getInternalCert("user1");

		SignedEntity signedEnt = cryptographer.sign(entity, certex);
		
		assertNotNull(signedEnt);

		cryptographer.checkSignature(signedEnt, cert, new ArrayList<X509Certificate>());

	}

}
