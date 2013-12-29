///* 
// Copyright (c) 2013, Direct Project
// All rights reserved.

// Authors:
//    Joe Shook      jshook@kryptiq.com
  
//Redistribution and use in source and binary forms, with or without modification, are permitted provided that the following conditions are met:

//Redistributions of source code must retain the above copyright notice, this list of conditions and the following disclaimer.
//Redistributions in binary form must reproduce the above copyright notice, this list of conditions and the following disclaimer in the documentation and/or other materials provided with the distribution.
//Neither the name of The Direct Project (directproject.org) nor the names of its contributors may be used to endorse or promote products derived from this software without specific prior written permission.
//THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 
//*/

//using System;
//using System.Collections.Generic;
//using System.IO;
//using System.Linq;
//using System.Linq.Expressions;
//using System.Security.Policy;
//using System.Text;
//using System.Threading.Tasks;

//namespace Health.Direct.Policy
//{
//    /// <summary>
//    /// Implements serialization/deserialization for <see cref="PolicyExpression"/> implementations.
//    /// </summary>
//    public abstract class PolicySerializer
//    {

//        /// <summary>
//        /// Deserializes and parses text from <paramref name="stream"/>
//        /// </summary>
//        /// <typeparam name="T">The <see cref="PolicyExpression"/> type to which to deserialize.</typeparam>
//        /// <param name="stream">The <see cref="Stream"/> providing the source data</param>
//        /// <returns>The deserialized and parsed <see cref="PolicyExpression"/></returns>
//        public virtual T Deserialize<T>(Stream stream)
//            where T : PolicyExpression, new()
//        {
//            if (stream == null)
//            {
//                throw new ArgumentNullException("stream");
//            }

//            using (StreamReader reader = new StreamReader(stream, Encoding.ASCII))
//            {
//                return Deserialize<T>(reader);
//            }
//        }

//        ///// <summary>
//        ///// Deserializes and parses text from <paramref name="reader"/>
//        ///// </summary>
//        ///// <typeparam name="T">The <see cref="PolicyExpression"/> type to which to deserialize.</typeparam>
//        ///// <param name="reader">The <see cref="TextReader"/> providing the source data</param>
//        ///// <returns>The deserialized and parsed <see cref="PolicyExpression"/></returns>
//        public virtual T Deserialize<T>(TextReader reader)
//            where T : PolicyExpression, new()
//        {
//            if (reader == null)
//            {
//                throw new ArgumentNullException("reader");
//            }

//            return Deserialize<T>(reader.ReadToEnd());
//        }

//        ///// <summary>
//        ///// Deserializes and parses text from <paramref name="messageText"/>
//        ///// </summary>
//        ///// <typeparam name="T">The <see cref="PolicyExpression"/> type to which to deserialize.</typeparam>
//        ///// <param name="policyText">The <see cref="string"/> representing the source text</param>
//        ///// <returns>The deserialized and parsed entity</returns>
//        public virtual T Deserialize<T>(string policyText)
//            where T : PolicyExpression, new()
//        {
//            if (string.IsNullOrEmpty(policyText))
//            {
//                throw new ArgumentNullException("policyText");
//            }

//            return Deserialize<T>(new StringSegment(messageText));

//        }
//    }
//}
