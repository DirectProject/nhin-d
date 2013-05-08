package org.nhindirect.policy.x509;


public interface ExtensionField<P> extends TBSField<P>
{
	public ExtentionIdentifier getExtentionIdentifier();
	
	public boolean isCritical();
}
