
package org.nhindirect.config.service.jaxws;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import org.nhindirect.config.store.Address;

@XmlRootElement(name = "updateAddress", namespace = "http://nhind.org/config")
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "updateAddress", namespace = "http://nhind.org/config")
public class UpdateAddress {

    @XmlElement(name = "arg0", namespace = "")
    private Address arg0;

    /**
     * 
     * @return
     *     returns Address
     */
    public Address getArg0() {
        return this.arg0;
    }

    /**
     * 
     * @param arg0
     *     the value for the arg0 property
     */
    public void setArg0(Address arg0) {
        this.arg0 = arg0;
    }

}
