package org.nhindirect.config.ui.util;

import java.util.ArrayList;
import java.util.List;

public enum PrivateKeyType
{
	NONE("No Private Key"),
	
	PKCS_12_UNPROTECTED("Unprotected PKCS12"),
	
	PKCS_12_PASSPHRASE("Password Protected PKCS12"),
	
	PKCS8_UNPROTECT("Unprotected PKCS8"),
	
	PKCS8_PASSPHRASE("Password Protected PKCS8"),
	
	PKCS8_WRAPPED("Wrapped PKCS8");
	
	
	private final String display;
	
	private PrivateKeyType(String display)
	{
		this.display = display;
	}
	
	@Override 
	public String toString()
	{
		return display;
	}
	
    public static List<String> getPrivKeyTypeList() 
    {
        List<String> result = new ArrayList<String>();

        for (PrivateKeyType type : PrivateKeyType.values()) 
        {
            result.add(type.toString());
        }

        return result;
    }
    
    public static PrivateKeyType fromString(String str)
    {
        for (PrivateKeyType type : PrivateKeyType.values()) 
            if (str.equals(type.toString()))
            	return type;
        
        return null;
    }
    
}
