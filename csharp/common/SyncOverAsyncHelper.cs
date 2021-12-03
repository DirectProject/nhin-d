using System;
using System.Threading.Tasks;

namespace Health.Direct.Common
{
    //TODO: Remove when done modernizing.
    /// <summary>
    /// Temporary helper as this code base is migrated from sync to async code.
    /// </summary>
    public static class SyncOverAsyncHelper
    {
        public static TResult RunSync<TResult>(Func<Task<TResult>> func)
            => Task.Run(func)
                .GetAwaiter()
                .GetResult();
        public static void RunSync(Func<Task> func)
            => Task.Run(func)
                .GetAwaiter()
                .GetResult();
    }
}
