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
using System.Linq;
using System.IO;

namespace Health.Direct.Config.Tools.Command
{
    /// <summary>
    /// Useful extensions that help with making simple & useful command line Console apps. 
    /// Provide simple extensions on arrays of strings (arguments)
    /// No major complex learning curve. Goal: keep it REALLY simple.
    /// 
    /// One set of methods operate directly on argument arrays.
    /// Another translates the array into a NameArguments object
    /// </summary>
    public static class CommandExtensions
    {
        //
        // If array is not big enough, returns null
        //
        public static string GetValueOrNull(this string[] args, int indexAt)
        {
            if (indexAt < 0 || indexAt >= args.Length)
            {
                return null;
            }
            
            return args[indexAt];
        }
        
        internal static bool IsNullOrEmpty(this Array args)
        {
            return (args == null || args.Length == 0);
        }
        
        public static string GetRequiredValue(this string[] args, int index)
        {
            string value = args.GetValueOrNull(index);
            if (string.IsNullOrEmpty(value))
            {
                throw new ArgumentException(string.Format("Missing argument at position {0}", index));
            }

            return value;
        }

        public static T GetRequiredValue<T>(this string[] args, int index)
        {
            return (T) Convert.ChangeType(args.GetRequiredValue(index), typeof(T));
        }

        public static T GetRequiredEnum<T>(this string[] args, int index)
        {
            string value = args.GetRequiredValue(index);
            return (T) Enum.Parse(typeof(T), value, true);
        }

        public static Uri GetRequiredUri(this string[] args, int index)
        {
            string value = args.GetRequiredValue(index);
            return new Uri(value);
        }

        public static string GetOptionalValue(this string[] args, int index, string defaultValue)
        {
            string value = args.GetValueOrNull(index);
            if (string.IsNullOrEmpty(value))
            {
                return defaultValue;
            }

            return value;
        }

        public static T GetOptionalValue<T>(this string[] args, int index, T defaultValue)
        {
            string value = args.GetValueOrNull(index);
            if (string.IsNullOrEmpty(value))
            {
                return defaultValue;
            }

            return (T) Convert.ChangeType(value, typeof(T));
        }

        public static T GetOptionalEnum<T>(this string[] args, int index, T defaultValue)
        {
            string value = args.GetValueOrNull(index);
            if (string.IsNullOrEmpty(value))
            {
                return defaultValue;
            }

            return (T) Enum.Parse(typeof(T), value, true);
        }

        /// <summary>
        /// Given a string, turn it into a file name by replacing disallowed characters with '_'
        /// </summary>
        /// <param name="fileName">input file name</param>
        /// <returns>legalized file name</returns>
        public static string ToFileName(this string fileName)
        {
            char[] illegals = Path.GetInvalidFileNameChars();
            char[] legalName = null;
            for (int s = 0; s < fileName.Length; ++s)
            {
                char ch = fileName[s];
                for (int i = 0; i < illegals.Length; ++i)
                {
                    if (ch == illegals[i])
                    {
                        legalName = legalName ?? fileName.ToCharArray();
                        legalName[s] = '_';
                        break;
                    }
                }
            }

            if (legalName != null)
            {
                fileName = new string(legalName);
            }

            return fileName;
        }

        static readonly char[] Whitespace = new[] { ' ', '\t', '\r', '\n' };
        static readonly char[] Quotes = new[] { '"' };
        
        /// <summary>
        /// Parse the string as though it was a command line, handling quoted arguments correctly.
        /// </summary>
        public static IEnumerable<string> ParseAsCommandLine(this string input)
        {
            if (string.IsNullOrEmpty(input))
            {
                yield break;
            }  

            int index = 0;
            while (index < input.Length)
            {
                int tokenStartAt = input.SkipOver(index, Whitespace);
                if (tokenStartAt < 0)
                {
                    break;
                }

                char[] delimiter;
                if (Quotes.Contains(input[tokenStartAt]))
                {
                    tokenStartAt++;
                    delimiter = Quotes;
                }
                else
                {
                    delimiter = Whitespace;
                }

                int tokenEndAt  = input.SkipTo(tokenStartAt, delimiter);
                if (tokenEndAt < 0)
                {
                    tokenEndAt = input.Length;
                }
                int length = tokenEndAt - tokenStartAt;
                if (length > 0)
                {
                    yield return input.Substring(tokenStartAt, length);
                }
                
                index = tokenEndAt + 1;
            }
        }

        /// <summary>
        /// Turn the given array of args into a dictionary of named arguments using the following rules
        ///  Any text prefixed with '-' is treated as a command with an associated value. The next string is the command's value
        ///  Any strings that are not part of a name,value pair are treated as names with no value. 
        /// </summary>
        /// <param name="rawArgs">arguments</param>
        /// <returns>A set of named arguments</returns>
        public static NamedArguments NamedArguments(this string[] rawArgs)
        {
            return new NamedArguments(rawArgs);
        }

        internal static IEnumerable<KeyValuePair<string, string>> ParseNamedArguments(this string[] rawArgs)
        {
            Stack<string> names = new Stack<string>();
            string name;

            for (int i = 0; i < rawArgs.Length; ++i)
            {
                string value = rawArgs[i].Trim();
                //
                // Arguments that have an associated value have their names prepended with a -
                //
                if (value[0] == '-')
                {
                    names.Push(value);  // Save the name, awaiting its value
                }
                else if (names.Count > 0)
                {
                    name = names.Pop();
                    yield return new KeyValuePair<string, string>(name, value);
                    while (names.Count > 0)
                    {
                        // Some left over names, which clearly no longer can have associated values
                        yield return new KeyValuePair<string, string>(names.Pop(), string.Empty);
                    }
                }
                else
                {
                    // A name with no value..
                    yield return new KeyValuePair<string, string>(value, string.Empty);
                }
            }

            // Any un-popped names become arguments
            while (names.Count > 0)
            {
                yield return new KeyValuePair<string, string>(names.Pop(), string.Empty);
            }
        }        

        static int SkipOver(this string source, int startAt, char[] chars)
        {
            for (int i = startAt; i < source.Length; ++i)
            {
                if (!chars.Contains(source[i]))
                {
                    return i;
                }
            }

            return -1;
        }

        static int SkipTo(this string source, int startAt, char[] chars)
        {
            return source.IndexOfAny(chars, startAt);
        }
    }
}