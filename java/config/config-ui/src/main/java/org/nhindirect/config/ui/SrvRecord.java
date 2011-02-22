package org.nhindirect.config.ui;

import org.nhindirect.config.store.DNSRecord;

public class SrvRecord extends DNSRecord{

	private int weight = 0;
	private int port = 0;
	private String service = "";
	private String protocol = "";
	private String thumb = "";
	private String priority = "";
	private String target = "";
	
	public void setWeight(int weight) {
		this.weight = weight;
	}
	public int getWeight() {
		return weight;
	}
	public void setPort(int port) {
		this.port = port;
	}
	public int getPort() {
		return port;
	}
	public void setService(String service) {
		this.service = service;
	}
	public String getService() {
		return service;
	}
	public void setProtocol(String protocol) {
		this.protocol = protocol;
	}
	public String getProtocol() {
		return protocol;
	}
	public void setThumb(String thumb) {
		this.thumb = thumb;
	}
	public String getThumb() {
		return thumb;
	}
	public void setPriority(String priority) {
		this.priority = priority;
	}
	public String getPriority() {
		return priority;
	}
	public void setTarget(String target) {
		this.target = target;
	}
	public String getTarget() {
		return target;
	}
	
}
