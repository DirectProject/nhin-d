package org.nhindirect.stagent.testmodules;


import org.nhindirect.stagent.annotation.PrivateCerts;
import org.nhindirect.stagent.annotation.PublicCerts;
import org.nhindirect.stagent.cert.CertificateResolver;
import org.nhindirect.stagent.cert.impl.KeyStoreCertificateStore;
import org.nhindirect.stagent.cert.impl.annotation.CertStoreKeyFile;
import org.nhindirect.stagent.cert.impl.annotation.CertStoreKeyFileCrl;
import org.nhindirect.stagent.cert.impl.annotation.CertStoreKeyFilePassword;
import org.nhindirect.stagent.cert.impl.annotation.CertStoreKeyFilePrivKeyPassword;

import com.google.inject.AbstractModule;

public class CertResolverTestModule extends AbstractModule 
{
	private final String keyStoreFile;
	private final String keyStorePassword;
	private final String keyStorePrivPassword;
	private final String certificateRevocationListFile;
	
	public CertResolverTestModule(String keyStoreFileName, String keyStorePassword, String  keyStorePrivPassword, String certificateRevocationListFile)
	{
		keyStoreFile = keyStoreFileName;
		this.keyStorePassword = keyStorePassword;
		this.keyStorePrivPassword = keyStorePrivPassword;
		this.certificateRevocationListFile = certificateRevocationListFile;
	}
	
	protected void configure()
	{
		bindConstant().annotatedWith(CertStoreKeyFile.class).to(keyStoreFile);
		bindConstant().annotatedWith(CertStoreKeyFilePassword.class).to(keyStorePassword);
		bindConstant().annotatedWith(CertStoreKeyFilePrivKeyPassword.class).to(keyStorePrivPassword);
	    bindConstant().annotatedWith(CertStoreKeyFileCrl.class).to(certificateRevocationListFile);
		this.bind(CertificateResolver.class).annotatedWith(PrivateCerts.class).to(KeyStoreCertificateStore.class);
		this.bind(CertificateResolver.class).annotatedWith(PublicCerts.class).to(KeyStoreCertificateStore.class);		
	}

}
