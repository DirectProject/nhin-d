﻿using System.Threading.Tasks;
using Microsoft.EntityFrameworkCore;

namespace Health.Direct.Config.Store.Tests;

public static class AnchorUtil
{
    public static async Task RemoveAll(DirectDbContext db)
    {
        await db.Database.ExecuteSqlRawAsync(
            @" truncate table Anchors  ");
    }
}