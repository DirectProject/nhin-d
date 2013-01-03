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

package org.nhindirect.monitor.dao.entity;

import java.util.Calendar;
import java.util.Locale;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.hibernate.annotations.Index;

/**
 * DAO entity object for received notification state.
 * @author Greg Meyer
 * @since 1.0
 */
@Entity
@Table(name = "receivednotification")
public class ReceivedNotification 
{
    private long id = 0L;
	private String messageid;
	private String address;
	private Calendar receivedTime;
	
	/**
	 * Default constructor
	 */
	public ReceivedNotification()
	{
		
	}
	
    /**
     * Get the value of id.
     * 
     * @return the value of id.
     */
    @Id
    @Column(name = "id", nullable = false)
    @GeneratedValue(strategy = GenerationType.AUTO)
    public long getId() 
    {
        return id;
    }

    /**
     * Set the value of id.
     * 
     * @param id
     *            The value of id.
     */
    public void setId(long id) 
    {
        this.id = id;
    }
    
	/**
	 * Gets the message id
	 * @return The message id
	 */
    @Index(name="i_messageid")
    @Column(name = "messageid", nullable = false)
	public String getMessageid()
	{
		return messageid.toLowerCase(Locale.getDefault());
	}
	
    /**
     * Sets the message id
     * @param messageid The id of the original message
     */
    public void setMessageid(String messageid)
    {
    	this.messageid = messageid.toLowerCase(Locale.getDefault());
    }
    
    /**
     * Gets the email address of the final recipient of the notification message
     * @return The email address of the final recipient of the notification message
     */
    @Index(name="i_address")
    @Column(name = "address", nullable = false)
    public String getAddress()
    {
    	return address.toLowerCase(Locale.getDefault());
    }
	
    /**
     * Sets the email address of the final recipient of the notification message
     * @param address The email address of the final recipient of the notification message
     */
    public void setAddress(String address)
    {
    	this.address = address;
    }
    
    /**
     * Gets the time the message was received by the system.
     * @return The time the message was received by the system.
     */
    @Index(name="i_receivedtime")
    @Column(name = "receivedtime")
    @Temporal(TemporalType.TIMESTAMP)
    public Calendar getReceivedTime()
    {
    	return this.receivedTime;
    }
    
    /**
     * Sets the time the message was received by the system.
     * @param time The time the message was received by the system.s
     */
    public void setReceivedTime(Calendar time)
    {
    	receivedTime = (Calendar) time.clone();
    }
}
