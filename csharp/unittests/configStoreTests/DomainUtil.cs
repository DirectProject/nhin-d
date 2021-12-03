using System.Threading.Tasks;
using Microsoft.EntityFrameworkCore;

namespace Health.Direct.Config.Store.Tests;

public static class DomainUtil
{
    public static async Task RemoveAll(ConfigDatabase db)
    {
        await db.Database.ExecuteSqlRawAsync(
            @" Begin tran delete from Addresses delete from [Domains] DBCC CHECKIDENT([Domains],RESEED,0) DBCC CHECKIDENT(Addresses,RESEED,0) commit tran ");
    }
}