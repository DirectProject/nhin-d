using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Reflection;

namespace NHINDirect.Tools.Command
{
    /// <summary>
    /// For the EASY implementation of command line apps. Can't always do Powershell.
    /// 
    /// 1. Create a class
    /// 2. Create some command handler methods. Prefix the name of your method with Command_.  E.g. Command_PrintCurrentDate
    ///     The methods should have the signature Action<string[]>
    /// 3. Optionally, create a method to provide usage information for your comamnds. E.g. Usage_PrintCurrentDate
    ///     Usage methods have the signature Action
    /// 4. You can create multiple classes with commands. 
    /// 5. In Main, create a new instance of the Commands.
    /// 6. Register each of your classes with the Commands object   
    /// 
    /// Several extension methods are supplied (CommandExtensions.cs) to make parsing of arguments Easy. 
    /// </summary>    
    /*
        static void Main(string[] args)
        {
            s_commands = new Commands("ConfigConsole");
            s_commands.Error += PrintError;
            s_commands.Register(new DomainCommands());
            s_commands.Register(new AddressCommands());
            s_commands.Register(new CertificateCommands());
            s_commands.Register(new AnchorCommands());
            
            if (args != null && args.Length > 0)
            {
                s_commands.Run(args);
            }
            else
            {
                s_commands.RunInteractive();
            }
        }
     */
    public class Commands
    {
        const string CommandPrefix = "command_";
        const string UsagePrefix = "usage_";
        
        static string[] EmptyArgs = new string[0];
        
        string m_appName;
        List<object> m_instances;        
        Dictionary<string, CommandDef> m_commands;
        string[] m_commandNames;
        
        public Commands(string appName)
        {
            if (string.IsNullOrEmpty(appName))
            {
                throw new ArgumentException();
            }
            m_appName = appName;
            m_instances = new List<object>();
            m_commands = new Dictionary<string,CommandDef>(StringComparer.OrdinalIgnoreCase);
            
            this.Register(this);
        }
        
        internal CommandDef this[string name]
        {
            get
            {
                CommandDef cmd;
                if (m_commands.TryGetValue(name, out cmd))
                {
                    return cmd;
                }                
                return null;
            }
        }
        
        public IEnumerable<string> CommandNames
        {
            get
            {
                this.EnsureCommandNamesArray();
                return m_commandNames;
            }
        }
        
        public event Action<Exception> Error;
        
        public void Register(object instance)
        {
            if (instance == null)
            {
                throw new ArgumentNullException();
            }
                                   
            Type type = instance.GetType();
            MethodInfo[] methods = type.GetMethods(BindingFlags.Instance | BindingFlags.Public | BindingFlags.InvokeMethod);
            if (methods == null)
            {
                return;
            }

            m_instances.Add(instance);
            this.DiscoverCommandMethods(methods, instance);
        }
        
        void DiscoverCommandMethods(MethodInfo[] methods, object instance)
        {
            foreach (MethodInfo method in methods)
            {
                string name = method.Name.ToLower();
                if (name.StartsWith(CommandPrefix, StringComparison.OrdinalIgnoreCase))
                {
                    name = name.Replace(CommandPrefix, string.Empty);                    
                    this.SetEval(name, (Action<string[]>) Delegate.CreateDelegate(typeof(Action<string[]>), instance, method));
                }
                else if (name.StartsWith(UsagePrefix, StringComparison.OrdinalIgnoreCase))
                {
                    name = name.Replace(UsagePrefix, string.Empty);
                    this.SetUsage(name, (Action)Delegate.CreateDelegate(typeof(Action), instance, method));
                }
            }        
        }    
        
        public void RunInteractive()
        {            
            CommandUI.PrintHeading(m_appName);

            string input;            
            while ((input = CommandUI.GetInput()) != null)
            {
                if (input.Length == 0)
                {
                    continue;
                }

                string[] args = input.SplitLikeCommandLine().ToArray();
                if(!args.IsNullOrEmpty())
                {
                    this.Run(args);
                }
            }
        }
        
        public void Run(string[] args)
        {
            try
            {
                this.Eval(args);
            }
            catch (Exception ex)
            {
                if (this.Error != null)
                {
                    this.Error(ex);
                }
                else
                {
                    CommandUI.Print(ex);
                }
            }
        }   
           
