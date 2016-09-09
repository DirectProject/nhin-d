package org.nhindirect.config.ui.form;
/* 
Copyright (c) 2010, Direct Project
All rights reserved.

Authors:
   Pat Pyette     ppyette@inpriva.com
 
Redistribution and use in source and binary forms, with or without modification, are permitted provided that the following conditions are met:

Redistributions of source code must retain the above copyright notice, this list of conditions and the following disclaimer.
Redistributions in binary form must reproduce the above copyright notice, this list of conditions and the following disclaimer in the documentation and/or other materials provided with the distribution.
Neither the name of The Direct Project (directproject.org) nor the names of its contributors may be used to endorse or promote products derived from this software without specific prior written permission.

THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, 
BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT 
SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS 
INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE 
OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.

*/
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.InetAddress;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.security.interfaces.RSAKey;
import java.util.Calendar;
import java.util.List;
import java.util.StringTokenizer;

import org.nhindirect.common.rest.exceptions.ServiceException;
import org.nhindirect.config.model.DNSRecord;
import org.nhindirect.config.store.util.DNSRecordUtils;
import org.springframework.web.multipart.commons.CommonsMultipartFile;
import org.xbill.DNS.AAAARecord;
import org.xbill.DNS.CERTRecord;
import org.xbill.DNS.DClass;
import org.xbill.DNS.NSRecord;
import org.xbill.DNS.Name;
import org.xbill.DNS.SRVRecord;
import org.xbill.DNS.CNAMERecord;
import org.xbill.DNS.Section;

/**
 * Backing form for the "Manage DNS Entries" page.
 * @author ppyette
 *
 */
public class DNSEntryForm
{
	private String admin = "";
	private long expire = 0L;
	private long minimum = 0L;
	private long refresh = 0L;
	private long retry = 0L;
	private long serial = 0L;
	
	private String thumbprint;
	private String trusteddomainoruser;    
    private Long id         = 0L;
    
    private String name     = "";
    private String type     = "";
    private String dest     = "";
    private String service  = null;
    private String protocol = null;
    private String domain   = null;
    
    private X509Certificate certificate = null;
    private byte[] certificateData;
    private CommonsMultipartFile fileData;
    
    private long   ttl      = 86400;
    private int    weight   = 0;
    private int    priority = 0;
    private int    port     = 0;
    
    private Calendar createTime;
    
    private List<String> remove;
    
    public static org.nhindirect.config.store.DNSRecord modelToEntityRecord(DNSRecord modelRec)
    {
		final org.nhindirect.config.store.DNSRecord retVal = new org.nhindirect.config.store.DNSRecord();
		retVal.setData(modelRec.getData());
		retVal.setDclass(modelRec.getDclass());
		retVal.setName(modelRec.getName());
		retVal.setTtl(modelRec.getTtl());
		retVal.setType(modelRec.getType());
		
		return retVal;
    }
    
    public static DNSRecord entityToModelRecord(org.nhindirect.config.store.DNSRecord entityRec)
    {
		final DNSRecord retVal = new DNSRecord();
		retVal.setData(entityRec.getData());
		retVal.setDclass(entityRec.getDclass());
		retVal.setName(entityRec.getName());
		retVal.setTtl(entityRec.getTtl());
		retVal.setType(entityRec.getType());
		
		return retVal;
    }
    
    public static DNSRecord xbillToModelRecord(org.xbill.DNS.Record rec) throws IOException
    {			
		return entityToModelRecord(DNSRecordUtils.fromWire(rec.toWireCanonical()));
    }
    
	/**
	 * Creates a DNS A4 type record.
	 * @param name The record name.  Generally a fully qualified domain name such as host.example.com.
	 * @param ttl The time to live in seconds.
	 * @param ip The ip4 address that the name will resolve.
	 * @return A DNSRecord representing an A type record.
	 * @throws ConfigurationStoreException
	 */
	public static DNSRecord createA4Record(String name, long ttl, String ip) throws ServiceException
	{
		if (!name.endsWith("."))
			name = name + ".";
		
		try
		{
			final AAAARecord rec = new AAAARecord(Name.fromString(name), DClass.IN, ttl, InetAddress.getByName(ip));
			
			return xbillToModelRecord(rec);
		}
		catch (Exception e)
		{
			throw new ServiceException("Failed to create DNS A4 record: " + e.getMessage(), e);
		}
	}
    
	/**
	 * Creates a DNS NS type record.
	 * @param name The record name.  Generally a fully qualified domain name such as host.example.com.
	 * @param ttl The time to live in seconds.
	 * @param ip The ip4 address that the name will resolve.
	 * @return A DNSRecord representing an A type record.
	 * @throws ConfigurationStoreException
	 */
	public static DNSRecord createNSRecord(String name, long ttl, String target) throws ServiceException
	{
		if (!name.endsWith("."))
			name = name + ".";
		
		if (!target.endsWith("."))
			target = target + ".";

		try
		{
			final NSRecord rec = new NSRecord(Name.fromString(name), DClass.IN, ttl, Name.fromString(target));
			
			return xbillToModelRecord(rec);
		}
		catch (Exception e)
		{
			throw new ServiceException("Failed to create DNS NS record: " + e.getMessage(), e);
		}
	}
    
