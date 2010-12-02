package org.nhindirect.dns.tools.utils;

import java.util.Locale;

class CommandDef 
{
	private String name;
	private Action<String[]> eval;
	private CommandUsage usage;
	
    public String getName()
    {
    	return name;
    }
    
    public void setName(String name)
    {
    	this.name = name;
    }
    
    public Action<String[]> getEval()
    {
    	return eval;
    }
    
    public void setEval(Action<String[]> eval)
    {
    	this.eval = eval;
    }

    public CommandUsage getUsage()
    { 
    	return usage;
    }
    
    public void setUsage(CommandUsage usage)
    {
    	this.usage = usage;
    }
    

    boolean hasUsage()
    {
       return (usage != null);
    }
            
    void showUsage()
    {
        System.out.println(getName().toUpperCase(Locale.getDefault()));
        if (usage != null)
        {
        	System.out.println(usage.getUsage());
        }

        System.out.println();
    }

    void showCommand()
    {
        System.out.println(getName().toUpperCase(Locale.getDefault()));
    }
}
