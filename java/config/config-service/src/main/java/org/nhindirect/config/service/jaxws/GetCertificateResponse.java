
package org.nhindirect.config.service.jaxws;

import java.security.cert.X509Certificate;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

@XmlRootElement(name = "getCertificateResponse", namespace = "http://nhind.org/config")
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "getCertificateResponse", namespace = "http://nhind.org/config")
public class GetCertificateResponse {

    @XmlElement(name = "return", namespace = "")
    private X509Certificate _return;

    /**
     * 
     * @return
     *     returns X509Certificate
     */
    public X509Certificate getReturn() {
        return this._return;
    }

    /**
     * 
     * @param _return
     *     the value for the _return property
     */
    public void setReturn(X509Certificate _return) {
        this._return = _return;
    }

}
