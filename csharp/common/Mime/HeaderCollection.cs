using System;
using System.Collections.Generic;
using System.Collections.ObjectModel;
using System.Linq;
using System.Text;
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
                int i = this.IndexOf(name);
                if (i < 0)
                {
                    return null;
                }
                
                return this[i];
            }  
            set
            {
                int currentIndex = this.IndexOf(name);
                if (currentIndex < 0)
                {
                    //
                    // Not found. New Header
                    //
                    VerifyName(name, value);
                    this.Add(value);
                    return;
                }
                
                if (value == null)
                {
                    //
                    // Null value means remove existing header
                    //
                    this.RemoveAt(currentIndex);
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
                throw new ArgumentException();
            }
            
            for (int i = 0, count = this.Count; i < count; ++i)
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
            this.Add(new Header(name, value));
        }

        public void Add(KeyValuePair<string, string> value)
        {
            this.Add(value.Key, value.Value);
        }
        
        public void Add(IEnumerable<KeyValuePair<string, string>> headers)
        {
            if (headers == null)
            {
                throw new ArgumentNullException();
            }

            foreach (KeyValuePair<string, string> pair in headers)
            {
                this.Add(pair);
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
                throw new ArgumentNullException();
            }
            
            foreach(Header header in headers)
            {
                this[header.Name] = header;
            }
        }
        
        public void AddUpdate(IEnumerable<KeyValuePair<string, string>> headers)
        {
            if (headers == null)
            {
                throw new ArgumentNullException();
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
                throw new ArgumentNullException();
            }

            foreach (Header header in source)
            {
                if (filter == null || filter(header))
                {
                    this.Add(header);
                }
            }
        }

        public void CopyFrom(IEnumerable<Header> source, string[] headerNames)
        {
            if (source == null)
            {
                throw new ArgumentNullException();
            }

            foreach (Header header in source)
            {
                if (headerNames == null || headerNames.Contains<string>(header.Name, MimeStandard.Comparer))
                {
                    this.Add(header);
                }
            }
        }
        
        public void Set(Header header)
        {
            if (header == null)
            {
                throw new ArgumentNullException();
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
            int index = this.IndexOf(name);
            if (index < 0)
            {
                this.Add(name, value);
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
            HeaderCollection copy = new HeaderCollection();
            for (int i = 0, count = this.Count; i < count; ++i)
            {
                copy.Add(this[i].Clone());
            }
            
            return copy;
        }
        
        public HeaderCollection SelectMimeHeaders()
        {
            return new HeaderCollection(this.MimeHeaders);
        }

        public HeaderCollection SelectNonMimeHeaders()
        {
            return new HeaderCollection(this.NonMimeHeaders);
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
