
package org.nhindirect.config.service.jaxws;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import org.nhindirect.config.store.TrustBundle;

@XmlRootElement(name = "getTrustBundleByNameResponse", namespace = "http://nhind.org/config")
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "getTrustBundleByNameResponse", namespace = "http://nhind.org/config")
public class GetTrustBundleByNameResponse {

    @XmlElement(name = "return", namespace = "")
    private TrustBundle _return;

    /**
     * 
     * @return
     *     returns TrustBundle
     */
    public TrustBundle getReturn() {
        return this._return;
    }

    /**
     * 
     * @param _return
     *     the value for the _return property
     */
    public void setReturn(TrustBundle _return) {
        this._return = _return;
    }

}
