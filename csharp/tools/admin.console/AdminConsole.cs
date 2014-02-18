using Health.Direct.Common.Extensions;
using Health.Direct.Config.Tools.Command;

namespace Health.Direct.Tools.Admin
{
    public class AdminConsole
    {
        readonly Commands m_commands;

        public AdminConsole()
        {
            m_commands = new Commands("AdminConsole");
            m_commands.Register(new AdminCommands());
        }

        public void Run(string[] args)
        {
            if (args.IsNullOrEmpty())
            {
                m_commands.RunInteractive();
            }
            else
            {
                m_commands.Run(args);
            }
        }

        static void Main(string[] args)
        {
            new AdminConsole().Run(args);
        }
    }
}