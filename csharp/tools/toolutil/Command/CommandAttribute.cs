using System;

namespace Health.Direct.Config.Tools.Command
{
    [AttributeUsage(AttributeTargets.Method, AllowMultiple = true)]
    public class CommandAttribute : Attribute
    {
        public string Name { get; set; }
        public string Usage { get; set; }
    }
}