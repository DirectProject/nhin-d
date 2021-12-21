/* 
 Copyright (c) 2010, Direct Project
 All rights reserved.

 Authors:
    Joe Shook     jshook@kryptiq.com
  
Redistribution and use in source and binary forms, with or without modification, are permitted provided that the following conditions are met:

Redistributions of source code must retain the above copyright notice, this list of conditions and the following disclaimer.
Redistributions in binary form must reproduce the above copyright notice, this list of conditions and the following disclaimer in the documentation and/or other materials provided with the distribution.
Neither the name of The Direct Project (directproject.org) nor the names of its contributors may be used to endorse or promote products derived from this software without specific prior written permission.
THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 
*/

#nullable enable
using System;
using System.Collections;
using System.Collections.Generic;
using System.Linq;
using System.Threading.Tasks;
using Health.Direct.Config.Store.Entity;
using Microsoft.EntityFrameworkCore;

namespace Health.Direct.Config.Store;

public class MdnManager : IEnumerable<Mdn>
{
    const int DEFAULT_PROCESSED_TIMEOUT_MINUTES = 10;
    const int DEFAULT_DISPATCHED_TIMEOUT_MINUTES = 10;
    private const int DEFAULT_TIMEOUT_RECORDS = 10;


    public MdnManager(ConfigStore store)
    {
        Store = store;
    }

    internal ConfigStore Store { get; }

    public async Task Start(Mdn mdn)
    {
        await using var db = Store.CreateContext();
        Start(db, new[] { mdn });
        await db.SaveChangesAsync();
    }

    public async Task Start(Mdn[] mdns)
    {
        await using var db = Store.CreateContext();
        Start(db, mdns);
        await db.SaveChangesAsync();
    }

    public void Start(ConfigDatabase db, Mdn[] mdns)
    {
        if (db == null)
        {
            throw new ArgumentNullException(nameof(db));
        }

        if (mdns.Length.Equals(0))
        {
            return;
        }

        foreach (var mdn in mdns)
        {
            db.Mdns.Add(mdn);
        }
    }

    public async Task TimeOut(Mdn mdn)
    {
        await using var db = Store.CreateContext();
        TimeOut(db, mdn);
        await db.SaveChangesAsync();
    }

    public async Task TimeOut(List<Mdn> mdns)
    {
        await using var db = Store.CreateContext();
        foreach (var mdn in mdns)
        {
            TimeOut(db, mdn);
        }
        await db.SaveChangesAsync();
    }

    public void TimeOut(ConfigDatabase db, Mdn mdn)
    {
        if (db == null)
        {
            throw new ArgumentNullException(nameof(db));
        }

        if (mdn == null)
        {
            throw new ConfigStoreException(ConfigStoreError.InvalidMdn);
        }
        mdn.Status = MdnStatus.TimedOut;
        db.Mdns.Update(mdn);
    }

    public async Task Update(Mdn mdn)
    {
        await using var db = Store.CreateContext();
        try
        {
            Update(db, mdn);
            await db.SaveChangesAsync();
        }
        catch
        {
            ThrowMdnFaultException(mdn);
            throw;
        }
    }

    public async Task Update(Mdn[] mdns)
    {
        await using var db = Store.CreateContext();
        foreach (var mdn in mdns)
        {
            Update(db, mdn);
        }
        await db.SaveChangesAsync();
    }

    public void Update(ConfigDatabase db, Mdn mdn)
    {
        if (db == null)
        {
            throw new ArgumentNullException(nameof(db));
        }

        if (mdn == null)
        {
            throw new ConfigStoreException(ConfigStoreError.InvalidMdn);
        }

        db.Mdns.Add(mdn);

    }

    private static void ThrowMdnFaultException(Mdn mdn)
    {

        if (mdn.Status == null)
        {
            throw new ConfigStoreException(ConfigStoreError.DuplicateMdnStart);
        }

        if (mdn.Status.Equals(MdnStatus.Processed, StringComparison.OrdinalIgnoreCase))
        {
            //
            // Duplicate processed MDN
            //
            throw new ConfigStoreException(ConfigStoreError.DuplicateProcessedMdn);
        }

        if (mdn.Status.Equals(MdnStatus.Failed, StringComparison.OrdinalIgnoreCase))
        {
            //
            // Duplicate failed MDN
            //
            throw new ConfigStoreException(ConfigStoreError.DuplicateFailedMdn);
        }

        if (mdn.Status.Equals(MdnStatus.Dispatched, StringComparison.OrdinalIgnoreCase))
        {
            //
            // Duplicate Dispatched MDN
            //
            throw new ConfigStoreException(ConfigStoreError.DuplicateDispatchedMdn);
        }

        if (mdn.Status.Equals(MdnStatus.Processed, StringComparison.OrdinalIgnoreCase))
        {
            //
            // Dispatched MDN already sent
            //
            throw new ConfigStoreException(ConfigStoreError.MdnPreviouslyProcessed);
        }

    }


