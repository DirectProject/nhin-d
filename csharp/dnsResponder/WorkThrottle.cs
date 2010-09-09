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
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading;

namespace DnsResponder
{
    public interface IWorkLoadThrottle
    {
        void Wait();
        void Completed();
    }
    
    public class NullThrottle : IWorkLoadThrottle
    {
        public void Wait()
        {            
        }

        public void Completed()
        {
        }
    }
    
    public class WorkThrottle : IWorkLoadThrottle
    {
        Semaphore m_semaphore;
        
        public WorkThrottle(int maxParallel)
        {
            m_semaphore = new Semaphore(maxParallel, maxParallel);
        }        
        
        public event Action<WorkThrottle, Exception> Error;
        
        public void Wait()
        {
            m_semaphore.WaitOne();
        }
        
        public void Wait(int timeout)
        {
            m_semaphore.WaitOne(timeout);
        }

        public void Completed()
        {
            try
            {
                m_semaphore.Release();
            }
            catch (Exception ex)
            {
                if (this.Error != null)
                {
                    this.Error.SafeInvoke(this, ex);
                }
            }
        }
    }
}