	/**
	 * Creates a DNS CNAME type record.
	 * @param name The record name.  Generally a fully qualified domain name such as host.example.com.
	 * @param ttl The time to live in seconds.
	 * @param ip The ip4 address that the name will resolve.
	 * @return A DNSRecord representing an A type record.
	 * @throws ConfigurationStoreException
	 */
	public static DNSRecord createCNAMERecord(String name, long ttl, String dest) throws ServiceException
	{
		if (!name.endsWith("."))
			name = name + ".";
		
		if (!dest.endsWith("."))
			dest = dest + ".";

		try
		{
			final CNAMERecord rec = new CNAMERecord(Name.fromString(name), DClass.IN, ttl, Name.fromString(dest));
			
			return xbillToModelRecord(rec);
		}
		catch (Exception e)
		{
			throw new ServiceException("Failed to create DNS CNAME record: " + e.getMessage(), e);
		}
	}

	/**
	 * Creates a DNS Cert type record.
	 * @param name The record name.  Generally a fully qualified domain name such as host.example.com.
	 * @param ttl The time to live in seconds.
	 * @param ip The ip4 address that the name will resolve.
	 * @return A DNSRecord representing an A type record.
	 * @throws ConfigurationStoreException
	 */
	public static DNSRecord createCertRecord(String name, long ttl, int certtype, int keytag, int alg, X509Certificate cert) throws ServiceException
	{
		if (!name.endsWith("."))
			name = name + ".";
		
		try
		{
			int keyTag = 0;
			if (cert.getPublicKey() instanceof RSAKey)
			{
				final RSAKey key = (RSAKey)cert.getPublicKey();
				byte[] modulus = key.getModulus().toByteArray();
				
				keyTag = (modulus[modulus.length - 2] << 8) & 0xFF00;
				
				keyTag |= modulus[modulus.length - 1] & 0xFF;				
			}
			

			final CERTRecord rec = new CERTRecord(Name.fromString(name), DClass.IN, ttl, CERTRecord.PKIX, keyTag, 5, cert.getEncoded());
			
			return xbillToModelRecord(rec);
		}
		catch (Exception e)
		{
			throw new ServiceException("Failed to create DNS CERT record: " + e.getMessage(), e);
		}
	}
	
	public static DNSRecord createCertRecord(DNSEntryForm form){
        DNSRecord result = new DNSRecord();

        if(form.getName() == null || form.getCertificate() == null ) return result;
    	
        final org.nhindirect.config.store.DNSRecord tempRec = DNSRecordUtils.createX509CERTRecord(form.getName(), form.getTtl(), form.getCertificate());

        return entityToModelRecord(tempRec);
	}
	
    @SuppressWarnings("incomplete-switch")
	public static DNSRecord toDNSRecord(DNSEntryForm form) throws ServiceException
    {
        DNSRecord result = new DNSRecord();
        
        if(form == null) return result;
        
        switch (DNSType.get(form.getType()))
        {
            case A   : 
            {
                result = entityToModelRecord(DNSRecordUtils.createARecord(form.getName(), form.getTtl(), form.getDest())); 
                break;
            }
            case AAAA :
            {
                result = createA4Record(form.getName(), form.getTtl(), form.getDest()); 
                break;
            	
            }
            case CNAME :
            {
                result = createCNAMERecord(form.getName(), form.getTtl(), form.getDest()); 
                break;
            	
            }
            case MX  : 
            {
                result = entityToModelRecord(DNSRecordUtils.createMXRecord(form.getName(), form.getDest(), form.getTtl(), form.getPriority()));
                break;
            }
            case CERT :
            {
            	if(form.getName() == null || form.getCertificate() == null ) break;
            	
                result = entityToModelRecord(DNSRecordUtils.createX509CERTRecord(form.getName(), form.getTtl(), form.getCertificate()));
                
                break;
            }
            case SRV :
            {
            	if(form.getDest() == null || form.getName() == null || form.getCertificate() == null ) break;
            	
                result = entityToModelRecord(DNSRecordUtils.createSRVRecord(form.getName(), 
                                                        form.getDest(), 
                                                        form.getTtl(), 
                                                        form.getPort(),
                                                        form.getPriority(),
                                                        form.getWeight()));
            }
        }
        
        return result;
    }
    
    public DNSEntryForm()
    {     
    }
    
