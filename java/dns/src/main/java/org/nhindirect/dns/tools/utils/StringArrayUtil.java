package org.nhindirect.dns.tools.utils;

public class StringArrayUtil 
{
    public static String getValueOrNull(String[] args, int indexAt)
    {
        if (indexAt < 0 || indexAt >= args.length)
        {
            return null;
        }
        
        return args[indexAt];
    }
    
    public static boolean isNullOrEmpty(String[] args)
    {
        return (args == null || args.length == 0);
    }
    
    public static boolean isNullOrEmpty(String str)
    {
        return (str == null || str.isEmpty());
    }
    
    public static String getRequiredValue(String[] args, int index)
    {
        String value = getValueOrNull(args, index);
        if (isNullOrEmpty(value))
        {
            throw new IllegalArgumentException("Missing argument at position " + index);
        }

        return value;
    }

    public static String getOptionalValue(String[] args, int index, String defaultValue)
    {
        String value = getValueOrNull(args,index);
        if (isNullOrEmpty(value))
        {
            return defaultValue;
        }

        return value;
    }

}
