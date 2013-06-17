/* 
 Copyright (c) 2010, Direct Project
 All rights reserved.

 Authors:
    Umesh Madan     umeshma@microsoft.com
  
Redistribution and use in source and binary forms, with or without modification, are permitted provided that the following conditions are met:

Redistributions of source code must retain the above copyright notice, this list of conditions and the following disclaimer.
Redistributions in binary form must reproduce the above copyright notice, this list of conditions and the following disclaimer in the documentation and/or other materials provided with the distribution.
Neither the name of The Direct Project (directproject.org) nor the names of its contributors may be used to endorse or promote products derived from this software without specific prior written permission.
THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 
*/
using System;
using System.Collections.Generic;
using System.Linq;
using System.Text.RegularExpressions;
using System.IO;
using System.Reflection;

namespace Health.Direct.Config.Tools.Command
{
    /// <summary>
    /// For the EASY implementation of command line apps. Can't always do Powershell, or may not want to.
    /// 
    /// 1. Create a class. This class will handle commands.
    /// 2. Create a handler method for each of your commands. 
    ///   - Use the <see cref="CommandAttribute"/> to mark up your method
    ///   - The handler MUST have the signature Action&lt;string[]&gt;
    /// 3. Optionally, provide usage information for your command. 
    ///    - Use the Usage property of the <see cref="CommandAttribute.Usage"/>
    ///    - Use the <see cref="UsageAttribute"/> to mark a handler with the signature of Action&lt;string&gt;
    /// 4. You can create multiple classes, each with multiple commands. 
    /// 5. In the Main method for your class, create a new instance of the Commands object (see below).
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
        static string[] EmptyArgs = new string[0];

        readonly string m_appName;
        readonly List<object> m_instances;
        readonly Dictionary<string, CommandDef> m_commands;
        readonly Dictionary<Type, object> m_typeLookup;
        string[] m_commandNames;
        
        public Commands(string appName)
        {
            if (string.IsNullOrEmpty(appName))
            {
                throw new ArgumentException("value null or empty", "appName");
            }
            m_appName = appName;
            m_instances = new List<object>();
            m_commands = new Dictionary<string,CommandDef>(StringComparer.OrdinalIgnoreCase);
            m_typeLookup = new Dictionary<Type, object>();
            
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

        public T GetCommand<T>()
            where T : class
        {
            T cmd = m_typeLookup[typeof (T)] as T;
            if (cmd == null)
            {
                throw new Exception("Command of type " + typeof(T) + " was not found.");
            }
            return cmd;
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
                throw new ArgumentNullException("instance");
            }
                                   
            Type type = instance.GetType();
            MethodInfo[] methods = type.GetMethods(BindingFlags.Instance | BindingFlags.Public | BindingFlags.InvokeMethod);

            m_instances.Add(instance);
            m_typeLookup.Add(instance.GetType(), instance);

            this.DiscoverCommandMethods(methods, instance);
        }
        
        void DiscoverCommandMethods(IEnumerable<MethodInfo> methods, object instance)
        {
            foreach (MethodInfo method in methods)
            {
                DiscoverCommandMethod(method, instance);
                DiscoverUsageMethod(method, instance);
            }
        }

        private void DiscoverCommandMethod(MethodInfo method, object instance)
        {
            object[] attributes = method.GetCustomAttributes(typeof (CommandAttribute), true);
            for (int i = 0; i < attributes.Length; i++ )
            {
                CommandAttribute attribute = (CommandAttribute) attributes[i];
                if (!string.IsNullOrEmpty(attribute.Name))
                {
                    this.SetEval(attribute.Name, (Action<string[]>)Delegate.CreateDelegate(typeof(Action<string[]>), instance, method));

                    if (!string.IsNullOrEmpty(attribute.Usage))
                    {
                        this.SetUsage(attribute.Name, () => attribute.Usage);
                    }
                }
            }
        }

        private void DiscoverUsageMethod(MethodInfo method, object instance)
        {
            object[] attributes = method.GetCustomAttributes(typeof (UsageAttribute), true);
            for (int i = 0; i < attributes.Length; i++ )
            {
                UsageAttribute attribute = (UsageAttribute) attributes[i];
                if (!string.IsNullOrEmpty(attribute.Name))
                {
                    this.SetUsage(attribute.Name, (Func<string>)Delegate.CreateDelegate(typeof(Func<string>), instance, method));
                }
            }
        }

