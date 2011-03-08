/* 
 Copyright (c) 2010, Direct Project
 All rights reserved.

 Authors:
    Arien Malec     arien.malec@nhindirect.org
  
Redistribution and use in source and binary forms, with or without modification, are permitted provided that the following conditions are met:

Redistributions of source code must retain the above copyright notice, this list of conditions and the following disclaimer.
Redistributions in binary form must reproduce the above copyright notice, this list of conditions and the following disclaimer in the documentation and/or other materials provided with the distribution.
Neither the name of The Direct Project (directproject.org) nor the names of its contributors may be used to endorse or promote products derived from this software without specific prior written permission.
THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 
*/
using System;
using System.IO;

namespace Health.Direct.Common.Metadata
{
    /// <summary>
    /// Extension methods for working with metadata elements
    /// </summary>
    public static class Extensions
    {

        /// <summary>
        /// Converts the supplied date to an HL7 formatted UTC datetime string
        /// </summary>
        public static string ToHL7Date(this DateTime? datetime)
        {
            if (datetime == null) return null;
            return datetime.Value.ToUniversalTime().ToString(HL7Util.DateTimeFormat, System.Globalization.DateTimeFormatInfo.InvariantInfo);
        }

        /// <summary>
        /// Formats the provided value as a string suitable for inclusion in HL7
        /// </summary>
        public static string AsString(this Sex? s)
        {
            return HL7Util.ToHL7Value(s);
        }


        /// <summary>
        /// Reads all bytes of the supplied stream.
        /// </summary>
        public static byte[] ReadAllBytes(this Stream stream)
        {
            int buffSize = 1024;
            byte[] buffer = new byte[buffSize];

            int bytesRead = 0;
            using (BufferedStream inStream = new BufferedStream(stream))
            {
                using (MemoryStream outStream = new MemoryStream())
                {
                    while ((bytesRead = inStream.Read(buffer, 0, buffSize)) > 0)
                    {
                        outStream.Write(buffer, 0, bytesRead);
                    }

                    return outStream.ToArray();
                }
            }
        }
    }
}