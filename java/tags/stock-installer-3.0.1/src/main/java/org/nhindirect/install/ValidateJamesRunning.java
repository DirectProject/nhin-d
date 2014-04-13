package org.nhindirect.install;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Arrays;

public class ValidateJamesRunning 
{
	public static void main(String[] args)
	{
		
		final int maxTries = 10;
		int attemps = 0;
		boolean connected = false;
		
		while ((attemps < maxTries) && !connected)
		{
			try
			{
			    final Socket sock = new Socket("127.0.0.1", 25);
			    sock.setSoTimeout(5000);
			    
			    final BufferedReader br = new BufferedReader(new InputStreamReader(sock.getInputStream()));
			    final PrintWriter pw = new PrintWriter(sock.getOutputStream());

			    read(br);
			    
			    pw.close();
			    br.close();
			    sock.close();
			    
			    connected = true;
			}
			catch (Exception e)
			{
				try
				{ Thread.sleep(10000);} catch (Exception ex){};
				++attemps;
				
			}
		}
		
		if (!connected)
			throw new RuntimeException("Coulnd not successfully validate the james service is running");
	}
	
	protected static void read(BufferedReader br) throws Exception
	{
	    char[] ca = new char[1024];
	    int rc = br.read(ca);
	    String s = new String(ca).trim();

	    Arrays.fill(ca, (char)0);

	    System.out.println("Connect James Response " + rc + ":" + s);
	}	
}