        public void RunInteractive()
        {            
            CommandUI.PrintHeading(m_appName);

            string input;            
            while ((input = CommandUI.GetInput()) != null)
            {
                if (input.Length > 0)
                {
                    this.Run(input);
                }
            }
        }
        
        public bool Run(string commandLine)
        {
            if (!string.IsNullOrEmpty(commandLine))
            {
                string[] args = commandLine.ParseAsCommandLine().ToArray();
                if (!args.IsNullOrEmpty())
                {
                    return this.Run(args);
                }
            }
                        
            return false;
        }
        
        public bool Run(string[] args)
        {
            try
            {
                this.Eval(args);
                return true;
            }
            catch (Exception ex)
            {
                this.HandleError(ex);
            }
            
            return false;
        }   
           
        public void Eval(params string[] input)
        {
            if (input.IsNullOrEmpty())
            {
                return;
            }
            
            string commandName = input.First();
            CommandDef cmd = this[commandName];
            if (cmd == null)
            {
                this.ShowUsageCommandNotFound(commandName);
                return;
            }
            
            try
            {
                cmd.Eval((input.Select(arg => arg)).Skip(1).ToArray());
            }
            catch(Exception ex)
            {
                this.HandleError(ex);
                CommandUI.PrintSectionBreak();
                cmd.ShowUsage();
            }
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

        void ShowUsageCommandNotFound(string commandName)
        {
            Console.WriteLine();
            CommandUI.PrintUpperCase("{0}:  No such command.", commandName);
            CommandUI.PrintSectionBreak();
            //
            // Speculatively print out names of commands with this prefix..
            //
            if (this.PrefixMatchCommandNames(commandName).FirstOrDefault() != null)
            {
                //
                // Speculatively print out names of commands with this prefix..
                //
                CommandUI.PrintHeading("Did you mean?");
                this.ListCommands(new string[] { commandName });
            }
            else
            {
                Console.WriteLine(HelpUsage);
            }
        }

        public IEnumerable<string> PrefixMatchCommandNames(string prefix)
        {
            //
            // Do a prefix match. Note: if needed, we can speed this up since the name array is sorted. 
            //
            return (from name in this.CommandNames
                    where name.StartsWith(prefix, StringComparison.OrdinalIgnoreCase)
                    select name);
        }

        public IEnumerable<string> MatchCommandNames(string pattern)
        {
            Regex regex = new Regex(pattern, RegexOptions.IgnoreCase);
            //
            // Do a prefix match. Note: if needed, we can speed this up since the name array is sorted. 
            //
            return (from name in this.CommandNames
                    where regex.IsMatch(name)
                    select name);
        }

        static void Exit(int code)
        {
            Environment.Exit(code);
        }

        //bool Validate()
        //{
        //    bool isValid = true;
        //    foreach (CommandDef cmd in m_commands.Values)
        //    {
        //        if (cmd.Eval == null)
        //        {
        //            Console.WriteLine("{0} has no Eval method", cmd.Name);
        //            isValid = false;
        //        }
        //    }

        //    return isValid;
        //}
        
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

            m_commandNames = (from cmd in m_commands.Values
                              let name = cmd.Name
                              orderby name
                              select name)
                .ToArray();
        }
        
        void SetEval(string name, Action<string[]> eval)
        {
            this.Ensure(name).Eval = eval;
        }

