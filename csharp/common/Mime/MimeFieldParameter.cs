/* 
 Copyright (c) 2013, Direct Project
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
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.IO;
using Health.Direct.Common.Mail;

namespace Health.Direct.Common.Mime
{
    /// <summary>
    /// Field Parameters are name=value pairs, separated by ';'
    /// Certain special characters must be treated with care. (see RFC 5322, 3.2.3, specials)
    /// 
    /// name *cannot* contain special characters. 
    /// If value has'special', then value must be entirely quoted. The quote character may be escaped. 
    ///
    /// This very simple parser is very forgiving. 
    /// </summary>
    public class MimeFieldParameters : List<KeyValuePair<string, string>>
    {   
        const char ParameterSeparator = ';';
        const char NameValueSeparator = '=';
        
        /// <summary>
        /// Construct an empty field parameters collection
        /// </summary>
        public MimeFieldParameters()
        {
        }
        
        /// <summary>
        /// Initialize a field parameters collection with the given params
        /// </summary>
        /// <param name="fieldParams"></param>
        public MimeFieldParameters(IEnumerable<KeyValuePair<string, string>> fieldParams)
            : base(fieldParams)
        {
        }
        
        /// <summary>
        /// Parse the given field value into this field parameters collection
        /// </summary>
        /// <param name="fieldValue"></param>
        public MimeFieldParameters(string fieldValue)
            : this(Read(fieldValue))
        {
        }
        
        /// <summary>
        /// Returns the value for the first parameter with the given name
        /// </summary>
        /// <param name="paramName"></param>
        /// <returns>null if no such parameter</returns>
        public string this[string paramName]
        {
            get
            {
                int index = this.IndexOfFirst(paramName);
                if (index < 0)
                { 
                    return null;
                }
                
                return this[index].Value;
            }
        }
        
        /// <summary>
        /// Deserialize the given field value into this collection
        /// </summary>
        /// <param name="fieldValue"></param>
        public void Deserialize(string fieldValue)
        {
            this.AddRange(Read(fieldValue));
        }
        
        /// <summary>
        /// Returns all values for the given paramName
        /// </summary>
        /// <param name="paramName"></param>
        /// <returns></returns>
        public IEnumerable<string> GetValues(string paramName)
        {
            return (
                from nv in this
                where MimeStandard.Equals(nv.Key, paramName)
                select nv.Value
            );
        }
        
        /// <summary>
        /// Returns the index of the first parameter with this name
        /// </summary>
        /// <param name="paramName"></param>
        /// <returns></returns>
        public int IndexOfFirst(string paramName)
        {
            if (string.IsNullOrEmpty(paramName))
            {
                throw new ArgumentException("paramName");
            }
            for (int i = 0, count = this.Count; i < count; ++i)
            {
                if (MimeStandard.Equals(this[i].Key, paramName))
                {
                    return i;
                }
            }
            
            return -1;
        }
        
        /// <summary>
        /// Serialize parameters to a string
        /// </summary>
        /// <returns></returns>
        public string Serialize()
        {
            return this.Serialize(false);
        }
        
        /// <summary>
        /// Serialize parameters to a string
        /// </summary>
        /// <param name="alwaysQuoteValues"></param>
        /// <returns></returns>
        public string Serialize(bool alwaysQuoteValues)
        {
            StringBuilder output = new StringBuilder();
            Write(output, this, alwaysQuoteValues);
            return output.ToString();
        }
        
        /// <summary>
        /// Serialize parameters to a string
        /// </summary>
        /// <returns></returns>
        public override string ToString()
        {   
            return this.Serialize();
        }   
        
        /// <summary>
        /// See RFC 5322, Section 3.2.3
        /// </summary>
        /// <param name="ch"></param>
        /// <returns></returns>
        public static bool IsSpecialChar(char ch)
        {
            switch(ch)
            {
                default:
                    return false;
                
                case '(':
                case ')':
                case '<':
                case '>':
                case '[':
                case ']':
                case ':':
                case ';':
                case '@':
                case '\\':
                case ',':
                case '.':
                    return true;
            }
        }
        
        /// <summary>
        /// Write field paramters to an output buffer
        /// </summary>
        /// <param name="output"></param>
        /// <param name="fieldParams"></param>
        /// <param name="alwaysQuoteValues"></param>
        public static void Write(StringBuilder output, IEnumerable<KeyValuePair<string, string>> fieldParams, bool alwaysQuoteValues)        
        {
            if (fieldParams == null)
            {
                throw new ArgumentNullException("paramSet");
            }
            
            foreach(KeyValuePair<string, string> param in fieldParams)
            {
                Write(output, param, alwaysQuoteValues);
            }
        }
        
        /// <summary>
        /// Write the next field parameter to an output buffer
        /// </summary>
        /// <param name="output"></param>
        /// <param name="param"></param>
        /// <param name="alwaysQuoteValue"></param>
        public static void Write(StringBuilder output, KeyValuePair<string, string> param, bool alwaysQuoteValue)
        {
            if (output == null)
            {
                throw new ArgumentNullException("output");
            }
            
            if (output.Length > 0)
            {
                output.Append(MimeFieldParameters.ParameterSeparator);
            }
            if (!string.IsNullOrEmpty(param.Key))
            {
                output.Append(param.Key);
                output.Append(MimeFieldParameters.NameValueSeparator);            
            }
            if (string.IsNullOrEmpty(param.Value))
            {
                return;
            }
            
            bool needsQuotes = (alwaysQuoteValue || ContainsSpecialChars(param.Value));
            if (needsQuotes)
            {
                output.Append(MailStandard.DQUOTE);
            }
            string value = EscapeQuotes(param.Value);
            output.Append(value);
            if (needsQuotes)
            {
                output.Append(MailStandard.DQUOTE);
            }
        }
        
        /// <summary>
        /// Read field parameters from the given field value
        /// </summary>
        /// <param name="fieldValue"></param>
        /// <returns></returns>
        public static IEnumerable<KeyValuePair<string,string>> Read(string fieldValue)
        {
            foreach(StringSegment parameter in StringSegment.Split(fieldValue, MimeFieldParameters.ParameterSeparator, MailStandard.DQUOTE))
            {
                yield return ReadNameValue(parameter);      
            }
        }
        
        /// <summary>
        /// Read name value parameters from the given segment
        /// </summary>
        /// <param name="parameterText"></param>
        /// <returns></returns>
        public static KeyValuePair<string, string> ReadNameValue(StringSegment parameterText)
        {
            CharReader reader = new CharReader(parameterText); // Struct. Cheap  
                        int nameStartAt = parameterText.StartIndex;
            string name;
            string value;
            if (!reader.ReadTo(MimeFieldParameters.NameValueSeparator, true))
            {
                // We ran out of segment. Treat the entire segment as a value with no name
                value = reader.Substring(nameStartAt, reader.Position);
                return new KeyValuePair<string,string>(string.Empty, value);
            }
            name = reader.Substring(nameStartAt, reader.Position - 1);
            if (string.IsNullOrEmpty(name))
            {
                throw new MimeException(MimeError.InvalidFieldParameter);
            }            
            value = ReadValue(ref reader);
            
            return new KeyValuePair<string,string>(name.TrimStart(), value);
        }
                        
        static string ReadValue(ref CharReader reader)
        {
            int startAt = reader.Position + 1;
            int endAt;
            reader.ReadTo(MimeFieldParameters.NameValueSeparator, true, MailStandard.DQUOTE);
            // We're forgiving here
            if (reader.Current() == MailStandard.DQUOTE)
            {
                startAt++;
                endAt = reader.Position - 1;
            }
            else
            {
                endAt = reader.Position;
            }
            string value = reader.Substring(startAt, endAt);
            if (string.IsNullOrEmpty(value))
            {
                return string.Empty;
            }
            return UnescapeQuotes(value);
        }
        
        static bool ContainsSpecialChars(string segment)
        {
            if (segment == null)
            {
                throw new ArgumentNullException("segment");
            }
            for (int i = 0; i < segment.Length; ++i)
            {
                if (IsSpecialChar(segment[i]))
                {
                    return true;
                }
            }
            return false;
        }
                
        static string EscapeQuotes(string value)
        {
            return value.Replace("\"", "\\\"");
        }

        static string UnescapeQuotes(string value)
        {
            return value.Replace("\\\"", "\"");
        }
    }
}
