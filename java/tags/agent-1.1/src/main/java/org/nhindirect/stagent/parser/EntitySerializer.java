/* 
Copyright (c) 2010, NHIN Direct Project
All rights reserved.

Authors:
   Umesh Madan     umeshma@microsoft.com
   Greg Meyer      gm2552@cerner.com
 
Redistribution and use in source and binary forms, with or without modification, are permitted provided that the following conditions are met:

Redistributions of source code must retain the above copyright notice, this list of conditions and the following disclaimer.
Redistributions in binary form must reproduce the above copyright notice, this list of conditions and the following disclaimer 
in the documentation and/or other materials provided with the distribution.  Neither the name of the The NHIN Direct Project (nhindirect.org). 
nor the names of its contributors may be used to endorse or promote products derived from this software without specific prior written permission.
THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, 
THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS 
BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE 
GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, 
STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF 
THE POSSIBILITY OF SUCH DAMAGE.
*/

package org.nhindirect.stagent.parser;


import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Reader;
import java.io.Writer;
import java.util.Collection;

import org.apache.commons.io.IOUtils;
import org.nhindirect.stagent.mail.MimeError;
import org.nhindirect.stagent.mail.MimeException;

import javax.mail.MessagingException;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import javax.mail.internet.MimePart;

/**
 * Serializes and deserializes {@link MimeParts} objects.
 * @author Greg Meyer
 * @author Umesh Madan
 *
 */
public class EntitySerializer 
{
	/**
	 * Default EntitySerializer implementation.
	 */
    public static final EntitySerializer Default = new EntitySerializer();
    
    /**
     * Constructs a default EntitySerializer.
     */
    public EntitySerializer()
    {
    }
    
    /**
     * Serializes a MimePart to and output stream.
     * @param entity The entity to serialize.
     * @param stream The output stream that the serialized object will be written to.
     */
    public void serialize(MimePart message, OutputStream stream)
    {
    	try
    	{
    		message.writeTo(stream);
    	}
    	catch (Exception e)
    	{
    		throw new MimeException(MimeError.Unexpected, e);
    	}
    }
    
    /**
     * Serializes a MimePart to a writer.
     * @param entity The entity to serialize.
     * @param stream The writer that the serialized object will be written to.
     */
    public void serialize(MimePart message, Writer writer)
    {
    	
    	try
    	{
    		ByteArrayOutputStream oStream = new ByteArrayOutputStream();
    		serialize(message, oStream);
    		oStream.flush();
    		IOUtils.write(oStream.toByteArray(), writer, "ASCII");
    		oStream.close();
    		
    	}
    	catch (Exception e)
    	{
    		throw new MimeException(MimeError.Unexpected, e);
    	}
    }
    
    /**
     * Serializes a MimePart to a String.
     * @param entity The entity to serialize.
     * @return A raw String representation of the entity.
     */
    public String serialize(MimePart message)
    {
    	String retVal = "";
    	
    	try
    	{
    		ByteArrayOutputStream oStream = new ByteArrayOutputStream();
    		serialize(message, oStream);
    		oStream.flush();
    		retVal = oStream.toString("ASCII");
    		oStream.close();
    		
    	}
    	catch (Exception e)
    	{
    		throw new MimeException(MimeError.Unexpected, e);
    	}
    	
        
        return retVal;
    }
    
    /**
     * Serializes a MimePart to a byte array.
     * @param entity The entity to serialize.
     * @return A raw byte representation of the entity.
     */
    public byte[] serializeToBytes(MimePart message)
    {
    	byte[] retVal;    	
    	try
    	{
    		ByteArrayOutputStream oStream = new ByteArrayOutputStream();
    		serialize(message, oStream);
    		oStream.flush();
    		retVal = oStream.toByteArray();
    		oStream.close();
    		
    	}
    	catch (Exception e)
    	{
    		throw new MimeException(MimeError.Unexpected, e);
    	}
    	
    	return retVal;
    }

    /**
     * Serializes a collection of MimeBodyPart to a writer with a given boundary.
     * @param entity The entities to serialize.
     * @param boundary The boundary string that will separate each entity.
     * @param writer The writer that the entities will be serialized to.
     */
    public void serialize(Collection<MimeBodyPart> parts, String boundary, Writer writer)
    {
        if (parts == null || parts.size() == 0)
        {
            throw new IllegalArgumentException();
        }
        
        try
        {
    		ByteArrayOutputStream oStream = new ByteArrayOutputStream();
    		serialize(parts, boundary, oStream);
    		oStream.flush();
    		String str = oStream.toString("ASCII");
    		writer.write(str, 0, str.length());
    		oStream.close();	        
        }
    	catch (Exception e)
    	{
    		throw new MimeException(MimeError.Unexpected, e);
    	}                
    }

