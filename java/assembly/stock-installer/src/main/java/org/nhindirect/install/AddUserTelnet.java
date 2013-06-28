package org.nhindirect.install;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Arrays;

public class AddUserTelnet 
{
	public static void main(String[] args)
	{
		final String user = args[0];
		final String password = args[1];
		
		 try
		 {
		    Socket sock = new Socket("127.0.0.1", 4555);

		    BufferedReader br = new BufferedReader(new InputStreamReader(sock.getInputStream()));
		    PrintWriter pw = new PrintWriter(sock.getOutputStream());

		    read(br);

		    pw.println("root");
		    pw.flush();
		    read(br);

		    pw.println("root");
		    pw.flush();
		    read(br);
		    
		    pw.println("adduser " + user + " " + password);
		    pw.flush();
		    read(br);
		    
		    pw.close();
		    br.close();
		    sock.close();
		  }
		  catch(Exception e)
		  {
			  throw new RuntimeException(e);
		  }
	}
	
	protected static void read(BufferedReader br) throws Exception
	{
	    char[] ca = new char[1024];
	    int rc = br.read(ca);
	    String s = new String(ca).trim();

	    Arrays.fill(ca, (char)0);

	    System.out.println("AddUser Response " + rc + ":" + s);
	}

}
