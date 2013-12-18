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
using System.Linq.Expressions;
using System.Text.RegularExpressions;
using Health.Direct.Policy.OpCode;
using Health.Direct.Policy.Operators;

namespace Health.Direct.Policy
{

    public class PolicyOperator<T> : PolicyOperator
    {

        
        public static LogicalAnd<T> LOGICAL_AND;
        public static LogicalOr<T> LOGICAL_OR;
        public static BitwiseAnd<T> BITWISE_AND;
        public static BitwiseOr<T> BITWISE_OR;
        public static Size<T> SIZE;
        public static Not<T> LOGICAL_NOT;
        public static UriValid<T> URI_VALIDATE;
               
        

        static PolicyOperator()
        {
            var logicalAnd = new Logical_And();
            LOGICAL_AND = new LogicalAnd<T>(logicalAnd, LogicalAndDelegate());
            TokenOperatorMap[logicalAnd.Token] = LOGICAL_AND;

            var logicalOr = new LogicalOr();
            LOGICAL_OR = new LogicalOr<T>(logicalOr, LogicalOrDelegate());
            TokenOperatorMap[logicalOr.Token] = LOGICAL_OR;

            var bitwiseAnd = new Bitwise_And();
            BITWISE_AND = new BitwiseAnd<T>(bitwiseAnd, BitwiseAndDelegate());
            TokenOperatorMap[bitwiseAnd.Token] = BITWISE_AND;

            var bitwiseOr = new Bitwise_Or();
            BITWISE_OR = new BitwiseOr<T>(bitwiseOr, BitwiseOrDelegate());
            TokenOperatorMap[bitwiseOr.Token] = BITWISE_OR;

            var size = new Size();
            SIZE = new Size<T>(size, SizeDelegate());
            TokenOperatorMap[size.Token] = SIZE;

            var logicalNot = new Logical_Not();
            LOGICAL_NOT = new Not<T>(logicalNot, LogicalNotDelegate());
            TokenOperatorMap[logicalNot.Token] = LOGICAL_NOT;

            var uriValid = new Uri_Valid();
            URI_VALIDATE = new UriValid<T>(uriValid);
            TokenOperatorMap[uriValid.Token] = URI_VALIDATE;
        }

        private static ParameterExpression Left
        {
            get
            {
                var left = Expression.Parameter(typeof(T), "left");
                return left;
            }
        }
        private static ParameterExpression Right
        {
            get
            {
                var left = Expression.Parameter(typeof (T), "left");
                return left;
            }
        }

        private static Func<T, T, T> LogicalAndDelegate()
        {
            return ExpressionTreeUtil.CreateDelegate<T, T, T>(Expression.AndAlso, Left, Right);
        }
        private static Func<T, T, T> LogicalOrDelegate()
        {
            return ExpressionTreeUtil.CreateDelegate<T, T, T>(Expression.OrElse, Left, Right);
        }
        private static Func<T, T, T> BitwiseAndDelegate()
        {
            return ExpressionTreeUtil.CreateDelegate<T, T, T>(Expression.And, Left, Right);
        }
        private static Func<T, T, T> BitwiseOrDelegate()
        {
            return ExpressionTreeUtil.CreateDelegate<T, T, T>(Expression.Or, Left, Right);
        }
        private static Func<T, int> SizeDelegate()
        {
            return ExpressionTreeUtil.CreateDelegate<T, int>(Expression.ArrayLength, Left);
        }
        private static Func<T, bool> LogicalNotDelegate()
        {
            return ExpressionTreeUtil.CreateDelegate<T, bool>(Expression.Not, Left);
        }
        
    }

    public class PolicyOperator<TValue, TResult> : PolicyOperator
    {
        public static Equals<TValue, TResult> EQUALS;
        public static NotEquals<TValue, TResult> NOT_EQUALS;
        public static Greater<TValue, TResult> GREATER;
        public static Less<TValue, TResult> LESS;
        public static RegularExpression<TValue, TResult> REG_EX;

        

