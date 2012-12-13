using System;

namespace Health.Direct.Config.Tools.Command
{
    [AttributeUsage(AttributeTargets.Method)]
    public class UsageAttribute : Attribute
    {
        public string Name { get; set; }
    }
}