/* 
Copyright (c) 2010, NHIN Direct Project
All rights reserved.

Authors:
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

package org.nhindirect.common.audit.impl;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.lang.management.ManagementFactory;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.DateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collection;
import java.util.Locale;
import java.util.UUID;
import java.util.Vector;

import javax.management.JMException;
import javax.management.MBeanServer;
import javax.management.ObjectName;
import javax.management.StandardMBean;
import javax.management.openmbean.CompositeData;
import javax.management.openmbean.CompositeDataSupport;
import javax.management.openmbean.CompositeType;
import javax.management.openmbean.OpenDataException;
import javax.management.openmbean.OpenType;
import javax.management.openmbean.SimpleType;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.nhindirect.common.audit.AbstractAuditor;
import org.nhindirect.common.audit.AuditContext;
import org.nhindirect.common.audit.AuditEvent;
import org.nhindirect.common.audit.AuditorMBean;
import org.nhindirect.common.audit.annotation.AuditFile;

import com.google.inject.Inject;
import javax.management.openmbean.ArrayType;

/**
 * File based auditor.  Events are stored in a non-circular flat file that is not truncated.  Each event is appended to the end of the audit file.
 * @author Greg Meyer
 * @since 1.0
 */
public class FileAuditor extends AbstractAuditor implements AuditorMBean
{		
	private static final Log LOGGER = LogFactory.getFactory().getInstance(FileAuditor.class);	
	
	/* record meta data goes at the end of each record */
	private static final short RECORD_METADATA_SIZE = 36; 
	
	private static final int RECORD_META_WRAPPER = 0xFFFFFFFF;
	
	private static final DateFormat df = DateFormat.getDateInstance(DateFormat.LONG, Locale.getDefault());

	
	private static final String EVENT_ID = "EVENT ID";
	private static final String EVENT_PRINCIPAL = "EVENT PRINCIPAL";	
	private static final String EVENT_TIME = "EVENT TIME";	
	private static final String EVENT_NAME = "EVENT CATEGORY";
	private static final String EVENT_TYPE = "EVENT MESSAGE";
	private static final String EVENT_CTX = "EVENT CONTEXTS";	
	
	private static final String EVENT_TAG_DELIMITER = "@@@@\r\n";	
	private static final String CONTEXT_TAG_DELIMITER = "====\r\n";
	
	private final RandomAccessFile auditFile;
	private int recordCount = 0;
	
	private CompositeType eventType;
	private String[] itemNames;
	
	
	/**
	 * Constructor.  If the audit file does not exist, then a new file is created barring access permissions or illegal file names or locations.  If the file already
	 * existing, then the file opened in append mode and new events are written to the end of the file.
	 * @param auditFile File descriptor of the audit file.
	 */
	@Inject
	public FileAuditor(@AuditFile File auditFile)
	{
		if (auditFile == null)
			throw new IllegalArgumentException("Audit file cannot be null.");
		
		LOGGER.info("Instantiating FileAuditor");
		
		if (!auditFile.exists())
		{
			LOGGER.info("Audit file does not exist.  Creating new file " + auditFile.getAbsolutePath());
			try
			{
				if (!auditFile.createNewFile())
					throw new IllegalArgumentException("Audit file could not be created.");
			}
			catch (IOException e)
			{
				throw new IllegalArgumentException("Audit file could not be created.", e);
			}
		}
		else
			LOGGER.info("Found existing audit file " + auditFile.getAbsolutePath() + "   Opening in read/write mode.");
		
		try
		{
			this.auditFile = new RandomAccessFile(auditFile, "rw");
		}
		catch (FileNotFoundException e)
		{
			throw new IllegalArgumentException("Audit file could not be found or created.", e);
		}
		
		// initialize the auditor state and validate that the file is not corrupt
		initAuditor();
		
		// register the auditor as an MBean
		registerMBean();
	}
	
