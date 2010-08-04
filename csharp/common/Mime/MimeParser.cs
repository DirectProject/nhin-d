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
using System.Collections.Generic;
using System.Linq;
using System.Text;

namespace NHINDirect.Mime
{   
    /// <summary>
    /// A set of lightweight MIME entity parsing routines. They utilize .NET's "lazy evaluation/LINQ" technique to generate
    /// a set of enumerators of the source - that can then be further composed into higher level constructs
    /// and integrated with LINQ, if required. 
    /// 
    /// One key requirement for this parser is that underlying raw entity text is always retained VERBATIM. 
    /// For cryptography, it is important that post-parsing, we be able to recover the original entity AS IS.
    /// The parser therefore works exclusively with StringSegments - a lightweight struct NOT allocated from the Heap.
    /// StringSegment maintains offsets into the original entity text. 
    /// 
    /// This design has the added benefit of minimizing string allocations (substrings etc), since no strings are
    /// actually allocated. 
    /// 
    /// </summary>
    public static class MimeParser
    {
        public static T Read<T>(string entityText)
            where T : MimeEntity, new()
        {
            return MimeParser.Read<T>(new StringSegment(entityText));
        }
        
        public static T Read<T>(StringSegment entityText)
            where T : MimeEntity, new()
        {
            T entity = new T();
            foreach(MimePart part in MimeParser.ReadMimeParts(entityText))
            {
                switch(part.Type)
                {
                    default:
                        break;
                        
                    case MimePartType.Header:
                        entity.Headers.Add((Header) part);
                        break;
                    
                    case MimePartType.Body:
                        if (entity.HasBody)
                        {
                            throw new MimeException(MimeError.InvalidBody);
                        }
                        entity.Body = (Body) part;
                        break;
                }
            }
            
            return entity;
        }

        public static KeyValuePair<string, string> ReadNameValue(string headerText)
        {
            int separatorPosition = MimeParser.IndexOf(headerText, MimeStandard.NameValueSeparator, true);
            if (separatorPosition < 0)
            {
                throw new MimeException(MimeError.MissingNameValueSeparator);
            }
            if (separatorPosition == 0)
            {
                throw new MimeException(MimeError.InvalidHeader);
            }
            string name = headerText.Substring(0, separatorPosition);
            separatorPosition++;

            if (separatorPosition == headerText.Length)
            {
                throw new MimeException(MimeError.MissingHeaderValue);
            }

            string value = headerText.Substring(separatorPosition).TrimStart();

            return new KeyValuePair<string, string>(name, value);
        }
        
        public static IEnumerable<StringSegment> ReadHeaderParts(string source, char separator)
        {
            return ReadHeaderParts(new StringSegment(source), separator);
        }
        
        public static IEnumerable<StringSegment> ReadHeaderParts(StringSegment source, char separator)
        {
            int startAt = source.StartIndex;
            CharReader reader = new CharReader(source);
            while (reader.ReadTo(separator))
            {
                if (reader.IsPrev(MimeStandard.Escape))
                {
                    continue;
                }
                
                yield return new StringSegment(source.Source, startAt, reader.Position - 1); // STRUCTS - fast
                startAt = reader.Position + 1;
            }            
            
            StringSegment last = new StringSegment(source.Source, startAt, reader.Position);
            if (!last.IsEmpty)
            {
                yield return last;
            }
        }

        public static IEnumerable<Header> ReadHeaders(string entity)
        {
            return MimeParser.ReadHeaders(new StringSegment(entity));
        }

        public static IEnumerable<Header> ReadHeaders(StringSegment entity)
        {
            return MimeParser.ReadHeaders(MimeParser.ReadLines(entity));
        }

