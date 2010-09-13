
package org.nhindirect.config.service.jaxws;

import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import org.nhindirect.config.store.Domain;

@XmlRootElement(name = "getDomainsResponse", namespace = "http://nhind.org/config")
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "getDomainsResponse", namespace = "http://nhind.org/config")
public class GetDomainsResponse {

    @XmlElement(name = "return", namespace = "")
    private List<Domain> _return;

    /**
     * 
     * @return
     *     returns List<Domain>
     */
    public List<Domain> getReturn() {
        return this._return;
    }

    /**
     * 
     * @param _return
     *     the value for the _return property
     */
    public void setReturn(List<Domain> _return) {
        this._return = _return;
    }

}
