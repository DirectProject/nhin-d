/* 
 Copyright (c) 2013, Direct Project
 All rights reserved.

 Authors:
    Joe Shook      jshook@kryptiq.com
  
Redistribution and use in source and binary forms, with or without modification, are permitted provided that the following conditions are met:

Redistributions of source code must retain the above copyright notice, this list of conditions and the following disclaimer.
Redistributions in binary form must reproduce the above copyright notice, this list of conditions and the following disclaimer in the documentation and/or other materials provided with the distribution.
Neither the name of The Direct Project (directproject.org) nor the names of its contributors may be used to endorse or promote products derived from this software without specific prior written permission.
THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 
*/

using System;
using System.Collections.Generic;
using System.Collections.ObjectModel;
using System.Security.Cryptography.X509Certificates;
using System.Threading;
using Health.Direct.Common.Policies;
using Health.Direct.Policy.X509;

namespace Health.Direct.Policy.Machine
{
    public class StackMachineCompiler : ICompiler
    {
        
        readonly ThreadLocal<IList<String>> m_compilerReport;

        public StackMachineCompiler()
        {
            ReportModeEnabled = false;
            m_compilerReport = new ThreadLocal<IList<String>>();
        }

        public bool ReportModeEnabled { get; set; }

        public IList<IOpCode> Compile(X509Certificate2 cert, IPolicyExpression expression)
        {
            IList<String> report = m_compilerReport.Value;
            if (report != null)
            {
                report.Clear();
            }

            IList<IOpCode> entries = new List<IOpCode>();
            entries.Add(Compile(entries, cert, expression));
            return entries;
        }


        protected StackMachineEntry<OperationPolicyExpression> Compile(IList<IOpCode> entries, X509Certificate2 cert, IPolicyExpression expression)
        {
            switch (expression.GetExpressionType())
            {
                //case PolicyExpressionType.LITERAL:
                //    return new StackMachineEntry(((LiteralPolicyExpression < ?  >)
                //    expression).
                //    GetPolicyValue())
                //    ;

                //case PolicyExpressionType.REFERENCE:
                //{
                //    object refExpression = GetRefExpressionType(expression);

                //    //IReferencePolicyExpression<X509Certificate2, object> refExpression = null;
                //    EvaluateReferenceExpression(cert, refExpression);

                //    return new ReferenceMachineEntry<IReferencePolicyExpression<X509Certificate2,T>>(expression);
                //}

                case PolicyExpressionType.OPERATION:
                {
                    OperationPolicyExpression opExpression = (OperationPolicyExpression) expression;
                    foreach (IPolicyExpression policyExpression in opExpression.GetOperands())
                    {
                        if (policyExpression is IReferencePolicyExpression<X509Certificate2, String>)
                        {
                            var refExpression = policyExpression as IReferencePolicyExpression<X509Certificate2, String>;
                            EvaluateReferenceExpression(cert, refExpression);
                            entries.Add(Compile(refExpression));
                        }
                        else if (policyExpression is IReferencePolicyExpression<X509Certificate2, Int32>)
                        {
                            var refExpression = policyExpression as IReferencePolicyExpression<X509Certificate2, Int32>;
                            EvaluateReferenceExpression(cert, refExpression);
                            entries.Add(Compile(refExpression));
                        }
                        else if (policyExpression is IReferencePolicyExpression<X509Certificate2, Int64>)
                        {
                            var refExpression = policyExpression as IReferencePolicyExpression<X509Certificate2, Int64>;
                            EvaluateReferenceExpression(cert, refExpression);
                            entries.Add(Compile(refExpression));
                        }
                        else if (policyExpression is IReferencePolicyExpression<X509Certificate2, IList<String>>)
                        {
                            var refExpression = policyExpression as IReferencePolicyExpression<X509Certificate2, IList<String>>;
                            EvaluateReferenceExpression(cert, refExpression);
                            entries.Add(Compile(refExpression));
                        }
                        else if (policyExpression is IReferencePolicyExpression<X509Certificate2, Boolean>)
                        {
                            var refExpression = policyExpression as IReferencePolicyExpression<X509Certificate2, Boolean>;
                            EvaluateReferenceExpression(cert, refExpression);
                            entries.Add(Compile(refExpression));
                        }
                        else if (policyExpression is ILiteralPolicyExpression<String>)
                        {
                            entries.Add(Compile(policyExpression as ILiteralPolicyExpression<String>));
                        }
                        else
                        {
                            entries.Add(Compile(entries, cert, policyExpression));
                        }
                    }
                    return new StackMachineEntry<OperationPolicyExpression>(opExpression.GetPolicyOperator());
                }

                default:
                    return null;
            }
        }

        private StackMachineEntry<T> Compile<T>(ILiteralPolicyExpression<T> expression)
        {
            return new StackMachineEntry<T>(EntryType.Value, expression.GetPolicyValue().GetPolicyValue());
        }

        private StackMachineEntry<T> Compile<T>(IReferencePolicyExpression<X509Certificate2, T> expression)
        {
            return new StackMachineEntry<T>(EntryType.Value, expression.GetPolicyValue().GetPolicyValue());
        }
        
        protected IPolicyValue<T> EvaluateReferenceExpression<R, T>(X509Certificate2 cert, IReferencePolicyExpression<R, T> expression)
        {
            switch (expression.GetPolicyExpressionReferenceType())
            {
                case PolicyExpressionReferenceType.Struct:
                case PolicyExpressionReferenceType.Certificate:
                    {
                        return EvaluateX509Field(cert, (X509Field<T>)expression);
                    }
                default:
                    return null;
            }
        }

        protected IPolicyValue<T> EvaluateX509Field<T>(X509Certificate2 cert, X509Field<T> expression)
        {
            try
            {
                expression.InjectReferenceValue(cert);
                return expression.GetPolicyValue();
            }
            catch (PolicyRequiredException e)
            {
                // add this to the report and re-evaluate without the required flag
                if (ReportModeEnabled)
                {
                    AddErrorToReport(e);
                    expression.SetRequired(false);
                    expression.InjectReferenceValue(cert);
                    return expression.GetPolicyValue();
                }
                // re-throw
                throw;
            }
        }

        protected void AddErrorToReport(PolicyProcessException e)
        {
            IList<String> report = m_compilerReport.Value;
            if (report == null)
            {
                report = new List<String>();
                m_compilerReport.Value = report;
            }
            report.Add(e.Message);
        }
        
        public IList<string> CompiliationReport
        {
            get
            {
                IList<String> report = m_compilerReport.Value;
                if (report != null)
                {
                    return new ReadOnlyCollection<string>(report);
                }
                return new ReadOnlyCollection<string>(new List<string>());
            }
        }
    }
}
