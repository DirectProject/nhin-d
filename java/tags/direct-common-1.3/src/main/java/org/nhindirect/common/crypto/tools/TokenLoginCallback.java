package org.nhindirect.common.crypto.tools;

import java.io.IOException;

import javax.security.auth.callback.Callback;
import javax.security.auth.callback.CallbackHandler;
import javax.security.auth.callback.PasswordCallback;
import javax.security.auth.callback.UnsupportedCallbackException;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;

///CLOVER:OFF
public class TokenLoginCallback implements CallbackHandler
{	
	public Object waitObject = new Object();
	
	public boolean loginSuccessful = false;
	
	public TokenLoginCallback()
	{
	}

	@Override
	public synchronized void handle(Callback[] callbacks) throws IOException, UnsupportedCallbackException 
	{
		for (Callback callback : callbacks)
		{
			if (callback instanceof PasswordCallback)
			{
				final JPanel panel = new JPanel();
				final JLabel label = new JLabel("Enter hardware token password:");
				final JPasswordField pass = new JPasswordField(20);
				panel.add(label);
				panel.add(pass);
				final String[] options = new String[]{"OK", "Cancel"};
				int option = JOptionPane.showOptionDialog(null, panel, "Token ",
				                         JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE,
				                         null, options, options[0]);
				
				if(option == JOptionPane.OK_OPTION) // pressing OK button
				{
				    ((PasswordCallback)callback).setPassword(pass.getPassword());
				}				
			}
		}
		
		this.notifyAll();
	}
}
///CLOVER:ON