        public void Eval(params string[] input)
        {
            if (input.IsNullOrEmpty())
            {
                return;
            }
            
            CommandDef cmd = this.Bind(input);                                    
            string[] args = EmptyArgs;
            
            if (input.Length > 1)
            {
                args = new string[input.Length - 1];
                Array.Copy(input, 1, args, 0, input.Length - 1);
            }
            
            cmd.Eval(args);
        }
        
        public void ShowUsage(string cmdName)
        {
            if (string.IsNullOrEmpty(cmdName))
            {
                this.ShowAllUsage();
            }
            else
            {
                this.Bind(cmdName).ShowUsage();
            }
        }
        
        void ShowAllUsage()
        {
            CommandUI.PrintHeading("Registered commands");
            
            foreach(string name in this.CommandNames)
            {
                this.ShowUsage(name);
            }
        }

        void Exit(int code)
        {
            Environment.Exit(code);
        }

        bool Validate()
        {
            bool isValid = true;
            foreach (CommandDef cmd in m_commands.Values)
            {
                if (cmd.Eval == null)
                {
                    Console.WriteLine("{0} has no Eval method", cmd.Name);
                    isValid = false;
                }
            }

            return isValid;
        }

        CommandDef Bind(string[] input)
        {
            return this.Bind(input[0]);
        }
        CommandDef Bind(string name)
        {
            CommandDef cmd = this[name];
            if (cmd == null)
            {
                throw new ArgumentException(string.Format("Command {0} not found. Type help for usage.", name));
            }

            return cmd;
        }
        
        void EnsureCommandNamesArray()
        {
            if (!m_commandNames.IsNullOrEmpty())
            {
                return;
            }
            
            m_commandNames = new string[m_commands.Values.Count];
            int i = 0;
            foreach (CommandDef command in m_commands.Values)
            {
                m_commandNames[i++] = command.Name;
            }

            Array.Sort(m_commandNames);
        }
        
        void SetEval(string name, Action<string[]> eval)
        {
            this.Ensure(name).Eval = eval;
        }

        void SetUsage(string name, Action usage)
        {
            this.Ensure(name).Usage = usage;
        }

        CommandDef Ensure(string name)
        {
            CommandDef cmd = this[name];
            if (cmd == null)
            {
                cmd = new CommandDef { Name = name };
                m_commands[name] = cmd;
            }

            return cmd;
        }
                
        //-------------------------------
        //
        // Built in Standard Commands
        //
        //-------------------------------
        public void Command_Quit(string[] args)
        {
            Command_Exit(args);
        }
        public void Usage_Quit()
        {
            Usage_Exit();
        }
        public void Command_Exit(string[] args)
        {
            Exit(0);
        }
        public void Usage_Exit()
        {
            Console.WriteLine("Exit the application");
        }
        public void Command_Help(string[] args)
        {
            string cmdName = null;
            if (!args.IsNullOrEmpty())
            { 
                cmdName = args[0];
            }
            
            if (string.IsNullOrEmpty(cmdName))
            {
                Usage_Help();
                return;
            }
            
            if (cmdName.Equals("all", StringComparison.OrdinalIgnoreCase))
            {
                ShowAllUsage();
                return;
            }
            
            CommandDef cmd = this[cmdName];
            if (cmd != null)
            {
                cmd.ShowUsage();
                return;
            }            
            //
            // Do a prefix match. Note: if needed, we can speed this up since the name array is sorted. 
            //
            var prefixMatch = from name in this.CommandNames
                              where name.StartsWith(cmdName, StringComparison.OrdinalIgnoreCase)
                              select name;
            foreach (string name in prefixMatch)
            {
                this.Bind(name).ShowUsage();
            }
        }
        
        public void Usage_Help()
        {
            Console.WriteLine("Show help");
            Console.WriteLine("help ['all' | commandName]");
            Console.WriteLine("   all: Show all commands");
            Console.WriteLine("   commandName: Show help for this command"); 
            Console.WriteLine("   If command not found, then show help for commands whose names begin with commandName");
        }        
    }    
}
