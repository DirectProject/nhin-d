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
using System.Linq;
using Health.Direct.Policy.Operators;

namespace Health.Direct.Policy.Machine
{
    public class StackMachine : IExecutionEngine
    {
        Stack<object> machineStack = new Stack<object>();


        /// <inheritdoc />
        public bool Evaluate(IList<IOpCode> opCodes)
        {
            bool retVal = false;
            foreach (var opCode in opCodes)
            {
                switch (opCode.EntryType)
                {
                    case EntryType.Value:
                        machineStack.Push(opCode.PolicyValue);
                        break;
                    case EntryType.Operator:
                        Delegate executor = null;
                        Object[] args = null;
                        if (opCode.PolicyOperator is BinaryOperator)
                        {
                            if (machineStack.Count < 2)
                            {
                                throw new InvalidOperationException("Stack machine must have at least two pushed operands for "
                                    + opCode.PolicyOperator.GetOperatorText()
                                    + " operator");
                            }

                            OperatorBase policyOperator = PolicyOperator.TokenOperatorMap.Single(t => t.Key == opCode.PolicyOperator.GetHashCode()).Value;
                            executor = policyOperator.ExecuteRef;
                            var rightArg = machineStack.Pop();
                            var leftArg = machineStack.Pop();
                            args = new[] { leftArg, rightArg };
                        }

                        if (opCode.PolicyOperator is UnaryOperator)
                        {
                            if (machineStack.Count < 1)
                            {
                                throw new InvalidOperationException("Stack machine must have at least one pushed operand for  "
                                    + opCode.PolicyOperator.GetOperatorText()
                                    + " operator");
                            }

                            OperatorBase policyOperator = PolicyOperator.TokenOperatorMap.Single(t => t.Key == opCode.PolicyOperator.GetHashCode()).Value;
                            executor = policyOperator.ExecuteRef;
                            args = new[]{machineStack.Pop()};
                        }
                        if (executor != null)
                        {
                            object result = executor.DynamicInvoke(args);
                            machineStack.Push(result);
                        }
                        break;
                }
            }

            if (! machineStack.Any() ||  machineStack.Count > 1)
			throw new InvalidOperationException("Stack machine is either empty or has remaining parameters to be processed." +
					"\r\n\tFinal stack size: " + machineStack.Count);

            object finalValue = machineStack.Pop();
            try
            {
                retVal = (bool)finalValue;
            }
            catch (InvalidCastException e)
            {
                throw new InvalidCastException("Final machine value must be a boolean litteral" +
                        "\r\n\tFinal value type: " + finalValue.GetType()
                        + "\r\n\tFinal value value:" + finalValue, e);
            }

            return retVal;
        }

    }
}
