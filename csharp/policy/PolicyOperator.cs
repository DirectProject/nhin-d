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
using System.Reflection;
using System.Text.RegularExpressions;
using Health.Direct.Policy.Extensions;
using Health.Direct.Policy.OpCode;
using Health.Direct.Policy.Operators;


namespace Health.Direct.Policy
{
    public class PolicyOperator<T> : PolicyOperator
    {

        public static LogicalAnd<T> LOGICAL_AND;
        public static LogicalOr<T> LOGICAL_OR;
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
            return ExpressionTreeUtil.CreateDelegateAutoConvert<T, T, T>(Expression.AndAlso, Left, Right);
        }
        private static Func<T, T, T> LogicalOrDelegate()
        {
            return ExpressionTreeUtil.CreateDelegateAutoConvert<T, T, T>(Expression.OrElse, Left, Right);
        }
        
        private static Func<T, Int32> SizeDelegate()
        {
            Type typeList = typeof(T);
            if (typeList.IsGenericType)
            {
                var genericTypeDefinition = typeList.GetGenericTypeDefinition();
                if (
                    // Type is IEnumerable<>
                    typeof (IEnumerable<>).IsAssignableFrom(typeList.GetGenericTypeDefinition()) 
                    ||
                    //Implemented Interfaces are IEnumerable
                    genericTypeDefinition.GetInterfaces()
                        .Any(t => t.IsGenericType && t.GetGenericTypeDefinition() == typeof (IEnumerable<>)))
                {

                    Type itemType = typeList.GetGenericArguments()[0];
                    var itemList = Expression.Parameter(typeList, "itemList");

                    Expression countExpression =
                        Expression.Call(typeof (Enumerable), "Count", new Type[] {itemType}
                            , itemList);

                    var lambda = Expression.Lambda<Func<T, Int32>>(countExpression, itemList).Compile();
                    return lambda;
                    //return ExpressionTreeUtil.CreateDelegate<T, Int32>(Expression.ArrayLength, Left);
                }
            }
            return null;
        }
        private static Func<T, Boolean> LogicalNotDelegate()
        {
            return ExpressionTreeUtil.CreateDelegate<T, Boolean>(Expression.Not, Left);
        }
    }

    public class PolicyOperator<T1, T2> : PolicyOperator
    {
        public static Greater<T1, T2> GREATER;
        public static Less<T1, T2> LESS;
        public static RegularExpression<T1, T2> REG_EX;
        public static Empty<T1, T2> EMPTY;
        public static NotEmpty<T1, T2> NOT_EMPTY;
        public static BitwiseAnd<T1, T2> BITWISE_AND;
        public static BitwiseOr<T1, T2> BITWISE_OR;

        static PolicyOperator()
        {

            if (typeof(T1) == typeof(Int32) && (typeof(T2) == typeof(Int32) || typeof(T2) == typeof(string)))
            {
                var greater = new Greater();
                GREATER = new Greater<T1, T2>(greater, GreaterThanDelegate());
                TokenOperatorMap[GREATER.GetHashCode()] = GREATER;

                var less = new Less();
                LESS = new Less<T1, T2>(less, LessThanDelegate());
                TokenOperatorMap[LESS.GetHashCode()] = LESS;

                var bitwiseAnd = new Bitwise_And();
                BITWISE_AND = new BitwiseAnd<T1, T2>(bitwiseAnd, BitwiseAndDelegate());
                TokenOperatorMap[BITWISE_AND.GetHashCode()] = BITWISE_AND;

                var bitwiseOr = new Bitwise_Or();
                BITWISE_OR = new BitwiseOr<T1, T2>(bitwiseOr, BitwiseOrDelegate());
                TokenOperatorMap[BITWISE_OR.GetHashCode()] = BITWISE_OR;
            }

            //
            // See RegExExist Func below
            //
            var regex = new Reg_Ex();
            REG_EX = new RegularExpression<T1, T2>(regex, RegExExists);
            TokenOperatorMap[REG_EX.GetHashCode()] = REG_EX;




            //
            // Build an expression for Empty
            //

            Type typeList = typeof(T1);
            if (typeList.IsGenericType && typeof(IList<>).IsAssignableFrom(typeList.GetGenericTypeDefinition()))
            {
                Type itemType = typeList.GetGenericArguments()[0];


                var itemList = Expression.Parameter(typeList, "itemList");

                Expression countExpression =
                   Expression.Call(typeof(Enumerable), "Count", new Type[] { itemType }
                                   , itemList);

                Expression emptyExpression = Expression.Equal(Expression.Constant(0), countExpression);

                var empty = new Empty();
                EMPTY = new Empty<T1, T2>(empty
                      , Expression.Lambda<Func<T1, T2>>(emptyExpression, itemList).Compile());
                TokenOperatorMap[EMPTY.GetHashCode()] = EMPTY;


                //
                // Build an expression for not Empty
                //
                Expression noEmptyExpression = Expression.Not(emptyExpression);
                var notEmpty = new NotEmpty();
                NOT_EMPTY = new NotEmpty<T1, T2>(notEmpty
                     , Expression.Lambda<Func<T1, T2>>(noEmptyExpression, itemList).Compile());
                TokenOperatorMap[NOT_EMPTY.GetHashCode()] = NOT_EMPTY;

            }
            
        }

        
        private static Func<T1, T2, Boolean> GreaterThanDelegate()
        {
            var left = Expression.Parameter(typeof(T1), "left");
            var right = Expression.Parameter(typeof(T2), "right");
            if (typeof(T1) == typeof(Int32) && typeof(T2) == typeof(String))
            {
                MethodInfo methodInfo = typeof(Int32).GetMethod("Parse", new Type[] { typeof(string) });
                Expression rightConvert = Expression.Convert(right, typeof(T1), methodInfo);
                return Expression.Lambda<Func<T1, T2, Boolean>>(Expression.GreaterThan(left, rightConvert), left, right).Compile();
            }
            //return Expression.Lambda<Func<TParam1, TParam2, TResult>>(body(left, right), left, right).Compile();


            return ExpressionTreeUtil.CreateDelegateAutoConvert<T1, T2, Boolean>(Expression.GreaterThan, left, right);
        }
        private static Func<T1, T2, Boolean> LessThanDelegate()
        {
            var left = Expression.Parameter(typeof(T1), "left");
            var right = Expression.Parameter(typeof(T2), "right");
            return ExpressionTreeUtil.CreateDelegateAutoConvert<T1, T2, Boolean>(Expression.LessThan, left, right);
        }


        private static Func<T1, T2, T1> BitwiseAndDelegate()
        {
            var left = Expression.Parameter(typeof(T1), "left");
            var right = Expression.Parameter(typeof(T2), "right");
            return ExpressionTreeUtil.CreateDelegateAutoConvert<T1, T2, T1>(Expression.And, left, right);
        }
        private static Func<T1, T2, T1> BitwiseOrDelegate()
        {
            var left = Expression.Parameter(typeof(T1), "left");
            var right = Expression.Parameter(typeof(T2), "right");
            return ExpressionTreeUtil.CreateDelegateAutoConvert<T1, T2, T1>(Expression.Or, left, right);
        }


        //TODO: Rethink this, don't like the conversion...
        /// <summary>
        /// Performs a regular expression match on a string.  This operation returns true if the regular expression is found in 
        /// the given string.
        /// </summary>
        /// <param name="pattern">Regular expression pattern</param>
        /// <param name="value">Source string to search</param>
        /// <returns></returns>
        public static T2 RegExExists(T1 pattern, T1 value)
        {
            string v = value as string;
            string p = pattern as string;

            Regex regex = new Regex(p);
            var match = regex.Match(v);
            return (T2)Convert.ChangeType(match.Success, typeof(T2));
        }

    }

    public class PolicyOperator<T1, T2, TResult> : PolicyOperator
    {
        public static Equals<T1, T2, TResult> EQUALS;
        public static NotEquals<T1, T2, TResult> NOT_EQUALS;
        public static Intersect<T1, T2, TResult> INTERSECT;
        public static Contains<T1, T2, TResult> CONTAINS;
        public static NotContains<T1, T2, TResult> NOT_CONTAINS;
        public static ContainsRegEx<T1, T2, TResult> CONTAINS_REG_EX;
        

        static PolicyOperator()
        {
            
            //
            // Intersect
            //
            var Int32ersection = new Intersection();
            var Int32ersectDelegate = IntersectDelegate();
            if (Int32ersectDelegate != null)
            {
                INTERSECT = new Intersect<T1, T2, TResult>(Int32ersection, Int32ersectDelegate);
                TokenOperatorMap[INTERSECT.GetHashCode()] = INTERSECT;
            }

            //
            // Equals
            //
            if (typeof(Boolean).IsAssignableFrom(typeof(TResult)))  
            {
                var equals = new Equals();
                if (typeof (Int64) == typeof (T1) && typeof (String) == typeof (T2))
                {
                    EQUALS = new Equals<T1, T2, TResult>(equals, EqualDelegateStringToLong());
                    TokenOperatorMap[EQUALS.GetHashCode()] = EQUALS;
                }
                else
                {
                    EQUALS = new Equals<T1, T2, TResult>(equals, EqualDelegate());
                    TokenOperatorMap[EQUALS.GetHashCode()] = EQUALS;
                }
            }

            //
            // Not Equals
            //
            if (typeof(Boolean).IsAssignableFrom(typeof(TResult)))
            {
                var notEquals = new NotEquals();
                if (typeof(Int64) == typeof(T1) && typeof(String) == typeof(T2))
                {
                    NOT_EQUALS = new NotEquals<T1, T2, TResult>(notEquals, NotEqualDelegateStringToLong());
                    TokenOperatorMap[NOT_EQUALS.GetHashCode()] = NOT_EQUALS;
                }
                else
                {
                    NOT_EQUALS = new NotEquals<T1, T2, TResult>(notEquals, NotEqualDelegate());
                    TokenOperatorMap[NOT_EQUALS.GetHashCode()] = NOT_EQUALS;
                }
            }


            //
            // Contains
            // list.Any(a => a = "joe")
            //
            if (typeof(IEnumerable).IsAssignableFrom(typeof(T1))
                && !typeof(string).IsAssignableFrom(typeof(T1))
                && (
                    !typeof(IEnumerable).IsAssignableFrom(typeof(T2))
                    || typeof(string).IsAssignableFrom(typeof(T2))
                    )
                && (typeof(TResult) == typeof(Boolean))
                )
            {
                var left = Expression.Parameter(typeof(T2), "left");
                var right = Expression.Parameter(typeof(T2), "right");
                var itemList = Expression.Parameter(typeof(IEnumerable<T2>), "itemList");


                var body = Expression.Equal(left, right);
                var lambda = Expression.Lambda(body, left);

                Expression anyCall =
                    Expression.Call(typeof(Enumerable), "Any", new Type[] { typeof(T2) }
                                    , itemList
                                    , lambda);


                var contains = new Contains();
                CONTAINS = new Contains<T1, T2, TResult>(contains
                    , Expression.Lambda<Func<T1, T2, TResult>>(anyCall, itemList, right).Compile());
                TokenOperatorMap[CONTAINS.GetHashCode()] = CONTAINS;


                //
                // Not Contains
                // ! list.Any(a => a = "joe")
                Expression notAnyCall = Expression.Not(anyCall);

                var notContains = new NotContains();
                NOT_CONTAINS = new NotContains<T1, T2, TResult>(notContains
                    , Expression.Lambda<Func<T1, T2, TResult>>(notAnyCall, itemList, right).Compile());
                TokenOperatorMap[NOT_CONTAINS.GetHashCode()] = NOT_CONTAINS;
            }


            //
            // Contains string in string
            // result = "joe".Contains("o");
            //if (typeof (String) == typeof (T1) && typeof (String) == typeof (T1) && typeof(TResult) == typeof(Boolean))
            //{
            //    var left = Expression.Parameter(typeof(T1), "left");
            //    var right = Expression.Parameter(typeof(T2), "right");
            //    MethodInfo method = typeof(string).GetMethod("Contains", new[] { typeof(string) });
            //    var containsMethodExp = Expression.Call(left, method, right);

            //    var contains = new Contains();

            //    CONTAINS = new Contains<T1, T2, TResult>(contains
            //        , Expression.Lambda<Func<T1, T2, TResult>>(containsMethodExp, left, right).Compile());
            //    TokenOperatorMap[CONTAINS.GetHashCode()] = CONTAINS;
            //}



            //
            // See ContainsRegEx Func below
            //
            if (typeof (IEnumerable<String>).IsAssignableFrom(typeof (T1)))
            {
                var containsRegEx = new ContainsRegEx();
                CONTAINS_REG_EX = new ContainsRegEx<T1, T2, TResult>(containsRegEx, RegExContains);
                TokenOperatorMap[CONTAINS_REG_EX.GetHashCode()] = CONTAINS_REG_EX;
            }
            


        }

        private static Func<T1, T2, TResult> IntersectDelegate()
        {

            //
            // Do not handle strings but handle all other IEnumerable types
            // Todo: Convert strings to IEnumerable and split on comma...
            //
            if (!typeof(IEnumerable).IsAssignableFrom(typeof(T1)) || typeof(string).IsAssignableFrom(typeof(T1))) return null;
            if (!typeof(IEnumerable).IsAssignableFrom(typeof(T2)))  return null;
            if (!typeof(IEnumerable).IsAssignableFrom(typeof(TResult))) return null;

            Type itemType = null;
            Type leftType = typeof(T1);
            Type rightType = typeof(T2);

            //if (left.IsGenericType && right.IsGenericType &&
            //    typeof(IList<>).IsAssignableFrom(left.GetGenericTypeDefinition()))
            //{
                itemType = leftType.GetGenericArguments()[0];
            //}
            var left = Expression.Parameter(leftType, "leftList");
            var right = Expression.Parameter(rightType, "rightList");
            Expression expressionBody;
            if (typeof(string).IsAssignableFrom(typeof(T2)))
            {
                MethodInfo methodInfo = typeof(Conversions).GetMethod("CommaStringToList");
                Expression rightConvert = Expression.Convert(right, leftType, methodInfo);

                expressionBody =
                  Expression.Call(typeof(Enumerable), "Intersect", new Type[] { itemType }
                                  , left, rightConvert);

                return Expression.Lambda<Func<T1, T2, TResult>>(expressionBody, left, right).Compile();
            }

            expressionBody = 
              Expression.Call(typeof(Enumerable), "Intersect", new Type[] { itemType }
                              , left, right);

            return Expression.Lambda<Func<T1, T2, TResult>>(expressionBody, left, right).Compile();
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


        // TODO: refactor these equal not equal function builders
        private static Func<T1, T2, TResult> EqualDelegateStringToLong()
        {
            var expression = ExpressionTreeUtil.CreateDelegateStringToLong<T1, T2, TResult>(Expression.Equal, Left, Right);
            return expression;
        }

        private static Func<T1, T2, TResult> NotEqualDelegateStringToLong()
        {
            var expression = ExpressionTreeUtil.CreateDelegateStringToLong<T1, T2, TResult>(Expression.NotEqual, Left, Right);
            return expression;
        }

        private static Func<T1, T2, TResult> EqualDelegate()
        {
            Type stringType = typeof (String);
            if (typeof(T1) == stringType && typeof(T2) == stringType)
            {
                return ExpressionTreeUtil.CreateCaseInsensitiveEqualsDelegate<T1, T2, TResult>(Left, Right);
            }
            return ExpressionTreeUtil.CreateDelegateAutoConvert<T1, T2, TResult>(Expression.Equal, Left, Right);
        }

        private static Func<T1, T2, TResult> NotEqualDelegate()
        {
            return ExpressionTreeUtil.CreateDelegateAutoConvert<T1, T2, TResult>(Expression.NotEqual, Left, Right);
        }

        //TODO: Rethink this, don't like the conversion...
        /// <summary>
        /// Performs a regular expression match on a string.  This operation returns true if the regular expression is found in 
        /// the given string.
        /// </summary>
        /// <param name="pattern">Regular expression pattern</param>
        /// <param name="items">Source strings to search</param>
        /// <returns></returns>
        private static TResult RegExContains(T1 items, T2 pattern) 
        {
            var values = items as IList<String>;
            Boolean success = false;
            foreach (var item in values)
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

        public static readonly Dictionary<Int32, OperatorBase> TokenOperatorMap;

        static PolicyOperator()
        {
            TokenOperatorMap = new Dictionary<Int32, OperatorBase>();

            new PolicyOperator<Boolean, Boolean>();
            new PolicyOperator<Int32, Boolean>();
            new PolicyOperator<Int32, String>();
            new PolicyOperator<String, Boolean>();
            new PolicyOperator<IList<String>, Boolean>();

            new PolicyOperator<Int32, Int32, Boolean>();
            new PolicyOperator<Int32, Int32, Int32>();
            new PolicyOperator<Int32, String, Int32>();
            new PolicyOperator<String, String, Boolean>();
            new PolicyOperator<Int64, Int64, Boolean>();
            new PolicyOperator<Boolean, Boolean, Boolean>();
            new PolicyOperator<Boolean, String, Boolean>();
            new PolicyOperator<IList<String>, IList<String>, IEnumerable<String>>();
            new PolicyOperator<Int64, String, Boolean>();
            new PolicyOperator<Int32, String, Boolean>();
            new PolicyOperator<IList<String>, String, IEnumerable<String>>();
            new PolicyOperator<IList<String>, String, Boolean>();

            //Unary
            //new PolicyOperator<String>();
            //new PolicyOperator<Int32>();
            new PolicyOperator<IList<String>>();
            new PolicyOperator<IEnumerable<String>>();
            new PolicyOperator<Boolean>();
        }

        //public static Equals<T, T> Equals<T>()
        //{
        //    return PolicyOperator<T, T>.EQUALS;
        //}

        public static TLeft BitwiseOr<TLeft, TRight>(TLeft value1, TRight value2)
        {
            return PolicyOperator<TLeft, TRight>.BITWISE_OR.Execute(value1, value2);
        }

        public static BitwiseOr<TLeft, TRight> BitwiseOr<TLeft, TRight>()
        {
            return PolicyOperator<TLeft, TRight>.BITWISE_OR;
        }

        public static TLeft BitwiseAnd<TLeft, TRight>(TLeft value1, TRight value2)
        {
            return PolicyOperator<TLeft, TRight>.BITWISE_AND.Execute(value1, value2);
        }

        public static BitwiseAnd<TLeft, TRight> BitwiseAnd<TLeft, TRight>()
        {
            return PolicyOperator<TLeft, TRight>.BITWISE_AND;
        }
        

        /// <summary>
        /// Gets the policy operator associated with a specific token string.
        /// @param token The token used to look up the PolicyOperator.
        /// @return The PolicyOperator associated with the token.  If the token does not represent a known operator, then null is returned,.
        /// </summary>
        public static OperatorBase FromToken(Int32 tokenHashCode)
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
