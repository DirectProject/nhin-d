
package org.nhindirect.config.service.jaxws;

import java.util.Collection;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import org.nhindirect.config.store.Certificate;

@XmlRootElement(name = "addCertificates", namespace = "http://nhind.org/config")
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "addCertificates", namespace = "http://nhind.org/config")
public class AddCertificates {

    @XmlElement(name = "certs", namespace = "")
    private Collection<Certificate> certs;

    /**
     * 
     * @return
     *     returns Collection<Certificate>
     */
    public Collection<Certificate> getCerts() {
        return this.certs;
    }

    /**
     * 
     * @param certs
     *     the value for the certs property
     */
    public void setCerts(Collection<Certificate> certs) {
        this.certs = certs;
    }

}
