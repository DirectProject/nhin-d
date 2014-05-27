package org.nhindirect.common.crypto.impl;

import java.io.IOException;

import javax.security.auth.callback.Callback;
import javax.security.auth.callback.CallbackHandler;
import javax.security.auth.callback.PasswordCallback;
import javax.security.auth.callback.UnsupportedCallbackException;

public class BootstrapTestCallbackHandler implements CallbackHandler
{
	protected final String pin;
	
	public BootstrapTestCallbackHandler(String pin)
	{
		this.pin = pin;
	}

	@Override
	public void handle(Callback[] callbacks) throws IOException, UnsupportedCallbackException 
	{
		for (Callback callback : callbacks)
		{
			if (callback instanceof PasswordCallback)
			{
				((PasswordCallback)callback).setPassword(pin.toCharArray());
			}
		}
	}
}
