/* 
Copyright (c) 2010, NHIN Direct Project
All rights reserved.

Authors:
   Greg Meyer      gm2552@cerner.com
 
Redistribution and use in source and binary forms, with or without modification, are permitted provided that the following conditions are met:

Redistributions of source code must retain the above copyright notice, this list of conditions and the following disclaimer.
Redistributions in binary form must reproduce the above copyright notice, this list of conditions and the following disclaimer 
in the documentation and/or other materials provided with the distribution.  Neither the name of the The NHIN Direct Project (nhindirect.org). 
nor the names of its contributors may be used to endorse or promote products derived from this software without specific prior written permission.
THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, 
THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS 
BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE 
GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, 
STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF 
THE POSSIBILITY OF SUCH DAMAGE.
*/

package org.nhindirect.dns.tools.utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Hashtable;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.io.FileUtils;

/**
 * Main command and control class for the DNS configuration manager tool.  Command classes are registered to this class, and 
 * commands marked with the {@link Command} annotation are mapped as runnable commands into the system.
 * @author Greg Meyer
 * 
 * @since 1.0
 */
public class Commands 
{
    static final String[] EmptyArgs = new String[0];

    private static final String SEARCH_USAGE = "Search for commands matching the given wildcard pattern" +
		"\r\n\tpattern" + 
		"\r\n\tpattern: (optional) pattern, containing '*' wildcards";
    
    private static final String HELP_USAGE =  "Show help" +
		"\r\nhelp ['all' | name]" +
		"\r\n\tall: All commands" +
		"\r\n\tname: This command name or names with this PREFIX" +
		"\r\nsearch [pattern]" +
		"\r\n" +
		SEARCH_USAGE;
    
    private static final String COMMANDS_USAGE = "List the commands available" + 
    	"\r\ncommands [nameprefix]";
    
    private static final String EXIT_USAGE = "Exit the application";
    
    private static final String BATCH_USAGE = "Run a series of commands from a file" +
    	"\r\nEach command is on its own line. Comments begin with //" +
    	"\r\nfilepath [echo command (default true)]";
    
    
    private final String appName;
    private final List<Object> instances;
    private final Map<String, CommandDef> commands;
    private final Map<Class<?>, Object> typeLookup;
    private String[] commandNames;
    private boolean running;
    

    public Commands(String appName)
    {
        if (appName == null || appName.isEmpty())
        {
            throw new IllegalArgumentException("appName value null or empty");
        }
        
        this.appName = appName;
        instances = new ArrayList<Object>();
        commands = new Hashtable<String,CommandDef>();
        typeLookup = new Hashtable<Class<?>, Object>();
        
        register(this);
    }
    
    public CommandDef getCommand(String name)
    {
        return commands.get(name.toUpperCase(Locale.getDefault()));
    }

    
    public Object getCommand(Class<?> clazz)
    {
    	Object cmd = typeLookup.get(clazz);
        if (cmd == null)
        {
            throw new IllegalStateException("Command of type " + clazz.getName() + " was not found.");
        }
        return cmd;
    }

    public Collection<String> getCommandNames()
    {
    	ensureCommandNamesArray();
    	return Arrays.asList(commandNames);
    }
    
    /*
    public event Action<Exception> Error;
    */
    
    
    public void register(Object instance)
    {
        if (instance == null)
        {
            throw new IllegalArgumentException("instance");
        }
                               
        Class<?> type = instance.getClass();
        Method[] methods = type.getMethods();
        
        Collection<Method> commandMethods;
        if (methods != null && methods.length > 0)
        	commandMethods = Arrays.asList(methods);
        else
        	commandMethods = Collections.emptyList();

        instances.add(instance);
        typeLookup.put(instance.getClass(), instance);

        discoverCommandMethods(commandMethods, instance);
    }
    
    private void discoverCommandMethods(Collection<Method> methods, Object instance)
    {
        for (Method method : methods)
        {
            discoverCommandMethod(method, instance);
        }
    }

