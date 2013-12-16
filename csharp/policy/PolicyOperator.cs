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
using Health.Direct.Policy.Operators;

namespace Health.Direct.Policy
{
    
    public class PolicyOperator<T>
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
            var left = Expression.Parameter(typeof(T), "left");
            var right = Expression.Parameter(typeof(T), "right");
            var itemList = Expression.Parameter(typeof(IEnumerable<Object>), "itemList");


            LOGICAL_AND = new LogicalAnd<T>("&&"
                , "and"
                , PolicyOpCode.LOGICAL_AND
                , ExpressionTreeUtil.CreateDelegate<T, T, T>(Expression.AndAlso, left, right));

            LOGICAL_OR = new LogicalOr<T>("||"
                , "or"
                , PolicyOpCode.LOGICAL_OR
                , ExpressionTreeUtil.CreateDelegate<T, T, T>(Expression.OrElse, left, right));

            BITWISE_AND = new BitwiseAnd<T>("&"
                  , "bitand"
                  , PolicyOpCode.BITWISE_AND
                  , ExpressionTreeUtil.CreateDelegate<T, T, T>(Expression.And, left, right));
   

            BITWISE_OR = new BitwiseOr<T>("|"
                  , "bitor"
                  , PolicyOpCode.BITWISE_OR
                  , ExpressionTreeUtil.CreateDelegate<T, T, T>(Expression.Or, left, right));

            
            SIZE = new Size<T>(
                    "^"
                  , "size"
                  , PolicyOpCode.SIZE
                  , ExpressionTreeUtil.CreateDelegate<T, int>(Expression.ArrayLength, left));


            LOGICAL_NOT = new Not<T>(
                "!"
                , "not"
                , PolicyOpCode.LOGICAL_NOT
                , ExpressionTreeUtil.CreateDelegate<T, bool>(Expression.Not, left));

            URI_VALIDATE = new UriValid<T>(
                "@@"
                , "uri validate"
                , PolicyOpCode.URI_VALIDATE);
        }

        
    }

    public class PolicyOperator<TValue, TResult>
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
            var left = Expression.Parameter(typeof(TValue), "left");
            var right = Expression.Parameter(typeof(TValue), "right");

            EQUALS = new Equals<TValue, TResult>("="
                 , "equals"
                 , PolicyOpCode.EQUALS
                 , ExpressionTreeUtil.CreateDelegate<TValue, TValue, TResult>(Expression.Equal, left, right));

            NOT_EQUALS = new NotEquals<TValue, TResult>("!="
                 , "not equals"
                 , PolicyOpCode.NOT_EQUALS
                 , ExpressionTreeUtil.CreateDelegate<TValue,TValue, TResult>(Expression.NotEqual, left, right ));


            GREATER = new Greater<TValue, TResult>(">"
                 , "greater than"
                 , PolicyOpCode.GREATER
                 , ExpressionTreeUtil.CreateDelegate<TValue,TValue, TResult>(Expression.GreaterThan, left, right));


            LESS = new Less<TValue, TResult>("<"
                 , "less than"
                 , PolicyOpCode.LESS
                 , ExpressionTreeUtil.CreateDelegate<TValue,TValue, TResult>(Expression.LessThan, left, right));


            REG_EX = new RegularExpression<TValue, TResult>("$"
                , "not contains"
                , PolicyOpCode.REG_EX
                , RegExExists);
        }
    }

    public class PolicyOperator<TValue, TList, TResult> where TList : IEnumerable<TValue>
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
            CONTAINS = new Contains<TValue, TList, TResult>("{?}"
                , "contains"
                , PolicyOpCode.CONTAINS
                , Expression.Lambda<Func<TValue, TList, TResult>>(anyCall, right, itemList).Compile());

            
            Expression notAnyCall = Expression.Not(anyCall);

            NOT_CONTAINS = new NotContains<TValue, TList, TResult>("{?}!"
                , "not contains"
                , PolicyOpCode.NOT_CONTAINS
                , Expression.Lambda<Func<TValue, TList, TResult>>(notAnyCall, right, itemList).Compile());


            CONTAINS_REG_EX = new ContainsRegEx<TValue, TList, TResult>("{}$"
                , "contains match"
                , PolicyOpCode.CONTAINS_REG_EX
                , RegExExists);


            Expression countExpression =
               Expression.Call(typeof(Enumerable), "Count", new Type[] { typeof(TValue) }
                               , itemList);

            Expression emptyExpression = Expression.Equal(Expression.Constant(0), countExpression);

            EMPTY = new Empty<TList, TResult>(
                    "{}"
                  , "empty"
                  , PolicyOpCode.EMPTY
                  , Expression.Lambda<Func<TList, TResult>>(emptyExpression, itemList).Compile());

            Expression noEmptyExpression = Expression.Not(emptyExpression);
            NOT_EMPTY = new NotEmpty<TList, TResult>(
                   "{}!"
                 , "not empty"
                 , PolicyOpCode.NOT_EMPTY
                 , Expression.Lambda<Func<TList, TResult>>(noEmptyExpression, itemList).Compile());
        }
    }

    public class PolicyOperator
    {
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

    }
}
