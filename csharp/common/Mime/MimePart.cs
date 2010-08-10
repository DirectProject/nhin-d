﻿/* 
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
using System.ComponentModel;

namespace NHINDirect.Mime
{
	public class EntityPart<T>
	{
		StringSegment m_sourceText; // Text from the source message string: VERBATIM. Necessary for signatures etc.
		string m_text;

		public EntityPart(T type)
		{
            Type = type;
            m_sourceText = StringSegment.Null;
		}

		protected EntityPart(T type, string text)
			: this(type)
		{
			Text = text;
		}

		protected EntityPart(T type, StringSegment segment)
		{
            Type = type;
            m_sourceText = segment;
		}

		public T Type { get; internal set; }

		public StringSegment SourceText
        {
            get
            {
                return m_sourceText;
            }
        }
        
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

		public override string ToString()
		{
            return Text;
		}

		internal void AppendText(string text)
		{
            m_text = Text + text;
		}

		internal virtual void AppendSourceText(StringSegment segment)
		{
            m_sourceText.Union(segment);
		}
	}


	public class MimePart : EntityPart<MimePartType>
    {
		protected MimePart(MimePartType type)
            : base(type)
        {
        }

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
