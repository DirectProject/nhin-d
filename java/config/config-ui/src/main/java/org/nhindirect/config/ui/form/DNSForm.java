package org.nhindirect.config.ui.form;

import java.util.List;

public class DNSForm {
	private String postmasterEmail;
	private long id;
	private List<String> remove;
	private String type;
	private String name;
	private String dest;
	private String ttl;
	
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

	public void setType(String type) {
		this.type = type;
	}

	public String getType() {
		return type;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}

	public void setDest(String dest) {
		this.dest = dest;
	}

	public String getDest() {
		return dest;
	}

	public void setTtl(String ttl) {
		this.ttl = ttl;
	}

	public String getTtl() {
		return ttl;
	}

}