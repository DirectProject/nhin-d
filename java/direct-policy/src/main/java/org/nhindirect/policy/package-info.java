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

/**
 * Interface definition and structures for the certificate policy engine.
 * <p>
 * The policy engine is more or less a boolean logic engine.  It processes a set of rules called a {@link PolicyExpression }
 * against a provided X509 certificate and determines if the certificate is in compliance with the policy.
 * <p>
 * The engine itself is structurally similar to a compiled programming language and runtime environment that the compiled code execute in.
 * Polices start as definition files written in a specific {@link PolicyLexicon}, are compiled to an intermediate state, converted into an 
 * {@link ExecutionEngine} specific set of {@link Opcode Opcodes}, and finally processed by the {@link ExecutionEngine}.
 * <p>
 * The engine is broken into four modules that can be consumed independently for the purpose of building tooling, but generally
 * the aggregate {@link PolicyFilter} interface is used to process certificates against a {@link PolicyExpression.}
 */
package org.nhindirect.policy;