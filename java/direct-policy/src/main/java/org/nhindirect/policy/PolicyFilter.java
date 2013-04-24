package org.nhindirect.policy;

import java.security.cert.X509Certificate;

public interface PolicyFilter 
{
	boolean isCompliant(X509Certificate cert, PolicyExpression expression) throws PolicyProcessException;
}
