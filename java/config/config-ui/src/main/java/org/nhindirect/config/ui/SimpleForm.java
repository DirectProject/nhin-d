package org.nhindirect.config.ui;

import java.util.List;

public class SimpleForm {
	private long id;
	private List<String> remove;

	public List<String> getRemove() {
		return remove;
	}

	public void setRemove(List<String> value) {
		this.remove = value;
	}

	public void setId(long id) {
		this.id = id;
	}

	public long getId() {
		return id;
	}
}