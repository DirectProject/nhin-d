package org.nhindirect.config.manager;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;
import org.nhind.config.ConfigurationServiceProxy;
import org.nhind.config.Domain;
import org.nhind.config.TrustBundle;
import org.nhind.config.TrustBundleAnchor;
import org.nhind.config.TrustBundleDomainReltn;
import org.nhindirect.config.manager.printers.BundleAnchorRecordPrinter;
import org.nhindirect.config.manager.printers.TrustBundleRecordPrinter;
import org.nhindirect.dns.tools.utils.Command;
import org.nhindirect.dns.tools.utils.StringArrayUtil;

public class TrustBundleCommands 
{
    private static final String ADD_TRUST_BUNDLE = "Adds a trust bundle to the system." +
            "\r\n  bundleName URL refreshInterval [signingCert]" +
            "\r\n\t bundleName: The name of the bundle.  MUST be unique" +
            "\r\n\t URL: URL of the bundle." +
            "\r\n\t refreshInterval: The interval in minutes that the bundle will be refreshed." +            
            "\r\n\t signingCert: Optional certificate that signed the bundle.  This the location and file name of the signing certficate. "
            + "This is generally used for bundles not protected by HTTPS.";
	
    private static final String REMOVE_TRUST_BUNDLE = "Removes a trust bundle from the system.  The bundle is automatically removed from all domains" +
            "\r\n  bundleId " +
            "\r\n\t bundleId: The id of the bundle to remove.";
    
	private static final String LIST_BUNDLES_USAGE = "Lists all trust bundles in the system";
    
    private static final String ADD_BUNDLE_TO_DOMAIN = "Adds a trust bundle to a domain." +
            "\r\n  bundleId domainId trustIncoming trustOutgoing" +
            "\r\n\t bundleId: The id of the bundle to add to the domain." +
            "\r\n\t domainId: The id of the domain that the bundle will be added to." +
            "\r\n\t trustIncoming: Indicates if the bundle should be used to trust incoming messages.  Valid values are true or false" +
            "\r\n\t trustOutgoing: Indicates if the bundle should be used to trust outgoing messages.  Valid values are true or false";
    
    private static final String REMOVE_BUNDLE_FROM_DOMAIN = "Removes a trust bundle from a domain." +
            "\r\n  bundleId domainId trustIncoming trustOutgoing" +
            "\r\n\t bundleId: The id of the bundle to add to the domain." +
            "\r\n\t domainId: The id of the domain that the bundle will be added to."; 
    
	private static final String LIST_DOMAIN_BUNDLES_USAGE = "Lists all trust bundles associated to a domain" +
            "\r\n  domainId " +
            "\r\n\t domainId: The id of the domain to list bundles for.";

	private static final String LIST_BUNDLE_ANCHORS = "Lists all anchors within a trust bundle" +
            "\r\n  bundleId " +
            "\r\n\t bundleId: The id of the bundle to list anchors for.";
	
	protected ConfigurationServiceProxy proxy;
	
	protected TrustBundleRecordPrinter bundlePrinter;
	
	protected BundleAnchorRecordPrinter anchorPrinter;
	
	public TrustBundleCommands(ConfigurationServiceProxy proxy)
	{
		this.proxy = proxy;
		
		this.bundlePrinter = new TrustBundleRecordPrinter();
		
		this.anchorPrinter = new BundleAnchorRecordPrinter();
	}
	