        //TODO: Rethink this, don't like the conversion...
        /// <summary>
        /// Performs a regular expression match on a string.  This operation returns true if the regular expression is found in 
	    /// the given string.
        /// </summary>
        /// <param name="pattern">Regular expression pattern</param>
        /// <param name="value">Source string to search</param>
        /// <returns></returns>
        public static TResult RegExExists(TValue pattern, TValue value) 
        {
            string v = value as string;
            string p = pattern as string;

            Regex regex = new Regex(p);
            var match = regex.Match(v);
            return (TResult)Convert.ChangeType(match.Success, typeof(TResult));
        }


        static PolicyOperator()
        {
            var equals = new Equals();
            EQUALS = new Equals<TValue, TResult>(equals, EqualDelegate());
            TokenOperatorMap[equals.Token] = EQUALS;

            var notEquals = new NotEquals();
            NOT_EQUALS = new NotEquals<TValue, TResult>(new NotEquals(), NotEqualDelegate());
            TokenOperatorMap[notEquals.Token] = NOT_EQUALS;

            var greater = new Greater();
            GREATER = new Greater<TValue, TResult>(greater, GreaterThanDelegate());
            TokenOperatorMap[greater.Token] = GREATER;

            var less = new Less();
            LESS = new Less<TValue, TResult>(less, LessThanDelegate());
            TokenOperatorMap[less.Token] = LESS;

            var regex = new Reg_Ex();
            REG_EX = new RegularExpression<TValue, TResult>(regex, RegExExists);
            TokenOperatorMap[regex.Token] = REG_EX;
        }

        private static ParameterExpression Left
        {
            get
            {
                var left = Expression.Parameter(typeof(TValue), "left");
                return left;
            }
        }
        private static ParameterExpression Right
        {
            get
            {
                var left = Expression.Parameter(typeof(TValue), "left");
                return left;
            }
        }
        private static Func<TValue, TValue, TResult> EqualDelegate()
        {
            return ExpressionTreeUtil.CreateDelegate<TValue, TValue, TResult>(Expression.Equal, Left, Right);
        }
        private static Func<TValue, TValue, TResult> NotEqualDelegate()
        {
            return ExpressionTreeUtil.CreateDelegate<TValue, TValue, TResult>(Expression.NotEqual, Left, Right);
        }
        private static Func<TValue, TValue, TResult> GreaterThanDelegate()
        {
            return ExpressionTreeUtil.CreateDelegate<TValue, TValue, TResult>(Expression.GreaterThan, Left, Right);
        }
        private static Func<TValue, TValue, TResult> LessThanDelegate()
        {
            return ExpressionTreeUtil.CreateDelegate<TValue, TValue, TResult>(Expression.LessThan, Left, Right);
        }
        private static Func<TValue, TResult> RegExExistsDelegate()
        {
            return ExpressionTreeUtil.CreateDelegate<TValue, TResult>(Expression.LessThan, Left, Right);
        }
    }

    public class PolicyOperator<TValue, TList, TResult> : PolicyOperator where TList : IEnumerable<TValue> 
    {

        public static Contains<TValue, TList, TResult> CONTAINS;
        public static NotContains<TValue, TList, TResult> NOT_CONTAINS;
        public static ContainsRegEx<TValue, TList, TResult>  CONTAINS_REG_EX;
        public static Empty<TList, TResult> EMPTY;
        public static NotEmpty<TList, TResult> NOT_EMPTY;

        //TODO: Rethink this, don't like the conversion...
        /// <summary>
        /// Performs a regular expression match on a string.  This operation returns true if the regular expression is found in 
        /// the given string.
        /// </summary>
        /// <param name="pattern">Regular expression pattern</param>
        /// <param name="value">Source string to search</param>
        /// <returns></returns>
        public static TResult RegExExists(TValue pattern, TList value)
        {
            bool success = false;
            foreach (var item in value)
            {
                string v = item as string;
                string p = pattern as string;

                Regex regex = new Regex(p);
                var match = regex.Match(v);
                success = match.Success;
                if (success) break;
            }
            return (TResult)Convert.ChangeType(success, typeof(TResult));
        }

