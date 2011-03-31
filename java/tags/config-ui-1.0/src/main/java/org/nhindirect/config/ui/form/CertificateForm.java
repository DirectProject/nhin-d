package org.nhindirect.config.ui.form;

import java.util.Calendar;
import java.util.List;

import org.nhindirect.config.store.EntityStatus;
import org.springframework.web.multipart.commons.CommonsMultipartFile;

public class CertificateForm {
    private String owner;
    private String thumbprint;
    private long id = 0L;
    private byte[] data;
    private Calendar createTime;
    private Calendar validStartDate;
    private Calendar validEndDate;
    private EntityStatus status;
    private List<String> remove;
	
	private CommonsMultipartFile fileData;
    
	public void setOwner(String owner) {
		this.owner = owner;
	}
	public String getOwner() {
		return owner;
	}
	public void setThumbprint(String thumbprint) {
		this.thumbprint = thumbprint;
	}
	public String getThumbprint() {
		return thumbprint;
	}
	public void setId(long id) {
		this.id = id;
	}
	public long getId() {
		return id;
	}
	public void setData(byte[] data) {
		this.data = data;
	}
	public byte[] getData() {
		return data;
	}
	public void setCreateTime(Calendar createTime) {
		this.createTime = createTime;
	}
	public Calendar getCreateTime() {
		return createTime;
	}
	public void setValidStartDate(Calendar validStartDate) {
		this.validStartDate = validStartDate;
	}
	public Calendar getValidStartDate() {
		return validStartDate;
	}
	public void setValidEndDate(Calendar validEndDate) {
		this.validEndDate = validEndDate;
	}
	public Calendar getValidEndDate() {
		return validEndDate;
	}
	public void setStatus(EntityStatus status) {
		this.status = status;
	}
	public EntityStatus getStatus() {
		return status;
	}
	public void setRemove(List<String> remove) {
		this.remove = remove;
	}
	public List<String> getRemove() {
		return remove;
	}
	public void setFileData(CommonsMultipartFile fileData) {
		this.fileData = fileData;
	}
	public CommonsMultipartFile getFileData() {
		return fileData;
	}
}