    private void discoverCommandMethod(Method method, Object instance)
    {
    	
    	Command cmd = method.getAnnotation(Command.class);
        if (cmd != null)
        {
            if (cmd.name() != null && !cmd.name().isEmpty())
            {
            	final Object inst = instance;
            	final Method meth = method;
            	Action<String[]> action = new Action<String[]>()
            	{
            		public void doAction(String[] params)
            		{
            			try
            			{
            				meth.invoke(inst, new Object[]{params});
            			}
            			catch (Throwable e)
            			{
            				Throwable error = e.getCause();
            				if (error != null)
            					System.err.println("\r\n" + error.getMessage());
            				
            			}
            		}
            	};
            	
                setEval(cmd.name(), action);

                
                final String usageStr = cmd.usage();
                if (usageStr != null && !usageStr.isEmpty())
                {
                	CommandUsage usage = new CommandUsage()
                	{
                		public String getUsage()
                		{
                			return usageStr;
                		}
                	};
                    setUsage(cmd.name(), usage);
                }
            }
        }
    }


    public void runInteractive()
    {            
    	running = true;
        System.out.println(appName);

        String input;   
		InputStreamReader in = new InputStreamReader(System.in);
		BufferedReader reader = new BufferedReader(in);
		System.out.print(">");
		try
		{
			while (running && (input = reader.readLine()) != null)
			{
				if (!input.isEmpty())
				{
					run(input);
				}
				if (running)
					System.out.print("\r\n>");
			}
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
    }
    
    public boolean run(String commandLine)
    {
        if (commandLine != null && !commandLine.isEmpty())
        {
            String[] args = StringArrayUtil.parseAsCommandLine(commandLine);
            if (args != null && args.length > 0)
            {
                return run(args);
            }
        }
                    
        return false;
    }
    
    public boolean run(String[] args)
    {
        try
        {
            eval(args);
            return true;
        }
        catch (Exception ex)
        {
            handleError(ex);
        }
        
        return false;
    }   
       
    public void eval(String[] input) throws Exception
    {
        if (input == null || input.length == 0)
        {
            return;
        }
        
        String commandName = input[0];
        CommandDef cmd = getCommand(commandName);
        if (cmd == null)
        {
            System.out.println(commandName.toUpperCase(Locale.getDefault()) + " not found.");
            System.out.println();
            System.out.println(HELP_USAGE);
            return;
        }
        
        try
        {
        	String[] commandParams = (input.length > 1) ? (String[])Arrays.copyOfRange(input, 1, input.length) : EmptyArgs;
        	cmd.getEval().doAction(commandParams);
        }
        catch(Exception ex)
        {
            handleError(ex);
            System.out.println();
            cmd.showUsage();
            throw ex;
        }
    }
    
    public void showUsage(String cmdName)
    {
        if (cmdName == null || cmdName.isEmpty())
        {
            showAllUsage();
        }
        else
        {
            this.bind(cmdName).showUsage();
        }
    }
    
    private void showAllUsage()
    {
        System.out.println("Registered commands");
        
        ensureCommandNamesArray();
        
        for(String name : commandNames)
        {
            showUsage(name);
        }
    }

    public Collection<String> prefixMatchCommandNames(String prefix)
    {
    	ensureCommandNamesArray();
    	Collection<String> retVal = new ArrayList<String>();
    	for (String cmd : commandNames)
    		if (cmd.toUpperCase(Locale.getDefault()).startsWith(prefix.toUpperCase(Locale.getDefault())))
    			retVal.add(cmd);

    	return retVal;
    }

    public Collection<String> matchCommandNames(String pat)
    {
    	ensureCommandNamesArray();
    	Pattern pattern = Pattern.compile(pat);

    	Collection<String> retVal = new ArrayList<String>();
    	for (String cmd : commandNames)
    	{
    		Matcher matcher = pattern.matcher(cmd);
    		if (matcher.find())
    		{
    			retVal.add(cmd);
    		}

    	}
    	
    	return retVal;
    }

    /*
    static void Exit(int code)
    {
        Environment.Exit(code);
    }
    */

    
    CommandDef bind(String name)
    {
        CommandDef cmd = getCommand(name);
        if (cmd == null)
        {
            throw new IllegalArgumentException("Command " + name + " not found. Type help for usage.");
        }
        return cmd;
    }
    
    public void ensureCommandNamesArray()
    {
        if (commandNames != null && commandNames.length > 0)
        {
            return;
        }

        Collection<String> names = new ArrayList<String>();
        if (commands != null)
        {
	        for (CommandDef cmd : commands.values())
	        	names.add(cmd.getName());
	        	
	        commandNames = names.toArray(new String[names.size()]);
        }        
    }
    
    void setEval(String name, Action<String[]> eval)
    {
        this.ensure(name).setEval(eval);
    }

    void setUsage(String name, CommandUsage usage)
    {
        this.ensure(name).setUsage(usage);
    }

    private CommandDef ensure(String name)
    {
        CommandDef cmd = getCommand(name);
        if (cmd == null)
        {
            cmd = new CommandDef();
            cmd.setName(name);
            commands.put(name.toUpperCase(Locale.getDefault()), cmd);
        }

        return cmd;
    }
    
    void handleError(Exception ex)
    {
        /*
    	if (this.Error != null)
        {
            this.Error(ex);
        }
        else
        {
            CommandUI.Print(ex);
        }
        */
    }    
    
    /*
    * Built in Standard Commands
    */
    @Command(name = "Quit", usage = EXIT_USAGE)
    public void quit(String[] args)
    {
        running = false;
    }

    @Command(name = "Exit", usage = EXIT_USAGE)
    public void exit(String[] args)
    {
        running = false;
    }

    @Command(name = "Commands", usage = COMMANDS_USAGE)
    public void listCommands(String[] args)
    {
    	ensureCommandNamesArray();
        String prefix = (args != null && args.length > 0) ? args[0] : null;
        
        Collection<String> names;
        if (prefix == null || prefix.isEmpty())
        {
            names = Arrays.asList(commandNames);
        }
        else
        {
            names = prefixMatchCommandNames(prefix);
        }
        
        for (String name : names)
        {
            bind(name).showCommand();
        }
    }
        

    @Command(name = "Help", usage = HELP_USAGE)
    public void help(String[] args)
    {
        String cmdName = null;
        if (args != null && args.length > 0)
        { 
            cmdName = args[0];
        }
        
        if (cmdName == null || cmdName.isEmpty())
        {
            System.out.println(HELP_USAGE);
            return;
        }
        
        if (cmdName.equalsIgnoreCase("all"))
        {
            showAllUsage();
            return;
        }
        
        CommandDef cmd = getCommand(cmdName);
        if (cmd != null)
        {
            cmd.showUsage();
            return;
        }            

        
        for (String name : prefixMatchCommandNames(cmdName))
        {
            bind(name).showUsage();
        }
    }

    
    @Command(name = "Search", usage = SEARCH_USAGE)     
    public void search(String[] args)
    {
        String pattern = (args != null && args.length > 0) ? args[0] : null;
        if (pattern == null || pattern.isEmpty())
        {
            showAllUsage();
            return;
        }
        
        pattern = pattern.replace("*", ".*");
        for (String name : matchCommandNames(pattern))
        {
            bind(name).showUsage();
        }
    }
    
    @SuppressWarnings("unchecked")
    @Command(name = "Batch", usage = BATCH_USAGE)
    public void batch(String[] args)
    {
    	if (args == null || args.length ==0)
    		throw new IllegalArgumentException("Missing argument at position 0");
    	
        File file = new File(args[0]);
        
        boolean echo = (args != null && args.length > 1) ? Boolean.parseBoolean(args[1]) : true;
        if (!file.exists())
        {
            throw new IllegalArgumentException("File " + file.getAbsolutePath() + " not found.");
        }
        
        try
        {
	        List<String> lines = FileUtils.readLines(file);
	        for (String line : lines)
	        {
	            line = line.trim();
	            if ((line != null && !line.isEmpty()) && !line.startsWith("//"))
	            {
	                if (echo && !line.toUpperCase(Locale.getDefault()).startsWith("ECHO"));
	                {
	                    System.out.println(line);
	                }
	                run(line);
	            }
	        }
        }
        catch (IOException e)
        {
        	throw new IllegalStateException("Error reading file " + file.getAbsolutePath());
        }
    }


    @Command(name = "Echo", usage = "Echo the args to the console")
    public void echo(String[] args)
    {
        if (args == null || args.length == 0)
        {
            return;
        }
        
        for (String arg : args)
        {
            System.out.println(arg);
        }
    }
}
