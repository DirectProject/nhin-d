package org.nhindirect.config.processor;

import org.nhindirect.config.store.TrustBundle;


public interface BundleRefreshProcessor 
{
	public void refreshBundle(TrustBundle bundle);	
}
