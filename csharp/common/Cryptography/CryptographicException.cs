/* 
 Copyright (c) 2010, Direct Project
 All rights reserved.

 Authors:
    Umesh Madan     umeshma@microsoft.com
  
Redistribution and use in source and binary forms, with or without modification, are permitted provided that the following conditions are met:

Redistributions of source code must retain the above copyright notice, this list of conditions and the following disclaimer.
Redistributions in binary form must reproduce the above copyright notice, this list of conditions and the following disclaimer in the documentation and/or other materials provided with the distribution.
Neither the name of The Direct Project (directproject.org) nor the names of its contributors may be used to endorse or promote products derived from this software without specific prior written permission.
THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 
*/
using System;
using System.Security.Cryptography;

namespace Health.Direct.Common.Cryptography
{
    /// <summary>
    /// Represents an exception ocurring in cryptographic operations.
    /// </summary>
    /// <typeparam name="T">The exception error type.</typeparam>
    public class CryptographicException<T> : CryptographicException
    {
        T m_error;
        
        /// <summary>
        /// Initializes an instance with the specified error type.
        /// </summary>
        /// <param name="error">The error type for this exception.</param>
        public CryptographicException(T error)
        {
            m_error = error;
        }

        /// <summary>
        /// Initializes an instance with the specified error type.
        /// </summary>
        /// <param name="error">The error type for this exception.</param>
        /// <param name="message">A cusom exception message.</param>
        public CryptographicException(T error, string message)
            : base(message)
        {
            m_error = error;
        }
        
        /// <summary>
        /// The error type for this exception.
        /// </summary>
        public T Error
        {
            get
            {
                return m_error;
            }
        }

        /// <summary>
        /// Formats this exception as a string.
        /// </summary>
        /// <returns>A string representation of the exception.</returns>
        public override string ToString()
        {
            return string.Format("ERROR={0};{1}{2}", this.m_error, Environment.NewLine, base.ToString());
        }
    }
}