	/*
	 * Initialize the auditor.  This includes consistency checks for corruption
	 */
	private void initAuditor()
	{
		try
		{
			if (auditFile.length() == 0)
			{
				// new file
				recordCount = 0;
				auditFile.seek(0);			
			}
			else
			{
				// existing file
				// check it to make sure it is not corrupt
								
				// set the current file position to the last valid record				
				long currentPosition = auditFile.length();
				
				// start at the end of the file and work backwards
				boolean foundValidRecord = false;
				boolean needsFixing = false;
				while (currentPosition >= RECORD_METADATA_SIZE)
				{
					foundValidRecord = isCurrentRecordValid(currentPosition);
					if (foundValidRecord)
						break;							
					else if (!needsFixing)
					{
						LOGGER.warn("Inconsistencies found in audit file.  Attempting to fix issues.  Some data may be lost."); 
						needsFixing = true;
					}
					
					--currentPosition;
				}
				
				// if we could not find a valid record, then just start over
				if (!foundValidRecord)
				{
					recordCount = 0;
					auditFile.seek(0);
				}
			}
		}
		catch (IOException e)
		{
			throw new IllegalStateException("Audit file is corrupt or could not be read.", e);
		}
	}
	
	/*
	 * Register the MBean
	 */
	private void registerMBean()
	{
		
		LOGGER.info("Registering FileAuditor MBean");
		
		try
		{
			itemNames = new String[] {"Event Id", "Event Time", "Event Principal", "Event Name", "Event Type", "Contexts"};
			
			OpenType<?>[] types = {SimpleType.STRING, SimpleType.STRING, SimpleType.STRING, SimpleType.STRING, 
					SimpleType.STRING, ArrayType.getArrayType(SimpleType.STRING)};
			
			eventType = new CompositeType("AuditEvent", "Direct Auditable Event", itemNames, itemNames, types);
		}
		catch (OpenDataException e)
		{
			LOGGER.error("Failed to create settings composite type: " + e.getLocalizedMessage(), e);
			return;
		}
		
		Class<?> clazz = this.getClass();
		final StringBuilder objectNameBuilder = new StringBuilder(clazz.getPackage().getName());
		objectNameBuilder.append(":type=").append(clazz.getSimpleName());
		objectNameBuilder.append(",name=").append(UUID.randomUUID());
				
		try
		{			
			final StandardMBean mbean = new StandardMBean(this, AuditorMBean.class);
		
			final MBeanServer mbeanServer = ManagementFactory.getPlatformMBeanServer();
			mbeanServer.registerMBean(mbean, new ObjectName(objectNameBuilder.toString()));
		}
		catch (JMException e)
		{
			LOGGER.error("Unable to register the FileAuditor MBean", e);
		}		
	}
	
	/*
	 * Ensure that the record at the given position is valid
	 */
	private boolean isCurrentRecordValid(long currentFilePosition) throws IOException
	{
		boolean validRecord = true;
		
		auditFile.seek(currentFilePosition - RECORD_METADATA_SIZE);
		
		// verify the last record is legit
		int start = auditFile.readInt();
		int size = auditFile.readInt();
		recordCount = auditFile.readInt(); 
		
		byte[] sha1 = new byte[20];
		auditFile.read(sha1);
		
		int end = auditFile.readInt();
		
		// verify the start and end of the meta data
		validRecord = (start == RECORD_META_WRAPPER && end == RECORD_META_WRAPPER);
		if (validRecord)
		{
			// calculate the SHA1 of the message
			auditFile.seek(currentFilePosition - RECORD_METADATA_SIZE - size);
			byte[] message = new byte[size];
			
			auditFile.read(message);
			
			// calculate the SHA1 hash of the message
			byte[] digest = generateDigest(message);
				
			validRecord = Arrays.equals(digest, sha1);
		}	
		
		// set the file position back to where we found it
		auditFile.seek(currentFilePosition);
		
		return validRecord;
	}
	
