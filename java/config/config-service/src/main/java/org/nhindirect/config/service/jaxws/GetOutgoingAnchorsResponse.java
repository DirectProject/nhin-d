
package org.nhindirect.config.service.jaxws;

import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import org.nhindirect.config.store.Anchor;

@XmlRootElement(name = "getOutgoingAnchorsResponse", namespace = "http://nhind.org/config")
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "getOutgoingAnchorsResponse", namespace = "http://nhind.org/config")
public class GetOutgoingAnchorsResponse {

    @XmlElement(name = "return", namespace = "")
    private List<Anchor> _return;

    /**
     * 
     * @return
     *     returns List<Anchor>
     */
    public List<Anchor> getReturn() {
        return this._return;
    }

    /**
     * 
     * @param _return
     *     the value for the _return property
     */
    public void setReturn(List<Anchor> _return) {
        this._return = _return;
    }

}