    public async Task<int> Count()
    {
        await using var db = Store.CreateReadContext();
        return await db.Mdns.CountAsync();
    }

    public async Task<Mdn?> Get(string mdnIdentifier)
    {
        await using var db = Store.CreateReadContext();
        return await Get(db, mdnIdentifier);
    }

    public async Task<Mdn?> Get(ConfigDatabase db, string mdnIdentifier)
    {
        if (db == null)
        {
            throw new ArgumentNullException(nameof(db));
        }
        if (string.IsNullOrEmpty(mdnIdentifier))
        {
            throw new ConfigStoreException(ConfigStoreError.InvalidMdnIdentifier);
        }

        var mdns = await db.Mdns
            .FromSqlRaw(@"
                            Declare @notifyRequest tinyint;
                            set @notifyRequest =
                            (   select max(Cast(m2.NotifyDispatched as tinyint))
                                FROM Mdns m1
                                Join Mdns m2 
                                on m1.MessageId = m2.MessageId
                                where m1.MdnIdentifier= {0}
                                group by m2.MessageId
                            );

                            select 
	                             [MdnIdentifier]
	                            ,[MdnId]
	                            ,[MessageId]
	                            ,[RecipientAddress]
	                            ,[SenderAddress]
	                            ,[Subject]
	                            ,[Status]
	                            ,Cast(@notifyRequest as bit) as NotifyDispatched    
	                            ,[CreateDate]
                            From Mdns
                            Where MdnIdentifier= {0}
                ", mdnIdentifier)
            .ToListAsync();

        return mdns.SingleOrDefault();
    }

    public async Task<List<Mdn>> Get(string lastMdn, int maxResults)
    {
        await using var db = this.Store.CreateReadContext();
        return await Get(db, lastMdn, maxResults);
    }

    public async Task<List<Mdn>> Get(ConfigDatabase db, string lastMdn, int maxResults)
    {
        if (db == null)
        {
            throw new ArgumentNullException(nameof(db));
        }

        if (string.IsNullOrEmpty(lastMdn))
        {
            return await db.Mdns
                .FromSqlRaw("SELECT TOP ({0}) * from Mdns order by CreateDate desc", maxResults)
            .ToListAsync();
        }

        return await db.Mdns
            .FromSqlRaw("SELECT TOP ({0}) * from Mdns where CreateDate > {1} order by CreateDate asc", maxResults, lastMdn)
            .ToListAsync();
    }


    public async Task<List<Mdn>> GetTimedOut()
    {
        await using var db = Store.CreateReadContext();
        return await db.Mdns.Where(m => m.Status == MdnStatus.TimedOut).ToListAsync();
    }

    public async Task<List<Mdn>> GetExpiredProcessed(TimeSpan expiredLimit, int maxResults)
    {
        await using var db = Store.CreateReadContext();
        return await GetExpiredProcessed(db, expiredLimit, maxResults);
    }
    public async Task<List<Mdn>> GetExpiredProcessed(TimeSpan expiredLimit)
    {
        await using var db = Store.CreateReadContext();
        return await GetExpiredProcessed(db, expiredLimit, DEFAULT_TIMEOUT_RECORDS);
    }
    public async Task<List<Mdn>> GetExpiredProcessed()
    {
        await using var db = Store.CreateReadContext();
        return await GetExpiredProcessed(db, TimeSpan.FromMinutes(DEFAULT_PROCESSED_TIMEOUT_MINUTES), DEFAULT_TIMEOUT_RECORDS);
    }
    public async Task<List<Mdn>> GetExpiredProcessed(ConfigDatabase db, TimeSpan expiredLimit, int maxResults)
    {
        if (db == null)
        {
            throw new ArgumentNullException(nameof(db));
        }

        var expiredDateLimit = DateTime.Now - expiredLimit;

        return await db.Mdns
            .FromSqlRaw(@"  ;With timeOuts as (
	                            Select MessageId
	                            From Mdns	
	                            Where status = 'timedout'
                                Or status = 'processed'
                                Or status = 'dispatched'
                                Or status = 'Failed'
                            )
                            Select 
	                            top({1}) *
                            From Mdns
                            Where
	                            MessageId not in (select MessageId from timeOuts)
                            And
	                            CreateDate <  {0}
                            Order by CreateDate desc
                        ", expiredDateLimit, maxResults)
            .ToListAsync();
    }


    public async Task<List<Mdn>> GetExpiredDispatched(TimeSpan expiredLimit, int maxResults)
    {
        await using var db = Store.CreateReadContext();
        return await GetExpiredDispatched(db, expiredLimit, maxResults);
    }
    public async Task<List<Mdn>> GetExpiredDispatched(TimeSpan expiredLimit)
    {
        await using var db = Store.CreateReadContext();
        return await GetExpiredDispatched(db, expiredLimit, DEFAULT_TIMEOUT_RECORDS);
    }
    public async Task<List<Mdn>> GetExpiredDispatched()
    {
        await using var db = Store.CreateReadContext();
        return await GetExpiredDispatched(db, TimeSpan.FromMinutes(DEFAULT_DISPATCHED_TIMEOUT_MINUTES), DEFAULT_TIMEOUT_RECORDS);
    }
    public async Task<List<Mdn>> GetExpiredDispatched(ConfigDatabase db, TimeSpan expiredLimit, int maxResults)
    {
        if (db == null)
        {
            throw new ArgumentNullException(nameof(db));
        }

        var expiredDateLimit = DateTime.Now - expiredLimit;

        return await db.Mdns.FromSqlRaw(@"  ;With timeOuts as (
	                        Select MessageId
	                        From Mdns	
	                        Where status = 'timedout'
                            Or status = 'dispatched'
                            Or status = 'Failed'
                        ),
                        dispatchRequests as (
	                        Select max(MdnId) MdnId
	                        From Mdns
	                        Group by MessageId		
                            Having max(Cast(NotifyDispatched as tinyint)) = 1                       
                        )  

                        Select 
	                        top({1}) *
                        From Mdns
                        Where
	                        MessageId not in (select MessageId from timeOuts)
                        And
                            Status = 'processed'
                        And
					        MdnId in (select MdnId from dispatchRequests)
                        And
	                        CreateDate <  {0}
                        Order by CreateDate desc
                    ", expiredDateLimit, maxResults)
            .ToListAsync();
    }

    public async Task Remove(Mdn mdn)
    {
        await using var db = Store.CreateContext();
        await Remove(db, mdn);
    }

    public async Task Remove(ConfigDatabase db, Mdn mdn)
    {
        if (db == null)
        {
            throw new ArgumentNullException(nameof(db));
        }

        db.Mdns.Remove(mdn);
        await db.SaveChangesAsync();
    }

    public async Task RemoveTimedOut(TimeSpan limitTime, int bulkCount)
    {
        await using var db = Store.CreateContext();
        await RemoveTimedOut(db, limitTime, bulkCount);
        await db.SaveChangesAsync();
    }

    public async Task<int> RemoveTimedOut(ConfigDatabase db, TimeSpan limitTime, int bulkCount)
    {
        if (db == null)
        {
            throw new ArgumentNullException(nameof(db));
        }

        var expiredDateLimit = DateTime.Now - limitTime;

        return await db.Database.ExecuteSqlRawAsync(@"  --CTE Common table expression
                            ;With Candidates as (
	                            Select top ({1})
		                              MessageId
		                            , RecipientAddress
	                            From
		                            Mdns
	                            Where
	                            ( 
		                            Status = 'timedout' 
	                            )
	                            AND
		                            CreateDate < {0}
	                            Order by CreateDate desc
                            )

                            Delete Mdns
                            Where
	                            MessageId in (select MessageId from Candidates)
	                            and
	                            RecipientAddress in (select RecipientAddress from Candidates)
                        ", expiredDateLimit, bulkCount);
    }

    public async Task RemoveDispositions(TimeSpan limitTime, int bulkCount)
    {
        await using var db = Store.CreateContext();
        await RemoveDispositions(db, limitTime, bulkCount);
    }

    public async Task<int> RemoveDispositions(ConfigDatabase db, TimeSpan limitTime, int bulkCount)
    {
        if (db == null)
        {
            throw new ArgumentNullException(nameof(db));
        }

        var expiredDateLimit = DateTime.Now - limitTime;

        return await db.Database.ExecuteSqlRawAsync(@"  --CTE Common table expression
                ;With Candidates as (
	                Select top ({1})
		                  MessageId
		                , RecipientAddress
                        , SenderAddress
                        , Status
	                From
		                Mdns
	                Where
	                ( 
		                Status = 'processed' AND NotifyDispatched = 0
	                OR
		                Status = 'dispatched'
                    OR
                        Status = 'Failed'
	                )
	                AND
		                CreateDate < {0}
	                Order by CreateDate desc
                )

                Delete Mdns
                Where
	                   MessageId in (select MessageId from Candidates)
	                   and
	                   (
                            RecipientAddress in (select RecipientAddress from Candidates)
                            or
                            RecipientAddress in (select SenderAddress from Candidates where Status = 'Failed')                 
                       )
                    
               
            ", expiredDateLimit, bulkCount);
    }

    public IEnumerator<Mdn> GetEnumerator()
    {
        using var db = Store.CreateContext();
        foreach (Mdn mdn in db.Mdns)
        {
            yield return mdn;
        }
    }

    IEnumerator IEnumerable.GetEnumerator()
    {
        return GetEnumerator();
    }



}

