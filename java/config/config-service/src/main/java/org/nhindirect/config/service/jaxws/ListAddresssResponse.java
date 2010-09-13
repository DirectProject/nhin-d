
package org.nhindirect.config.service.jaxws;

import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import org.nhindirect.config.store.Address;

@XmlRootElement(name = "ListAddresssResponse", namespace = "http://nhind.org/config")
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ListAddresssResponse", namespace = "http://nhind.org/config")
public class ListAddresssResponse {

    @XmlElement(name = "return", namespace = "")
    private List<Address> _return;

    /**
     * 
     * @return
     *     returns List<Address>
     */
    public List<Address> getReturn() {
        return this._return;
    }

    /**
     * 
     * @param _return
     *     the value for the _return property
     */
    public void setReturn(List<Address> _return) {
        this._return = _return;
    }

}
