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
    ///   - Prefix the method with Command_.  E.g. Command_PrintCurrentDate
    ///   - The handler MUST have the signature Action<string[]>
    /// 3. Optionally, create a method to provide usage information for your command. 
    ///    - Prefix the method with Usage_. E.g. Usage_PrintCurrentDate
    ///    - Usage methods have the signature Action()
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
                throw new ArgumentException("value null or empty", "appName");
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
                throw new ArgumentNullException("instance");
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
            
            string commandName = input[0];
            CommandDef cmd = this[commandName];
            if (cmd == null)
            {
                CommandUI.PrintUpperCase("{0} not found", commandName);
                CommandUI.PrintSectionBreak();
                Usage_Help();
                return;
            }
            
            string[] args = EmptyArgs;            
            if (input.Length > 1)
            {
                args = new string[input.Length - 1];
                Array.Copy(input, 1, args, 0, input.Length - 1);
            }
            
            try
            {
                cmd.Eval(args);
                return;
            }
            catch(Exception ex)
            {
                this.HandleError(ex);
            }
            
            CommandUI.PrintSectionBreak();
            cmd.ShowUsage();            
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
            Regex regex = new Regex(pattern);
            //
            // Do a prefix match. Note: if needed, we can speed this up since the name array is sorted. 
            //
            return (from name in this.CommandNames
                    where regex.IsMatch(name)
                    select name);
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
        
        /// <summary>
        /// Show help
        /// </summary>
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
            foreach (string name in this.PrefixMatchCommandNames(cmdName))
            {
                this.Bind(name).ShowUsage();
            }
        }        
        public void Usage_Help()
        {
            Console.WriteLine("Show help");
            Console.WriteLine("help ['all' | name]");
            Console.WriteLine("   all: All commands");
            Console.WriteLine("   name: This command name or names with this PREFIX"); 
            Console.WriteLine();
            Console.WriteLine("search [pattern]");
            Usage_Search();
        }
        
        /// <summary>
        /// Search for a command containing the given pattern
        /// </summary>
        /// <param name="args"></param>
        public void Command_Search(string[] args)
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
        public void Usage_Search()
        {
            Console.WriteLine("Search for commands matching the given wildcard pattern");
            Console.WriteLine("    pattern");
            Console.WriteLine("\t pattern: (optional) pattern, containing '*' wildcards");
        }
        /// <summary>
        /// Run commands in a batch
        /// </summary>
        public void Command_Batch(string[] args)
        {
            string filePath = args.GetRequiredValue(0);
            bool echo = args.GetOptionalValue<bool>(1, true);
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
        public void Usage_Batch()
        {
            Console.WriteLine("Run a series of commands from a file");
            Console.WriteLine("Each command is on its own line. Comments begin with //");
            Console.WriteLine("   filepath [echo command (default true)]");
        }
        
        public void Command_Echo(string[] args)
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
        public void Usage_Echo()
        {
            Console.WriteLine("Echo the args to the console");
        }
    }
}