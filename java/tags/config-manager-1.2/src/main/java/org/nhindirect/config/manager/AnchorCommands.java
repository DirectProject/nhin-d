package org.nhindirect.config.manager;

import java.io.File;
import java.io.IOException;
import java.security.cert.X509Certificate;
import java.util.Arrays;

import org.apache.commons.io.FileUtils;
import org.nhind.config.Anchor;
import org.nhind.config.ConfigurationServiceProxy;
import org.nhind.config.Domain;
import org.nhindirect.config.manager.printers.AnchorRecordPrinter;
import org.nhindirect.config.model.utils.CertUtils;
import org.nhindirect.dns.tools.utils.Command;
import org.nhindirect.dns.tools.utils.StringArrayUtil;
import org.nhindirect.stagent.CryptoExtensions;

public class AnchorCommands
{
	private static final String LIST_ANCHORS_USAGE = "Lists all anchors in the system";
	
    private static final String IMPORT_ANCHOR_USAGE = "Imports a trust anchor certificate file into the system and associates it to a domain." +
            "\r\n  anchor domainId incoming outgoing" +
            "\r\n\t anchor: Fully qualified path and file name of the X509 certificate anchor file.  " +
            "Place the file name in quotes (\"\") if there are spaces in the path or name." +
            "\r\n\t domainId: Id of the domain that the anchor will be associated with" +
            "\r\n\t incoming: Indicates if the anchor should be used to trust incoming messages.  Valid values are true or false" +
            "\r\n\t outgoing: Indicates if the anchor should be used to trust outgoing messages.  Valid values are true or false";
            
    private static final String DELETE_ANCHOR_USAGE = "Deletes an anchor from the system by id." +
            "\r\n  id" +
            "\r\n\t id: Id of the anchor to be deleted";
    
    private static final String EXPORT_ANCHOR_USAGE = "Exports an anchor to a DER encoded file." +
            "\r\n  id" +
            "\r\n\t id: Id of the anchor to be exported";
	
	protected ConfigurationServiceProxy proxy;
	
	protected final AnchorRecordPrinter anchorPrinter;
	
	public AnchorCommands(ConfigurationServiceProxy proxy)
	{
		this.proxy = proxy;
		
		this.anchorPrinter = new AnchorRecordPrinter();
	}
	
	@Command(name = "ListAnchors", usage = LIST_ANCHORS_USAGE)
    public void listAncors(String[] args)
	{
		try
		{
			final Anchor[] anchors = proxy.listAnchors((long)0, 100000, null);
			
			if (anchors == null || anchors.length == 0)
				System.out.println("No anchors found");
			else
			{
				anchorPrinter.printRecords(Arrays.asList(anchors));
			}
		}
		catch (Exception e)
		{
			System.err.println("Failed to get anchors: " + e.getMessage());
		}
	}
	
	@Command(name = "ImportAnchor", usage = IMPORT_ANCHOR_USAGE)
    public void importAnchor(String[] args)
	{
		final String fileLoc = StringArrayUtil.getRequiredValue(args, 0);
		final String domainId = StringArrayUtil.getRequiredValue(args, 1);
		final boolean incoming = Boolean.parseBoolean(StringArrayUtil.getRequiredValue(args, 2));
		final boolean outgoing = Boolean.parseBoolean(StringArrayUtil.getRequiredValue(args, 3));
		
		try
		{
			// makes sure the domain exists
			final Domain exDomain = proxy.getDomain(Long.parseLong(domainId));
			
			if (exDomain == null)
			{
				System.out.println("The domain with the id " + domainId + " does not exists in the system");
				return;
			}
			
			
			byte[] certBytes = FileUtils.readFileToByteArray(new File(fileLoc));
			
			if (certBytes != null)
			{
				Anchor anchor = new Anchor();
				anchor.setData(certBytes);
				anchor.setIncoming(incoming);
				anchor.setOutgoing(outgoing);
				anchor.setOwner(exDomain.getDomainName());
				proxy.addAnchor(new Anchor[]{anchor});
				System.out.println("Successfully imported trust anchor.");
			}
			
		}
		catch (IOException e)
		{
			System.out.println("Error reading file " + fileLoc + " : " + e.getMessage());
		}
		catch (Exception e)
		{
			System.out.println("Error importing trust anchor " + fileLoc + " : " + e.getMessage());
		}
	}

	
	@Command(name = "ExportAnchor", usage = EXPORT_ANCHOR_USAGE)
    public void exportAnchor(String[] args)
	{	
		final String id = StringArrayUtil.getRequiredValue(args, 0);
		
		try
		{
			// make sure the anchor exists
			long[] ids = new long[]{Long.parseLong(id)};
			final Anchor[] anchors = proxy.getAnchors(ids, null);
			
			if (anchors == null || anchors.length == 0)
			{
				System.out.println("Anchor does not exists.");
				return;
			}
			else
			{
				for (Anchor anchor : anchors)
				{

					final X509Certificate cert = CertUtils.toX509Certificate(anchor.getData());
					final String certFileHold = CryptoExtensions.getSubjectAddress(cert) + ".der";
						
					File certFile = new File(certFileHold);
					if (certFile.exists())
						certFile.delete();
					
					System.out.println("Writing anchor file: " + certFile.getAbsolutePath());
					
					try 
					{
						FileUtils.writeByteArrayToFile(certFile, cert.getEncoded());
					} 
					catch (Exception e) 
					{
						System.err.println("Failed to write anchor to file: " + e.getMessage());
					}
				}
			}
		}
		catch (Exception e)
		{
			System.err.println("Error exporting anchor: " + e.getMessage());
		}
	}
	
	@Command(name = "DeleteAnchor", usage = DELETE_ANCHOR_USAGE)
    public void deleteUnmagedCert(String[] args)
	{
		final String id = StringArrayUtil.getRequiredValue(args, 0);

		try
		{
			// make sure the anchor exists
			long[] ids = new long[]{Long.parseLong(id)};
			final Anchor[] anchors = proxy.getAnchors(ids, null);
			
			if (anchors == null || anchors.length == 0)
			{
				System.out.println("Anchor does not exists.");
				return;
			}
			else
			{
				proxy.removeAnchors(ids);
				System.out.println("Anchor with id " + ids[0] + " removed");
			}
			
		}
		catch (Exception e)
		{
			System.out.println("Error deleting anchor: " + e.getMessage());
		}	
	}
}
