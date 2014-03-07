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
using System.Net;
using Health.Direct.Policy.OpCode;

namespace Health.Direct.Policy.Operators
{
    public class UriValid<T> : UnaryOperator
    {
        private string url;
        public UriValid(Code opCode)
            : base(opCode)
        {
            Execute = Stock;
        }

        public UriValid(Code opCode
            , Func<T, bool> body)
            : base(opCode)
        {
            Execute = body;
        }

        
        public readonly Func<T, bool> Execute;

        public override Delegate ExecuteRef
        {
            get { return Execute; }
        }

        private bool Stock(T uri)
        {
            try
            {
                Uri certUri = null;
                if (uri.GetType() == typeof(Uri))
                {
                    certUri = uri as Uri;
                }
                else
                {
                    certUri = new Uri(uri as string);
                }
                if (certUri == null)
                {
                    return false;
                }
                url = certUri.ToString();

                var request = WebRequest.Create(certUri) as HttpWebRequest;
                if (request == null) return false;
                request.Method = "HEAD";
                using (var response = (HttpWebResponse)request.GetResponse())
                {
                    int respCode = (int)response.StatusCode;
                    if (respCode >= 200 && respCode <= 300)
                    {
                        return true;
                    }
                    return true;
                }
            }
            catch (UriFormatException ex)
            {
                //Invalid Url
                Notify(this, string.Format("{0}:{1}", uri, ex.Message));
                return false;
            }
            catch (WebException ex)
            {
                //Unable to access url
                Notify(this, string.Format("{0}:{1}", uri, ex.Message));
                return false;
            }
            catch (NotSupportedException ex)
            {
                //Most likely a bad url
                Notify(this, string.Format("{0}:{1}", uri, ex.Message));
                return false;
            }
        }


    }
}