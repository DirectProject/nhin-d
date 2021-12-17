using System.Threading.Tasks;
using Microsoft.EntityFrameworkCore;

namespace Health.Direct.Config.Store.Tests;

public static class MdnUtil
{
    public static async Task RemoveAll(ConfigDatabase db)
    {
        await db.Database.ExecuteSqlRawAsync(
            @"BEGIN TRAN 
                DELETE From Mdns DBCC CHECKIDENT([Mdns],RESEED,0) 
              COMMIT TRAN ");
    }
}