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
	private List<String> policiesSelected;
        private String allSelected;
        private String policyContent;
        private String updateType; // file or edit
        
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
        
        public String getPolicyContent() {
            return policyContent;            
        }
        
        public void setPolicyContent(String policyContent) {
            this.policyContent = policyContent;
        }
        
        public void setUpdateType(String updateType) {
            this.updateType = updateType;
        }
        
        public String getUpdateType() {
            return updateType;
        }

}