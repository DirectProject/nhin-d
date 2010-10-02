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

package org.nhindirect.gateway.smtp;

import java.io.File;
import java.io.IOException;

import org.apache.commons.io.FileUtils;

/**
 * Generic settings for processing messages.
 * @author Greg Meyer
 * @author Umesh Madan
 *
 */
public abstract class MessageProcessingSettings
{
	private File saveMessagesFolder = null;
	
	/**
	 * Sets the folder where messages will be written to disk.  
	 * @param folder The folder where messages will be written to disk. 
	 */
	public void setSaveMessageFolder(File folder)
	{
		saveMessagesFolder = folder;
		
		try
		{
			ensureSaveMessageFolder();
		}
		catch (IOException e)
		{
			/*
			 * TODO: log exception
			 */
			throw new IllegalArgumentException(e);
		}
	}
	
	/**
	 * Gets the folder where messages will be written to desk.
	 * @return The folder where messages will be written to desk.
	 */
	public File getSaveMessageFolder()
	{
		return saveMessagesFolder;
	}
	
	/**
	 * Indicates if messages should be written to disk.  If this setting is null, then messages will not be written.  This is
	 * useful for debugging, but generally should not be set in a production environment.
	 * @return
	 */
	public boolean hasSaveMessageFolder()
	{
		return getSaveMessageFolder() != null;
	}
	
	/*
	 * Ensures that a valid folder exits for messages to be written to.
	 */
	private void ensureSaveMessageFolder() throws IOException
	{
		if (hasSaveMessageFolder() && !getSaveMessageFolder().exists())
				FileUtils.forceMkdir(getSaveMessageFolder());	
	}
}
