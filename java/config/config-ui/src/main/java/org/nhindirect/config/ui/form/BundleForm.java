package org.nhindirect.config.ui.form;

import java.util.List;
import org.springframework.web.multipart.commons.CommonsMultipartFile;

public class BundleForm {

	private long id;
	private String bundleName;
	private String trustURL;
	private String signingCertificate;
	private int refreshInterval; // In seconds
	private String thumbprint;
	private byte[] data;
	private List<String> bundlesSelected;
        private String allSelected;
        
	private CommonsMultipartFile fileData;	// Signing Certificate Data
	
        public BundleForm() {
            
            
            
        }
	
	public void setId(long id) {
		this.id = id;
	}
	public long getId() {
		return id;
	}
	
	public void setBundleName(String bundleName) {
		this.bundleName = bundleName;
	}
	
	public String getBundleName() {
		return bundleName;
	}
	
	public void setTrustURL(String trustURL) {
            this.trustURL = trustURL;
	}
	public String getTrustURL() {
            return trustURL;
	}
	
	public void setSigningCertificate(String signingCertificate) {
		this.signingCertificate = signingCertificate;
	}
	public String getSigningCertificate() {
		return signingCertificate;
	}
	
	public void setRefreshInterval(int refreshInterval) {
		this.refreshInterval = refreshInterval;
	}
	public int getRefreshInterval() {
		return refreshInterval;
	}
	
	public void setThumbprint(String thumbprint) {
		this.thumbprint = thumbprint;
	}
	public String getThumbprint() {
		return thumbprint;
	}
	
	public void setFileData(CommonsMultipartFile fileData) {
		this.fileData = fileData;
	}
	public CommonsMultipartFile getFileData() {
		return fileData;
	}
        
        public void setBundlesSelected(List<String> bundlesSelected) {
		this.bundlesSelected = bundlesSelected;
	}
	public List<String> getBundlesSelected() {
		return bundlesSelected;
	}
        
        public void setAllSelected(String allSelected) {
            this.allSelected = allSelected;
        }
        public String getAllSelected() {
            return allSelected;
        }
        
	

}