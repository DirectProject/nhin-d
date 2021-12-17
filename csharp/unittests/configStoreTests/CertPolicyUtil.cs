using System.Threading.Tasks;
using Microsoft.EntityFrameworkCore;

namespace Health.Direct.Config.Store.Tests;

public static class CertPolicyUtil
{
    public static async Task RemoveAll(ConfigDatabase db)
    {
        await db.Database.ExecuteSqlRawAsync(
            @" Begin tran                         
                         delete from CertPolicyGroupMap                        
                         delete from CertPolicies
                         DBCC CHECKIDENT(CertPolicies,RESEED,0)                                             
                     Commit tran 
                 ");
    }
}