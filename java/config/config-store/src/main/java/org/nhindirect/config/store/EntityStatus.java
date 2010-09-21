package org.nhindirect.config.store;

import java.util.ArrayList;
import java.util.List;

public enum EntityStatus {
	NEW, 
	ENABLED, 
	DISABLED ;
	
	public static List<String> getEntityStatusList() {
		ArrayList<String> result = new ArrayList<String>();
		EntityStatus[] stati = EntityStatus.values();
		for (int i = 0; i < stati.length; i++) {
			result.add(stati[i].toString());
		}
		return result;
	}
}