	@Command(name = "AddTrustBundle", usage = ADD_TRUST_BUNDLE)
    public void addTrustBundle(String[] args)
	{
		final String bundleName = StringArrayUtil.getRequiredValue(args, 0);
		final String url = StringArrayUtil.getRequiredValue(args, 1);
		final int refreshInterval = Integer.parseInt(StringArrayUtil.getRequiredValue(args, 2)) * 60; // convert minutes to seconds
		final String signingCertFile = StringArrayUtil.getOptionalValue(args, 3, "");
		
		try
		{
			final TrustBundle exBundle = proxy.getTrustBundleByName(bundleName);
			
			if (exBundle != null)
			{
				System.out.println("Bundle with name " +  bundleName + " already exists.");
			}
			else
			{
				
				final TrustBundle newBundle = new TrustBundle();
				newBundle.setBundleName(bundleName);
				newBundle.setBundleURL(url);
				newBundle.setRefreshInterval(refreshInterval);
				
				if (!StringUtils.isEmpty(signingCertFile))
				{
					final byte[] signCertData = FileUtils.readFileToByteArray(new File(signingCertFile));
					newBundle.setSigningCertificateData(signCertData);
				}
				proxy.addTrustBundle(newBundle);
				System.out.println("Trust bundle " + bundleName + " added to the system.");
			}
			
		}
		catch (Exception e)
		{
			System.out.println("Error adding trust bundle " + bundleName + " : " + e.getMessage());
		}
		
	}
	
	@Command(name = "DeleteTrustBundle", usage = REMOVE_TRUST_BUNDLE)
    public void removeTrustBundle(String[] args)
	{
		final long bundleId = Long.parseLong(StringArrayUtil.getRequiredValue(args, 0));
		
		try
		{
			final TrustBundle bundle = proxy.getTrustBundleById(bundleId);
			
			if (bundle == null)
			{
				System.out.println("Bundle with id " +  bundleId + " does not exist.");
				return;
			}
			
			proxy.deleteTrustBundles(new Long[]{bundleId});
			
			System.out.println("Trust bundle " + bundle.getBundleName() + " deleted");
			
		}
		catch (Exception e)
		{
			System.out.println("Error deleting trust bundle: " + e.getMessage());
		}
	}
	
	@Command(name = "ListTrustBundles", usage = LIST_BUNDLES_USAGE)
    public void listBundles(String[] args)
	{
		try
		{
			final TrustBundle[] bundles = proxy.getTrustBundles(false);
			
			if (bundles == null || bundles.length == 0)
				System.out.println("No bundles found");
			else
			{
				bundlePrinter.printRecords(Arrays.asList(bundles));
			}
		}
		catch (Exception e)
		{
			System.out.println("Error getting trust bundles" + e.getMessage());
		}
	}
	
	
	@Command(name = "DeleteTrustBundleFromDomain", usage = REMOVE_BUNDLE_FROM_DOMAIN)
    public void deleteTrustBundleFromDomain(String[] args)
	{
		final long bundleId = Long.parseLong(StringArrayUtil.getRequiredValue(args, 0));
		final long domainId = Long.parseLong(StringArrayUtil.getRequiredValue(args, 1));
		
		try
		{
			final TrustBundle bundle = proxy.getTrustBundleById(bundleId);
			
			if (bundle == null)
			{
				System.out.println("Bundle with id " +  bundleId + " does not exist.");
				return;
			}
			
			final Domain domain = proxy.getDomain(domainId);
			
			if (domain == null)
			{
				System.out.println("Domain with id " +  domainId + " does not exist.");
				return;
			}
			
			// make sure there is already an association
			boolean associationExists = false;
			final TrustBundleDomainReltn[] reltns = proxy.getTrustBundlesByDomain(domainId, false);
			if (reltns != null && reltns.length > 0)
			{
				for (TrustBundleDomainReltn reltn : reltns) 
				{
					if (reltn.getTrustBundle().getId() == bundleId)
					{
						associationExists = true;
						break;
					}
				}
			}
			
			if (!associationExists)
			{
				System.out.println("Bundle " +  bundle.getBundleName() + " is not associated with domain " + domain.getDomainName());
				return;
			}
			
			proxy.disassociateTrustBundleFromDomain(domainId, bundleId);
			
			System.out.println("Trust bundle " + bundle.getBundleName() + " removed from domain " + domain.getDomainName());
			
		}
		catch (Exception e)
		{
			System.out.println("Error removing bundle from domain : " + e.getMessage());
		}		
	}
	
