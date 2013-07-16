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
using System.Collections.Generic;

namespace Health.Direct.Common.Mime
{
    /// <summary>A set of lightweight MIME and email entity parsing routines.</summary>
    /// <remarks>
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
    /// </remarks>
    public static class MimeParser
    {
        /// <summary>
        /// Reads the supplied text returning a newly created instance of a <see cref="MimeEntity"/> or subclass 
        /// </summary>
        /// <typeparam name="T">The <see cref="MimeEntity"/> or subclass type to return.</typeparam>
        /// <param name="entityText">The text to parse.</param>
        /// <returns>The parsed <see cref="MimeEntity"/> or subclass.</returns>
        public static T Read<T>(string entityText)
            where T : MimeEntity, new()
        {
            return Read<T>(new StringSegment(entityText));
        }

        /// <summary>
        /// Reads the supplied <see cref="StringSegment"/> returning a newly created instance of a <see cref="MimeEntity"/> or subclass 
        /// </summary>
        /// <typeparam name="T">The <see cref="MimeEntity"/> or subclass type to return.</typeparam>
        /// <param name="entityText">The segment text to parse.</param>
        /// <returns>The parsed <see cref="MimeEntity"/> or subclass.</returns>
        public static T Read<T>(StringSegment entityText)
            where T : MimeEntity, new()
        {
            T entity = new T();

            foreach (MimePart part in ReadMimeParts(entityText))
            {
                switch (part.Type)
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

        /// <summary>
        /// Parses a header string
        /// </summary>
        /// <param name="headerText">The header text to parse.</param>
        /// <returns>A <see cref="KeyValuePair{T, T}"/> where the key is the header name, and the value is the header value.</returns>
        public static KeyValuePair<string, string> ReadNameValue(string headerText)
        {
            int separatorPosition = IndexOf(headerText, MimeStandard.NameValueSeparator);
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
                return new KeyValuePair<string, string>(name, string.Empty);
            }

            string value = headerText.Substring(separatorPosition).TrimStart();

            return new KeyValuePair<string, string>(name, value);
        }

        /// <summary>
        /// Parses the supplied string providing a valid block of headers into an enumeration of <see cref="Header"/> instances.
        /// </summary>
        /// <param name="entity">The header block string to parse.</param>
        /// <returns>An enumeration of <see cref="Header"/> instances supplying the parsed headers</returns>
        public static IEnumerable<Header> ReadHeaders(string entity)
        {
            return ReadHeaders(new StringSegment(entity));
        }

        /// <summary>
        /// Parses the supplied <see cref="StringSegment"/> providing a valid block of headers into an enumeration of <see cref="Header"/> instances.
        /// </summary>
        /// <param name="entity">The header block to parse.</param>
        /// <returns>An enumeration of <see cref="Header"/> instances supplying the parsed headers</returns>
        public static IEnumerable<Header> ReadHeaders(StringSegment entity)
        {
            return ReadHeaders(ReadLines(entity));
        }

        /// <summary>
        /// Parse each line in lines as a <see cref="Header"/>.
        /// </summary>
        /// <param name="lines">The lines to parse.</param>
        /// <returns>The parsed enumeration of <see cref="Header"/> instances.</returns>
        public static IEnumerable<Header> ReadHeaders(IEnumerable<StringSegment> lines)
        {
            if (lines == null)
            {
                throw new ArgumentNullException("lines");
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

        
        /// <summary>
        /// Parses a string supplying a <c>multipart</c> body returning contituent parts.
        /// </summary>
        /// <param name="entity">The <c>multipart</c> body</param>
        /// <param name="boundary">The <c>multipart</c> boundary string</param>
        /// <returns>The constituent body parts.</returns>
        public static IEnumerable<MimePart> ReadBodyParts(string entity, string boundary)
        {
            return ReadBodyParts(new StringSegment(entity), boundary);
        }

        /// <summary>
        /// Parses a <see cref="StringSegment"/> supplying a <c>multipart</c> body, returning contituent parts.
        /// </summary>
        /// <param name="entity">The <c>multipart</c> body</param>
        /// <param name="boundary">The <c>multipart</c> boundary string</param>
        /// <returns>The constituent body parts.</returns>
        public static IEnumerable<MimePart> ReadBodyParts(StringSegment entity, string boundary)
        {
            return ReadBodyParts(ReadLines(entity), boundary);
        }

        /// <summary>
        /// Parses set of <see cref="StringSegment"/> supplying the individual lines of a <c>multipart</c> body, returning contituent parts.
        /// </summary>
        /// <param name="bodyLines">The <c>multipart</c> body lines</param>
        /// <param name="boundary">The <c>multipart</c> boundary string</param>
        /// <returns>The constituent body parts.</returns>

        public static IEnumerable<MimePart> ReadBodyParts(IEnumerable<StringSegment> bodyLines, string boundary)
        {
            if (bodyLines == null)
            {
                throw new ArgumentNullException("bodyLines");
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
        
        /// <summary>
        /// Tests the supplied line against <paramref name="boundary"/> to see if the line is the multipart boundary
        /// </summary>
        /// <param name="line">The line to test</param>
        /// <param name="boundary">The multipart boundary string.</param>
        /// <returns><c>true</c> if the line is the multipart boundary, <c>false</c> if not.</returns>
        public static bool IsBoundary(StringSegment line, string boundary)
        {
            int length = line.Length;
            return (length > 1
                    && length >= (boundary.Length + 2)
                    && line[0] == MimeStandard.BoundaryChar
                    && line[1] == MimeStandard.BoundaryChar 
                    && (string.Compare(boundary, 0, line.Source, line.StartIndex + 2, boundary.Length) == 0));
        }
        
        /// <summary>
        /// Tests the <paramref name="line"/> to see if it represents the last multipart boundary separator
        /// </summary>
        /// <param name="line">The line to test</param>
        /// <returns><c>true</c> if the line is the last multipart boundary separator, <c>false</c> otherwise.</returns>
        public static bool IsBoundaryEnd(StringSegment line)
        {
            int length = line.Length;
            return (length > 4
                    && line[length - 1] == MimeStandard.BoundaryChar
                    && line[length - 2] == MimeStandard.BoundaryChar);
        }
        
        /// <summary>
        /// Parses the supplied <paramref name="entity"/> into constituent <see cref="MimePart"/> instances
        /// </summary>
        /// <param name="entity">The <see cref="string"/> to parse</param>
        /// <returns>An enumeration of the constituent <see cref="MimePart"/> instances parsed from the <paramref name="entity"/></returns>
        public static IEnumerable<MimePart> ReadMimeParts(string entity)
        {
            return ReadMimeParts(new StringSegment(entity));
        }

        /// <summary>
        /// Parses the supplied <paramref name="entity"/> into constituent <see cref="MimePart"/> instances
        /// </summary>
        /// <remarks>
        /// This is the main parser interface. The <see cref="MimePart"/> instances expected to be returned are <see cref="Header"/> (one for each header), and <see cref="Body"/>
        /// </remarks>
        /// <param name="entity">The <see cref="StringSegment"/> to parse</param>
        /// <returns>An enumeration of the constituent <see cref="MimePart"/> instances parsed from the <paramref name="entity"/></returns>
        public static IEnumerable<MimePart> ReadMimeParts(StringSegment entity)
        {
            return ReadMimeParts(ReadLines(entity));
        }

        /// <summary>
        /// Parses the supplied <paramref name="lines"/> into constituent <see cref="MimePart"/> instances
        /// </summary>
        /// <remarks>
        /// This is the main parser interface. The <see cref="MimePart"/> instances expected to be returned are <see cref="Header"/> (one for each header), and <see cref="Body"/>
        /// </remarks>
        /// <param name="lines">The enumeration of <see cref="StringSegment"/> lines to parse</param>
        /// <returns>An enumeration of the constituent <see cref="MimePart"/> instances parsed from the <paramref name="entity"/></returns>
        public static IEnumerable<MimePart> ReadMimeParts(IEnumerable<StringSegment> lines)
        {
            if (lines == null)
            {
                throw new ArgumentNullException("lines");
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
        
        /// <summary>
        /// Breaks the supplied <paramref name="entity"/> into constituent lines by CRLF.
        /// </summary>
        /// <param name="entity">The entity to parse into lines.</param>
        /// <returns>An enumeration of <see cref="StringSegment"/> instances, one for each parsed line</returns>
        public static IEnumerable<StringSegment> ReadLines(string entity)
        {
            return ReadLines(new StringSegment(entity));
        }

        /// <summary>
        /// Breaks the supplied <paramref name="entity"/> into constituent lines by CRLF.
        /// </summary>
        /// <param name="entity">The entity to parse into lines.</param>
        /// <returns>An enumeration of <see cref="StringSegment"/> instances, one for each parsed line</returns>
        public static IEnumerable<StringSegment> ReadLines(StringSegment entity)
        {
            CharReader reader = new CharReader(entity);
            int startIndex = reader.Position + 1;
            int endIndex = startIndex - 1;
            char ch;

            while ((ch = reader.Read()) != CharReader.EOF)
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

                        yield return reader.GetSegment(startIndex, endIndex);

                        startIndex = reader.Position + 1;
                        endIndex = reader.Position;
                        break;
                }
            }
        
            if (endIndex >= 0)
            {
                yield return reader.GetSegment(startIndex, endIndex);                
            }
        }
        
        /// <summary>
        /// Advances the start position of <paramref name="text"/> to the first non-whitespace position
        /// </summary>
        /// <param name="text">The <see cref="StringSegment"/> potentially containing initial whitepace</param>
        /// <returns>A <see cref="StringSegment"/> with no intial whitespace (possibly an empty segment if the <paramref name="text"/> was empty or all whitespace)</returns>
        public static StringSegment SkipWhitespace(StringSegment text)
        {
            CharReader reader = new CharReader(text);
            char ch;
            while ((ch = reader.Read()) != CharReader.EOF && MimeStandard.IsWhitespace(ch))
            {
                // quick skip
            }

            return new StringSegment(text.Source, reader.Position, text.EndIndex);
        }

        /// <summary>
        /// Returns the first index of the supplied <paramref name="ch"/> <c>char</c> in <paramref name="text"/>
        /// </summary>
        /// <param name="text">The <see cref="string"/> to search</param>
        /// <param name="ch">The <c>char</c> to test for</param>
        /// <returns>The zero-based index of the first instance of <paramref name="ch"/>, or -1 if no instances found/</returns>
        public static int IndexOf(string text, char ch)
        {
            CharReader reader = new CharReader(text);

            if (reader.ReadTo(ch, false))
            {
                return reader.Position;
            }

            return -1;
        }
    }
}