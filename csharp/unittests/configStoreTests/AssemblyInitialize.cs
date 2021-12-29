using System;
using System.Diagnostics;
using Xunit;

namespace Health.Direct.Config.Store.Tests
{

    public class DatabaseFixture : IDisposable
    {
        public void Dispose()
        {
            //
            // Left hanging and Resharper complains.  This fixes that after all tests complete.
            //
            using var process = Process.Start("sqllocaldb", "stop Projects");
            process?.WaitForExit();
        }
    }

    [CollectionDefinition("ManagerFacts")]
    public class DatabaseCollection : ICollectionFixture<DatabaseFixture>
    {
        //
        // https://stackoverflow.com/questions/12976319/xunit-net-global-setup-teardown
        //
        // This class has no code, and is never created. Its purpose is simply
        // to be the place to apply [CollectionDefinition] and all the
        // ICollectionFixture<> interfaces.
    }
}
