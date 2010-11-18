using System;
using System.Collections.Generic;
using System.Configuration;

using Health.Direct.Config.Store;
using Health.Direct.Config.Tools;
using Health.Direct.Config.Tools.Command;

namespace Health.Direct.Tools.Admin
{
    public class AdminCommands
    {
        private readonly AdministratorManager m_manager;

        public AdminCommands()
        {
            ConfigStore store = new ConfigStore(ConfigurationManager.ConnectionStrings["configStore"].ConnectionString);
            m_manager = store.Administrators;
        }

        [Command(Name="User_Add", Usage = AddUserUsage)]
        public void AddUser(string[] args)
        {
            string username = args.GetRequiredValue(0);
            string password = args.GetRequiredValue(1);

            m_manager.Add(new Administrator(username, password));
            WriteLine("User added.");
        }

        private const string AddUserUsage
            = "Add a new user."
              + Constants.CRLF + "    username password"
              + Constants.CRLF + "\t username: the username for the administrator to be added."
              + Constants.CRLF + "\t password: the password for the administrator.";

        [Command(Name = "User_Remove", Usage = RemoveUserUsage)]
        public void RemoveUser(string[] args)
        {
            string username = args.GetRequiredValue(0);

            m_manager.Remove(username);
            WriteLine("User removed.");
        }

        private const string RemoveUserUsage
            = "Remove a user."
              + Constants.CRLF + "    username"
              + Constants.CRLF + "\t username: the username to remove.";

        [Command(Name = "User_Status_Set", Usage = SetUserStatusUsage)]
        public void SetUserStatus(string[] args)
        {
            string username = args.GetRequiredValue(0);
            EntityStatus status = args.GetRequiredEnum<EntityStatus>(1);

            m_manager.SetStatus(username, status);
            WriteLine("User status updated to '{0}'.", status);
        }

        private const string SetUserStatusUsage
            = "Add a new user."
              + Constants.CRLF + "    username status"
              + Constants.CRLF + "\t username: set the status of this username"
              + Constants.CRLF + "\t status: New | Enabled | Disabled.";

        [Command(Name = "User_Change_Password", Usage = ChangeUserPasswordUsage)]
        public void ChangeUserPassword(string[] args)
        {
            string username = args.GetRequiredValue(0);
            string password = args.GetRequiredValue(1);

            var user = m_manager.Get(username);
            if (user != null)
            {
                user.SetPassword(password);
                m_manager.Update(user);
                WriteLine("User password successfully changed.");
            }
            else
            {
                WriteLine("User not found.");
            }
        }

        private const string ChangeUserPasswordUsage
            = "Add a new user."
              + Constants.CRLF + "    username password"
              + Constants.CRLF + "\t username: set the password of the user"
              + Constants.CRLF + "\t password: the new password for the user";

        [Command(Name = "User_ListAll", Usage = UserListAllUsage)]
        public void ListUsers(string[] args)
        {
            Print(m_manager.Get("", int.MaxValue));
        }

        private const string UserListAllUsage
            = "List all users.";

        private static void Print(IEnumerable<Administrator> administrators)
        {
            foreach (Administrator administrator in administrators)
            {
                Print(administrator);
                CommandUI.PrintSectionBreak();
            }
        }

        private static void Print(Administrator administrator)
        {
            CommandUI.Print("Username", administrator.Username);
            CommandUI.Print("ID", administrator.ID);
            CommandUI.Print("Status", administrator.Status);
            CommandUI.Print("CreateDate", administrator.CreateDate);
            CommandUI.Print("UpdateDate", administrator.UpdateDate);
        }

        private static void WriteLine(string format, params object[] args)
        {
            Console.WriteLine(format, args);
        }
    }
}