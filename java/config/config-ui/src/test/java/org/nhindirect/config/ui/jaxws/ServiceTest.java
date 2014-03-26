package org.nhindirect.config.ui.jaxws;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.security.Key;
import java.security.KeyFactory;
import java.security.KeyStore;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.security.spec.PKCS8EncodedKeySpec;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Enumeration;
import java.util.Iterator;

import javax.security.cert.CertificateEncodingException;
import javax.xml.namespace.QName;
import javax.xml.ws.Service;

import org.apache.commons.io.FileUtils;
import org.apache.log4j.LogManager;
import org.junit.Before;
import org.junit.Test;
import org.nhindirect.config.service.ConfigurationService;
import org.nhindirect.config.service.ConfigurationServiceException;
import org.nhindirect.config.store.Certificate;
import org.nhindirect.config.store.DNSRecord;
import org.nhindirect.config.store.util.DNSRecordUtils;
import org.nhindirect.config.ui.SrvRecord;
import org.nhindirect.config.ui.MainController.CertContainer;
import org.nhindirect.config.ui.form.DNSEntryForm;
import org.nhindirect.config.ui.form.DNSType;
import org.xbill.DNS.CERTRecord;
import org.xbill.DNS.DClass;
import org.xbill.DNS.NSRecord;
import org.xbill.DNS.Name;
import org.xbill.DNS.Record;
import org.xbill.DNS.SOARecord;
import org.xbill.DNS.SRVRecord;
import org.xbill.DNS.TextParseException;
import java.security.cert.X509Certificate;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.security.interfaces.RSAKey;

public class ServiceTest 
{
    private static org.apache.log4j.Logger log = LogManager.getLogger(ServiceTest.class);
    
    private static final QName SERVICE_NAME = new QName("http://impl.service.config.nhindirect.org/", "ConfigurationServiceImplService"); 
    private static final QName PORT_NAME = new QName("http://impl.service.config.nhindirect.org/", "ConfigurationServiceImplPort");
    private static final String WSDL_LOCATION = "http://localhost:8081/config-service/ConfigurationService?wsdl";
    
    private static final String certBasePath = "src/test/resources/certs/";
    
    private URL wsdlURL;
    private Service service;
    private ConfigurationService configSvc;
    
    @Before
    public void oneTimeSetUp() throws Exception
    {
        wsdlURL = new URL(WSDL_LOCATION);
//        service = Service.create(wsdlURL, SERVICE_NAME); 
//        configSvc = service.getPort(PORT_NAME, ConfigurationService.class);
//        assertNotNull(wsdlURL);
//        assertNotNull(service);
//        assertNotNull(configSvc);
    }