        static PolicyOperator()
        {
            var left = Expression.Parameter(typeof (TValue), "itemType");
            var right = Expression.Parameter(typeof(TValue), "item");
            var itemList = Expression.Parameter(typeof(IEnumerable<TValue>), "itemList");

            // list.Any(a => a = "joe")
            var body = Expression.Equal(left, right);
            var lambda = Expression.Lambda(body, left);
            
            Expression anyCall =
                Expression.Call(typeof(Enumerable), "Any", new Type[] { typeof(TValue) }
                                , itemList
                                , lambda);
            var contains = new Contains();
            CONTAINS = new Contains<TValue, TList, TResult>(contains
                , Expression.Lambda<Func<TValue, TList, TResult>>(anyCall, right, itemList).Compile());
            TokenOperatorMap[contains.Token] = CONTAINS;
            
            Expression notAnyCall = Expression.Not(anyCall);

            var notContains = new NotContains();
            NOT_CONTAINS = new NotContains<TValue, TList, TResult>(notContains
                , Expression.Lambda<Func<TValue, TList, TResult>>(notAnyCall, right, itemList).Compile());
            TokenOperatorMap[notContains.Token] = NOT_CONTAINS;

            var containsRegEx = new ContainsRegEx();
            CONTAINS_REG_EX = new ContainsRegEx<TValue, TList, TResult>(containsRegEx, RegExExists);
            TokenOperatorMap[containsRegEx.Token] = CONTAINS_REG_EX;

            Expression countExpression =
               Expression.Call(typeof(Enumerable), "Count", new Type[] { typeof(TValue) }
                               , itemList);

            Expression emptyExpression = Expression.Equal(Expression.Constant(0), countExpression);

            var empty = new Empty();
            EMPTY = new Empty<TList, TResult>(empty
                  , Expression.Lambda<Func<TList, TResult>>(emptyExpression, itemList).Compile());
            TokenOperatorMap[empty.Token] = EMPTY;

            Expression noEmptyExpression = Expression.Not(emptyExpression);
            var notEmpty = new NotEmpty();
            NOT_EMPTY = new NotEmpty<TList, TResult>(notEmpty
                 , Expression.Lambda<Func<TList, TResult>>(noEmptyExpression, itemList).Compile());
            TokenOperatorMap[notEmpty.Token] = NOT_EMPTY;
        }
    }

    public class PolicyOperator
    {
        
        protected static readonly Dictionary<string, OperatorBase> TokenOperatorMap;

        static PolicyOperator()
        {
            Console.WriteLine("hello:: PolicyOperator");
            TokenOperatorMap = new Dictionary<string, OperatorBase>();

        }

        public static Equals<T, T> Equals<T>()
        {
            return PolicyOperator<T, T>.EQUALS;
        }

        public static T BitwiseOr<T>(T value1, T value2)
        {
            return PolicyOperator<T>.BITWISE_OR.Execute(value1, value2);
        }

        public static BitwiseOr<T> BitwiseOr<T>()
        {
            return PolicyOperator<T>.BITWISE_OR;
        }

        
        /// <summary>
	    /// Gets the policy operator associated with a specific token string.
	    /// @param token The token used to look up the PolicyOperator.
	    /// @return The PolicyOperator associated with the token.  If the token does not represent a known operator, then null is returned,.
        /// </summary>
        public static OperatorBase FromToken(String token)
        {
            OperatorBase operatorBase;
            if (TokenOperatorMap.TryGetValue(token, out operatorBase))
            {
                return operatorBase;
            }
            return null;
        }
    }
}
