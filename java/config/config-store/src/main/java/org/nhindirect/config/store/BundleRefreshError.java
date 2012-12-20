package org.nhindirect.config.store;

public enum BundleRefreshError 
{
	SUCCESS,
	
	NOT_FOUND,
	
	DOWNLOAD_TIMEOUT,
	
	INVALID_BUNDLE_FORMAT,
	
	INVALID_SIGNING_CERT,
	
	UNMATCHED_SIGNATURE
}
