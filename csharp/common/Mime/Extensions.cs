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
using System.IO;
using System.Net.Mime;
using System.Collections.Generic;
using System.Net.Mail;
using Health.Direct.Common.Mail.DSN;

namespace Health.Direct.Common.Mime
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
        
        /// <summary>
        /// Tests if this content type has the named parameter and parameter value.
        /// </summary>
        /// <param name="contentType">The content type to test</param>
        /// <param name="parameter">The parameter name to test</param>
        /// <param name="value">The parameter value to test</param>
        /// <returns><c>true</c> if the content type has the named parameter with the parameter value</returns>
        public static bool HasParameter(this ContentType contentType, string parameter, string value)
        {
            string paramValue = contentType.Parameters[parameter];
            if (paramValue == null)
            {
                return false;
            }
            
            return MimeStandard.Equals(paramValue, value);
        }

        /// <summary>
        /// Returns a  string representation of <paramref name="encoding"/> compatable with the <c>micalg</c> parameter
        /// </summary>
        /// <param name="encoding">The <see cref="TransferEncoding"/> to stringify.</param>
        /// <returns>The string representation of the encoding compatable with the <c>Content-Transfer-Encoding</c> header</returns>
        public static string AsString(this TransferEncoding encoding)
        {
            return MimeStandard.ToString(encoding);
        }

        // TODO: turn supplied code example into a unit test.

        /// <summary>
        /// Splits the supplied <see cref="StringSegment"/> by <paramref name="separator"/>, returning an enumeration of <see cref="StringSegment"/> instances for each header subpart.
        /// </summary>
        /// <param name="source">Segment to split.</param>
        /// <param name="separator">The value separator to split on.</param>
        /// <example>
        /// <code>
        /// StringSegment text = new StringSegment("a, b, c;d, e, f:g, e");
        /// IEnumerable&lt;StringSegment&gt; parts = Split(text, ',');
        /// foreach(StringSegment part in parts)
        /// {
        ///     Console.WriteLine(part);
        /// }
        /// // Prints:
        /// // a
        /// // b
        /// // c;d
        /// // e
        /// // f:g
        /// // e
        /// </code>
        /// </example>
        /// <returns>An enumeration of <see cref="StringSegment"/> instances, one for each parsed part.</returns>
        public static IEnumerable<StringSegment> Split(this StringSegment source, char separator)
        {
            return StringSegment.Split(source, separator);
        }

        /// <summary>
        /// Remove empty lines.
        /// </summary>
        /// <param name="body"></param>
        /// <returns></returns>
        public static string TrimEmptyLines(this Body body)
        {
            string outputString;
            using (StringReader reader = new StringReader(body.Text))
            using (StringWriter writer = new StringWriter())
            {
                string line;
                while((line = reader.ReadLine()) != null)
                {
                    if (line.Trim().Length > 0)
                        writer.WriteLine(line);
                }
                outputString = writer.ToString();
            }
            return outputString;
        }

        /// <summary>
        /// Per-Recipient seperator.
        /// </summary>
        /// <param name="body"></param>
        /// <returns></returns>
        public static string PerRecipientSeperator(this Body body)
        {
            string outputString;
            using (StringReader reader = new StringReader(body.Text))
            using (StringWriter writer = new StringWriter())
            {
                string line;
                while ((line = reader.ReadLine()) != null)
                {
                    if (line.Trim().Length > 0)
                    {
                        writer.WriteLine(line);
                    }
                    else
                    {
                        writer.WriteLine(DSNParser.SeperatorHeader + MimeStandard.NameValueSeparator);
                    }
                }
                outputString = writer.ToString();
            }
            return outputString;
        }
        
        /// <summary>
        /// Takes the given text content, and creates a Mail attachment. 
        /// Gives the attachment the given file name
        /// </summary>
        /// <param name="textContent">Body of the attachment</param>
        /// <param name="fileName">Filename for this attachment</param>
        /// <returns></returns>
        public static Attachment CreateMailAttachmentFromString(string textContent, string fileName)
        {
            Attachment attachment = Attachment.CreateAttachmentFromString(textContent, fileName);
            attachment.ContentDisposition.DispositionType = "attachment";
            attachment.ContentDisposition.FileName = fileName;
            
            return attachment;
        }
        
        /// <summary>
        /// Reads the ContentStream of a <see cref="Attachment"/> as a string.
        /// Encoding bytemarks are automatically interpreted. 
        /// </summary>
        /// <param name="attachment">Attachment to read</param>
        /// <returns>Content stream decoded into a string</returns>
        public static string StringContent(this Attachment attachment)
        {
            using(StreamReader reader = new StreamReader(attachment.ContentStream))
            {
                return reader.ReadToEnd();
            }
        }
    }
}