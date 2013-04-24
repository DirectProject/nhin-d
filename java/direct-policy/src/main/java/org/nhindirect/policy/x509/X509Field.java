package org.nhindirect.policy.x509;

import java.security.cert.X509Certificate;

import org.nhindirect.policy.ReferencePolicyExpression;


public interface X509Field<P> extends ReferencePolicyExpression<X509Certificate, P>
{
	public X509FieldType getX509FieldType();
}
