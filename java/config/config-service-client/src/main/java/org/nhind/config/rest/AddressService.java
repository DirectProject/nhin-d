package org.nhind.config.rest;

import java.util.Collection;

import org.nhindirect.common.rest.exceptions.ServiceException;
import org.nhindirect.config.model.Address;

public interface AddressService 
{
	public Address getAddress(String address) throws ServiceException;
	
	public Collection<Address> getAddressesByDomain(String domainName) throws ServiceException;
	
	public void addAddress(Address address) throws ServiceException;
	
	public void updateAddress(Address address) throws ServiceException;
	
	public void deleteAddress(String address) throws ServiceException;
	
}
