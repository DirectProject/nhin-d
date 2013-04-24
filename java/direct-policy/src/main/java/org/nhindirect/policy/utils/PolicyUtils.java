package org.nhindirect.policy.utils;

public class PolicyUtils 
{
	public static String createByteStringRep(byte[] bytes)
	{
	    final char[] hexDigits = {'0', '1', '2', '3', '4', '5', '6', '7', 
            '8', '9', 'a', 'b', 'c', 'd', 'e', 'f'};		
		
        final StringBuffer buf = new StringBuffer(bytes.length * 2);

        for (byte bt : bytes) 
        {
            buf.append(hexDigits[(bt & 0xf0) >> 4]);
            buf.append(hexDigits[bt & 0x0f]);
        }

        return buf.toString();
	}
}
