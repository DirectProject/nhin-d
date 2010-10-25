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
using System.Collections.Generic;
using System.Linq;

namespace Health.Direct.Xd
{
    /// <summary>
    /// General extension methods for use with XD* processing.
    /// Extension specific to generating XElements for XD* are in XDMetadataGenerator
    /// </summary>
    public static class Extensions
    {

        /// <summary>
        /// Adds an enumeration of elements to this list
        /// </summary>
        public static void AddAll<T>(this IList<T> target, IEnumerable<T> elements)
        {
            if (elements == null) return;
            foreach (T elt in elements)
            {
                target.Add(elt);
            }
        }


        /// <summary>
        /// Breaks this string into substrings, each of which is length <paramref name="n"/>, except for the last,
        /// which will have the remainder strings.
        /// </summary>
        /// <param name="source">This string, from which we are creating substrings</param>
        /// <param name="n">Number of characters to break on.</param>
        /// <returns>An enumeration of strings that, when joined, recreate the original string, and each of 
        /// which is <paramref name="n"/> characters or shorter</returns>
        public static IEnumerable<string> Break(this string source, int n)
        {
            if (source == null)
            {
                throw new ArgumentException();
            }
            if (n <= 0)
            {
                throw new ArgumentException("Must be positive", "n");
            }
            int len, remaining;
            len = remaining = source.Length;
            int pos = 0;
            while (remaining > 0)
            {
                int substringLength = (pos + n) > len ? len - pos : n;
                yield return source.Substring(pos, substringLength);
                pos += n;
                remaining -= n;
            }
        }

        /// <summary>
        /// Concatenates all the elements of a string enumeration, using the specified separator between each element.
        /// </summary>
        public static string Join(string sep, IEnumerable<string> strings)
        {
            if (strings == null || strings.Count() == 0) return null;
            return strings.Skip(1).Aggregate(strings.First(), (a, s) => a + sep + s);
        }
    }
}