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
using System.Collections.ObjectModel;
using System.Collections.Generic;

namespace Health.Direct.Common.Collections
{
    /// <summary>
    /// Change types for event observation
    /// </summary>
    public enum CollectionChangeType
    {
        /// <summary>
        /// An item has been added
        /// </summary>
        Add,
        /// <summary>
        /// An item has been removed
        /// </summary>
        Remove,
        /// <summary>
        /// An item has been changed
        /// </summary>
        Update
    }

    /// <summary>
    /// Extends Collection, but makes sure you cannot put Null elements in
    /// Additionally, this collection is observable
    /// </summary>
    public class ObjectCollection<T> : Collection<T>
    {        

        /// <summary>
        /// Initializes a new empty instance.
        /// </summary>
        public ObjectCollection()
        {
        }
        
        /// <summary>
        /// Initializes a new instance containing the provided items.
        /// </summary>
        /// <param name="items">An enumeration of items with which to initialize this instance</param>
        public ObjectCollection(IEnumerable<T> items)
        {
            this.Add(items);
        }
        
        /// <summary>
        /// An event sink to register for observing container item changes.
        /// </summary>
        public event Action<CollectionChangeType, T> Changed;
        
        /// <summary>
        /// Adds the provided items to this instance.
        /// </summary>
        /// <param name="items">An enumeration of items to add to this instance</param>
        public void Add(IEnumerable<T> items)
        {
            if (items == null)
            {
                throw new ArgumentNullException("items");
            }
            
            foreach(T item in items)
            {
                this.Add(item);
            }
        }
        
        /// <summary>
        /// Remove all items matching the given filter
        /// </summary>
        /// <example>
        /// <code>
        /// ObjectCollection&lt;int&gt; coll = new ObjectCollection&lt;int&gt;();
        /// coll.Add(1);
        /// coll.Add(2);
        /// coll.Add(3);
        /// coll.Remove(x => i % 2 == 0); // Removes 2
        /// </code>
        /// </example>
        /// <param name="filter">The filter taking items and returning <c>bool</c></param>
        public void Remove(Predicate<T> filter)
        {
            this.Remove(filter, true);
        }
        
        /// <summary>
        /// Remove all items EXCEPT those that match this filter
        /// </summary>
        /// <param name="filter">The filter delegat taking items and returning <c>bool</c></param>
        public void RemoveExcept(Predicate<T> filter)
        {
            this.Remove(filter, false);
        }

        void Remove(Predicate<T> filter, bool filterResult)
        {
            if (filter == null)
            {
                throw new ArgumentNullException("filter");
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

        /// <summary>
        /// Inserts the provided item at the provided index, moving items at and past the insertion
        /// point down.
        /// </summary>
        /// <param name="index">The index where the new item will appear.</param>
        /// <param name="item">The item to insert.</param>
        protected override void InsertItem(int index, T item)
        {
            if (item == null)
            {
                throw new ArgumentNullException("item");
            }
            base.InsertItem(index, item);
            this.Notify(CollectionChangeType.Add, item);
        }

        /// <summary>
        /// Sets an item at an index position.
        /// </summary>
        /// <param name="index">The index position to update.</param>
        /// <param name="item">The item to update at the index position.</param>
        protected override void SetItem(int index, T item)
        {
            if (item == null)
            {
                throw new ArgumentNullException("item");
            }
            
            base.SetItem(index, item);
            this.Notify(CollectionChangeType.Update, item);
        }

        /// <summary>
        /// Removes the item at the specified index position (moving subsequent items up)
        /// </summary>
        /// <param name="index">The index position for which to remove the item.</param>
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