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

namespace Health.Direct.DnsResponder
{
    public abstract class ObjectPoolBase<T>
    {
        int m_maxSize;
        
        public ObjectPoolBase()
            : this(int.MaxValue)
        {
        }
        
        public ObjectPoolBase(int maxSize)
        {
            m_maxSize = maxSize;
        }
                
        public virtual int Count
        {
            get
            {
                return 0;
            }
        }
        
        public virtual int MaxSize
        {
            get
            {
                return m_maxSize;
            }
            set
            {
                if (value <= 0)
                {
                    throw new ArgumentException();
                }
                m_maxSize = value;
            }
        }

        public abstract T Get();
        public abstract void Put(T value);
    }

    public class NullPool<T> : ObjectPoolBase<T>
    {
        public override T Get() { return default(T);}
        public override void Put(T value) { }
    }

    public class ObjectPool<T> : ObjectPoolBase<T>
    {
        protected Stack<T> m_stack;

        public ObjectPool()
            : this(int.MaxValue)
        {
        }
        
        public ObjectPool(int maxSize)
            : base(maxSize)
        {
            m_stack = new Stack<T>();
        }
                
        public override int Count
        {
            get
            {
                return m_stack.Count;
            }
        }
        
        public override T Get()
        {
            if (m_stack.Count > 0)
            {
                return m_stack.Pop();
            }
            
            return default(T);
        }

        public override void Put(T value)
        {
            if (m_stack.Count < this.MaxSize)
            {
                m_stack.Push(value);
            }
        }
    }

    /// <summary>
    /// The weak pool lives off a Weak References and can be GC'd safely under memory pressure
    /// </summary>
    public class WeakPool<T> : ObjectPoolBase<T>
        where T : ObjectPoolBase<T>, new()
    {
        WeakReference m_inner;
        
        public WeakPool(int maxSize)
            : base(maxSize)
        {
            m_inner = new WeakReference(Create());
        }
        
        public override T Get()
        {
            T pool = Ensure();
            if (pool != null)
            {
                pool.Get();
            }            
            return default(T);
        }

        public override void Put(T value)
        {
            T pool = Ensure();
            if (pool != null)
            {
                pool.Put(value);
            }
        }        
        
        T Ensure()
        {
            T pool = m_inner.Target as T;
            if (pool == null)
            {
                lock(m_inner)
                {
                    pool = m_inner.Target as T;
                    if (pool == null)
                    {
                        pool = Create();
                        m_inner.Target = pool;
                    }                
                }
            }
            
            return pool;
        }
        
        T Create()
        {
            T pool = new T();
            pool.MaxSize = this.MaxSize;
            return pool;
        }
    }
}