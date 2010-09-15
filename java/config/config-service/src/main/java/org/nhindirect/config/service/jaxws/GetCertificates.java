
package org.nhindirect.config.service.jaxws;

import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import org.nhindirect.config.service.ws.CertificateGetOptions;

@XmlRootElement(name = "getCertificates", namespace = "http://nhind.org/config")
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "getCertificates", namespace = "http://nhind.org/config", propOrder = {
    "arg0",
    "arg1"
})
public class GetCertificates {

    @XmlElement(name = "arg0", namespace = "")
    private List<Long> arg0;
    @XmlElement(name = "arg1", namespace = "")
    private CertificateGetOptions arg1;

    /**
     * 
     * @return
     *     returns List<Long>
     */
    public List<Long> getArg0() {
        return this.arg0;
    }

    /**
     * 
     * @param arg0
     *     the value for the arg0 property
     */
    public void setArg0(List<Long> arg0) {
        this.arg0 = arg0;
    }

    /**
     * 
     * @return
     *     returns CertificateGetOptions
     */
    public CertificateGetOptions getArg1() {
        return this.arg1;
    }

    /**
     * 
     * @param arg1
     *     the value for the arg1 property
     */
    public void setArg1(CertificateGetOptions arg1) {
        this.arg1 = arg1;
    }

}
