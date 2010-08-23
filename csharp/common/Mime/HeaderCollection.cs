using System;
using System.Collections.Generic;
using System.Linq;

using NHINDirect.Collections;

namespace NHINDirect.Mime
{
    public class HeaderCollection : ObjectCollection<Header>
    {
        public HeaderCollection()
        {                
        }
        
        public HeaderCollection(IEnumerable<Header> headers)
            : base(headers)
        {
        }
        
        public Header this[string name]
        {
            get
            {
                int i = IndexOf(name);
                if (i < 0)
                {
                    return null;
                }
                
                return this[i];
            }  
            set
            {
                int currentIndex = IndexOf(name);
                if (currentIndex < 0)
                {
                    //
                    // Not found. New Header
                    //
                    VerifyName(name, value);
                    Add(value);
                    return;
                }
                
                if (value == null)
                {
                    //
                    // Null value means remove existing header
                    //
                    RemoveAt(currentIndex);
                }
                else
                {
                    VerifyName(name, value);
                    this[currentIndex] = value;
                }
            }          
        }
        
        public IEnumerable<Header> MimeHeaders
        {
            get
            {
                return from header in this
                                where ( MimeStandard.StartsWith(header.Name, MimeStandard.HeaderPrefix))
                                select header;                
            }
        }
        
        public IEnumerable<Header> NonMimeHeaders
        {
            get
            {
                return from header in this
                       where (!MimeStandard.StartsWith(header.Name, MimeStandard.HeaderPrefix))
                       select header;
            }
        }
        
        public int IndexOf(string name)
        {
            if (string.IsNullOrEmpty(name))
            {
                throw new ArgumentException("name was null or empty", "name");
            }
            
            for (int i = 0, count = Count; i < count; ++i)
            {
                if (MimeStandard.Equals(this[i].Name, name))
                {
                    return i;
                }
            }            
            
            return -1;
        }
        
        public void Add(string name, string value)
        {
            Add(new Header(name, value));
        }

        public void Add(KeyValuePair<string, string> value)
        {
            Add(value.Key, value.Value);
        }
        
        public void Add(IEnumerable<KeyValuePair<string, string>> headers)
        {
            if (headers == null)
            {
                throw new ArgumentNullException("headers");
            }

            foreach (KeyValuePair<string, string> pair in headers)
            {
                Add(pair);
            }
        }
                
        public void AddUpdate(string name, string value)
        {
            this[name] = new Header(name, value);
        }
        
        public void AddUpdate(IEnumerable<Header> headers)
        {
            if (headers == null)
            {
                throw new ArgumentNullException("headers");
            }
            
            foreach (Header header in headers)
            {
                this[header.Name] = header;
            }
        }
        
        public void AddUpdate(IEnumerable<KeyValuePair<string, string>> headers)
        {
            if (headers == null)
            {
                throw new ArgumentNullException("headers");
            }

            foreach (KeyValuePair<string, string> header in headers)
            {
                this[header.Value] = new Header(header);
            }
        }

        public void CopyFrom(IEnumerable<Header> source, Predicate<Header> filter)
        {
            if (source == null)
            {
                throw new ArgumentNullException("source");
            }

            foreach (Header header in source)
            {
                if (filter == null || filter(header))
                {
                    Add(header);
                }
            }
        }

        public void CopyFrom(IEnumerable<Header> source, string[] headerNames)
        {
            if (source == null)
            {
                throw new ArgumentNullException("source");
            }

            foreach (Header header in source)
            {
                if (headerNames == null || headerNames.Contains(header.Name, MimeStandard.Comparer))
                {
                    Add(header);
                }
            }
        }
        
        public void Set(Header header)
        {
            if (header == null)
            {
                throw new ArgumentNullException("header");
            }
            
            this[header.Name] = header;
        }
        
        //
        // Locates the header with the give name. If it exists, return its value.
        // Else return null
        //
        public string GetValue(string headerName)
        {
            Header header = this[headerName];
            if (header == null)
            {
                return null;
            }
            
            return header.Value;
        }
        
        public void SetValue(string name, string value)
        {
            int index = IndexOf(name);
            if (index < 0)
            {
                Add(name, value);
                return;
            }

            this[index] = new Header(name, value);
        }
        
        public HeaderCollection Clone()
        {
            return new HeaderCollection(this);
        }
        
        /// <summary>
        /// Does a deep clone - also clones each header object
        /// </summary>
        /// <returns></returns>
        public HeaderCollection DeepClone()
        {
            var copy = new HeaderCollection();
			foreach (var header in this)
            {
                copy.Add(header.Clone());
            }
            
            return copy;
        }
        
        public HeaderCollection SelectMimeHeaders()
        {
            return new HeaderCollection(MimeHeaders);
        }

        public HeaderCollection SelectNonMimeHeaders()
        {
            return new HeaderCollection(NonMimeHeaders);
        }

    	static void VerifyName(string name, Header header)
        {
            if (!MimeStandard.Equals(name, header.Name))
            {
                throw new MimeException(MimeError.InvalidHeader);
            }
        }
    }
}
