package org.nhindirect.stagent.cert;

public interface CacheableCertStore 
{
	public void flush(boolean purgeBootStrap);
	
	public void setBootStrap(CertificateStore bootstrapStore);
	
	public void loadBootStrap();
	
	public void loadBootStrap(CertificateStore bootstrapStore);
	
	public void setCachePolicy(CertStoreCachePolicy policy); 
}