        void SetUsage(string name, Func<string> usageFunctor)
        {
            this.Ensure(name).UsageFunctor = usageFunctor;
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
        
        void HandleError(Exception ex)
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
        //-------------------------------
        //
        // Built in Standard Commands
        //
        //-------------------------------
        [Command(Name = "Quit", Usage = ExitUsage)]
        [Command(Name = "Exit", Usage = ExitUsage)]
        public void Quit(string[] args)
        {
            Exit(0);
        }
        private const string ExitUsage
            = "Exit the application";


        [Command(Name = "Commands", Usage = CommandsUsage)]
        public void ListCommands(string[] args)
        {
            string prefix = args.GetOptionalValue(0, null);
            
            IEnumerable<string> names;
            if (string.IsNullOrEmpty(prefix))
            {
                names = this.CommandNames;
            }
            else
            {
                names = this.PrefixMatchCommandNames(prefix);
            }
            foreach (string name in names)
            {
                this.Bind(name).ShowCommand();
            }
        }
        private const string CommandsUsage
            = "List the commands available"
            + Constants.CRLF + "commands [nameprefix]";
            
        /// <summary>
        /// Show help
        /// </summary>
        [Command(Name = "Help", Usage = HelpUsage)]
        public void Help(string[] args)
        {
            string cmdName = null;
            if (!args.IsNullOrEmpty())
            { 
                cmdName = args[0];
            }
            
            if (string.IsNullOrEmpty(cmdName))
            {
                Console.WriteLine(HelpUsage);
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
            foreach (string name in this.PrefixMatchCommandNames(cmdName))
            {
                this.Bind(name).ShowUsage();
            }
        }

        private const string HelpUsage
            = "Show help"
              + Constants.CRLF + "help ['all' | name]"
              + Constants.CRLF + "   all: All commands"
              + Constants.CRLF + "   name: This command name or names with this PREFIX"
              + Constants.CRLF + Constants.CRLF + "search [pattern]"
              + Constants.CRLF + SearchUsage;
        
        /// <summary>
        /// Search for a command containing the given pattern
        /// </summary>
        /// <param name="args"></param>
        [Command(Name = "Search", Usage = SearchUsage)]
        public void Search(string[] args)
        {
            string pattern = args.GetOptionalValue(0, null);
            if (string.IsNullOrEmpty(pattern))
            {
                ShowAllUsage();
                return;
            }
            
            pattern = pattern.Replace("*", ".*");
            foreach (string name in this.MatchCommandNames(pattern))
            {
                this.Bind(name).ShowUsage();
            }
        }

        private const string SearchUsage
            = "Search for commands matching the given wildcard pattern"
              + Constants.CRLF + "    pattern"
              + Constants.CRLF + "\t pattern: (optional) pattern, containing '*' wildcards";

        /// <summary>
        /// Run commands in a batch
        /// </summary>
        [Command(Name = "Batch", Usage = BatchUsage)]
        public void Batch(string[] args)
        {
            string filePath = args.GetRequiredValue(0);
            bool echo = args.GetOptionalValue(1, true);
            if (!File.Exists(filePath))
            {
                throw new FileNotFoundException(filePath);
            }
            
            using(StreamReader reader = new StreamReader(filePath))
            {
                string line;
                while ((line = reader.ReadLine()) != null)
                {
                    line = line.Trim();
                    if (!string.IsNullOrEmpty(line) && !line.StartsWith("//"))
                    {
                        if (echo && !line.StartsWith("echo", StringComparison.OrdinalIgnoreCase))
                        {
                            Console.WriteLine(line);
                        }
                        this.Run(line);
                    }
                }
            }
        }

        private const string BatchUsage
            = "Run a series of commands from a file"
              + Constants.CRLF + "Each command is on its own line. Comments begin with //"
              + Constants.CRLF + "   filepath [echo command (default true)]";

        [Command(Name = "Echo", Usage = "Echo the args to the console")]
        public void Echo(string[] args)
        {
            if (args.IsNullOrEmpty())
            {
                return;
            }
            foreach(string arg in args)
            {
                Console.WriteLine(arg);
            }
        }

        private const string RepeatUsage
           = "Run a series of commands repetitively."
             + Constants.CRLF + "repeatcount command args";

        /// <summary>
        /// Run commands in a batch
        /// </summary>
        [Command(Name = "Repeat", Usage = RepeatUsage)]
        public void Repeat(string[] args)
        {
            int count = args.GetRequiredValue<int>(0);
            if (args.Length <= 2)
            {
                return;
            }
            string[] command = args.Skip(1).ToArray();
            for (int i= 0; i < count; ++i)
            {
                Console.WriteLine(i);
                this.Run(command);
            }
        }
    }
}