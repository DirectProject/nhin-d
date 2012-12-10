package org.nhindirect.config.ui.form;

import java.util.List;

public class SettingsForm {
	private long id;
	private String key;
	private String value;
	private List<String> remove;
	public void setKey(String key) {
		this.key = key;
	}
	public String getKey() {
		return key;
	}
	public void setValue(String value) {
		this.value = value;
	}
	public String getValue() {
		return value;
	}
	public void setRemove(List<String> remove) {
		this.remove = remove;
	}
	public List<String> getRemove() {
		return remove;
	}
	public void setId(long id) {
		this.id = id;
	}
	public long getId() {
		return id;
	}
}