    public DNSEntryForm(DNSRecord r)
    {
        setName(r.getName());
        setType(DNSType.get(r.getType()).toString());
        setTtl(r.getTtl());
        setCreateTime(r.getCreateTime());
        switch (DNSType.get(r.getType()))
        {
            case A :
            case AAAA :
            case CNAME :
            case MX :
            {
                setDest(new String(r.getData()));
                break;
            }
            case CERT :
            {
                try
                {
                    InputStream str = new ByteArrayInputStream(r.getData());
                    
                    setCertificate((X509Certificate)CertificateFactory.getInstance("X.509").generateCertificate(str));
                    
                    str.close();
                }
                catch (CertificateException e)
                {
                    //TODO Let someone know about this
                }
                catch (IOException e)
                {
                    //TODO Let someone know about this
                }
                break;
            }
            case SRV :
            {
                try
                {
                    final SRVRecord record = (SRVRecord)SRVRecord.fromWire(DNSRecordUtils.toWire(modelToEntityRecord(r)), Section.ANSWER); 
                    setPort(record.getPort());
                    setPriority(record.getPriority());
                    setWeight(record.getWeight());
                    setDest(record.getTarget().toString());
                    final StringTokenizer stok = new StringTokenizer(record.getName().toString(), ".");
                    if (stok.countTokens() > 2)
                    {
                        setService(stok.nextToken());
                        setProtocol(stok.nextToken());
                        StringBuffer dName = new StringBuffer(stok.nextToken());
                        while (stok.hasMoreTokens())
                        {
                            dName.append(".").append(stok.nextToken());
                        }
                        setDomain(dName.toString());
                    }      
                }
                catch (IOException e)
                {
                    //TODO Let someone know about this
                }
            }
            default : {}
        }

    }

    public Long getId()
    {
        return id;
    }

    public void setId(Long id)
    {
        this.id = id;
    }

    public String getName()
    {
        if ((name.length() == 0) &&
            (getType().equals(DNSType.SRV.toString())))
        {
            name = "_" + getService() + "._" + getProtocol() + "." + getDomain();
        }
        return name;
    }

    public void setName(String name)
    {
        this.name = name;
    }

    public String getType()
    {
        return type;
    }
    
    public DNSType getDNSType()
    {
        return DNSType.get(getType());
    }

    public void setType(String type)
    {
        DNSType dType = DNSType.get(type);
        if (dType != null)
        {
            this.type = type;
        }        
    }

    public String getDest()
    {
        return dest;
    }

    public void setDest(String dest)
    {
        this.dest = dest;
    }

    public String getService()
    {
        return service;
    }

    public void setService(String service)
    {
        this.service = service;
    }

    public String getProtocol()
    {
        return protocol;
    }

    public void setProtocol(String protocol)
    {
        this.protocol = protocol;
    }

    public String getDomain()
    {
        return domain;
    }

    public void setDomain(String name)
    {
        this.domain = name;
    }

    public long getTtl()
    {
        return ttl;
    }

    public void setTtl(long ttl)
    {
        this.ttl = ttl;
    }

    public int getWeight()
    {
        return weight;
    }

    public void setWeight(int weight)
    {
        this.weight = weight;
    }

    public int getPriority()
    {
        return priority;
    }

    public void setPriority(int priority)
    {
        this.priority = priority;
    }

    public int getPort()
    {
        return port;
    }

    public void setPort(int port)
    {
        this.port = port;
    }

    public Calendar getCreateTime()
    {
        return createTime;
    }

    public void setCreateTime(Calendar createTime)
    {
        this.createTime = createTime;
    }
    
    public X509Certificate getCertificate()
    {
        return certificate;
    }

    public void setCertificate(X509Certificate certificate)
    {
        this.certificate = certificate;
    }

    public void setRemove(List<String> remove) 
    {
        this.remove = remove;
    }
    
    public List<String> getRemove() 
    {
        return remove;
    }

	public void setCertificateData(byte[] certificateData) {
		this.certificateData = certificateData;
	}

	public byte[] getCertificateData() {
		return certificateData;
	}

	public void setFileData(CommonsMultipartFile fileData) {
		this.fileData = fileData;
	}

	public CommonsMultipartFile getFileData() {
		return fileData;
	}

	public void setTrusteddomainoruser(String trusteddomainoruser) {
		this.trusteddomainoruser = trusteddomainoruser;
	}

	public String getTrusteddomainoruser() {
		return trusteddomainoruser;
	}

	public void setThumbprint(String thumbprint) {
		this.thumbprint = thumbprint;
	}

	public String getThumbprint() {
		return thumbprint;
	}

	public void setAdmin(String admin) {
		this.admin = admin;
	}

	public String getAdmin() {
		return admin;
	}

	public void setExpire(long expire) {
		this.expire = expire;
	}

	public long getExpire() {
		return expire;
	}

	public void setMinimum(long minimum) {
		this.minimum = minimum;
	}

	public long getMinimum() {
		return minimum;
	}

	public void setRefresh(long refresh) {
		this.refresh = refresh;
	}

	public long getRefresh() {
		return refresh;
	}

	public void setRetry(long retry) {
		this.retry = retry;
	}

	public long getRetry() {
		return retry;
	}

	public void setSerial(long serial) {
		this.serial = serial;
	}

	public long getSerial() {
		return serial;
	}
}
