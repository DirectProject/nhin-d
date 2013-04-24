package org.nhindirect.policy.x509;

public abstract class AbstractTBSField<P> extends AbstractX509Field<P> implements TBSField<P>
{	
	static final long serialVersionUID = -5732010856760538062L;	
	
	protected final boolean required;
	
	protected AbstractTBSField(boolean required)
	{
		this.required = required;
	}
	
	@Override
	public boolean isRequired()
	{
		return required;
	}
	
	@Override
	public X509FieldType getX509FieldType()
	{
		return X509FieldType.TBS;
	}
}
