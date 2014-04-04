/* 
 Copyright (c) 2010, Direct Project
 All rights reserved.

 Authors:    
    Joe Shook       jshook@kryptiq.com
 
Redistribution and use in source and binary forms, with or without modification, are permitted provided that the following conditions are met:

Redistributions of source code must retain the above copyright notice, this list of conditions and the following disclaimer.
Redistributions in binary form must reproduce the above copyright notice, this list of conditions and the following disclaimer in the documentation and/or other materials provided with the distribution.
Neither the name of The Direct Project (directproject.org) nor the names of its contributors may be used to endorse or promote products derived from this software without specific prior written permission.
THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 
*/

using System;
using Health.Direct.Config.Store;


namespace Health.Direct.Config.Service
{
   public class MonitorService : ConfigServiceBase, IMdnMonitor
    {
       public void Start(Mdn[] mdns)
       {
           try
           {
               Store.Mdns.Start(mdns);
           }
           catch (Exception ex)
           {
               throw CreateFault("Start", ex);
           }
       }

       public void Update(Mdn mdn)
       {
           try
           {
               Store.Mdns.Update(mdn);
           }
           catch (Exception ex)
           {
               throw CreateFault("Update", ex);
           }
       }

       public Mdn[] GetExpiredProcessed(TimeSpan expiredLimit, int maxResults)
       {
           try
           {
               return Store.Mdns.GetExpiredProcessed(expiredLimit, maxResults);
           }
           catch (Exception ex)
           {
               throw CreateFault("GetExpiredProcessed", ex);
           }
       }

       public Mdn[] GetExpiredDispatched(TimeSpan expiredLimit, int maxResults)
       {
           try
           {
               return Store.Mdns.GetExpiredDispatched(expiredLimit, maxResults);
           }
           catch (Exception ex)
           {
               throw CreateFault("GetExpiredProcessed", ex);
           }
       }

       public void SweepTimouts(TimeSpan expiredLimit, int bulkCount)
       {
           try
           {
               Store.Mdns.RemoveTimedOut(expiredLimit, bulkCount);
           }
           catch (Exception ex)
           {
               throw CreateFault("SweepTimouts", ex);
           }
       }

       public Mdn[] EnumerateMdns(string lastMdnName, int maxResults)
       {
           try
           {
               return Store.Mdns.Get(lastMdnName, maxResults);
           }
           catch (Exception ex)
           {
               throw CreateFault("EnumerateMdns", ex);
           }
       }
    }
}
