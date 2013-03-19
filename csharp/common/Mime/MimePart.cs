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

namespace Health.Direct.Common.Mime
{
    /// <summary>
    /// Generic class for MIME and other entities, parameterized, generally by entity type enumeration.
    /// </summary>
    /// <typeparam name="T">The subtype, generally an enumeration type.</typeparam>
    public class EntityPart<T>
    {
        StringSegment m_sourceText; // Text from the source message string: VERBATIM. Necessary for signatures etc.
        string m_text;

        /// <summary>
        /// Intializes an instance with the associated entity type.
        /// </summary>
        /// <remarks>See <see cref="MimePart"/> for concrete parameterized instances.</remarks>
        /// <param name="type">The entity type for this instance.</param>
        public EntityPart(T type)
        {
            Type = type;
            m_sourceText = StringSegment.Null;
        }

        /// <summary>
        /// Initalizes an instance with entity type and raw entity text.
        /// </summary>
        /// <param name="type">The entity type for this instance.</param>
        /// <param name="text">The raw entity text for this entity.</param>
        protected EntityPart(T type, string text)
            : this(type)
        {
            Text = text;
        }

        /// <summary>
        /// Initalizes an instance with entity type and raw entity text as a <see cref="StringSegment"/>
        /// </summary>
        /// <param name="type">The entity type for this instance.</param>
        /// <param name="segment">The raw entity text for this entity as a <see cref="StringSegment"/>.</param>
        protected EntityPart(T type, StringSegment segment)
        {
            Type = type;
            m_sourceText = segment;
        }

        /// <summary>
        /// Gets the type for this entity.
        /// </summary>
        public T Type { get; internal set; }

        /// <summary>
        /// Gets the raw text for this instance as a <see cref="StringSegment"/>
        /// </summary>
        /// <value>A <see cref="StringSegment"/> encompassing the entire entity text.</value>
        public StringSegment SourceText
        {
            get
            {
                return m_sourceText;
            }
        }
        
        /// <summary>
        /// Gets the raw text for this instance
        /// </summary>
        public virtual string Text
        {
            get
            {
                if (m_text == null)
                {
                    m_text = m_sourceText.ToString();
                }

                return m_text;
            }
            protected set
            {
                if (value == null)
                {
                    throw new ArgumentNullException("value");
                }
            
                m_sourceText = new StringSegment(value);
                m_text = null; 
            }
        }

        /// <summary>
        /// Returns a default string representation of this entity
        /// </summary>
        /// <returns>The entity text as a <see cref="string"/></returns>
        public override string ToString()
        {
            return Text;
        }

        /// <summary>
        /// Appends text to the text for this entity.
        /// </summary>
        /// <param name="text">The text to append to this entity.</param>
        internal void AppendText(string text)
        {
            m_text = Text + text;
        }
        
        /// <summary>
        /// Expands the source segment for this entity
        /// </summary>
        internal virtual void AppendSourceText(StringSegment segment)
        {
            m_sourceText.Union(segment);
        }
        
        internal void SetTextTo(string text)
        {
            m_text = text;
        }
    }

    /// <summary>
    /// Represents MIME entity parts
    /// </summary>
    /// <remarks>MIME entity parts are the decomposed subparts of a MIME entity. <see cref="MimePartType"/>
    /// for the concrete parts.</remarks>
    public class MimePart : EntityPart<MimePartType>
    {
        /// <summary>
        /// Intializes an instance with the supplied <paramref name="type"/>
        /// </summary>
        /// <param name="type">The <see cref="MimePartType"/> of this instance.</param>
        protected MimePart(MimePartType type)
            : base(type)
        {
        }

        /// <summary>
        /// Intializes an instance with the supplied <paramref name="type"/> and body string.
        /// </summary>
        /// <param name="type">The <see cref="MimePartType"/> of this instance.</param>
        /// <param name="text">The body text.</param>
        protected MimePart(MimePartType type, string text)
            : base(type, text)
        {
        }

        internal MimePart(MimePartType type, StringSegment segment)
            : base(type, segment)
        {
        }
    }
}