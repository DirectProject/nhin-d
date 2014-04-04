package org.nhindirect.stagent.james.mailet;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Properties;

import javax.mail.Message;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import org.nhindirect.stagent.NHINDAgentTest;

public class MailSender 
{
	
	private static String readResource(String _rec) throws Exception
	{
		
		int BUF_SIZE = 2048;		
		int count = 0;
	
		BufferedInputStream imgStream = new BufferedInputStream(NHINDAgentTest.class.getResourceAsStream(_rec));
				
		ByteArrayOutputStream ouStream = new ByteArrayOutputStream();

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


		return new String(ouStream.toByteArray());		
	}	
	
	public static void main(String[] args)
	{
		
		if (args.length == 0)
		{
			// error out
		}
		else
		{
			if (args[0].compareTo("-sign") == 0)
			{
				try
				{
				
			        Properties p = new Properties();
			        //p.put("mail.smtp.host", "localhost");
			        //p.put("mail.smtp.port", "10025");
			        p.put("mail.smtp.host", "smtprr.cerner.com");
			        p.setProperty("mail.debug", "true");	   
			        
			        // start a session
			        javax.mail.Session session = javax.mail.Session.getDefaultInstance(p, null);
			
			        // create the message
			        // create the message
			        MimeMessage message = new MimeMessage(session);
			        
			        message.setFrom(new InternetAddress("gmeyer@cerner.com"));
			        message.addRecipients(Message.RecipientType.TO, new InternetAddress[] {new InternetAddress("gm2552@securehealthemail.com"),
			        		new InternetAddress("ryan@securehealthemail.com")});
		            message.setSubject("Test subject:");
		            String text = "This is some test text to attempt to encypt and sign.";
		            message.setText(text);
		            
		            Transport trans = session.getTransport("smtp");
		
		            trans.connect();
		            message.saveChanges();
		            trans.sendMessage(message, message.getAllRecipients());
		            trans.close();            
				}
				catch (Exception e)
				{
					e.printStackTrace();
				}
			}
			else
			{
				try
				{
					String msgText = readResource("EncryptedMessage.txt");
					
			        Properties p = new Properties();
			        p.put("mail.smtp.host", "localhost");
			        p.put("mail.smtp.port", "10025");
			        	        
			        // start a session
			        javax.mail.Session session = javax.mail.Session.getInstance(p, null);
			
			        // create the message
			        MimeMessage message = new MimeMessage(session, new ByteArrayInputStream(msgText.getBytes("ASCII")));
			        
			        Transport trans = session.getTransport("smtp");
			        trans.connect();
			        
		            message.saveChanges();
		            trans.sendMessage(message, message.getAllRecipients());
		            trans.close();
					
				}
				catch (Exception e)
				{
					e.printStackTrace();
				}				
			}
		}
	}
}
