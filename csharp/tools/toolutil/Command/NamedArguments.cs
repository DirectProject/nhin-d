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

namespace Health.Direct.Config.Tools.Command
{
    /// <summary>
    /// A Dictionary of name value pairs, where value is optional 
    /// </summary>
    public class NamedArguments
    {
        string[] m_rawArgs;
        Dictionary<string, string> m_args;

        public NamedArguments(params string[] args)
        {
            if (args == null)
            {
                throw new ArgumentNullException("args");
            }

            m_rawArgs = args;
            m_args = new Dictionary<string, string>(StringComparer.OrdinalIgnoreCase);
            foreach(KeyValuePair<string, string> nvPair in args.ParseNamedArguments())
            {
                m_args[nvPair.Key] = nvPair.Value;
            }
        }

        public NamedArguments(string parameters)
            : this(parameters.ParseAsCommandLine().ToArray())
        {
        }

        public string this[string name]
        {
            get
            {
                string value;
                if (this.m_args.TryGetValue(name, out value))
                {
                    return value;
                }

                return null;
            }
        }

        public IEnumerable<string> Names
        {
            get
            {
                return this.m_args.Keys;
            }
        }

        public IEnumerable<string> Values
        {
            get
            {
                return this.m_args.Values;
            }
        }

        public string[] Raw
        {
            get
            {
                return this.m_rawArgs;
            }
        }

        public void Set(string name)
        {
            this.Set(name, null);
        }

        public void Set(string name, string value)
        {
            if (string.IsNullOrEmpty(name))
            {
                throw new ArgumentException("value null or empty", "name");
            }

            if (value == null)
            {
                value = string.Empty;
            }

            this.m_args[name] = value;
        }

        public bool Contains(string name)
        {
            return (this[name] != null);
        }

        public string GetRequiredValue(string name)
        {
            string value = this[name];
            if (value == null)
            {
                throw new ArgumentException(string.Format("Missing argument {0}", name));
            }

            if (value.Length == 0)
            {
                throw new ArgumentException(string.Format("Missing value for argument {0}", name));
            }

            return value;
        }

        public T GetRequiredValue<T>(string name)
        {
            string value = this.GetRequiredValue(name);
            return (T)Convert.ChangeType(value, typeof(T));
        }

        public Guid GetRequiredGuid(string name)
        {
            return new Guid(this.GetRequiredValue(name));
        }

        public Guid GetOptionalGuid(string name, Guid defaultValue)
        {
            string value = this[name];
            if (value == null || value.Length == 0)
            {
                return defaultValue;
            }

            return new Guid(this.GetRequiredValue(name));
        }

        public T GetRequiredEnum<T>(string name)
        {
            string value = this.GetRequiredValue(name);
            return (T)Enum.Parse(typeof(T), value, true);
        }

        public string GetOptionalValue(string name, string defaultValue)
        {
            string value = this[name];
            if (value == null || value.Length == 0)
            {
                return defaultValue;
            }

            return value;
        }

        public T GetOptionalValue<T>(string name, T defaultValue)
        {
            string value = this[name];
            if (value == null || value.Length == 0)
            {
                return defaultValue;
            }

            return (T)Convert.ChangeType(value, typeof(T));
        }

        public T GetOptionalEnum<T>(string name, T defaultValue)
        {
            string value = this[name];
            if (value == null || value.Length == 0)
            {
                return defaultValue;
            }

            return (T)Enum.Parse(typeof(T), value, true);
        }

        static char[] ArgSplitParams = new char[] { ',', ';' };
        public string[] GetSubArguments(string name)
        {
            string value = this.GetRequiredValue(name);
            return value.Split(ArgSplitParams, StringSplitOptions.RemoveEmptyEntries);
        }

        public T[] GetSubArgumentsAsEnums<T>(string name)
        {
            string[] subArgs = this.GetSubArguments(name);
            T[] items = new T[subArgs.Length];
            Type type = typeof(T);
            for (int i = 0; i < subArgs.Length; ++i)
            {
                items[i] = (T)Enum.Parse(type, subArgs[i], true);
            }

            return items;
        }

        public bool HasValue(string arg)
        {
            string value = this[arg];
            return (value != null && value.Length > 0);
        }
    }
}