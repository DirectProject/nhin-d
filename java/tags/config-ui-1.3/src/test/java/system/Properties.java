package system;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.util.ArrayList;
import java.util.List;


import org.apache.commons.io.FileUtils;
import org.junit.Test;

public class Properties {

	@Test
	public void testProp(){
		System.out.println(System.getProperty("java.endorsed.dirs"));		
	}
}
