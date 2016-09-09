package org.nhindirect.config.ui.form;

import java.util.List;

public class SimpleForm {
	private String postmasterEmail;
	private long id;
	private List<String> remove;
    private String domainName;
	
    public void setDomainName(String domainName)
	{
		this.domainName = domainName;
	}
	
	public String getDomainName()
	{
		return this.domainName;
	}
	
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

	public void setPostmasterEmail(String postmasterEmail) {
		this.postmasterEmail = postmasterEmail;
	}

	public String getPostmasterEmail() {
		return postmasterEmail;
	}
}