
package org.nhindirect.config.service.jaxws;

import java.util.Collection;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import org.nhindirect.config.store.TrustBundle;

@XmlRootElement(name = "getTrustBundlesResponse", namespace = "http://nhind.org/config")
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "getTrustBundlesResponse", namespace = "http://nhind.org/config")
public class GetTrustBundlesResponse {

    @XmlElement(name = "return", namespace = "")
    private Collection<TrustBundle> _return;

    /**
     * 
     * @return
     *     returns Collection<TrustBundle>
     */
    public Collection<TrustBundle> getReturn() {
        return this._return;
    }

    /**
     * 
     * @param _return
     *     the value for the _return property
     */
    public void setReturn(Collection<TrustBundle> _return) {
        this._return = _return;
    }

}
