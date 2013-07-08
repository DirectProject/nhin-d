
package org.nhindirect.config.service.jaxws;

import java.util.Collection;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import org.nhindirect.config.store.CertPolicyGroupDomainReltn;

@XmlRootElement(name = "getPolicyGroupsByDomainResponse", namespace = "http://nhind.org/config")
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "getPolicyGroupsByDomainResponse", namespace = "http://nhind.org/config")
public class GetPolicyGroupsByDomainResponse {

    @XmlElement(name = "return", namespace = "")
    private Collection<CertPolicyGroupDomainReltn> _return;

    /**
     * 
     * @return
     *     returns Collection<CertPolicyGroupDomainReltn>
     */
    public Collection<CertPolicyGroupDomainReltn> getReturn() {
        return this._return;
    }

    /**
     * 
     * @param _return
     *     the value for the _return property
     */
    public void setReturn(Collection<CertPolicyGroupDomainReltn> _return) {
        this._return = _return;
    }

}
