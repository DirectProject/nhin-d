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
using System.Collections;
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
            TokenOperatorMap[LOGICAL_AND.GetHashCode()] = LOGICAL_AND;

            var logicalOr = new LogicalOr();
            LOGICAL_OR = new LogicalOr<T>(logicalOr, LogicalOrDelegate());
            TokenOperatorMap[LOGICAL_OR.GetHashCode()] = LOGICAL_OR;

            var bitwiseAnd = new Bitwise_And();
            BITWISE_AND = new BitwiseAnd<T>(bitwiseAnd, BitwiseAndDelegate());
            TokenOperatorMap[BITWISE_AND.GetHashCode()] = BITWISE_AND;

            var bitwiseOr = new Bitwise_Or();
            BITWISE_OR = new BitwiseOr<T>(bitwiseOr, BitwiseOrDelegate());
            TokenOperatorMap[BITWISE_OR.GetHashCode()] = BITWISE_OR;

            var size = new Size();
            SIZE = new Size<T>(size, SizeDelegate());
            TokenOperatorMap[SIZE.GetHashCode()] = SIZE;

            var logicalNot = new Logical_Not();
            LOGICAL_NOT = new Not<T>(logicalNot, LogicalNotDelegate());
            TokenOperatorMap[LOGICAL_NOT.GetHashCode()] = LOGICAL_NOT;

            var uriValid = new Uri_Valid();
            URI_VALIDATE = new UriValid<T>(uriValid);
            TokenOperatorMap[URI_VALIDATE.GetHashCode()] = URI_VALIDATE;
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
                var left = Expression.Parameter(typeof(T), "left");
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
        private static Func<T, Boolean> LogicalNotDelegate()
        {
            return ExpressionTreeUtil.CreateDelegate<T, Boolean>(Expression.Not, Left);
        }
    }

    public class PolicyOperator<TValue, TResult> : PolicyOperator
    {
        public static NotEquals<TValue, TResult> NOT_EQUALS;
        public static Greater<TValue, TResult> GREATER;
        public static Less<TValue, TResult> LESS;
        public static RegularExpression<TValue, TResult> REG_EX;
        public static Empty<TValue, TResult> EMPTY;
        public static NotEmpty<TValue, TResult> NOT_EMPTY;

        static PolicyOperator()
        {

            var notEquals = new NotEquals();
            NOT_EQUALS = new NotEquals<TValue, TResult>(notEquals, NotEqualDelegate());
            TokenOperatorMap[NOT_EQUALS.GetHashCode()] = NOT_EQUALS;

            var greater = new Greater();
            GREATER = new Greater<TValue, TResult>(greater, GreaterThanDelegate());
            TokenOperatorMap[GREATER.GetHashCode()] = GREATER;

            var less = new Less();
            LESS = new Less<TValue, TResult>(less, LessThanDelegate());
            TokenOperatorMap[LESS.GetHashCode()] = LESS;

            //
            // See RegExExist Func below
            //
            var regex = new Reg_Ex();
            REG_EX = new RegularExpression<TValue, TResult>(regex, RegExExists);
            TokenOperatorMap[REG_EX.GetHashCode()] = REG_EX;




            //
            // Build an expression for Empty
            //

            Type itemType = null;
            Type typeList = typeof(TValue);
            if (typeList.IsGenericType && typeof(IList<>).IsAssignableFrom(typeList.GetGenericTypeDefinition()))
            {
                itemType = typeList.GetGenericArguments()[0];


                var itemList = Expression.Parameter(typeList, "itemList");

                Expression countExpression =
                   Expression.Call(typeof(Enumerable), "Count", new Type[] { itemType }
                                   , itemList);

                Expression emptyExpression = Expression.Equal(Expression.Constant(0), countExpression);

                var empty = new Empty();
                EMPTY = new Empty<TValue, TResult>(empty
                      , Expression.Lambda<Func<TValue, TResult>>(emptyExpression, itemList).Compile());
                TokenOperatorMap[EMPTY.GetHashCode()] = EMPTY;


                //
                // Build an expression for not Empty
                //
                Expression noEmptyExpression = Expression.Not(emptyExpression);
                var notEmpty = new NotEmpty();
                NOT_EMPTY = new NotEmpty<TValue, TResult>(notEmpty
                     , Expression.Lambda<Func<TValue, TResult>>(noEmptyExpression, itemList).Compile());
                TokenOperatorMap[NOT_EMPTY.GetHashCode()] = NOT_EMPTY;

            }

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

    }

    public class PolicyOperator<T1, T2, TResult> : PolicyOperator
    {
        public static Equals<T1, T2, TResult> EQUALS;
        public static Intersect<T1, T2, TResult> INTERSECT;
        public static Contains<T1, T2, TResult> CONTAINS;
        public static NotContains<T1, T2, TResult> NOT_CONTAINS;
        public static ContainsRegEx<T1, List<T1>, TResult> CONTAINS_REG_EX;


        private static Func<T1, T2, TResult> IntersectDelegate()
        {

            //
            // Do not handle strings but handle all other IEnumerable types
            // Todo: Convert strings to IEnumerable and split on comma...
            //
            if (!typeof(IEnumerable).IsAssignableFrom(typeof(T1)) || typeof(string).IsAssignableFrom(typeof(T1))) return null;
            if (!typeof(IEnumerable).IsAssignableFrom(typeof(T2)) || typeof(string).IsAssignableFrom(typeof(T2))) return null;
            
            Type itemType = null;
            Type typeList = typeof(T1);
            Type typeList2 = typeof(T2);
            if (typeList.IsGenericType && typeList2.IsGenericType &&
                typeof(IList<>).IsAssignableFrom(typeList.GetGenericTypeDefinition()))
            {
                itemType = typeList.GetGenericArguments()[0];
            }

            var itemList = Expression.Parameter(typeList, "itemList");
            var itemList2 = Expression.Parameter(typeList2, "itemList2");
            
            Expression intersectExpression =
              Expression.Call(typeof(Enumerable), "Intersect", new Type[] { itemType }
                              , itemList, itemList2);

            return Expression.Lambda<Func<T1, T2, TResult>>(intersectExpression, itemList, itemList2).Compile();
        }

        static PolicyOperator()
        {
            var left = Expression.Parameter(typeof(T1), "itemType");
            var right = Expression.Parameter(typeof(T1), "item");
            var itemList = Expression.Parameter(typeof(IEnumerable<T1>), "itemList");


            var intersection = new Intersection();
            var intersectDelegate = IntersectDelegate();
            if (intersectDelegate != null)
            {
                INTERSECT = new Intersect<T1, T2, TResult>(intersection, intersectDelegate);
                TokenOperatorMap[INTERSECT.GetHashCode()] = INTERSECT;
            }

            if (typeof(Boolean).IsAssignableFrom(typeof(TResult)))  
            {
                var equals = new Equals();
                EQUALS = new Equals<T1, T2, TResult>(equals, EqualDelegate());
                TokenOperatorMap[EQUALS.GetHashCode()] = EQUALS;
            }

            // list.Any(a => a = "joe")
            if (typeof(IEnumerable).IsAssignableFrom(typeof(T2))
                && !typeof(string).IsAssignableFrom(typeof(T2))
                && (
                    !typeof(IEnumerable).IsAssignableFrom(typeof(T1))
                    || typeof(string).IsAssignableFrom(typeof(T1)))
                )
            {
                var body = Expression.Equal(left, right);
                var lambda = Expression.Lambda(body, left);

                Expression anyCall =
                    Expression.Call(typeof(Enumerable), "Any", new Type[] { typeof(T1) }
                                    , itemList
                                    , lambda);


                var contains = new Contains();
                CONTAINS = new Contains<T1, T2, TResult>(contains
                    , Expression.Lambda<Func<T1, T2, TResult>>(anyCall, right, itemList).Compile());
                TokenOperatorMap[CONTAINS.GetHashCode()] = CONTAINS;


                Expression notAnyCall = Expression.Not(anyCall);

                var notContains = new NotContains();
                NOT_CONTAINS = new NotContains<T1, T2, TResult>(notContains
                    , Expression.Lambda<Func<T1, T2, TResult>>(notAnyCall, right, itemList).Compile());
                TokenOperatorMap[NOT_CONTAINS.GetHashCode()] = NOT_CONTAINS;
            }





            //
            // See ContainsRegEx Func below
            //
            var containsRegEx = new ContainsRegEx();
            CONTAINS_REG_EX = new ContainsRegEx<T1, List<T1>, TResult>(containsRegEx, RegExContains);
            TokenOperatorMap[CONTAINS_REG_EX.GetHashCode()] = CONTAINS_REG_EX;


        }


        private static ParameterExpression Left
        {
            get
            {
                var left = Expression.Parameter(typeof(T1), "left");
                return left;
            }
        }
        private static ParameterExpression Right
        {
            get
            {
                var right = Expression.Parameter(typeof(T2), "right");
                return right;
            }
        }

        private static Func<T1, T2, TResult> EqualDelegate()
        {
            return ExpressionTreeUtil.CreateDelegate<T1, T2, TResult>(Expression.Equal, Left, Right);
        }
        private static Func<T1, T2, TResult> NotEqualDelegate()
        {
            return ExpressionTreeUtil.CreateDelegate<T1, T2, TResult>(Expression.NotEqual, Left, Right);
        }

        //TODO: Rethink this, don't like the conversion...
        /// <summary>
        /// Performs a regular expression match on a string.  This operation returns true if the regular expression is found in 
        /// the given string.
        /// </summary>
        /// <param name="pattern">Regular expression pattern</param>
        /// <param name="value">Source string to search</param>
        /// <returns></returns>
        public static TResult RegExContains(T1 pattern, List<T1> value)
        {
            Boolean success = false;
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

    }

    public class PolicyOperator
    {

        public static readonly Dictionary<int, OperatorBase> TokenOperatorMap;

        static PolicyOperator()
        {
            TokenOperatorMap = new Dictionary<Int32, OperatorBase>();
            new PolicyOperator<Boolean, Boolean>();
            new PolicyOperator<Int32, Boolean>();
            new PolicyOperator<String, Boolean>();
            new PolicyOperator<Int32, Int32, Boolean>();
            new PolicyOperator<String, String, Boolean>();
            new PolicyOperator<Int64, Int64, Boolean>();
            new PolicyOperator<Boolean, Boolean, Boolean>();
            new PolicyOperator<IList<string>, IList<string>, IEnumerable<string>> ();
        }

        //public static Equals<T, T> Equals<T>()
        //{
        //    return PolicyOperator<T, T>.EQUALS;
        //}

        public static T BitwiseOr<T>(T value1, T value2)
        {
            return PolicyOperator<T>.BITWISE_OR.Execute(value1, value2);
        }

        public static BitwiseOr<T> BitwiseOr<T>()
        {
            return PolicyOperator<T>.BITWISE_OR;
        }

        public static T BitwiseAnd<T> (T value1, T value2)
        {
            return PolicyOperator<T>.BITWISE_AND.Execute(value1, value2);
        }

        public static BitwiseAnd<T> BitwiseAnd<T>()
        {
            return PolicyOperator<T>.BITWISE_AND;
        }
        

        /// <summary>
        /// Gets the policy operator associated with a specific token string.
        /// @param token The token used to look up the PolicyOperator.
        /// @return The PolicyOperator associated with the token.  If the token does not represent a known operator, then null is returned,.
        /// </summary>
        public static OperatorBase FromToken(int tokenHashCode)
        {
            OperatorBase operatorBase;

            if (TokenOperatorMap.TryGetValue(tokenHashCode, out operatorBase))
            {
                return operatorBase;
            }
            return null;
        }
    }
}
