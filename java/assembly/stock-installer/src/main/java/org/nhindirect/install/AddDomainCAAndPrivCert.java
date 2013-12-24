package org.nhindirect.install;

import java.io.File;

import org.apache.commons.io.FileUtils;
import org.nhind.config.Anchor;
import org.nhind.config.Certificate;
import org.nhind.config.ConfigurationServiceProxy;
import org.nhind.config.Domain;
import org.nhind.config.EntityStatus;

public class AddDomainCAAndPrivCert
{
	public static void main(String[] args)
	{
		final String configServiceUrl = args[0];
		final String domainName = args[1];
		final String caCommonName = args[2];
		final String certCommonName = args[3];
		
		try
		{
			final ConfigurationServiceProxy cfService = new ConfigurationServiceProxy(configServiceUrl);
	
			final Domain domain = new Domain();
			domain.setDomainName(domainName);
			domain.setPostMasterEmail("postmaster@" + domainName);
			domain.setStatus(EntityStatus.ENABLED);
			
			cfService.addDomain(domain);
			
			// now add the anchor and cert
			final File caFile = AbstractCertCreator.createNewFileName(caCommonName, false);
			final Anchor anchor = new Anchor();
			anchor.setData(FileUtils.readFileToByteArray(caFile));
			anchor.setOwner(domainName);
			anchor.setIncoming(true);
			anchor.setOutgoing(true);
			anchor.setStatus(EntityStatus.ENABLED);

			cfService.addAnchor(new Anchor[] {anchor});
			
			final File certFile = AbstractCertCreator.createNewFileName(certCommonName, false);		
			final String certFileName = certFile.getName();
			int idx = certFileName.lastIndexOf(".der");
			final String p12FileName = certFileName.substring(0, idx) + ".p12";
			
			final Certificate cert = new Certificate();
			cert.setData(FileUtils.readFileToByteArray(new File(p12FileName)));
			cert.setStatus(EntityStatus.ENABLED);

			cfService.addCertificates(new Certificate[] {cert});
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}
}
