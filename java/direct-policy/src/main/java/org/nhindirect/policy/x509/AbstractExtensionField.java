package org.nhindirect.policy.x509;

import java.security.cert.X509Certificate;
import java.util.Set;

import org.bouncycastle.asn1.DERObject;
import org.nhindirect.policy.PolicyProcessException;

public abstract class AbstractExtensionField<P> extends AbstractTBSField<P> implements ExtensionField<P>
{
	static final long serialVersionUID = 5312652953460972702L;
	
	public AbstractExtensionField(boolean required)
	{
		super(required);
	}
	
	@Override
	public TBSFieldName getFieldName() 
	{
		return TBSFieldName.EXTENSIONS;
	}
	
	@Override
	public boolean isCritical() 
	{
		if (this.certificate == null)
			throw new IllegalStateException("Certificate value is null");
		
		final Set<String> criticalOIDs = certificate.getCriticalExtensionOIDs();
		
		return criticalOIDs.contains(getExtentionIdentifier().getId());
	}
	
    protected DERObject getExtensionValue(X509Certificate cert)
            throws PolicyProcessException
    {
    	final String oid = getExtentionIdentifier().getId();
    	
        byte[]  bytes = cert.getExtensionValue(oid);
        if (bytes == null)
        {
            return null;
        }

        return getObject(bytes);
    }
}
