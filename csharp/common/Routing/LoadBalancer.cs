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
using System.Text;
using System.Threading;
using Health.Direct.Common.Extensions;

namespace Health.Direct.Common.Routing
{
    /// <summary>
    /// A generic, synchronous load balancer object. 
    /// Supports:
    ///  1. Round Robin load balancing
    ///  2. Failure checking 
    ///  3. Fallback to random load balancing if failure during round robin delivery
    ///  
    /// Once initialized, the load balancer object is thread safe. 
    /// 
    /// </summary>
    /// <typeparam name="T">type of data to forward to receivers</typeparam>
    /// <typeparam name="TReceiver">Receiver type</typeparam>
    public class LoadBalancer<T, TReceiver>
    {
        TReceiver[] m_receivers;
        long m_roundRobinCounter = -1;
        
        /// <summary>
        /// Create a new LoadBalancer
        /// </summary>
        /// <param name="receivers">receivers over whom to balance the load</param>
        /// <param name="dataCopier">function that actually copies source data to the receiver</param>
        public LoadBalancer(TReceiver[] receivers, Func<T, TReceiver, bool> dataCopier)
        {
            if (dataCopier == null)
            {
                throw new ArgumentNullException("dataCopier");
            }
            
            this.DataCopier = dataCopier;
            this.Receivers = receivers;
        }
        
        /// <summary>
        /// Configured receivers. 
        /// Do not change this value once routing begins because it is not thread safe
        /// </summary>
        public virtual TReceiver[] Receivers
        {
            get
            {
                return m_receivers;
            }
            set
            {
                m_receivers = value;
                this.Reset();
            }
        }
        
        /// <summary>
        /// The function that does the actual data copying...
        /// </summary>
        public Func<T, TReceiver, bool> DataCopier
        {
            get;
            set;
        }
            
        /// <summary>
        /// Process the given data
        /// </summary>
        /// <param name="data">data to process</param>
        /// <returns>true if successfully processed</returns>
        public bool Process(T data)
        {
            if (m_receivers.IsNullOrEmpty())
            {
                return false;
            }

            if (this.ProcessSingle(data))
            {
                return true;
            }
            //
            // Try to round robin
            //
            int receiverIndex = -1;
            if (this.ProcessRoundRobin(data, out receiverIndex))
            {
                return true;
            }
            //
            // Roundrobin failed. 
            // Try the other share, in case there were only 2 configured
            //
            if (this.ProcessTwin(data, receiverIndex))
            {
                return true;
            }
            //
            // Randomly pick one of the others
            // This will help distribute load over any servers that are actually available
            // We currently don't track "down" servers
            //
            return this.ProcessRandomRobin(data, receiverIndex, out receiverIndex);
        }
        
        /// <summary>
        /// Reset the round robin counter
        /// </summary>
        public void Reset()
        {
            m_roundRobinCounter = -1;
        }
        
        /// <summary>
        /// Round robin load balancing
        /// </summary>
        /// <param name="data">data to forward</param>
        /// <param name="receiverIndex">returns the ordinal of the receiver who received the data</param>
        /// <returns>true if successful</returns>
        public bool ProcessRoundRobin(T data, out int receiverIndex)
        {
            long counter = Interlocked.Increment(ref m_roundRobinCounter);
            receiverIndex = (int)(counter % m_receivers.Length);
            return this.CopyToTarget(data, receiverIndex);
        }

        bool ProcessSingle(T data)
        {
            if (m_receivers.Length == 1)
            {
                return this.CopyToTarget(data, 0);
            }

            return false;
        }

        bool ProcessTwin(T data, int prevReceiverIndex)
        {
            if (m_receivers.Length == 2)
            {
                return this.CopyToTarget(data, (prevReceiverIndex == 0) ? 1 : 0);
            }

            return false;
        }

        bool ProcessRandomRobin(T data, int prevFailedIndex, out int receiverIndex)
        {
            receiverIndex = -1;

            Random random = new Random();
            int startIndex = random.Next(0, m_receivers.Length);
            //
            // Start at a random spot in the list, then find a target that works
            //          
            for (int i = 0; i < m_receivers.Length; ++i)
            {
                receiverIndex = (startIndex + i) % m_receivers.Length;
                //
                // We won't retry the one that failed
                // 
                if (receiverIndex != prevFailedIndex && this.CopyToTarget(data, receiverIndex))
                {
                    return true;
                }
            }

            return false;
        }

        bool CopyToTarget(T data, int receiverIndex)
        {
            return this.Copy(data, m_receivers[receiverIndex]);
        }

        bool Copy(T data, TReceiver receiver)
        {
            try
            {
                return this.DataCopier(data, receiver);
            }
            catch
            {
            }

            return false;
        }        
    }
    
    /// <summary>
    /// A LoadBalancer that forwards data to IReceiver
    /// </summary>
    /// <typeparam name="T">data type to transfer</typeparam>
    public class LoadBalancer<T> : LoadBalancer<T, IReceiver<T>>
    {
        /// <summary>
        /// Create a new load balancer
        /// </summary>
        /// <param name="receivers">The receivers to load balance over</param>
        public LoadBalancer(IReceiver<T>[] receivers)
            : base(receivers, Push)
        {
        }
        
        static bool Push(T data, IReceiver<T> receiver)
        {
            return receiver.Receive(data);
        }
    }
}
