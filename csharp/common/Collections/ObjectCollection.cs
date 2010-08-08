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
using System.Collections.ObjectModel;
using System.Collections.Generic;
using System.Linq;
using System.Text;

namespace NHINDirect.Collections
{
    public enum CollectionChangeType
    {
        Add,
        Remove,
        Update
    }

    /// <summary>
    /// Extends Collection, but makes sure you cannot put Null elements in
    /// Additionally, this collection is observable
    /// </summary>
    public class ObjectCollection<T> : Collection<T>
    {        
        public ObjectCollection()
        {
        }
        
        public ObjectCollection(IEnumerable<T> items)
        {
            this.Add(items);
        }
        
        public event Action<CollectionChangeType, T> Changed;
        
        public void Add(IEnumerable<T> items)
        {
            if (items == null)
            {
                throw new ArgumentNullException();
            }
            
            foreach(T item in items)
            {
                Add(item);
            }
        }
        
        /// <summary>
        /// Remove all items matching the given filter
        /// </summary>
        /// <param name="filter"></param>
        public void Remove(Predicate<T> filter)
        {
            this.Remove(filter, true);
        }
        
        /// <summary>
        /// Remove all items EXCEPT those that match this filter
        /// </summary>
        /// <param name="filter"></param>
        public void RemoveExcept(Predicate<T> filter)
        {
            this.Remove(filter, false);
        }

        void Remove(Predicate<T> filter, bool filterResult)
        {
            if (filter == null)
            {
                throw new ArgumentNullException();
            }

            int i = 0;
            int count = this.Count;
            while (i < count)
            {
                if (filter(this[i]) == filterResult)
                {
                    this.RemoveAt(i);
                    count--;
                }
                else
                {
                    ++i;
                }
            }
        }
                   
        protected override void InsertItem(int index, T item)
        {
            if (item == null)
            {
                throw new ArgumentNullException();
            }
            base.InsertItem(index, item);
            this.Notify(CollectionChangeType.Add, item);
        }

        protected override void SetItem(int index, T item)
        {
            if (item == null)
            {
                throw new ArgumentNullException();
            }
            
            base.SetItem(index, item);
            this.Notify(CollectionChangeType.Update, item);
        }

        protected override void RemoveItem(int index)
        {
            T item = this[index];
            base.RemoveItem(index);
            this.Notify(CollectionChangeType.Remove, item);
        }
        
        void Notify(CollectionChangeType type, T item)
        {
            if (this.Changed != null)
            {
                this.Changed(type, item);
            }
        }        
    }
}
