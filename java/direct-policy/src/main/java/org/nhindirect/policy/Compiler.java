package org.nhindirect.policy;

import java.security.cert.X509Certificate;
import java.util.Vector;

public interface Compiler 
{
	public Vector<Opcode> compile(X509Certificate cert, PolicyExpression expression) throws PolicyProcessException;
}
