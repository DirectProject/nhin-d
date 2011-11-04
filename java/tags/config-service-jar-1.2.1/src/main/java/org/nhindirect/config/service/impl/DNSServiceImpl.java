/* 
Copyright (c) 2010, NHIN Direct Project
All rights reserved.

Authors:
   Greg Meyer     gm2552@cerner.com
 
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

package org.nhindirect.config.service.impl;

import java.util.Collection;

import javax.jws.WebService;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.nhindirect.config.service.ConfigurationServiceException;
import org.nhindirect.config.service.DNSService;
import org.nhindirect.config.store.DNSRecord;
import org.nhindirect.config.store.dao.DNSDao;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Web service implementation of the DNSService.
 * @author Greg Meyer
 * @since 1.1
 */
@WebService(endpointInterface = "org.nhindirect.config.service.DNSService")
public class DNSServiceImpl implements DNSService
{
	private static final Log log = LogFactory.getLog(DNSServiceImpl.class);

    private DNSDao dao;
    
    /**
     * Initialization method.
     */
    public void init() 
    {
        log.info("DNSService initialized");
    }    
    
    /**
     * {@inheritDoc}
     */
    @Override
	public void addDNS(Collection<DNSRecord> records)
			throws ConfigurationServiceException 
	{
		dao.add(records);
	}

    /**
     * {@inheritDoc}
     */
	@Override
	public Collection<DNSRecord> getDNSByName(String name)
			throws ConfigurationServiceException 
    {
		return dao.get(name);
    }

    /**
     * {@inheritDoc}
     */
	@Override
	public Collection<DNSRecord> getDNSByNameAndType(String name, int type)
			throws ConfigurationServiceException 
	{
		return dao.get(name, type);
	}

    /**
     * {@inheritDoc}
     */
	@Override
	public DNSRecord getDNSByRecordId(long recordId)
			throws ConfigurationServiceException 
	{
		return dao.get(recordId);
	}

    /**
     * {@inheritDoc}
     */
	@Override
	public Collection<DNSRecord> getDNSByRecordIds(long[] recordIds)
			throws ConfigurationServiceException 
	{
		return dao.get(recordIds);
	}

    /**
     * {@inheritDoc}
     */
	@Override
	public Collection<DNSRecord> getDNSByType(int type)
			throws ConfigurationServiceException 
	{
		return dao.get(type);
	}

    /**
     * {@inheritDoc}
     */
	@Override
	public int getDNSCount() throws ConfigurationServiceException 
	{
		return dao.count();
	}

    /**
     * {@inheritDoc}
     */
	@Override
	public void removeDNS(Collection<DNSRecord> records)
			throws ConfigurationServiceException 
	{
		dao.remove(records);
	}

    /**
     * {@inheritDoc}
     */
	@Override
	public void removeDNSByRecordId(long recordId)
			throws ConfigurationServiceException 
	{
		dao.remove(recordId);
	}

    /**
     * {@inheritDoc}
     */
	@Override
	public void removeDNSByRecordIds(long[] recordIds)
			throws ConfigurationServiceException 
	{
		dao.remove(recordIds);
	}

    /**
     * {@inheritDoc}
     */
	@Override
	public void updateDNS(long recordId, DNSRecord record)
			throws ConfigurationServiceException 
	{
		dao.update(recordId, record);
	}
    
    /**
     * Set the value of the DNSDao object.
     * 
     * @param dao
     *            the value of the DNSDao object.
     */
    @Autowired
    public void setDao(DNSDao dao) 
    {
        this.dao = dao;
    }

    /**
     * Return the value of the DNSDao object.
     * 
     * @return the value of the DNSDao object.
     */
    public DNSDao getDao() 
    {
        return dao;
    }
}
