package org.nhindirect.common.crypto.tools.commands;

import java.security.Key;

public class KeyModel 
{
	protected final String keyName;
	protected final Key key;
	protected final char[] keyText;
	
	public KeyModel(String keyName, Key key, char[] keyText)
	{
		this.keyName = keyName;
		this.key = key;
		this.keyText = keyText;
	}

	public String getKeyName() 
	{
		return keyName;
	}

	public Key getKey() 
	{
		return key;
	}

	public char[] getKeyText() 
	{
		return keyText;
	}
	
	
}
