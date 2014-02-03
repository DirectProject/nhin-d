package org.nhind.config.rest;

import java.util.Collection;

import org.nhindirect.common.rest.exceptions.ServiceException;
import org.nhindirect.config.model.DNSRecord;

public interface DNSService 
{
	public Collection<DNSRecord> getDNSRecord(int type, String name) throws ServiceException;
	
	public void addDNSRecord(DNSRecord record) throws ServiceException;
	
	public void updatedDNSRecord(DNSRecord dnsRecord) throws ServiceException;
	
	public void deleteDNSRecordsByIds(Collection<Long> ids) throws ServiceException;
}
