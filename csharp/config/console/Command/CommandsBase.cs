using System;

using Health.Direct.Config.Store;

namespace Health.Direct.Config.Console.Command
{
    public class CommandsBase
    {
        public readonly static string EntityStatusString = string.Join(" | ", Enum.GetNames(typeof(EntityStatus)));
    }
}