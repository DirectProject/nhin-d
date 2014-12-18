package org.nhindirect.common.crypto.tools;

import java.io.BufferedReader;
import java.io.Console;
import java.io.IOException;
import java.io.InputStreamReader;

import javax.security.auth.callback.Callback;
import javax.security.auth.callback.CallbackHandler;
import javax.security.auth.callback.PasswordCallback;
import javax.security.auth.callback.UnsupportedCallbackException;

public class CommandLineTokenLoginCallback implements CallbackHandler
{
	public Object waitObject = new Object();
	
	public boolean loginSuccessful = false;
	
	public CommandLineTokenLoginCallback()
	{
	}
	
	@Override
	public synchronized void handle(Callback[] callbacks) throws IOException, UnsupportedCallbackException 
	{
		for (Callback callback : callbacks)
		{
			if (callback instanceof PasswordCallback)
			{		
				
				 final Console cons = System.console();
				 char[] passwd = null;
				 if (cons != null) 
				 {
					 passwd = cons.readPassword("[%s]", "Enter hardware token password: ");
				     java.util.Arrays.fill(passwd, ' ');
				 }
				 else
				 {
					 System.out.print("Enter hardware token password: ");
					  final BufferedReader reader = new BufferedReader(new InputStreamReader(
					            System.in));
					  passwd = reader.readLine().toCharArray();
				 }
				

				 ((PasswordCallback)callback).setPassword(passwd);
			
			}
		}
		
		this.notifyAll();
	}
	
}