    /**
     * Serializes a collection of MimeBodyPart to a string with a given boundary.
     * @param entity The entities to serialize.
     * @param boundary The boundary string that will separate each entity.
     * @return A raw String representation of the serialized entities.
     */    
    public String serialize(Collection<MimeBodyPart> parts, String boundary)
    {
    	String retVal = "";
           
        try
        {	        
    		ByteArrayOutputStream oStream = new ByteArrayOutputStream();
    		serialize(parts, boundary, oStream);
    		oStream.flush();
    		retVal = oStream.toString("ASCII");

    		oStream.close();	        
        }
    	catch (Exception e)
    	{
    		throw new MimeException(MimeError.Unexpected, e);
    	}                    
    	
    	return retVal;
	}
    
    /**
     * Serializes a collection of MimeBodyPart to an output stream with a given boundary.
     * @param entity The entities to serialize.
     * @param boundary The boundary string that will separate each entity.
     * @param stream The output stream that the entities will be serialized to.
     */
    public void serialize(Collection<MimeBodyPart> parts, String boundary, OutputStream stream)
    {        
        if (parts == null || parts.size() == 0)
        {
            throw new IllegalArgumentException();
        }
        
        try
        {
	        MimeMultipart mm = new MimeMultipart();
	        for (MimeBodyPart part : parts)
	        {
	        	mm.addBodyPart(part);
	        }
	        
    		ByteArrayOutputStream oStream = new ByteArrayOutputStream();
    		mm.writeTo(oStream);
    		oStream.flush();
        }
    	catch (Exception e)
    	{
    		throw new MimeException(MimeError.Unexpected, e);
    	}                        	
    }

    /**
     * Serializes a collection of MimeBodyPart to a byte array with a given boundary.
     * @param entity The entities to serialize.
     * @param boundary The boundary string that will separate each entity.
     * @return A raw byte array representation of the serialized entities.
     */    
    public byte[] serializeToBytes(Collection<MimeBodyPart> parts, String boundary)
    {               
    	byte[] retVal = null;
    	
        try
        {
	        
    		ByteArrayOutputStream oStream = new ByteArrayOutputStream();
    		serialize(parts, boundary, oStream);
    		oStream.flush();
    		retVal = oStream.toByteArray();
        }
    	catch (Exception e)
    	{
    		throw new MimeException(MimeError.Unexpected, e);
    	}   
    	
    	return retVal;
    }

    
    /**
     * Deserializes a MimeMessage from an input stream.
     * @param stream The input stream containing the serialized entity.
     * @return A MimeMessage deserialized from the input stream.
     */     
    public MimeMessage deserialize(InputStream stream)
    {
        if (stream == null)
        {
            throw new IllegalArgumentException();
        }

        MimeMessage msg = null;
        
        try
        {
        	msg = new MimeMessage(null, stream);
        }
        catch (MessagingException e)
        {
        	throw new MimeException(MimeError.Unexpected, e);
        }
        
        return msg;
    }

    /**
     * Deserializes a MimeMessage from a reader.
     * @param stream The reader containing the serialized entity.
     * @return A MimeMessage deserialized from the reader.
     */  
    public MimeMessage deserialize(Reader reader)
    {
    	MimeMessage retVal = null;
    	
        if (reader == null)
        {
            throw new IllegalArgumentException();
        }        

        try
        {
        	
	        ByteArrayInputStream inStream = new ByteArrayInputStream(IOUtils.toByteArray(reader, "ASCII"));
	        
	        retVal = deserialize(inStream);
        }
        catch (IOException e)
        {
        	throw new MimeException(MimeError.Unexpected, e);
        }
        
        return retVal;        
    }

    /**
     * Deserializes a MimeMessage from a raw String representation.
     * @param stream A raw String representation of the entity.
     * @return A MimeMessage deserialized from the string.
     */      
    public MimeMessage deserialize(String messageText)
    {
    	MimeMessage retVal = null;
    	
        if (messageText == null  || messageText.length() == 0)
        {
            throw new IllegalArgumentException();
        }
        
        try
        {
        	ByteArrayInputStream inStream = new ByteArrayInputStream(messageText.getBytes("ASCII"));
        	retVal = deserialize(inStream);
        }
    	catch (Exception e)
    	{
    		throw new MimeException(MimeError.Unexpected, e);
    	}   
    	
        return retVal;
    }
    
    /**
     * Deserializes a MimeMessage from a raw byte array representation.
     * @param stream A raw byte array representation of the entity.
     * @return A MimeMessage deserialized from the byte array.
     */      
    public MimeMessage deserialize(byte[] messageBytes)
    {
    	MimeMessage retVal = null;
    	
        if (messageBytes == null  || messageBytes.length == 0)
        {
            throw new IllegalArgumentException();
        }
        
        try
        {
        	ByteArrayInputStream inStream = new ByteArrayInputStream(messageBytes);
        	retVal = deserialize(inStream);
        }
    	catch (Exception e)
    	{
    		throw new MimeException(MimeError.Unexpected, e);
    	}   
    	
        return retVal;
    }
}
