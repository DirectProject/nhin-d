/* 
 Copyright (c) 2010, Direct Project
 All rights reserved.

 Authors:
    Joseph Shook    Joseph.Shook@Surescripts.com
  
Redistribution and use in source and binary forms, with or without modification, are permitted provided that the following conditions are met:

Redistributions of source code must retain the above copyright notice, this list of conditions and the following disclaimer.
Redistributions in binary form must reproduce the above copyright notice, this list of conditions and the following disclaimer in the documentation and/or other materials provided with the distribution.
Neither the name of The Direct Project (directproject.org) nor the names of its contributors may be used to endorse or promote products derived from this software without specific prior written permission.
THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 
*/

using System;
using System.Collections.Generic;
using System.Linq;
using Health.Direct.Common.Extensions;
using Health.Direct.Common.Mail.Notifications;
using Health.Direct.Common.Mime;

namespace Health.Direct.Common.Mail.Context
{
    /// <summary>
    /// A set of lightweight <see cref="ContextParser"/> parsing methods
    /// </summary>
    public static class ContextParser
    {
        /// <summary>
        /// Extract <see cref="Context"/> from a message (Health Content Container)
        /// </summary>
        /// <param name="message"><see cref="Health.Direct.Common.Mail.Message"/></param>
        /// <returns><see cref="Context"/>object</returns>
        public static Context Parse(MimeEntity message, string version)
        {
            if (message == null)
            {
                throw new ArgumentNullException("message");
            }

            var contextBody = message.Body.ToString();
            Metadata metadata;

            try
            {
                metadata = new Metadata(contextBody);

            }
            catch (Exception ex)
            {
                throw new ContextException(ContextError.InvalidContextMetadataFileds, ex);
            }

            VerifyVersion(metadata, version);
            VerifyPatientId(metadata);
            VerifyType(metadata);
            VerifyUse(metadata);
            VerifyPatientId(metadata);
            VerifyEncapsulation(metadata);
            
            var context = new Context(message.Headers, metadata);

            return context;
        }

        
        private static void VerifyVersion(Metadata headers, string version)
        {
            if (!headers.Contains(ContextStandard.Version))
            {
                throw new ContextException(ContextError.MissingVersionIdentifier);
            }

            if (!headers.HasHeader(ContextStandard.Version, "1.0"))
            {
                throw new ContextException(ContextError.UnsupportedVersionIdentifier);
            }
        }
        

        private static void VerifyPatientId(HeaderCollection message)
        {

        }
        private static void VerifyType(HeaderCollection message)
        {

        }
        private static void VerifyUse(HeaderCollection message)
        {
            
        }

        private static void VerifyEncapsulation(HeaderCollection message)
        {
            
        }

        /// <summary>
        /// Parse a <c>patient-id</c> metadata value into <see cref="PatientIdentifier"/>s.
        /// </summary>
        /// <param name="headerValue"></param>
        /// <returns></returns>
        public static IEnumerable<PatientIdentifier> ParsePatientIdentifier(string headerValue)
        {
            string[] parts = SplitField(headerValue, ContextError.InvalidPatientId);

            foreach (var part in parts)
            {
                var patientId = Split(part, new[]{ ':' }, ContextError.InvalidPatientId);

                yield return new PatientIdentifier()
                {
                    PidContext = patientId[0].Trim(),
                    LocalPatientId = patientId[1].Trim()
                };
            }
        }

        /// <summary>
        /// 
        /// </summary>
        /// <param name="headerValue"></param>
        /// <returns></returns>
        public static Type ParseType(string headerValue)
        {
            var typeValue = Split(headerValue, new[] { '/' }, ContextError.InvalidType);
            
            return new Type()
            {
                Category = typeValue[0],
                Action = typeValue[1]
            };
        }
        
        static readonly char[] s_fieldSeparator = { ';' };

        public static string[] SplitField(string value, ContextError error)
        {
            return Split(value, s_fieldSeparator, error);
        }

        internal static string[] Split(string value, char[] separators, ContextError error)
        {
            if (string.IsNullOrEmpty(value))
            {
                throw new ContextException(error);
            }

            string[] parts = value.Split(separators);

            if (parts.IsNullOrEmpty())
            {
                throw new ContextException(error);
            }

            return parts;
        }

        public static Dictionary<string, string> GetPatientAttributes(string value, ContextError error)
        {
            string[] parts = ContextParser.SplitField(value, error);

            var patientAttributes = new Dictionary<string, string>();

            foreach (var part in parts)
            {
                var attribute = Split(part, new[] { '=' }, error);
                patientAttributes.Add(attribute[0].Trim(), attribute[1].Trim());
            }

            return patientAttributes;
        }
    }
}
