
package org.nhindirect.config.service.jaxws;

import java.security.cert.X509Certificate;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

@XmlRootElement(name = "getCertificatesForOwnerResponse", namespace = "http://nhind.org/config")
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "getCertificatesForOwnerResponse", namespace = "http://nhind.org/config")
public class GetCertificatesForOwnerResponse {

    @XmlElement(name = "return", namespace = "")
    private List<X509Certificate> _return;

    /**
     * 
     * @return
     *     returns List<X509Certificate>
     */
    public List<X509Certificate> getReturn() {
        return this._return;
    }

    /**
     * 
     * @param _return
     *     the value for the _return property
     */
    public void setReturn(List<X509Certificate> _return) {
        this._return = _return;
    }

}
