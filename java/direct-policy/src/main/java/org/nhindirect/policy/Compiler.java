package org.nhindirect.policy;

import java.security.cert.X509Certificate;
import java.util.Collection;
import java.util.Vector;

public interface Compiler 
{
	public Vector<Opcode> compile(X509Certificate cert, PolicyExpression expression) throws PolicyProcessException;
	
	public void setReportModeEnabled(boolean reportMode);
	
	public boolean isReportModeEnabled();
	
	public Collection<String> getCompilationReport();
}
