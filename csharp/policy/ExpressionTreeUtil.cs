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
using System.Linq.Expressions;
using System.Reflection;
using Health.Direct.Policy.Extensions;
using Org.BouncyCastle.Asn1.X509.Qualified;

namespace Health.Direct.Policy
{
    public static class ExpressionTreeUtil
    {

        //Todo: refactor int CreateDelegate
        public static Func<TParam1, TParam2, TResult> CreateDelegateStringToLong<TParam1, TParam2, TResult>(
            Func<Expression, Expression, BinaryExpression> body, ParameterExpression left, ParameterExpression right)
        {
            try
            {
                MethodInfo methodInfo = typeof(Conversions).GetMethod("HexAsLong", new []{typeof(String)});
                Expression rightLong = Expression.Convert(right, typeof(Int64), methodInfo);
                return Expression.Lambda<Func<TParam1, TParam2, TResult>>(body(left, rightLong), left, right).Compile();
            }
            catch (InvalidOperationException ex)
            {
                var msg = ex.Message;
                return delegate { throw new InvalidOperationException(msg); };
            }
        }

        public static Func<TParam1, TParam2, TResult> CreateDelegate<TParam1, TParam2, TResult>(
            Func<Expression, Expression, BinaryExpression> body, ParameterExpression left, ParameterExpression right)
        {
            try
            {
                if (typeof (TParam1) != typeof (TParam2))
                {
                    MethodInfo methodInfo = typeof(ExpressionTreeUtil).GetMethod("ConvertValue");
                    MethodInfo generic = methodInfo.MakeGenericMethod(typeof(TParam1));
                    Expression rightConvert = Expression.Convert(right, typeof(TParam1), generic);
                    return Expression.Lambda<Func<TParam1, TParam2, TResult>>(body(left, rightConvert), left, right).Compile();
                }
                return Expression.Lambda<Func<TParam1, TParam2, TResult>>(body(left, right), left, right).Compile();
            }
            catch (InvalidOperationException ex)
            {
                var msg = ex.Message;
                return delegate { throw new InvalidOperationException(msg); };
            }
        }

        public static T ConvertValue<T>(string value)
        {
            return (T)Convert.ChangeType(value, typeof(T));
        }

        public static Func<TParam1, TResult> CreateDelegate<TParam1, TResult>(
            Func<Expression, Expression, BinaryExpression> body, ParameterExpression left, ParameterExpression right)
        {
            try
            {
                return Expression.Lambda<Func<TParam1, TResult>>(body(left, right), left, right).Compile();
            }
            catch (InvalidOperationException ex)
            {
                var msg = ex.Message;
                return delegate { throw new InvalidOperationException(msg); };
            }
        }


        public static Func<TParam1, TResult> CreateDelegate<TParam1, TResult>(
            Func<Expression, UnaryExpression> body, ParameterExpression input)
        {
            try
            {
                return Expression.Lambda<Func<TParam1, TResult>>(body(input), input).Compile();
            }
            catch (InvalidOperationException ex)
            {
                var msg = ex.Message;
                return delegate { throw new InvalidOperationException(msg); };
            }
            catch (ArgumentException ex)
            {
                var msg = ex.Message;
                return delegate { throw new ArgumentException(msg); };
            }
        }
        
    }
}