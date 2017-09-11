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
using System.IO;
using System.Linq;
using System.Text;
using MimeKit;

namespace Health.Direct.Context
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
        public static Context Parse(MimePart message, string version)
        {
            if (message == null)
            {
                throw new ArgumentNullException("message");
            }

            Metadata metadata;

            try
            {
                using (var stream = new MemoryStream())
                {
                    message.ContentObject.DecodeTo(stream);
                    stream.Position = 0;
                    metadata = new Metadata(stream);
                }
            }
            catch (Exception ex)
            {
                throw new ContextException(ContextError.InvalidContextMetadataFields, ex);
            }

            VerifyVersion(metadata, version);
            VerifyPatientId(metadata.Headers);
            VerifyType(metadata.Headers);
            VerifyUse(metadata.Headers);
            VerifyPatientId(metadata.Headers);
            VerifyEncapsulation(metadata.Headers);
            
            var context = new Context(
                message.ContentType.MediaType, 
                message.ContentType.MediaSubtype, 
                message.ContentId,
                metadata);

            return context;
        }

        
        private static void VerifyVersion(Metadata metadata, string version)
        {
            if (!metadata.Headers.Contains(ContextStandard.Version))
            {
                throw new ContextException(ContextError.MissingVersionIdentifier);
            }

            if (!metadata.Headers.Select(h => 
                h.Field == ContextStandard.Version &&
                h.Value == "1.0").Any())
            {
                throw new ContextException(ContextError.UnsupportedVersionIdentifier);
            }
        }
        

        private static void VerifyPatientId(HeaderList message)
        {

        }
        private static void VerifyType(HeaderList message)
        {

        }
        private static void VerifyUse(HeaderList message)
        {
            
        }

        private static void VerifyEncapsulation(HeaderList message)
        {
            
        }

        /// <summary>
        /// Parse a <c>patient-id</c> metadata value into <see cref="PatientIdentifier"/>s.
        /// </summary>
        /// <param name="headerValue"></param>
        /// <returns></returns>
        public static IEnumerable<PatientIdentifier> ParsePatientIdentifier(string headerValue)
        {
            var parts = SplitField(headerValue, ContextError.InvalidPatientId);

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

        
        internal static Type ParseType(string headerValue)
        {
            var typeValue = Split(headerValue, new[] { '/' }, ContextError.InvalidType);

            var category = typeValue.First();
            var action = typeValue.Last();

            typeValue.First().AssertEnum<ContextStandard.Type.Category>(ContextError.InvalidType);
            typeValue.Last().AssertEnum<ContextStandard.Type.Action>(ContextError.InvalidType);

            return new Type()
            {
                Category = category,
                Action = action
            };
        }

        
        internal static Encapsulation ParseEncapsulation(string headerValue)
        {
            headerValue.AssertEnum<ContextStandard.Encapsulation.Type>(ContextError.InvalidType);

            return new Encapsulation()
            {
                Type = headerValue
            };
        }

        static readonly char[] s_fieldSeparator = { ';' };

        public static List<string> SplitField(string value, ContextError error)
        {
            return Split(value, s_fieldSeparator, error);
        }

        internal static List<string>Split(string value, char[] separators, ContextError error)
        {
            if (string.IsNullOrEmpty(value))
            {
                throw new ContextException(error);
            }

            var parts = value.Split(separators).Select(v => v.Trim()).ToList();

            if (!parts.Any())
            {
                throw new ContextException(error);
            }

            return parts;
        }

        public static Dictionary<string, string> GetPatientAttributes(string value, ContextError error)
        {
            var parts = ContextParser.SplitField(value, error);

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