    @Test
    public void testPlaceholder()
    {
	}
	
//    @Test
    public void testNS()
    {
    	DNSEntryForm nsForm = new DNSEntryForm();
    	nsForm.setTtl(8455L);
    	nsForm.setName("name3");
    	nsForm.setDest("192.3.4.5");
    	
		try {
			Collection<DNSRecord> arecords = configSvc.getDNSByType(DNSType.NS.getValue());
			for (Iterator<DNSRecord> iter = arecords.iterator(); iter.hasNext();) {
				DNSRecord arec = iter.next();
				NSRecord newrec = (NSRecord) Record.newRecord(Name
						.fromString(arec.getName()), arec.getType(), arec
						.getDclass(), arec.getTtl(), arec.getData());
				System.out.println("target : " + newrec.getTarget());
				System.out.println("name: " + newrec.getName());

			}
		} catch (Exception e) {

		}
    }
    
//  @Test
  public void testSOA()
  {
	  DNSEntryForm SoadnsForm = new DNSEntryForm();
	  SoadnsForm.setName("savvy");
	  SoadnsForm.setTtl(84555L);
	  SoadnsForm.setAdmin("ns.savvy.com");
	  SoadnsForm.setDomain("ns2.savvy.com");
	  SoadnsForm.setSerial(4L);
	  SoadnsForm.setRefresh(6L);
	  SoadnsForm.setRetry(8L);
	  SoadnsForm.setExpire(66L);
	  SoadnsForm.setMinimum(22L);
	  
      Collection<DNSRecord> records = new ArrayList<DNSRecord>();
      records.add(DNSRecordUtils.createSOARecord(SoadnsForm.getName(), SoadnsForm.getTtl(), SoadnsForm.getDomain(), SoadnsForm.getAdmin(), (int)SoadnsForm.getSerial(), SoadnsForm.getRefresh(), SoadnsForm.getRetry(), SoadnsForm.getExpire(), SoadnsForm.getMinimum()));
      
      
      try {
			configSvc.addDNS(records);
			
			Collection<DNSRecord> arecords = configSvc.getDNSByType(DNSType.SOA.getValue());
			for (Iterator<DNSRecord> iter = arecords.iterator(); iter.hasNext();) {
				DNSRecord arec = iter.next();
				SOARecord newrec = (SOARecord)Record.newRecord(Name.fromString(arec.getName()), arec.getType(), arec.getDclass(), arec.getTtl(), arec.getData());
				System.out.println("A admin: " + newrec.getAdmin());
				System.out.println("A name: " + newrec.getName());

			}
			
		} catch (ConfigurationServiceException e) {
			e.printStackTrace();
		} catch (TextParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	  
	  
  }
  
  
//    @Test
//    public void testDomainService()
//    {
//        try
//        {
//            int count = configSvc. getDomainCount();
//            log.info(""+count + " Domains exist in the database");
//        } 
//        catch (ConfigurationServiceException e)
//        {
//            e.printStackTrace();
//            fail(e.getMessage());
//        }  
//    }
    
//    @Test
    public void testDNSService ()
    {
//        try
//        {
            int count = 0;//configSvc.getDNSCount();
//            log.info(""+count + " DNS records exist in the database");
//            System.out.println(""+count + " DNS records exist in the database");
//            configSvc.removeDomain("pjpassoc.com");
            Collection<DNSRecord> records = new ArrayList<DNSRecord>();
//            records.add(DNSRecordUtils.createARecord("pjpassoc.com", 3600L, "192.168.1.1"));
//            records.add(DNSRecordUtils.createMXRecord("pjpassoc.com", "mail.pjpassoc.com", 3600L, 0));
            byte[] certData = null;
    		try {
//				certData = loadPkcs12FromCertAndKey("gm2552.der", "gm2552Key.der");
				certData = loadCertificateData("gm2552.der");
				if (certData != null) {
					// get the owner from the certificate information
					// first transform into a certificate
					CertContainer cont = toCertContainer(certData);
					if (cont != null && cont.getCert() != null) {

						Certificate cert2 = new Certificate();
						cert2.setData(certData);
						System.out.println(getThumbPrint(cont.getCert()));
					}
				}
				
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

//            records.add(DNSEntryForm.createCertRecord("yahoo.com", 3600L, 0,0,0,certData));
//            configSvc.addDNS(records);
//            assertEquals(2, configSvc.getDNSCount());
//        }
//        catch (ConfigurationServiceException e)
//        {
//            e.printStackTrace();
//            fail(e.getMessage());
//        }  
    }

    public void testARecords(){
        Collection<DNSRecord> arecords = null;
//        try {
//			arecords = configSvc.getDNSByType(DNSType.A.getValue());
//            for (Iterator<DNSRecord> iter = arecords.iterator(); iter.hasNext();) 
//            {
//            	DNSRecord arec = iter.next();
//                System.out.println("A data: "+ new String(arec.getData()) );
//                System.out.println("A name: "+ arec.getName());
//                
//            }
//            configSvc.removeDNS(arecords);
//			arecords = configSvc.getDNSByType(DNSType.MX.getValue());
//            for (Iterator<DNSRecord> iter = arecords.iterator(); iter.hasNext();) 
//            {
//            	DNSRecord arec = iter.next();
//                System.out.println("MX data: "+new String(arec.getData()));
//                System.out.println("MX name: "+ arec.getName());
//            }
//            configSvc.removeDNS(arecords);
//
//            
//			arecords = configSvc.getDNSByType(DNSType.AAAA.getValue());
//            for (Iterator<DNSRecord> iter = arecords.iterator(); iter.hasNext();) 
//            {
//            	DNSRecord arec = iter.next();
//                System.out.println("AAAA data: "+ new String(arec.getData()) );
//                System.out.println("AAAA name: "+ arec.getName());
//                
//            }
//            configSvc.removeDNS(arecords);
            
//			arecords = configSvc.getDNSByType(DNSType.CERT.getValue());
//            for (Iterator<DNSRecord> iter = arecords.iterator(); iter.hasNext();) 
//            {
//            	DNSRecord arec = iter.next();
//                
//    			byte[] bytesb = arec.getData();
//    			if (bytesb != null) {
//    				CertContainer cont;
//    				try {
//    					
//    					System.out.println(arec.getName());
//    					System.out.println(arec.getType());
//    					System.out.println(arec.getDclass());
//    					System.out.println(arec.getTtl());
//    					CERTRecord newrec = (CERTRecord)Record.newRecord(Name.fromString(arec.getName()), arec.getType(), arec.getDclass(), arec.getTtl(), arec.getData());
//    					
//    		            byte[] certData = newrec.getCert();
//    		    		try {
//    						if (certData != null) {
//    							// get the owner from the certificate information
//    							// first transform into a certificate
//    							cont = toCertContainer(certData);
//    							if (cont != null && cont.getCert() != null) {
//
//    								Certificate cert2 = new Certificate();
//    								cert2.setData(certData);
//    								System.out.println(getThumbPrint(cont.getCert()));
//    							}
//    						}
//    						
//    					} catch (Exception e) {
//    						// TODO Auto-generated catch block
//    						e.printStackTrace();
//    					}

    					
//    					int keyTag = 0;
//    					CERTRecord rec = new CERTRecord(Name.fromString(arec.getName()), DClass.IN, (long)arec.getTtl(), CERTRecord.PKIX, keyTag, 5, arec.getData());
//    					InputStream str = new ByteArrayInputStream(arec.getData());
//    					X509Certificate certificate = (X509Certificate)CertificateFactory.getInstance("X.509").generateCertificate(str);
//    					System.out.println(getThumbPrint(certificate));
//    					
//    					Certificate cert = new Certificate();
//    					cert.setData(rec.getCert());
//    					
//    					System.out.println("thumbprint: "+cert.getThumbprint());
//    				} catch (Exception e) {
//    					// TODO Auto-generated catch block
//    					e.printStackTrace();
//    				}
//    			}
//                
//                
//        		Collection<SrvRecord> form = new ArrayList<SrvRecord>();
//        		for (Iterator iter2 = arecords.iterator(); iter.hasNext();) {
//        			DNSRecord t = (DNSRecord) iter2.next();
//        			SrvRecord srv = new SrvRecord();
//        			
//        			
//        			
//        			srv.setCreateTime(t.getCreateTime());
//        			srv.setData(t.getData());
//        			srv.setDclass(t.getDclass());
//        			srv.setId(t.getId());
//        			srv.setName(t.getName());
//        			srv.setTtl(t.getTtl());
//        			srv.setType(t.getType());
//        			srv.setThumb("");
//        			byte[] bytes = t.getData();
//        			String thumb = "hello";
//        			if (bytes != null) {
//        				CertContainer cont;
//        				try {
//        					Certificate cert = new Certificate();
//        					cert.setData(bytes);
//        					srv.setThumb(cert.getThumbprint());
//        					System.out.println("thumbprint: "+srv.getThumb());
//        				} catch (Exception e) {
//        					// TODO Auto-generated catch block
//        					e.printStackTrace();
//        				}
//        			}
//        			form.add(srv);
//        		}
//                
//                System.out.println("CERT name: "+ arec.getName());
//                
//            }
////            configSvc.removeDNS(arecords);
//		} catch (ConfigurationServiceException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
    	
    }
    
	private static byte[] loadCertificateData(String certFileName) throws Exception
	{
		File fl = new File(certBasePath + certFileName);
		
		return FileUtils.readFileToByteArray(fl);
	}
	
	private static byte[] loadPkcs12FromCertAndKey(String certFileName, String keyFileName) throws Exception
	{
		byte[] retVal = null;
		try
		{
			KeyStore localKeyStore = KeyStore.getInstance("PKCS12", Certificate.getJCEProviderName());
			
			localKeyStore.load(null, null);
			
			byte[] certData = loadCertificateData(certFileName);
			byte[] keyData = loadCertificateData(keyFileName);
			
			CertificateFactory cf = CertificateFactory.getInstance("X.509");
			InputStream inStr = new ByteArrayInputStream(certData);
			java.security.cert.Certificate cert = cf.generateCertificate(inStr);
			inStr.close();
			
			KeyFactory kf = KeyFactory.getInstance("RSA");
			PKCS8EncodedKeySpec keysp = new PKCS8EncodedKeySpec ( keyData );
			Key privKey = kf.generatePrivate (keysp);
			
			char[] array = "".toCharArray();
			
			localKeyStore.setKeyEntry("privCert", privKey, array,  new java.security.cert.Certificate[] {cert});
			
			ByteArrayOutputStream outStr = new ByteArrayOutputStream();
			localKeyStore.store(outStr, array);
			
			retVal = outStr.toByteArray();
			
			outStr.close();
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		return retVal;
	}
    

	public static String getThumbPrint(X509Certificate cert)
			throws NoSuchAlgorithmException, CertificateEncodingException {
		MessageDigest md = MessageDigest.getInstance("SHA-1");
		byte[] der = null;
		byte[] digest = null;
		try {
			der = cert.getEncoded();
			md.update(der);
			digest = md.digest();
		} catch (java.security.cert.CertificateEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return hexify(digest);

	}

	public static String hexify(byte bytes[]) {

		char[] hexDigits = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9',
				'a', 'b', 'c', 'd', 'e', 'f' };

		StringBuffer buf = new StringBuffer(bytes.length * 2);

		for (int i = 0; i < bytes.length; ++i) {
			buf.append(hexDigits[(bytes[i] & 0xf0) >> 4]);
			buf.append(hexDigits[bytes[i] & 0x0f]);
		}

		return buf.toString();
	}	
    public CertContainer toCertContainer(byte[] data) throws Exception
    {
        CertContainer certContainer = null;
        try
        {
            ByteArrayInputStream bais = new ByteArrayInputStream(data);

            // lets try this a as a PKCS12 data stream first
            try
            {
                KeyStore localKeyStore = KeyStore.getInstance("PKCS12", Certificate.getJCEProviderName());

                localKeyStore.load(bais, "".toCharArray());
                Enumeration<String> aliases = localKeyStore.aliases();


                        // we are really expecting only one alias
                        if (aliases.hasMoreElements())
                        {
                                String alias = aliases.nextElement();
                                X509Certificate cert = (X509Certificate)localKeyStore.getCertificate(alias);

                                // check if there is private key
                                Key key = localKeyStore.getKey(alias, "".toCharArray());
                                if (key != null && key instanceof PrivateKey)
                                {
                                        certContainer = new CertContainer(cert, key);

                                }
                        }
            }
            catch (Exception e)
            {
                // must not be a PKCS12 stream, go on to next step
            }

            if (certContainer == null)
            {
                //try X509 certificate factory next
                bais.reset();
                bais = new ByteArrayInputStream(data);

                X509Certificate cert = (X509Certificate) CertificateFactory.getInstance("X.509").generateCertificate(bais);
                certContainer = new CertContainer(cert, null);
            }
            bais.close();
        }
        catch (Exception e)
        {
            throw new ConfigurationServiceException("Data cannot be converted to a valid X.509 Certificate", e);
        }

        return certContainer;
    }

    public static class CertContainer
    {
                private final X509Certificate cert;
        private final Key key;

        public CertContainer(X509Certificate cert, Key key)
        {
                this.cert = cert;
                this.key = key;
        }

        public X509Certificate getCert()
        {
                        return cert;
                }

                public Key getKey()
                {
                        return key;
                }

    }
    
}