	/*
	 * generates a SHA1 digest of a message
	 */
	private byte[] generateDigest(byte[] message)
	{
		// calculate the SHA1 hash of the message
		try
		{
			MessageDigest md = MessageDigest.getInstance("SHA1");
			md.update(message);
			return md.digest();
		}
		catch (NoSuchAlgorithmException e)
		{
			return new byte[] {};
		}
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void writeEvent(UUID eventId, Calendar eventTimeStamp, String principal, AuditEvent event, Collection<? extends AuditContext> contexts)
	{
		///CLOVER:OFF
		if (LOGGER.isDebugEnabled())
		{
			StringBuilder builder = new StringBuilder("Attempting to write new event to the audit store.");
			builder.append("\r\n\t Event Id: ").append(eventId.toString());
			builder.append("\r\n\t Event Time: ").append(df.format(eventTimeStamp.getTime()));
			builder.append("\r\n\t Event Principal: ").append(principal);
			builder.append("\r\n\t Event Name: ").append(event.getName());
			builder.append("\r\n\t Event Type: ").append(event.getType());
			LOGGER.trace(builder.toString());
		}
		///CLOVER:ON
		
		String recordText = buildRecordText(eventId, eventTimeStamp, principal, event, contexts);
		
		byte[] messageBytes = recordText.getBytes();
		
		// generate the SHA1
		byte[] sha1 =  generateDigest(messageBytes);
		
		try
		{
			// write the message out
			auditFile.writeInt(messageBytes.length);
			auditFile.write(messageBytes);
			auditFile.writeInt(RECORD_META_WRAPPER);
			auditFile.writeInt(messageBytes.length);
			auditFile.writeInt(++recordCount);
			auditFile.write(sha1);
			auditFile.writeInt(RECORD_META_WRAPPER);
		}
		catch (IOException e)
		{
			throw new IllegalStateException("The audit file cannot be written to.", e);
		}
	}

	/*
	 * builds the text of the record that will be placed in the file
	 */
	private String buildRecordText(UUID eventId, Calendar eventTimeStamp, String principal, AuditEvent event, Collection<? extends AuditContext> contexts)
	{
		StringBuilder builder = new StringBuilder();
		
		builder.append("\r\n" + EVENT_ID + ": " + eventId + EVENT_TAG_DELIMITER);
		builder.append("\t" + EVENT_TIME + ": " + df.format(eventTimeStamp.getTime()) + EVENT_TAG_DELIMITER);
		builder.append("\t" + EVENT_PRINCIPAL + ": " + principal + EVENT_TAG_DELIMITER);
		builder.append("\t" + EVENT_NAME + ": " + event.getName() + EVENT_TAG_DELIMITER);
		builder.append("\t" + EVENT_TYPE + ": " + event.getType() + EVENT_TAG_DELIMITER);
		
		if (contexts != null && contexts.size() > 0)
		{
			builder.append("\t" + EVENT_CTX + CONTEXT_TAG_DELIMITER);
			for (AuditContext context : contexts)
				builder.append("\t\t" + context.getContextName() + ":" + context.getContextValue() + CONTEXT_TAG_DELIMITER);
			
			builder.append(EVENT_TAG_DELIMITER);
		}
		
		builder.append("\r\n");
		
		return builder.toString();
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public synchronized Integer getEventCount() 
	{
		return recordCount;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public synchronized CompositeData[] getEvents(Integer eventCount) 
	{
		if (eventType == null || eventCount == 0)
			return null;
		
		Vector<CompositeData> retVal = new Vector<CompositeData>();
		
		/*
		 * Save off the position
		 */
		long savePosition = -1;
		try
		{
			savePosition = auditFile.getFilePointer();
			long currentPosition = savePosition;
		
			// Get the last event
			CompositeData event = getLastEvent();
			if (event != null)
				retVal.add(event);
			
			// keep getting event until either we have reached the requested count
			// or there are no more records
			int cnt = 1;			
			while (cnt < eventCount && event != null)
			{				
				int size = getRecordSize(currentPosition);
				
				// move the file pointer to the last record
				currentPosition -= (RECORD_METADATA_SIZE + size + 4);
				auditFile.seek(currentPosition);
				event = getLastEvent();
				
				if (event != null)
					retVal.add(event);				
				
				++cnt;
			}
		}
		catch (IOException e) 
		{
			/* no-op */
		}
		finally 
		{
			try
			{
				// set the file point back to the orignal position
				if (savePosition > -1)
					auditFile.seek(savePosition);
			}
			catch (IOException e) {/* no-op */}
		}		
		
		return (retVal.size() > 0) ? retVal.toArray(new CompositeData[retVal.size()]) : null;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public synchronized CompositeData getLastEvent() 
	{
		if (eventType == null)
			return null;
		
		CompositeData retVal = null;
		
		long currentPosition = -1;
		try
		{
			// save off the current position and get the last event
			currentPosition = auditFile.getFilePointer();
		
			retVal = getEvent(currentPosition);
		
		}
		catch (IOException e)
		{
			/* no-op */
		}
		finally
		{
			try
			{
				// set the file position back to the original position
				if (currentPosition > -1)
					auditFile.seek(currentPosition);
			}
			catch (IOException e) {/* no-op */}
		}
		
		return retVal;
	}
	
	/*
	 * Gets the size of a record at the before the current file posistion
	 */
	private int getRecordSize(long position)
	{
		int retVal = -1;
		
		try
		{
			long currentPosition = position;
			if (recordCount > 0 && currentPosition >= RECORD_METADATA_SIZE)
			{
				auditFile.seek(currentPosition - RECORD_METADATA_SIZE);
				
				auditFile.readInt();
				retVal = auditFile.readInt();
			}
		}
		catch (IOException e){}
		finally
		{
			try
			{
				// put the file pointer back in the original position
				auditFile.seek(position);
			}
			catch (IOException e) {/* no-op */}
		}
		
		return retVal;
	}
	
	/*
	 * Get the event prior to the file position
	 */
	private CompositeData getEvent(long position)
	{
		CompositeData retVal = null;
		
		try
		{
			long currentPosition = position;
			if (recordCount > 0 && currentPosition >= RECORD_METADATA_SIZE)
			{
				int size = this.getRecordSize(position);
				
				if (size > 0)
				{
					// go to the beginning of the record	
					auditFile.seek(currentPosition - RECORD_METADATA_SIZE - size);
		
					
					byte[] message = new byte[size];
					
					// read the message
					auditFile.read(message);					
					String strMessage = new String(message);
	
					// split into an array using the event delimiter
					String[] eventTags = strMessage.split(EVENT_TAG_DELIMITER);
					
					String id = "";
					String time = "";
					String principal = "";
					String name = "";
					String type = "";
					String[] contexts = null;
					
					for (String tag : eventTags)
					{
						tag = tag.trim();
						
						if (tag.startsWith(EVENT_ID))
							id = getItemText(tag);
						else if (tag.startsWith(EVENT_TIME))
							time = getItemText(tag);
						else if (tag.startsWith(EVENT_PRINCIPAL))
							principal = getItemText(tag);
						else if (tag.startsWith(EVENT_NAME))
							name = getItemText(tag);
						else if (tag.startsWith(EVENT_TYPE))
							type = getItemText(tag);						
						else if (tag.startsWith(EVENT_CTX))
						{
							// need to add the \r\n back on the end
							tag += "\r\n";
							String[] ctx = tag.split(CONTEXT_TAG_DELIMITER);
							if (ctx.length > 1)
							{
								contexts = new String[ctx.length - 1];
								for (int i = 1; i < ctx.length; ++i)
									contexts[i-1] = ctx[i].trim();
							}
						}
					}
					
					if (contexts == null)
						contexts = new String[] {" "};
					
					Object[] eventValues = {id, time, principal, name, type, contexts};
					
					try
					{
						// create the record to be returned
						retVal = new CompositeDataSupport(eventType, itemNames, eventValues);
					}
					catch (OpenDataException e)
					{
						LOGGER.error("Error create composit data for audit event.", e);
					}
				}			
			}
		}
		catch (IOException e)
		{
			LOGGER.error("Error reading audit file to create audit event composite data.", e);
		}
		finally 
		{
			try
			{
				// put the file pointer back in the original position
				auditFile.seek(position);
			}
			catch (IOException e) { /* no-op */} 
		}
		
		return retVal;
	}
	
	/*
	 * get the text of a specific item tag
	 */
	private String getItemText(String item)
	{
		int index = item.indexOf(":");
		if (index > -1)
		{
			return item.substring(index + 1).trim();
		}
		
		return "";
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public synchronized void clear()
	{
		// simply set the file length to 0 and put the file pointer back to the beginning of the file
		try
		{
			auditFile.setLength(0);
			auditFile.seek(0);
			recordCount = 0;
		}
		catch (IOException e) {/*no-op */}
	}
	
}
