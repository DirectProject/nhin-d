package org.nhindirect.gateway.testutils;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

import com.google.inject.Provider;
import com.google.inject.TypeLiteral;

public class TestUtils 
{
	public static String getTestConfigFile(String fileName)
	{
		File fl = new File("dummy");
		int idx = fl.getAbsolutePath().lastIndexOf("dummy");
		
		String path = fl.getAbsolutePath().substring(0, idx);
		
		return path + "src/test/resources/configFiles/" + fileName;	

	}	
	
	 @SuppressWarnings("unchecked") 
	 public static <T> TypeLiteral<Provider<T>> providerOf(final Class<T> parameterType) 
	 { 
	        return (TypeLiteral<Provider<T>>) TypeLiteral.get(new ParameterizedType() 
	        { 
	            public Type[] getActualTypeArguments()
	            {
	            	return new Type[] 
	                {
	            			parameterType
	            	}; 
	            } 
	            public Type getRawType() 
	            { 
	            	return Provider.class; 
	            } 
	            public Type getOwnerType() 
	            { 
	            	return null; 
	            } 
	        }); 
	 }	
	 
	public static String readMessageResource(String _rec) throws Exception
	{
		
		int BUF_SIZE = 2048;		
		int count = 0;
	
		String msgResource = "/messages/" + _rec;
	
		InputStream stream = TestUtils.class.getResourceAsStream(msgResource);;
				
		ByteArrayOutputStream ouStream = new ByteArrayOutputStream();
		if (stream != null) 
		{
			byte buf[] = new byte[BUF_SIZE];
			
			while ((count = stream.read(buf)) > -1)
			{
				ouStream.write(buf, 0, count);
			}
			
			try 
			{
				stream.close();
			} 
			catch (IOException ieo) 
			{
				throw ieo;
			}
			catch (Exception e)
			{
				throw e;
			}					
		} 
		else
			throw new IOException("Failed to open resource " + _rec);

		return new String(ouStream.toByteArray());		
	}
	 
}