        public static IEnumerable<Header> ReadHeaders(IEnumerable<StringSegment> lines)
        {
            if (lines == null)
            {
                throw new ArgumentNullException();
            }

            Header header = null;

            foreach (StringSegment line in lines)
            {
                if (line.IsEmpty)
                {
                    if (header != null)
                    {
                        yield return header;
                        header = null;
                    }
                    //                            
                    // Done with the header section. Onto the Body, so we're done
                    //
                    break;
                }                
                
                if (MimeStandard.IsWhitespace(line[0]))
                {
                    // Line folding. This line belongs to the current header
                    if (header == null)
                    {
                        throw new MimeException(MimeError.InvalidHeader);
                    }
                    header.AppendSourceText(line);
                }
                else
                {
                    if (header != null)
                    {
                        yield return header;
                    }
                    
                    header = new Header(line);
                }
            }

            if (header != null)
            {
                yield return header;
            }
        }

        
        public static IEnumerable<MimePart> ReadBodyParts(string entity, string boundary)
        {
            return MimeParser.ReadBodyParts(new StringSegment(entity), boundary);
        }

        public static IEnumerable<MimePart> ReadBodyParts(StringSegment entity, string boundary)
        {
            return MimeParser.ReadBodyParts(MimeParser.ReadLines(entity), boundary);
        }
        
        public static IEnumerable<MimePart> ReadBodyParts(IEnumerable<StringSegment> bodyLines, string boundary)
        {
            if (bodyLines == null)
            {
                throw new ArgumentNullException();
            }
            
            if (string.IsNullOrEmpty(boundary))
            {
                throw new MimeException(MimeError.MissingBoundarySeparator);
            }
            
            IEnumerator<StringSegment> lineEnumerator = bodyLines.GetEnumerator();
            StringSegment part = StringSegment.Null;
            StringSegment prevLine = StringSegment.Null;
            MimePartType expectedPart = MimePartType.BodyPrologue;
            
            //
            // As per the Multipart Spec:
            //  The boundary delimiter is actually CRLF--boundary
            // So we must maintain a stack (prevLine)..
            //            
            while (expectedPart != MimePartType.BodyEpilogue && lineEnumerator.MoveNext())
            {
                StringSegment line = lineEnumerator.Current;
                if (IsBoundary(line, boundary))
                {
                    if (!part.IsNull)
                    {
                        part.Union(prevLine);
                    }
                    //
                    // When we hit a boundary, we yield the part we've collected so far
                    //
                    MimePartType partType = expectedPart;
                    switch(expectedPart)
                    {
                        default:
                            throw new MimeException(MimeError.InvalidBodySubpart);
                        
                        case MimePartType.BodyPrologue:
                            expectedPart = MimePartType.BodyPart;
                            break;
                        
                        case MimePartType.BodyPart:
                            if (IsBoundaryEnd(line))
                            {   
                                expectedPart = MimePartType.BodyEpilogue;
                            }
                            break;
                    }
                    
                    if (!part.IsNull)
                    {                    
                        yield return new MimePart(partType, part);
                    }
                    
                    prevLine = StringSegment.Null;
                    part = StringSegment.Null;
                }
                else
                {
                    if (!prevLine.IsNull)
                    {
                        part.Union(prevLine);
                    }
                    
                    prevLine = line;
                }
            }
            
            if (expectedPart != MimePartType.BodyEpilogue)
            {
                throw new MimeException(MimeError.InvalidBodySubpart);
            }
            
            // Epilogue
            part = StringSegment.Null; 
            while (lineEnumerator.MoveNext())
            {
                part.Union(lineEnumerator.Current);
            }
            if (!part.IsEmpty)
            {
                yield return new MimePart(MimePartType.BodyEpilogue, part);
            }
        }
        
        static bool IsBoundary(StringSegment line, string boundary)
        {
            int length = line.Length;
            return (length > 1
                    && length >= (boundary.Length + 2)
                    && line[0] == MimeStandard.BoundaryChar
                    && line[1] == MimeStandard.BoundaryChar 
                    && (string.Compare(boundary, 0, line.Source, line.StartIndex + 2, boundary.Length) == 0));
        }
        
        static bool IsBoundaryEnd(StringSegment line)
        {
            int length = line.Length;
            return (length > 4
                    && line[length - 1] == MimeStandard.BoundaryChar
                    && line[length - 2] == MimeStandard.BoundaryChar);
        }
        
        public static IEnumerable<MimePart> ReadMimeParts(string entity)
        {
            return MimeParser.ReadMimeParts(new StringSegment(entity));
        }

        public static IEnumerable<MimePart> ReadMimeParts(StringSegment entity)
        {
            return MimeParser.ReadMimeParts(MimeParser.ReadLines(entity));
        }
        
