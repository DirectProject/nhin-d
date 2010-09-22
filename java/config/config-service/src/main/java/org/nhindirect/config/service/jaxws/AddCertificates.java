
package org.nhindirect.config.service.jaxws;

import java.security.cert.X509Certificate;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

@XmlRootElement(name = "addCertificates", namespace = "http://nhind.org/config")
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "addCertificates", namespace = "http://nhind.org/config")
public class AddCertificates {

    @XmlElement(name = "arg0", namespace = "")
    private List<X509Certificate> arg0;

    /**
     * 
     * @return
     *     returns List<X509Certificate>
     */
    public List<X509Certificate> getArg0() {
        return this.arg0;
    }

    /**
     * 
     * @param arg0
     *     the value for the arg0 property
     */
    public void setArg0(List<X509Certificate> arg0) {
        this.arg0 = arg0;
    }

}
