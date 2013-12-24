/* 
Copyright (c) 2010, NHIN Direct Project
All rights reserved.

Authors:
   Greg Meyer      gm2552@cerner.com
 
Redistribution and use in source and binary forms, with or without modification, are permitted provided that the following conditions are met:

Redistributions of source code must retain the above copyright notice, this list of conditions and the following disclaimer.
Redistributions in binary form must reproduce the above copyright notice, this list of conditions and the following disclaimer 
in the documentation and/or other materials provided with the distribution.  Neither the name of the The NHIN Direct Project (nhindirect.org). 
nor the names of its contributors may be used to endorse or promote products derived from this software without specific prior written permission.
THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, 
THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS 
BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE 
GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, 
STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF 
THE POSSIBILITY OF SUCH DAMAGE.
*/

package org.nhindirect.policy;

import java.security.cert.X509Certificate;
import java.util.Collection;
import java.util.Vector;

/**
 * The compiler is the second step of the execution process after a lexicon has been parse into a {@link PolicyExpression} tree.  The compiler consumes
 * the parsed expressions and generates an ordered vector of {@link Opcode} objects that are specific to a particular {@link ExecutionEngine}. 
 * The resulting vector is then fed to the engine to evaluate the boolean result of the expression.
 * <p>
 * By default, the compile operation throws a {@link PolicyRequiredException} exception if a required attribute is missing in the provided certificate.  This 
 * halts the evaluation process immediately at the point the first missing required field is encountered.  However, there are use cases, such as a policy 
 * validation  tool, where a generating a complete list of missing fields may be useful.  In addition, it may desired to process the certificate through the 
 * execution engine even though it is known that required fields are missing.  Obviously this is not the most efficient operating mode when executing inside
 * of an environment where the only result of interest is the simple binary decision of policy compliance.  
 * <br>
 * To enable the ability to retrieve a list of all known missing fields (or other compilation errors), the compiler supports a report mode that can
 * be enabled by calling the {@link #setReportModeEnabled(boolean)} method.  When reporting mode is turned on, the compiler will no longer throw an exception
 * when a required attribute is missing.  After compilation is complete, a collection of all compilation issues can be retrieved by calling 
 * the {@link #getCompilationReport()} method.
 * 
 * @author Greg Meyer
 * @since 1.0
 */
public interface Compiler 
{
	/**
	 * Compiles the given certificate and a parsed {@link PolicyExpression} tree in a series of executables {@link Opcode} objects.
	 * <p>
	 * Implementations should be thread same meaning multiple expressions can be compiled concurrently with the same compiler.
	 * @param cert The certificate that will be evaluated for policy compliance.
	 * @param expression The parsed expression tree.
	 * @return A vector of {@link Opcode} objects that are fed into an {@link ExecutionEngine} for final evaluation of policy compliance.
	 * @throws PolicyProcessException Thrown if a required certificate attribute does not exist in the given certification.  However, this exception
	 * is suppressed if the compiler is placed into report model.
	 */
	public Vector<Opcode> compile(X509Certificate cert, PolicyExpression expression) throws PolicyProcessException;
	
	/**
	 * Sets or removes the compiler from report mode.  When in report mode, the compiler will not fail if required attributes are missing from the certificate.
	 * @param reportMode true if the compiler is to be set into report mode.  false otherwise
	 */
	public void setReportModeEnabled(boolean reportMode);
	
	/**
	 * Indicates whether or not the compiler is in report mode.
	 * @return true if the compiler is in report mode.  false otherwise
	 */
	public boolean isReportModeEnabled();
	
	/**
	 * Gets a collection of compilation issues.  This report can only be generated if the compiler is in report mode.
	 * <p>
	 * The collection is reset each time a compilation occurs.  For concurrent compilation, the generated report is specific to the thread that
	 * compilation process was executed on.
	 * @return A collection of compilation issues.  If no issues were found or the compiler was not in report mode, then an empty collection is returned.
	 */
	public Collection<String> getCompilationReport();
}
