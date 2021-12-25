﻿using System.Threading.Tasks;
using Microsoft.EntityFrameworkCore;

namespace Health.Direct.Config.Store.Tests;

public static class CertificateUtil
{
    public static async Task RemoveAll(DirectDbContext db)
    {
        await db.Database.ExecuteSqlRawAsync(
            @" truncate table Certificates  ");
    }
}