        public static IEnumerable<MimePart> ReadMimeParts(IEnumerable<StringSegment> lines)
        {
            if (lines == null)
            {
                throw new ArgumentNullException();
            }
            
            MimePartType expectedPartType = MimePartType.Header;
            Header header = null;
            Body body = null;
            
            foreach(StringSegment line in lines)
            {
                switch(expectedPartType)
                {
                    default:
                        throw new MimeException(MimeError.Unexpected);
                        
                    case MimePartType.Header:                        
                        if (line.IsEmpty)
                        {
                            if (header != null)
                            {
                                yield return header;
                                header = null;
                            }
                            
                            yield return new MimePart(MimePartType.HeaderBoundary, line);
                            //                            
                            // Done with the header section. Onto the Body
                            //
                            expectedPartType = MimePartType.Body;
                            break;
                        }

                        if (MimeStandard.IsWhitespace(line[0]))
                        {
                            // Line folding. This line belongs to the current header
                            if (header == null)
                            {
                                throw new MimeException(MimeError.InvalidHeader);
                            }
                            header.AppendSourceText(line);
                            break;
                        }
                        
                        if (header != null)
                        {
                            yield return  header;
                        }
                        header = new Header(line);                        
                        break;
                    
                    case MimePartType.Body:
                        if (body == null)
                        {
                            body = new Body(line);
                        }
                        else
                        {
                            body.AppendSourceText(line);
                        }
                        break;                        
                }
            }
            
            if (header != null)
            {
                yield return header;
            }
            if (body != null)
            {
                yield return body;
            }
        }
        
        public static IEnumerable<StringSegment> ReadLines(string entity)
        {
            return MimeParser.ReadLines(new StringSegment(entity));
        }
        
        public static IEnumerable<StringSegment> ReadLines(StringSegment entity)
        {
            CharReader reader = new CharReader(entity);
            int startIndex = reader.Position + 1;
            int endIndex = startIndex - 1;            
            char ch;
            
            while ((ch = reader.Read()) != char.MinValue)
            {
                switch (ch)
                {
                    default:
                        endIndex = reader.Position;
                        break;

                    case MimeStandard.CR:
                        //
                        // RFC 2822 mandates that CRLF be together always
                        //
                        if (reader.Read() != MimeStandard.LF)
                        {
                            throw new MimeException(MimeError.InvalidCRLF);
                        }
                        yield return new StringSegment(reader.Source, startIndex, endIndex);
                        
                        startIndex = reader.Position + 1;
                        endIndex = reader.Position;
                        break;
                        
                    case MimeStandard.LF:
                        //
                        // No standalone LF allowed
                        //
                        throw new MimeException(MimeError.InvalidCRLF);
                }
            }
        
            if (endIndex >= 0)
            {
                yield return new StringSegment(reader.Source, startIndex, endIndex);                
            }
        }
        
        public static StringSegment SkipWhitespace(StringSegment text)
        {
            CharReader reader = new CharReader(text); // Struct. Typically created on stack, so pretty efficient
            char ch;
            while ((ch = reader.Read()) != char.MinValue && MimeStandard.IsWhitespace(ch));
            
            text.StartIndex = reader.Position;
            return text;  // StringSegment is a struct, so this will return a copy
        }
        
        public static int IndexOfChar(StringSegment text, char ch)
        {
            CharReader reader = new CharReader(text);
            if (reader.ReadTo(ch))
            {
                return reader.Position;
            }

            return -1;
        }

        public static int IndexOf(string text, char ch)
        {
            return IndexOfChar(new StringSegment(text), ch);
        }
        
        public static int IndexOf(string text, char ch, bool isSpecialChar)
        {
            return IndexOf(new StringSegment(text), ch, isSpecialChar);
        }
        
        public static int IndexOf(StringSegment text, char ch, bool isSpecialChar)
        {
            CharReader reader = new CharReader(text);
            while (reader.ReadTo(ch))
            {
                if (!isSpecialChar || !reader.IsPrev(MimeStandard.Escape))
                {
                    return reader.Position;
                }
            }
            
            return -1;
        }
    }
}
