package org.nhindirect.gateway.smtp.dsn.provider;

import org.apache.mailet.Mailet;
import org.nhindirect.gateway.smtp.dsn.DSNCreator;
import org.nhindirect.gateway.smtp.dsn.impl.FailedDeliveryDSNCreator;

import com.google.inject.Provider;

public class FailedDeliveryDSNCreatorProvider implements Provider<DSNCreator> 
{
	protected final Mailet mailet;
	
	/**
	 * Constructor
	 */
	public FailedDeliveryDSNCreatorProvider()
	{
		this.mailet = null;
	}	
	
	
	/**
	 * Construtor
	 * @param mailet Mailet used to retrive configuration parameters
	 */
	public FailedDeliveryDSNCreatorProvider(Mailet mailet)
	{
		this.mailet = mailet;
	}	
	
	/**
	 * {@inheritDoc}
	 */
	public DSNCreator get()
	{
		return new FailedDeliveryDSNCreator(mailet);
	}
}
