using System.Threading.Tasks;
using Microsoft.EntityFrameworkCore;

namespace Health.Direct.Config.Store.Tests;

public static class CertPolicyGroupUtil
{
    public static async Task RemoveAll(ConfigDatabase db)
    {
        await db.Database.ExecuteSqlRawAsync(
            @" Begin tran 
                     Delete from CertPolicyGroupDomainMap 
                     Delete from CertPolicyGroupMap 
                     Delete from CertPolicyGroups
                     DBCC CHECKIDENT(CertPolicyGroups,RESEED,0)                         
                 commit tran 
             ");
    }
}