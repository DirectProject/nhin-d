/* 
 Copyright (c) 2010, NHIN Direct Project
 All rights reserved.

 Authors:
    Umesh Madan     umeshma@microsoft.com
  
Redistribution and use in source and binary forms, with or without modification, are permitted provided that the following conditions are met:

Redistributions of source code must retain the above copyright notice, this list of conditions and the following disclaimer.
Redistributions in binary form must reproduce the above copyright notice, this list of conditions and the following disclaimer in the documentation and/or other materials provided with the distribution.
Neither the name of the The NHIN Direct Project (nhindirect.org). nor the names of its contributors may be used to endorse or promote products derived from this software without specific prior written permission.
THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 
*/
using System;
using System.Net.Mime;

namespace NHINDirect.Mime
{
    /// <summary>
    /// Holds extension methods.
    /// </summary>
    public static class Extensions
    {
        /// <summary>
        /// Tests if this content type is same one represented by the specified <paramref name="mediaType"/>
        /// </summary>
        /// <param name="contentType">This <see cref="ContentType"/></param>
        /// <param name="mediaType">The content type string to test against this instance.</param>
        /// <returns></returns>
        public static bool IsMediaType(this ContentType contentType, string mediaType)
        {
            return (MimeStandard.Equals(contentType.MediaType, mediaType));
        }
        
        //TODO: should be HasParameter...
        /// <summary>
        /// Tests if this content type has the named parameter and parameter value.
        /// </summary>
        /// <param name="contentType">The content type to test</param>
        /// <param name="parameter">The parameter name to test</param>
        /// <param name="value">The parameter value to test</param>
        /// <returns><c>true</c> if the content type has the named parameter with the parameter value</returns>
        public static bool IsParameter(this ContentType contentType, string parameter, string value)
        {
            string paramValue = contentType.Parameters[parameter];
            if (paramValue == null)
            {
                return false;
            }
            
            return MimeStandard.Equals(paramValue, value);
        }

        //TODO: ToString??
        /// <summary>
        /// Returns a  string representation of <paramref name="encoding"/> compatable with the <c>micalg</c> parameter
        /// </summary>
        /// <param name="encoding">The <see cref="TransferEncoding"/> to stringify.</param>
        /// <returns>The string representation of the encoding compatable with the <c>Content-Transfer-Encoding</c> header</returns>
        public static string AsString(this TransferEncoding encoding)
		{
		    return MimeStandard.AsString(encoding);
		}
    }
}
