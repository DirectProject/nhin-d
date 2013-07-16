package org.nhindirect.config.ui.form;

import java.util.List;
import java.util.Map;
import java.util.LinkedHashMap;
import org.nhindirect.policy.PolicyLexicon;
import org.springframework.web.multipart.commons.CommonsMultipartFile;

public class PolicyForm {

	private long id;
	private String policyName;
	private PolicyLexicon policyLexicon;
	private String signingCertificate;
	private int refreshInterval; // In seconds
	private String thumbprint;
	private byte[] data;
	private List<String> policiesSelected;
        private String allSelected;
        
        private Map<String,String> lexiconNames;
        
	private CommonsMultipartFile fileData;	// Lexicon file content
	
        public PolicyForm() {
            
            
            
        }
	
	public void setId(long id) {
		this.id = id;
	}
	public long getId() {
		return id;
	}
	
	public void setPolicyName(String policyName) {
		this.policyName = policyName;
	}
	
	public String getPolicyName() {
		return policyName;
	}
	
	public void setPolicyLexicon(PolicyLexicon policyLexicon) {
            this.policyLexicon = policyLexicon;
	}
	public PolicyLexicon getPolicyLexicon() {
            return policyLexicon;
	}
	
        public Map<String,String> getLexiconNames() {
            
            this.lexiconNames = new LinkedHashMap<String,String>();
            this.lexiconNames.put("XML", "XML");
            this.lexiconNames.put("JAVA_SER", "JAVA_SER");
            this.lexiconNames.put("SIMPLE_TEXT_V1", "SIMPLE_TEXT_V1");            
            
            return this.lexiconNames;
            
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
        
        public String getFileDataAsString() {
            return fileData.toString();
        }
        
        public void setPoliciesSelected(List<String> policiesSelected) {
		this.policiesSelected = policiesSelected;
	}
	public List<String> getPoliciesSelected() {
		return policiesSelected;
	}
        
        public void setAllSelected(String allSelected) {
            this.allSelected = allSelected;
        }
        public String getAllSelected() {
            return allSelected;
        }
        
	

}