	@Command(name = "AddTrustBundleToDomain", usage = ADD_BUNDLE_TO_DOMAIN)
    public void addTrustBundleToDomain(String[] args)
	{
		final long bundleId = Long.parseLong(StringArrayUtil.getRequiredValue(args, 0));
		final long domainId = Long.parseLong(StringArrayUtil.getRequiredValue(args, 1));
		final boolean trustIncoming = Boolean.parseBoolean(StringArrayUtil.getRequiredValue(args, 2));
		final boolean trustOutgoing = Boolean.parseBoolean(StringArrayUtil.getRequiredValue(args, 3)); 
		
		try
		{
			final TrustBundle bundle = proxy.getTrustBundleById(bundleId);
			
			if (bundle == null)
			{
				System.out.println("Bundle with id " +  bundleId + " does not exist.");
				return;
			}
			
			final Domain domain = proxy.getDomain(domainId);
			
			if (domain == null)
			{
				System.out.println("Domain with id " +  domainId + " does not exist.");
				return;
			}
			
			// make sure there isn't already an association
			final TrustBundleDomainReltn[] reltns = proxy.getTrustBundlesByDomain(domainId, false);
			if (reltns != null && reltns.length > 0)
			{
				for (TrustBundleDomainReltn reltn : reltns) 
				{
					if (reltn.getTrustBundle().getId() == bundleId)
					{
						System.out.println("Bundle " +  bundle.getBundleName() + " is already associated with domain " + domain.getDomainName());
						return;
					}
				}
			}
			
			proxy.associateTrustBundleToDomain(domainId, bundleId, trustIncoming, trustOutgoing);
			
			System.out.println("Trust bundle " + bundle.getBundleName() + " added to domain " + domain.getDomainName());
			
		}
		catch (Exception e)
		{
			System.out.println("Error associating bundle to domain : " + e.getMessage());
		}
	}
	
	@Command(name = "ListDomainBundles", usage = LIST_DOMAIN_BUNDLES_USAGE)
    public void listDomainBundles(String[] args)
	{
	
		final long domainId = Long.parseLong(StringArrayUtil.getRequiredValue(args, 0));
		
		try
		{
			final Domain domain = proxy.getDomain(domainId);
			
			if (domain == null)
			{
				System.out.println("Domain with id " +  domainId + " does not exist.");
				return;
			}
			
			// make sure there isn't already an association
			final TrustBundleDomainReltn[] reltns = proxy.getTrustBundlesByDomain(domainId, false);
			if (reltns == null || reltns.length == 0)
			{
				System.out.println("No bundles associated with domain " +  domain.getDomainName());
				return;
			}
			
			List<TrustBundle> bundles = new ArrayList<TrustBundle>();
			for (TrustBundleDomainReltn reltn : reltns) 
				bundles.add(reltn.getTrustBundle());

			System.out.println("Bundles associated with domain " +  domain.getDomainName());
			bundlePrinter.printRecords(bundles);
			
		}
		catch (Exception e)
		{
			System.out.println("Error getting domain bundles : " + e.getMessage());
		}
	}
	
	@Command(name = "ListTrustBundleAnchors", usage = LIST_BUNDLE_ANCHORS)
    public void listBundleAnchors(String[] args)
	{
		final long bundleId = Long.parseLong(StringArrayUtil.getRequiredValue(args, 0));
		
		try
		{
			final TrustBundle bundle = proxy.getTrustBundleById(bundleId);
			
			if (bundle == null)
			{
				System.out.println("Bundle with id " +  bundleId + " does not exist.");
				return;
			}

			if (bundle.getLastSuccessfulRefresh() == null)
			{
				System.out.println("Bundle has never been successfully downloaded.");
				return;
			}
			
			final TrustBundleAnchor[] anchors = bundle.getTrustBundleAnchors();
			if (anchors == null || anchors.length == 0)
			{
				System.out.println("Bundle has not anchors.");
				return;			
			}
			anchorPrinter.printRecords(Arrays.asList(anchors));

			
		}
		catch (Exception e)
		{
			System.out.println("Error deleting trust bundle: " + e.getMessage());
		}
	}
	
}
