package org.nhindirect.dns.util;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

public class IPUtils 
{
	public static String[] getDNSLocalIps()
	{
		final List<String> ips = new ArrayList<String>();
		ips.add("127.0.0.1");
		
		try
		{
			final Enumeration<NetworkInterface> netInts = NetworkInterface.getNetworkInterfaces();
			while (netInts.hasMoreElements())
			{
				final NetworkInterface netInt = netInts.nextElement();
				
				final Enumeration<InetAddress> addrs =  netInt.getInetAddresses();
				while (addrs.hasMoreElements())
				{
					final InetAddress addr = addrs.nextElement();
					ips.add(addr.getHostAddress());
				}
			}
		}
		catch (Exception e)
		{
			
		}
			
		return ips.toArray(new String[ips.size()]);
	}
}
