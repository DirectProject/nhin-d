package org.nhindirect.stagent.james.mailet;

import java.io.File;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;

public class JamesLoader 
{
	public static void main(String[] args)
	{
	       try
	        {
	    	   
	    	    String property = System.getProperty("java.ext.dirs");
	    	   
	    	    
	    	    
				File fl = new File("testfile");
				int idx = fl.getAbsolutePath().lastIndexOf("testfile");
				
				String path = fl.getAbsolutePath().substring(0, idx);
				
				String phoenixHome = path + "src/test/resources/james-server/";			

			    ArrayList<URL> classLoaderURLList = new ArrayList<URL>();

				/*	
				File phoenixLibFile = new File(phoenixHome + "lib");
				File[] subFileList = phoenixLibFile.listFiles();
				for (File subFile : subFileList)
				{
					if (subFile.getName().endsWith(".jar"))
						classLoaderURLList.add(subFile.toURL());
				}

				phoenixLibFile = new File(phoenixHome + "tools/lib");
				subFileList = phoenixLibFile.listFiles();
				for (File subFile : subFileList)
				{
					if (subFile.getName().endsWith(".jar"))
						classLoaderURLList.add(subFile.toURL());
				}

				phoenixLibFile = new File(phoenixHome + "bin/lib");
				subFileList = phoenixLibFile.listFiles();
				for (File subFile : subFileList)
				{
					if (subFile.getName().endsWith(".jar"))
						classLoaderURLList.add(subFile.toURL());
				}				
*/				

	            System.setProperty( "phoenix.home", phoenixHome);

				

				
				classLoaderURLList.add(new File(phoenixHome + "bin/phoenix-loader.jar").toURL());
	    	   
				URL classLoaderURLArray[] = classLoaderURLList.toArray(new URL[classLoaderURLList.size()]);
				
	            //System.setProperty( "catalina.home", "C:\\Program Files\\Apache Software Foundation\\Tomcat 5.5");
	            URLClassLoader childClassLoader =
	                new URLClassLoader(classLoaderURLArray, JamesLoader.class.getClassLoader());	            	            	           
	            
	            final Class mainClass = childClassLoader.loadClass( "org.apache.avalon.phoenix.launcher.Main" );
	            final Class[] paramTypes =
	                new Class[]{args.getClass()};
	            final Method method = mainClass.getMethod( "main", paramTypes );
	            Object main_instance = mainClass.newInstance();
	            method.invoke(main_instance, new Object[]{args} );
	        }
	        catch (Exception e)
	        {
	                e.printStackTrace();
	        }
		
	}
	
}
