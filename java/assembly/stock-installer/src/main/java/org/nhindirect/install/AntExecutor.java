package org.nhindirect.install;

import java.io.File;

import org.apache.tools.ant.Project;
import org.apache.tools.ant.ProjectHelper;

public class AntExecutor 
{
	public static void main(String[] args)
	{
		try
		{
			File buildFile = new File(args[0]);
	
			Project project = new Project();
			project.setUserProperty("ant.file", buildFile.getAbsolutePath());
			project.init();
			
			ProjectHelper helper = ProjectHelper.getProjectHelper();
			project.addReference("ant.projectHelper", helper);
			helper.parse(project, buildFile);
			project.executeTarget(project.getDefaultTarget());
		}
		catch (Exception e)
		{
			e.printStackTrace();
			throw new RuntimeException(e);
		}
	}
	
}
